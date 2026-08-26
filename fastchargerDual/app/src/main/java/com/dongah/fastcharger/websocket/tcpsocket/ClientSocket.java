package com.dongah.fastcharger.websocket.tcpsocket;

import android.os.Handler;
import android.os.Looper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

public class ClientSocket {

    private static final Logger logger = LoggerFactory.getLogger(ClientSocket.class);

//    private String SERVER_IP = "192.168.39.1"; // TM700/TX700 서버 IP
//    private  int SERVER_PORT = 9999;            // 서버 포트

    private String serverIp;
    private int serverPort;
    private int reconnectDelay = 5000; // 자동 재연결 간격(ms)

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // ioExecutor: connect + read loop
    private final ExecutorService executorService;
    // sendExecutor: 메시지 전송 전용
    private final ExecutorService sendExecutor;
    // scheduler: 타임아웃 처리용
    private final ScheduledExecutorService scheduler;

    private final Handler mainHandler;

    private boolean running = false;


    // callBack interface
    public interface TcpClientListener {
        void onConnected();
        void onDisconnected();
        void onError(Exception e);
        void onMessageReceived(String message);
    }
    private TcpClientListener listener;

    //pending 응답
    private final List<PendingResponse> pending = new CopyOnWriteArrayList<>();
    // 연결 완료 신호를 기다리기 위한 Future
    // 연결이 성사되면 complete(null), 연결 끊기면 새 CompletableFuture로 교체
    private volatile CompletableFuture<Void> connectedFuture = new CompletableFuture<>();

    public ClientSocket(String serverIp, int serverPort, TcpClientListener listener) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.listener = listener;
        this.executorService = Executors.newSingleThreadExecutor();
        this.sendExecutor = Executors.newSingleThreadExecutor();
        // 단일 스레드 스케줄러 (타임아웃 체크용)
        this.scheduler = new ScheduledThreadPoolExecutor(1);
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    //서버 연결 시작
    public void start() {
        if (running) return;
        running = true;
        connectedFuture = new CompletableFuture<>();
        executorService.execute(this::connectLoop);
    }

    //서버 연결 루프 (자동 재연결)
    private void connectLoop() {
        while (running) {
            try {
                socket = new Socket(serverIp, serverPort);
                out =  new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader((socket.getInputStream())));

                // signal connected
                if (!connectedFuture.isDone()) connectedFuture.complete(null);
                postConnected();

                //message receive loop
                String serverMessage;
                while (running && (serverMessage = in.readLine()) != null) {
                    String finalMessage = serverMessage;
                    logger.debug("RECV: {}", finalMessage);
                    // 일반 리스너 콜백
                    mainHandler.post(() -> {
                        if (listener != null) listener.onMessageReceived(finalMessage);
                    });

                    // pending 응답 검사
                    checkPending(finalMessage);
                }

            } catch (Exception e) {
                postError(e);
                logger.error(" connectLoop error : {}", e.getMessage());
            } finally {
                postDisconnected();
                closeSocket();
                running = false;

//                connectedFuture = new CompletableFuture<>();

//                // 자동 재연결 대기
//                try {
//                    Thread.sleep(reconnectDelay);
//                } catch (InterruptedException ignored) { }
            }
        }
    }

    //raw로 보냄(서버가 CR/LF을 요구하면 변경
    public void sendRaw(String message) {
        if (!running) {
            logger.warn("sendRaw called while not running; message ignored");
            return;
        }
        try {
            sendExecutor.execute(() -> {
                try {
                    PrintWriter writer = out;
                    if (writer != null) {
                        // AT 명령은 CR/LF를 요구하는 경우가 많습니다. 필요시 수정하세요.
                        writer.print(message + "\r\n");
                        writer.flush();
                        logger.debug("SENT: {}", message);
                    } else {
                        logger.warn("out is null - error cannot send message");
                    }
                } catch (Exception e) {
                    postError(e);
                }
            });
        }  catch (RejectedExecutionException rex) {
            logger.error("sendExecutor rejected task", rex);
            postError(rex);
        }
    }

    //메세지 전송
    public void sendMessage(String message) {
        if (!running) {
            logger.warn("sendMessage called while not running; message ignored");
            return;
        }
        try {
            sendExecutor.execute(() -> {
                try {
                    PrintWriter writer = out;
                    if (writer != null) {
                        // 잘못된 getBytes() 사용을 제거하고 문자열로 전송
                        writer.println(message); // autoFlush true 이면 println에서 flush 됨
                        writer.flush();
                        logger.debug("Sent: {}", message);
                    } else {
                        logger.warn("out is null - cannot send message");
                    }
                } catch (Exception e) {
                    postError(e);
                }
            });
        } catch (RejectedExecutionException rex) {
            logger.error("sendExecutor rejected task", rex);
            postError(rex);
        }

    }


    // ---------- Pending 응답 시스템 ----------
    private static class PendingResponse {
        final Predicate<String> matcher;
        final CompletableFuture<String> future;
        final ScheduledFuture<?> timeoutTask;
        PendingResponse(Predicate<String> matcher, CompletableFuture<String> future, ScheduledFuture<?> timeoutTask) {
            this.matcher = matcher;
            this.future = future;
            this.timeoutTask = timeoutTask;
        }
    }

    private void checkPending(String msg) {
        for (PendingResponse p : pending) {
            try {
                if (p.matcher.test(msg)) {
                    // 매치되면 timeout 취소 및 complete
                    if (p.timeoutTask != null) p.timeoutTask.cancel(false);
                    if (pending.remove(p)) {
                        p.future.complete(msg);
                        logger.debug("Completed pending for msg : {}", msg);
                    }
                }
            } catch (Exception e) {
                logger.error("Error checking pending matcher", e);
            }
        }
    }

    public CompletableFuture<String> sendCommandExpect(String atCommand, Predicate<String> matcher, long timeoutMs) {
        CompletableFuture<String> future = new CompletableFuture<>();
        long start = System.currentTimeMillis();

        // 1) 연결될 때까지 대기 (전체 timeoutMs 범위 내)
        try {
            logger.debug("Waiting for connection before sending: {} (timeout {}ms)", atCommand, timeoutMs);
            connectedFuture.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // 연결이 안되면 즉시 실패
            future.completeExceptionally(new IllegalStateException("Not connected within timeout before sending: " + atCommand, e));
            return future;
        }
        long elapsed = System.currentTimeMillis() - start;
        long remaining = timeoutMs - elapsed;
        if (remaining <= 0) {
            future.completeExceptionally(new TimeoutException("No time left to wait for response after connection for: " + atCommand));
            return future;
        }


        // 2) 실제 송신 전에 pending 등록 및 타임아웃 예약 (남은 시간 사용)
        ScheduledFuture<?> timeoutFuture = scheduler.schedule(() -> {
            if (pending.removeIf(p -> p.future == future)) {
                future.completeExceptionally(new TimeoutException("Timeout waiting for response to: " + atCommand));
                logger.warn("Timeout waiting for response to: {}", atCommand);
            }
        }, remaining, TimeUnit.MILLISECONDS);

        PendingResponse pr = new PendingResponse(matcher, future, timeoutFuture);
        pending.add(pr);

        // 3) 송신 (CRLF 포함)
        sendRaw(atCommand);
        return future;
    }

    public CompletableFuture<String> sendCommandExpectPrefix(String atCommand, String expectedPrefix, long timeoutMs) {
        Predicate<String> prefixMatcher = s -> {
            if (s == null) return false;
            String t = s.trim();
            return t.startsWith(expectedPrefix) || t.contains(expectedPrefix);
        };
        return sendCommandExpect(atCommand, prefixMatcher, timeoutMs);
    }


    //서버 연결 종료
    public void stop(){
        running = false;
        try { connectedFuture.completeExceptionally(new IllegalStateException("Client stopped")); } catch (Exception ignored) {}
        closeSocket();

        try {
            executorService.shutdownNow();
        } catch (Exception ignored) {}
        try {
            sendExecutor.shutdownNow();
        } catch (Exception ignored) {}
        try {
            scheduler.shutdownNow();
        } catch (Exception ignored) {}

    }

    //call back
    private void postConnected() {
        mainHandler.post(() ->{
            if (listener != null) listener.onConnected();

        });
    }

    public void postDisconnected() {
        mainHandler.post(() -> {
            if (listener != null) listener.onDisconnected();
        });
    }

    private void postError(Exception e) {
        mainHandler.post(() -> {
            if (listener != null) listener.onError(e);
        });
    }

    public void closeSocket() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            logger.error("Error closing socket : {}", e.getMessage());
        } finally {
            out = null;
            in = null;
            socket = null;
        }
    }
}

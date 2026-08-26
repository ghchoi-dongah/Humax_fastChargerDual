package com.dongah.fastcharger.websocket.socket;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.basefunction.ChargerConfiguration;
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.utils.FileManagement;
import com.dongah.fastcharger.websocket.ocpp.utilities.Base64Util;
import com.dongah.fastcharger.websocket.ocpp.utilities.ZonedDateTimeConvert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Handshake;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class Socket extends WebSocketListener {
    private static final Logger logger = LoggerFactory.getLogger(Socket.class);

    private static final String KEYSTORE_PATH = GlobalVariables.getRootPath() + File.separator  + "keystore.bks";
    private static final String KEYSTORE_PASSWORD = "ecospass";
    private static final String TRUSTSTORE_PATH = GlobalVariables.getRootPath() + File.separator  + "truststore.bks";
    private static final String TRUSTSTORE_PASSWORD = "trustpass";
    private static final String OCPP_SERVER_URL = "wss://ocpp-server.example.com:8443/ocpp/";


    private static final int MAX_COLLISION = 2;
    private SocketState state = SocketState.NONE;
    private int reconnectingAttempts;
    private String url;
    private WebSocket webSocket;
    private OkHttpClient client;
    private final Base64Util base64Util = new Base64Util();
    private boolean signedType;

    private static final ZonedDateTimeConvert zonedDateTimeConvert = new ZonedDateTimeConvert();
    private static final FileManagement fileManagement = new FileManagement();
    private static final String FILE_NAME = "securityLog.dongah";
    /**
     * socket interface callback (New Class)
     */
    private static SocketInterface socketInterface = null;

    /**
     * Reconnect handler
     */
    private final Handler reconnectHandler = new Handler(Looper.getMainLooper());

    public SocketState getState() {
        return state;
    }

    public void setState(SocketState state) {
        this.state = state;
    }

    public Socket() {
        super();
    }


    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        try {
            super.onOpen(webSocket, response);
            setState(SocketState.OPEN);
            reconnectingAttempts = 0;
            socketInterface.onOpen(webSocket);
        } catch (Exception e) {
            logger.error("onOpen Error : {}", e.getMessage());
        }
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        try {
            super.onMessage(webSocket, text);
            socketInterface.onGetMessage(webSocket, text);
        } catch (Exception e) {
            logger.error("onMessage Error : {}", e.getMessage());
        }
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
        super.onMessage(webSocket, bytes);
        logger.info("receive byte : {}", bytes.hex());
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        super.onClosing(webSocket, code, reason);
        setState(SocketState.CLOSING);
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        super.onClosed(webSocket, code, reason);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        socketInterface.onGetFailure(webSocket, t);
        setState(SocketState.CONNECT_ERROR);
        this.webSocket = null;
        saveFailureLog(webSocket, t, response);
        scheduleReconnect();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveFailureLog(WebSocket webSocket,
                                Throwable t,
                                Response response) {
        try {
            JSONObject log = new JSONObject();

            log.put("time", zonedDateTimeConvert.doGetUtcDatetimeAsStringSimple());
            log.put("state", state.name());
            log.put("url", url);

            // Exception 정보
            log.put("exception", t.getClass().getSimpleName());
            log.put("message", t.getMessage());

            // HTTP / TLS 정보
            if (response != null) {
                log.put("httpCode", response.code());
                log.put("httpMessage", response.message());

                if (response.handshake() != null) {
                    log.put("tlsVersion", response.handshake().tlsVersion().javaName());
                    log.put("cipherSuite", response.handshake().cipherSuite().javaName());
                }
            }
            // JSON append 저장
            fileManagement.stringToFileSave(
                    GlobalVariables.getRootPath(),
                    FILE_NAME,
                    log.toString(),
                    true
            );
            logger.error("WebSocket Failure logged : {}", log.toString());
        } catch (Exception e) {
            logger.error("saveFailureLog error : {}", e.getMessage());
        }
    }

    private void connect(String url) {
        try {
            Request request;
            ChargerConfiguration chargerConfiguration = ((MainActivity) MainActivity.mContext).getChargerConfiguration();
            signedType = chargerConfiguration.isSigned();
            if (signedType) {
                //Basic <Based64encoded(chargerPointId:AuthorizationKey)>
                String connectionString = GlobalVariables.getHumaxClientId() + ":" + GlobalVariables.getHmConfigPasswd();
                request = new Request.Builder()
                        .url(url)
                        .header("Accept", "application/json")
                        .addHeader("Sec-WebSocket-Protocol", "ocpp1.6")
                        .addHeader("Authorization","Basic " + base64Util.encode(connectionString))
                        .build();
            } else {
                request = new Request.Builder()
                        .url(url)
                        .header("Accept", "application/json")
                        .addHeader("Sec-WebSocket-Protocol", "ocpp1.6")
                        .build();
            }
            webSocket = client.newWebSocket(request, this);
        } catch (Exception e) {
            logger.error("connect fail {}", e.getMessage());
        }
    }

    private static final int MAX_RECONNECT_ATTEMPTS = 500;
    private static final long BASE_RECONNECT_DELAY_MS = 3000;

    private void scheduleReconnect() {
        reconnect();
    }

    private void reconnect() {
        if (reconnectingAttempts >= MAX_RECONNECT_ATTEMPTS) {
            logger.error("Reconnect max attempts reached");
            reconnectingAttempts = 0;
            return;
        }
        setState(SocketState.RECONNECT_ATTEMPT);

        long delay = BASE_RECONNECT_DELAY_MS * (reconnectingAttempts + 1);
        delay = Math.min(5000, 30000); // 최대 30초

        reconnectHandler.removeCallbacks(reconnectRunnable);
        reconnectHandler.postDelayed(reconnectRunnable, delay);
    }

    private final Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            if (state == SocketState.OPEN || state == SocketState.OPENING) return;

            setState(SocketState.RECONNECTING);
            logger.warn("WebSocket reconnect attempt : {}", reconnectingAttempts);

            try {
                reconnectingAttempts++;
                ((MainActivity) MainActivity.mContext).getSocketReceiveMessage().onSocketInitialize();
            } catch (Exception e) {
                scheduleReconnect();
                logger.error("Reconnect error : {}", e.getMessage());
            }
        }
    };

    public synchronized void fullClose() {
        try {
            logger.warn("Socket FULL close");

            // WebSocket 종료
            if (webSocket != null) {
                webSocket.close(1000, "full-close");
                webSocket.cancel();
                webSocket = null;
            }

            // OkHttpClient 완전 폐기
            if (client != null) {
                client.dispatcher().executorService().shutdown();
                client.connectionPool().evictAll();
                client = null;
            }

            // 상태 초기화
            setState(SocketState.NONE);
            ((MainActivity) MainActivity.mContext).getProcessHandler().onHeartBeatStop();
        } catch (Exception e) {
            logger.error("fullClose error", e);
        }
    }

    public void disconnect() {
        try {
            if (webSocket != null) {
                webSocket.close(1000, "disconnect");
                webSocket = null;
            }
            closeClient();
        } catch (Exception e) {
            logger.error("disconnect error {}", e.getMessage());
        }
    }
    /**
     * blue ocpp web socket instance
     *
     * @param url server url (TLS 1.2)
     */
    public Socket(String url) {
        this.url = url;
    }

    public void getInstance(SocketInterface socketInterface) {
        try {
            if (webSocket == null) {
                setState(SocketState.OPENING);
                Socket.socketInterface = socketInterface;
                run(url);
            }

        } catch (Exception e) {
            logger.error("getInstance error : {}", e.getMessage());
        }
    }

    private void run(String url) {
        try {
            if (signedType) {
                // SSL context 설정
                FileInputStream keystoreInputStream = new FileInputStream(KEYSTORE_PATH);
                FileInputStream truststoreInputStream = new FileInputStream(TRUSTSTORE_PATH);
                SSLContext sslContext = createSSLContext(keystoreInputStream, truststoreInputStream);

                truststoreInputStream = new FileInputStream(TRUSTSTORE_PATH);
                X509TrustManager trustManager = getTrustManager(truststoreInputStream);

                //2025.12.09 add
                // Cipher + TLS 설정
                ConnectionSpec tlsSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .cipherSuites(
                                CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256,  // 환경부 SP2 필수 cipher
                                CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384
                        )
                        .build();

                client = new OkHttpClient.Builder()
                        .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                        .hostnameVerifier((hostname, session) -> true)
                        .connectionSpecs(Collections.singletonList(tlsSpec))
                        .protocols(Collections.singletonList(Protocol.HTTP_1_1))                //2025.12.09 add
                        .pingInterval(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor(new SSLHandshakeInterceptor())
                        .addInterceptor(new LoggingInterceptor())
                        .build();
            } else {
                client = new OkHttpClient.Builder()
                        .pingInterval(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .addInterceptor(new SSLHandshakeInterceptor())
                        .addInterceptor(new LoggingInterceptor())
                        .build();
            }
            closeClient();
            connect(url);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void closeClient() {
        try {
            if (webSocket != null) {
                webSocket.close(1000, "reconnect");
                webSocket = null;
            }
        } catch (Exception e) {
            logger.error("closeClient error : {}", e.getMessage());
        }
    }

    public static class SSLHandshakeInterceptor implements Interceptor {

        private static final String TAG = "OkHttp3-SSLHandshake";

        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            final Response response = chain.proceed(chain.request());
            printTlsAndCipherSuiteInfo(response);
            return response;
        }

        private void printTlsAndCipherSuiteInfo(Response response) {
            if (response != null) {
                Handshake handshake = response.handshake();
                if (handshake != null) {
                    final CipherSuite cipherSuite = handshake.cipherSuite();
                    final TlsVersion tlsVersion = handshake.tlsVersion();
                    logger.debug("TLS: {} , CipherSuite: {}", tlsVersion, cipherSuite);
                }
            }
        }
    }

    public String getConfigurationValue(String key) {
        String result = "none";
        try {
            FileManagement fileManagement = new FileManagement();
            String configurationString = fileManagement.getStringFromFile(GlobalVariables.getRootPath() + File.separator + "ConfigurationKey");
            JSONObject jsonObjectData = new JSONObject(configurationString);
            JSONArray jsonArrayContent = jsonObjectData.getJSONArray("values");
            for (int i = 0; i < jsonArrayContent.length(); i++) {
                JSONObject contDetail = jsonArrayContent.getJSONObject(i);
                if (Objects.equals(contDetail.get("key"), key)) {
                    result = contDetail.getString("value");
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("{}", e.getMessage());
        }
        return result;
    }


    static class LoggingInterceptor implements Interceptor {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            Log.d("OkHttp", String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));
            //log save
            doSecurityLogSave(String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));


            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            Log.d("OkHttp", String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            //log save
            doSecurityLogSave(String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));


            return response;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void doSecurityLogSave(String securityLog) {
        try {
            String startTime = zonedDateTimeConvert.doGetUtcDatetimeAsString();
            JSONArray data = insertData(startTime, securityLog);
            JSONObject obj = new JSONObject();
            try {
                obj.put("SecurityLogs", data);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
//            fileManagement.stringToFileSave(GlobalVariables.getRootPath(), FILE_NAME, obj.toString(), true);

        } catch (Exception e) {
            logger.error(" doSecurityLogSave error : {}", e.getMessage());
        }
    }

    public static JSONArray insertData(String startTime, String securityLog) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonObject.put("startTime", startTime);
            jsonObject.put("securityLog", securityLog);
            return jsonArray.put(jsonObject);
        } catch (Exception e) {
            logger.error("insertData() : {}", e.getMessage());
        }
        return null;
    }

    private SSLContext createSSLContext(InputStream keystoreInputStream, InputStream truststoreInputStream) throws Exception {
        // 키스토어 로드
        KeyStore keyStore = KeyStore.getInstance("BKS"); // 안드로이드에서는 BKS 형식 사용
        keyStore.load(keystoreInputStream, KEYSTORE_PASSWORD.toCharArray());

        // 트러스트스토어 로드
        KeyStore trustStore = KeyStore.getInstance("BKS");
        trustStore.load(truststoreInputStream, TRUSTSTORE_PASSWORD.toCharArray());

        // 키 매니저 설정
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD.toCharArray());

        // 트러스트 매니저 설정
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        // SSL 컨텍스트 설정
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        return sslContext;
    }


    private X509TrustManager getTrustManager(InputStream truststoreInputStream) throws Exception {
        // 트러스트스토어에서 X509TrustManager 가져오기
        KeyStore trustStore = KeyStore.getInstance("BKS");
        trustStore.load(truststoreInputStream, TRUSTSTORE_PASSWORD.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        return (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
    }

}

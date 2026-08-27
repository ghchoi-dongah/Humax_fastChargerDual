package com.dongah.fastcharger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dongah.fastcharger.TECH3800.TLS3800;
import com.dongah.fastcharger.basefunction.ChargerConfiguration;
import com.dongah.fastcharger.basefunction.ChargingCurrentData;
import com.dongah.fastcharger.basefunction.ClassUiProcess;
import com.dongah.fastcharger.basefunction.ConfigurationKeyRead;
import com.dongah.fastcharger.basefunction.FocusChangeEnabled;
import com.dongah.fastcharger.basefunction.FragmentChange;
import com.dongah.fastcharger.basefunction.FragmentCurrent;
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.basefunction.UiSeq;
import com.dongah.fastcharger.controlboard.ControlBoard;
import com.dongah.fastcharger.handler.ProcessHandler;
import com.dongah.fastcharger.utils.SftpRxJava;
import com.dongah.fastcharger.utils.ToastPositionMake;
import com.dongah.fastcharger.websocket.ocpp.core.Reason;
import com.dongah.fastcharger.websocket.socket.ConnectionListJsonParse;
import com.dongah.fastcharger.websocket.socket.Connector;
import com.dongah.fastcharger.websocket.socket.HttpClientHelper;
import com.dongah.fastcharger.websocket.socket.SocketReceiveMessage;
import com.dongah.fastcharger.websocket.socket.SocketState;
import com.dongah.fastcharger.websocket.socket.TripleDES;
import com.dongah.fastcharger.websocket.tcpsocket.ClientSocket;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    public static final Logger logger = LoggerFactory.getLogger(MainActivity.class);

    private static final String PACKAGE_NAME = "com.dongah.autoupdatesr";

    @SuppressLint("StaticFieldLeak")
    public static Context mContext;


    TextView textViewVersionValue, textViewTime;
    ImageView imgNetwork;
    Runnable runnable;
    Handler handler = new Handler();

    ChargerConfiguration chargerConfiguration;
    ClassUiProcess[] classUiProcess;
    ChargingCurrentData[] chargingCurrentData;
    UiSeq[] fragmentSeq;
    ToastPositionMake toastPositionMake;
    SocketReceiveMessage socketReceiveMessage;
    FragmentChange fragmentChange;
    ProcessHandler processHandler;

    ControlBoard controlBoard;
    TLS3800 tls3800;
    SftpRxJava sftpRxJava;
    ConfigurationKeyRead configurationKeyRead;
    /**
     * current fragment Exception check
     */
    FragmentCurrent fragmentCurrent;
    List<Connector> connectorList = new ArrayList<>();

    ClientSocket clientSocket;




    public ToastPositionMake getToastPositionMake() {
        return toastPositionMake;
    }

    public UiSeq getFragmentSeq(int ch) {
        return fragmentSeq[ch];
    }

    public void setFragmentSeq(int ch, UiSeq fragmentSeq) {
        this.fragmentSeq[ch] = fragmentSeq;
    }

    public SocketReceiveMessage getSocketReceiveMessage() {
        return socketReceiveMessage;
    }

    public FragmentChange getFragmentChange() {
        return fragmentChange;
    }

    public ProcessHandler getProcessHandler() {
        return processHandler;
    }

    public ControlBoard getControlBoard() {
        return controlBoard;
    }

    public TLS3800 getTls3800() {
        return tls3800;
    }

    public ChargerConfiguration getChargerConfiguration() {
        return chargerConfiguration;
    }

    public ClassUiProcess[] getClassUiProcess() {
        return classUiProcess;
    }

    public ClassUiProcess getClassUiProcess(int ch) {
        return classUiProcess[ch];
    }

    public ChargingCurrentData getChargingCurrentData(int ch) {
        return chargingCurrentData[ch];
    }

    public ConfigurationKeyRead getConfigurationKeyRead() {
        return configurationKeyRead;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        hideNavigationBar();
        mContext = this;

        imgNetwork = findViewById(R.id.imgNetwork);
        textViewTime = findViewById(R.id.textViewTime);
        textViewVersionValue = findViewById(R.id.textViewVersionValue);

        fragmentCurrent = new FragmentCurrent();

        //   ConfigurationKey read */
        configurationKeyRead = new ConfigurationKeyRead();
        configurationKeyRead.onRead();

        toastPositionMake = new ToastPositionMake(this);

        // 1. charger configuration, ConfigurationKey read */
        chargerConfiguration = new ChargerConfiguration();
        chargerConfiguration.onLoadConfiguration();
        textViewVersionValue.setText("VER-" + GlobalVariables.VERSION + " | ");

        // 2. fragment change management */
        fragmentChange = new FragmentChange();
        fragmentSeq = new UiSeq[GlobalVariables.maxChannel];
        for (int i = 0; i < GlobalVariables.maxChannel; i++) {
            fragmentChange.onFragmentChange(i, UiSeq.INIT, "INIT", "");
            fragmentChange.onFragmentHeaderChange(i, "Header");
        }

        // 3. Control board
        controlBoard = new ControlBoard(GlobalVariables.maxChannel, chargerConfiguration.getControlCom());
        // 4. rf card reade : MID = terminal ID */
        tls3800 = new TLS3800(chargerConfiguration.getCreditCom());

        // 5. Handler */
        processHandler = new ProcessHandler(chargerConfiguration);

        //Cat Id 설정
        tls3800.onTLS3800Request(0, TLS3800.CMD_TX_TREMCHACK, 0);

        // 6. classUiProcess */
        chargingCurrentData = new ChargingCurrentData[GlobalVariables.maxChannel];
        for (int i = 0; i < GlobalVariables.maxChannel; i++) {
            chargingCurrentData[i] = new ChargingCurrentData();
            chargingCurrentData[i].onCurrentDataClear();
            chargingCurrentData[i].setConnectorId(i+1);
        }

        classUiProcess = new ClassUiProcess[GlobalVariables.maxChannel];
        for (int i = 0; i < GlobalVariables.maxChannel; i++) {
            classUiProcess[i] = new ClassUiProcess(i);
            classUiProcess[i].setUiSeq(UiSeq.INIT);
        }

        //모뎀 정보 갖고 오기
        clientSocket = new ClientSocket("192.168.39.1", 9999, new ClientSocket.TcpClientListener() {
            @Override
            public void onConnected() {
                logger.debug("connected");
//                clientSocket.sendMessage("AT+CNUM");
            }

            @Override
            public void onDisconnected() {

            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onMessageReceived(String message) {
                // 모든 수신 메시지(일반)는 여기로 들어옵니다.
                Log.d("TCP","General recv: " + message);
            }
        });
        clientSocket.start();

        // 예: AT+CNUM 전송 후 +CNUM: 응답을 기다리고, 성공하면 AT$$DSCREEN? 전송
        clientSocket.sendCommandExpectPrefix("AT+CNUM", "+CNUM:", 10000)
                .thenApply(line -> {
                    // line 예: +CNUM: "LGU","+821222492396",145
                    String[] parts = line.split(",");
                    String raw = parts.length >= 2 ? parts[1].replace("\"","") : null;
                    GlobalVariables.setIMSI(raw == null ? "" : parseToLocal(raw));
                    return parseToLocal(raw); // 01222492396
                })
                .thenCompose(localNumber -> {
                    Log.d("TCP","Parsed local number: " + localNumber);
                    // 이어서 DSCREEN 명령
                    return clientSocket.sendCommandExpectPrefix("AT$$DSCREEN?", "DSCREEN:", 5000);
                })
                .thenAccept(dscreenResp -> {
                    GlobalVariables.setRSRP(parseToRSRP(dscreenResp));
                    Log.d("TCP","DSCREEN response: " + dscreenResp);
                    clientSocket.postDisconnected();
                    clientSocket.closeSocket();
                })
                .exceptionally(ex -> {
                    Log.e("TCP","Command chain error", ex);
                    return null;
                });

        if (Objects.equals(chargerConfiguration.getAuthMode(), "0")) sendOcppAuthInfoRequest();


        // 7. version check - FTP 변경 */
//        sftpRxJava = new SftpRxJava();
//        versionHandler = new Handler();
//        versionHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //15분 마다....
//                sftpRxJava.doVersionCheck();
//                versionHandler.postDelayed(this, 60000);
//            }
//        }, 60000);


        // 전류 제한 설정
        for (int i = 0; i < GlobalVariables.maxChannel; i++) {
            ((MainActivity) MainActivity.mContext).getControlBoard().getTxData(i).setOutPowerLimit((short) chargerConfiguration.getDr());
        }

        //8. ChargerOperate read
        File file = new File(GlobalVariables.getRootPath() + File.separator + "ChargerOperate");
        File firmwareFile = new File(GlobalVariables.getRootPath() + File.separator + "FirmwareStatusNotification");
        if (!firmwareFile.exists()) {
            if (file.exists()) {
                FileReader fileReader = null;
                try {
                    fileReader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line;
                    int count = 0;
                    while ((line = bufferedReader.readLine()) != null) {
                        GlobalVariables.ChargerOperation[count] = Objects.equals(line, "true");
                        count++;
                    }
                } catch (IOException e) {
                    logger.error("ChargerOperate read error : {}", e.getMessage());
                }
            } else {
                for (int i = 0; i < GlobalVariables.maxPlugCount; i++) {
                    GlobalVariables.ChargerOperation[i] = true;
                }
            }
        }

        // customer unit price
        processHandler.onCustomUnitPriceStart(3600);

        //Diagnostics thread start
//        processHandler.onDiagnosticsStart(60);

//        socketReceiveMessage.onChargingSchedulePeriodCurrentData(0, 400);
//        boolean aaa = socketReceiveMessage.onSecurityLogFileMake("2024-08-25T20:37:05Z", "2025-08-25T20:37:05Z", "192.168.30.120");
    }


    @Override
    protected void onStart() {
        super.onStart();

        runnable = new Runnable() {
            @Override
            public void run() {
                updateTime();
                // 1초마다 실행
                handler.postDelayed(this, 1000);
                try {
                    if (socketReceiveMessage.getSocket().getState() != null) {
                        imgNetwork.setBackgroundResource(socketReceiveMessage.getSocket().getState() == SocketState.OPEN ?
                                R.drawable.network : R.drawable.nonetwork);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        };
        runnable.run();
    }

    private void updateTime() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String currentTime = sdf.format(new Date());
            textViewTime.setText(currentTime);
        } catch (Exception e){
            logger.error(e.getMessage());
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        try {
            // channel argument check
            for (int i = 0; i < GlobalVariables.maxChannel; i++) {
                if (fragmentCurrent.getCurrentFragment(i) instanceof FocusChangeEnabled) {
                    ((FocusChangeEnabled) fragmentCurrent.getCurrentFragment(i)).onWindowFocusChanged(hasFocus);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void onRebooting(String type) {
        try {
//            ((MainActivity) MainActivity.mContext).getSocketReceiveMessage().getSocket().disconnect();
//            if (Objects.equals(type, "Soft")) {
////                Intent intent = ((MainActivity) MainActivity.mContext).getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
////                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                (MainActivity.mContext).startActivity(intent);
//                ActivityCompat.finishAffinity(((MainActivity) MainActivity.mContext));
//                System.exit(0);
//            } else {
//                PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//                powerManager.reboot("reboot");
//            }

            Intent intent = getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent); // 새 앱 실행
                overridePendingTransition(0, 0);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    ActivityCompat.finishAffinity(MainActivity.this); // 모든 액티비티 종료
                }, 500); // 200ms 딜레이
                exitApp();
            }
        } catch (Exception e) {
            logger.error("onRebooting : {}", e.getMessage());
        }
    }

    private void exitApp() {
        handler.postDelayed(() -> {
            ActivityCompat.finishAffinity(MainActivity.this);
            System.exit(0);
        }, 300); // 안전한 작은 딜레이
    }


    /**
     * ui version update
     */
    public void onRebooting() {
        try {
            boolean result = false;
            ChargingCurrentData chargingCurrentData;
            for (int i = 0; i < GlobalVariables.maxChannel; i++) {
                chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(i);
                result = chargingCurrentData.isReBoot() && (getClassUiProcess(i).getUiSeq() == UiSeq.INIT);
            }

            if (result) {
                for (int i = 0; i < GlobalVariables.maxChannel; i++) {
                    getClassUiProcess(i).setUiSeq(UiSeq.REBOOTING);
                    ((MainActivity) MainActivity.mContext).getChargingCurrentData(i).setStopReason(Reason.Reboot);
                }
            }

        } catch (Exception e) {
            logger.error(" version reboot : {}", e.getMessage());
        }
    }

    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Custom status notification stop
        for (int i = 0; i < GlobalVariables.maxChannel; i++) {
            ((MainActivity) MainActivity.mContext).getClassUiProcess(i).onCustomStatusNotificationStop();
        }
//        versionHandler.removeMessages(0);
//        versionHandler.removeCallbacksAndMessages(null);
//        if (versionHandler != null) versionHandler = null;

//        processHandler.onDiagnosticsStop();
    }


    /**
     * HTTPS 연결이 안되면 다시 접속
     */
    private static final int RETRY_DELAY_MS = 3000;  // 3초
    private static final int MAX_RETRY_COUNT = 5;    // 최대 재시도 횟수
    private int retryCount = 0;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private void sendOcppAuthInfoRequest() {
        HttpClientHelper httpClient = new HttpClientHelper();
        String addUrl = TextUtils.isEmpty(chargerConfiguration.getM2mTel()) ? "SN" : "TN";
        String url = chargerConfiguration.getServerHttpString() + "/getOcppAuthInfo/" + addUrl;

        TripleDES tripleDES = new TripleDES();

        try {
            String encrypted = tripleDES.encrypt(TextUtils.isEmpty(chargerConfiguration.getM2mTel()) ?
                    chargerConfiguration.getChargerPointSerialNumber() : chargerConfiguration.getM2mTel());
            String jsonBody = httpClient.onJsonMake("reqVal", encrypted);

            httpClient.postWithRetry(url, jsonBody, new HttpClientHelper.HttpCallback() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(int statusCode, String response) {
                    try {
                        if (statusCode == 200) {
                            JSONObject jsonObject = new JSONObject(response);
                            String resultCode = jsonObject.optString("resultCode", "");

                            if ("OK".equals(resultCode)) {
                                // WebSocket 연결 설정
                                GlobalVariables.setHumaxClientId(tripleDES.decrypt(jsonObject.getString("clientId")));
                                if (!jsonObject.getString("passwd").isEmpty()) {
                                    GlobalVariables.setHumaxPassWd(tripleDES.decrypt(jsonObject.getString("passwd")));
                                }

                                ConnectionListJsonParse connectionListJsonParse = new ConnectionListJsonParse();
                                connectorList = connectionListJsonParse.parseConnectorList(response);

//                                runOnUiThread(() -> textViewChargerId.setText("ID: " + connectorList.get(0).getSearchKey()));
                                runOnUiThread(() -> chargerConfiguration.setChargerId(String.valueOf(connectorList.get(0).getSearchKey())));

                                String baseUrl = chargerConfiguration.getServerConnectingString() + "/" + GlobalVariables.getHumaxClientId();
                                socketReceiveMessage = new SocketReceiveMessage(baseUrl);

                                retryCount = 0; // 성공 시 재시도 횟수 초기화
                                // 초기 회면
                                for (int i = 0; i < GlobalVariables.maxChannel; i++) {
                                    fragmentChange.onFragmentChange(i, UiSeq.INIT, "INIT", "");
                                    fragmentChange.onFragmentHeaderChange(i, "Header");
                                }

                            } else {
                                Log.w("HTTP", "resultCode != OK → 3초 후 재요청");
                                scheduleRetry();
                            }
                        } else {
                            Log.w("HTTP", "statusCode != 200 → 3초 후 재요청");
                            scheduleRetry();
                        }
                    } catch (Exception e) {
                        Log.e("PARSE_ERROR", "JSON 파싱 오류", e);
                        scheduleRetry();
                    }
                    Log.d("HTTP", "Response: " + response);
                }

                @Override
                public void onFailure(IOException e) {
                    Log.e("HTTP", "Failed to send request", e);
                    scheduleRetry();
                }
            });
        } catch (Exception e) {
            logger.error("REQUEST_ERROR {}", e.getMessage());
            scheduleRetry();
        }
    }


    private void scheduleRetry() {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            Log.w("HTTP", "재시도 " + retryCount + "회 / " + MAX_RETRY_COUNT + "회");
            mainHandler.postDelayed(this::sendOcppAuthInfoRequest, RETRY_DELAY_MS);
        } else {
            Log.e("HTTP", "최대 재시도 횟수 초과 → 요청 중단");
        }
    }

    private String parseToLocal(String number) {
        if (number.startsWith("+82")) {
            return "0" + number.substring(3);
        }
        return number;
    }

    private String parseToRSRP(String resp) {
        Pattern p = Pattern.compile("RSRP:([-]?\\d+)");
        Matcher m = p.matcher(resp);
        if (m.find()) {
            int rsrp = Integer.parseInt(m.group(1));  // -71
            return String.valueOf(rsrp);
        } else {
            System.out.println("RSRP not found");
        }
        return "";
    }

    public List<Connector> getConnectorList() {
        return connectorList;
    }

    // 키보드 내리기
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();

        if (view != null) {
            int[] scrcoords = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];

            if (ev.getAction() == MotionEvent.ACTION_UP &&
                    (x < view.getLeft() || x >= view.getRight() ||
                            y < view.getTop() || y > view.getBottom())) {

                // 키보드 내리기
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                // EditText 포커스 제거
                view.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}
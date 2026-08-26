package com.dongah.fastcharger.websocket.socket;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.TECH3800.TLS3800;
import com.dongah.fastcharger.basefunction.ChargerConfiguration;
import com.dongah.fastcharger.basefunction.ChargingCurrentData;
import com.dongah.fastcharger.basefunction.DumpDataSend;
import com.dongah.fastcharger.basefunction.FileTransType;
import com.dongah.fastcharger.basefunction.FragmentChange;
import com.dongah.fastcharger.basefunction.FtpRxJava;
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.basefunction.HTTPHelper;
import com.dongah.fastcharger.basefunction.PaymentType;
import com.dongah.fastcharger.basefunction.UiSeq;
import com.dongah.fastcharger.controlboard.RxData;
import com.dongah.fastcharger.controlboard.TxData;
import com.dongah.fastcharger.handler.ProcessHandler;
import com.dongah.fastcharger.utils.FileManagement;
import com.dongah.fastcharger.utils.LogDataSave;
import com.dongah.fastcharger.utils.ToastPositionMake;
import com.dongah.fastcharger.websocket.ocpp.common.JSONCommunicator;
import com.dongah.fastcharger.websocket.ocpp.common.OccurenceConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.common.model.Message;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.core.AuthorizationStatus;
import com.dongah.fastcharger.websocket.ocpp.core.AvailabilityType;
import com.dongah.fastcharger.websocket.ocpp.core.BootNotificationRequest;
import com.dongah.fastcharger.websocket.ocpp.core.ChargePointStatus;
import com.dongah.fastcharger.websocket.ocpp.core.ChargingProfileKindType;
import com.dongah.fastcharger.websocket.ocpp.core.ChargingProfilePurposeType;
import com.dongah.fastcharger.websocket.ocpp.core.ChargingSchedule;
import com.dongah.fastcharger.websocket.ocpp.core.ChargingSchedulePeriod;
import com.dongah.fastcharger.websocket.ocpp.core.DataTransferConfirmation;
import com.dongah.fastcharger.websocket.ocpp.core.DataTransferStatus;
import com.dongah.fastcharger.websocket.ocpp.core.HeartbeatRequest;
import com.dongah.fastcharger.websocket.ocpp.core.Reason;
import com.dongah.fastcharger.websocket.ocpp.core.RecurrencyKindType;
import com.dongah.fastcharger.websocket.ocpp.core.RegistrationStatus;
import com.dongah.fastcharger.websocket.ocpp.core.RemoteStartStopStatus;
import com.dongah.fastcharger.websocket.ocpp.core.RemoteStopTransactionConfirmation;
import com.dongah.fastcharger.websocket.ocpp.core.ResetConfirmation;
import com.dongah.fastcharger.websocket.ocpp.core.ResetStatus;
import com.dongah.fastcharger.websocket.ocpp.core.ResetType;
import com.dongah.fastcharger.websocket.ocpp.core.UnlockConnectorConfirmation;
import com.dongah.fastcharger.websocket.ocpp.core.UnlockStatus;
import com.dongah.fastcharger.websocket.ocpp.firmware.DiagnosticsStatus;
import com.dongah.fastcharger.websocket.ocpp.firmware.DiagnosticsStatusNotificationRequest;
import com.dongah.fastcharger.websocket.ocpp.firmware.FirmwareStatus;
import com.dongah.fastcharger.websocket.ocpp.firmware.FirmwareStatusNotificationRequest;
import com.dongah.fastcharger.websocket.ocpp.firmware.GetDiagnosticsConfirmation;
import com.dongah.fastcharger.websocket.ocpp.firmware.UpdateFirmwareConfirmation;
import com.dongah.fastcharger.websocket.ocpp.localauthlist.GetLocalListVersionConfirmation;
import com.dongah.fastcharger.websocket.ocpp.localauthlist.SendLocalListConfirmation;
import com.dongah.fastcharger.websocket.ocpp.localauthlist.UpdateStatus;
import com.dongah.fastcharger.websocket.ocpp.localauthlist.UpdateType;
import com.dongah.fastcharger.websocket.ocpp.remotetrigger.TriggerMessageConfirmation;
import com.dongah.fastcharger.websocket.ocpp.remotetrigger.TriggerMessageRequestType;
import com.dongah.fastcharger.websocket.ocpp.remotetrigger.TriggerMessageStatus;
import com.dongah.fastcharger.websocket.ocpp.reservation.CancelReservationConfirmation;
import com.dongah.fastcharger.websocket.ocpp.reservation.CancelReservationStatus;
import com.dongah.fastcharger.websocket.ocpp.reservation.ReservationStatus;
import com.dongah.fastcharger.websocket.ocpp.reservation.ReserveNowConfirmation;
import com.dongah.fastcharger.websocket.ocpp.security.CertificateHashDataType;
import com.dongah.fastcharger.websocket.ocpp.security.CertificateStatus;
import com.dongah.fastcharger.websocket.ocpp.security.CertificateUse;
import com.dongah.fastcharger.websocket.ocpp.security.DeleteCertificateConfirmation;
import com.dongah.fastcharger.websocket.ocpp.security.DeleteCertificateStatus;
import com.dongah.fastcharger.websocket.ocpp.security.ExtendedTriggerMessageConfirmation;
import com.dongah.fastcharger.websocket.ocpp.security.GetInstalledCertificateIdsConfirmation;
import com.dongah.fastcharger.websocket.ocpp.security.GetInstalledCertificateStatus;
import com.dongah.fastcharger.websocket.ocpp.security.GetLogConfirmation;
import com.dongah.fastcharger.websocket.ocpp.security.HashAlgorithm;
import com.dongah.fastcharger.websocket.ocpp.security.InstallCertificateConfirmation;
import com.dongah.fastcharger.websocket.ocpp.security.LogStatus;
import com.dongah.fastcharger.websocket.ocpp.security.LogType;
import com.dongah.fastcharger.websocket.ocpp.security.SignedFirmwareStatus;
import com.dongah.fastcharger.websocket.ocpp.security.SignedFirmwareStatusNotificationRequest;
import com.dongah.fastcharger.websocket.ocpp.security.SignedUpdateFirmwareConfirmation;
import com.dongah.fastcharger.websocket.ocpp.security.UpdateFirmwareStatus;
import com.dongah.fastcharger.websocket.ocpp.smartcharging.ChargingProfileStatus;
import com.dongah.fastcharger.websocket.ocpp.smartcharging.ChargingRateUnitType;
import com.dongah.fastcharger.websocket.ocpp.smartcharging.ClearChargingProfileConfirmation;
import com.dongah.fastcharger.websocket.ocpp.smartcharging.ClearChargingProfileStatus;
import com.dongah.fastcharger.websocket.ocpp.smartcharging.GetCompositeScheduleConfirmation;
import com.dongah.fastcharger.websocket.ocpp.smartcharging.GetCompositeScheduleStatus;
import com.dongah.fastcharger.websocket.ocpp.smartcharging.SetChargingProfileConfirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.Stopwatch;
import com.dongah.fastcharger.websocket.ocpp.utilities.ZonedDateTimeConvert;
import com.google.gson.Gson;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import okhttp3.WebSocket;

public class SocketReceiveMessage extends JSONCommunicator implements SocketInterface {

    private static final Logger logger = LoggerFactory.getLogger(SocketReceiveMessage.class);


    static {
        // BouncyCastle 프로바이더 추가 (Android에서 RSASSA-PSS 사용 가능하도록)
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Okhttp3 web-socket
     */
    WebSocket webSocket = null;
    /**
     * user define socket blue-networks socket
     */
    Socket socket = null;
    String url;
    String actionName;
    Message message = null;
    /**
     * message send/receive list (UUID,Action)
     */
    HashMap<String, String> hashMapUuid = null;
    HashMap<String, Object> newHashMapUuid = null;
    HashMap<Integer, Integer> getConnectorIdHashMap;
    int channel;
    int connectorId;
    SendHashMapObject sendHashMapObject;
    JSONObject jsonObjectData;
    UiSeq uiSeq;
    /**
     * LogData save class
     */
    LogDataSave logDataSave = new LogDataSave("log");
    /**
     * dump data save actions
     */
    String[] actionNames = {"StopTransaction", "partialCancel", "resultPrice"};
    ArrayList<String> actionList = new ArrayList<>();
    LogDataSave logDataSaveDump = new LogDataSave("dump");
    FragmentChange fragmentChange;
    ChargerConfiguration chargerConfiguration;
    ChargingCurrentData chargingCurrentData;
    ProcessHandler processHandler;

    FileManagement fileManagement;
    ZonedDateTimeConvert zonedDateTimeConvert;
    ToastPositionMake toastPositionMake = new ToastPositionMake(((MainActivity) MainActivity.mContext));
    /**
     * web socket listener register
     */
    SocketMessageListener socketMessageListener;



    public SocketMessageListener getSocketMessageListener() {
        return socketMessageListener;
    }

    public void setSocketMessageListener(SocketMessageListener socketMessageListener) {
        this.socketMessageListener = socketMessageListener;
    }

    public ToastPositionMake getToastPositionMake() {
        return toastPositionMake;
    }

    /**
     * web socket debug listener register
     */
    private SocketMessageListener socketMessageDebugListener;

    public void setSocketMessageDebugListener(SocketMessageListener listener) {
        this.socketMessageDebugListener = listener;
    }

    public void setSocketMessageDebugListenerStop() {
        this.socketMessageDebugListener = null;
    }

    /**
     * socket getter
     */
    public Socket getSocket() {
        return socket;
    }


    /**
     * socket constructor
     */
    public SocketReceiveMessage(String url) {
        this.url = url;
        fragmentChange = ((MainActivity) MainActivity.mContext).getFragmentChange();
        processHandler = ((MainActivity) MainActivity.mContext).getProcessHandler();
        zonedDateTimeConvert = new ZonedDateTimeConvert();
        fileManagement = new FileManagement();
        Collections.addAll(actionList, actionNames);
        onSocketInitialize();
    }

    public void onSocketInitialize() {
        try {
            socket = new Socket(url);
            socket.getInstance(this);
            socket.setState(SocketState.OPENING);
            // request  ==> UUID, ActionName  hashmap 저장
            // response ==> hashmap find uuid 삭제
            // (key:UUID, value:Action) ==> hashMap
            if (hashMapUuid != null) hashMapUuid = null;
            hashMapUuid = new HashMap<String, String>();

            if (newHashMapUuid != null) newHashMapUuid = null;
            newHashMapUuid = new HashMap<String, Object>();

            // connectorId to channel (remoteStart ==> remoteStop)
            if (getConnectorIdHashMap != null) getConnectorIdHashMap = null;
            getConnectorIdHashMap = new HashMap<Integer, Integer>();

        } catch (Exception e) {
            logger.error(" socket receive message  : {}", e.getMessage());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onOpen(WebSocket webSocket) {
        this.webSocket = webSocket;
        socket.setState(SocketState.OPEN);

        //미전송 데이터 확인
        DumpDataSend dumpDataSend = new DumpDataSend();
        dumpDataSend.onDumpSend();


        if (!GlobalVariables.isConnectRetry()) {
            // bootNotification send
            processHandler.sendMessage(onMakeHandlerMessage(
                    GlobalVariables.MESSAGE_HANDLER_BOOT_NOTIFICATION,
                    100,
                    5,
                    null,
                    null,
                    null,
                    false));
        } else {
            // Status Notification
            processHandler.sendMessage(onMakeHandlerMessage(
                    GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION_BOOT,
                    100,
                    5,
                    null,
                    null,
                    null,
                    false));

        }

    }

    @Override
    public void onGetMessage(WebSocket webSocket, String text) throws JSONException {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void run() {
                try {
                    actionName = null;
                    message = parse(text);
                    int resultType = message.getResultType();
                    switch (resultType) {
                        case 2:
                            actionName = message.getAction();
                            break;
                        case 3:
                            //request 에 대한 response
                            sendHashMapObject = (SendHashMapObject) newHashMapUuid.get(message.getId());
                            if (sendHashMapObject != null) {
                                connectorId = sendHashMapObject.getConnectorId();
                                setChannel(connectorId == 1 ? 0 : connectorId == 2 ? 1 : -1);
                                actionName = sendHashMapObject.getActionName();
                            }
                            break;
                    }

                    JSONObject jsonObject = new JSONObject(message.getPayload().toString());
                    logDataSave.makeLogDate(actionName, text);
                    //* debug event listener register */
                    if (socketMessageDebugListener != null)
                        socketMessageDebugListener.onMessageReceiveDebugEvent(1, text, actionName);
                    //* receive message type */
                    switch (resultType) {
                        case 4:
                            //* Central System response --> call error message  */
                            break;
                        case 3:
                            //* Central System response --> success * SEND ACTION 의 UUID find , actionName parser */
                            if (!newHashMapUuid.containsKey(message.getId())) return;
                            //* default info */

                            if (!Objects.equals(100, connectorId)) {
                                chargerConfiguration = ((MainActivity) MainActivity.mContext).getChargerConfiguration();
                                chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(getChannel());
                            }
                            /////////////////////
                            if (Objects.equals("BootNotification", actionName)) {
                                try {
                                    int interval = jsonObject.getInt("interval");
                                    ZonedDateTime currentTime = ZonedDateTime.parse(jsonObject.getString("currentTime"));
                                    GlobalVariables.setConnectRetry(true);
                                    processHandler.onBootNotificationStop();
                                    // bootNotification 응답을 받아서 Heartbeat delayTime 만큼 데이터 전송
                                    RegistrationStatus status = RegistrationStatus.valueOf(jsonObject.getString("status"));
                                    if (Objects.equals(status, RegistrationStatus.Accepted)) {
                                        // Status Notification */
                                        processHandler.sendMessage(onMakeHandlerMessage(
                                                GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION_BOOT,
                                                100,
                                                0,
                                                null,
                                                null,
                                                null,
                                                false));
                                        // heart beat */
                                        processHandler.sendMessage(onMakeHandlerMessage(
                                                GlobalVariables.MESSAGE_HANDLER_HEART_BEAT,
                                                100,
                                                GlobalVariables.getHeartBeatInterval(),
                                                null,
                                                null,
                                                null,
                                                false));
                                        // Custom Unit Price
                                        GlobalVariables.setCustomUnitPriceReq(true);
                                        GlobalVariables.setHumaxUserType("A");
                                        processHandler.sendMessage(onMakeHandlerMessage(
                                                GlobalVariables.MESSAGE_CUSTOM_UNIT_PRICE,
                                                connectorId,
                                                0,
                                                null,
                                                null,
                                                GlobalVariables.getHumaxUserType(),
                                                false));
                                    } else if (Objects.equals(status, RegistrationStatus.Rejected)) {
                                        processHandler.sendMessage(onMakeHandlerMessage(
                                                GlobalVariables.MESSAGE_HANDLER_BOOT_NOTIFICATION,
                                                100,
                                                5,
                                                null,
                                                null,
                                                null,
                                                false));

                                    } else if (Objects.equals(status, RegistrationStatus.Pending)) {
                                        processHandler.onBootNotificationStart(5);
                                        GlobalVariables.setReconnectCheck(false);
                                    }
                                } catch (Exception e) {
                                    logger.error("BootNotification error ; {} ", e.getMessage());
                                }

                            } else if (Objects.equals("Heartbeat", actionName)) {
                                // Heartbeat */
                                try {
                                    ZonedDateTime currentTime = ZonedDateTime.parse(jsonObject.getString("currentTime"));
                                } catch (Exception e) {
                                    logger.error("Heartbeat error : {} ", e.getMessage());
                                }
                            } else if (Objects.equals("Authorize", actionName)) {
                                jsonObjectData = jsonObject.getJSONObject("idTagInfo");
                                AuthorizationStatus status = AuthorizationStatus.valueOf(jsonObjectData.getString("status"));
                                String parentIdTag = jsonObjectData.has("parentIdTag") ? jsonObjectData.getString("parentIdTag") : "";
                                String expiryDate = jsonObjectData.has("expiryDate") ? jsonObjectData.getString("expiryDate") : "";
                                uiSeq = ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).getUiSeq();
                                if (Objects.equals(status, AuthorizationStatus.Accepted)) {
                                    //인증 성공(getPrice 단가 요청)
                                    chargingCurrentData.setAuthorizeResult(true);
                                    if (Objects.equals(uiSeq, UiSeq.CHARGING)) {
                                        chargingCurrentData.setParentIdTagStop(parentIdTag);
                                        if (Objects.equals(chargingCurrentData.getParentIdTag(), chargingCurrentData.getParentIdTagStop())) {
                                            ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(getChannel(), UiSeq.CHARGING_STOP_MESSAGE, "CHARGING_STOP_MESSAGE", null);
                                        } else {
                                            ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).setUiSeq(UiSeq.CHARGING);
                                            ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(getChannel(), UiSeq.CHARGING, "CHARGING", "small");
                                        }
                                    } else {
                                        chargingCurrentData.setParentIdTag(parentIdTag);
                                        boolean ocppMode = ((MainActivity) MainActivity.mContext).getChargerConfiguration().isStopConfirm();
                                        //OCPP 인증이 연부 확인
                                        if (!ocppMode && Objects.equals(chargerConfiguration.getAuthMode(), "0")) {
                                            //custom unit price
                                            GlobalVariables.setCustomUnitPriceReq(false);
                                            GlobalVariables.setHumaxUserType("A");
                                            processHandler.sendMessage(onMakeHandlerMessage(
                                                    GlobalVariables.MESSAGE_CUSTOM_UNIT_PRICE,
                                                    chargingCurrentData.getConnectorId(),
                                                    0,
                                                    chargingCurrentData.getIdTag(),
                                                    null,
                                                    GlobalVariables.getHumaxUserType(),       /////////Customer unit price 에서만 userType(A:회원, B:비회원) 사용 한다.
                                                    false));
                                        } else {
                                            chargingCurrentData.setPowerUnitPrice(Double.parseDouble(chargerConfiguration.getTestPrice()));
                                            ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).setUiSeq(UiSeq.PLUG_CHECK);
                                            fragmentChange.onFragmentChange(getChannel(), UiSeq.PLUG_CHECK, "PLUG_CHECK", null);
                                        }

                                        if (!Objects.equals(chargingCurrentData.getChargePointStatus(), ChargePointStatus.Preparing) &&
                                                Objects.equals(chargerConfiguration.getAuthMode(), "0")) {
                                            chargingCurrentData.setChargePointStatus(ChargePointStatus.Preparing);
                                            processHandler.sendMessage(onMakeHandlerMessage(
                                                    GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                                                    chargingCurrentData.getConnectorId(),
                                                    0,
                                                    null,
                                                    null,
                                                    null,
                                                    false));
                                        }
                                    }
                                } else {
                                    String certificationReason = status.name();
                                    if (Objects.equals(uiSeq, UiSeq.CHARGING)) {
                                        ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).setUiSeq(UiSeq.CHARGING);
                                        fragmentChange.onFragmentChange(getChannel(), UiSeq.CHARGING, "CHARGING", "small");
                                        getToastPositionMake().onShowToast(getChannel(), "충전 중지 인증 실패 : " + certificationReason);
                                    } else {
                                        ((MainActivity) MainActivity.mContext).getChargingCurrentData(getChannel()).setAuthorizeResult(false);
                                        ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).onHome();
                                        getToastPositionMake().onShowToast(getChannel(), "인증 실패 : " + certificationReason);
                                    }
                                }
                            } else if (Objects.equals("getPrice", actionName)) {
                                DataTransferStatus status = DataTransferStatus.valueOf(jsonObject.getString("status"));
                                JSONObject jsonObjectDataPrices = new JSONObject(jsonObject.getString("data"));
                                JSONArray jsonArrayUnitPrice = jsonObjectDataPrices.getJSONArray("unitPrice");
                                JSONObject jsonObjectUnitPrice = jsonArrayUnitPrice.getJSONObject(0);
                                if (Objects.equals(DataTransferStatus.Accepted, status)) {
                                    chargingCurrentData.setPowerUnitPrice(jsonObjectUnitPrice.getDouble("unitPrice"));
                                    //비회원 회원인 경우 구분 (회원 : plug_wait  /  비회원 : auth_credit)
                                    switch (chargingCurrentData.getPaymentType().value()) {
                                        case 1:
                                        case 3:
                                        case 4:
                                            ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).setUiSeq(UiSeq.PLUG_CHECK);
                                            fragmentChange.onFragmentChange(getChannel(), UiSeq.PLUG_CHECK, "PLUG_CHECK", null);
                                            break;
                                        case 2:
                                            ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).setUiSeq(UiSeq.CREDIT_CARD);
                                            fragmentChange.onFragmentChange(getChannel(), UiSeq.CREDIT_CARD, "CREDIT_CARD", null);
                                            break;
                                    }
                                }
                                //단가 Accept ==> status preparing
                                if (!Objects.equals(chargingCurrentData.getChargePointStatus(), ChargePointStatus.Preparing)) {
                                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Preparing);
                                    processHandler.sendMessage(onMakeHandlerMessage(
                                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                                            chargingCurrentData.getConnectorId(),
                                            0,
                                            null,
                                            null,
                                            null,
                                            false));
                                }
                            } else if (Objects.equals("StartTransaction", actionName)) {
                                /** remote stop 에서 connectorId(채널) 정보 필요 ==> transactionId로 connectorId */
                                getConnectorIdHashMap.put(jsonObject.getInt("transactionId"), connectorId);
                                //서버에서 transactionId 받음 ==> stopTransaction 계속하여 사용.
                                chargingCurrentData.setTransactionId(jsonObject.getInt("transactionId"));

                                GlobalVariables.dumpTransactionId = jsonObject.getInt("transactionId");

                                jsonObjectData = jsonObject.getJSONObject("idTagInfo");
                                AuthorizationStatus status = AuthorizationStatus.valueOf(jsonObjectData.getString("status"));
                                String parentIdTag = jsonObjectData.has("parentIdTag") ? jsonObjectData.getString("parentIdTag") : "";
                                String expiryDate = jsonObjectData.has("expiryDate") ? jsonObjectData.getString("expiryDate") : "";
                                //accept continue
                                uiSeq = ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).getUiSeq();
                                if (Objects.equals(status, AuthorizationStatus.Accepted)) {
                                    ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).setUiSeq(UiSeq.CHARGING);
                                    fragmentChange.onFragmentChange(getChannel(), UiSeq.CHARGING, "CHARGING", "small");
                                } else {
                                    //충전기 정지
                                    TxData txData = ((MainActivity) MainActivity.mContext).getControlBoard().getTxData(getChannel());
                                    txData.setStop(true);
                                    txData.setStart(false);
                                    ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).onMeterValueStop();
                                    // CREDIT 선 결제가  있는 경우
                                    if (chargingCurrentData.isPrePaymentResult()) {
                                        // 부분 취소.....
//                                        ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).onNoCardCancelTrigger();
                                        chargingCurrentData.setPartialCancelPayment(chargingCurrentData.getPrePayment());
                                        ((MainActivity) MainActivity.mContext).getTls3800().onTLS3800Request(channel, TLS3800.CMD_TX_PAYCANCEL, 4);
                                    }
                                    ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).onHome();
                                }
                            } else if (Objects.equals("StopTransaction", actionName)) {
                                jsonObjectData = jsonObject.getJSONObject("idTagInfo");
                                AuthorizationStatus status = AuthorizationStatus.valueOf(jsonObjectData.getString("status"));
                                String parentIdTag = jsonObjectData.has("parentIdTag") ? jsonObjectData.getString("parentIdTag") : "";
                                String expiryDate = jsonObjectData.has("expiryDate") ? jsonObjectData.getString("expiryDate") : "";

                                ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).onMeterValueStop();

                            } else if (Objects.equals("payInfo", actionName)) {
                                DataTransferStatus status = DataTransferStatus.valueOf(jsonObject.getString("status"));
                                if (Objects.equals(status, DataTransferStatus.Accepted)) {
                                    jsonObjectData = jsonObject.getJSONObject("data");
                                    DataTransferStatus statusData = DataTransferStatus.valueOf(jsonObjectData.getString("status"));
                                    if (Objects.equals(chargingCurrentData.getPayId(), jsonObjectData.getString("payId"))) {
                                        //커플러 연결
                                        ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).setUiSeq(UiSeq.PLUG_CHECK);
                                        ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(getChannel(), UiSeq.PLUG_CHECK, "PLUG_CHECK", null);
                                    }
                                } else {
                                    if (chargingCurrentData.isPrePaymentResult()) {
//                                        ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).onNoCardCancelTrigger();
                                        getToastPositionMake().onShowToast(getChannel(), "선결제 취소 금액 : " + chargingCurrentData.getPrePayment() + "원");
                                    }
                                }
                            } else if (Objects.equals("PartialCancel", actionName)) {
                                DataTransferStatus status = DataTransferStatus.valueOf(jsonObject.getString("status"));
                                if (Objects.equals(status, DataTransferStatus.Accepted)) {
                                    JSONObject data = jsonObject.has("data") ? new JSONObject(jsonObject.getString("data")) : null;
                                    if (data != null) {
                                        //DataTransferStatus statusData = DataTransferStatus.valueOf(data.getString("status"));
                                    }
                                }
                            } else if (Objects.equals("resultPrice", actionName)) {
                                DataTransferStatus status = DataTransferStatus.valueOf(jsonObject.getString("status"));
                                if (Objects.equals(status, DataTransferStatus.Accepted)) {
                                    //accept
                                    JSONObject jsonObjectDataResult = new JSONObject(jsonObject.getString("data"));
//                                    int connectorId = jsonObjectDataResult.getInt("connectorId");
//                                    int resultPrice = jsonObjectDataResult.getInt("resultPrice");
                                }
                            } else if (Objects.equals("smsMessage", actionName)) {
                                DataTransferStatus status = DataTransferStatus.valueOf(jsonObject.getString("status"));
                                if (Objects.equals(status, DataTransferStatus.Accepted)) {
                                    //sms success
                                }
                            } else if (Objects.equals("FirmwareStatusNotification", actionName)) {
                                //DownLoading
                                if (Objects.equals(chargerConfiguration.getFirmwareStatus(), FirmwareStatus.Downloading)) {
                                    // downloading start
                                    // 나중에 삭제 해야 함.
//                                    String location = "211.44.234.112";
//                                    location = "192.168.30.120";
//                                    SftpRxJava sftpRxJava = new SftpRxJava(FileTransType.FIRMWARE,location);
//                                    sftpRxJava.downloadTask();
                                    //////////////////////////////////////////////////////////////////
                                } else if (Objects.equals(chargerConfiguration.getFirmwareStatus(), FirmwareStatus.Downloaded)) {
                                    //installing reboot
                                    chargerConfiguration.setFirmwareStatus(FirmwareStatus.Installing);
                                    FirmwareStatusNotificationRequest firmwareStatusNotificationRequest = new FirmwareStatusNotificationRequest(chargerConfiguration.getFirmwareStatus());
                                    onSend(100, firmwareStatusNotificationRequest.getActionName(), firmwareStatusNotificationRequest);
                                } else if (Objects.equals(chargerConfiguration.getFirmwareStatus(), FirmwareStatus.Installing)) {
                                    // FirmwareStatusNotification file create
                                    String fileName = "FirmwareStatusNotification";
                                    boolean check = fileManagement.fileCreate(fileName, "Firmware");
                                    ((MainActivity) MainActivity.mContext).onRebooting("Hard");

                                } else if (Objects.equals(chargerConfiguration.getFirmwareStatus(), FirmwareStatus.Installed)) {
                                    // update firmware 다운 전에 GlobalVariables.ChargerOperation[] = true ==> Unavailable
                                    for (int i = 0; i < GlobalVariables.maxPlugCount; i++)
                                        GlobalVariables.ChargerOperation[i] = true;
                                    onChargerOperateSave();
                                    chargerConfiguration.setFirmwareStatus(FirmwareStatus.Idle);
                                } else if (Objects.equals(chargerConfiguration.getFirmwareStatus(), FirmwareStatus.DownloadFailed) ||
                                        Objects.equals(chargerConfiguration.getFirmwareStatus(), FirmwareStatus.InstallationFailed)) {
                                    // update firmware 다운 전에 GlobalVariables.ChargerOperation[] = false ==> Unavailable
                                    for (int i = 0; i < GlobalVariables.maxPlugCount; i++)
                                        GlobalVariables.ChargerOperation[i] = true;
                                    onChargerOperateSave();
                                    // Status Notification
                                    processHandler.sendMessage(onMakeHandlerMessage(
                                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION_BOOT,
                                            100,
                                            0,
                                            null,
                                            null,
                                            null,
                                            false));
                                } else if (Objects.equals("LogStatusNotification", actionName)) {
                                    // not response
                                }
                            } else if (Objects.equals("SignedFirmwareStatusNotification", actionName)) {
                                if (Objects.equals(chargerConfiguration.getSignedFirmwareStatus(), SignedFirmwareStatus.Downloaded)) {

                                    //   Downloaded
                                    try {
                                        X509Certificate cert = loadCertificate(GlobalVariables.getSigningCertificate());
                                        PublicKey publicKey = cert.getPublicKey();

                                        byte[] signature = Base64.decode(GlobalVariables.getSignature(), Base64.DEFAULT);

                                        String LOCAL_PATH = GlobalVariables.getRootPath() + File.separator + "DEVS050D12.apk";
                                        boolean isVerified = verifySignature(publicKey, signature, LOCAL_PATH);

                                        chargerConfiguration.setSignedFirmwareStatus(isVerified ? SignedFirmwareStatus.Installing : SignedFirmwareStatus.InvalidSignature);
                                        //installing reboot

                                        SignedFirmwareStatusNotificationRequest signedFirmwareStatusNotificationRequest =
                                                new SignedFirmwareStatusNotificationRequest(chargerConfiguration.getSignedFirmwareStatus());
                                        onSend(100, signedFirmwareStatusNotificationRequest.getActionName(), signedFirmwareStatusNotificationRequest);

                                    } catch (Exception e) {
                                        logger.error(e.getMessage());
                                    }
                                } else if (Objects.equals(chargerConfiguration.getSignedFirmwareStatus(), SignedFirmwareStatus.Installing)) {
                                    // FirmwareStatusNotification file create
                                    String fileName = "FirmwareStatusNotification";
                                    boolean check = fileManagement.fileCreate(fileName, "SignedFirmware");
                                    ((MainActivity) MainActivity.mContext).onRebooting("Hard");
                                } else if (Objects.equals(chargerConfiguration.getSignedFirmwareStatus(), SignedFirmwareStatus.Installed)) {
                                    // update firmware 다운 전에 GlobalVariables.ChargerOperation[] = true ==> Unavailable
                                    for (int i = 0; i < GlobalVariables.maxPlugCount; i++)
                                        GlobalVariables.ChargerOperation[i] = true;
                                    onChargerOperateSave();
                                    chargerConfiguration.setFirmwareStatus(FirmwareStatus.Idle);
                                } else if (Objects.equals(chargerConfiguration.getSignedFirmwareStatus(), SignedFirmwareStatus.DownloadFailed) ||
                                        Objects.equals(chargerConfiguration.getSignedFirmwareStatus(), SignedFirmwareStatus.InstallationFailed)) {
                                    // update firmware 다운 전에 GlobalVariables.ChargerOperation[] = false ==> Unavailable
                                    for (int i = 0; i < GlobalVariables.maxPlugCount; i++)
                                        GlobalVariables.ChargerOperation[i] = true;
                                    onChargerOperateSave();
                                    // Status Notification ???
                                    processHandler.sendMessage(onMakeHandlerMessage(
                                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION_BOOT,
                                            100,
                                            0,
                                            null,
                                            null,
                                            null,
                                            false));
                                }
                            } else if (Objects.equals("CustomUnitPrice", actionName)) {
                                DataTransferStatus status = DataTransferStatus.valueOf(jsonObject.getString("status"));
                                if (Objects.equals(DataTransferStatus.Accepted, status)) {
//                                    String idTag;
//                                    UiSeq currentSeq ;
//                                    try {
//                                        currentSeq = ((MainActivity) MainActivity.mContext).getClassUiProcess(channel).getUiSeq();
//                                    } catch (Exception e) {
//                                        currentSeq = UiSeq.FINISH;
//                                    }
//                                    try {
//                                        idTag = chargingCurrentData.getIdTag();
//                                    } catch (Exception e) {
//                                        idTag = "";
//                                    }
//                                    if (TextUtils.isEmpty(idTag) || currentSeq == UiSeq.FAULT ||
//                                            currentSeq == UiSeq.FINISH_WAIT || currentSeq == UiSeq.FINISH) {
                                    if (GlobalVariables.isCustomUnitPriceReq()) {
                                        String dataStr = jsonObject.getString("data");
                                        JSONObject dataJson = new JSONObject(dataStr);
                                        dataJson.put("userType", GlobalVariables.getHumaxUserType());
                                        //////
                                        boolean chk;
                                        File file = new File(GlobalVariables.getRootPath() + File.separator + GlobalVariables.UNIT_FILE_NAME);

                                        if (file.exists() && fileManagement.countFileRows(file) >= 2 ) chk = file.delete();
                                        chk = fileManagement.stringToFileSave(GlobalVariables.getRootPath(), GlobalVariables.UNIT_FILE_NAME, dataJson.toString(), true);
                                        if (Objects.equals(GlobalVariables.getHumaxUserType(), "A")) {
                                            GlobalVariables.setHumaxUserType("B");
                                            // Custom Unit Price
                                            processHandler.sendMessage(onMakeHandlerMessage(
                                                    GlobalVariables.MESSAGE_CUSTOM_UNIT_PRICE,
                                                    connectorId,
                                                    0,
                                                    null,
                                                    null,
                                                    GlobalVariables.getHumaxUserType(),
                                                    false));

                                            //CustomStatusNoti
                                            processHandler.sendMessage(onMakeHandlerMessage(
                                                    GlobalVariables.MESSAGE_CUSTOM_STATUS_NOTIFICATION,
                                                    chargingCurrentData.getConnectorId(),
                                                    0,
                                                    null,
                                                    null,
                                                    null,
                                                    false));

                                        } else if (Objects.equals(GlobalVariables.getHumaxUserType(), "B")) {
                                            GlobalVariables.setCustomUnitPriceReq(false);
                                        }
                                    } else {
                                        JSONObject data = jsonObject.has("data") ? new JSONObject(jsonObject.getString("data")) : null;
                                        assert data != null;
                                        chargingCurrentData.setHmChargingLimitFee(data.getInt("HmChargingLimitFee"));
                                        JSONArray jsonArrayTariff = data.getJSONArray("tariff");
                                        chargingCurrentData.setPowerUnitPrice(onFindUnitPrice(jsonArrayTariff));
                                        uiSeq = ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).getUiSeq();
                                        if (!Objects.equals(UiSeq.FINISH, uiSeq)){
                                            //비회원 회원인 경우 구분 (회원 : plug_wait  /  비회원 : auth_credit)
                                            switch (chargingCurrentData.getPaymentType().value()) {
                                                case 1:
                                                case 4:
                                                    ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).setUiSeq(UiSeq.PLUG_CHECK);
                                                    fragmentChange.onFragmentChange(getChannel(),UiSeq.PLUG_CHECK, "PLUG_CHECK", null);
                                                    break;
                                                case 2:
                                                    ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).setUiSeq(UiSeq.CREDIT_CARD);
                                                    fragmentChange.onFragmentChange(getChannel(),UiSeq.CREDIT_CARD, "CREDIT_CARD", null);
                                                    break;
                                            }
                                        }

                                        //단가 Accept ==> status preparing
                                        if (!Objects.equals(chargingCurrentData.getChargePointStatus(), ChargePointStatus.Preparing)
                                                && Objects.equals(chargingCurrentData.getPowerMeterUsePay(), 0)) {
                                            chargingCurrentData.setChargePointStatus(ChargePointStatus.Preparing);
                                            processHandler.sendMessage(onMakeHandlerMessage(
                                                    GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                                                    chargingCurrentData.getConnectorId(),
                                                    0,
                                                    null,
                                                    null,
                                                    null,
                                                    false));
                                        } else if (Objects.equals(UiSeq.FINISH, uiSeq)) {
                                            // custom unit price ==> 다시 금액 계산
                                            chargingCurrentData.setPowerMeterUsePay(chargingCurrentData.getPowerMeterUse() * 0.01 * chargingCurrentData.getPowerUnitPrice());
                                            //
                                            ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).setUiSeq(UiSeq.FINISH);
                                            fragmentChange.onFragmentChange(getChannel(), UiSeq.FINISH, "FINISH", null);
                                        } else if (Objects.equals(UiSeq.FAULT, uiSeq)) {
                                            // custom unit price ==> 다시 금액 계산
                                            chargingCurrentData.setPowerMeterUsePay(chargingCurrentData.getPowerMeterUse() * 0.01 * chargingCurrentData.getPowerUnitPrice());
                                        }
                                    }
                                }
                            } else if (Objects.equals("Payment", actionName)) {
                                DataTransferStatus status = DataTransferStatus.valueOf(jsonObject.getString("status"));
                                if (Objects.equals(DataTransferStatus.Accepted, status)) {
                                    jsonObjectData =  new JSONObject(jsonObject.getString("data"));
                                    chargingCurrentData.setIdTag(jsonObjectData.getString("idTag"));
                                    // preparing
                                    if (!Objects.equals(chargingCurrentData.getChargePointStatus(), ChargePointStatus.Preparing) &&
                                            Objects.equals(chargerConfiguration.getAuthMode(), "0")) {
                                        chargingCurrentData.setChargePointStatus(ChargePointStatus.Preparing);
                                        processHandler.sendMessage(onMakeHandlerMessage(
                                                GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                                                chargingCurrentData.getConnectorId(),
                                                0,
                                                null,
                                                null,
                                                null,
                                                false));

                                        //커플러 연결
                                        ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).setUiSeq(UiSeq.PLUG_CHECK);
                                        ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(getChannel(), UiSeq.PLUG_CHECK, "PLUG_CHECK", null);
                                    }
                                } else {
                                    /////////// 무카드 취소
                                    //선 결제에 의한 무카드 취소 (4:무카드 취소)(5:부분 취소)
                                    if (chargingCurrentData.isPrePaymentResult()) {
                                        chargingCurrentData.setPartialCancelPayment(chargingCurrentData.getPrePayment());
                                        ((MainActivity) MainActivity.mContext).getTls3800().onTLS3800Request(getChannel(), TLS3800.CMD_TX_PAYCANCEL, 4);
                                        getToastPositionMake().onShowToast(getChannel(), "선결제 취소 금액 : " + chargingCurrentData.getPrePayment() + "원");
                                        ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).onHome();
                                        ///

                                    }
                                }
                            } else if (Objects.equals("PaymentCancel", actionName)) {
                                DataTransferStatus status = DataTransferStatus.valueOf(jsonObject.getString("status"));

                            }

                            //central system response 받아 hashMapUid 삭제 StatusNotification
                            if (!TextUtils.isEmpty(actionName)) {
                                newHashMapUuid.remove(message.getId());
                            }
                            break;
                        case 2:
                            if (Objects.equals("Reset", actionName)) {
                                ResetType type = ResetType.valueOf(jsonObject.getString("type"));
                                ResetConfirmation resetConfirmation = new ResetConfirmation(ResetStatus.Accepted);
                                onResultSend(actionName, message.getId(), resetConfirmation);

                                // charging status check
                                for (int i = 0; i < GlobalVariables.maxChannel; i++) {
                                    // accept
                                    uiSeq = ((MainActivity) MainActivity.mContext).getClassUiProcess(i).getUiSeq();
                                    if (Objects.equals(uiSeq, UiSeq.CHARGING)) {
                                        ((MainActivity) MainActivity.mContext).getClassUiProcess(i).onResetStop(i, type);
                                    }
//                                    if (Objects.equals(type, ResetType.Hard)) {
//                                        ((MainActivity) MainActivity.mContext).getChargingCurrentData(i).setStopReason(Reason.HardReset);
//                                        ((MainActivity) MainActivity.mContext).getChargingCurrentData(i).setReBoot(true);
//                                    } else if (Objects.equals(type, ResetType.Soft)) {
//                                        ((MainActivity) MainActivity.mContext).getChargingCurrentData(i).setStopReason(Reason.SoftReset);
//                                        ((MainActivity) MainActivity.mContext).getChargingCurrentData(i).setReBoot(true);
//                                    }
                                }
                                ((MainActivity) MainActivity.mContext).onRebooting(Objects.equals(ResetType.Hard, type) ? "Hard" : "Soft" );
                            } else if (Objects.equals("RemoteStartTransaction", actionName)) {
                                // connectorId ==> 채널 정보
                                int realConnectorId = jsonObject.has("connectorId") ? jsonObject.getInt("connectorId") : -1;
                                chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(realConnectorId - 1);
                                chargingCurrentData.setConnectorId(realConnectorId);
                                String idTag = jsonObject.has("idTag") ? jsonObject.getString("idTag") : "";
                                chargingCurrentData.setIdTag(idTag);
                                chargingCurrentData.setPaymentType(PaymentType.MEMBER);
                                uiSeq = ((MainActivity) MainActivity.mContext).getClassUiProcess(realConnectorId - 1).getUiSeq();

                                // 충전중 또는 connectorId == 0 이면 reject
                                if (uiSeq == UiSeq.CHARGING || realConnectorId == 0) {
                                    processHandler.sendMessage(onMakeHandlerMessage(
                                            GlobalVariables.MESSAGE_HANDLER_REMOTE_START_TRANSACTION,
                                            realConnectorId,
                                            0,
                                            idTag,
                                            message.getId(),
                                            null,
                                            false));
                                } else {
                                    // remoteStart 응답
                                    processHandler.sendMessage(onMakeHandlerMessage(
                                            GlobalVariables.MESSAGE_HANDLER_REMOTE_START_TRANSACTION,
                                            realConnectorId,
                                            0,
                                            idTag,
                                            message.getId(),
                                            null,
                                            realConnectorId > 0 ));

                                    // Custom Unit Price
                                    chargingCurrentData.setPaymentType(PaymentType.APP);
                                    GlobalVariables.setHumaxUserType("C");
                                    processHandler.sendMessage(onMakeHandlerMessage(
                                            GlobalVariables.MESSAGE_CUSTOM_UNIT_PRICE,
                                            realConnectorId,
                                            0,
                                            idTag,
                                            null,
                                            GlobalVariables.getHumaxUserType(),
                                            false));
                                }
                            } else if (Objects.equals("RemoteStopTransaction", actionName)) {
                                boolean result = false;
                                int transactionId = jsonObject.has("transactionId") ? jsonObject.getInt("transactionId") : 0;
                                int realConnectorId = -1, channelId = 0;
                                realConnectorId = Optional.ofNullable(getConnectorIdHashMap.get(transactionId)).orElse(-1);
                                if (realConnectorId != -1) {
                                    channelId = realConnectorId == 1 ? 0 : 1;
                                    chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(channelId);
                                    result = chargingCurrentData.getTransactionId() == transactionId;
                                }

                                RemoteStartStopStatus remoteStartStopStatus = result ? RemoteStartStopStatus.Accepted : RemoteStartStopStatus.Rejected;
                                RemoteStopTransactionConfirmation remoteStopTransactionConfirmation = new RemoteStopTransactionConfirmation(remoteStartStopStatus);
                                onResultSend(remoteStopTransactionConfirmation.getActionName(), message.getId(), remoteStopTransactionConfirmation);

                                if (result) {
                                    uiSeq = ((MainActivity) MainActivity.mContext).getClassUiProcess(channelId).getUiSeq();
                                    if (Objects.equals(uiSeq, UiSeq.CHARGING)) {
                                        ((MainActivity) MainActivity.mContext).getClassUiProcess(channelId).onRemoteTransactionStop(channelId, Reason.Remote);
                                    }
                                    //hash map delete
                                    getConnectorIdHashMap.remove(transactionId);
                                }
                            } else if (Objects.equals("DataTransfer", actionName)) {
                                //* dataTransfer-(messageId) */
                                if (jsonObject.has("messageId")) {
                                    if (Objects.equals(jsonObject.getString("messageId"), "announce")) {
                                        String vendorId = jsonObject.has("vendorId") ? jsonObject.getString("vendorId") : null;
                                        JSONObject jsonObjectDataAnnounce = new JSONObject(jsonObject.getString("data"));
                                        fileManagement.stringToFileSave(GlobalVariables.getRootPath(), "announce", jsonObjectDataAnnounce.toString(), false);
                                        processHandler.sendMessage(onMakeHandlerMessage(
                                                GlobalVariables.MESSAGE_HANDLER_ANNOUNCE,
                                                100,
                                                0,
                                                null,
                                                message.getId(),
                                                null,
                                                true));
                                    }
                                } else {
                                    DataTransferConfirmation dataTransferConfirmation = new DataTransferConfirmation(DataTransferStatus.Rejected);
                                    onResultSend(actionName, message.getId(), dataTransferConfirmation);
                                }
                            } else if (Objects.equals("TriggerMessage", actionName)) {
                                int triggerConnectorId = jsonObject.has("connectorId") ? jsonObject.getInt("connectorId") : -1;
                                boolean chkConnectorId = triggerConnectorId == 1 || triggerConnectorId == 2;
                                TriggerMessageRequestType triggerMessageRequestType = TriggerMessageRequestType.valueOf(jsonObject.getString("requestedMessage"));
                                boolean resultCheck = Objects.equals(triggerMessageRequestType, TriggerMessageRequestType.MeterValues) ? chkConnectorId : triggerConnectorId >= 0;
                                TriggerMessageConfirmation triggerMessageConfirmation = new TriggerMessageConfirmation(resultCheck ? TriggerMessageStatus.Accepted : TriggerMessageStatus.Rejected);
                                onResultSend(actionName, message.getId(), triggerMessageConfirmation);

                                //TriggerMessageRequestType 별 응답
                                if (Objects.equals(triggerMessageRequestType, TriggerMessageRequestType.MeterValues) && chkConnectorId) {
                                    processHandler.onMeterValueSendOne(triggerConnectorId - 1);
                                } else if (Objects.equals(triggerMessageRequestType, TriggerMessageRequestType.Heartbeat)) {
                                    HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
                                    onSend(heartbeatRequest.getActionName(), heartbeatRequest);
                                } else if (Objects.equals(triggerMessageRequestType, TriggerMessageRequestType.StatusNotification)) {
                                    processHandler.sendMessage(onMakeHandlerMessage(
                                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                                            triggerConnectorId,
                                            0,
                                            null,
                                            null,
                                            null,
                                            false));
                                } else if (Objects.equals(triggerMessageRequestType, TriggerMessageRequestType.DiagnosticsStatusNotification)) {
                                    DiagnosticsStatusNotificationRequest diagnosticsStatusNotificationRequest =
                                            new DiagnosticsStatusNotificationRequest(chargerConfiguration.getDiagnosticsStatus());
                                    onSend(100, diagnosticsStatusNotificationRequest.getActionName(), diagnosticsStatusNotificationRequest);
                                } else if (Objects.equals(triggerMessageRequestType, TriggerMessageRequestType.FirmwareStatusNotification)) {
                                    processHandler.sendMessage(onMakeHandlerMessage(
                                            GlobalVariables.MESSAGE_HANDLER_FIRMWARE_STATUS,
                                            100,
                                            1,
                                            null,
                                            null,
                                            null,
                                            false
                                    ));
                                } else if (Objects.equals(triggerMessageRequestType, TriggerMessageRequestType.BootNotification)) {
                                    BootNotificationRequest bootNotificationRequest;
                                    try {
                                        bootNotificationRequest = new BootNotificationRequest(
                                                chargerConfiguration.getChargerPointVendor(),
                                                chargerConfiguration.getChargerPointModel()
                                        );
                                        bootNotificationRequest.setFirmwareVersion(GlobalVariables.VERSION);
                                        bootNotificationRequest.setImsi(chargerConfiguration.getImsi());
                                        bootNotificationRequest.setChargePointSerialNumber(chargerConfiguration.getChargerPointSerialNumber());
                                        onSend(100, bootNotificationRequest.getActionName(), bootNotificationRequest);
                                    } catch (Exception e){
                                        logger.error("{}", e.getMessage());
                                    }
                                }


                                //// original 소스
    //                                int connectorId = jsonObject.has("connectorId") ? jsonObject.getInt("connectorId") : -1;
    //                                TriggerMessageRequestType triggerMessageRequestType = TriggerMessageRequestType.valueOf(jsonObject.getString("requestedMessage"));
    //                                processHandler.sendMessage(onMakeHandlerMessage(
    //                                        GlobalVariables.MESSAGE_HANDLER_TRIGGER_MESSAGE,
    //                                        connectorId,
    //                                        0,
    //                                        null,
    //                                        message.getId(),
    //                                        null,
    //                                        connectorId >= 1));
    //                                if (Objects.equals(triggerMessageRequestType, TriggerMessageRequestType.MeterValues)) {
    //                                    //Meter Value response
    //                                    processHandler.onMeterValueSendOne(connectorId);
    //                                }
                            } else if (Objects.equals("ChangeConfiguration", actionName)) {
                                boolean result;
                                GlobalVariables.setNotSupportedKey(false);
                                String key = jsonObject.has("key") ? jsonObject.getString("key") : "";
                                String value = jsonObject.has("value") ? jsonObject.getString("value") : "";
                                //valid check
                                if (Objects.equals(key, "MeterValueSampleInterval") && Integer.parseInt(value) < 0) {
                                    result = false;
                                } else {
                                    result = setConfigurationValue(key, value);
                                    ((MainActivity) MainActivity.mContext).getConfigurationKeyRead().onRead();
                                    if (Objects.equals(key, "AuthorizationKey")) {
                                        ((MainActivity) MainActivity.mContext).getSocketReceiveMessage().getSocket().disconnect();
                                    }
                                    if (Objects.equals(key, "ClockAlignedDataInterval")) {
    //                                    if (Integer.parseInt(value) == 0) {
    //                                        for (int i = 0; i < GlobalVariables.maxChannel; i++) {
    //                                            ((MainActivity) MainActivity.mContext).getClassUiProcess(i).onMeterValuesAlignedDataStop();
    //                                        }
    //                                    } else {
    //                                        for (int i = 0; i < GlobalVariables.maxChannel; i++) {
    //                                            ((MainActivity) MainActivity.mContext).getClassUiProcess(i).onMeterValuesAlignedDataStart(i, Integer.parseInt(value));
    //                                        }
    //                                    }
                                    }
                                }
                                processHandler.sendMessage(onMakeHandlerMessage(
                                        GlobalVariables.MESSAGE_CHANGE_CONFIGURATION,
                                        100,
                                        1,
                                        null,
                                        message.getId(),
                                        null,
                                        result));

                            } else if (Objects.equals("ChangeAvailability", actionName)) {
                                int connectorId = jsonObject.has("connectorId") ? jsonObject.getInt("connectorId") : -1;
                                AvailabilityType type = AvailabilityType.valueOf(jsonObject.getString("type"));
                                //change availability response
                                boolean checkType = type == AvailabilityType.Operative;
                                processHandler.sendMessage(onMakeHandlerMessage(
                                        GlobalVariables.MESSAGE_CHANGE_AVAILABILITY,
                                        connectorId,
                                        1,
                                        null,
                                        message.getId(),
                                        null,
                                        true));
                                switch (connectorId) {
                                    case 0:
                                        for (int i = 0; i < GlobalVariables.maxPlugCount; i++) {
                                            if (!Objects.equals(GlobalVariables.ChargerOperation[i], checkType)) {
                                                //send
                                                GlobalVariables.ChargerOperation[i] = checkType;
                                            }
                                        }
                                        processHandler.sendMessage(onMakeHandlerMessage(
                                                GlobalVariables.MESSAGE_HANDLER_CHANGE_AVAILABILITY,
                                                connectorId,
                                                1,
                                                null,
                                                null,
                                                null,
                                                false));
                                        break;
                                    case 1:
                                    case 2:
                                        //
                                        GlobalVariables.ChargerOperation[connectorId] = checkType;
                                        processHandler.sendMessage(onMakeHandlerMessage(
                                                GlobalVariables.MESSAGE_HANDLER_CHANGE_AVAILABILITY,
                                                connectorId,
                                                1,
                                                null,
                                                null,
                                                null,
                                                false));
                                        break;
                                }

                                //저장을 한다,
                                onChargerOperateSave();

                                } else if (Objects.equals("GetConfiguration", actionName)) {
                                    try {
                                        GlobalVariables.setConfigurationKey(jsonObject.has("key") ? jsonObject.getString("key").replaceAll("[^a-zA-Z]", "") : "");
                                        processHandler.sendMessage(onMakeHandlerMessage(
                                                GlobalVariables.MESSAGE_GET_CONFIGURATION,
                                                0,
                                                1,
                                                null,
                                                message.getId(),
                                                null,
                                                false));
                                    } catch (Exception e) {
                                        logger.error("GetConfiguration error : {}", e.getMessage());
                                    }

                                } else if (Objects.equals("ClearCache", actionName)) {
                                    try {
                                        processHandler.sendMessage(onMakeHandlerMessage(
                                                GlobalVariables.MESSAGE_CLEAR_CACHE,
                                                0,
                                                1,
                                                null,
                                                message.getId(),
                                                null,
                                                false));
                                    } catch (Exception e) {
                                        logger.error("ClearCache error : {}", e.getMessage());
                                    }
                                } else if (Objects.equals("SendLocalList", actionName)) {
                                    try {
                                        boolean status = false, authSupported = false;
                                        //configuration key SupportedFeatureProfiles check
                                        authSupported = onSupportedFeatureProfiles("LocalAuthListManagement");
                                        if (authSupported) {
                                            int listVersion = 0;
                                            String resultValue = "none";
                                            //서버 에서 받은 데이터
                                            int newListVersion = jsonObject.getInt("listVersion");
                                            UpdateType updateType = UpdateType.valueOf(jsonObject.getString("updateType"));
                                            //configurationKey get list version

                                            //localAuthorizationList file not found
                                            File file = new File(GlobalVariables.getRootPath() + File.separator + "localAuthorizationList");
                                            if (!file.exists()) {
                                                fileManagement.stringToFileSave(GlobalVariables.getRootPath(), "localAuthorizationList", message.getPayload().toString(), false);
                                                GlobalVariables.updateStatus = UpdateStatus.Accepted;
                                            } else {
                                                JSONObject jsonLocalAuthorizationList = new JSONObject(fileManagement.getStringFromFile(GlobalVariables.getRootPath() + File.separator + "localAuthorizationList"));
                                                int fileListVersion = jsonLocalAuthorizationList.getInt("listVersion");
                                                //configurationKey localAuthorizationList check
                                                resultValue = getConfigurationValue("LocalAuthListEnabled");

                                                GlobalVariables.updateStatus = Objects.equals(resultValue, "none") || Objects.equals(resultValue, "false") ? UpdateStatus.NotSupported : UpdateStatus.Accepted;

                                                //Accepted, Failed, Not supported, versionMismatch
                                                JSONArray localAuthorizationList = jsonObject.getJSONArray("localAuthorizationList");
                                                if (Objects.equals(GlobalVariables.updateStatus, UpdateStatus.Accepted)) {
                                                    if (Objects.equals(UpdateType.Full, updateType)) {
                                                        if (localAuthorizationList == null) {
                                                            String localList = "{\"listVersion\":\"0\",\"localAuthorizationList\":\"\"}" ;
                                                            status = fileManagement.stringToFileSave(GlobalVariables.getRootPath(), "localAuthorizationList", localList, false);
                                                        } else {
                                                            status = fileManagement.stringToFileSave(GlobalVariables.getRootPath(), "localAuthorizationList", jsonObject.toString(), false);
                                                        }
                                                        GlobalVariables.updateStatus = status ? UpdateStatus.Accepted : UpdateStatus.Failed;
                                                    } else if (Objects.equals(UpdateType.Differential, updateType)) {
                                                        //부분변경
                                                        try {
                                                            listVersion = jsonObject.getInt("listVersion");
                                                            if (fileListVersion <= newListVersion) {
                                                                String sUpdateType = jsonObject.getString("updateType");
                                                                for (int i = 0; i < localAuthorizationList.length(); i++) {
                                                                    JSONObject contDetail = localAuthorizationList.getJSONObject(i);
                                                                    status = setLocalAuthorizationList(newListVersion, contDetail);
                                                                }
                                                                GlobalVariables.updateStatus = status ? UpdateStatus.Accepted : UpdateStatus.Failed;
                                                            } else {
                                                                GlobalVariables.updateStatus = UpdateStatus.VersionMismatch;
                                                            }
                                                        } catch (Exception e) {
                                                            logger.error("{}", e.getMessage());
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            GlobalVariables.updateStatus = UpdateStatus.NotSupported;
                                        }
                                        //send
                                        SendLocalListConfirmation sendLocalListConfirmation = new SendLocalListConfirmation(GlobalVariables.updateStatus);
                                        onResultSend(actionName, message.getId(), sendLocalListConfirmation);
                                    } catch (Exception e) {
                                        logger.error("SendLocalList error : {}", e.getMessage());
                                    }
                                } else if (Objects.equals("ClearChargingProfile", actionName)) {
                                    try {
                                        int id = jsonObject.has("id") ? jsonObject.getInt("id") : -1;
                                        int connectorId = jsonObject.has("connectorId") ? jsonObject.getInt("connectorId") : -1;
                                        ChargingProfilePurposeType chargingProfilePurposeType = jsonObject.has("chargingProfilePurpose") ?
                                                ChargingProfilePurposeType.valueOf(jsonObject.getString("chargingProfilePurpose")) : null;
                                        int stackLevel = jsonObject.has("stackLevel") ? jsonObject.getInt("stackLevel") : -1;

                                        //response
                                        boolean result = onClearChargingProfile(connectorId, id);

                                        ClearChargingProfileConfirmation clearChargingProfileConfirmation =
                                                new ClearChargingProfileConfirmation(result ? ClearChargingProfileStatus.Accepted : ClearChargingProfileStatus.Unknown);
                                        onResultSend(actionName, message.getId(), clearChargingProfileConfirmation);

                                    } catch (Exception e) {
                                        logger.error("ClearChargingProfile error : {}", e.getMessage());
                                    }
                                } else if (Objects.equals("UnlockConnector", actionName)) {
                                    UnlockConnectorConfirmation unlockConnectorConfirmation = new UnlockConnectorConfirmation(UnlockStatus.NotSupported);
                                    onResultSend(actionName, message.getId(), unlockConnectorConfirmation);
                                    //Unlock Connector 지원할 경우 (2024.12.19)
    //                                uiSeq = ((MainActivity)MainActivity.mContext).getClassUiProcess(getChannel()).getUiSeq();
    //                                if (uiSeq == UiSeq.CHARGING) {
    //                                    unlockConnectorConfirmation.setStatus(UnlockStatus.Unlocked);
    //                                    ////
    //                                    ((MainActivity) MainActivity.mContext).getClassUiProcess(getChannel()).onRemoteTransactionStop(Reason.Remote);
    //
    //                                } else {
    //                                    onResultSend(actionName, message.getId(), unlockConnectorConfirmation);
    //                                }

                                } else if (Objects.equals("GetLocalListVersion", actionName)) {
                                    //Local List
                                    int localListVersion, listVersion;
                                    File file = new File(GlobalVariables.getRootPath() + File.separator + "localAuthorizationList");
                                    if (!file.exists()) {
                                        listVersion = 0;
                                    } else {
                                        JSONObject jsonLocalAuthorizationList = new JSONObject(fileManagement.getStringFromFile(GlobalVariables.getRootPath() + File.separator + "localAuthorizationList"));
                                        localListVersion = jsonLocalAuthorizationList.has("listVersion") ? jsonLocalAuthorizationList.getInt("listVersion") : 0;
                                        listVersion = !GlobalVariables.isLocalAuthListEnabled() ? 0 : localListVersion;
                                    }
                                    GetLocalListVersionConfirmation getLocalListVersionConfirmation = new GetLocalListVersionConfirmation(listVersion);
                                    onResultSend(actionName, message.getId(), getLocalListVersionConfirmation);
                                } else if (Objects.equals("UpdateFirmware", actionName)) {
                                    String location = jsonObject.has("location") ? jsonObject.getString("location") : "";
                                    int retries = jsonObject.has("retries") ? jsonObject.getInt("retries") : 1;
                                    //response 즉시 send
                                    UpdateFirmwareConfirmation updateFirmwareConfirmation = new UpdateFirmwareConfirmation();
                                    onResultSend(actionName, message.getId(), updateFirmwareConfirmation);

                                    // 1. firmware status : Downloading
                                    chargerConfiguration = ((MainActivity) MainActivity.mContext).getChargerConfiguration();
                                    FirmwareStatusNotificationRequest firmwareStatusNotificationRequest = new FirmwareStatusNotificationRequest(FirmwareStatus.Downloading);
                                    chargerConfiguration.setFirmwareStatus(FirmwareStatus.Downloading);
                                    onSend(100, firmwareStatusNotificationRequest.getActionName(), firmwareStatusNotificationRequest);

                                    // update firmware 다운 전에 GlobalVariables.ChargerOperation[] = false ==> Unavailable
                                    for (int i = 0; i < GlobalVariables.maxPlugCount; i++)
                                        GlobalVariables.ChargerOperation[i] = false;
                                    onChargerOperateSave();
                                    // Status Notification
                                    processHandler.sendMessage(onMakeHandlerMessage(
                                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION_BOOT,
                                            100,
                                            0,
                                            null,
                                            null,
                                            null,
                                            false));
                                    /** Https */
                                    HTTPHelper httpHelper = new HTTPHelper(location);
                                    httpHelper.download().subscribe(new SingleObserver<String>() {
                                        @Override
                                        public void onSubscribe(Disposable d) {
                                            // 구독 시작 시 처리 (필요 시)
                                        }
                                        @Override
                                        public void onSuccess(String result) {
                                            if ("success".equals(result)) {
                                                logger.debug("HTTPHelper : 파일 다운 로드 성공");
                                            } else if ("ConnectFail".equals(result)) {
                                                logger.debug("HTTPHelper : 서버 연결 실패");
                                            } else if ("DownloadFail".equals(result)) {
                                                logger.error("HTTPHelper : 파일 다운 로드 실패");
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            // 예외 처리: 네트 워크 오류 등
                                            logger.error("HTTPHelper : 다운 로드 중 에러 발생 {}", e.getMessage());
                                        }
                                    });

                                } else if (Objects.equals("GetDiagnostics", actionName)) {
                                    String location = jsonObject.has("location") ? jsonObject.getString("location") : "";
                                    int retries = jsonObject.has("retries") ? jsonObject.getInt("retries") : -1;
                                    int retryInterval = jsonObject.has("retryInterval") ? jsonObject.getInt("retryInterval") : -1;
                                    String startTime = jsonObject.has("startTime") ? jsonObject.getString("startTime") : null;
                                    String stopTime = jsonObject.has("stopTime") ? jsonObject.getString("stopTime") : null;
                                    // diagnostics file name return : response
                                    GetDiagnosticsConfirmation getDiagnosticsConfirmation = new GetDiagnosticsConfirmation("diagnostics");
                                    onResultSend(actionName, message.getId(), getDiagnosticsConfirmation);

                                    // DiagnosticsStatus uploading
                                    chargerConfiguration.setDiagnosticsStatus(DiagnosticsStatus.Uploading);
                                    DiagnosticsStatusNotificationRequest diagnosticsStatusNotificationRequest = new DiagnosticsStatusNotificationRequest(DiagnosticsStatus.Uploading);
                                    onSend(100, diagnosticsStatusNotificationRequest.getActionName(), diagnosticsStatusNotificationRequest);

                                    //Diagnostics file make & Sftp 연결
                                    boolean fileMakeCheck = onDiagnosticsFileMake(startTime, stopTime, location);
                                } else if (Objects.equals("ReserveNow", actionName)) {
                                    ReservationStatus reservationStatus;
                                    int resConnectorId = jsonObject.has("connectorId") ? jsonObject.getInt("connectorId") : 0;
                                    String resExpiryDate = jsonObject.has("expiryDate") ? jsonObject.getString("expiryDate") : "";
                                    String resIdTag = jsonObject.has("idTag") ? jsonObject.getString("idTag") : "";
                                    String resParentIdTag = jsonObject.has("parentIdTag") ? jsonObject.getString("parentIdTag") : "";
                                    String resReservationId = jsonObject.has("reservationId") ? jsonObject.getString("reservationId") : "";
                                    chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(resConnectorId - 1);
                                    boolean faultedCase = false, occupiedCase = false, unavailableCase = false;
                                    if (GlobalVariables.isReserveConnectorZeroSupported() && resConnectorId == 0) {
                                        RxData rxData0 = ((MainActivity) MainActivity.mContext).getControlBoard().getRxData(0);
                                        RxData rxData1 = ((MainActivity) MainActivity.mContext).getControlBoard().getRxData(1);
                                        faultedCase = rxData0.isCsFault() || rxData1.isCsFault();
                                        occupiedCase = ((MainActivity) MainActivity.mContext).getChargingCurrentData(0).getChargePointStatus() == ChargePointStatus.Available
                                                || ((MainActivity) MainActivity.mContext).getChargingCurrentData(1).getChargePointStatus() == ChargePointStatus.Available;
                                        unavailableCase = GlobalVariables.ChargerOperation[1] || GlobalVariables.ChargerOperation[2];
                                    } else if (resConnectorId > 0) {
                                        RxData rxData = ((MainActivity) MainActivity.mContext).getControlBoard().getRxData(resConnectorId - 1);
                                        faultedCase = rxData.isCsFault() || ((MainActivity) MainActivity.mContext).getClassUiProcess(resConnectorId-1).getUiSeq() != UiSeq.INIT;
                                        occupiedCase = chargingCurrentData.getChargePointStatus() == ChargePointStatus.Available
                                                && chargingCurrentData.getReservedStatus() == ChargePointStatus.Available;
                                        unavailableCase = GlobalVariables.ChargerOperation[resConnectorId];
                                    }

                                    //configuration key SupportedFeatureProfiles check
                                    boolean reserveSupported = onSupportedFeatureProfiles("Reservation") ;

                                    reservationStatus = (!reserveSupported ? ReservationStatus.Rejected : faultedCase ? ReservationStatus.Faulted :
                                            !occupiedCase ? ReservationStatus.Occupied : !unavailableCase ? ReservationStatus.Unavailable :
                                                    ReservationStatus.Accepted);
                                    //reserve now response
                                    ReserveNowConfirmation reserveNowConfirmation = new ReserveNowConfirmation(reservationStatus);
                                    onResultSend(actionName, message.getId(), reserveNowConfirmation);

                                    if (Objects.equals(ReservationStatus.Accepted, reservationStatus)) {
                                        chargingCurrentData.setResConnectorId(resConnectorId);
                                        chargingCurrentData.setResExpiryDate(resExpiryDate);
                                        chargingCurrentData.setResIdTag(resIdTag);
                                        chargingCurrentData.setResParentIdTag(resParentIdTag);
                                        chargingCurrentData.setResReservationId(resReservationId);
    //                                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Reserved);
                                        chargingCurrentData.setReservedStatus(ChargePointStatus.Reserved);

                                    }

                                    // status notification : reserved
                                    processHandler.sendMessage(onMakeHandlerMessage(
                                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                                            chargingCurrentData.getResConnectorId(),
                                            100,
                                            null,
                                            null,
                                            null,
                                            false));
                                } else if (Objects.equals("CancelReservation", actionName)) {
                                    String resReservationId = jsonObject.has("reservationId") ? jsonObject.getString("reservationId") : "";

                                    int resConnectorId = onFindConnectorId(resReservationId, false);
                                    CancelReservationStatus cancelReservationStatus = resConnectorId == 0 ? CancelReservationStatus.Rejected : CancelReservationStatus.Accepted;
                                    if (cancelReservationStatus == CancelReservationStatus.Accepted) {
                                        int chk = onFindConnectorId(resReservationId, true);
                                    }
                                    //cancelReservationConfirmation response
                                    CancelReservationConfirmation cancelReservationConfirmation = new CancelReservationConfirmation(cancelReservationStatus);
                                    onResultSend(actionName, message.getId(), cancelReservationConfirmation);
                                    //  CancelReservationStatus.Accepted 인 경우에 statusNotification change : available
                                    if (cancelReservationStatus == CancelReservationStatus.Accepted) {
                                        processHandler.sendMessage(onMakeHandlerMessage(
                                                GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                                                resConnectorId,
                                                0,
                                                null,
                                                null,
                                                null,
                                                false));
                                    }
                                } else if (Objects.equals("SetChargingProfile", actionName)) {
                                    try {
                                        int connectorId = jsonObject.has("connectorId") ? jsonObject.getInt("connectorId") : -1;
                                        JSONObject chargingProfiles = jsonObject.getJSONObject("csChargingProfiles");
                                        int chargingProfileId = chargingProfiles.getInt("chargingProfileId");
                                        int transactionId = chargingProfiles.has("transactionId") ? chargingProfiles.getInt("transactionId") : -1;
                                        int stackLevel = chargingProfiles.has("stackLevel") ? chargingProfiles.getInt("stackLevel") : -1;
                                        ChargingProfilePurposeType chargingProfilePurpose = ChargingProfilePurposeType.valueOf(chargingProfiles.getString("chargingProfilePurpose"));
                                        ChargingProfileKindType chargingProfileKind = ChargingProfileKindType.valueOf(chargingProfiles.getString("chargingProfileKind"));
                                        if (chargingProfiles.has("recurrencyKind")) {
                                            RecurrencyKindType recurrencyKind = RecurrencyKindType.valueOf(chargingProfiles.getString("recurrencyKind"));
                                        }

                                        String validFrom = chargingProfiles.has("validFrom") ? chargingProfiles.getString("validFrom") : "";
                                        String validTo = chargingProfiles.has("validFrom") ? chargingProfiles.getString("validTo") : "";

                                        JSONObject chargingSchedule = chargingProfiles.getJSONObject("chargingSchedule");
                                        int duration = chargingSchedule.has("duration") ? chargingSchedule.getInt("duration") : -1;
                                        String startSchedule = chargingSchedule.has("startSchedule") ? chargingSchedule.getString("startSchedule") : "";
                                        ChargingRateUnitType chargingRateUnit = ChargingRateUnitType.valueOf(chargingSchedule.getString("chargingRateUnit"));
                                        long minChargingRate = chargingSchedule.has("minChargingRate") ? chargingSchedule.getLong("minChargingRate") : -1;

                                        if (!Objects.equals(chargingProfilePurpose, ChargingProfilePurposeType.ChargePointMaxProfile)) {
                                            chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(connectorId - 1);
                                        }

                                        boolean result = true;
                                        if (chargingProfilePurpose == ChargingProfilePurposeType.TxProfile) {
                                            result = chargingCurrentData.getChargePointStatus() == ChargePointStatus.Charging || chargingCurrentData.getTransactionId() == transactionId;
                                        }

                                        //SetChargingProfile response ==> ChargingProfileStatus.Accepted
                                        SetChargingProfileConfirmation setChargingProfileConfirmation =
                                                new SetChargingProfileConfirmation(result ? ChargingProfileStatus.Accepted : ChargingProfileStatus.Rejected);
                                        onResultSend(actionName, message.getId(), setChargingProfileConfirmation);
                                        if (result) onUpdateChargingProfile(jsonObject);
                                    } catch (Exception e) {
                                        logger.error(" SetChargingProfile error :  {}", e.getMessage());
                                    }

                                } else if (Objects.equals("GetCompositeSchedule", actionName)) {
                                    int connectorId = jsonObject.has("connectorId") ? jsonObject.getInt("connectorId") : -1;
                                    int duration = jsonObject.has("duration") ? jsonObject.getInt("duration") : -1;
                                    ChargingRateUnitType chargingRateUnit = jsonObject.has("chargingRateUnit") ? ChargingRateUnitType.valueOf(jsonObject.getString("chargingRateUnit")) : null;
                                    // GetCompositeSchedule response
                                    GetCompositeScheduleConfirmation getCompositeScheduleConfirmation = new GetCompositeScheduleConfirmation(GetCompositeScheduleStatus.Accepted);
                                    getCompositeScheduleConfirmation.setConnectorId(connectorId);
                                    getCompositeScheduleConfirmation.setScheduleStart(zonedDateTimeConvert.doZonedDateTimeToDatetime1());
                                    JSONArray jsonArray = onChargingSchedulePeriodCurrentData(connectorId - 1, duration);

                                    Gson gson = new Gson();
                                    ChargingSchedulePeriod[] chargingSchedulePeriods = gson.fromJson(jsonArray.toString(), ChargingSchedulePeriod[].class);
                                    ChargingSchedule chargingSchedule = new ChargingSchedule(ChargingRateUnitType.W, chargingSchedulePeriods);
                                    chargingSchedule.setDuration(duration);

                                    chargingSchedule.setStartSchedule(zonedDateTimeConvert.doZonedDateTimeToDatetime1());
                                    getCompositeScheduleConfirmation.setChargingSchedule(chargingSchedule);

                                    onResultSend(actionName, message.getId(), getCompositeScheduleConfirmation);
                                } else if (Objects.equals("extendedTriggerMessage", actionName)) {
                                    TriggerMessageStatus requestedMessage = jsonObject.has("requestedMessage") ? TriggerMessageStatus.valueOf(jsonObject.getString("requestedMessage")) : null;
                                    int connectorId = jsonObject.has("connectorId") ? jsonObject.getInt("connectorId") : -1;
                                    ExtendedTriggerMessageConfirmation extendedTriggerMessageConfirmation = new ExtendedTriggerMessageConfirmation(TriggerMessageStatus.Accepted);
                                    onResultSend(actionName, message.getId(), extendedTriggerMessageConfirmation);
                                } else if (Objects.equals("InstallCertificate", actionName)) {
                                    boolean validPeriod = false, result = false;
                                    CertificateUse certificateType = jsonObject.has("certificateType") ? CertificateUse.valueOf(jsonObject.getString("certificateType")) : null;
                                    String certificate = jsonObject.has("certificate") ? jsonObject.getString("certificate") : "";
                                    // 인증서 유효 기간 check
                                    validPeriod = getCertificateValidity(certificate);
                                    if (validPeriod) {
                                        result = fileManagement.stringToFileSave(GlobalVariables.getRootPath(),
                                                certificateType == CertificateUse.CentralSystemRootCertificate ? "cert.pem" : "dongahtest.p-e.kr.crt", certificate, false);
                                    }

                                    // 인증서 저장을 한다.
                                    InstallCertificateConfirmation installCertificateConfirmation =
                                            new InstallCertificateConfirmation(!validPeriod || !result ? CertificateStatus.Rejected : CertificateStatus.Accepted);
                                    onResultSend(actionName, message.getId(), installCertificateConfirmation);

                                } else if (Objects.equals("GetInstalledCertificateIds", actionName)) {
                                    CertificateUse certificateType = jsonObject.has("certificateType") ? CertificateUse.valueOf(jsonObject.getString("certificateType")) : null;
                                    JSONArray jsonArray = getCertification(certificateType);
                                    Gson gson = new Gson();
                                    GetInstalledCertificateIdsConfirmation getInstalledCertificateIdsConfirmation =
                                            new GetInstalledCertificateIdsConfirmation(jsonArray != null ? GetInstalledCertificateStatus.Accepted : GetInstalledCertificateStatus.NotFound);
                                    if (jsonArray != null) {
                                        CertificateHashDataType[] certificateHashDataTypes = gson.fromJson(jsonArray.toString(), CertificateHashDataType[].class);
                                        getInstalledCertificateIdsConfirmation.setCertificateHashData(certificateHashDataTypes);
                                    }
                                    onResultSend(actionName, message.getId(), getInstalledCertificateIdsConfirmation);
                                } else if (Objects.equals("DeleteCertificate", actionName)) {
                                    String certificateHashDataString = jsonObject.has("certificateHashData") ? jsonObject.getString("certificateHashData") : null;
                                    Gson gson = new Gson();
                                    CertificateHashDataType certificateHashDataType = gson.fromJson(certificateHashDataString, CertificateHashDataType.class);

                                    // result = 1:CentralSystemRootCertificate   2:ManufacturerRootCertificate
                                    CertificateHashDataType certificateData;
                                    int deleteCheck = -1;
                                    boolean deleteChk = false;
                                    JSONArray jsonArray = getCertification(CertificateUse.CentralSystemRootCertificate);
                                    if (jsonArray == null) {
                                        jsonArray = getCertification(CertificateUse.ManufacturerRootCertificate);
                                        if (jsonArray != null) deleteCheck = 2;
                                    } else {
                                        certificateData = gson.fromJson(certificateHashDataString, CertificateHashDataType.class);
                                        if (Objects.equals(certificateData.getSerialNumber(), certificateHashDataType.getSerialNumber()))
                                            deleteCheck = 1;
                                    }

                                    if (deleteCheck > 0) {
                                        String filename = deleteCheck == 1 ? "cert.pem" : "dongahtest.p-e.kr.crt";
                                        File file = new File(GlobalVariables.getRootPath() + File.separator + filename);
                                        deleteChk = file.delete();
                                    }
                                    DeleteCertificateStatus deleteCertificateStatus = deleteCheck == -1 ? DeleteCertificateStatus.NotFound :
                                            deleteChk ? DeleteCertificateStatus.Accepted : DeleteCertificateStatus.Failed;
                                    DeleteCertificateConfirmation deleteCertificateConfirmation = new DeleteCertificateConfirmation(deleteCertificateStatus);
                                    onResultSend(actionName, message.getId(), deleteCertificateConfirmation);
                                } else if (Objects.equals("GetLog", actionName)) {
                                    LogType logType = jsonObject.has("logType") ? LogType.valueOf(jsonObject.getString("logType")) : null;
                                    int requestId = jsonObject.has("requestId") ? jsonObject.getInt("requestId") : -1;
                                    int retries = jsonObject.has("retries") ? jsonObject.getInt("retries") : -1;
                                    int retryInterval = jsonObject.has("retryInterval") ? jsonObject.getInt("retryInterval") : -1;
                                    jsonObjectData = jsonObject.getJSONObject("log");
                                    String remoteLocation = jsonObjectData.getString("remoteLocation");
                                    String oldestTimestamp = jsonObjectData.has("oldestTimestamp") ? jsonObjectData.getString("oldestTimestamp") : "";
                                    String latestTimestamp = jsonObjectData.has("latestTimestamp") ? jsonObjectData.getString("latestTimestamp") : "";

                                    GetLogConfirmation getLogConfirmation = new GetLogConfirmation(LogStatus.Accepted);
                                    getLogConfirmation.setFilename(logType == LogType.SecurityLog ? "securityLog.dongah" : "diagnostics.dongah");
                                    onResultSend(actionName, message.getId(), getLogConfirmation);
                                    // security log upload
                                    boolean securityLogFileMake = onSecurityLogFileMake(oldestTimestamp, latestTimestamp, remoteLocation);

                                } else if (Objects.equals("SignedUpdateFirmware", actionName)) {
                                    int retries = jsonObject.has("retries") ? jsonObject.getInt("retries") : -1;
                                    int retryInterval = jsonObject.has("retryInterval") ? jsonObject.getInt("retryInterval") : -1;
                                    GlobalVariables.setRequestId(jsonObject.has("requestId") ? jsonObject.getInt("requestId") : -1);
                                    jsonObjectData = jsonObject.getJSONObject("firmware");
                                    String location = jsonObjectData.getString("location");
                                    String[] locations = location.split("@");
                                    location = locations[1];

                                    String retrieveDateTime = jsonObjectData.getString("retrieveDateTime");
                                    String installDateTime = jsonObjectData.getString("installDateTime");
                                    String signingCertificate = jsonObjectData.getString("signingCertificate");
                                    String signature = jsonObjectData.getString("signature");

                                    GlobalVariables.setSignature(signature);
                                    GlobalVariables.setSigningCertificate(signingCertificate);

                                    UpdateFirmwareStatus updateFirmwareStatus = UpdateFirmwareStatus.Accepted;
                                    SignedUpdateFirmwareConfirmation signedUpdateFirmwareConfirmation = new SignedUpdateFirmwareConfirmation(updateFirmwareStatus);
                                    onResultSend(actionName, message.getId(), signedUpdateFirmwareConfirmation);

                                    //SignedFirmwareStatus
                                    SignedFirmwareStatusNotificationRequest signedFirmwareStatusNotificationRequest = new SignedFirmwareStatusNotificationRequest(SignedFirmwareStatus.Downloading);
                                    chargerConfiguration.setSignedFirmwareStatus(SignedFirmwareStatus.Downloading);
                                    onSend(100, signedFirmwareStatusNotificationRequest.getActionName(), signedFirmwareStatusNotificationRequest);
                                    // update firmware 다운 전에 GlobalVariables.ChargerOperation[] = false ==> Unavailable
                                    for (int i = 0; i < GlobalVariables.maxPlugCount; i++)
                                        GlobalVariables.ChargerOperation[i] = false;
                                    onChargerOperateSave();
                                    // Status Notification
                                    processHandler.sendMessage(onMakeHandlerMessage(
                                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION_BOOT,
                                            100,
                                            0,
                                            null,
                                            null,
                                            null,
                                            false));

                                    /** SFTP */
    //                                SftpRxJava sftpRxJava = new SftpRxJava(FileTransType.SIGNED_FIRMWARE, location);
    //                                sftpRxJava.downloadTask();

                                    /** Ftp */
                                    FtpRxJava ftpRxJava = new FtpRxJava(FileTransType.SIGNED_FIRMWARE, location);
                                    ftpRxJava.downloadTask();
                                }
                                break;
                        }

                } catch (Exception e) {
                    logger.error("receive error : {}", e.getMessage());
                    // central system response 받아 hashMapUid 삭제 */
                    if (!TextUtils.isEmpty(actionName)) hashMapUuid.remove(message.getId());
                }
            }
        });
    }

    private boolean setLocalAuthorizationList(int version , JSONObject jsonObject) {
        boolean result = false;
        try {
            String idTag = jsonObject.getString("idTag");
            JSONObject idTagInfo = new JSONObject(jsonObject.getString("idTagInfo"));

            //저장된 리스트
            String orgList = fileManagement.getStringFromFile(GlobalVariables.getRootPath() + File.separator + "localAuthorizationList");
            JSONObject orgJsonList = new JSONObject(orgList);
            int listVersion = orgJsonList.getInt("listVersion");
            JSONArray orgLocalAuthorizationList = orgJsonList.getJSONArray("localAuthorizationList");
            JSONArray jArray = new JSONArray();
            //not found
            boolean emptyChk = false;

            for (int i = 0; i < orgLocalAuthorizationList.length(); i++) {
                JSONObject orgDetail = orgLocalAuthorizationList.getJSONObject(i);
                if (Objects.equals(idTag, orgDetail.getString("idTag"))) {
                    JSONObject obj = new JSONObject();
                    obj.put("idTag", idTag);
                    JSONObject objIdTagInfo = new JSONObject();
                    objIdTagInfo.put("expiryDate", idTagInfo.getString("expiryDate"));
                    objIdTagInfo.put("parentIdTag", idTagInfo.getString("parentIdTag"));
                    objIdTagInfo.put("status", idTagInfo.getString("status"));
                    obj.put("idTagInfo", objIdTagInfo);
                    jArray.put(obj);
                    result = emptyChk = true;
                } else {
                    jArray.put(orgDetail);
                }
            }
            if (!emptyChk) {
                JSONObject obj = new JSONObject();
                obj.put("idTag", idTag);
                JSONObject objIdTagInfo = new JSONObject();
                objIdTagInfo.put("expiryDate", idTagInfo.getString("expiryDate"));
                objIdTagInfo.put("parentIdTag", idTagInfo.getString("parentIdTag"));
                objIdTagInfo.put("status", idTagInfo.getString("status"));
                obj.put("idTagInfo", objIdTagInfo);
                jArray.put(obj);
            }
            JSONObject sObject = new JSONObject();
            sObject.put("listVersion", version);
            sObject.put("localAuthorizationList", jArray);
            sObject.put("updateType", "Differential");
            result = fileManagement.stringToFileSave(GlobalVariables.getRootPath(), "localAuthorizationList", sObject.toString(), false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public String getConfigurationValue(String key) {
        String result = "none";
        try {
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

    public boolean setConfigurationValue(String key, String value) {
        boolean result = false;
        try {
            String configurationString = fileManagement.getStringFromFile(GlobalVariables.getRootPath() + File.separator + "ConfigurationKey");

            JSONArray jsonArrayContent = new JSONObject(configurationString).getJSONArray("values");
            JSONArray jsonArray = new JSONArray();
            boolean notFond = true;
            for (int i = 0; i < jsonArrayContent.length(); i++) {
                JSONObject contDetail = jsonArrayContent.getJSONObject(i);
                if (Objects.equals(contDetail.get("key"), key)) {
                    if (contDetail.getBoolean("readonly")) {
                        notFond = true;
                    } else {
                        JSONObject obj = new JSONObject();
                        obj.put("key", key);
                        obj.put("readonly", contDetail.getBoolean("readonly"));
                        obj.put("value", value);
                        jsonArray.put(obj);
                        notFond = false;
                    }
                } else {
                    jsonArray.put(contDetail);
                }
            }
            GlobalVariables.setNotSupportedKey(notFond);
            JSONObject sObject = new JSONObject();
            sObject.put("values", jsonArray);
            result = fileManagement.stringToFileSave(GlobalVariables.getRootPath(), "ConfigurationKey", sObject.toString(), false);
        } catch (Exception e) {
            logger.error("SetConfigurationValue {}", e.getMessage());
        }
        return result;
    }

    // localAuthorizationList ==> 사용자 인증
    public boolean getLocalAuthorizationListFind(String idTag) {
        boolean result = false;
        try {
            String authorizationList = GlobalVariables.getRootPath() + File.separator + "localAuthorizationList";
            File targetFile = new File(authorizationList);
            if (targetFile.exists()) {
                JSONObject jsonObject = new JSONObject(fileManagement.getStringFromFile(authorizationList));
                JSONArray jsonArray = jsonObject.getJSONArray("localAuthorizationList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject contDetail = jsonArray.getJSONObject(i);
                    if (Objects.equals(idTag, contDetail.getString("idTag"))) {
                        result = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(" getLocalAuthorizationListFind error : {}", e.getMessage());
        }
        return result;
    }

    // idTag, parentIdTag return
    public String[] getLocalAuthorizationListStrings(String idTag) {
        boolean idTagCheck = false;
        String[] result = new String[2];
        try {
            String authorizationList = GlobalVariables.getRootPath() + File.separator + "localAuthorizationList";
            File targetFile = new File(authorizationList);
            if (targetFile.exists()) {
                JSONObject jsonObject = new JSONObject(fileManagement.getStringFromFile(authorizationList));
                JSONArray jsonArray = jsonObject.getJSONArray("localAuthorizationList");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject contDetail = jsonArray.getJSONObject(i);
                    if (Objects.equals(idTag, contDetail.getString("idTag"))) {
                        result[0] = contDetail.getString("idTag");
                        JSONObject idTagInfo = new JSONObject(contDetail.getString("idTagInfo"));
                        result[1] = idTagInfo.getString("parentIdTag");
                        idTagCheck = true;
                        break;
                    }
                }
            }
            //idTag 값이 없는 경우
            if (!idTagCheck) {
                result[0] = "notFound";
                result[1] = "";
            }
        } catch (Exception e) {
            logger.error(" getLocalAuthorizationListStrings error : {}", e.getMessage());
        }
        return result;
    }


    //ChargerOperate
    public void onChargerOperateSave() {
        try {
            boolean chk;
            String rootPath = Environment.getExternalStorageDirectory().toString() + File.separator + "Download";
            String fileName = "ChargerOperate";
            File file = new File(rootPath + File.separator + fileName);
            if (file.exists()) chk = file.delete();
            for (int i = 0; i < GlobalVariables.maxPlugCount; i++) {
                String statusContent = String.valueOf(GlobalVariables.ChargerOperation[i]);
                fileManagement.stringToFileSave(rootPath, fileName, statusContent, true);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean onDiagnosticsFileMake(String startTime, String stopTime, String location) {
        boolean result = false;
        try {
            File diagnosticsContext = new File(GlobalVariables.getRootPath() + File.separator + "diagnostics.dongah");
            FileReader fileReader = new FileReader(diagnosticsContext);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int count = 0;
            String line;
            JSONArray resultJsonArray = new JSONArray();
            while ((line = bufferedReader.readLine()) != null) {
                JSONObject object = new JSONObject(line);
                JSONArray array = object.getJSONArray("diagnostics");
                JSONObject contDetail = array.getJSONObject(0);
                String startDate = contDetail.getString("startTime");
                String energy = contDetail.getString("Energy.Active.Export.Register");
                if (startTime.compareTo(startDate) < 0 && stopTime.compareTo(startDate) > 0) {
                    //json array make
                    resultJsonArray.put(contDetail);
                    count++;
                }
            }
            if (count > 0) {
                //save
                File resultFile = new File(GlobalVariables.getRootPath() + File.separator + "diagnostics");
                if (resultFile.exists()) {
                    boolean check = resultFile.delete();
                }
                fileManagement.stringToFileSave(GlobalVariables.getRootPath(), "diagnostics", resultJsonArray.toString(), false);
                /** SFTP 연결 */
//                SftpRxJava sftpRxJava = new SftpRxJava(FileTransType.DIAGNOSTICS, location);
//                sftpRxJava.downloadTask();

                /** FTP */
                FtpRxJava ftpRxJava = new FtpRxJava(FileTransType.DIAGNOSTICS, location);
                ftpRxJava.downloadTask();;

                result = true;
            } else {
                chargerConfiguration.setDiagnosticsStatus(DiagnosticsStatus.UploadFailed);
                DiagnosticsStatusNotificationRequest diagnosticsStatusNotificationRequest = new DiagnosticsStatusNotificationRequest(DiagnosticsStatus.UploadFailed);
                onSend(100, diagnosticsStatusNotificationRequest.getActionName(), diagnosticsStatusNotificationRequest);
            }
        } catch (Exception e) {
            logger.error(" diagnosticsFileMake {}", e.getMessage());
        }
        return result;
    }

    /**
     * Security log
     */
    public boolean onSecurityLogFileMake(String oldestTimestamp, String latestTimestamp, String location) {
        boolean result = false;
        try {
            File securityLogContext = new File(GlobalVariables.getRootPath() + File.separator + "securityLog.dongah");
            FileReader fileReader = new FileReader(securityLogContext);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int count = 0;
            String line;
            JSONArray resultJsonArray = new JSONArray();
            while ((line = bufferedReader.readLine()) != null) {
                JSONObject object = new JSONObject(line);
                JSONArray array = object.getJSONArray("SecurityLogs");
                JSONObject contDetail = array.getJSONObject(0);
                String startDate = contDetail.getString("startTime");
                String SecurityLog = contDetail.getString("securityLog");
                if (oldestTimestamp.compareTo(startDate) < 0 && latestTimestamp.compareTo(startDate) > 0) {
                    //json array make
                    resultJsonArray.put(contDetail);
                    count++;
                }
            }
            if (count > 0) {
                //save
                File resultFile = new File(GlobalVariables.getRootPath() + File.separator + "securityLogs");
                if (resultFile.exists()) {
                    boolean check = resultFile.delete();
                }
                fileManagement.stringToFileSave(GlobalVariables.getRootPath(), "securityLogs", resultJsonArray.toString(), false);

                //SFTP 연결
//                SftpRxJava sftpRxJava = new SftpRxJava(FileTransType.SECURITY, location);
//                sftpRxJava.downloadTask();

                /** FTP */
                FtpRxJava ftpRxJava = new FtpRxJava(FileTransType.SECURITY, location);
                ftpRxJava.downloadTask();;

                result = true;
            }
        } catch (Exception e) {
            logger.error(" onSecurityLogFileMake {}", e.getMessage());
        }
        return result;
    }


    private int onFindConnectorId(String reservationId, boolean upDate) {
        int result = 0;
        try {
            for (int i = 0; i < 2; i++) {
                ChargingCurrentData chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(i);
                if (Objects.equals(reservationId, chargingCurrentData.getResReservationId())) {
                    result = chargingCurrentData.getResConnectorId();
                    if (upDate) {
                        chargingCurrentData.setResConnectorId(0);
                        chargingCurrentData.setResExpiryDate("");
                        chargingCurrentData.setResIdTag("");
                        chargingCurrentData.setResParentIdTag("");
                        chargingCurrentData.setResReservationId("");
                        chargingCurrentData.setChargePointStatus(ChargePointStatus.Available);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(" {}", e.getMessage());
        }
        return result;
    }

    public String getChargingSchedule() {
        try {
            JSONObject chargingSchedule;
            String csChargingProfilesList = GlobalVariables.getRootPath() + File.separator + "csChargingProfiles";
            File targetFile = new File(csChargingProfilesList);
            if (targetFile.exists()) {
                JSONObject jsonObject = new JSONObject(fileManagement.getStringFromFile(csChargingProfilesList));

                JSONObject chargingProfiles = jsonObject.getJSONObject("csChargingProfiles");

                chargingSchedule = chargingProfiles.getJSONObject("chargingSchedule");

                return chargingSchedule.toString();
            }
        } catch (Exception e) {
            logger.error(" getChargingSchedule error : {}", e.getMessage());
        }
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onUpdateChargingProfile(JSONObject scProfile) {
        try {
            int connectorId = scProfile.has("connectorId") ? scProfile.getInt("connectorId") : -1;
            String idTag = scProfile.has("idTag") ? scProfile.getString("idTag") : "";
            JSONObject chargingProfiles = scProfile.has("csChargingProfiles") ? scProfile.getJSONObject("csChargingProfiles")
                    : scProfile.has("chargingProfile") ? scProfile.getJSONObject("chargingProfile") : null;
            assert chargingProfiles != null;
            int profileId = chargingProfiles.getInt("chargingProfileId");
            int level = chargingProfiles.has("stackLevel") ? chargingProfiles.getInt("stackLevel") : -1;
            // ChargePointMaxProfile, TxDefaultProfile, TxProfile
            ChargingProfilePurposeType chargingProfilePurpose = ChargingProfilePurposeType.valueOf(chargingProfiles.getString("chargingProfilePurpose"));
            ChargingProfileKindType chargingProfileKind = ChargingProfileKindType.valueOf(chargingProfiles.getString("chargingProfileKind"));

            JSONObject chargingSchedule = chargingProfiles.getJSONObject("chargingSchedule");
            int duration = chargingSchedule.has("duration") ? chargingSchedule.getInt("duration") : 0;

            boolean found = false;
            JSONArray jArray = new JSONArray();

            File file = new File(GlobalVariables.getRootPath() + File.separator + "csChargingProfiles");
            if (!file.exists()) {
                boolean check = file.createNewFile();
                //chargingProfiles
                jArray.put(scProfile);
                JSONObject sObject = new JSONObject();
                sObject.put("SetChargingProfile", jArray);

                boolean result = fileManagement.stringToFileSave(GlobalVariables.getRootPath(), "csChargingProfiles", sObject.toString(), false);
            } else {
                String csFileString = fileManagement.getStringFromFile(GlobalVariables.getRootPath() + File.separator + "csChargingProfiles");
                JSONObject orgChargingProfiles = new JSONObject(csFileString);
                JSONArray orgSetChargingProfile = orgChargingProfiles.getJSONArray("SetChargingProfile");


                for (int i = 0; i < orgSetChargingProfile.length(); i++) {
                    JSONObject jsonobject = orgSetChargingProfile.getJSONObject(i);
                    int readConnectorId = jsonobject.getInt("connectorId");
                    JSONObject readChargingProfiles = jsonobject.getJSONObject("csChargingProfiles");
                    ChargingProfilePurposeType orgProfilePurpose = ChargingProfilePurposeType.valueOf(readChargingProfiles.getString("chargingProfilePurpose"));
                    int stackLevel = readChargingProfiles.getInt("stackLevel");
                    if (Objects.equals(chargingProfilePurpose, orgProfilePurpose) && Objects.equals(level, stackLevel)) {
                        jArray.put(scProfile);
                        found = true;
                    } else {
                        jArray.put(jsonobject);
                    }
                }
                // for문에서 없는 profile
                if (!found) {
                    jArray.put(scProfile);
                }

                JSONObject sObject = new JSONObject();
                sObject.put("SetChargingProfile", jArray);
                boolean result = fileManagement.stringToFileSave(GlobalVariables.getRootPath(), "csChargingProfiles", sObject.toString(), false);
                if (!Objects.equals(chargingProfilePurpose, ChargingProfilePurposeType.ChargePointMaxProfile)) {
                    chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(connectorId - 1);
                }
            }
            if (chargingCurrentData.isRemoteStartSmartCharging() && duration > 0) {
                chargingCurrentData.remoteSmartChargingJsonArray = onChargingSchedulePeriodCurrentData(connectorId - 1, duration);
            }
        } catch (Exception e) {
            logger.error(" onUpdateChargingProfile error -  {} ", e.getMessage());
        }
    }


    public boolean onClearChargingProfile(int scConnectorId, int scProfileId) {
        boolean found = false;
        try {
            JSONArray jArray = new JSONArray();
            String csFileString = fileManagement.getStringFromFile(GlobalVariables.getRootPath() + File.separator + "csChargingProfiles");
            JSONObject orgChargingProfiles = new JSONObject(csFileString);
            JSONArray orgChargingProfilesList = orgChargingProfiles.getJSONArray("SetChargingProfile");
            //chargingProfileId 같은면 Update
            for (int i = 0; i < orgChargingProfilesList.length(); i++) {
                JSONObject jsonobject = orgChargingProfilesList.getJSONObject(i);
                int connectorId = jsonobject.getInt("connectorId");
                JSONObject csChargingProfiles = jsonobject.getJSONObject("csChargingProfiles");
                int ProfileId = jsonobject.getInt("chargingProfileId");
                if (Objects.equals(connectorId, scConnectorId) && Objects.equals(ProfileId, scProfileId)) {
                    found = true;
                    continue;
                } else {
                    jArray.put(jsonobject);
                }
            }
            //save
            JSONObject sObject = new JSONObject();
            sObject.put("SetChargingProfile", jArray);
            boolean result = fileManagement.stringToFileSave(GlobalVariables.getRootPath(), "csChargingProfiles", sObject.toString(), false);
        } catch (Exception e) {
            logger.error(" onClearChargingProfile error : {}", e.getMessage());
        }
        return found;

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public JSONArray onChargingSchedulePeriodCurrentData(int sChannel, int sDuration) {
        try {
            String startSchedule;
            int compositeGap = 0;
            Date compositeTime = zonedDateTimeConvert.doStringDateToDate(zonedDateTimeConvert.getStringCurrentTimeZone());
            //
//            compositeTime = zonedDateTimeConvert.doStringDateToDate1("2024-08-25T20:37:05Z");
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            ChargingCurrentData chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(sChannel);
            String csFileString = fileManagement.getStringFromFile(GlobalVariables.getRootPath() + File.separator + "csChargingProfiles");
            JSONObject orgChargingProfiles = new JSONObject(csFileString);

            JSONArray setChargingProfiles = orgChargingProfiles.getJSONArray("SetChargingProfile");
            int chargingSchedulePeriodLength;
            int txProfileCount = 0;
            JSONArray jArray = new JSONArray();
            JSONArray maxProfileArray = new JSONArray();
            JSONArray defaultProfileArray = new JSONArray();
            JSONArray txProfileArray = new JSONArray();
            chargingCurrentData.maxProfileSchedulePeriod = null;
            chargingCurrentData.defaultProfileSchedulePeriod = null;
            chargingCurrentData.txProfileSchedulePeriod = null;

            for (int i = 0; i < setChargingProfiles.length(); i++) {
                JSONObject setChargingProfile = setChargingProfiles.getJSONObject(i);
                int connectorId = setChargingProfile.getInt("connectorId");
                JSONObject csChargingProfile = setChargingProfile.getJSONObject("csChargingProfiles");
                int chargingProfileId = csChargingProfile.getInt("chargingProfileId");
                int stackLevel = csChargingProfile.getInt("stackLevel");

                String chargingProfilePurpose = csChargingProfile.getString("chargingProfilePurpose");
                JSONObject csChargingSchedule = csChargingProfile.getJSONObject("chargingSchedule");
                int duration = csChargingSchedule.getInt("duration");

                JSONArray chargingSchedulePeriod = csChargingSchedule.getJSONArray("chargingSchedulePeriod");
                chargingSchedulePeriodLength = chargingSchedulePeriod.length();

                switch (chargingProfilePurpose) {
                    case "ChargePointMaxProfile":
                        chargingCurrentData.setMaxProfileDuration(duration);
                        chargingCurrentData.maxProfileSchedulePeriod = new String[chargingSchedulePeriodLength];
                        for (int j = 0; j < chargingSchedulePeriod.length(); j++) {
                            JSONObject obj = chargingSchedulePeriod.getJSONObject(j);
                            chargingCurrentData.setMaxProfileLimit(obj.getDouble("limit"));
                            chargingCurrentData.maxProfileSchedulePeriod[j] = obj.toString();
                            maxProfileArray.put(obj);
                        }
                        break;
                    case "TxDefaultProfile":
                        chargingCurrentData.setDefaultProfileDuration(duration);
                        chargingCurrentData.defaultProfileSchedulePeriod = new String[chargingSchedulePeriodLength];

                        for (int j = 0; j < chargingSchedulePeriod.length(); j++) {
                            JSONObject obj = chargingSchedulePeriod.getJSONObject(j);
                            chargingCurrentData.defaultProfileSchedulePeriod[j] = obj.toString();
                            defaultProfileArray.put(obj);
                        }
                        break;
                    case "TxProfile":
                        txProfileCount++;
                        chargingCurrentData.setTxProfileDuration(duration);
                        chargingCurrentData.txProfileSchedulePeriod = new String[chargingSchedulePeriodLength];
                        for (int j = chargingSchedulePeriod.length() - 1; j >= 0; j--) {
                            JSONObject obj = chargingSchedulePeriod.getJSONObject(j);
                            chargingCurrentData.txProfileSchedulePeriod[j] = obj.toString();
                            txProfileArray.put(obj);
                        }
                        startSchedule = csChargingSchedule.getString("startSchedule");
                        long diffTime = (compositeTime.getTime() - zonedDateTimeConvert.doStringDateToDate1(startSchedule).getTime()) / 1000;
                        compositeGap = (int) diffTime % 60;
                        break;
                }
            }

            ///ChargePointMaxProfile
            JSONArray newTxProfileArray = new JSONArray();
            for (int i = txProfileArray.length() - 1; i >= 0; i--) {
                newTxProfileArray.put(txProfileArray.get(i));
            }
            double limitCheck = 0, limit = 0;
            int startTxPeriod = 0, startDefaultPeriod = 0, currentPeriod;
            for (int s = 0; s < newTxProfileArray.length(); s++) {
                JSONObject obj = newTxProfileArray.getJSONObject(s);

                startTxPeriod = obj.getInt("startPeriod");

                if (startTxPeriod > sDuration) break;
                limit = Math.min(obj.getDouble("limit"), chargingCurrentData.getMaxProfileLimit());
                if (limitCheck == 0) {
                    obj.putOpt("startPeriod", startTxPeriod == 0 ? 0 : startTxPeriod - compositeGap);
                    limitCheck = limit;
                    obj.putOpt("limit", limitCheck);
                    jArray.put(obj);
                } else if (limit != limitCheck) {
                    obj.putOpt("startPeriod", startTxPeriod - compositeGap);
                    limitCheck = limit;
                    obj.putOpt("limit", limitCheck);
                    jArray.put(obj);
                }
            }

            if (startTxPeriod < sDuration) {
                currentPeriod = chargingCurrentData.getTxProfileDuration() - compositeGap;
                for (int i = 0; i < defaultProfileArray.length(); i++) {
                    JSONObject obj = defaultProfileArray.getJSONObject(i);
                    startDefaultPeriod = obj.getInt("startPeriod");
                    if (startDefaultPeriod > currentPeriod) {
                        obj.putOpt("startPeriod", currentPeriod);
                        jArray.put(obj);
                    }
                }
            }

            if (startDefaultPeriod < sDuration && startDefaultPeriod != 0) {
                //
                JSONObject obj = new JSONObject();
                obj.put("startPeriod", chargingCurrentData.getDefaultProfileDuration() - compositeGap);
                obj.put("limit", chargingCurrentData.getMaxProfileLimit());
                obj.put("numberPhases", 3);
                jArray.put(obj);
            }
            return jArray;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    private boolean onSupportedFeatureProfiles(String key) {
        boolean result = false;
        try {
            String[] values = getConfigurationValue("SupportedFeatureProfiles").split(",");
            for (String value : values) {
                if (Objects.equals(key, value)) {
                    result = true;
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }


    private JSONArray getCertification(CertificateUse certificateUse) {
        try {
            String filename = certificateUse == CertificateUse.CentralSystemRootCertificate ? "cert.pem" :
                    certificateUse == CertificateUse.ManufacturerRootCertificate ? "dongahtest.p-e.kr.crt" : null;
            File file = new File(GlobalVariables.getRootPath() + File.separator + filename);
            if (file.exists()) {
                X509Certificate certificatePem = loadCertificateFromFile(file);
                CertificateHashDataType certificateHashDataType = new CertificateHashDataType();
                certificateHashDataType.setSerialNumber(certificatePem.getSerialNumber().toString(16).toUpperCase());
                certificateHashDataType.setHashAlgorithm(HashAlgorithm.valueOf(certificatePem.getSigAlgName().substring(0, 6)));
                certificateHashDataType.setIssuerNameHash(getIssuerName(certificatePem));
                certificateHashDataType.setIssuerKeyHash(calculateSHA1(certificatePem.getPublicKey().getEncoded()));
                // Class ==> JsonObject
                Gson gson = new Gson();
                JSONObject obj = new JSONObject(gson.toJson(certificateHashDataType));
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(obj);
                return jsonArray;
            }
        } catch (Exception e) {
            logger.error(" getCertification {} : ", e.getMessage());
        }
        return null;
    }

    private X509Certificate loadCertificateFromFile(File file) throws CertificateException, IOException {
        FileInputStream inputStream = new FileInputStream(file);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
        inputStream.close();
        return certificate;
    }

    private X509Certificate loadCertificate(String pemCert) throws Exception {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(pemCert.getBytes())) {
            return (X509Certificate) factory.generateCertificate(inputStream);
        }
    }


    @NonNull
    private String getCertificateValidity(@NonNull X509Certificate cert) {
        String notBefore = cert.getNotBefore().toString();
        String notAfter = cert.getNotAfter().toString();
        return String.format("Validity Period: \nStart Date: %s\nEnd Date: %s", notBefore, notAfter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean getCertificateValidity(@NonNull String cert) throws CertificateException {
        boolean result = false;
        InputStream inputStream = new ByteArrayInputStream(cert.getBytes());
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
        String notBefore = certificate.getNotBefore().toString();
        String notAfter = certificate.getNotAfter().toString();
        String currentTime = zonedDateTimeConvert.doGetUtcDatetimeAsString();

        // Fri Dec 20 14:02:52 GMT+09:00 2024 ==> 2024-12-20T14:02:52
        final Set<ZoneId> PREFERRED_ZONES = Set.of(ZoneId.of("UTC"));
        final DateTimeFormatter PARSER = new DateTimeFormatterBuilder()
                .appendPattern("EEE MMM dd HH:mm:ss [O][")
                .appendZoneText(TextStyle.SHORT, PREFERRED_ZONES)
                .appendPattern("] yyyy")
                .toFormatter(Locale.ENGLISH);

        ZonedDateTime zdt = ZonedDateTime.parse(notBefore, PARSER);
        notBefore = zonedDateTimeConvert.zonedString(zdt);
        zdt = ZonedDateTime.parse(notAfter, PARSER);
        notAfter = zonedDateTimeConvert.zonedString(zdt);

        if (notBefore.compareTo(currentTime) < 0 && notAfter.compareTo(currentTime) > 0) {
            result = true;
        }
        return result;
    }

    @NonNull
    private String calculateSHA1(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] hash = sha1.digest(data);

        // Convert to hexadecimal string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0'); // Append leading zero
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }


    public String getIssuerName(X509Certificate cert) throws Exception {
        // Get the issuer's DN in DER format
        byte[] issuerDN = cert.getIssuerX500Principal().getEncoded();

        // Hash the DER-encoded DN using SHA-1
        return calculateSHA1(issuerDN);
    }




    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private boolean verifySignature(PublicKey publicKey, byte[] signature, String firmwareFilePath) {
        boolean result = false;
        try {
            File firmwareFile = new File(firmwareFilePath);
            FileInputStream firmwareInputStream = new FileInputStream(firmwareFile);

//            byte[] firmwareData = firmwareInputStream.readAllBytes();   //java 9 이상
            byte[] firmwareData = readFileToByteArray(firmwareFile);
            firmwareInputStream.close();

            // Initialize Signature instance with RSA SSA-PSS
//            Signature sig = Signature.getInstance("RSASSA-PSS");
            Security.removeProvider("BC");
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Signature sig = Signature.getInstance("SHA256withRSA/PSS", "BC"); // BouncyCastle 사용
            PSSParameterSpec pssSpec = new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1);
            sig.setParameter(pssSpec);
            sig.initVerify(publicKey);
            sig.update(firmwareData); // Use original data, not its hash
            result = sig.verify(signature);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    private byte[] readFileToByteArray(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }
        fis.close();
        return bos.toByteArray();
    }


    @Override
    public void onGetFailure(WebSocket webSocket, Throwable t) {
        this.webSocket = webSocket;
        socket.setState(SocketState.RECONNECT_ATTEMPT);
        logger.error(t.toString());
    }

    /**
     * single connector Id
     *
     * @param actionName action name
     * @param request    request
     * @throws OccurenceConstraintException
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSend(String actionName, Request request) throws OccurenceConstraintException {
        if (!request.validate()) {
            logger.error("Can't send request: not validated. Payload {}: ", request);
            throw new OccurenceConstraintException();
        }
        try {
            String id = store(request);
            Object payload = packPayload(request);
            Object call = makeCall(id, actionName, payload);
            String actionNameCompare = null;
            if (call != null) {
                try {
                    this.webSocket.send(call.toString());
                    if (Objects.equals(actionName, "DataTransfer")) {
                        // DataTransfer 종류가 많아 ACTION_NAME 대신 MESSAGE_ID 를 Key 값으로 정의
                        // message_id 별로 parsing 해야 하는 부분이 있음.
                        JSONObject jsonObject = new JSONObject(payload.toString());
                        actionNameCompare = jsonObject.getString("messageId");
                        hashMapUuid.put(id, jsonObject.getString("messageId"));
                        logDataSave.makeLogDate(jsonObject.getString("messageId"), call.toString());
                    } else {
                        actionNameCompare = actionName;
                        hashMapUuid.put(id, actionName);
                        logDataSave.makeLogDate(actionName, call.toString());
                    }
                    // debug event listener register
                    if (socketMessageDebugListener != null) {
                        socketMessageDebugListener.onMessageReceiveDebugEvent(2, call.toString(), actionName);
                    }
                    logger.trace("Send a message : {}", call.toString());
                } catch (Exception e) {
                    //dump data
                    if (actionList.contains(actionNameCompare)) {
                        logDataSaveDump.makeDump(call.toString());
                    }
                    logDataSave.makeLogDate("<<send fail>>" + actionName, call.toString());
                    logger.error("send error  : {} ", e.toString());

                }
            }
        } catch (Exception e) {
            logger.error("onSend error  : {} ", e.toString());
        }
    }

    /**
     * multi connectorId
     *
     * @param connectorId connector id
     * @param actionName  action name
     * @param request     request
     * @throws OccurenceConstraintException
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSend(int connectorId, String actionName, Request request) throws OccurenceConstraintException {
        if (!request.validate()) {
            logger.error("multi Can't send request: not validated. Payload {}: ", request);
            throw new OccurenceConstraintException();
        }
        try {
            String id = store(request);
            Object payload = packPayload(request);
            Object call = makeCall(id, actionName, payload);
            String actionNameCompare = null;
            if (call != null) {
                try {
                    this.webSocket.send(call.toString());
                    SendHashMapObject sendHashMapObject = new SendHashMapObject();
                    sendHashMapObject.setConnectorId(connectorId);
                    if (Objects.equals(actionName, "DataTransfer")) {
                        // DataTransfer 종류가 많아 ACTION_NAME 대신 MESSAGE_ID 를 Key 값으로 정의
                        // message_id 별로 parsing 해야 하는 부분이 있음.
                        JSONObject jsonObject = new JSONObject(payload.toString());
                        actionNameCompare = jsonObject.getString("messageId");
                        sendHashMapObject.setActionName(jsonObject.getString("messageId"));
                        newHashMapUuid.put(id, sendHashMapObject);
                        logDataSave.makeLogDate(jsonObject.getString("messageId"), call.toString());
                    } else {
                        actionNameCompare = actionName;
                        sendHashMapObject.setActionName(actionName);
                        newHashMapUuid.put(id, sendHashMapObject);
                        logDataSave.makeLogDate(actionName, call.toString());
                    }
                    // debug event listener register
                    if (socketMessageDebugListener != null) {
                        socketMessageDebugListener.onMessageReceiveDebugEvent(2, call.toString(), actionName);
                    }
                    logger.trace("Send a message: {}", call.toString());
                } catch (Exception e) {
                    //dump data
                    if (actionList.contains(actionNameCompare)) {
                        logDataSaveDump.makeDump(call.toString());
                    }
                    logDataSave.makeLogDate("<<send fail>>" + actionName, call.toString());
                    logger.error("send error : {} ", e.toString());
                }
            }
        } catch (Exception e) {
            logger.error("onSend error : {} ", e.toString());
        }
    }

    /**
     * Dump data send (미전송 데이터)
     *
     * @param text json string
     */
    public void onSend(String text) {
        try {
            this.webSocket.send(text);
            Message message = parse(text);
            String uuid = message.getId();
            String actionName = message.getAction();
            if (Objects.equals(actionName, "DataTransfer")) {
                JSONObject jsonObject = new JSONObject(message.getPayload().toString());
                actionName = jsonObject.getString("messageId");
                hashMapUuid.put(uuid, actionName);
            } else {
                hashMapUuid.put(uuid, actionName);
            }
            LogDataSave logDataSave = new LogDataSave("log");
            logDataSave.makeLogDate(actionName, text);
            logger.trace(" Send a message : {}", message);
        } catch (Exception e) {
            logger.error(" onSend error : {} ", e.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResultSend(String actionName, String uuid, Confirmation confirmation) throws OccurenceConstraintException {
        if (!confirmation.validate()) {
            logger.error("Can't send request:  not validated. Payload {}: ", confirmation);
            throw new OccurenceConstraintException();
        }
        try {
            Object call = makeCallResult(uuid, actionName, packPayload(confirmation));
            if (call != null) {
                this.webSocket.send(call.toString());
                logDataSave.makeLogDate(actionName, call.toString());
                logger.trace(" Send a message: {}", call);
            }
        } catch (Exception e) {
            logger.error("onResultSend : {}", e.getMessage());
        }
    }

    @Override
    public void onCall(String id, String action, Object payload) {
        logger.trace("Send a message: id : {}, action : {}, payload : {}", id, action, payload.toString());
    }


    public String store(Request request) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        return UUID.randomUUID().toString();
    }

    /**
     * handler --> message send
     *
     * @param messageType message Type
     * @param connectorId connector Id ( 0 : All  1: 1채널  2:2채널)
     * @param delayTime   delay time
     * @param idTag       idTag (idTag, smsTele)
     * @param Uuid        UUID
     * @param result      RESULT (결제성공여부), remote start/stop connectorId check,
     * @return msg
     */
    public android.os.Message onMakeHandlerMessage(int messageType, int connectorId, int delayTime, String idTag, String Uuid, String alarmCode, Boolean result) {
        try {
            android.os.Message msg = new android.os.Message();
            Bundle bundle = new Bundle();
            bundle.putInt("connectorId", connectorId);
            bundle.putInt("delay", delayTime);
            bundle.putString("idTag", idTag);
            bundle.putString("uuid", Uuid);
            bundle.putString("alarmCode", alarmCode);
            bundle.putBoolean("result", result);
            msg.setData(bundle);
            msg.what = messageType;
            return msg;
        } catch (Exception e) {
            logger.error("onMakeHandlerMessage error : {}", e.getMessage());
        }
        return null;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }


    public HashMap<String, Object> getNewHashMapUuid() {
        return newHashMapUuid;
    }

    public void setNewHashMapUuid(String uuid, SendHashMapObject newHashMapUuid) {
        this.newHashMapUuid.put(uuid, newHashMapUuid);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private double onFindUnitPrice(JSONArray jsonArray) {
        try {
            ZonedDateTime now = zonedDateTimeConvert.doGetCurrentTime();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String startAtStr = obj.getString("startAt");
                String endAtArStr = obj.getString("endAt");
                ZonedDateTime startAt = ZonedDateTime.parse(startAtStr, DateTimeFormatter.ISO_DATE_TIME);
                ZonedDateTime endAt = ZonedDateTime.parse(endAtArStr, DateTimeFormatter.ISO_DATE_TIME);

                if ((now.isEqual(startAt) || now.isAfter(startAt)) && (now.isBefore(endAt) || now.isEqual(endAt))) {
                    return obj.getDouble("price");
                }
            }
        } catch (Exception e){
            logger.error(" onFindUnitPrice error : {}", e.getMessage());
        }
        return 0;
    }

}

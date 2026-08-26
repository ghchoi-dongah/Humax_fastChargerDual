package com.dongah.fastcharger.handler;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.basefunction.ChargerConfiguration;
import com.dongah.fastcharger.basefunction.ChargingCurrentData;
import com.dongah.fastcharger.basefunction.ClassUiProcess;
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.basefunction.UiSeq;
import com.dongah.fastcharger.controlboard.ControlBoard;
import com.dongah.fastcharger.controlboard.RxData;
import com.dongah.fastcharger.utils.FileManagement;
import com.dongah.fastcharger.utils.LogDataSave;
import com.dongah.fastcharger.websocket.ocpp.common.OccurenceConstraintException;
import com.dongah.fastcharger.websocket.ocpp.core.AuthorizeRequest;
import com.dongah.fastcharger.websocket.ocpp.core.AvailabilityStatus;
import com.dongah.fastcharger.websocket.ocpp.core.ChangeAvailabilityConfirmation;
import com.dongah.fastcharger.websocket.ocpp.core.ChangeConfigurationConfirmation;
import com.dongah.fastcharger.websocket.ocpp.core.ChargePointErrorCode;
import com.dongah.fastcharger.websocket.ocpp.core.ChargePointStatus;
import com.dongah.fastcharger.websocket.ocpp.core.ClearCacheConfirmation;
import com.dongah.fastcharger.websocket.ocpp.core.ClearCacheStatus;
import com.dongah.fastcharger.websocket.ocpp.core.ConfigurationStatus;
import com.dongah.fastcharger.websocket.ocpp.core.DataTransferStatus;
import com.dongah.fastcharger.websocket.ocpp.core.GetConfigurationConfirmation;
import com.dongah.fastcharger.websocket.ocpp.core.KeyValueType;
import com.dongah.fastcharger.websocket.ocpp.core.MeterValue;
import com.dongah.fastcharger.websocket.ocpp.core.MeterValuesRequest;
import com.dongah.fastcharger.websocket.ocpp.core.RemoteStartStopStatus;
import com.dongah.fastcharger.websocket.ocpp.core.RemoteStartTransactionConfirmation;
import com.dongah.fastcharger.websocket.ocpp.core.RemoteStopTransactionConfirmation;
import com.dongah.fastcharger.websocket.ocpp.core.ResetConfirmation;
import com.dongah.fastcharger.websocket.ocpp.core.ResetStatus;
import com.dongah.fastcharger.websocket.ocpp.core.SampledValue;
import com.dongah.fastcharger.websocket.ocpp.core.StartTransactionRequest;
import com.dongah.fastcharger.websocket.ocpp.core.StatusNotificationRequest;
import com.dongah.fastcharger.websocket.ocpp.core.StopTransactionRequest;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah.AnnounceConfirmation;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah.GetPriceData;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah.GetPriceRequest;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah.PartialCancelData;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah.PartialCancelRequest;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah.PayInfoData;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah.PayInfoRequest;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah.ResultPriceData;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah.ResultPriceRequest;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah.SmsMessageData;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah.SmsMessageRequest;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.humax.ChargerPointInfoData;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.humax.CustomStatusNotiRequest;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.humax.CustomUnitPriceData;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.humax.CustomUnitPriceRequest;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.humax.PaymentCancelData;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.humax.PaymentCancelRequest;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.humax.PaymentRequest;
import com.dongah.fastcharger.websocket.ocpp.datatransfer.humax.PaymentRequestData;
import com.dongah.fastcharger.websocket.ocpp.firmware.FirmwareStatus;
import com.dongah.fastcharger.websocket.ocpp.firmware.FirmwareStatusNotificationRequest;
import com.dongah.fastcharger.websocket.ocpp.localauthlist.SendLocalListConfirmation;
import com.dongah.fastcharger.websocket.ocpp.remotetrigger.TriggerMessageConfirmation;
import com.dongah.fastcharger.websocket.ocpp.remotetrigger.TriggerMessageStatus;
import com.dongah.fastcharger.websocket.ocpp.security.SignedFirmwareStatus;
import com.dongah.fastcharger.websocket.ocpp.security.SignedFirmwareStatusNotificationRequest;
import com.dongah.fastcharger.websocket.ocpp.smartcharging.ClearChargingProfileConfirmation;
import com.dongah.fastcharger.websocket.ocpp.smartcharging.ClearChargingProfileStatus;
import com.dongah.fastcharger.websocket.ocpp.utilities.ZonedDateTimeConvert;
import com.dongah.fastcharger.websocket.socket.Connector;
import com.dongah.fastcharger.websocket.socket.SocketReceiveMessage;
import com.dongah.fastcharger.websocket.socket.SocketState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class ProcessHandler extends Handler {

    private static final Logger logger = LoggerFactory.getLogger(ProcessHandler.class);

    final ZonedDateTimeConvert zonedDateTimeConvert = new ZonedDateTimeConvert();
    Gson gson = new Gson();

    ChargerConfiguration chargerConfiguration;
    ChargingCurrentData chargingCurrentData;
    int connectorId;
    String uuid;
    Object payload, call;
    final String CALL_FORMAT = "[2, \"%s\", \"%s\", %s]";
    Bundle bundle;
    BootNotificationThread bootNotificationThread;
    HeartbeatThread heartbeatThread;
    DiagnosticsThread diagnosticsThread;
    CustomUnitPriceThread customUnitPriceThread;

    boolean result;

    public ProcessHandler(ChargerConfiguration chargerConfiguration) {
        this.chargerConfiguration = chargerConfiguration;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        // server mode 가 아니면 return
        if (!Objects.equals(chargerConfiguration.getAuthMode(), "0")) return;

        bundle = msg.getData();
        // All : 100 , 채널 정보로 connectorId 설정함.
        int channel = bundle.getInt("connectorId") == 1 ? 0 : bundle.getInt("connectorId") == 2 ? 1 : bundle.getInt("connectorId");
        SocketReceiveMessage socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
        if (!Objects.equals(channel, 100)) {
//            chargingCurrentData = ((MainActivity) MainActivity.mContext).getClassUiProcess(channel).getChargingCurrentData();
            chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(channel);
            setConnectorId(chargingCurrentData.getConnectorId());
        } else {
            setConnectorId(100);
        }

        // message parsing
        switch (msg.what) {
            case GlobalVariables.MESSAGE_HANDLER_BOOT_NOTIFICATION:
                try {
                    int delay = bundle.getInt("delay");
                    //BootNotification
                    onBootNotificationStart(delay);
                } catch (Exception e) {
                    logger.error("MESSAGE_HANDLER_BOOT_NOTIFICATION error : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_HEART_BEAT:
                try {
                    int delay = bundle.getInt("delay");
                    onHeartBeatStart(delay);
                } catch (Exception e) {
                    logger.error("MESSAGE_HANDLER_HEART_BEAT error : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION:
                // charger point status change send
                try {
                    String alarmCode = bundle.getString("alarmCode");
                    int connectorId = bundle.getInt("connectorId");
                    // delayTime == 100 reservation 이다
                    int delayTime = bundle.getInt("delay");
                    ZonedDateTime timestamp = zonedDateTimeConvert.doZonedDateTimeToDatetime();
                    StatusNotificationRequest statusNotificationRequest = new StatusNotificationRequest(
                            connectorId,
                            chargingCurrentData.getChargePointErrorCode(),
                            delayTime == 100 ? chargingCurrentData.getReservedStatus() : chargingCurrentData.getChargePointStatus(),
                            timestamp);
                    if (!TextUtils.isEmpty(alarmCode))
                        statusNotificationRequest.setVendorErrorCode(alarmCode);
                    SocketState state = socketReceiveMessage.getSocket().getState();
                    if (state == SocketState.OPEN) {
                        socketReceiveMessage.onSend(100, statusNotificationRequest.getActionName(), statusNotificationRequest);
                    } else {
                        uuid = UUID.randomUUID().toString();
                        payload = packPayload(statusNotificationRequest);
                        call = String.format(CALL_FORMAT, uuid, statusNotificationRequest.getActionName(), payload);
                        LogDataSave logDataSave = new LogDataSave("dump");
                        logDataSave.makeDump(call.toString());
                    }
                } catch (Exception e) {
                    logger.error("MESSAGE_HANDLER_STATUS_NOTIFICATION error : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION_BOOT:
                try {
                    RxData rxData;
                    ZonedDateTime timestamp = zonedDateTimeConvert.doZonedDateTimeToDatetime();
                    StatusNotificationRequest statusNotificationRequest = new StatusNotificationRequest(timestamp);
                    for (int i = 0; i < GlobalVariables.maxPlugCount; i++) {
                        statusNotificationRequest.setConnectorId(i);
                        if (i == 0) {//control board
                            ControlBoard controlBoard = ((MainActivity) MainActivity.mContext).getControlBoard();
                            rxData = ((MainActivity) MainActivity.mContext).getControlBoard().getRxData(0);
                            statusNotificationRequest.setErrorCode(controlBoard.isDisconnected() ? ChargePointErrorCode.EVCommunicationError :
                                    rxData.isCsFault() ? ChargePointErrorCode.OtherError : ChargePointErrorCode.NoError);
                            statusNotificationRequest.setStatus(rxData.isCsFault() ? ChargePointStatus.Faulted :
                                    !GlobalVariables.ChargerOperation[i] ? ChargePointStatus.Unavailable : ChargePointStatus.Available);
                        } else {
                            chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(i - 1);
                            rxData = ((MainActivity) MainActivity.mContext).getControlBoard().getRxData(i - 1);
                            chargingCurrentData.setChargePointStatus(!GlobalVariables.ChargerOperation[i] ? ChargePointStatus.Unavailable :
                                    rxData.isCsStop() ? ChargePointStatus.Charging : rxData.isCsPilot() ? ChargePointStatus.Preparing : ChargePointStatus.Available);
                            statusNotificationRequest.setErrorCode(chargingCurrentData.getChargePointErrorCode());
                            statusNotificationRequest.setStatus(chargingCurrentData.getChargePointStatus());
                        }
                        socketReceiveMessage.onSend(100, statusNotificationRequest.getActionName(), statusNotificationRequest);
                    }
                } catch (Exception e) {
                    logger.error("MESSAGE_HANDLER_STATUS_NOTIFICATION_BOOT error : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_AUTHORIZE:
                try {
                    AuthorizeRequest authorizeRequest = new AuthorizeRequest(bundle.getString("idTag"));
                    socketReceiveMessage.onSend(getConnectorId(), authorizeRequest.getActionName(), authorizeRequest);
                } catch (Exception e) {
                    logger.error("MESSAGE_HANDLER_AUTHORIZE error : {} ", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_GET_PRICE:
                try {
                    ZonedDateTime timestamp = zonedDateTimeConvert.doZonedDateTimeToDatetime();
                    GetPriceData getPriceData = new GetPriceData();
                    getPriceData.setIdTag(bundle.getString("idTag"));
                    getPriceData.setTimestamp(timestamp.toString());
                    GetPriceRequest getPriceRequest = new GetPriceRequest();
                    getPriceRequest.setVendorId(chargerConfiguration.getUnitPriceVendorCode());
                    getPriceRequest.setMessageId("getPrice");
                    getPriceRequest.setData(gson.toJson(getPriceData));
                    socketReceiveMessage.onSend(getConnectorId(), getPriceRequest.getActionName(), getPriceRequest);
                } catch (Exception e) {
                    logger.error("MESSAGE_HANDLER_GET_PRICE error : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_START_TRANSACTION:
                try {
                    int connectorId = bundle.getInt("connectorId");
                    String idTag = bundle.getString("idTag");
                    double meterStart = chargingCurrentData.getPowerMeterStart();
                    ZonedDateTime timestamp = zonedDateTimeConvert.doZonedDateTimeToDatetime(chargingCurrentData.getChargingStartTime());
                    StartTransactionRequest startTransactionRequest = new StartTransactionRequest(getConnectorId(), idTag, (long) (meterStart), timestamp);

                    if (!TextUtils.isEmpty(chargingCurrentData.getResParentIdTag()) || !TextUtils.isEmpty(chargingCurrentData.getResIdTag())) {
                        if (Objects.equals(chargingCurrentData.getIdTag(), chargingCurrentData.getResIdTag()) ||
                                Objects.equals(chargingCurrentData.getParentIdTag(), chargingCurrentData.getResParentIdTag())) {
                            startTransactionRequest.setReservationId(Integer.parseInt(chargingCurrentData.getResReservationId()));
                        }
                    }

                    SocketState state = socketReceiveMessage.getSocket().getState();
                    if (state == SocketState.OPEN) {
                        socketReceiveMessage.onSend(getConnectorId(), startTransactionRequest.getActionName(), startTransactionRequest);
                    } else {
                        uuid = UUID.randomUUID().toString();
                        payload = packPayload(startTransactionRequest);
                        call = String.format(CALL_FORMAT, uuid, startTransactionRequest.getActionName(), payload);
                        LogDataSave logDataSave = new LogDataSave("dump");
                        logDataSave.makeDump(call.toString());
                        //화면을 전환
                        ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(connectorId - 1, UiSeq.CHARGING, "CHARGING", "small");
                        //Meter value start
                        if (GlobalVariables.getMeterValueSampleInterval() > 0) {
                            ((MainActivity) MainActivity.mContext).getClassUiProcess(connectorId - 1).onMeterValueStart(connectorId, GlobalVariables.getMeterValueSampleInterval());
                        }
                    }
                } catch (Exception e) {
                    logger.error("MESSAGE_HANDLER_START_TRANSACTION error : {} ", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_PAY_INFO:
                try {
                    SocketState state = socketReceiveMessage.getSocket().getState();
                    String cpoName = bundle.getString("alarmCode");
                    if (Objects.equals(cpoName, "HUMAX")) {
                        PaymentRequestData paymentRequestData = new PaymentRequestData();
                        paymentRequestData.setConnectorId(getConnectorId());
                        paymentRequestData.setResultCode(chargingCurrentData.getResponseCode());
                        paymentRequestData.setResultMsg(chargingCurrentData.getResponseMessage());
                        paymentRequestData.setMid(chargerConfiguration.getMID());
                        paymentRequestData.setTid(chargingCurrentData.getPgTranSeq());
                        paymentRequestData.setOid(chargingCurrentData.getTradeUniqueNumber());
                        paymentRequestData.setAmt(chargingCurrentData.getPrePayment());
                        paymentRequestData.setAuthCode(chargingCurrentData.getApprovalNumber());
                        String authDate = zonedDateTimeConvert.doZonedDateTimeToStringUTC(chargingCurrentData.getApprovalDate()+chargingCurrentData.getApprovalTime());

                        paymentRequestData.setAuthDate(authDate);

                        for (Connector connector : ((MainActivity) MainActivity.mContext).getConnectorList()) {
                            int connectorId = connector.getConnectorId();
                            if (Objects.equals(connectorId, getConnectorId())) {
                                String qrUrl = connector.getQrUrl();
                                paymentRequestData.setBuyerName(qrUrl.substring(qrUrl.lastIndexOf("/") + 1));
                            }
                        }
                        paymentRequestData.setCardNum(chargingCurrentData.getCreditCardNumber());
                        paymentRequestData.setFnCd(Objects.equals(chargingCurrentData.getIssuer(), "") ? "" : chargingCurrentData.getIssuer().substring(0,4));
                        paymentRequestData.setFnName(Objects.equals(chargingCurrentData.getIssuer(), "") ? "" : chargingCurrentData.getIssuer().substring(4).trim());

                        PaymentRequest paymentRequest = new PaymentRequest();
                        paymentRequest.setVendorId(chargerConfiguration.getChargerPointVendor());
                        paymentRequest.setMessageId("Payment");
                        paymentRequest.setData(gson.toJson(paymentRequestData));
                        if (state == SocketState.OPEN) {
                            socketReceiveMessage.onSend(getConnectorId(), paymentRequest.getActionName(), paymentRequest);
                        }  else {
                            uuid = UUID.randomUUID().toString();
                            payload = packPayload(paymentRequest);
                            call = String.format(CALL_FORMAT, uuid, paymentRequest.getActionName(), payload);
                            LogDataSave logDataSave = new LogDataSave("dump");
                            logDataSave.makeDump(call.toString());
                        }
                    } else {
                        ZonedDateTime timestamp = zonedDateTimeConvert.doZonedDateTimeToDatetime();
                        PayInfoData payInfoData = new PayInfoData();
                        payInfoData.setConnectorId(getConnectorId());
                        payInfoData.setTimestamp(zonedDateTimeConvert.doZonedDateTimeToString(timestamp));
                        payInfoData.setPgTransactionNum(chargingCurrentData.getPgTranSeq());
                        payInfoData.setPayId(chargingCurrentData.getPayId());
                        payInfoData.setApprovalNum(chargingCurrentData.getApprovalDate());
                        payInfoData.setTransactionDate(chargingCurrentData.getApprovalDate());
                        payInfoData.setTransactionTime(chargingCurrentData.getApprovalTime());
                        payInfoData.setAuthAmount(String.valueOf(chargingCurrentData.getPrePayment()));      //선결제 금액
                        payInfoData.setCardNum(chargingCurrentData.getCreditCardNumber());

                        PayInfoRequest payInfoRequest = new PayInfoRequest();
                        payInfoRequest.setVendorId(chargerConfiguration.getChargerPointVendor());
                        payInfoRequest.setMessageId("payInfo");
                        payInfoRequest.setData(gson.toJson(payInfoData));
                        // 결제 성공, 서버와 연결이 안되는 경우에 우선 충전 start 시킨다.
                        // 결제 이력 dump 저장
                        if (state == SocketState.OPEN) {
                            socketReceiveMessage.onSend(getConnectorId(), payInfoRequest.getActionName(), payInfoRequest);
                        } else {
                            uuid = UUID.randomUUID().toString();
                            payload = packPayload(payInfoRequest);
                            call = String.format(CALL_FORMAT, uuid, payInfoRequest.getActionName(), payload);
                            LogDataSave logDataSave = new LogDataSave("dump");
                            logDataSave.makeDump(call.toString());
                        }
                    }

                } catch (Exception e) {
                    logger.error("MESSAGE_HANDLER_METER_VALUE error : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_PARTIAL_CANCEL:
                try {
                    SocketState state = socketReceiveMessage.getSocket().getState();
                    String idTag = bundle.getString("idTag");
                    boolean payResult = bundle.getBoolean("result");
                    ZonedDateTime timestamp = zonedDateTimeConvert.doZonedDateTimeToDatetime();
                    String cpoName = bundle.getString("alarmCode");
                    if (Objects.equals(cpoName, "HUMAX")) {
                        PaymentCancelData paymentCancelData = new PaymentCancelData();
                        paymentCancelData.setConnectorId(chargingCurrentData.getConnectorId());
                        paymentCancelData.setIdTag(chargingCurrentData.getIdTag());
                        paymentCancelData.setResultCode(chargingCurrentData.getResponseCode());
                        paymentCancelData.setResultMsg(chargingCurrentData.getResponseMessage());
                        paymentCancelData.setTid(chargingCurrentData.getPgTranSeq());
                        paymentCancelData.setCancelAmt(chargingCurrentData.getPartialCancelPayment());
                        //String authDate = zonedDateTimeConvert.doZonedDateTimeToString(chargingCurrentData.getApprovalDate()+chargingCurrentData.getApprovalTime());
                        String authDate = zonedDateTimeConvert.doZonedDateTimeToStringUTC(chargingCurrentData.getApprovalDate()+chargingCurrentData.getApprovalTime());
                        paymentCancelData.setCancelDate(authDate);

                        PaymentCancelRequest paymentCancelRequest = new PaymentCancelRequest();
                        paymentCancelRequest.setVendorId(chargerConfiguration.getChargerPointVendor());
                        paymentCancelRequest.setMessageId("PaymentCancel");
                        paymentCancelRequest.setData(gson.toJson(paymentCancelData));
                        if (state == SocketState.OPEN) {
                            socketReceiveMessage.onSend(getConnectorId(), paymentCancelRequest.getActionName(), paymentCancelRequest);
                        }  else {
                            uuid = UUID.randomUUID().toString();
                            payload = packPayload(paymentCancelRequest);
                            call = String.format(CALL_FORMAT, uuid, paymentCancelRequest.getActionName(), payload);
                            LogDataSave logDataSave = new LogDataSave("dump");
                            logDataSave.makeDump(call.toString());
                        }
                    } else {
                        PartialCancelData partialCancelData = new PartialCancelData();
                        partialCancelData.setConnectorId(getConnectorId());
                        partialCancelData.setTimestamp(zonedDateTimeConvert.doZonedDateTimeToString(timestamp));
                        partialCancelData.setPgTransactionNum(chargingCurrentData.getPgTranSeq());
                        partialCancelData.setPayId(idTag);
                        partialCancelData.setTransactionId(chargingCurrentData.getTransactionId());
                        partialCancelData.setDeposit(chargingCurrentData.getPartialCancelPayment());                  //부분취소 후 실결제 금액
                        partialCancelData.setPayResult(payResult ? 0 : 1);
                        if (!TextUtils.isEmpty(chargingCurrentData.getChargingStartTime())) {
                            partialCancelData.setStartTimestamp(zonedDateTimeConvert.doZonedDateTimeToString(chargingCurrentData.getChargingStartTime()));      //input date-time format = yyyyMMddHHmmss
                        }
                        if (!TextUtils.isEmpty(chargingCurrentData.getChargingEndTime())) {
                            partialCancelData.setStopTimestamp(zonedDateTimeConvert.doZonedDateTimeToString(chargingCurrentData.getChargingEndTime()));         //input date-time format = yyyyMMddHHmmss
                        }

                        PartialCancelRequest partialCancelRequest = new PartialCancelRequest();
                        partialCancelRequest.setVendorId(chargerConfiguration.getChargerPointVendor());
                        partialCancelRequest.setMessageId("partialCancel");
                        partialCancelRequest.setData(gson.toJson(partialCancelData));
                        if (Objects.equals(state.getValue(), 7)) {
                            socketReceiveMessage.onSend(getConnectorId(), partialCancelRequest.getActionName(), partialCancelRequest);
                        } else {
                            uuid = UUID.randomUUID().toString();
                            payload = packPayload(partialCancelRequest);
                            call = String.format(CALL_FORMAT, uuid, partialCancelRequest.getActionName(), payload);
                            LogDataSave logDataSave = new LogDataSave("dump");
                            logDataSave.makeDump(call.toString());
                        }
                    }
                } catch (Exception e) {
                    logger.error(" MESSAGE_HANDLER_PARTIAL_CANCEL error : {} ", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_STOP_TRANSACTION:
                try {
                    String idTag = bundle.getString("idTag");
                    ZonedDateTime timestamp = zonedDateTimeConvert.doZonedDateTimeToDatetime(chargingCurrentData.getChargingEndTime());
                    StopTransactionRequest stopTransactionRequest = new StopTransactionRequest(
                            chargingCurrentData.getPowerMeterStop(),
                            timestamp,
                            chargingCurrentData.getTransactionId(),
                            chargingCurrentData.getStopReason());
                    stopTransactionRequest.setIdTag(idTag);
                    SocketState state = socketReceiveMessage.getSocket().getState();
                    if (Objects.equals(state, SocketState.OPEN)) {
                        socketReceiveMessage.onSend(getConnectorId(), stopTransactionRequest.getActionName(), stopTransactionRequest);
                    } else {
                        uuid = UUID.randomUUID().toString();
                        payload = packPayload(stopTransactionRequest);
                        call = String.format(CALL_FORMAT, uuid, stopTransactionRequest.getActionName(), payload);
                        LogDataSave logDataSave = new LogDataSave("dump");
                        logDataSave.makeDump(call.toString());
                    }
                } catch (Exception e) {
                    logger.error("MESSAGE_HANDLER_STOP_TRANSACTION error : {} ", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_RESET:
                try {
                    uuid = bundle.getString("uuid");
                    ResetStatus status = bundle.getBoolean("result") ? ResetStatus.Accepted : ResetStatus.Rejected;
                    ResetConfirmation resetConfirmation = new ResetConfirmation(status);
                    socketReceiveMessage.onResultSend(resetConfirmation.getActionName(), uuid, resetConfirmation);
                } catch (OccurenceConstraintException e) {
                    logger.error("MESSAGE_HANDLER_RESET/ REMOTE_START_TRANSACTION error : {} ", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_REMOTE_START_TRANSACTION:
                try {
                    uuid = bundle.getString("uuid");
                    RemoteStartStopStatus status = bundle.getBoolean("result") ? RemoteStartStopStatus.Accepted : RemoteStartStopStatus.Rejected;
                    RemoteStartTransactionConfirmation remoteStartTransactionConfirmation = new RemoteStartTransactionConfirmation(status);
                    socketReceiveMessage.onResultSend(remoteStartTransactionConfirmation.getActionName(), uuid, remoteStartTransactionConfirmation);
                } catch (OccurenceConstraintException e) {
                    logger.error("MESSAGE_HANDLER_RESET/ MESSAGE_HANDLER_REMOTE_START_TRANSACTION error : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_REMOTE_STOP_TRANSACTION:
                try {
                    uuid = bundle.getString("uuid");
                    RemoteStartStopStatus result = bundle.getBoolean("result") ? RemoteStartStopStatus.Accepted : RemoteStartStopStatus.Rejected;
                    RemoteStopTransactionConfirmation remoteStopTransactionConfirmation = new RemoteStopTransactionConfirmation(result);
                    socketReceiveMessage.onResultSend(remoteStopTransactionConfirmation.getActionName(), uuid, remoteStopTransactionConfirmation);
                } catch (Exception e) {
                    logger.error("MESSAGE_HANDLER_REMOTE_STOP_TRANSACTION error : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_RESULT_PRICE:
                try {
                    ResultPriceData resultPriceData = new ResultPriceData();
                    resultPriceData.setConnectorId(getConnectorId());
                    resultPriceData.setTransactionId(chargingCurrentData.getTransactionId());
                    resultPriceData.setUnitPrice(chargingCurrentData.getPowerUnitPrice());
                    resultPriceData.setUsePower(chargingCurrentData.getPowerMeterUse());
                    resultPriceData.setResultPrice((int) chargingCurrentData.getPowerMeterUsePay());

                    ResultPriceRequest resultPriceRequest = new ResultPriceRequest();
                    resultPriceRequest.setVendorId(chargerConfiguration.getUnitPriceVendorCode());
                    resultPriceRequest.setMessageId("resultPrice");
                    resultPriceRequest.setData(gson.toJson(resultPriceData));
                    SocketState state = socketReceiveMessage.getSocket().getState();
                    if (Objects.equals(state.getValue(), 7)) {
                        socketReceiveMessage.onSend(getConnectorId(), resultPriceRequest.getActionName(), resultPriceRequest);
                    } else {
                        uuid = UUID.randomUUID().toString();
                        payload = packPayload(resultPriceRequest);
                        call = String.format(CALL_FORMAT, uuid, resultPriceRequest.getActionName(), payload);
                        LogDataSave logDataSave = new LogDataSave("dump");
                        logDataSave.makeDump(call.toString());
                    }
                } catch (Exception e) {
                    logger.error("GlobalVariables.MESSAGE_HANDLER_RESULT_PRICE error : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_SMS_MESSAGE:
                try {
                    String telNum = bundle.getString("idTag");  //idTag 값을 전화 번호 넣는다.
                    SmsMessageData smsMessageData = new SmsMessageData();
                    smsMessageData.setTransactionId(chargingCurrentData.getTransactionId());
                    smsMessageData.setTeleNum(telNum);
                    SmsMessageRequest smsMessageRequest = new SmsMessageRequest();
                    smsMessageRequest.setVendorId(chargerConfiguration.getUnitPriceVendorCode());
                    smsMessageRequest.setMessageId("smsMessage");
                    smsMessageRequest.setData(gson.toJson(smsMessageData));
                    socketReceiveMessage.onSend(getConnectorId(), smsMessageRequest.getActionName(), smsMessageRequest);
                } catch (Exception e) {
                    logger.error("GlobalVariables.MESSAGE_HANDLER_SMS_MESSAGE error : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_ANNOUNCE:
                try {
                    uuid = bundle.getString("uuid");
                    DataTransferStatus status = bundle.getBoolean("result") ? DataTransferStatus.Accepted : DataTransferStatus.Rejected;
                    AnnounceConfirmation announceConfirmation = new AnnounceConfirmation(status);
                    socketReceiveMessage.onResultSend(announceConfirmation.getActionName(), uuid, announceConfirmation);
                } catch (Exception e) {
                    logger.error("GlobalVariables.MESSAGE_HANDLER_ANNOUNCE error : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_TRIGGER_MESSAGE:
                try {
                    bundle = msg.getData();
                    uuid = bundle.getString("uuid");
                    TriggerMessageStatus status = bundle.getBoolean("result") ? TriggerMessageStatus.Accepted : TriggerMessageStatus.Rejected;
                    TriggerMessageConfirmation triggerMessageConfirmation = new TriggerMessageConfirmation(status);
                    socketReceiveMessage.onResultSend(triggerMessageConfirmation.getActionName(), uuid, triggerMessageConfirmation);
                } catch (OccurenceConstraintException e) {
                    logger.error("MESSAGE_HANDLER_TRIGGER_MESSAGE : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_CHANGE_CONFIGURATION:
                try {
                    bundle = msg.getData();
                    uuid = bundle.getString("uuid");
                    result = bundle.getBoolean("result");
                    ConfigurationStatus configurationStatus = GlobalVariables.isNotSupportedKey() ? ConfigurationStatus.NotSupported : result ? ConfigurationStatus.Accepted : ConfigurationStatus.Rejected;
                    ChangeConfigurationConfirmation changeConfigurationConfirmation = new ChangeConfigurationConfirmation(configurationStatus);
                    socketReceiveMessage.onResultSend(changeConfigurationConfirmation.getActionName(), uuid, changeConfigurationConfirmation);
                } catch (OccurenceConstraintException e) {
                    logger.error("MESSAGE_CHANGE_CONFIGURATION error :  {} ", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_CHANGE_AVAILABILITY:
                try {
                    bundle = msg.getData();
                    uuid = bundle.getString("uuid");
                    AvailabilityStatus result = bundle.getBoolean("result") ? AvailabilityStatus.Accepted : AvailabilityStatus.Rejected;
                    ChangeAvailabilityConfirmation changeAvailabilityConfirmation = new ChangeAvailabilityConfirmation(result);
                    socketReceiveMessage.onResultSend(changeAvailabilityConfirmation.getActionName(), uuid, changeAvailabilityConfirmation);
                } catch (OccurenceConstraintException e) {
                    logger.error("MESSAGE_CHANGE_AVAILABILITY : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_CHANGE_AVAILABILITY:
                try {
                    bundle = msg.getData();
                    int connectorId = bundle.getInt("connectorId");
                    ZonedDateTimeConvert zonedDateTimeConvert = new ZonedDateTimeConvert();
                    ZonedDateTime timestamp = zonedDateTimeConvert.doZonedDateTimeToDatetime();
                    StatusNotificationRequest statusNotificationRequest = new StatusNotificationRequest(timestamp);

                    int ch = connectorId == 2 ? 1 : 0;

                    switch (connectorId) {
                        case 0:
                            try {
                                for (int i = 0; i < GlobalVariables.maxPlugCount; i++) {
                                    if (i == 0) {
                                        statusNotificationRequest.setConnectorId(i);
                                        boolean controlConnect = ((MainActivity) MainActivity.mContext).getControlBoard().isDisconnected();
                                        statusNotificationRequest.setErrorCode(controlConnect ? ChargePointErrorCode.OtherError : ChargePointErrorCode.NoError);
                                        statusNotificationRequest.setStatus(!GlobalVariables.ChargerOperation[i] ? ChargePointStatus.Unavailable : ChargePointStatus.Available);
                                    } else {
                                        chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(i - 1);
                                        statusNotificationRequest.setConnectorId(i);
                                        statusNotificationRequest.setErrorCode(chargingCurrentData.getChargePointErrorCode());
                                        chargingCurrentData.setChargePointStatus(GlobalVariables.ChargerOperation[i] ? ChargePointStatus.Available : ChargePointStatus.Unavailable);
                                        statusNotificationRequest.setStatus(GlobalVariables.ChargerOperation[i] ? ChargePointStatus.Available : ChargePointStatus.Unavailable);
                                    }
                                    socketReceiveMessage.onSend(i, statusNotificationRequest.getActionName(), statusNotificationRequest);
                                }
                            } catch (Exception e) {
                                logger.error("MESSAGE_HANDLER_CHANGE_AVAILABILITY2 error : {}", e.getMessage());
                            }
                            break;
                        case 1:
                        case 2:
                            try {
                                chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(ch);
                                statusNotificationRequest.setConnectorId(connectorId);
                                statusNotificationRequest.setErrorCode(chargingCurrentData.getChargePointErrorCode());
                                statusNotificationRequest.setStatus(GlobalVariables.ChargerOperation[connectorId] ? ChargePointStatus.Available : ChargePointStatus.Unavailable);
                                socketReceiveMessage.onSend(connectorId, statusNotificationRequest.getActionName(), statusNotificationRequest);
                            } catch (Exception e) {
                                logger.error("MESSAGE_HANDLER_CHANGE_AVAILABILITY1 error : {}", e.getMessage());
                            }
                            break;
                    }
                } catch (Exception e) {
                    logger.error("MESSAGE_HANDLER_CHANGE_AVAILABILITY error :  {} ", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_GET_CONFIGURATION:
                try {
                    bundle = msg.getData();
                    connectorId = bundle.getInt("connectorId");
                    uuid = bundle.getString("uuid");
                    String configurationString;
                    JSONObject jsonObjectData;
                    JSONArray jsonArrayContent;
                    KeyValueType[] keyValueTypes = null;
                    chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(connectorId);
                    FileManagement fileManagement = new FileManagement();
                    String rootPath = Environment.getExternalStorageDirectory().toString() + File.separator + "Download" + File.separator + "ConfigurationKey";
                    try {
                        configurationString = fileManagement.getStringFromFile(rootPath);
                        jsonObjectData = new JSONObject(configurationString);
                        jsonArrayContent = jsonObjectData.getJSONArray("values");
                        if (!Objects.equals(GlobalVariables.getConfigurationKey(), "")) {
                            keyValueTypes = new KeyValueType[1];
                            for (int i = 0; i < jsonArrayContent.length(); i++) {
                                JSONObject contDetail = jsonArrayContent.getJSONObject(i);
                                if (Objects.equals(GlobalVariables.getConfigurationKey(), contDetail.getString("key"))) {
                                    KeyValueType keyValueType = new KeyValueType(contDetail.getString("key"), contDetail.getBoolean("readonly"), contDetail.getString("value"));
                                    keyValueTypes[0] = keyValueType;
                                    break;
                                }
                            }
                            GlobalVariables.setConfigurationKey("");
                        } else {
                            keyValueTypes = new KeyValueType[jsonArrayContent.length()];
                            for (int i = 0; i < jsonArrayContent.length(); i++) {
                                JSONObject contDetail = jsonArrayContent.getJSONObject(i);
                                KeyValueType keyValueType = new KeyValueType(contDetail.getString("key"), contDetail.getBoolean("readonly"), contDetail.getString("value"));
                                keyValueTypes[i] = keyValueType;
                            }
                        }
                    } catch (Exception e) {
                        logger.error(" MESSAGE_GET_CONFIGURATION error : {}", e.getMessage());
                    }
                    GetConfigurationConfirmation getConfigurationConfirmation = new GetConfigurationConfirmation();
                    getConfigurationConfirmation.setConfigurationKey(keyValueTypes);
                    socketReceiveMessage.onResultSend(getConfigurationConfirmation.getActionName(), uuid, getConfigurationConfirmation);
                } catch (Exception e) {
                    logger.error("MESSAGE_GET_CONFIGURATION error : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_CLEAR_CACHE:
                try {
                    ClearCacheStatus clearCacheStatus = ClearCacheStatus.Rejected;
                    bundle = msg.getData();
                    uuid = bundle.getString("uuid");

                    String value = socketReceiveMessage.getConfigurationValue("AuthorizationCacheEnabled");
                    clearCacheStatus = Objects.equals(value, "none") ? ClearCacheStatus.Rejected : ClearCacheStatus.Accepted;
                    ClearCacheConfirmation clearCacheConfirmation = new ClearCacheConfirmation(clearCacheStatus);
                    socketReceiveMessage.onResultSend(clearCacheConfirmation.getActionName(), uuid, clearCacheConfirmation);

                } catch (Exception e) {
                    logger.error("ClearCache error : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_CLEAR_CHARGING_PROFILE:
                try {
                    bundle = msg.getData();
                    ClearChargingProfileStatus clearChargingProfileStatus = ClearChargingProfileStatus.Unknown;
                    uuid = bundle.getString("uuid");
                    FileManagement fileManagement = new FileManagement();
                    String rootPath = Environment.getExternalStorageDirectory().toString() + File.separator + "Download" + File.separator + "ConfigurationKey";
                    String configurationString = fileManagement.getStringFromFile(rootPath);
                    JSONObject jsonObject = new JSONObject(configurationString);
                    JSONArray jsonArray = jsonObject.getJSONArray("values");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject contDetail = jsonArray.getJSONObject(i);
                        if (Objects.equals("MaxChargingProfilesInstalled", contDetail.getString("key"))) {
                            clearChargingProfileStatus = ClearChargingProfileStatus.Accepted;
                            break;
                        }
                    }
                    ClearChargingProfileConfirmation clearChargingConfirmation = new ClearChargingProfileConfirmation(clearChargingProfileStatus);
                    socketReceiveMessage.onResultSend(clearChargingConfirmation.getActionName(), uuid, clearChargingConfirmation);
                } catch (Exception e) {
                    logger.error("ClearChargingProfile : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_LOCAL_LIST:
                try {
                    bundle = msg.getData();
                    uuid = bundle.getString("uuid");
                    SendLocalListConfirmation sendLocalListConfirmation = new SendLocalListConfirmation(GlobalVariables.updateStatus);
                    socketReceiveMessage.onResultSend(sendLocalListConfirmation.getActionName(), uuid, sendLocalListConfirmation);
                } catch (Exception e) {
                    logger.error("MESSAGE_HANDLER_LOCAL_LIST : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_HANDLER_FIRMWARE_STATUS:
                try {
                    bundle = msg.getData();
                    String path = GlobalVariables.getRootPath() + File.separator + "FirmwareStatusNotification";
                    File firmwareFile = new File(path);
                    // file == exists =>  rebooting 후, firmware status
                    if (firmwareFile.exists()) {
                        String line;
                        FileReader fileReader = new FileReader(firmwareFile);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        while ((line = bufferedReader.readLine()) != null) {
                            String[] resultStatus = line.split("-");
                            if (Objects.equals(resultStatus[0], "SignedFirmware")) {
                                SignedFirmwareStatus signedFirmwareStatus = SignedFirmwareStatus.valueOf(resultStatus[1]);
                                SignedFirmwareStatusNotificationRequest signedFirmwareStatusNotificationRequest =
                                        new SignedFirmwareStatusNotificationRequest(signedFirmwareStatus);
                                socketReceiveMessage.onSend(100, signedFirmwareStatusNotificationRequest.getActionName(), signedFirmwareStatusNotificationRequest);
                                chargerConfiguration.setSignedFirmwareStatus(signedFirmwareStatus);
                            } else if (Objects.equals(resultStatus[0], "Firmware")) {
                                FirmwareStatus firmwareStatus = FirmwareStatus.valueOf(resultStatus[1]);
                                ;
                                FirmwareStatusNotificationRequest firmwareStatusNotificationRequest = new FirmwareStatusNotificationRequest(firmwareStatus);
                                firmwareStatusNotificationRequest.setStatus(firmwareStatus);
                                socketReceiveMessage.onSend(100, firmwareStatusNotificationRequest.getActionName(), firmwareStatusNotificationRequest);
                                chargerConfiguration.setFirmwareStatus(firmwareStatus);
                            }
                        }
                        result = firmwareFile.delete();
                        fileReader.close();
                        bufferedReader.close();
                    }
                } catch (Exception e) {
                    logger.error("MESSAGE_HANDLER_FIRMWARE_STATUS : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_CUSTOM_UNIT_PRICE:
                try {
                    bundle = msg.getData();
                    String idTag = bundle.getString("idTag");
                    int ch = bundle.getInt("connectorId");
                    String userType = bundle.getString("alarmCode");        //Customer unit price ==> userType 변경 사용

                    CustomUnitPriceData customUnitPriceData = new CustomUnitPriceData();
//                    customUnitPriceData.setConnectorId(TextUtils.isEmpty(idTag) ? ch : 1);
                    customUnitPriceData.setConnectorId(1);
                    customUnitPriceData.setIdTag(TextUtils.isEmpty(idTag) ? "" : idTag);

                    //회원
                    customUnitPriceData.setUserType(userType);
                    ZonedDateTimeConvert zonedDateTimeConvert = new ZonedDateTimeConvert();
                    ZonedDateTime timestamp = zonedDateTimeConvert.doZonedDateTimeToDatetime();
                    customUnitPriceData.setTimestamp(timestamp.toString());

                    CustomUnitPriceRequest customUnitPriceRequest = new CustomUnitPriceRequest();
                    customUnitPriceRequest.setVendorId(chargerConfiguration.getChargerPointVendor());
                    customUnitPriceRequest.setMessageId("CustomUnitPrice");

                    customUnitPriceRequest.setData(gson.toJson(customUnitPriceData));

                    socketReceiveMessage.onSend(getConnectorId(), customUnitPriceRequest.getActionName(), customUnitPriceRequest);

                } catch (Exception e) {
                    logger.error("MESSAGE_CUSTOM_UNIT_PRICE : {}", e.getMessage());
                }
                break;
            case GlobalVariables.MESSAGE_CUSTOM_STATUS_NOTIFICATION:
                try {
                    bundle = msg.getData();
                    connectorId = bundle.getInt("connectorId");
                    ControlBoard controlBoard = ((MainActivity) MainActivity.mContext).getControlBoard();
                    ClassUiProcess classUiProcess = ((MainActivity) MainActivity.mContext).getClassUiProcess(connectorId-1);
                    chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(connectorId-1);
                    ChargerPointInfoData chargerPointInfoData = new ChargerPointInfoData();
                    chargerPointInfoData.setConnectorId(connectorId);
                    chargerPointInfoData.setTransactionId(classUiProcess.getUiSeq() == UiSeq.CHARGING ? chargingCurrentData.getTransactionId() : 0);
                    chargerPointInfoData.setRsrp(GlobalVariables.getRSRP());
                    chargerPointInfoData.setFirmwareVersion(GlobalVariables.VERSION);
                    chargerPointInfoData.setConfigVersion(GlobalVariables.HmCpConfigVersion);
                    ChargePointStatus chargePointStatus = chargingCurrentData.getChargePointStatus();
                    String cpStatus = chargePointStatus == ChargePointStatus.Preparing || chargePointStatus == ChargePointStatus.Charging ? "3" :
                            socketReceiveMessage.getSocket().getState() != SocketState.OPEN || controlBoard.disconnected ? "1" :
                                    chargePointStatus == ChargePointStatus.Finishing ? "8" : "2";
                    chargerPointInfoData.setCpStatus(cpStatus);
                    chargerPointInfoData.setCableConnYn(controlBoard.getRxData(connectorId-1).isCsPilot() ? "Y" : "N");
                    chargerPointInfoData.setEmergencyYn(controlBoard.getRxData(connectorId-1).isCsEmergency() ? "Y" : "N");
                    chargerPointInfoData.setCpCoolerYn("N");
                    chargerPointInfoData.setCpCoolerSpeed(0);
                    chargerPointInfoData.setCpTemperature("0");
                    chargerPointInfoData.setIdTag(chargerPointInfoData.getIdTag());
                    chargerPointInfoData.setEventCode("0");
                    chargerPointInfoData.setErrorCode("0");
                    chargerPointInfoData.setAccPower((int) controlBoard.getRxData(connectorId-1).getPowerMeter() * 10);
                    chargerPointInfoData.setUsePower(classUiProcess.getUiSeq() == UiSeq.CHARGING ? (int) chargingCurrentData.getPowerMeterUse() * 10 : 0);
                    chargerPointInfoData.setUsePrice(classUiProcess.getUiSeq() == UiSeq.CHARGING ? String.valueOf(chargingCurrentData.getPowerUnitPrice()) : "0");
                    chargerPointInfoData.setUseAmt(classUiProcess.getUiSeq() == UiSeq.CHARGING ? String.valueOf((int) chargingCurrentData.getPowerMeterUsePay()) : "0");
                    chargerPointInfoData.setTimestamp(zonedDateTimeConvert.doGetCurrentTimeUTC());
                    chargerPointInfoData.setBmsSwVersion("0");
                    chargerPointInfoData.setBatCapacity("0");
                    chargerPointInfoData.setBatStatus("0");

//                    chargerPointInfoData.setBatSoc(String.valueOf(classUiProcess.getUiSeq() == UiSeq.CHARGING ? controlBoard.getRxData(connectorId - 1).getSoc() : "0"));
                    int soc  = ((MainActivity) MainActivity.mContext).getControlBoard().getRxData(connectorId-1).getSoc();
                    chargerPointInfoData.setBatSoc(String.valueOf(soc));
                    chargerPointInfoData.setBatTemperature("0");
                    chargerPointInfoData.setBatEleCurrent("0");
                    chargerPointInfoData.setBatVoltage("0");
                    chargerPointInfoData.setRemainTime("0");

                    CustomStatusNotiRequest customStatusNotiRequest = new CustomStatusNotiRequest();
                    customStatusNotiRequest.setVendorId(chargerConfiguration.getChargerPointVendor());
                    customStatusNotiRequest.setMessageId("CustomStatusNoti");
                    customStatusNotiRequest.setData(gson.toJson(chargerPointInfoData));

                    socketReceiveMessage.onSend(getConnectorId(), customStatusNotiRequest.getActionName(), customStatusNotiRequest);

                } catch (Exception e) {
                    logger.error("MESSAGE_CUSTOM_STATUS_NOTIFICATION : {}", e.getMessage());
                }
                break;
        }
    }


    /**
     * Heart beat thread
     *
     * @param delay delay time
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onHeartBeatStart(int delay) {
        if (heartbeatThread == null || heartbeatThread.getState() != Thread.State.RUNNABLE) {
            heartbeatThread = new HeartbeatThread(delay);
            heartbeatThread.setStopped(false);
            heartbeatThread.start();
        }
    }

    public void onHeartBeatStop() {
        if (heartbeatThread != null) {
            heartbeatThread.interrupt();
            heartbeatThread.setStopped(true);
            heartbeatThread = null;
        }
    }


    /**
     * boot notification
     *
     * @param delay delay time
     */
    public void onBootNotificationStart(int delay) {
        onBootNotificationStop();
        bootNotificationThread = new BootNotificationThread(delay);
        bootNotificationThread.setStopped(false);
        bootNotificationThread.start();

    }

    public void onBootNotificationStop() {
        if (bootNotificationThread != null) {
            bootNotificationThread.interrupt();
            bootNotificationThread.setStopped(true);
            bootNotificationThread = null;
        }
    }


    /**
     * Diagnostics log create
     *
     * @param delay delay time 기본 15분
     */
    public void onDiagnosticsStart(int delay) {
        onDiagnosticsStop();
        diagnosticsThread = new DiagnosticsThread(delay);
        diagnosticsThread.setStopped(false);
        diagnosticsThread.start();
    }

    public void onDiagnosticsStop() {
        if (diagnosticsThread != null) {
            diagnosticsThread.interrupt();
            diagnosticsThread.setStopped(true);
            diagnosticsThread = null;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onMeterValueSendOne(int channel) {
        try {
            SocketReceiveMessage socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
            ZonedDateTimeConvert zonedDateTimeConvert = new ZonedDateTimeConvert();
            ZonedDateTime timestamp = zonedDateTimeConvert.doZonedDateTimeToDatetime();
            if (Objects.equals(socketReceiveMessage.getSocket().getState(), SocketState.OPEN)) {
                ChargingCurrentData chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(channel);
                SampledValue[] sampledValues = chargingCurrentData.getSampleValueData().getSampledValues(chargingCurrentData);
                MeterValue[] meterValues = {new MeterValue(timestamp, sampledValues)};
                MeterValuesRequest meterValuesRequest = new MeterValuesRequest(chargingCurrentData.getConnectorId());
                meterValuesRequest.setMeterValue(meterValues);
                socketReceiveMessage.onSend(meterValuesRequest.getActionName(), meterValuesRequest);
            }
        } catch (Exception e) {
            logger.error("onMeterValueSendOne error : {} ", e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Object packPayload(Object payload) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeSerializer());
        Gson gson = builder.create();
        return gson.toJson(payload);
    }

    public void onCustomUnitPriceStart(int delay) {
        onCustomUnitPriceStop();
        customUnitPriceThread = new CustomUnitPriceThread(delay);
        customUnitPriceThread.setStopped(false);
        customUnitPriceThread.start();
    }
    public void onCustomUnitPriceStop() {
        if (customUnitPriceThread != null) {
            customUnitPriceThread.interrupt();
            customUnitPriceThread.setStopped(true);
            customUnitPriceThread = null;
        }
    }

    private class ZonedDateTimeSerializer implements JsonSerializer<ZonedDateTime> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public JsonElement serialize(ZonedDateTime zonedDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(zonedDateTime.format(DateTimeFormatter.ISO_INSTANT));
        }
    }

    public int getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(int connectorId) {
        this.connectorId = connectorId;
    }
}

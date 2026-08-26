package com.dongah.fastcharger.basefunction;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.R;
import com.dongah.fastcharger.TECH3800.TLS3800;
import com.dongah.fastcharger.TECH3800.TLS3800Listener;
import com.dongah.fastcharger.TECH3800.TLS3800ResponseType;
import com.dongah.fastcharger.controlboard.ControlBoard;
import com.dongah.fastcharger.controlboard.RxData;
import com.dongah.fastcharger.handler.CustomStatusNotificationThread;
import com.dongah.fastcharger.handler.MeterValueThread;
import com.dongah.fastcharger.handler.MeterValuesAlignedDataThread;
import com.dongah.fastcharger.handler.ProcessHandler;
import com.dongah.fastcharger.pages.FaultFragment;
import com.dongah.fastcharger.websocket.ocpp.core.ChargePointErrorCode;
import com.dongah.fastcharger.websocket.ocpp.core.ChargePointStatus;
import com.dongah.fastcharger.websocket.ocpp.core.Reason;
import com.dongah.fastcharger.websocket.ocpp.core.ResetType;
import com.dongah.fastcharger.websocket.ocpp.utilities.ZonedDateTimeConvert;
import com.dongah.fastcharger.websocket.socket.SocketReceiveMessage;
import com.dongah.fastcharger.websocket.socket.SocketState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ClassUiProcess implements TLS3800Listener {


    private static final Logger logger = LoggerFactory.getLogger(ClassUiProcess.class);

    int ch;
    UiSeq uiSeq;
    UiSeq oSeq;
    ChargerConfiguration chargerConfiguration;
    ChargingCurrentData chargingCurrentData;

    TLS3800 tls3800;
    FragmentChange fragmentChange;
    NotifyFaultCheck notifyFaultCheck;
    ControlBoard controlBoard;
    Timer eventTimer;

    SocketReceiveMessage socketReceiveMessage;
    ProcessHandler processHandler;


    double powerUnitPrice = 0f;
    int powerMeterCheck = 0;


    ZonedDateTimeConvert zonedDateTimeConvert;
    /**
     * MeterValue Thread
     */
    MeterValueThread meterValueThread;
    MeterValuesAlignedDataThread meterValuesAlignedDataThread;

    /**
     * custom Status Notification
     */
    CustomStatusNotificationThread customStatusNotificationThread;


    public int getCh() {
        return ch;
    }

    public UiSeq getUiSeq() {
        return uiSeq;
    }

    public void setUiSeq(UiSeq uiSeq) {
        this.uiSeq = uiSeq;
    }

    public UiSeq getoSeq() {
        return oSeq;
    }

    public void setoSeq(UiSeq oSeq) {
        this.oSeq = oSeq;
    }

    public double getPowerUnitPrice() {
        return powerUnitPrice;
    }

    public void setPowerUnitPrice(double powerUnitPrice) {
        this.powerUnitPrice = powerUnitPrice;
    }

    public int getPowerMeterCheck() {
        return powerMeterCheck;
    }

    public void setPowerMeterCheck(int powerMeterCheck) {
        this.powerMeterCheck = powerMeterCheck;
    }


    public ClassUiProcess(int ch) {
        this.ch = ch;
        try {
            setUiSeq(UiSeq.INIT);
            zonedDateTimeConvert = new ZonedDateTimeConvert();
            //rf card
            tls3800 = ((MainActivity) MainActivity.mContext).getTls3800();
            tls3800.setTls3800Listener(this);
            // configuration
            chargerConfiguration = ((MainActivity) MainActivity.mContext).getChargerConfiguration();
            //fragment change
            fragmentChange = ((MainActivity) MainActivity.mContext).getFragmentChange();
            //control board
            controlBoard = ((MainActivity) MainActivity.mContext).getControlBoard();
            // alarm check
            notifyFaultCheck = new NotifyFaultCheck(ch);
            //process handler
            processHandler = ((MainActivity) MainActivity.mContext).getProcessHandler();
            //custom status notification
            onCustomStatusNotificationStart(ch+1, Integer.parseInt(GlobalVariables.HmPreparingTranTerm));
            //loop
            eventTimer = new Timer();
            eventTimer.schedule(new TimerTask() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    onEventAction();
                }
            }, 3000, 2000);
        } catch (Exception e) {
            logger.error("ClassUiProcess - construct error : {}", e.getMessage());
        }
    }

    int getId = 0;
    int channel;
    boolean check;

    /**
     * charging sequence loop
     * server data send : 서버와 연결이 안된 경우 ProcessHandler dump data save
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onEventAction() {
        try {
            channel = getCh();
            RxData rxData = controlBoard.getRxData(getCh());
            check = rxData.isCsFault();
            chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(channel);
            chargingCurrentData.setIntegratedPower(rxData.getPowerMeter());
            getId = ((MainActivity) MainActivity.mContext).getFragmentSeq(getCh()).getValue();
            if (((MainActivity) MainActivity.mContext).getFragmentSeq(getCh()).getValue() < 17) onFaultCheck(rxData);
           //reservation check
//            onReservationExpiryDate(chargingCurrentData);

            //sequence check
            switch (getUiSeq()) {
                case NONE:
                case INIT:
                    setoSeq(UiSeq.INIT);
                    setPowerMeterCheck(0);
                    if (Objects.equals(controlBoard.getTxData(channel).getChargerPointMode(), 0)) {
                        controlBoard.getTxData(getCh()).setUiSequence((short) 1);
                        controlBoard.getTxData(getCh()).setStart(false);
                        controlBoard.getTxData(getCh()).setStop(false);
                    }
                    if (chargingCurrentData.isReBoot() && onRebootCheck()) {
                        setUiSeq(UiSeq.REBOOTING);
                    }
                    //MeterValue Stop
                    onMeterValueStop();
                    break;
                case REBOOTING:
                    if (!(getCurrentFragment() instanceof FaultFragment)) {
                        fragmentChange.onFragmentChange(
                                getCh(),
                                UiSeq.REBOOTING,
                                "REBOOTING",
                                chargingCurrentData.getStopReason() == Reason.HardReset ? "Hard" : "Soft");
                    }
                    break;
                case MEMBER_CARD:
                case MEMBER_CARD_WAIT:
                case CREDIT_CARD:
                case CREDIT_CARD_WAIT:
                    break;
                case PLUG_CHECK:
                    if (rxData.isCsPilot()) {
                        controlBoard.getTxData(getCh()).setStart(true);
                        controlBoard.getTxData(getCh()).setStop(false);
                        setUiSeq(UiSeq.CONNECT_CHECK);
                    }
                    break;
                case CONNECT_CHECK:
                    if (rxData.isCsStart()) {
                        chargingCurrentData.setChargePointStatus(ChargePointStatus.Charging);
                        powerUnitPrice = Objects.equals(chargerConfiguration.getAuthMode(), "0") ?
                                chargingCurrentData.getPowerUnitPrice() : Double.parseDouble(chargerConfiguration.getTestPrice());
                        chargingCurrentData.setPowerMeterStart(rxData.getPowerMeter()*10);
                        chargingCurrentData.setPowerMeterCalculate(rxData.getPowerMeter());
                        chargingCurrentData.setChargingStartTime(zonedDateTimeConvert.getStringCurrentTimeZone());
                        //Auto 및 Test
                        //socket receive message get instance
                        socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
                        if (!Objects.equals(chargerConfiguration.getAuthMode(), "0") ||
                                (SocketState.OPEN != socketReceiveMessage.getSocket().getState() && !GlobalVariables.isStopTransactionOnInvalidId())) {
                            setUiSeq(UiSeq.CHARGING);
                            ((MainActivity) MainActivity.mContext).getFragmentChange().onFragmentChange(getCh(), UiSeq.CHARGING, "CHARGING", "small");
                        }
                        if (Objects.equals(chargerConfiguration.getAuthMode(), "0")) {
                            //meter values start
                            if (GlobalVariables.getMeterValueSampleInterval() > 0) {
                                ((MainActivity) MainActivity.mContext).getClassUiProcess(getCh()).
                                        onMeterValueStart(chargingCurrentData.getConnectorId(), GlobalVariables.getMeterValueSampleInterval());
                            }
                            //start transaction send to server
                            processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                                    GlobalVariables.MESSAGE_HANDLER_START_TRANSACTION,
                                    chargingCurrentData.getConnectorId(),
                                    0,
                                    chargingCurrentData.getIdTag(),
                                    null,
                                    null,
                                    false));

                            //status notification send to server
                            processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                                    GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                                    chargingCurrentData.getConnectorId(),
                                    0,
                                    null,
                                    null,
                                    null,
                                    false));
                        }
                        //
                        // ClockAlignedDataInterval > 0 ? 유효 전력량 전송
//                        if (GlobalVariables.getClockAlignedDataInterval() > 0) {
//                            onMeterValuesAlignedDataStart(getCh() + 1, GlobalVariables.getClockAlignedDataInterval());
//                        }
                        setUiSeq(UiSeq.CHARGING);
                    } else if (rxData.isCsStop() || rxData.getCsmStatusCode() == (byte) 0x10) {
                        controlBoard.getTxData(getCh()).setStop(true);
                        controlBoard.getTxData(getCh()).setStart(false);
                        // 충전 실패 사유 toast
                        onHome();
                    }
                    break;
                case CHARGING:
                    try {
                        //충전 사용량 계산
                        onUsePowerMeter(getCh(), rxData);
                        controlBoard.getTxData(getCh()).setUiSequence((short) 2);
                        //stop 조건
                        if (!GlobalVariables.isStopTransactionOnEVSideDisconnect() &&
                                !GlobalVariables.isUnlockConnectorOnEVSideDisconnect()) {
                            if (rxData.isCsStop() || !rxData.isCsPilot()) {
                                if (chargingCurrentData.getStopReason() == Reason.Remote || chargingCurrentData.isUserStop()) {
                                    controlBoard.getTxData(getCh()).setStop(true);
                                    controlBoard.getTxData(getCh()).setStart(false);
                                    if (!rxData.isCsPilot()) {
                                        //status notification send to server : ChargePointStatus.SuspendedEV
                                        //2.4.5. EV Side Disconnected
                                        chargingCurrentData.setStopReason(Reason.EVDisconnected);
//                                        processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
//                                                GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
//                                                chargingCurrentData.getConnectorId(),
//                                                0,
//                                                null,
//                                                null,
//                                                null,
//                                                false));
                                    }
                                    setUiSeq(UiSeq.FINISH_WAIT);
                                }
                            }
                        } else {
                            if (rxData.isCsStop() || !rxData.isCsPilot() || chargingCurrentData.isUserStop() || rxData.getSoc() >= chargerConfiguration.getTargetSoc()) {
//                                    (chargingCurrentData.getHmChargingLimitFee() <= chargingCurrentData.getPowerMeterUsePay())) {
                                controlBoard.getTxData(getCh()).setStop(true);
                                controlBoard.getTxData(getCh()).setStart(false);
                                if (!rxData.isCsPilot()) {
                                    //status notification send to server : ChargePointStatus.SuspendedEV
                                    //2.4.5. EV Side Disconnected
                                    chargingCurrentData.setStopReason(Reason.EVDisconnected);
//                                    processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
//                                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
//                                            chargingCurrentData.getConnectorId(),
//                                            0,
//                                            null,
//                                            null,
//                                            null,
//                                            false));
                                }
                                setUiSeq(UiSeq.FINISH_WAIT);
                            } else if (chargingCurrentData.isPrePaymentResult() &&
                                    (chargingCurrentData.getPrePayment() <= chargingCurrentData.getPowerMeterUsePay())) {
//                                            || chargingCurrentData.getHmChargingLimitFee() <= chargingCurrentData.getPowerMeterUsePay())) {
                                controlBoard.getTxData(getCh()).setStop(true);
                                controlBoard.getTxData(getCh()).setStart(false);
                                chargingCurrentData.setPowerMeterUsePay(chargingCurrentData.getPrePayment());
                                chargingCurrentData.setStopReason(Reason.Other);
                                setUiSeq(UiSeq.FINISH_WAIT);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("classUiProcess - charging error : {}", e.getMessage());
                    }
                    break;
                case FINISH_WAIT:
                    try {
                        controlBoard.getTxData(getCh()).setStop(true);
                        controlBoard.getTxData(getCh()).setUiSequence((short) 3);
                        onMeterValueStop();
                        //사용자 user stop
                        chargingCurrentData.setStopReason(chargingCurrentData.isUserStop() ? Reason.Local : chargingCurrentData.getStopReason());
                        // 충전 사용량 정리
                        chargingCurrentData.setPowerMeterStop(rxData.getPowerMeter()*10);
                        chargingCurrentData.setChargingEndTime(zonedDateTimeConvert.getStringCurrentTimeZone());
                        //stop transaction send to server
                        chargingCurrentData.setChargePointStatus(ChargePointStatus.Finishing);
                        //socket receive message get instance
                        socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
                        if (Objects.equals(chargerConfiguration.getAuthMode(), "0")) {
                            processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                                    GlobalVariables.MESSAGE_HANDLER_STOP_TRANSACTION,
                                    chargingCurrentData.getConnectorId(),
                                    0,
                                    chargingCurrentData.getIdTag(),
                                    null,
                                    null,
                                    false));
                            //status notification send to server
                            processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                                    GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                                    chargingCurrentData.getConnectorId(),
                                    0,
                                    null,
                                    null,
                                    null,
                                    false));
                            //unit price
                            GlobalVariables.setCustomUnitPriceReq(true);
                            GlobalVariables.setHumaxUserType("A");
                            processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                                    GlobalVariables.MESSAGE_CUSTOM_UNIT_PRICE,
                                    chargingCurrentData.getConnectorId(),
                                    0,
                                    null,
                                    null,
                                    GlobalVariables.getHumaxUserType(),       /////////Customer unit price 에서만 userType(A:회원, B:비회원) 사용 한다.
                                    false));
                        }
                        setUiSeq(UiSeq.FINISH);
                        fragmentChange.onFragmentChange(getCh(), UiSeq.FINISH, "FINISH", null);
                    } catch (Exception e) {
                        logger.error("classUiProcess - FINISH_WAIT error : {} ", e.getMessage());
                    }
                    break;
                case FINISH:
                    onFinish();
                    break;
                case FAULT:
                    //* fault check */
                    UiSeq currentViewSeq = ((MainActivity) MainActivity.mContext).getFragmentSeq(getCh());
                    if (currentViewSeq.getValue() < 15) {
                        if (!(getCurrentFragment() instanceof FaultFragment)) {
                            // server mode 및 charging
                            if (Objects.equals(chargerConfiguration.getAuthMode(), "0") &&
                                    Objects.equals(getoSeq(), UiSeq.CHARGING)) {
                                // meter values stop
                                onMeterValueStop();
                                chargingCurrentData.setStopReason(rxData.isCsEmergency()? Reason.EmergencyStop : Reason.Other);
                                controlBoard.getTxData(getCh()).setStop(true);
                                controlBoard.getTxData(getCh()).setStart(false);
                                chargingCurrentData.setUserStop(false);
                                chargingCurrentData.setPowerMeterStop(rxData.getPowerMeter()*10);
                                chargingCurrentData.setChargingEndTime(zonedDateTimeConvert.getStringCurrentTimeZone());
                                chargingCurrentData.setChargePointStatus(ChargePointStatus.Finishing);
                                //socket receive message get instance
                                socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
                                if (socketReceiveMessage != null) {
                                    SocketState state = socketReceiveMessage.getSocket().getState();
                                    if (Objects.equals(state.getValue(), 7) && Objects.equals(chargerConfiguration.getAuthMode(), "0")) {
                                        //server send
                                        processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                                                GlobalVariables.MESSAGE_HANDLER_STOP_TRANSACTION,
                                                chargingCurrentData.getConnectorId(),
                                                0,
                                                chargingCurrentData.getIdTag(),
                                                null,
                                                null,
                                                false));
                                        //status notification send to server
                                        processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                                                GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                                                chargingCurrentData.getConnectorId(),
                                                0,
                                                null,
                                                null,
                                                null,
                                                false));
                                        //unit price
                                        GlobalVariables.setCustomUnitPriceReq(true);
                                        GlobalVariables.setHumaxUserType("A");
                                        processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                                                GlobalVariables.MESSAGE_CUSTOM_UNIT_PRICE,
                                                chargingCurrentData.getConnectorId(),
                                                0,
                                                null,
                                                null,
                                                GlobalVariables.getHumaxUserType(),       /////////Customer unit price 에서만 userType(A:회원, B:비회원) 사용 한다.
                                                false));
                                    }
                                }
                            }
                            fragmentChange.onFragmentChange(getCh(), UiSeq.FAULT, "FAULT", "full");
                        }
                    }

                    //fault 가 해제가 되면..........
                    if (!controlBoard.isDisconnected() && !rxData.isCsFault()) {
                        if (Objects.equals(getoSeq(), UiSeq.CHARGING)) {
                            chargingCurrentData.setChargePointStatus(ChargePointStatus.Finishing);
                            chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.NoError);
                            setUiSeq(UiSeq.FINISH);
                            fragmentChange.onFragmentChange(getCh(), UiSeq.FINISH, "FINISH", null);
                        } else {
                            if (Objects.equals(chargingCurrentData.getChargePointStatus(), ChargePointStatus.Preparing) &&
                                    !rxData.isCsPilot()) {
                                chargingCurrentData.setChargePointStatus(ChargePointStatus.Available);
                                //socket receive message get instance
                                socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
                                if (socketReceiveMessage != null) {
                                    SocketState state = socketReceiveMessage.getSocket().getState();
                                    if (Objects.equals(state.getValue(), 7) && Objects.equals(chargerConfiguration.getAuthMode(), "0")) {
                                        processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                                                GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                                                chargingCurrentData.getConnectorId(),
                                                0,
                                                null,
                                                null,
                                                null,
                                                false));
                                    }
                                }
                            }
                            onHome();
                        }
                    }
                    break;
            }

        } catch (Exception e) {
            logger.error(" onEventAction() exception error : {}", e.getMessage());
        }
    }

    /**
     * TLS3800 call back
     *
     * @param ch          ch
     * @param type        response type
     * @param returnValue hashMap {idTag : value}
     */
    @Override
    public void onTLS3800ResponseCallBack(int ch, TLS3800ResponseType type, HashMap<String, String> returnValue) {
        try {
            String cancelType;
            for (String key : returnValue.keySet()) {
                if (Objects.equals(key, "idTag")) {
                    chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(ch);
                    UiSeq uiSeq = ((MainActivity) MainActivity.mContext).getClassUiProcess(ch).getUiSeq();

                    if (Objects.equals(UiSeq.CHARGING, uiSeq)) {
                        chargingCurrentData.setIdTagStop(returnValue.get("idTag"));
                    } else {
                        chargingCurrentData.setIdTag(returnValue.get("idTag"));
                    }
//                    fragmentChange.onFragmentChange(ch, UiSeq.MEMBER_CARD_WAIT, null, null);
                } else if (Objects.equals(key, "MID")) {
                    chargerConfiguration.setMID(returnValue.get("MID"));
                } else if (Objects.equals(key, "tradeCode")) {
                    chargingCurrentData.setPrePaymentResult(!Objects.equals(returnValue.get("tradeCode"), "X"));
                } else if (Objects.equals(key, "cancelType")) {
                    //(4:무카드 취소)(5:부분 취소)
                    cancelType = returnValue.get("cancelType");
                } else if (Objects.equals(key, "responseCode")) {
                    chargingCurrentData.setResponseCode(returnValue.get("responseCode"));
                } else if (Objects.equals(key, "responseMessage")) {
                    chargingCurrentData.setResponseMessage(returnValue.get("responseMessage"));
                }
            }

            if (Objects.equals(TLS3800ResponseType.PAYG, type)) {
                if (chargingCurrentData.isPrePaymentResult()) {
                    //server send
                    socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
                    processHandler.sendMessage(
                            socketReceiveMessage.onMakeHandlerMessage(
                                    GlobalVariables.MESSAGE_HANDLER_PAY_INFO,
                                    chargingCurrentData.getConnectorId(),
                                    0,
                                    chargingCurrentData.getCreditCardNumber(),
                                    null,
                                    "HUMAX",        //alarmCode : CPO 구분 ==> HUMAX / ""
                                    !Objects.equals(returnValue.get("tradeCode"), "X")
                            ));
                }
            } else if (Objects.equals(TLS3800ResponseType.CANCEL, type)) {
                if (chargingCurrentData.isPrePaymentResult()) {
                    socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
                    processHandler.sendMessage(
                            socketReceiveMessage.onMakeHandlerMessage(
                                    GlobalVariables.MESSAGE_HANDLER_PARTIAL_CANCEL,
                                    chargingCurrentData.getConnectorId(),
                                    0,
                                    chargingCurrentData.getCreditCardNumber(),
                                    null,
                                    "HUMAX",
                                    !Objects.equals(returnValue.get("tradeCode"), "X")
                            ));
                }
            } else if (Objects.equals(TLS3800ResponseType.RF_READ, type))  {
                if (uiSeq != UiSeq.MEMBER_CARD_WAIT) {
                    ((MainActivity) MainActivity.mContext).getClassUiProcess(ch).setUiSeq(UiSeq.MEMBER_CARD_WAIT);
                    fragmentChange.onFragmentChange(ch, UiSeq.MEMBER_CARD_WAIT, null, null);
                }
            } else {
                onHome();
            }


        } catch (Exception e) {
            logger.error("onTLS3800ResponseCallBack error : {}", e.getMessage());
        }
    }


    /**
     * ChargerCurrentData 나중에 init 에서 삭제
     */
    public void onHome() {
        setUiSeq(UiSeq.INIT);
        fragmentChange.onFragmentChange(getCh(), UiSeq.INIT, "INIT", null);
    }


    private void onFinish() {
        //충전 완료
        if (chargingCurrentData.isReBoot()) {
            setUiSeq(UiSeq.INIT);
        }
    }


    /**
     * 현재 Fragment 찾기
     *
     * @return fragment;
     */
    private Fragment getCurrentFragment() {
        return ((MainActivity) MainActivity.mContext).getSupportFragmentManager().findFragmentById(getCh() == 0 ? R.id.ch0 : R.id.ch1);
    }

    /**
     * Remote Transaction stop
     */
    public void onRemoteTransactionStop(int channel, Reason reason) {
        try {
            UiSeq uiSeq = ((MainActivity) MainActivity.mContext).getClassUiProcess(channel).getUiSeq();
            if (Objects.equals(uiSeq, UiSeq.CHARGING)) {
                controlBoard = ((MainActivity) MainActivity.mContext).getControlBoard();
                chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(channel);

                controlBoard.getTxData(channel).setStop(true);
                controlBoard.getTxData(channel).setStart(false);
                chargingCurrentData.setUserStop(false);
                chargingCurrentData.setStopReason(reason);
            }
        } catch (Exception e) {
            logger.error("remote stop error : {} ", e.getMessage());
        }
    }

    public void onResetStop(int channel, ResetType resetType) {
        try {
            UiSeq uiSeq = ((MainActivity) MainActivity.mContext).getClassUiProcess(channel).getUiSeq();
            if (Objects.equals(uiSeq, UiSeq.CHARGING)) {
                controlBoard.getTxData(getCh()).setStop(true);
                controlBoard.getTxData(getCh()).setStart(false);
                chargingCurrentData.setUserStop(false);
                chargingCurrentData.setStopReason(resetType == ResetType.Hard ? Reason.HardReset : Reason.SoftReset);
                setUiSeq(UiSeq.FINISH_WAIT);
            }
        } catch (Exception e) {
            logger.error("reset stop error : {} ", e.getMessage());
        }
    }

    private boolean onRebootCheck() {
        boolean result = false;
        try {
            UiSeq uiSeq1 = ((MainActivity) MainActivity.mContext).getClassUiProcess(0).getUiSeq();
            UiSeq uiSeq2 = ((MainActivity) MainActivity.mContext).getClassUiProcess(1).getUiSeq();
            result = Objects.equals(UiSeq.REBOOTING, uiSeq1) || Objects.equals(UiSeq.INIT, uiSeq1);
            result = result && Objects.equals(UiSeq.REBOOTING, uiSeq2) || Objects.equals(UiSeq.INIT, uiSeq2);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    /**
     * Meter value
     *
     * @param connectorId connector id
     * @param delay       delay time
     */
    public void onMeterValueStart(int connectorId, int delay) {
        onMeterValueStop();
        meterValueThread = new MeterValueThread(connectorId, delay);
        meterValueThread.setStopped(false);
        meterValueThread.start();
    }

    public void onMeterValueStop() {
        if (meterValueThread != null) {
            meterValueThread.setStopped(true);
            meterValueThread.interrupt();
            meterValueThread = null;
        }
    }

    public void onMeterValuesAlignedDataStart(int connectorId, int delay) {
        onMeterValuesAlignedDataStop();
        meterValuesAlignedDataThread = new MeterValuesAlignedDataThread(connectorId, delay);
        meterValuesAlignedDataThread.setStopped(false);
        meterValuesAlignedDataThread.start();
    }

    //meterValuesAlignedData
    public void onMeterValuesAlignedDataStop() {
        if (meterValuesAlignedDataThread != null) {
            meterValuesAlignedDataThread.setStopped(true);
            meterValuesAlignedDataThread.interrupt();
            meterValuesAlignedDataThread = null;
        }
    }

    /**
     * custom status notification
     * @param connectorId connector id
     * @param delay HmChargingTranTerm
     */
    public void onCustomStatusNotificationStart(int connectorId, int delay) {
        onCustomStatusNotificationStop();
        customStatusNotificationThread = new CustomStatusNotificationThread(connectorId,delay);
        customStatusNotificationThread.setStopped(false);
        customStatusNotificationThread.start();
    }

    public void onCustomStatusNotificationStop() {
        if (customStatusNotificationThread != null) {
            customStatusNotificationThread.interrupt();
            customStatusNotificationThread.setStopped(true);
            customStatusNotificationThread = null;
        }
    }


    private void onFaultCheck(RxData rxData) {
        try {
            //충전중 일 때 fault 가 발생한 경우
            if (controlBoard.isDisconnected() || rxData.csFault) {
                if (Objects.equals(getUiSeq(), UiSeq.CHARGING)) {
                    controlBoard.getTxData(getCh()).setStop(true);
                    controlBoard.getTxData(getCh()).setStart(false);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Faulted);
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.OtherError);
                    //비회원 충전 요금 단가 조정을 한다.
                    if (Objects.equals(chargingCurrentData.getPaymentType().value(), 2) &&
                            chargingCurrentData.getPrePayment() <= chargingCurrentData.getPowerMeterUsePay()) {
                        chargingCurrentData.setPowerMeterUsePay(chargingCurrentData.getPrePayment());
                    }
                }
                // fault 발생하기 전에 충전 스퀀스 저장
                if (getUiSeq() != UiSeq.FAULT) setoSeq(getUiSeq());
                setUiSeq(UiSeq.FAULT);
            }
            notifyFaultCheck.onErrorMessageMake(rxData);
        } catch (Exception e) {
            logger.error("onFaultCheck error.... : {}", e.toString());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onReservationExpiryDate(ChargingCurrentData chargingCurrentData) {
        try {
            if (chargingCurrentData.getReservedStatus() == ChargePointStatus.Reserved) {
                String currentTime = zonedDateTimeConvert.doGetUtcDatetimeAsString();

                if (currentTime.compareTo(chargingCurrentData.getResExpiryDate()) > 0) {
                    // available
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Available);
                    chargingCurrentData.setResConnectorId(0);
                    chargingCurrentData.setResIdTag("");
                    chargingCurrentData.setResExpiryDate("");
                    chargingCurrentData.setResReservationId("");
                    chargingCurrentData.setResParentIdTag("");
                    chargingCurrentData.setReservedStatus(ChargePointStatus.Available);

                    socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
                    processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                            chargingCurrentData.getResConnectorId(),
                            0,
                            null,
                            null,
                            null,
                            false));
                }
            }
        } catch (Exception e) {
            logger.error(" onReservationExpiryDate error : {} ", e.getMessage() );
        }
    }

    /**
     * 충전 사용량 계산
     *
     * @param rxData power meter raw data pick
     */
    private void onUsePowerMeter(int ch, RxData rxData) {
        try {
            long gapPower = 0;
            double gapPay = 0;
            if (rxData.getPowerMeter() > 0) {
                //current power meter --> chargingCurrentData .powerKwh
                //전력량 변화 여부 체크
                chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(ch);
                gapPower = rxData.getPowerMeter() - chargingCurrentData.getPowerMeterCalculate();
                gapPower = (gapPower <= 0) ? 0 : (gapPower > 10) ? 1 : gapPower;
                //전력량 변화 여부 체크 892 = 8.92kW
                powerMeterCheck = gapPower == 0 ? powerMeterCheck + 1 : 0;

                chargingCurrentData.setPowerMeterUse(chargingCurrentData.getPowerMeterUse() + gapPower);
                gapPay = gapPower * 0.01 * powerUnitPrice;

                chargingCurrentData.setPowerMeterUsePay(chargingCurrentData.getPowerMeterUsePay() + gapPay);
                chargingCurrentData.setPowerMeterCalculate(rxData.getPowerMeter());

                chargingCurrentData.setRemaintime(rxData.getRemainTime());
            }
            chargingCurrentData.setOutPutCurrent(rxData.getOutCurrent());  //출력전류
            chargingCurrentData.setOutPutVoltage(rxData.getOutVoltage());  //출력전압
            chargingCurrentData.setPowerMeter(rxData.getPowerMeter());  //전력량
            chargingCurrentData.setTargetCurrent(rxData.getCsmEVTargetCurrent());   // 요청전류
            chargingCurrentData.setFrequency(60);    //주파수
            chargingCurrentData.setChargingRemainTime(rxData.getRemainTime());  //충전 남은 시간
            chargingCurrentData.setSoc(rxData.getSoc());
        } catch (Exception e) {
            logger.error("power meter calculate error : {}", e.getMessage());
        }
    }

    /** TargetSoc check*/
    private boolean onSocStop(int targetSoc, int soc) {
        boolean result = false;
        try {
            if (Objects.equals(targetSoc , 0)) {
                result = (soc >= 99);
            } else {
                result = soc > targetSoc;
            }
        } catch (Exception e) {
            logger.error("Soc Check  erro : {}", e.getMessage());
        }
        return result;
    }
}

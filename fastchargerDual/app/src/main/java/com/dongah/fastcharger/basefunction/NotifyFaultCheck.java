package com.dongah.fastcharger.basefunction;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.controlboard.RxData;
import com.dongah.fastcharger.handler.ProcessHandler;
import com.dongah.fastcharger.websocket.ocpp.core.ChargePointErrorCode;
import com.dongah.fastcharger.websocket.ocpp.core.ChargePointStatus;
import com.dongah.fastcharger.websocket.socket.SocketReceiveMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class NotifyFaultCheck {

    private static final Logger logger = LoggerFactory.getLogger(NotifyFaultCheck.class);

    int ch;
    ProcessHandler processHandler;
    SocketReceiveMessage socketReceiveMessage;
    ChargingCurrentData chargingCurrentData;

    NotifyPropertyChange UiDSP = new NotifyPropertyChange("1021");
    NotifyPropertyChange emergency = new NotifyPropertyChange("1000");
    NotifyPropertyChange csPLCComm = new NotifyPropertyChange("1001");
    NotifyPropertyChange csPowerMeterComm = new NotifyPropertyChange("1002");
    NotifyPropertyChange csChargerLeak = new NotifyPropertyChange("1003");
    NotifyPropertyChange csCarLeak = new NotifyPropertyChange("1004");
    NotifyPropertyChange csOutOVR = new NotifyPropertyChange("1005");
    NotifyPropertyChange csOutOCR = new NotifyPropertyChange("1006");
    NotifyPropertyChange csCouplerTempSensor = new NotifyPropertyChange("1007");
    NotifyPropertyChange csCouplerOVT = new NotifyPropertyChange("1008");
    NotifyPropertyChange csUnPlug = new NotifyPropertyChange("1009");

    //Warning
    NotifyPropertyChange csModule1Error = new NotifyPropertyChange("1100");
    NotifyPropertyChange csModule2Error = new NotifyPropertyChange("1101");
    NotifyPropertyChange csModule3Error = new NotifyPropertyChange("1102");
    NotifyPropertyChange csModule4Error = new NotifyPropertyChange("1103");
    NotifyPropertyChange csModule1Comm = new NotifyPropertyChange("1104");
    NotifyPropertyChange csModule2Comm = new NotifyPropertyChange("1105");
    NotifyPropertyChange csModule3Comm = new NotifyPropertyChange("1106");
    NotifyPropertyChange csModule4Comm = new NotifyPropertyChange("1107");


    public int getCh() {
        return ch;
    }

    public void setCh(int ch) {
        this.ch = ch;
    }

    public NotifyFaultCheck(int ch) {
        this.ch = ch;
        processHandler = ((MainActivity) MainActivity.mContext).getProcessHandler();
        socketReceiveMessage = ((MainActivity) MainActivity.mContext).getSocketReceiveMessage();
    }


    public void onErrorMessageMake(RxData rxData) {
        try {
            chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(getCh());
            chargingCurrentData.faultMessage = new StringBuilder();
            onFaultDetect(rxData);
            if (rxData.csFault || ((MainActivity) MainActivity.mContext).getControlBoard().isDisconnected()) {
                if (((MainActivity) MainActivity.mContext).getControlBoard().isDisconnected())
                    chargingCurrentData.faultMessage.append("UI-Control Board 통신오류\n");
                if (rxData.csEmergency) chargingCurrentData.faultMessage.append("비상정지\n");
                if (rxData.csPLCComm) chargingCurrentData.faultMessage.append("PLC 통신에러\n");
                if (rxData.csPowerMeterComm)
                    chargingCurrentData.faultMessage.append("PowerMerer 에러\n");
                if (rxData.csChargerLeak) chargingCurrentData.faultMessage.append("충전기 누설\n");
                if (rxData.csCarLeak) chargingCurrentData.faultMessage.append("차량 누설\n");
                if (rxData.csOutOVR) chargingCurrentData.faultMessage.append("OVR 에러\n");
                if (rxData.csOutOCR) chargingCurrentData.faultMessage.append("OCR 에러\n");
                if (rxData.csCouplerTempSensor)
                    chargingCurrentData.faultMessage.append("커플러 온도센서 에러\n");
                if (rxData.csCouplerOVT) chargingCurrentData.faultMessage.append("커플러 온도 이상\n");


            }

        } catch (Exception e) {
            logger.error("onErrorMessageMake error : {} ", e.getMessage());
        }

    }


    private void onFaultDetect(RxData rxData) {
        try {
            boolean disconnected = ((MainActivity) MainActivity.mContext).getControlBoard().isDisconnected();    //true ==> fault 발생

            if (!Objects.equals(UiDSP.ResultCompare, disconnected)) {
                UiDSP.setResultCompare(disconnected);
                if (disconnected) {
                    //발생
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.EVCommunicationError);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Faulted);
                    processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                            getCh() + 1,
                            0,
                            null,
                            null,
                            UiDSP.alarmCode,
                            false));
                } else {
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.NoError);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Available);
                    processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                            getCh() + 1,
                            0,
                            null,
                            null,
                            null,
                            false));
                }
            }

            //unPlug check ( rxData.csPilot == true : plug)
            if (!Objects.equals(csUnPlug.ResultCompare, rxData.csPilot)) {
                csUnPlug.setResultCompare(rxData.csPilot);
                if (!rxData.csPilot) {
                    //unPlug
                    boolean isPlugStatus = Objects.equals(chargingCurrentData.getChargePointStatus(), ChargePointStatus.Finishing) ||
                            Objects.equals(chargingCurrentData.getChargePointStatus(), ChargePointStatus.Preparing) ||
                            Objects.equals(chargingCurrentData.getChargePointStatus(), ChargePointStatus.Charging);
                    if (isPlugStatus) {
                        chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.NoError);
                        chargingCurrentData.setChargePointStatus(ChargePointStatus.Available);
                        processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                                GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                                chargingCurrentData.getConnectorId(),
                                0,
                                null,
                                null,
                                null,
                                false));
                    }
                } else {
                    if (Objects.equals(chargingCurrentData.getChargePointStatus(), ChargePointStatus.Available)) {
                        chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.NoError);
                        chargingCurrentData.setChargePointStatus(ChargePointStatus.Preparing);
                        processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                                GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                                getCh() + 1,
                                0,
                                null,
                                null,
                                null,
                                false));
                    }
                }
            }

            if (!Objects.equals(emergency.ResultCompare, rxData.csEmergency)) {
                emergency.setResultCompare(rxData.csEmergency);
                if (rxData.csEmergency) {
                    //발생
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.OtherError);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Faulted);
                    processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                            getCh() + 1,
                            0,
                            null,
                            null,
                            emergency.alarmCode,
                            false));
                } else {
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.NoError);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Available);
                }
            }

//            if (!Objects.equals(csPLCComm.ResultCompare, rxData.csPLCComm)){
//                csPLCComm.setResultCompare(rxData.csPLCComm);
//                if (rxData.csPLCComm){
//                    //발생
//                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.OtherError);
//                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Faulted);
//                    processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
//                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
//                            getCh() + 1,
//                            0,
//                            null,
//                            null,
//                            csPLCComm.alarmCode,
//                            false));
//                }else {
//                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.NoError);
//                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Available);
//                }
//            }
//            if (!Objects.equals(csPowerMeterComm.ResultCompare, rxData.csPowerMeterComm)){
//                csPowerMeterComm.setResultCompare(rxData.csPowerMeterComm);
//                if (rxData.csPowerMeterComm){
//                    //발생
//                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.PowerMeterFailure);
//                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Faulted);
//                    processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
//                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
//                            getCh() + 1,
//                            0,
//                            null,
//                            null,
//                            csPowerMeterComm.alarmCode,
//                            false));
//                }else {
//                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.NoError);
//                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Available);
//                }
//            }

            //csChargerLeak
            if (!Objects.equals(csChargerLeak.ResultCompare, rxData.csChargerLeak)) {
                csChargerLeak.setResultCompare(rxData.csChargerLeak);
                if (rxData.csChargerLeak) {
                    //발생
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.OtherError);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Faulted);
                    processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                            getCh() + 1,
                            0,
                            null,
                            null,
                            csChargerLeak.alarmCode,
                            false));
                } else {
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.NoError);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Available);
                }
            }
            //csCarLeak
            if (!Objects.equals(csCarLeak.ResultCompare, rxData.csCarLeak)) {
                csCarLeak.setResultCompare(rxData.csCarLeak);
                if (rxData.csCarLeak) {
                    //발생
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.OtherError);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Faulted);
                    processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                            getCh() + 1,
                            0,
                            null,
                            null,
                            csCarLeak.alarmCode,
                            false));
                } else {
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.NoError);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Available);
                }
            }
            //csOutOVR
            if (!Objects.equals(csOutOVR.ResultCompare, rxData.csOutOVR)) {
                csOutOVR.setResultCompare(rxData.csOutOVR);
                if (rxData.csOutOVR) {
                    //발생
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.OverVoltage);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Faulted);
                    processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                            getCh() + 1,
                            0,
                            null,
                            null,
                            csOutOVR.alarmCode,
                            false));
                } else {
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.NoError);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Available);
                }
            }
            // csOutOCR
            if (!Objects.equals(csOutOCR.ResultCompare, rxData.csOutOCR)) {
                csOutOCR.setResultCompare(rxData.csOutOCR);
                if (rxData.csOutOCR) {
                    //발생
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.OverCurrentFailure);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Faulted);
                    processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                            getCh() + 1,
                            0,
                            null,
                            null,
                            csOutOCR.alarmCode,
                            false));
                } else {
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.NoError);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Available);
                }
            }
            //csCouplerTempSensor
            if (!Objects.equals(csCouplerTempSensor.ResultCompare, rxData.csCouplerTempSensor)) {
                csCouplerTempSensor.setResultCompare(rxData.csCouplerTempSensor);
                if (rxData.csCouplerTempSensor) {
                    //발생
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.OtherError);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Faulted);
                    processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                            getCh() + 1,
                            0,
                            null,
                            null,
                            csCouplerTempSensor.alarmCode,
                            false));
                } else {
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.NoError);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Available);
                }
            }
            //csCouplerOVT
            if (!Objects.equals(csCouplerOVT.ResultCompare, rxData.csCouplerOVT)) {
                csCouplerOVT.setResultCompare(rxData.csCouplerOVT);
                if (rxData.csCouplerOVT) {
                    //발생
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.HighTemperature);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Faulted);
                    processHandler.sendMessage(socketReceiveMessage.onMakeHandlerMessage(
                            GlobalVariables.MESSAGE_HANDLER_STATUS_NOTIFICATION,
                            getCh() + 1,
                            0,
                            null,
                            null,
                            csCouplerOVT.alarmCode,
                            false));
                } else {
                    chargingCurrentData.setChargePointErrorCode(ChargePointErrorCode.NoError);
                    chargingCurrentData.setChargePointStatus(ChargePointStatus.Available);
                }
            }
//            if(!Objects.equals(csModule1Error.ResultCompare, rxData.csModule1Error)){
//                csModule1Error.setResultCompare(rxData.csModule1Error);
//                if (rxData.csModule1Error){
//                    //발생
//                    ((MainActivity)MainActivity.mContext).getToastPositionMake().onShowToast(getCh(),"PowerModule 1 Error");
//                }
//            }
//            if(!Objects.equals(csModule2Error.ResultCompare, rxData.csModule2Error)){
//                csModule2Error.setResultCompare(rxData.csModule1Error);
//                if (rxData.csModule2Error){
//                    //발생
//                    ((MainActivity)MainActivity.mContext).getToastPositionMake().onShowToast(getCh(),"PowerModule 2 Error");
//                }
//            }
//            if(!Objects.equals(csModule3Error.ResultCompare, rxData.csModule3Error)){
//                csModule3Error.setResultCompare(rxData.csModule3Error);
//                if (rxData.csModule3Error){
//                    //발생
//                    ((MainActivity)MainActivity.mContext).getToastPositionMake().onShowToast(getCh(),"PowerModule 3 Error");
//                }
//            }
//            if(!Objects.equals(csModule4Error.ResultCompare, rxData.csModule4Error)){
//                csModule4Error.setResultCompare(rxData.csModule4Error);
//                if (rxData.csModule4Error){
//                    //발생
//                    ((MainActivity)MainActivity.mContext).getToastPositionMake().onShowToast(getCh(),"PowerModule 4 Error");
//                }
//            }
//            if(!Objects.equals(csModule1Comm.ResultCompare, rxData.csModule1Comm)){
//                csModule1Comm.setResultCompare(rxData.csModule1Comm);
//                if (rxData.csModule1Comm){
//                    //발생
//                    ((MainActivity)MainActivity.mContext).getToastPositionMake().onShowToast(getCh(),"PowerModule 1 통신 오류");
//                }
//            }
//            if(!Objects.equals(csModule2Comm.ResultCompare, rxData.csModule2Comm)){
//                csModule2Comm.setResultCompare(rxData.csModule2Comm);
//                if (rxData.csModule2Comm){
//                    //발생
//                    ((MainActivity)MainActivity.mContext).getToastPositionMake().onShowToast(getCh(),"PowerModule 2 통신 오류");
//                }
//            }
//            if(!Objects.equals(csModule3Comm.ResultCompare, rxData.csModule3Comm)){
//                csModule3Comm.setResultCompare(rxData.csModule3Comm);
//                if (rxData.csModule3Comm){
//                    //발생
//                    ((MainActivity)MainActivity.mContext).getToastPositionMake().onShowToast(getCh(),"PowerModule 3 통신 오류");
//                }
//            }
//            if(!Objects.equals(csModule4Comm.ResultCompare, rxData.csModule4Comm)){
//                csModule4Comm.setResultCompare(rxData.csModule4Comm);
//                if (rxData.csModule4Comm){
//                    //발생
//                    ((MainActivity)MainActivity.mContext).getToastPositionMake().onShowToast(getCh(),"PowerModule 4 통신 오류");
//                }
//            }

        } catch (Exception e) {
            logger.error("onFaultDetect error : {} ", e.getMessage());
        }
    }
}

package com.dongah.fastcharger.controlboard;

import com.dongah.fastcharger.utils.BitUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RxData {

    private static final Logger logger = LoggerFactory.getLogger(RxData.class);

    private static final int RX_DATA_CNT = 46;

    // 400 address
    public boolean csPilot = false;         // 1bit
    public boolean csStart = false;         // 4bit
    public boolean csStop = false;          // 5bit
    public boolean csFault = false;         // 6bit
    public boolean csRY1Status = false;     // 8bit
    public boolean csRY2Status = false;     // 9bit
    public boolean csRY3Status = false;     // 10bit
    public boolean csRY4Status = false;     // 11bit
    public boolean csRY5Status = false;     // 12bit
    public boolean csRY6Status = false;     // 13bit
    public boolean csMC1Status = false;     // 14bit
    public boolean csMC2Status = false;     // 15bit


    // 401 address
    public short reserved0 = 0;              // reserve addresss
    // 402 address
    public short cpVoltage = 0;             // cp 전압 (ex) 1198 => 119.8
    // 403 address
    public short firmWareVersion = 0;       // firmware version (ex) 121 => 1.2.1
    // 404 address
    public short remainTime = 0;            // 충전 남은 시간
    // 405 address
    public short soc = 0;                   // soc
    // 406 address
    public boolean csMc1Fault = false;        // 406[0] 0: 정상, 1:비정상(오픈 또는 융착)
    public boolean csMc2Fault = false;        // 406[1] 0: 정상, 1:비정상(오픈 또는 융착)
    public boolean csRelay1 = false;          // 406[2] 0: 정상, 1:비정상(오픈 또는 융착)
    public boolean csRelay2 = false;          // 406[3] 0: 정상, 1:비정상(오픈 또는 융착)
    public boolean csRelay3 = false;          // 406[4] 0: 정상, 1:비정상(오픈 또는 융착)
    public boolean csRelay4 = false;          // 406[5] 0: 정상, 1:비정상(오픈 또는 융착)
    public boolean csRelay5 = false;          // 406[6] 0: 정상, 1:비정상(오픈 또는 융착)
    public boolean csRelay6 = false;          // 406[7] 0: 정상, 1:비정상(오픈 또는 융착)
    // 407 ~ 408 address
    public long powerMeter = 0;             // wH
    //409
    public short outVoltage = 0;            // 0.1V
    //410
    public short outCurrent = 0;            // 0.1A
    //411 address
    public boolean csEmergency = false;         // 411[0] Emergency
    public boolean csPLCComm = false;           // 411[1] PLC 통신 오류
    public boolean csPowerMeterComm = false;    // 411[2] power meter 통신 오루
    public boolean csChargerLeak = false;       // 411[7] 충전기 누설 감지
    public boolean csCarLeak = false;           // 411[8] 차량 누설 감지
    public boolean csOutOVR = false;            // 411[9] 출력 과전압(정격의 110%)
    public boolean csOutOCR = false;            // 411[10] 출력 과전류(정격의 110%)
    public boolean csCouplerTempSensor = false; // 411[11] 커플러 온도 센서 이상
    public boolean csCouplerOVT = false;        // 411[12] 커플러 과온도
    //412 address
    public boolean csModule1Error = false;      // 412[1] 모듈1 에러
    public boolean csModule2Error = false;      // 412[2] 모듈2 에러
    public boolean csModule3Error = false;      // 412[3] 모듈3 에러
    public boolean csModule4Error = false;      // 412[4] 모듈4 에러
    public boolean csModule1Comm = false;       // 411[3] 모듈1 통신 오류
    public boolean csModule2Comm = false;       // 411[4] 모듈2 통신 오류
    public boolean csModule3Comm = false;       // 411[5] 모듈3 통신 오류
    public boolean csModule4Comm = false;       // 411[6] 모듈4 통신 오류

    //413 address
    public short reserved1 = 0;
    // 414 address
    public short couplerTemp = 0;               // 커플러 온도 1 => 1℃


    /**
     * 415 ~ 418 (CSM Status)
     */
    public byte csmReady = 0x00;  // CSM Ready => 0: false / 1:true
    public byte csmStatusCode = 0x00; // CSM_STATUS_NONE = 0             CSM_STATUS_READY = 1
    // CSM_STATUS_WAIT_HANDSHAKE = 2   CSM_STATUS_SESSION_READY = 3
    // CSM_STATUS_AUTH_CHECK = 4       CSM_STATUS_CHARGE_PARAM_CHECK = 5
    // CSM_STATUS_CABLE_CHECK = 6      CSM_STATUS_PRE_CHARGE = 7
    // CSM_STATUS_CHARGING = 8         CSM_STATUS_STOP_CHARGING = 9
    // CSM_STATUS_FAULT_STOPPED = 10   CSM_STATUS_NORMAL_STOPPED = 11
    public byte csmErrorCode = 0x00;  // refer to Annex A
    public short csmReserved0 = 0;
    public short csmPwmDutyCycle = 0; // IEC 61851 PWM duty(%) value
    public byte csmPwmVoltage = 0x00; // IEC 61851 PWM voltage(v)
    /**
     * 419 ~ 422 (Vehicle EvccId)
     */
    public long csmVehicleEvccId = 0L;    // Specifies the EV’s identification in a readable format
    /**
     * 423 ~ 426 (Vehicle Charging Service)
     */
    public byte csmSelectedPaymentOption = 0x00;  // 0x00: Using EIM / 0x01: Using PnC
    public byte csmRequestedEnergy = 0x00; //Available charging types or methods,Supported by the EVSE
    //0x00: AC_SINGLE_PHASE_CORE  0x01: AC_THREE_PHASE_CORE
    //0x02: DC_CORE               0x03: DC_EXTENDED
    //0x04: DC_COMBO_CORE         0x05: DC_DUAL
    public short csmMaxEntriesSAScheduleTuple = 0; // [Optional] Indicates the maximal number of entries in the SAScheduleTuple
    public int csmDepartureTime = 0; // [Optional] This element is used to indicate when the vehicle intends to finish the charging process.
    //            Offset in seconds from the point in time of sending this message.

    /**
     * 427 ~ 430 (Vehicle_DC_Charging_Status)
     */
    public byte csmReserved1 = 0x00;
    public boolean csBulkChargingComplete = false;      //[0]
    public boolean csFullChargingComplete = false;      //[1]
    public boolean csEVReady = false;                   //[2]
    public boolean csEVCabinConditioning = false;       //[3]
    public boolean csEVRessConditioning = false;        //[4]
    public byte csEvErrorCode = 0x00;   // EV internal status
    // 1: FAILED_RESSTemperatureLeInhibit    2: FAILED_EVShiftPosition
    //3: FAILED_ChargerConnectorLockFault    4: FAILED_EVRESSMalfunction
    //5: FAILED_ChargingCurrentDifferential  6: FAILED_ChargingVoltageOutOfRange
    //7: Reserved_A    8: Reserved_B    9: Reserved_C
    //10: FAILED_ChargingSystem Incompatibility   11: NoData
    public byte csmRessSoc = 0x00;       // State of charge of the EV’s battery (SOC)
    public short csmRemainingTimeFullSoc = 0; // [Optional]  Estimated or calculated time until full charge (100 % SOC) is complete
    public short csmRemainingTimeBulkSoc = 0; // [Optional] Estimated or calculated time until bulk charge (approx.. 80 % SOC) is complete
    /**
     * 431 ~ 434 ( Vehicle_DC_Charging_Variable)
     */
    public short csmEVTargetCurrent = 0;        // Instantaneous current requested by the EV
    public short csmEVTargetVoltage = 0;        // Target voltage requested by the EV
    public short csmEVSEMaximumCurrentLimit = 0;    //Maximum current allowed by the EV
    public short csmEVSEMaximumVoltageLimit = 0;    //Maximum voltage allowed by the EV
    /**
     * 435 ~ 439 (Vehicle_DC_Charge_Parameter)
     */
    public short csmEVEnergyCapacity = 0;   //[Optional] Maximum energy capacity supported by the EV
    public short csmEVEnergyRequest = 0;    //[Optional] Amount of energy the EV requests from the EVSE
    public short csmEVMaximumPowerLimit = 0; // [Optional] Maximum power supported by the EV
    public short csmFullSOC = 0;            // [Optional] SOC at which EV considers the battery to be fully charged
    public short csmBulkSOC = 0;            //[Optional] SOC at which EV considers a fast charging process to end
    /**
     * 440 ~ 442 (Vehicle_AC_Charge_Parameter)
     */
    public short csmEVmount = 0;        //
    public short csmEVMaxVoltage = 0;
    public short csmEVMaxCurrent = 0;
    public short csmEVMinCurrent = 0;
    public short csmReserved2 = 0;
    public short csmReserved3 = 0;


    public void Decode(short[] data) {
        try {
            // 400 address
            csPilot = BitUtilities.getBitBoolean(data[0], 1);
            csStart = BitUtilities.getBitBoolean(data[0], 4);
            csStop = BitUtilities.getBitBoolean(data[0], 5);
            csFault = BitUtilities.getBitBoolean(data[0], 6);
            csRY1Status = BitUtilities.getBitBoolean(data[0], 8);
            csRY2Status = BitUtilities.getBitBoolean(data[0], 9);
            csRY3Status = BitUtilities.getBitBoolean(data[0], 10);
            csRY4Status = BitUtilities.getBitBoolean(data[0], 11);
            csRY5Status = BitUtilities.getBitBoolean(data[0], 12);
            csRY6Status = BitUtilities.getBitBoolean(data[0], 13);
            csMC1Status = BitUtilities.getBitBoolean(data[0], 14);
            csMC2Status = BitUtilities.getBitBoolean(data[0], 15);

            //종합 경보
//            csFault = csPLCComm || csPowerMeterComm;


            reserved0 = data[1];
            cpVoltage = data[2];
            firmWareVersion = data[3];
            remainTime = data[4];
            soc = data[5];
            csMc1Fault = BitUtilities.getBitBoolean(data[6], 0);
            csMc2Fault = BitUtilities.getBitBoolean(data[6], 1);
            csRelay1 = BitUtilities.getBitBoolean(data[6], 2);
            csRelay2 = BitUtilities.getBitBoolean(data[6], 3);
            csRelay3 = BitUtilities.getBitBoolean(data[6], 4);
            csRelay4 = BitUtilities.getBitBoolean(data[6], 5);
            csRelay5 = BitUtilities.getBitBoolean(data[6], 6);
            csRelay6 = BitUtilities.getBitBoolean(data[6], 7);

            powerMeter = (data[7] << 16) | (data[8] & 0xffff);      //1w
            outVoltage = data[9];                                   //0.1V
            outCurrent = data[10];                                  //0.1A

            csEmergency = BitUtilities.getBitBoolean(data[11], 0);
            csPLCComm = BitUtilities.getBitBoolean(data[11], 1);
            csPowerMeterComm = BitUtilities.getBitBoolean(data[11], 2);

            csChargerLeak = BitUtilities.getBitBoolean(data[11], 7);
            csCarLeak = BitUtilities.getBitBoolean(data[11], 8);
            csOutOVR = BitUtilities.getBitBoolean(data[11], 9);
            csOutOCR = BitUtilities.getBitBoolean(data[11], 10);
            csCouplerTempSensor = BitUtilities.getBitBoolean(data[11], 11);
            csCouplerOVT = BitUtilities.getBitBoolean(data[11], 12);

            csModule1Error = BitUtilities.getBitBoolean(data[12], 0);
            csModule2Error = BitUtilities.getBitBoolean(data[12], 1);
            csModule3Error = BitUtilities.getBitBoolean(data[12], 2);
            csModule4Error = BitUtilities.getBitBoolean(data[12], 3);
            csModule1Comm = BitUtilities.getBitBoolean(data[12], 4);
            csModule2Comm = BitUtilities.getBitBoolean(data[12], 5);
            csModule3Comm = BitUtilities.getBitBoolean(data[12], 6);
            csModule4Comm = BitUtilities.getBitBoolean(data[12], 7);
            reserved1 = data[13];
            couplerTemp = data[14];                 //커플러 온도 1 => 1℃

            /** plc model information */
            //CAN ID : 0x30001 CSM Status
            byte[] parsData, parseNext;
            parsData = BitUtilities.ShortToByteArray(data[15]);
            csmReady = parsData[0];
            csmStatusCode = parsData[1];
            parsData = BitUtilities.ShortToByteArray(data[16]);
            csmErrorCode = parsData[0];

            parseNext = BitUtilities.ShortToByteArray(data[17]);
            csmReserved0 = BitUtilities.ByteArrayToShort(parsData[1], parseNext[0]);

            parsData = BitUtilities.ShortToByteArray(data[18]);
            csmPwmDutyCycle = BitUtilities.ByteArrayToShort(parseNext[1], parsData[0]);
            csmPwmVoltage = parsData[1];

            //CAN ID : 0x30002 Vehicle EvccId
            csmVehicleEvccId = (((long) data[19] << 32) | ((long) data[20] << 24) | (data[21] << 16) | (data[22] & 0xffff));

            //CAN ID : 0x30003 Vehicle Charging Service
            parsData = BitUtilities.ShortToByteArray(data[23]);
            csmSelectedPaymentOption = parsData[0];
            csmRequestedEnergy = parsData[1];
            csmMaxEntriesSAScheduleTuple = data[24];
            csmDepartureTime = (data[25] << 16) | (data[26] & 0xffff);

            //CAN ID : 0x30004 Vehicle DC Charging Status
            parsData = BitUtilities.ShortToByteArray(data[27]);
            csmReserved1 = parsData[0];
            csBulkChargingComplete = BitUtilities.getBitBoolean(parsData[1], 0);
            csFullChargingComplete = BitUtilities.getBitBoolean(parsData[1], 1);
            csEVReady = BitUtilities.getBitBoolean(parsData[1], 2);
            csEVCabinConditioning = BitUtilities.getBitBoolean(parsData[1], 3);
            csEVRessConditioning = BitUtilities.getBitBoolean(parsData[1], 4);
            parsData = BitUtilities.ShortToByteArray(data[28]);
            csEvErrorCode = parsData[0];
            csmRessSoc = parsData[1];
            csmRemainingTimeFullSoc = data[29];
            csmRemainingTimeBulkSoc = data[30];
            //CAN ID : 0x30005 Vehicle DC Charging Variable
            csmEVTargetCurrent = data[31];
            csmEVTargetVoltage = data[32];
            csmEVSEMaximumCurrentLimit = data[33];
            csmEVSEMaximumVoltageLimit = data[34];
            //CAN ID : 0x30006 Vehicle DC Charging Parameter
            csmEVEnergyCapacity = data[35];
            csmEVEnergyRequest = data[36];
            csmEVMaximumPowerLimit = data[37];
            csmFullSOC = data[38];
            csmBulkSOC = data[39];
            //CAN ID : 0x30009 Vehicle AC Charging Parameter
            csmEVmount = data[40];
            csmEVMaxVoltage = data[41];
            csmEVMaxCurrent = data[42];
            csmEVMinCurrent = data[43];
            //reserved
            csmReserved2 = data[44];
            csmReserved3 = data[45];
        } catch (Exception e) {
            logger.error("rx dara decode error : {}", e.getMessage());
        }
    }

    public boolean isCsPilot() {
        return csPilot;
    }

    public void setCsPilot(boolean csPilot) {
        this.csPilot = csPilot;
    }

    public boolean isCsStart() {
        return csStart;
    }

    public void setCsStart(boolean csStart) {
        this.csStart = csStart;
    }

    public boolean isCsStop() {
        return csStop;
    }

    public void setCsStop(boolean csStop) {
        this.csStop = csStop;
    }

    public boolean isCsFault() {
        return csFault;
    }

    public void setCsFault(boolean csFault) {
        this.csFault = csFault;
    }

    public boolean isCsRY1Status() {
        return csRY1Status;
    }

    public void setCsRY1Status(boolean csRY1Status) {
        this.csRY1Status = csRY1Status;
    }

    public boolean isCsRY2Status() {
        return csRY2Status;
    }

    public void setCsRY2Status(boolean csRY2Status) {
        this.csRY2Status = csRY2Status;
    }

    public boolean isCsRY3Status() {
        return csRY3Status;
    }

    public void setCsRY3Status(boolean csRY3Status) {
        this.csRY3Status = csRY3Status;
    }

    public boolean isCsRY4Status() {
        return csRY4Status;
    }

    public void setCsRY4Status(boolean csRY4Status) {
        this.csRY4Status = csRY4Status;
    }

    public boolean isCsRY5Status() {
        return csRY5Status;
    }

    public void setCsRY5Status(boolean csRY5Status) {
        this.csRY5Status = csRY5Status;
    }

    public boolean isCsRY6Status() {
        return csRY6Status;
    }

    public void setCsRY6Status(boolean csRY6Status) {
        this.csRY6Status = csRY6Status;
    }

    public boolean isCsMC1Status() {
        return csMC1Status;
    }

    public void setCsMC1Status(boolean csMC1Status) {
        this.csMC1Status = csMC1Status;
    }

    public boolean isCsMC2Status() {
        return csMC2Status;
    }

    public void setCsMC2Status(boolean csMC2Status) {
        this.csMC2Status = csMC2Status;
    }

    public short getReserved0() {
        return reserved0;
    }

    public void setReserved0(short reserved0) {
        this.reserved0 = reserved0;
    }

    public short getCpVoltage() {
        return cpVoltage;
    }

    public void setCpVoltage(short cpVoltage) {
        this.cpVoltage = cpVoltage;
    }

    public short getFirmWareVersion() {
        return firmWareVersion;
    }

    public void setFirmWareVersion(short firmWareVersion) {
        this.firmWareVersion = firmWareVersion;
    }

    public short getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(short remainTime) {
        this.remainTime = remainTime;
    }

    public short getSoc() {
        return soc;
    }

    public void setSoc(short soc) {
        this.soc = soc;
    }

    public boolean isCsMc1Fault() {
        return csMc1Fault;
    }

    public void setCsMc1Fault(boolean csMc1Fault) {
        this.csMc1Fault = csMc1Fault;
    }

    public boolean isCsMc2Fault() {
        return csMc2Fault;
    }

    public void setCsMc2Fault(boolean csMc2Fault) {
        this.csMc2Fault = csMc2Fault;
    }

    public boolean isCsRelay1() {
        return csRelay1;
    }

    public void setCsRelay1(boolean csRelay1) {
        this.csRelay1 = csRelay1;
    }

    public boolean isCsRelay2() {
        return csRelay2;
    }

    public void setCsRelay2(boolean csRelay2) {
        this.csRelay2 = csRelay2;
    }

    public boolean isCsRelay3() {
        return csRelay3;
    }

    public void setCsRelay3(boolean csRelay3) {
        this.csRelay3 = csRelay3;
    }

    public boolean isCsRelay4() {
        return csRelay4;
    }

    public void setCsRelay4(boolean csRelay4) {
        this.csRelay4 = csRelay4;
    }

    public boolean isCsRelay5() {
        return csRelay5;
    }

    public void setCsRelay5(boolean csRelay5) {
        this.csRelay5 = csRelay5;
    }

    public boolean isCsRelay6() {
        return csRelay6;
    }

    public void setCsRelay6(boolean csRelay6) {
        this.csRelay6 = csRelay6;
    }

    public long getPowerMeter() {
        return powerMeter;
    }

    public void setPowerMeter(long powerMeter) {
        this.powerMeter = powerMeter;
    }

    public short getOutVoltage() {
        return outVoltage;
    }

    public void setOutVoltage(short outVoltage) {
        this.outVoltage = outVoltage;
    }

    public short getOutCurrent() {
        return outCurrent;
    }

    public void setOutCurrent(short outCurrent) {
        this.outCurrent = outCurrent;
    }

    public boolean isCsEmergency() {
        return csEmergency;
    }

    public void setCsEmergency(boolean csEmergency) {
        this.csEmergency = csEmergency;
    }

    public boolean isCsPLCComm() {
        return csPLCComm;
    }

    public void setCsPLCComm(boolean csPLCComm) {
        this.csPLCComm = csPLCComm;
    }

    public boolean isCsPowerMeterComm() {
        return csPowerMeterComm;
    }

    public void setCsPowerMeterComm(boolean csPowerMeterComm) {
        this.csPowerMeterComm = csPowerMeterComm;
    }

    public boolean isCsModule1Comm() {
        return csModule1Comm;
    }

    public void setCsModule1Comm(boolean csModule1Comm) {
        this.csModule1Comm = csModule1Comm;
    }

    public boolean isCsModule2Comm() {
        return csModule2Comm;
    }

    public void setCsModule2Comm(boolean csModule2Comm) {
        this.csModule2Comm = csModule2Comm;
    }

    public boolean isCsModule3Comm() {
        return csModule3Comm;
    }

    public void setCsModule3Comm(boolean csModule3Comm) {
        this.csModule3Comm = csModule3Comm;
    }

    public boolean isCsModule4Comm() {
        return csModule4Comm;
    }

    public void setCsModule4Comm(boolean csModule4Comm) {
        this.csModule4Comm = csModule4Comm;
    }

    public boolean isCsChargerLeak() {
        return csChargerLeak;
    }

    public void setCsChargerLeak(boolean csChargerLeak) {
        this.csChargerLeak = csChargerLeak;
    }

    public boolean isCsCarLeak() {
        return csCarLeak;
    }

    public void setCsCarLeak(boolean csCarLeak) {
        this.csCarLeak = csCarLeak;
    }

    public boolean isCsOutOVR() {
        return csOutOVR;
    }

    public void setCsOutOVR(boolean csOutOVR) {
        this.csOutOVR = csOutOVR;
    }

    public boolean isCsOutOCR() {
        return csOutOCR;
    }

    public void setCsOutOCR(boolean csOutOCR) {
        this.csOutOCR = csOutOCR;
    }

    public boolean isCsCouplerTempSensor() {
        return csCouplerTempSensor;
    }

    public void setCsCouplerTempSensor(boolean csCouplerTempSensor) {
        this.csCouplerTempSensor = csCouplerTempSensor;
    }

    public boolean isCsCouplerOVT() {
        return csCouplerOVT;
    }

    public void setCsCouplerOVT(boolean csCouplerOVT) {
        this.csCouplerOVT = csCouplerOVT;
    }

    public boolean isCsModule1Error() {
        return csModule1Error;
    }

    public void setCsModule1Error(boolean csModule1Error) {
        this.csModule1Error = csModule1Error;
    }

    public boolean isCsModule2Error() {
        return csModule2Error;
    }

    public void setCsModule2Error(boolean csModule2Error) {
        this.csModule2Error = csModule2Error;
    }

    public boolean isCsModule3Error() {
        return csModule3Error;
    }

    public void setCsModule3Error(boolean csModule3Error) {
        this.csModule3Error = csModule3Error;
    }

    public boolean isCsModule4Error() {
        return csModule4Error;
    }

    public void setCsModule4Error(boolean csModule4Error) {
        this.csModule4Error = csModule4Error;
    }

    public short getReserved1() {
        return reserved1;
    }

    public void setReserved1(short reserved1) {
        this.reserved1 = reserved1;
    }

    public short getCouplerTemp() {
        return couplerTemp;
    }

    public void setCouplerTemp(short couplerTemp) {
        this.couplerTemp = couplerTemp;
    }

    public byte getCsmReady() {
        return csmReady;
    }

    public void setCsmReady(byte csmReady) {
        this.csmReady = csmReady;
    }

    public byte getCsmStatusCode() {
        return csmStatusCode;
    }

    public void setCsmStatusCode(byte csmStatusCode) {
        this.csmStatusCode = csmStatusCode;
    }

    public byte getCsmErrorCode() {
        return csmErrorCode;
    }

    public void setCsmErrorCode(byte csmErrorCode) {
        this.csmErrorCode = csmErrorCode;
    }

    public short getCsmReserved0() {
        return csmReserved0;
    }

    public void setCsmReserved0(short csmReserved0) {
        this.csmReserved0 = csmReserved0;
    }

    public short getCsmPwmDutyCycle() {
        return csmPwmDutyCycle;
    }

    public void setCsmPwmDutyCycle(short csmPwmDutyCycle) {
        this.csmPwmDutyCycle = csmPwmDutyCycle;
    }

    public byte getCsmPwmVoltage() {
        return csmPwmVoltage;
    }

    public void setCsmPwmVoltage(byte csmPwmVoltage) {
        this.csmPwmVoltage = csmPwmVoltage;
    }

    public long getCsmVehicleEvccId() {
        return csmVehicleEvccId;
    }

    public void setCsmVehicleEvccId(long csmVehicleEvccId) {
        this.csmVehicleEvccId = csmVehicleEvccId;
    }

    public byte getCsmSelectedPaymentOption() {
        return csmSelectedPaymentOption;
    }

    public void setCsmSelectedPaymentOption(byte csmSelectedPaymentOption) {
        this.csmSelectedPaymentOption = csmSelectedPaymentOption;
    }

    public byte getCsmRequestedEnergy() {
        return csmRequestedEnergy;
    }

    public void setCsmRequestedEnergy(byte csmRequestedEnergy) {
        this.csmRequestedEnergy = csmRequestedEnergy;
    }

    public short getCsmMaxEntriesSAScheduleTuple() {
        return csmMaxEntriesSAScheduleTuple;
    }

    public void setCsmMaxEntriesSAScheduleTuple(short csmMaxEntriesSAScheduleTuple) {
        this.csmMaxEntriesSAScheduleTuple = csmMaxEntriesSAScheduleTuple;
    }

    public int getCsmDepartureTime() {
        return csmDepartureTime;
    }

    public void setCsmDepartureTime(int csmDepartureTime) {
        this.csmDepartureTime = csmDepartureTime;
    }

    public byte getCsmReserved1() {
        return csmReserved1;
    }

    public void setCsmReserved1(byte csmReserved1) {
        this.csmReserved1 = csmReserved1;
    }

    public boolean isCsBulkChargingComplete() {
        return csBulkChargingComplete;
    }

    public void setCsBulkChargingComplete(boolean csBulkChargingComplete) {
        this.csBulkChargingComplete = csBulkChargingComplete;
    }

    public boolean isCsFullChargingComplete() {
        return csFullChargingComplete;
    }

    public void setCsFullChargingComplete(boolean csFullChargingComplete) {
        this.csFullChargingComplete = csFullChargingComplete;
    }

    public boolean isCsEVReady() {
        return csEVReady;
    }

    public void setCsEVReady(boolean csEVReady) {
        this.csEVReady = csEVReady;
    }

    public boolean isCsEVCabinConditioning() {
        return csEVCabinConditioning;
    }

    public void setCsEVCabinConditioning(boolean csEVCabinConditioning) {
        this.csEVCabinConditioning = csEVCabinConditioning;
    }

    public boolean isCsEVRessConditioning() {
        return csEVRessConditioning;
    }

    public void setCsEVRessConditioning(boolean csEVRessConditioning) {
        this.csEVRessConditioning = csEVRessConditioning;
    }

    public byte getCsEvErrorCode() {
        return csEvErrorCode;
    }

    public void setCsEvErrorCode(byte csEvErrorCode) {
        this.csEvErrorCode = csEvErrorCode;
    }

    public byte getCsmRessSoc() {
        return csmRessSoc;
    }

    public void setCsmRessSoc(byte csmRessSoc) {
        this.csmRessSoc = csmRessSoc;
    }

    public short getCsmRemainingTimeFullSoc() {
        return csmRemainingTimeFullSoc;
    }

    public void setCsmRemainingTimeFullSoc(short csmRemainingTimeFullSoc) {
        this.csmRemainingTimeFullSoc = csmRemainingTimeFullSoc;
    }

    public short getCsmRemainingTimeBulkSoc() {
        return csmRemainingTimeBulkSoc;
    }

    public void setCsmRemainingTimeBulkSoc(short csmRemainingTimeBulkSoc) {
        this.csmRemainingTimeBulkSoc = csmRemainingTimeBulkSoc;
    }

    public short getCsmEVTargetCurrent() {
        return csmEVTargetCurrent;
    }

    public void setCsmEVTargetCurrent(short csmEVTargetCurrent) {
        this.csmEVTargetCurrent = csmEVTargetCurrent;
    }

    public short getCsmEVTargetVoltage() {
        return csmEVTargetVoltage;
    }

    public void setCsmEVTargetVoltage(short csmEVTargetVoltage) {
        this.csmEVTargetVoltage = csmEVTargetVoltage;
    }

    public short getCsmEVSEMaximumCurrentLimit() {
        return csmEVSEMaximumCurrentLimit;
    }

    public void setCsmEVSEMaximumCurrentLimit(short csmEVSEMaximumCurrentLimit) {
        this.csmEVSEMaximumCurrentLimit = csmEVSEMaximumCurrentLimit;
    }

    public short getCsmEVSEMaximumVoltageLimit() {
        return csmEVSEMaximumVoltageLimit;
    }

    public void setCsmEVSEMaximumVoltageLimit(short csmEVSEMaximumVoltageLimit) {
        this.csmEVSEMaximumVoltageLimit = csmEVSEMaximumVoltageLimit;
    }

    public short getCsmEVEnergyCapacity() {
        return csmEVEnergyCapacity;
    }

    public void setCsmEVEnergyCapacity(short csmEVEnergyCapacity) {
        this.csmEVEnergyCapacity = csmEVEnergyCapacity;
    }

    public short getCsmEVEnergyRequest() {
        return csmEVEnergyRequest;
    }

    public void setCsmEVEnergyRequest(short csmEVEnergyRequest) {
        this.csmEVEnergyRequest = csmEVEnergyRequest;
    }

    public short getCsmEVMaximumPowerLimit() {
        return csmEVMaximumPowerLimit;
    }

    public void setCsmEVMaximumPowerLimit(short csmEVMaximumPowerLimit) {
        this.csmEVMaximumPowerLimit = csmEVMaximumPowerLimit;
    }

    public short getCsmFullSOC() {
        return csmFullSOC;
    }

    public void setCsmFullSOC(short csmFullSOC) {
        this.csmFullSOC = csmFullSOC;
    }

    public short getCsmBulkSOC() {
        return csmBulkSOC;
    }

    public void setCsmBulkSOC(short csmBulkSOC) {
        this.csmBulkSOC = csmBulkSOC;
    }

    public short getCsmEVmount() {
        return csmEVmount;
    }

    public void setCsmEVmount(short csmEVmount) {
        this.csmEVmount = csmEVmount;
    }

    public short getCsmEVMaxVoltage() {
        return csmEVMaxVoltage;
    }

    public void setCsmEVMaxVoltage(short csmEVMaxVoltage) {
        this.csmEVMaxVoltage = csmEVMaxVoltage;
    }

    public short getCsmEVMaxCurrent() {
        return csmEVMaxCurrent;
    }

    public void setCsmEVMaxCurrent(short csmEVMaxCurrent) {
        this.csmEVMaxCurrent = csmEVMaxCurrent;
    }

    public short getCsmEVMinCurrent() {
        return csmEVMinCurrent;
    }

    public void setCsmEVMinCurrent(short csmEVMinCurrent) {
        this.csmEVMinCurrent = csmEVMinCurrent;
    }

    public short getCsmReserved2() {
        return csmReserved2;
    }

    public void setCsmReserved2(short csmReserved2) {
        this.csmReserved2 = csmReserved2;
    }

    public short getCsmReserved3() {
        return csmReserved3;
    }

    public void setCsmReserved3(short csmReserved3) {
        this.csmReserved3 = csmReserved3;
    }
}
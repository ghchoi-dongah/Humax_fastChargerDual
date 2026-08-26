package com.dongah.fastcharger.websocket.ocpp.datatransfer.humax;

import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChargerPointInfoData implements Validatable {

    private static final Logger logger = LoggerFactory.getLogger(ChargerPointInfoData.class);


    private int connectorId;
    private int transactionId;
    private String rsrp;                    //신호 세기(RSRP)
    private String firmwareVersion;
    private String configVersion;           //사용자환경설정버전. key:HmCpConfigVersion ==> ConfigurationKey_HUMAX
    private String cpStatus;              //환경부 기준
    private String cableConnYn;             // [Y / N]
    private String emergencyYn;             // 비상 정지 버튼 [Y / N]
    private String cpCoolerYn;              // 냉각장치 가동여부 [Y/N]
    private int cpCoolerSpeed;           // 냉각장치 속도[RPM]
    private String cpTemperature;           // 충전기 내부온도
    private String idTag;                   // 회원/로밍카드번호
    private String eventCode;               // 이벤트발생시 이벤트 코드
    private String errorCode;               //
    private int accPower;                // 누적충전량[단위:W]
    private int usePower;                // 현재충전량[단위:W]:충전중일경우
    private String usePrice;                // 현재충전단가(비충전시:회원단가)
    private String useAmt;                  // 현재충전금액
    private String timestamp;               // 수집시간
    private String bmsSwVersion;            // BMS SW VERSION
    private String batCapacity;             // 배터리 용량
    private String batStatus;               // 배터리 상태[0:Ready,1:Warning,2:Fault,3:Run]
    private String batSoc;                  // 배터리 충전량(%)[소수점 2자리까지]
    private String batTemperature;          // 배터리 온도
    private String batEleCurrent;           // 전류[소수점 2자리까지]
    private String batVoltage;              // 전압[소수점 2자리까지]
    private String remainTime;              // 충전남은시간(00시00분00초)

    public ChargerPointInfoData() {
    }

    public int getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(int connectorId) {
        this.connectorId = connectorId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getRsrp() {
        return rsrp;
    }

    public void setRsrp(String rsrp) {
        this.rsrp = rsrp;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(String configVersion) {
        this.configVersion = configVersion;
    }

    public String getCpStatus() {
        return cpStatus;
    }

    public void setCpStatus(String cpStatus) {
        this.cpStatus = cpStatus;
    }

    public String getCableConnYn() {
        return cableConnYn;
    }

    public void setCableConnYn(String cableConnYn) {
        this.cableConnYn = cableConnYn;
    }

    public String getEmergencyYn() {
        return emergencyYn;
    }

    public void setEmergencyYn(String emergencyYn) {
        this.emergencyYn = emergencyYn;
    }

    public String getCpCoolerYn() {
        return cpCoolerYn;
    }

    public void setCpCoolerYn(String cpCoolerYn) {
        this.cpCoolerYn = cpCoolerYn;
    }

    public int getCpCoolerSpeed() {
        return cpCoolerSpeed;
    }

    public void setCpCoolerSpeed(int cpCoolerSpeed) {
        this.cpCoolerSpeed = cpCoolerSpeed;
    }

    public String getCpTemperature() {
        return cpTemperature;
    }

    public void setCpTemperature(String cpTemperature) {
        this.cpTemperature = cpTemperature;
    }

    public String getIdTag() {
        return idTag;
    }

    public void setIdTag(String idTag) {
        this.idTag = idTag;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getAccPower() {
        return accPower;
    }

    public void setAccPower(int accPower) {
        this.accPower = accPower;
    }

    public int getUsePower() {
        return usePower;
    }

    public void setUsePower(int usePower) {
        this.usePower = usePower;
    }

    public String getUsePrice() {
        return usePrice;
    }

    public void setUsePrice(String usePrice) {
        this.usePrice = usePrice;
    }

    public String getUseAmt() {
        return useAmt;
    }

    public void setUseAmt(String useAmt) {
        this.useAmt = useAmt;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getBmsSwVersion() {
        return bmsSwVersion;
    }

    public void setBmsSwVersion(String bmsSwVersion) {
        this.bmsSwVersion = bmsSwVersion;
    }

    public String getBatCapacity() {
        return batCapacity;
    }

    public void setBatCapacity(String batCapacity) {
        this.batCapacity = batCapacity;
    }

    public String getBatStatus() {
        return batStatus;
    }

    public void setBatStatus(String batStatus) {
        this.batStatus = batStatus;
    }

    public String getBatSoc() {
        return batSoc;
    }

    public void setBatSoc(String batSoc) {
        this.batSoc = batSoc;
    }

    public String getBatTemperature() {
        return batTemperature;
    }

    public void setBatTemperature(String batTemperature) {
        this.batTemperature = batTemperature;
    }

    public String getBatEleCurrent() {
        return batEleCurrent;
    }

    public void setBatEleCurrent(String batEleCurrent) {
        this.batEleCurrent = batEleCurrent;
    }

    public String getBatVoltage() {
        return batVoltage;
    }

    public void setBatVoltage(String batVoltage) {
        this.batVoltage = batVoltage;
    }

    public String getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(String remainTime) {
        this.remainTime = remainTime;
    }

    @Override
    public boolean validate() {
        return true;
    }
}

package com.dongah.fastcharger.basefunction;

import android.os.Environment;
import android.text.TextUtils;

import com.dongah.fastcharger.utils.FileManagement;
import com.dongah.fastcharger.websocket.ocpp.firmware.DiagnosticsStatus;
import com.dongah.fastcharger.websocket.ocpp.firmware.FirmwareStatus;
import com.dongah.fastcharger.websocket.ocpp.security.SignedFirmwareStatus;
import com.dongah.fastcharger.websocket.ocpp.security.UploadLogStatus;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ChargerConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ChargerConfiguration.class);

    public static final String CONFIG_FILE_NAME = "config";
    FileManagement fileManagement;


    public String rootPath = "";

    /**
     * TLS3800 id
     */
//    public String MID = "KIOSK1114915545";
    public String MID = "humax0001m";

    /**
     * Max Channel Count
     */
    public int maxChannel = 2;

    /**
     * 충전기 ID, 충전기 No
     */
    public String chargerId = "";


//    /**
//     * server connection string
//     */
//    public String serverConnectingString = "wss://211.44.234.112";
//    public int serverPort = 5000;

    /**
     * server connection string
     * test server : https://ocpp-stg.turucharger.com
     */
    public String serverHttpString = "https://ocpp-stg.turucharger.com";
    public String serverConnectingString = "wss://ocpp-stg.turucharger.com/ocpp";
    public int serverPort = 4000;

    /**
     * 충전기 타입
     */
    public int chargerType = 5;

    /**
     * 0:credit+member  1:member
     */
    public String selectPayment = "0";
    public int selectPaymentId;

    /**
     * 0:server 2:auto 3:test 4:PowerMetertest
     */
    public String authMode = "0";
    public int authModeId;

    /**
     * device serial port
     */
    public String controlCom = "/dev/ttyS4";
    public String rfCom = "/dev/ttyS3";     //not used
    public String CreditCom = "/dev/ttyS2";

    /**
     * 테스트 충전 단가
     */
    public String testPrice = "313.0";
    /**
     * 전류 제한 A
     */
    public int dr = 0;

    /**
     * charging point configuration setting
     */
    public String chargerPointVendor = "Dongah";
    public String chargerPointModel = "DEV0100D00";
    public int chargerPointModelCode = 0;
    public String unitPriceVendorCode = "kr.co.dongah";
    public String chargerPointSerialNumber = "";
    public String firmwareVersion = "";
    public String iccid = "";                         //this contains the ICCID of modem's SIM card
    public String imsi = "";                          //this contains the IMSI of modem's SIM card

    public String gpsX = "";
    public String gpsY = "";

    public String m2mTel = "";
    public String rfMaker = "";
    public int statusTime = 300;

    public int statusNotificationDelay = 300;

    public int targetSoc = 0;
    public int targetChargingTime = 0;

    public boolean StopConfirm;
    public boolean signed = true ;
    public FirmwareStatus firmwareStatus = FirmwareStatus.Idle;
    public SignedFirmwareStatus signedFirmwareStatus = SignedFirmwareStatus.Idle;
    public DiagnosticsStatus diagnosticsStatus = DiagnosticsStatus.Idle;
    public UploadLogStatus uploadLogStatus = UploadLogStatus.Idle;

    //Battery info data count
    public int configCnt = 10;

    public ChargerConfiguration() {
        setRootPath(Environment.getExternalStorageDirectory().toString() + File.separator + "Download");
        fileManagement = new FileManagement();
    }

    public void onLoadConfiguration() {
        try {
            File targetFile = new File(rootPath + File.separator + CONFIG_FILE_NAME);
            String configurationString;
            if (!targetFile.exists()) onSaveConfiguration();

            //get file context json string
            configurationString = fileManagement.getStringFromFile(rootPath + File.separator + CONFIG_FILE_NAME);
            if (!TextUtils.isEmpty(configurationString)) {
                JSONObject obj = new JSONObject(configurationString);
                setChargerId(obj.getString("CHARGER_ID"));
                setServerHttpString(obj.getString("SERVER_HTTP_STRING"));
                setServerConnectingString(obj.getString("SERVER_CONNECTING_STRING"));
                setServerPort(obj.getInt("SERVER_PORT"));
                setControlCom(obj.getString("CONTROL_COM"));
                setRfCom(obj.getString("RF_COM"));
                setCreditCom(obj.getString("CREDIT_COM"));
                setTestPrice(obj.getString("TEST_PRICE"));
                setSelectPayment(obj.getString("SELECT_PAYMENT"));
                setSelectPaymentId(obj.getInt("SELECT_PAYMENT_ID"));
                setAuthMode(obj.getString("AUTH_MODE"));
                setAuthModeId(obj.getInt("AUTH_MODE_ID"));
                setChargerType(obj.getInt("CHARGER_TYPE"));
                setDr(obj.getInt("DR"));
                setChargerPointModel(obj.getString("CHARGER_POINT_MODEL"));
                setChargerPointModelCode(obj.getInt("CHARGER_POINT_MODEL_CODE"));
                setChargerPointVendor(obj.getString("CHARGER_POINT_VENDOR"));
                setChargerPointSerialNumber(obj.getString("CHARGER_POINT_SERIAL_NUMBER"));
                setFirmwareVersion(obj.getString("FIRMWARE_VERSION"));
                setIccid(obj.getString("ICCID"));
                setImsi(obj.getString("IMSI"));
                setUnitPriceVendorCode(obj.getString("UNIT_PRICE_VENDOR_CODE"));
                setGpsX(obj.getString("GPS_X"));
                setGpsY(obj.getString("GPS_Y"));
                setM2mTel(obj.getString("M2M_TEL"));
                setRfMaker(obj.getString("RF_MAKER"));
                setStatusNotificationDelay(obj.getInt("statusNotificationDelay"));
                setTargetSoc(obj.getInt("TARGET_SOC"));
                setTargetChargingTime(obj.getInt("TARGET_CHARGING_TIME"));
                setStopConfirm(obj.getBoolean("STOP_CONFIRM"));

                setConfigCnt(obj.getInt("CONFIG_CNT"));
                setSigned(obj.getBoolean("SIGNED"));
            }
        } catch (Exception e) {
            logger.error("configuration load fail : {}", e.getMessage());
        }
    }

    public void onSaveConfiguration() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("CHARGER_ID", getChargerId());
            obj.put("SERVER_HTTP_STRING", getServerHttpString());
            obj.put("SERVER_CONNECTING_STRING", getServerConnectingString());
            obj.put("SERVER_PORT", getServerPort());
            obj.put("CONTROL_COM", getControlCom());
            obj.put("RF_COM", getRfCom());
            obj.put("CREDIT_COM", getCreditCom());
            obj.put("TEST_PRICE", getTestPrice());
            obj.put("SELECT_PAYMENT", getSelectPayment());
            obj.put("SELECT_PAYMENT_ID", getSelectPaymentId());
            obj.put("AUTH_MODE", getAuthMode());
            obj.put("AUTH_MODE_ID", getAuthModeId());
            obj.put("CHARGER_TYPE", getChargerType());
            obj.put("DR", getDr());
            obj.put("CHARGER_POINT_MODEL", getChargerPointModel());
            obj.put("CHARGER_POINT_MODEL_CODE", getChargerPointModelCode());
            obj.put("CHARGER_POINT_VENDOR", getChargerPointVendor());
            obj.put("CHARGER_POINT_SERIAL_NUMBER", getChargerPointSerialNumber());
            obj.put("FIRMWARE_VERSION", getFirmwareVersion());
            obj.put("ICCID", getIccid());
            obj.put("IMSI", getImsi());
            obj.put("UNIT_PRICE_VENDOR_CODE", getUnitPriceVendorCode());
            obj.put("GPS_X", getGpsX());
            obj.put("GPS_Y", getGpsY());
            obj.put("M2M_TEL", getM2mTel());
            obj.put("RF_MAKER", getRfMaker());
            obj.put("statusNotificationDelay", getStatusNotificationDelay());
            obj.put("TARGET_SOC", getTargetSoc());
            obj.put("TARGET_CHARGING_TIME", getTargetChargingTime());
            obj.put("STOP_CONFIRM", isStopConfirm());
            obj.put("CONFIG_CNT", getConfigCnt());
            obj.put("SIGNED", isSigned());
            fileManagement.stringToFileSave(rootPath, CONFIG_FILE_NAME, obj.toString(), false);
        } catch (Exception e) {
            logger.error("configuration save fail :  {}", e.getMessage());
        }
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getMID() {
        return MID;
    }

    public void setMID(String MID) {
        this.MID = MID;
    }

    public String getChargerId() {
        return chargerId;
    }

    public void setChargerId(String chargerId) {
        this.chargerId = chargerId;
    }

    public int getMaxChannel() {
        return maxChannel;
    }

    public void setMaxChannel(int maxChannel) {
        this.maxChannel = maxChannel;
    }

    public String getServerConnectingString() {
        return serverConnectingString;
    }

    public void setServerConnectingString(String serverConnectingString) {
        this.serverConnectingString = serverConnectingString;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getChargerType() {
        return chargerType;
    }

    public void setChargerType(int chargerType) {
        this.chargerType = chargerType;
    }

    public String getSelectPayment() {
        return selectPayment;
    }

    public void setSelectPayment(String selectPayment) {
        this.selectPayment = selectPayment;
    }

    public int getSelectPaymentId() {
        return selectPaymentId;
    }

    public void setSelectPaymentId(int selectPaymentId) {
        this.selectPaymentId = selectPaymentId;
    }

    public String getAuthMode() {
        return authMode;
    }

    public void setAuthMode(String authMode) {
        this.authMode = authMode;
    }

    public int getAuthModeId() {
        return authModeId;
    }

    public void setAuthModeId(int authModeId) {
        this.authModeId = authModeId;
    }

    public String getControlCom() {
        return controlCom;
    }

    public void setControlCom(String controlCom) {
        this.controlCom = controlCom;
    }

    public String getRfCom() {
        return rfCom;
    }

    public void setRfCom(String rfCom) {
        this.rfCom = rfCom;
    }

    public String getCreditCom() {
        return CreditCom;
    }

    public void setCreditCom(String creditCom) {
        CreditCom = creditCom;
    }

    public String getTestPrice() {
        return testPrice;
    }

    public void setTestPrice(String testPrice) {
        this.testPrice = testPrice;
    }

    public int getDr() {
        return dr;
    }

    public void setDr(int dr) {
        this.dr = dr;
    }

    public String getChargerPointVendor() {
        return chargerPointVendor;
    }

    public void setChargerPointVendor(String chargerPointVendor) {
        this.chargerPointVendor = chargerPointVendor;
    }

    public String getChargerPointModel() {
        return chargerPointModel;
    }

    public void setChargerPointModel(String chargerPointModel) {
        this.chargerPointModel = chargerPointModel;
    }

    public int getChargerPointModelCode() {
        return chargerPointModelCode;
    }

    public void setChargerPointModelCode(int chargerPointModelCode) {
        this.chargerPointModelCode = chargerPointModelCode;
    }

    public String getUnitPriceVendorCode() {
        return unitPriceVendorCode;
    }

    public void setUnitPriceVendorCode(String unitPriceVendorCode) {
        this.unitPriceVendorCode = unitPriceVendorCode;
    }

    public String getChargerPointSerialNumber() {
        return chargerPointSerialNumber;
    }

    public void setChargerPointSerialNumber(String chargerPointSerialNumber) {
        this.chargerPointSerialNumber = chargerPointSerialNumber;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getGpsX() {
        return gpsX;
    }

    public void setGpsX(String gpsX) {
        this.gpsX = gpsX;
    }

    public String getGpsY() {
        return gpsY;
    }

    public void setGpsY(String gpsY) {
        this.gpsY = gpsY;
    }

    public String getM2mTel() {
        return m2mTel;
    }

    public void setM2mTel(String m2mTel) {
        this.m2mTel = m2mTel;
    }

    public String getRfMaker() {
        return rfMaker;
    }

    public void setRfMaker(String rfMaker) {
        this.rfMaker = rfMaker;
    }

    public int getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(int statusTime) {
        this.statusTime = statusTime;
    }

    public int getStatusNotificationDelay() {
        return statusNotificationDelay;
    }

    public void setStatusNotificationDelay(int statusNotificationDelay) {
        this.statusNotificationDelay = statusNotificationDelay;
    }

    public int getTargetSoc() {
        return targetSoc;
    }

    public void setTargetSoc(int targetSoc) {
        this.targetSoc = targetSoc;
    }

    public int getTargetChargingTime() {
        return targetChargingTime;
    }

    public void setTargetChargingTime(int targetChargingTime) {
        this.targetChargingTime = targetChargingTime;
    }

    public boolean isStopConfirm() {
        return StopConfirm;
    }

    public void setStopConfirm(boolean stopConfirm) {
        StopConfirm = stopConfirm;
    }

    public FirmwareStatus getFirmwareStatus() {
        return firmwareStatus;
    }

    public void setFirmwareStatus(FirmwareStatus firmwareStatus) {
        this.firmwareStatus = firmwareStatus;
    }

    public DiagnosticsStatus getDiagnosticsStatus() {
        return diagnosticsStatus;
    }

    public void setDiagnosticsStatus(DiagnosticsStatus diagnosticsStatus) {
        this.diagnosticsStatus = diagnosticsStatus;
    }

    public UploadLogStatus getUploadLogStatus() {
        return uploadLogStatus;
    }

    public void setUploadLogStatus(UploadLogStatus uploadLogStatus) {
        this.uploadLogStatus = uploadLogStatus;
    }

    public SignedFirmwareStatus getSignedFirmwareStatus() {
        return signedFirmwareStatus;
    }

    public void setSignedFirmwareStatus(SignedFirmwareStatus signedFirmwareStatus) {
        this.signedFirmwareStatus = signedFirmwareStatus;
    }
    public String getServerHttpString() {
        return serverHttpString;
    }

    public void setServerHttpString(String serverHttpString) {
        this.serverHttpString = serverHttpString;
    }


    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public int getConfigCnt() {
        return configCnt;
    }

    public void setConfigCnt(int configCnt) {
        this.configCnt = configCnt;
    }

}

package com.dongah.fastcharger.basefunction;

import com.dongah.fastcharger.utils.FileManagement;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;

public class ConfigurationKeyRead {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationKeyRead.class);

    final String fileName = "ConfigurationKey";
    String configurationString;


    public ConfigurationKeyRead() {
    }



    public void onRead() {
        try {
            File file = new File(GlobalVariables.getRootPath() + File.separator + fileName);
            if (file.exists()) {
                FileManagement fileManagement = new FileManagement();
                configurationString = fileManagement.getStringFromFile(GlobalVariables.getRootPath() + File.separator + fileName);
                JSONObject jsonObjectData = new JSONObject(configurationString);
                JSONArray jsonArrayContent = jsonObjectData.getJSONArray("values");
                for (int i = 0; i < jsonArrayContent.length(); i++) {
                    JSONObject contDetail = jsonArrayContent.getJSONObject(i);
                    if (Objects.equals("ConnectionTimeOut", contDetail.getString("key"))) {
                        GlobalVariables.setConnectionTimeOut(Integer.parseInt(contDetail.getString("value")));
                    } else if (Objects.equals("MeterValueSampleInterval", contDetail.getString("key"))) {
                        GlobalVariables.setMeterValueSampleInterval(Integer.parseInt(contDetail.getString("value")));
                    } else if (Objects.equals("HeartbeatInterval", contDetail.getString("key"))) {
                        GlobalVariables.setHeartBeatInterval(Integer.parseInt(contDetail.getString("value")));
                    } else if (Objects.equals("LocalAuthListEnabled", contDetail.getString("key"))) {
                        GlobalVariables.setLocalAuthListEnabled(contDetail.getBoolean("value"));
                    } else if (Objects.equals("AuthorizeRemoteTxRequests", contDetail.getString("key"))) {
                        GlobalVariables.setAuthorizeRemoteTxRequests(Boolean.parseBoolean(contDetail.getString("value")));
                    } else if (Objects.equals("ReserveConnectorZeroSupported", contDetail.getString("key"))) {
                        GlobalVariables.setReserveConnectorZeroSupported(contDetail.getBoolean("value"));
                    } else if (Objects.equals("LocalPreAuthorize", contDetail.getString("key"))) {
                        GlobalVariables.setLocalPreAuthorize(contDetail.getBoolean("value"));
                    } else if (Objects.equals("AllowOfflineTxForUnknownId", contDetail.getString("key"))) {
                        GlobalVariables.setAllowOfflineTxForUnknownId(contDetail.getBoolean("value"));
                    } else if (Objects.equals("StopTransactionOnInvalidId", contDetail.getString("key"))) {
                        GlobalVariables.setStopTransactionOnInvalidId(contDetail.getBoolean("value"));
                    } else if (Objects.equals("StopTransactionOnEVSideDisconnect", contDetail.getString("key"))) {
                        GlobalVariables.setStopTransactionOnEVSideDisconnect(contDetail.getBoolean("value"));
                    } else if (Objects.equals("UnlockConnectorOnEVSideDisconnect", contDetail.getString("key"))) {
                        GlobalVariables.setUnlockConnectorOnEVSideDisconnect(contDetail.getBoolean("value"));
                    } else if (Objects.equals("ClockAlignedDataInterval", contDetail.getString("key"))) {
                        GlobalVariables.setClockAlignedDataInterval(contDetail.getInt("value"));
                    } else if (Objects.equals("LocalAuthorizeOffline", contDetail.getString("key"))) {
                        GlobalVariables.setLocalAuthorizeOffline(contDetail.getBoolean("value"));
                    } else if (Objects.equals("AuthorizationKey", contDetail.getString("key"))) {
                        GlobalVariables.setAuthorizationKey(contDetail.getString("value"));
                    } else if (Objects.equals("HmConfigPasswd", contDetail.getString("key"))) {
                        GlobalVariables.setHmConfigPasswd(contDetail.getString("value"));
                    } else if (Objects.equals("HmChargerMode", contDetail.getString("key"))) {
                        GlobalVariables.setHmChargerMode(contDetail.getString("value"));
                    } else if (Objects.equals("HmSoundVolumn", contDetail.getString("key"))) {
                        GlobalVariables.setHmSoundVolumn(contDetail.getString("value"));
                    } else if (Objects.equals("HmCharingLimitFee", contDetail.getString("key"))) {
                        GlobalVariables.setHmCharingLimitFee(contDetail.getString("value"));
                    } else if (Objects.equals("HmChargingTranTerm", contDetail.getString("key"))) {
                        GlobalVariables.setHmChargingTranTerm(contDetail.getString("value"));
                    } else if (Objects.equals("HmPreparingTranTerm", contDetail.getString("key"))) {
                        GlobalVariables.setHmPreparingTranTerm(contDetail.getString("value"));
                    } else if (Objects.equals("HmCpConfigVersion", contDetail.getString("key"))) {
                        GlobalVariables.setHmCpConfigVersion(contDetail.getString("value"));
                    } else if (Objects.equals("HmAuthExpiredTerm", contDetail.getString("key"))) {
                        GlobalVariables.setHmAuthExpiredTerm(contDetail.getString("value"));
                    } else if (Objects.equals("HmPreFee", contDetail.getString("key"))) {
                        GlobalVariables.setHmPreFee(contDetail.getString("value"));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("ConfigurationKeyRead onRead error {}", e.getMessage());
        }
    }

}

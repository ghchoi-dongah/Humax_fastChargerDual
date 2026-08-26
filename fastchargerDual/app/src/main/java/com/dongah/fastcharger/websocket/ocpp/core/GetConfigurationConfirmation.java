package com.dongah.fastcharger.websocket.ocpp.core;

import static com.dongah.fastcharger.websocket.ocpp.core.ChangeAvailabilityConfirmation.ACTION_NAME;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Arrays;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Sent by Charge Point the to the Central System in response to a {@link GetConfigurationRequest}
 * Central System 요청 목록이 비어 있는 경우 Charge Point 구성 목록 전체를 보내고
 * Central System 요청 목록이 있는 경우 해당하는 key 의 구성 value 만 보낸다.
 */
@XmlRootElement(name = "getConfigurationResponse")
@XmlType(propOrder = {"configurationKey", "unknownKey"})
public class GetConfigurationConfirmation implements Confirmation {

    private static final String ERROR_MESSAGE = "Exceeds limit od %s chars";
    private KeyValueType[] configurationKey;
    private String[] unknownKey;

    public KeyValueType[] getConfigurationKey() {
        return configurationKey;
    }

    public String getActionName() {
        return ACTION_NAME;
    }

    /**
     * Optional. List of requested or known keys.
     *
     * @param configurationKey Array of {@link KeyValueType}
     */
    @XmlElement
    public void setConfigurationKey(KeyValueType[] configurationKey) {
        this.configurationKey = configurationKey;
    }

    public String[] getUnknownKey() {
        return unknownKey;
    }

    @XmlElement
    public void setUnknownKey(String[] unknownKey) {
        isValidUnknownKey(unknownKey);
        this.unknownKey = unknownKey;
    }

    private void isValidUnknownKey(String[] unknownKey) {
        for (String key : unknownKey) {
            if (!ModelUtil.validate(key, 50)) {
                throw new PropertyConstraintException(key.length(), String.format(ERROR_MESSAGE, 50));
            }
        }
    }

    private boolean validateConfigurationKeys() {
        boolean output = true;
        if (configurationKey != null && configurationKey.length > 0) {
            for (KeyValueType key : configurationKey) {
                if (!key.validate()) {
                    output = false;
                    break;
                }
            }
        }
        return output;
    }

    @Override
    public boolean validate() {
        return validateConfigurationKeys();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetConfigurationConfirmation that = (GetConfigurationConfirmation) o;
        return Arrays.equals(configurationKey, that.configurationKey)
                && Arrays.equals(unknownKey, that.unknownKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configurationKey, unknownKey);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("configurationKey", configurationKey)
                .add("unknownKey", unknownKey)
                .add("isValid", validate())
                .toString();
    }
}

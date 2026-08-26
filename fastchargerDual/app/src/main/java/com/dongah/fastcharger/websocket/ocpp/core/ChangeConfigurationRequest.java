package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Sent by Central System to Charge Point
 * <p>
 * It is RECOMMENDED that the content and meaning of the 'key' and 'value' fields is agreed upon
 * between Charge Point and Central System.
 * </P>
 */
@XmlRootElement
@XmlType(propOrder = {"key", "value"})
public class ChangeConfigurationRequest implements Request {

    private static final String ERROR_MESSAGE = "Exceeded limited od %s chars";
    private static final int KEY_MAX_LENGTH = 50;
    private static final int VALUE_MAX_LENGTH = 500;

    private String key;
    private String value;

    /**
     * @deprecated use {@link #ChangeConfigurationRequest(String, String)} to be sure to set required fields.
     */
    @Deprecated
    public ChangeConfigurationRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param key   String, max 50 characters, case insensitive, see {@link #setKey(String)}
     * @param value String, max 500 characters, case insensitive, see {@link #setValue(String)}
     */
    public ChangeConfigurationRequest(String key, String value) {
        setKey(key);
        setValue(value);
    }

    private static String createErrorMessage(int valueMaxLength) {
        return String.format(ERROR_MESSAGE, valueMaxLength);
    }

    public String getKey() {
        return key;
    }

    /**
     * Required. The name of the configuration setting to change
     *
     * @param key String, max 50 characters, case insensitive.
     */
    @XmlElement
    public void setKey(String key) {
        if (!isValidKey(key)) {
            throw new PropertyConstraintException(key.length(), createErrorMessage(KEY_MAX_LENGTH));
        }
        this.key = key;
    }

    private boolean isValidKey(String key) {
        return ModelUtil.validate(key, KEY_MAX_LENGTH);
    }

    public String getValue() {
        return value;
    }

    /**
     * Required. The new value as string for the setting.
     *
     * @param value String, ax 500 characters, case insensitive.
     */
    public void setValue(String value) {
        this.value = value;
    }

    private boolean isValidValue(String value) {
        return ModelUtil.validate(value, VALUE_MAX_LENGTH);
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return isValidKey(this.key) && isValidValue(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangeConfigurationRequest that = (ChangeConfigurationRequest) o;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("key", key)
                .add("value", value)
                .add("isValid", validate())
                .toString();
    }
}

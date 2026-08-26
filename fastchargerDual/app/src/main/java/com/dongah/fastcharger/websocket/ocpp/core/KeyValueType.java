package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Contains information about a specific configuration key. It is return in {@link GetConfigurationConfirmation}
 */
@XmlRootElement
@XmlType(propOrder = {"key", "readonly", "value"})
public class KeyValueType implements Validatable {

    private static final String ERROR_MESSAGE = "Exceeds limit of %s chars";
    private String key;
    private Boolean readonly;
    private String value;

    /**
     * @deprecated use {@link #KeyValueType(String, Boolean, String)} to be sure to set required fields
     */
    @Deprecated
    public KeyValueType() {
    }

    /**
     * Handle required fields.
     *
     * @param key      String, max 50 characters, case insensitive, see{@link #setKey(String)}
     * @param readonly Boolean, configuration is read only, see {@link #setReadonly(Boolean)}
     */
    public KeyValueType(String key, Boolean readonly, String value) {
        setKey(key);
        setReadonly(readonly);
        setValue(value);
    }

    private static String createErrorMessage(int maxLength) {
        return String.format(ERROR_MESSAGE, maxLength);
    }

    public String getKey() {
        return key;
    }

    /**
     * Required. Name of the key.
     *
     * @param key String, max 50 characters, case insensitive.
     */
    public void setKey(String key) {
        if (!isValidKey(key)) {
            throw new PropertyConstraintException(key.length(), createErrorMessage(50));
        }
        this.key = key;
    }

    public Boolean getReadonly() {
        return readonly;
    }

    /**
     * Required. False if the value can be set with a {@link ChangeConfigurationRequest}
     *
     * @param readonly Boolean, configuration is read only
     */
    @XmlElement
    public void setReadonly(Boolean readonly) {
        if (!isValidReadonly(readonly)) {
            throw new PropertyConstraintException(null, "readonly must be present");
        }
        this.readonly = readonly;
    }

    public String getValue() {
        return value;
    }

    /**
     * Optional. If key is known but not set, this field may be absent.
     *
     * @param value String, max 500 characters, case insensitive.
     */
    @XmlElement
    public void setValue(String value) {
        if (!isValidValue(value)) {
            throw new PropertyConstraintException(value.length(), createErrorMessage(500));
        }
        this.value = value;
    }

    private boolean isValidKey(String key) {
        return ModelUtil.validate(key, 50);
    }

    private boolean isValidReadonly(Boolean readonly) {
        return readonly != null;
    }

    private boolean isValidValue(String value) {
        return ModelUtil.validate(value, 500);
    }

    @Override
    public boolean validate() {
        return isValidKey(this.key) && isValidReadonly(this.readonly) && isValidValue(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyValueType that = (KeyValueType) o;
        return Objects.equals(key, that.key)
                && Objects.equals(readonly, that.readonly)
                && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, readonly, value);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("key", key)
                .add("readonly", readonly)
                .add("value", value)
                .toString();
    }
}

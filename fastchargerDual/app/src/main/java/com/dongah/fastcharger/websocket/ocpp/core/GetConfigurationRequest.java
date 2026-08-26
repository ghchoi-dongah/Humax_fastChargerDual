package com.dongah.fastcharger.websocket.ocpp.core;


import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Sent by the Central System to the Charge Point.
 */
@XmlRootElement
public class GetConfigurationRequest implements Request {

    private String[] key;

    public String[] getKey() {
        return key;
    }

    /**
     * Optional. List of keys for which the configuration value is requested.
     * key 목이 비어 있는 경우 Charge Point 는 구성 설정 목록 전체를 return.
     *
     * @param key Array of Strings, max 50 characters each, case insensitive.
     */
    @XmlElement
    public void setKey(String[] key) {
        validateKeys(key);
        this.key = key;
    }

    private void validateKeys(String[] keys) {
        for (String k : keys) {
            if (!ModelUtil.validate(k, 50)) {
                throw new PropertyConstraintException(k.length(), "Exceeds limit of 50 chars");
            }
        }
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetConfigurationRequest that = (GetConfigurationRequest) o;
        return Arrays.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(key);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("key", key)
                .add("isValid", validate())
                .toString();
    }
}

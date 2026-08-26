package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

/**
 * Sent by the Central System to the Charge Point.
 */
public class ResetRequest implements Request {

    private ResetType type;

    /**
     * @deprecated use {@link #ResetRequest(ResetType)} to be sure to set required fields.
     */
    @Deprecated
    public ResetRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param type the {@link ResetType}, see {@link #setType(ResetType)}
     */
    public ResetRequest(ResetType type) {
        setType(type);
    }

    public ResetType getType() {
        return type;
    }

    /**
     * Required. This contains the type of reset that the Charge Point should perform
     *
     * @param type the {@link ResetType}
     */
    @XmlElement
    public void setType(ResetType type) {
        this.type = type;
    }

    @Deprecated
    public ResetType objType() {
        return type;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return type != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResetRequest that = (ResetRequest) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)
                .add("isValid", validate())
                .toString();
    }
}

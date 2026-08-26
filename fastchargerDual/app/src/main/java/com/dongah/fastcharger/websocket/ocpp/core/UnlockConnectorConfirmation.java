package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sent by Charge Point to the Central System in response ot an {@link UnlockConnectorRequest}
 */
@XmlRootElement(name = "unlockConnectorResponse")
public class UnlockConnectorConfirmation implements Confirmation {

    private UnlockStatus status;

    /**
     * @deprecated use {@link #UnlockConnectorConfirmation(UnlockStatus)} to be sure to set required fields.
     */
    @Deprecated
    public UnlockConnectorConfirmation() {
    }

    /**
     * Handle required fields.
     *
     * @param status the {@link UnlockStatus}, see {@link #setStatus(UnlockStatus)}
     */
    public UnlockConnectorConfirmation(UnlockStatus status) {
        setStatus(status);
    }

    public UnlockStatus getStatus() {
        return status;
    }

    /**
     * Required. This indicates whether the Charge Point has unlocked the connector.
     *
     * @param status the {@link UnlockStatus}
     */
    @XmlElement
    public void setStatus(UnlockStatus status) {
        this.status = status;
    }

    @Deprecated
    public UnlockStatus objStatus() {
        return status;
    }

    @Override
    public boolean validate() {
        return status != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnlockConnectorConfirmation that = (UnlockConnectorConfirmation) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("status", status)
                .add("isValid", validate())
                .toString();
    }
}

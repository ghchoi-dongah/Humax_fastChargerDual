package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

/**
 * Sent from Charge Point to Central System
 */
public class RemoteStopTransactionConfirmation implements Confirmation {

    private static final String ACTION_NAME = "RemoteStopTransaction";

    private RemoteStartStopStatus status;

    /**
     * @deprecated use {@link #RemoteStopTransactionConfirmation(RemoteStartStopStatus)} to be sure
     * to set required fields.
     */
    @Deprecated
    public RemoteStopTransactionConfirmation() {
    }

    public String getActionName() {
        return ACTION_NAME;
    }

    /**
     * Handle required fields.
     *
     * @param status the {@link RemoteStartStopStatus}, see {@link #setStatus(RemoteStartStopStatus)}
     */
    public RemoteStopTransactionConfirmation(RemoteStartStopStatus status) {
        setStatus(status);
    }

    public RemoteStartStopStatus getStatus() {
        return status;
    }

    /**
     * Required. Status indicating whether Charge Point accepts the request to stop a transaction.
     *
     * @param status
     */
    @XmlElement
    public void setStatus(RemoteStartStopStatus status) {
        this.status = status;
    }

    /**
     * Status indicating whether Charge Point accepts the request to stop a transaction.
     *
     * @return the {@link RemoteStartStopStatus}
     */
    @Deprecated
    public RemoteStartStopStatus objStatus() {
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
        RemoteStopTransactionConfirmation that = (RemoteStopTransactionConfirmation) o;
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

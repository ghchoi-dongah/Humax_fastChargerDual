package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sent from Charge Point to Central System
 */
@XmlRootElement(name = "remoteStartTransactionResponse")
public class RemoteStartTransactionConfirmation implements Confirmation {

    private static final String ACTION_NAME = "RemoteStartTransaction";

    private RemoteStartStopStatus status;

    /**
     * @deprecated use {@link #RemoteStartTransactionConfirmation(RemoteStartStopStatus)} to be sure
     * to set required fields.
     */
    @Deprecated
    public RemoteStartTransactionConfirmation() {
    }

    public String getActionName() {
        return ACTION_NAME;
    }

    /**
     * Handle required fields.
     *
     * @param status status fro request, see {@link #setStatus(RemoteStartStopStatus)}
     */
    public RemoteStartTransactionConfirmation(RemoteStartStopStatus status) {
        setStatus(status);
    }

    public RemoteStartStopStatus getStatus() {
        return status;
    }

    /**
     * Required. Status indicating whether Charge Point accept the request to start a transaction.
     *
     * @param status the {@link RemoteStartStopStatus}
     */
    @XmlElement
    public void setStatus(RemoteStartStopStatus status) {
        this.status = status;
    }

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
        RemoteStartTransactionConfirmation that = (RemoteStartTransactionConfirmation) o;
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

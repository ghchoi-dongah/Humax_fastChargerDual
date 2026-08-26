package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

/**
 * Sent by the Charge Point to the Central System in response to a {@link ResetRequest}
 */
public class ResetConfirmation implements Confirmation {

    private static final String ACTION_NAME = "Reset";

    public String getActionName() {
        return ACTION_NAME;
    }

    private ResetStatus status;

    /**
     * @deprecated use {@link #ResetConfirmation(ResetStatus)} to be sure to set required fields.
     */
    @Deprecated
    public ResetConfirmation() {
    }

    /**
     * Handle required fields.
     *
     * @param status the {@link ResetStatus}, see {@link #setStatus(ResetStatus)}
     */
    public ResetConfirmation(ResetStatus status) {
        setStatus(status);
    }

    public ResetStatus getStatus() {
        return status;
    }

    /**
     * Required. This indicates whether the Charge Point is able to perform the reset
     *
     * @param status the {@link ResetStatus}
     */
    @XmlElement
    public void setStatus(ResetStatus status) {
        this.status = status;
    }

    @Deprecated
    public ResetStatus objStatus() {
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
        ResetConfirmation that = (ResetConfirmation) o;
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

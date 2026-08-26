package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * return by Change Point to Central System
 */
@XmlRootElement(name = "changeAvailabilityResponse")
public class ChangeAvailabilityConfirmation implements Confirmation {

    private AvailabilityStatus status;
    public static final String ACTION_NAME = "ChangeAvailability";

    /**
     * @deprecated use {@link #ChangeAvailabilityConfirmation(AvailabilityStatus)} to be sure to set
     * required fields.
     */
    @Deprecated
    public ChangeAvailabilityConfirmation() {
    }

    public String getActionName() {
        return ACTION_NAME;
    }

    /**
     * Handle required fields.
     *
     * @param status the {@link AvailabilityStatus}, see {@link #setStatus(AvailabilityStatus)}
     */
    public ChangeAvailabilityConfirmation(AvailabilityStatus status) {
        setStatus(status);
    }

    public AvailabilityStatus getStatus() {
        return status;
    }

    /**
     * Required. This indicates whether the Charge Point is able to perform the availability change.
     *
     * @param status the {@link AvailabilityStatus} of connector.
     */
    @XmlElement
    public void setStatus(AvailabilityStatus status) {
        this.status = status;
    }

    @Deprecated
    public AvailabilityStatus objStatus() {
        return this.status;
    }

    @Override
    public boolean validate() {
        return this.status != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangeAvailabilityConfirmation that = (ChangeAvailabilityConfirmation) o;
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

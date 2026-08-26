package com.dongah.fastcharger.websocket.ocpp.reservation;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class CancelReservationConfirmation implements Confirmation {

    private CancelReservationStatus status;

    /**
     * @deprecated use {@link #CancelReservationConfirmation(CancelReservationStatus)} to be sure to
     * set required fields.
     */
    @Deprecated
    public CancelReservationConfirmation() {
    }

    /**
     * Handle required fields.
     *
     * @param status CancelReservationStatus, status of the request, see {@link #setStatus(CancelReservationStatus)}
     */
    public CancelReservationConfirmation(CancelReservationStatus status) {
        setStatus(status);
    }

    public CancelReservationStatus getStatus() {
        return status;
    }

    /**
     * Required. This indicates the success or failure of the cancelling of a reservation by Central System.
     *
     * @param status CancelReservationStatus, status of the request.
     */
    public void setStatus(CancelReservationStatus status) {
        this.status = status;
    }

    @Override
    public boolean validate() {
        return status != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CancelReservationConfirmation that = (CancelReservationConfirmation) o;
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

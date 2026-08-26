package com.dongah.fastcharger.websocket.ocpp.reservation;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sent by the Charge Point to the Central System in response to an {@link ReserveNowRequest}
 */
@XmlRootElement(name = "reserveNowResponse")
public class ReserveNowConfirmation implements Confirmation {

    private ReservationStatus status;

    /**
     * @deprecated use {@link #ReserveNowConfirmation(ReservationStatus)} to be sure to set required fields.
     */
    @Deprecated
    public ReserveNowConfirmation() {
    }

    /**
     * Handle required fields.
     *
     * @param status ReservationStatus, status of the reservation, see {@link #setStatus(ReservationStatus)}
     */
    public ReserveNowConfirmation(ReservationStatus status) {
        setStatus(status);
    }

    public ReservationStatus getStatus() {
        return status;
    }

    /**
     * Required. This indicates the success or failure of the reservation.
     *
     * @param status ReservationStatus, status of the reservation.
     */
    @XmlElement
    public void setStatus(ReservationStatus status) {
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
        ReserveNowConfirmation that = (ReserveNowConfirmation) o;
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

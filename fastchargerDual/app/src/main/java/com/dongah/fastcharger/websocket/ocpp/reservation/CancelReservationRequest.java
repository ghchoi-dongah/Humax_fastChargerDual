package com.dongah.fastcharger.websocket.ocpp.reservation;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sent by Central System to the Charge Point.
 */
@XmlRootElement
public class CancelReservationRequest implements Request {

    private static final String ACTION_NAME = "CancelReservation";
    private Integer reservationId;

    /**
     * @deprecated use {@link #CancelReservationRequest(Integer)} to be sure to set required fields.
     */
    @Deprecated
    public CancelReservationRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param reservationId Integer, id of reservation, see {@link #setReservationId(Integer)}
     */
    public CancelReservationRequest(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public String getActionName() {
        return ACTION_NAME;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    /**
     * Required. Id of the reservation to cancel.
     *
     * @param reservationId Integer, id of the reservation.
     */
    @XmlElement
    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return reservationId != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CancelReservationRequest that = (CancelReservationRequest) o;
        return Objects.equals(reservationId, that.reservationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("reservationId", reservationId)
                .add("isValid", validate())
                .toString();
    }
}

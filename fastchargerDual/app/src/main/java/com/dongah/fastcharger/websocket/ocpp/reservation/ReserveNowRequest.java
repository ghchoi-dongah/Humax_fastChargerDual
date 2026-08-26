package com.dongah.fastcharger.websocket.ocpp.reservation;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.time.ZonedDateTime;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Sent by the Central System to the Charge Point
 */
@XmlRootElement
@XmlType(propOrder = {"connectorId", "expiryDate", "parentIdTag", "reservationId"})

public class ReserveNowRequest implements Request {

    private static final int ID_TAG_MAX_LENGTH = 20;
    private static final String ERROR_MESSAGE = "Exceeded limit of " + ID_TAG_MAX_LENGTH + " chars";
    private Integer connectorId;
    private ZonedDateTime expiryDate;
    private String idTag;
    private String parentIdTag;
    private Integer reservationId;

    /**
     * @deprecated use {@link #ReserveNowRequest(Integer, ZonedDateTime, String, Integer)} to be sure
     * to set required fields.
     */
    @Deprecated
    public ReserveNowRequest() {
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    /**
     * Handle required fields
     *
     * @param connectorId   Integer, the destination connector id, see {@link #setConnectorId(Integer)}
     * @param expiryDate    ZonedDateTime, end of reservation, see {@link #setExpiryDate(ZonedDateTime)}
     * @param idTag         String, the identifier, see {@link #setIdTag(String)}
     * @param reservationId Integer, id of reservation, see {@link #setReservationId(Integer)}
     */
    public ReserveNowRequest(Integer connectorId, ZonedDateTime expiryDate, String idTag, Integer reservationId) {
        setConnectorId(connectorId);
        setExpiryDate(expiryDate);
        setIdTag(idTag);
        setReservationId(reservationId);
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    /**
     * Required. This contains the id of the connector to be reserved. A value of 0 means that the
     * reservation is not for a specific connector.
     *
     * @param connectorId Integer, the destination connectorId.
     */
    @XmlElement
    public void setConnectorId(Integer connectorId) {
        if (connectorId < 0) {
            throw new PropertyConstraintException(connectorId, "connectorId must be >= 0");
        }
        this.connectorId = connectorId;
    }

    public ZonedDateTime getExpiryDate() {
        return expiryDate;
    }

    /**
     * Required. This contains the date and time when the reservation ends.
     * 예약이 종료되는 날짜와 시간
     *
     * @param expiryDate ZonedDateTime, end of reservation.
     */
    @XmlElement
    public void setExpiryDate(ZonedDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getIdTag() {
        return idTag;
    }

    /**
     * Required. The identifier for which Change Point has to reserve a connector.
     *
     * @param idTag String, the identifier
     */
    @XmlElement
    public void setIdTag(String idTag) {
        if (!ModelUtil.validate(idTag, ID_TAG_MAX_LENGTH)) {
            throw new PropertyConstraintException(idTag.length(), ERROR_MESSAGE);
        }
        this.idTag = idTag;
    }

    public String getParentIdTag() {
        return parentIdTag;
    }

    /**
     * Optional. The parent idTag.
     *
     * @param parentIdTag String, the parent identifier.
     */
    @XmlElement
    public void setParentIdTag(String parentIdTag) {
        if (!ModelUtil.validate(parentIdTag, ID_TAG_MAX_LENGTH)) {
            throw new PropertyConstraintException(parentIdTag.length(), ERROR_MESSAGE);
        }
        this.parentIdTag = parentIdTag;
    }


    public Integer getReservationId() {
        return reservationId;
    }

    /**
     * Required. Unique id for this reservation.
     *
     * @param reservationId Integer, id of reservation.
     */
    @XmlElement
    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public boolean validate() {
        boolean valid = (connectorId != null && connectorId >= 0);
        valid &= expiryDate != null;
        valid &= ModelUtil.validate(idTag, ID_TAG_MAX_LENGTH);
        valid &= reservationId != null;
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReserveNowRequest that = (ReserveNowRequest) o;
        return Objects.equals(connectorId, that.connectorId)
                && Objects.equals(expiryDate, that.expiryDate)
                && Objects.equals(idTag, that.idTag)
                && Objects.equals(parentIdTag, that.parentIdTag)
                && Objects.equals(reservationId, that.reservationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId, expiryDate, idTag, parentIdTag, reservationId);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("connectorId", connectorId)
                .add("expiryDate", expiryDate)
                .add("idTag", idTag)
                .add("parentIdTag", parentIdTag)
                .add("reservationId", reservationId)
                .add("isValid", validate())
                .toString();
    }
}

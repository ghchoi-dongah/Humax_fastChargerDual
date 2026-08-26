package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.time.ZonedDateTime;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Sent by the Charge Point to the Central System.
 */
@XmlRootElement
@XmlType(propOrder = {"connectorId", "idTag", "timestamp", "meterStart", "reservationId"})
public class StartTransactionRequest implements Request {

    private static final String ACTION_NAME = "StartTransaction";
    private static final int ID_TAG_MAX_LENGTH = 20;
    private static final String ID_TAG_ERROR_MESSAGE = "Exceeded limit of " + ID_TAG_MAX_LENGTH + "chars";

    private Integer connectorId;
    private String idTag;
    private long meterStart;
    private Integer reservationId;
    private ZonedDateTime timestamp;

    /**
     * @deprecated use {@link #StartTransactionRequest{Integer, String, Integer, ZonedDateTime)} to be
     * sure to set required fields.
     */
    @Deprecated
    public StartTransactionRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param connectorId integer. value 0, see {@link #setConnectorId(Integer)}
     * @param idTag       a String with max length 20, see {@link #setIdTag(String)}
     * @param meterStart  long, Wh at start, see {@link #setMeterStart(long)}
     * @param timestamp   ZonedDateTime, start time, see {@link #setTimestamp(ZonedDateTime)}
     */
    public StartTransactionRequest(Integer connectorId, String idTag, long meterStart, ZonedDateTime timestamp) {
        setConnectorId(connectorId);
        setIdTag(idTag);
        setMeterStart(meterStart);
        setTimestamp(timestamp);
    }


    public String getActionName() {
        return ACTION_NAME;
    }


    public Integer getConnectorId() {
        return connectorId;
    }

    /**
     * Required. This identifies which connector of the Charge Point is used.
     *
     * @param connectorId integer. values 0
     */
    public void setConnectorId(Integer connectorId) {
        if (connectorId == null || connectorId <= 0) {
            throw new PropertyConstraintException(connectorId, "connectorId must be > 0");
        }
        this.connectorId = connectorId;
    }

    public String getIdTag() {
        return idTag;
    }

    /**
     * Required. This contains the identifier for which a transaction has to be started.
     *
     * @param idTag a String with max length 20
     */
    public void setIdTag(String idTag) {
        if (!ModelUtil.validate(idTag, ID_TAG_MAX_LENGTH)) {
            throw new PropertyConstraintException(idTag.length(), ID_TAG_ERROR_MESSAGE);
        }
        this.idTag = idTag;
    }

    public long getMeterStart() {
        return meterStart;
    }

    /**
     * Required. This contains the meter value in Wh for the connector at start of the transaction.
     *
     * @param meterStart integer, Wh at start.
     */
    public void setMeterStart(long meterStart) {
        this.meterStart = meterStart;
    }


    public Integer getReservationId() {
        return reservationId;
    }

    /**
     * Optional. This contains the id of the reservation that terminates as a result of that transaction
     *
     * @param reservationId integer, reservation.
     */
    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Required. This contains the data and time on which the transaction is started.
     *
     * @param timestamp ZonedDateTime, start time.
     */
    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Deprecated
    public ZonedDateTime objTimestamp() {
        return timestamp;
    }

    @Override
    public boolean transactionRelated() {
        return true;
    }

    @Override
    public boolean validate() {
        boolean valid = connectorId != null && connectorId > 0;
        valid &= ModelUtil.validate(idTag, 20);
        valid &= (long) meterStart >= 0;
        valid &= timestamp != null;
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StartTransactionRequest that = (StartTransactionRequest) o;
        return Objects.equals(connectorId, that.connectorId)
                && Objects.equals(idTag, that.idTag)
                && Objects.equals(meterStart, that.meterStart)
                && Objects.equals(reservationId, that.reservationId)
                && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId, idTag, meterStart, reservationId, timestamp);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("connectorId", connectorId)
                .add("idTag", idTag)
                .add("meterStart", meterStart)
                .add("reservationId", reservationId)
                .add("timestamp", timestamp)
                .add("isValid", validate())
                .toString();
    }
}
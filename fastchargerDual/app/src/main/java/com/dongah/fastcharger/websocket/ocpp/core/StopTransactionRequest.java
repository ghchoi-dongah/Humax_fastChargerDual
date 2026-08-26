package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.time.ZonedDateTime;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Sent by the Charge Point to the Central System
 */
@XmlRootElement
@XmlType(propOrder = {"transactionId", "IdTag", "timestamp", "meterStop", "reason", "transactionDate"})
public class StopTransactionRequest implements Request {

    private static final String ACTION_NAME = "StopTransaction";

    private String idTag;
    private long meterStop;
    private ZonedDateTime timestamp;
    private Integer transactionId;
    private Reason reason;
    private MeterValue[] transactionData;

    /**
     * @deprecated use {@link #StopTransactionRequest(long, Integer, Reason)} to be sure to
     * set required fields.
     */
    @Deprecated
    public StopTransactionRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param meterStop     long, meter value in Wh, see {@link #setMeterStop(long)}
     * @param transactionId integer, transaction id, see {@link #setTransactionId(Integer)}
     * @param reason        Reason, see {@link #setReason(Reason)}
     */
    public StopTransactionRequest(long meterStop, Integer transactionId, Reason reason) {
        setMeterStop(meterStop);
        setTransactionId(transactionId);
        setReason(reason);
    }


    /**
     * Handle required fields.
     *
     * @param meterStop     long, meter value in Wh, see {@link #setMeterStop(long)}
     * @param timestamp     ZonedDateTime, stop time, see {@link #setTimestamp(ZonedDateTime)}
     * @param transactionId integer, transaction id, see {@link #setTransactionId(Integer)}
     * @param reason        Reason, see {@link #setReason(Reason)}
     */
    public StopTransactionRequest(long meterStop, ZonedDateTime timestamp, Integer transactionId, Reason reason) {
        setMeterStop(meterStop);
        setTimestamp(timestamp);
        setTransactionId(transactionId);
        setReason(reason);
    }


    public String getActionName() {
        return ACTION_NAME;
    }

    public String getIdTag() {
        return idTag;
    }

    /**
     * Optional. This contains the identifier which requested to stop the charging. It is optional
     * because a Charge Point may terminate charging without the presence of an iDtag, e.g in case
     * if a reset. A Charge Point SHALL send the idTag if known.
     *
     * @param idTag a String with max length 20
     */
    @XmlElement
    public void setIdTag(String idTag) {
        if (!ModelUtil.validate(idTag, 20)) {
            throw new PropertyConstraintException(idTag.length(), "Exceeded limit of 20 chars");
        }
        this.idTag = idTag;
    }

    public long getMeterStop() {
        return meterStop;
    }

    /**
     * Required. This contains the meter value in Wh for the connector at end of the transaction.
     *
     * @param meterStop integer, meter value in Wh
     */
    @XmlElement
    public void setMeterStop(long meterStop) {
//        @SuppressLint("DefaultLocale") String kWhFormat = String.format("%.2f",meterStop);
//        this.meterStop = Long.parseLong(kWhFormat);
        this.meterStop = meterStop;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Required. This contains the data and time on which the transaction is stopped
     *
     * @param timestamp ZonedDateTime, Stop time
     */
    @XmlElement
    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Deprecated
    public ZonedDateTime objTimestamp() {
        return timestamp;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    /**
     * Required. This contains the transaction id as received by the {@link StartTransactionConfirmation}
     *
     * @param transactionId integer, transaction id.
     */
    @XmlElement
    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }


    public Reason getReason() {
        return reason;
    }

    /**
     * Required. This contains the reason why the transaction was stopped. May only be omitted when
     * te=he {@link Reason} is "Local".
     *
     * @param reason the {@link Reason}
     */
    @XmlElement
    public void setReason(Reason reason) {
        this.reason = reason;
    }

    @Deprecated
    public Reason objReason() {
        return reason;
    }

    public MeterValue[] getTransactionData() {
        return transactionData;
    }

    /**
     * Optional. This contains transaction usage details relevant for billing purposes.
     *
     * @param transactionData the {@link MeterValue}
     */
    public void setTransactionData(MeterValue[] transactionData) {
        this.transactionData = transactionData;
    }

    @Override
    public boolean transactionRelated() {
        return true;
    }

    @Override
    public boolean validate() {
        boolean valid = (double) meterStop >= 0;
        valid &= timestamp != null;
        valid &= transactionId != null;
        if (transactionData != null) {
            for (MeterValue meterValue : transactionData) {
                valid &= meterValue.validate();
            }
        }
        return valid;
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("idTag", idTag)
                .add("meterStop", meterStop)
                .add("timestamp", timestamp)
                .add("transactionId", transactionId)
                .add("reason", reason)
                .add("transactionData", transactionData)
                .add("isValid", validate())
                .toString();
    }
}

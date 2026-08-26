package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sent to Charge Point by Central System.
 */
@XmlRootElement
public class RemoteStopTransactionRequest implements Request {

    private Integer transactionId;

    /**
     * @deprecated use {@link #RemoteStopTransactionRequest(Integer)} to be sure to set required fields
     */
    @Deprecated
    public RemoteStopTransactionRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param transactionId Integer, transaction id see {@link #setTransactionId(Integer)}
     */
    public RemoteStopTransactionRequest(Integer transactionId) {
        setTransactionId(transactionId);
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    /**
     * Required. The identifier of the transaction which Charge Point is requested to stop.
     *
     * @param transactionId integer, transaction id.
     */
    @XmlElement
    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return transactionId != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteStopTransactionRequest that = (RemoteStopTransactionRequest) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("transactionId", transactionId)
                .add("isValid", validate())
                .toString();
    }
}

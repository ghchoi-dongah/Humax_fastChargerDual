package com.dongah.fastcharger.websocket.ocpp.remotetrigger;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"requestedMessage", "connectorId"})
public class TriggerMessageRequest implements Request {

    private Integer connectorId;
    private TriggerMessageRequestType requestedMessage;

    /**
     * @deprecated use {@link #TriggerMessageRequest(TriggerMessageRequestType)} to be sure to set required fields.
     */
    @Deprecated
    public TriggerMessageRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param requestedMessage{@link TriggerMessageRequestType}, see {@link #setRequestedMessage}
     */
    public TriggerMessageRequest(TriggerMessageRequestType requestedMessage) {
        setRequestedMessage(requestedMessage);
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    /**
     * Optional. This identifies which connector of the Change Point is used.
     *
     * @param connectorId Integer. value 0.
     */
    @XmlElement
    public void setConnectorId(Integer connectorId) {
        if (connectorId != null && connectorId <= 0) {
            throw new PropertyConstraintException(connectorId, "connectorId must be > 0");
        }
        this.connectorId = connectorId;
    }

    public TriggerMessageRequestType getRequestedMessage() {
        return requestedMessage;
    }

    /**
     * Required. This identifies which type of message you want to trigger.
     *
     * @param requestedMessage {@link TriggerMessageRequestType}
     */
    @XmlElement
    public void setRequestedMessage(TriggerMessageRequestType requestedMessage) {
        this.requestedMessage = requestedMessage;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        boolean valid = requestedMessage != null;
        valid &= connectorId == null || connectorId > 0;
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TriggerMessageRequest that = (TriggerMessageRequest) o;
        return Objects.equals(connectorId, that.connectorId)
                && requestedMessage == that.requestedMessage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId, requestedMessage);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("connectorId", connectorId)
                .add("requestedMessage", requestedMessage)
                .add("isValid", validate())
                .toString();
    }

}

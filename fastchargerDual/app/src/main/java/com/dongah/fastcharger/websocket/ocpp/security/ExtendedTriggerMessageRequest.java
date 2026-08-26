package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.remotetrigger.TriggerMessageStatus;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class ExtendedTriggerMessageRequest implements Request {

    private TriggerMessageStatus requestedMessage;
    private int connectorId;


    public ExtendedTriggerMessageRequest(TriggerMessageStatus requestedMessage) {
        this.requestedMessage = requestedMessage;
    }


    public TriggerMessageStatus getRequestedMessage() {
        return requestedMessage;
    }

    public void setRequestedMessage(TriggerMessageStatus requestedMessage) {
        this.requestedMessage = requestedMessage;
    }

    public int getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(int connectorId) {
        if (connectorId <= 0) {
            throw new PropertyConstraintException(connectorId, "connectorId must be > 0");
        }
        this.connectorId = connectorId;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return this.requestedMessage != null && this.connectorId > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtendedTriggerMessageRequest that = (ExtendedTriggerMessageRequest) o;
        return Objects.equals(connectorId, that.connectorId)
                && Objects.equals(requestedMessage, that.requestedMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId, requestedMessage);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("connectorId", connectorId)
                .add("requestedMessage", requestedMessage)
                .add("isValid", validate())
                .toString();
    }

}

package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sent by the Central System to the Charge Point
 */
@XmlRootElement
public class UnlockConnectorRequest implements Request {

    private Integer connectorId;

    /**
     * @deprecated use {@link #UnlockConnectorRequest(Integer)} to be sure to set required fields.
     */
    @Deprecated
    public UnlockConnectorRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param connectorId Integer, value 0; see {@link #setConnectorId(Integer)}
     */
    public UnlockConnectorRequest(Integer connectorId) {
        setConnectorId(connectorId);
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    /**
     * Required. This contains the identifier of the connector to be unlocked.
     *
     * @param connectorId Integer, value 0;
     */
    @XmlElement
    public void setConnectorId(Integer connectorId) {
        if (connectorId == null || connectorId <= 0) {
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
        return connectorId != null && connectorId > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnlockConnectorRequest that = (UnlockConnectorRequest) o;
        return Objects.equals(connectorId, that.connectorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("connectorId", connectorId)
                .add("isValid", validate())
                .toString();
    }
}

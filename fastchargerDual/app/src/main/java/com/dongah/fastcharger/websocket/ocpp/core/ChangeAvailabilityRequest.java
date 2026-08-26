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
public class ChangeAvailabilityRequest implements Request {

    private static final String ACTION_NAME = "ChangeAvailability";
    private Integer connectorId = -1;
    private AvailabilityType type;

    /**
     * @deprecated use {@link #ChangeAvailabilityRequest(Integer, AvailabilityType)} to be sure
     * to set required fields.
     */
    @Deprecated
    public ChangeAvailabilityRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param connectorId integer, must be a non-negative number.
     * @param type        the {@link AvailabilityType} of the connector, see {@link #setType(AvailabilityType)}
     */
    public ChangeAvailabilityRequest(Integer connectorId, AvailabilityType type) {
        setConnectorId(connectorId);
        setType(type);
    }

    public String getActionName() {
        return ACTION_NAME;
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    /**
     * Required. The id of the connector for which availability needs to change. Id '0'(zero) is
     * used if the availability of the Charger Point and all its connectors needs to change.
     *
     * @param connectorId integer, must be a non-negative number.
     */
    @XmlElement
    public void setConnectorId(Integer connectorId) {
        if (connectorId < 0) {
            throw new PropertyConstraintException(connectorId, "connectorId must be >= 0");
        }
        this.connectorId = connectorId;
    }

    public AvailabilityType getType() {
        return type;
    }

    /**
     * Required. This contains the type of availability change that the Charge Point should perform.
     *
     * @param type {@link AvailabilityType} of the connector
     */
    @XmlElement
    public void setType(AvailabilityType type) {
        this.type = type;
    }

    @Deprecated
    public AvailabilityType objType() {
        return type;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return type != null && connectorId != null && connectorId >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangeAvailabilityRequest that = (ChangeAvailabilityRequest) o;
        return Objects.equals(connectorId, that.connectorId) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId, type);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("connectorId", connectorId)
                .add("type", type)
                .add("isValid", validate())
                .toString();
    }
}

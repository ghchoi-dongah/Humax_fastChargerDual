package com.dongah.fastcharger.websocket.ocpp.smartcharging;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GetCompositeScheduleRequest implements Request {

    private Integer connectorId;
    private Integer duration;
    private ChargingRateUnitType chargingRateUnit;

    /**
     * @deprecated use {@link #GetCompositeScheduleRequest(Integer, Integer)} to be sure to set
     * required fields.
     */
    @Deprecated
    public GetCompositeScheduleRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param connectorId Integer, see {@link #setConnectorId(Integer)}
     * @param duration    Integer, see {@link #setDuration(Integer)}
     */
    public GetCompositeScheduleRequest(Integer connectorId, Integer duration) {
        setConnectorId(connectorId);
        setDuration(duration);
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    /**
     * Required. The ID of the Connector for which the schedule is requested. When ConnectorId = 0,
     * the Charge Point will calculate the expected consumption for the grid connection.
     *
     * @param connectorId Integer.
     */
    @XmlElement
    public void setConnectorId(Integer connectorId) {
        if (connectorId == null || connectorId < 0) {
            throw new PropertyConstraintException(connectorId, "connectorId must be >= 0");
        }
        this.connectorId = connectorId;
    }

    public Integer getDuration() {
        return duration;
    }

    /**
     * Required. Time in seconds. length of requested schedule
     *
     * @param duration Integer
     */
    @XmlElement
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public ChargingRateUnitType getChargingRateUnit() {
        return chargingRateUnit;
    }

    /**
     * Optional. Can be used to force a power or current profile
     *
     * @param chargingRateUnit the {@link ChargingRateUnitType}
     */
    @XmlElement
    public void setChargingRateUnit(ChargingRateUnitType chargingRateUnit) {
        this.chargingRateUnit = chargingRateUnit;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        boolean valid = connectorId != null && connectorId >= 0;
        valid &= duration != null;
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetCompositeScheduleRequest that = (GetCompositeScheduleRequest) o;
        return Objects.equals(connectorId, that.connectorId) && Objects.equals(duration, that.duration) && chargingRateUnit == that.chargingRateUnit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId, duration, chargingRateUnit);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("connectorId", connectorId)
                .add("duration", duration)
                .add("chargingRateUnit", chargingRateUnit)
                .add("isValid", validate())
                .toString();
    }
}

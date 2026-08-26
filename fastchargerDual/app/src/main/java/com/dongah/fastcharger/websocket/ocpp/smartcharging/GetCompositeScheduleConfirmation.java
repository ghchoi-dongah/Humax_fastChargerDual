package com.dongah.fastcharger.websocket.ocpp.smartcharging;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.core.ChargingSchedule;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.time.ZonedDateTime;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

public class GetCompositeScheduleConfirmation implements Confirmation {

    private GetCompositeScheduleStatus status;
    private Integer connectorId;
    private ZonedDateTime scheduleStart;
    private ChargingSchedule chargingSchedule;

    /**
     * @deprecated use {@link #GetCompositeScheduleConfirmation(GetCompositeScheduleStatus)} to be
     * sure to set required fields.
     */
    @Deprecated
    public GetCompositeScheduleConfirmation() {
    }

    /**
     * Handle required fields.
     *
     * @param status {@link GetCompositeScheduleStatus}, see {@link #setStatus(GetCompositeScheduleStatus)}
     */
    public GetCompositeScheduleConfirmation(GetCompositeScheduleStatus status) {
        setStatus(status);
    }

    public GetCompositeScheduleStatus getStatus() {
        return status;
    }

    /**
     * Required. Status of the request. The Charge Point will indicate if it was able to process the request
     *
     * @param status {@link GetCompositeScheduleStatus}
     */
    @XmlElement
    public void setStatus(GetCompositeScheduleStatus status) {
        this.status = status;
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    /**
     * Optional. The charging schedule contained in this notification applies to a Connector.
     *
     * @param connectorId Integer
     */
    @XmlElement
    public void setConnectorId(Integer connectorId) {
        this.connectorId = connectorId;
    }

    public ZonedDateTime getScheduleStart() {
        return scheduleStart;
    }

    /**
     * Optional. Time. Periods contained in the charging profile are relative to this point in time.
     * If status is "Rejected", this field may be absent.
     *
     * @param scheduleStart {@link ZonedDateTime}
     */
    @XmlElement
    public void setScheduleStart(ZonedDateTime scheduleStart) {
        this.scheduleStart = scheduleStart;
    }

    public ChargingSchedule getChargingSchedule() {
        return chargingSchedule;
    }

    /**
     * Optional. Planned Composite Charging Schedule, the energy consumption over time. Always
     * relative to ScheduleStart. If status is "Rejected". this field may be absent.
     *
     * @param chargingSchedule {@link ChargingSchedule}
     */
    @XmlElement
    public void setChargingSchedule(ChargingSchedule chargingSchedule) {
        this.chargingSchedule = chargingSchedule;
    }

    @Override
    public boolean validate() {
        return this.status != null;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetCompositeScheduleConfirmation that = (GetCompositeScheduleConfirmation) o;
        return status == that.status && Objects.equals(connectorId, that.connectorId) && Objects.equals(scheduleStart, that.scheduleStart) && Objects.equals(chargingSchedule, that.chargingSchedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, connectorId, scheduleStart, chargingSchedule);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("compositeScheduleStatus", status)
                .add("connectorId", connectorId)
                .add("scheduleStart", scheduleStart)
                .add("chargingSchedule", chargingSchedule)
                .add("isValid", validate())
                .toString();
    }
}

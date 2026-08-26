package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Class Type used with {@link ChargingSchedule}
 */
@XmlRootElement
@XmlType(propOrder = {"startPeriod", "limit", "numberPhase"})
public class ChargingSchedulePeriod implements Validatable {

    private Integer startPeriod;
    private Double limit;
    private Integer numberPhases = 3;

    /**
     * @deprecated use {@link #ChargingSchedulePeriod(Integer, Double)} to be sure to set required fields
     */
    @Deprecated
    public ChargingSchedulePeriod() {
    }

    /**
     * Handle required fields.
     *
     * @param startPeriod integer, seconds from start of schedule, see {@link #setStartPeriod(Integer)}
     * @param limit       decimal, power limit, see {@link #setLimit(Double)}
     */
    public ChargingSchedulePeriod(Integer startPeriod, Double limit) {
        setStartPeriod(startPeriod);
        setLimit(limit);
    }

    public Integer getStartPeriod() {
        return startPeriod;
    }

    /**
     * Required. Start of the period, in seconds from the start of schedule. The value od StartPeriod
     * also defines teh stop time of the previous period. (정지시간도 정의)
     *
     * @param startPeriod integer, seconds from start of schedule.
     */
    @XmlElement
    public void setStartPeriod(Integer startPeriod) {
        this.startPeriod = startPeriod;
    }

    public Double getLimit() {
        return limit;
    }

    /**
     * Required. Power limit during the schedule period, expressed in Amperes. Accepts at most one
     * digit fraction (솟수점 1자리, 충전 전력 제한 암페어)
     *
     * @param limit decimal, power limit
     */
    @XmlElement
    public void setLimit(Double limit) {
        this.limit = limit;
    }


    public Integer getNumberPhase() {
        return numberPhases;
    }

    /**
     * Optional. The number of phases that can be used for charging. Value is set to 3 by default.
     *
     * @param numberPhase integer, default is 3.
     */
    @XmlElement
    public void setNumberPhase(Integer numberPhase) {
        this.numberPhases = numberPhase;
    }

    @Override
    public boolean validate() {
        boolean valid = true;
        valid &= startPeriod != null;
        valid &= limit != null;
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChargingSchedulePeriod that = (ChargingSchedulePeriod) o;
        return Objects.equals(startPeriod, that.startPeriod)
                && Objects.equals(limit, that.limit)
                && Objects.equals(numberPhases, that.numberPhases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPeriod, limit, numberPhases);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("startPeriod", startPeriod)
                .add("limit", limit)
                .add("numberPhases", numberPhases)
                .toString();
    }

}

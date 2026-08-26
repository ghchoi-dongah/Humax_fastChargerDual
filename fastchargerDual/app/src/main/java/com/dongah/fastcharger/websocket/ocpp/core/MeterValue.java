package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

/**
 * Collection of one or more sample values in {@link MeterValuesRequest}. All {@link SampledValue}
 * in a {@link MeterValue} are sampled at the same point in time
 */
public class MeterValue implements Validatable {

    private ZonedDateTime timestamp;
    private SampledValue[] sampledValue;

    /**
     * @deprecated use {@link #MeterValue(ZonedDateTime, SampledValue[])} to be sure to set required fields
     */
    @Deprecated
    public MeterValue() {
    }

    /**
     * Handle required fields.
     *
     * @param timestamp     {@link ZonedDateTime} timestamp, see {@link #setTimestamp(ZonedDateTime)}
     * @param sampledValues Array of {@link SampledValue}, see {@link #setSampledValue(SampledValue[])}
     */
    public MeterValue(ZonedDateTime timestamp, SampledValue[] sampledValues) {
        setTimestamp(timestamp);
        setSampledValue(sampledValues);
    }

    @Override
    public boolean validate() {
        boolean valid = timestamp != null && sampledValue != null;

        if (valid) {
            for (SampledValue value : sampledValue) {
                valid &= value.validate();
            }
        }
        return valid;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @XmlElement
    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Deprecated
    public ZonedDateTime objTimestamp() {
        return timestamp;
    }


    public SampledValue[] getSampledValue() {
        return sampledValue;
    }

    public void setSampledValue(SampledValue[] sampledValue) {
        this.sampledValue = sampledValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeterValue that = (MeterValue) o;
        return Objects.equals(timestamp, that.timestamp) && Arrays.equals(sampledValue, that.sampledValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, sampledValue);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("timestamp", timestamp)
                .add("sampledValue", sampledValue)
                .add("isValid", validate())
                .toString();
    }
}

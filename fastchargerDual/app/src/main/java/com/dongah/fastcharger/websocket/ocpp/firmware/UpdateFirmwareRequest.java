package com.dongah.fastcharger.websocket.ocpp.firmware;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.time.ZonedDateTime;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Sent by the Central System to the Charge Point
 */
@XmlRootElement
@XmlType(propOrder = {"location", "retries", "retrieveData", "retryInterval"})
public class UpdateFirmwareRequest implements Request {

    private String location;
    private Integer retries;
    private ZonedDateTime retrieveDate;
    private Integer retryInterval;

    /**
     * @deprecated use {@link #UpdateFirmwareRequest(String, ZonedDateTime)} to be sure to set required fields.
     */
    @Deprecated
    public UpdateFirmwareRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param location     String, a URI with the firmware, see {@link #setLocation(String)}
     * @param retrieveDate ZonedDatetime, date and time of retrieving, see {@link #setRetrieveDate(ZonedDateTime)}
     */
    public UpdateFirmwareRequest(String location, ZonedDateTime retrieveDate) {
        setLocation(location);
        setRetrieveDate(retrieveDate);
    }

    public String getLocation() {
        return location;
    }

    /**
     * Required. This contains a string containing a URI pointing to a location from which to retrieve
     * the firmware.
     *
     * @param location String, a URI with the firmware
     */
    @XmlElement
    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getRetries() {
        return retries;
    }

    /**
     * Optional. This specifies how many times Charge Point must try to download the firmware before
     * giving up. If this field is not present, it is left to Charge Point to decide how many times it
     * wants to retry.
     *
     * @param retries Integer, retry times
     */
    @XmlElement
    public void setRetries(Integer retries) {
        if (retries <= 0) {
            throw new PropertyConstraintException(retries, "retries must be > 0");
        }
        this.retries = retries;
    }

    public ZonedDateTime getRetrieveDate() {
        return retrieveDate;
    }

    /**
     * Required. This contains the data and time after which the Charge Point must retries the (new)
     * firmware.
     *
     * @param retrieveDate ZonedDateTime, data and time of retrieving.
     */
    @XmlElement
    public void setRetrieveDate(ZonedDateTime retrieveDate) {
        this.retrieveDate = retrieveDate;
    }

    public Integer getRetryInterval() {
        return retryInterval;
    }

    /**
     * Optional. The Interval in seconds after which a retry may be attempted. If this field is not
     * present, it is left to Charge Point to decide how long to wait between attempts.
     *
     * @param retryInterval Integer, retry interval.
     */
    @XmlElement
    public void setRetryInterval(Integer retryInterval) {
        if (retryInterval <= 0) {
            throw new PropertyConstraintException(retryInterval, "retryInterval must be > 0");
        }
        this.retryInterval = retryInterval;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return location != null && retrieveDate != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateFirmwareRequest that = (UpdateFirmwareRequest) o;
        return retryInterval.equals(that.retryInterval)
                && Objects.equals(location, that.location)
                && Objects.equals(retries, that.retries)
                && Objects.equals(retrieveDate, that.retrieveDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, retries, retrieveDate, retryInterval);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("location", location)
                .add("retries", retries)
                .add("retrieveDate", retrieveDate)
                .add("retryInterval", retryInterval)
                .add("isValid", validate())
                .toString();
    }
}

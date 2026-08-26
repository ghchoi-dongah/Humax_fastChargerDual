package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.time.ZonedDateTime;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "bootNotificationResponse")
@XmlType(propOrder = {"status", "currentTime", "interval"})

public class BootNotificationConfirmation implements Confirmation {

    private ZonedDateTime currentTime;
    private Integer interval;
    private RegistrationStatus status;

    @Deprecated
    public BootNotificationConfirmation() {
    }

    public ZonedDateTime getCurrentTime() {
        return currentTime;
    }

    @XmlElement
    public void setCurrentTime(ZonedDateTime currentTime) {
        this.currentTime = currentTime;
    }

    @Deprecated
    public ZonedDateTime objCurrentTime() {
        return currentTime;
    }


    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        if (interval < 0) {
            throw new PropertyConstraintException(interval, "interval be a positive value");
        }
        this.interval = interval;
    }


    @Deprecated
    public RegistrationStatus objStatus() {
        return status;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    @Override
    public boolean validate() {
        boolean valid = status != null;
        valid &= currentTime != null;
        valid &= interval != null && interval >= 0;

        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BootNotificationConfirmation that = (BootNotificationConfirmation) o;
        return Objects.equals(interval, that.interval)
                && Objects.equals(currentTime, that.currentTime)
                && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTime, interval, status);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("currentTime", currentTime)
                .add("interval", interval)
                .add("status", status)
                .add("isValid", validate())
                .toString();
    }

}

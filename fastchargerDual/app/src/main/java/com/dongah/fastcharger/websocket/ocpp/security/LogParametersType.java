package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.time.ZonedDateTime;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement
@XmlType(propOrder = {
        "remoteLocation",
        "oldestTimestamp",
        "latestTimestamp"
})
public class LogParametersType implements Validatable {

    private String remoteLocation;
    private ZonedDateTime oldestTimestamp;
    private ZonedDateTime latestTimestamp;

    public LogParametersType(String remoteLocation) {
        this.remoteLocation = remoteLocation;
    }

    public String getRemoteLocation() {
        return remoteLocation;
    }

    public void setRemoteLocation(String remoteLocation) {
        this.remoteLocation = remoteLocation;
    }

    public ZonedDateTime getOldestTimestamp() {
        return oldestTimestamp;
    }

    public void setOldestTimestamp(ZonedDateTime oldestTimestamp) {
        this.oldestTimestamp = oldestTimestamp;
    }

    public ZonedDateTime getLatestTimestamp() {
        return latestTimestamp;
    }

    public void setLatestTimestamp(ZonedDateTime latestTimestamp) {
        this.latestTimestamp = latestTimestamp;
    }

    @Override
    public boolean validate() {
        return remoteLocation != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogParametersType that = (LogParametersType) o;
        return Objects.equals(remoteLocation, that.remoteLocation)
                && Objects.equals(oldestTimestamp, that.oldestTimestamp)
                && Objects.equals(latestTimestamp, that.latestTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                remoteLocation,
                oldestTimestamp,
                latestTimestamp);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("remoteLocation", remoteLocation)
                .add("oldestTimestamp", oldestTimestamp)
                .add("latestTimestamp", latestTimestamp)
                .add("isValid", validate())
                .toString();
    }

}

package com.dongah.fastcharger.websocket.ocpp.firmware;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.time.ZonedDateTime;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"location", "startTime", "stopTime", "retries", "retryInterval"})
public class GetDiagnosticsRequest implements Request {

    private String location;
    private Integer retries;
    private Integer retryInterval;
    private ZonedDateTime startTime;
    private ZonedDateTime stopTime;

    /**
     * @deprecated use {@link #GetDiagnosticsRequest(String)} to be sure to set required fields.
     */
    @Deprecated
    public GetDiagnosticsRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param location String , the destination folder, see {@link #setLocation(String)}
     */
    public GetDiagnosticsRequest(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    /**
     * Required. This contains the location (directory) where the diagnostics file shall be uploaded to.
     * location : 업로드될 위치(디렉토리/폴더) 포함
     *
     * @param location String. the destination folder. (대상 폴더)
     */
    @XmlElement
    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getRetries() {
        return retries;
    }

    /**
     * Optional. This specifies how many times Charge Point must try to upload diagnostics before
     * giving up. If this fields is not present, it is left to Charge Point ro decide how many times it
     * wants to retry.
     *
     * @param retries Integer, minimum number of tries.
     */
    @XmlElement
    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public Integer getRetryInterval() {
        return retryInterval;
    }

    /**
     * Optional. The interval in seconds after which a retry may be attempted. If this field is not
     * present, it is left to Charge Point to decide how long to wait between attempts.
     * 재시도 간격, 만약에 interval retry 가 없으면 Charge point 가 결정함.
     *
     * @param retryInterval integer, seconds
     */
    @XmlElement
    public void setRetryInterval(Integer retryInterval) {
        this.retryInterval = retryInterval;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    /**
     * Optional. This contains the date and time of the oldest logging information to include in the diagnostics.
     * 진단에 포함할 가장 오래된 로깅 정보의 날짜와 시간 포함.
     *
     * @param startTime ZonedDateTime, oldest log entry
     */
    @XmlElement
    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getStopTime() {
        return stopTime;
    }

    /**
     * Optional. This contains the date and time of latest logging information to include in the diagnostics
     * 진단에 포함할 최신 로깅 정보의 날자와 시간 포함.
     *
     * @param stopTime ZonedDateTime, latest log entry
     */
    @XmlElement
    public void setStopTime(ZonedDateTime stopTime) {
        this.stopTime = stopTime;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return this.location != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetDiagnosticsRequest that = (GetDiagnosticsRequest) o;
        return Objects.equals(retries, that.retries)
                && Objects.equals(retryInterval, that.retryInterval)
                && Objects.equals(location, that.location)
                && Objects.equals(startTime, that.startTime)
                && Objects.equals(stopTime, that.stopTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, retries, retryInterval, startTime, stopTime);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("location", location)
                .add("retries", retries)
                .add("retryInterval", retryInterval)
                .add("startTime", startTime)
                .add("stopTime", stopTime)
                .add("isValid", validate())
                .toString();
    }
}

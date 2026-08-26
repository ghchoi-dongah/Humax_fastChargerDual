package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"retries", "retryInterval", "requestId", "firmware"})
public class SignedUpdateFirmwareRequest implements Request {

    private int retries;
    private int retryInterval;
    private int requestId;
    private FirmwareType firmware;

    @Deprecated
    public SignedUpdateFirmwareRequest() {
    }

    public SignedUpdateFirmwareRequest(int requestId, FirmwareType firmware) {
        this.requestId = requestId;
        this.firmware = firmware;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public FirmwareType getFirmware() {
        return firmware;
    }

    public void setFirmware(FirmwareType firmware) {
        this.firmware = firmware;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return firmware.validate() & requestId > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignedUpdateFirmwareRequest that = (SignedUpdateFirmwareRequest) o;
        return Objects.equals(retries, that.retries)
                && Objects.equals(retryInterval, that.retryInterval)
                && Objects.equals(requestId, that.requestId)
                && Objects.equals(firmware, that.firmware);
    }

    @Override
    public int hashCode() {
        return Objects.hash(retries, retryInterval, requestId, firmware);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("retries", retries)
                .add("retryInterval", retryInterval)
                .add("requestId", requestId)
                .add("firmware", firmware)
                .add("isValid", validate())
                .toString();
    }

}

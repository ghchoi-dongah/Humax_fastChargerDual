package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"status", "requestId"})
public class SignedFirmwareStatusNotificationRequest implements Request {

    private static final String ACTION_NAME = "SignedFirmwareStatusNotification";

    private SignedFirmwareStatus status;
    private int requestId;

    @Deprecated
    public SignedFirmwareStatusNotificationRequest() {
    }

    public SignedFirmwareStatusNotificationRequest(SignedFirmwareStatus status) {
        this.status = status;
    }


    /**
     * ACTION_NAME
     *
     * @return action name
     */
    public String getActionName() {
        return ACTION_NAME;
    }

    public SignedFirmwareStatus getStatus() {
        return status;
    }

    public void setStatus(SignedFirmwareStatus status) {
        this.status = status;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    @Override
    public boolean transactionRelated() {
        return true;
    }

    @Override
    public boolean validate() {
        return this.status != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignedFirmwareStatusNotificationRequest that = (SignedFirmwareStatusNotificationRequest) o;
        return Objects.equals(status, that.status)
                && Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, requestId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("status", status)
                .add("requestId", requestId)
                .add("isValid", validate())
                .toString();
    }

}

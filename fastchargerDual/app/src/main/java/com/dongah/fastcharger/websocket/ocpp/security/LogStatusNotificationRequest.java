package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement
@XmlType(propOrder = {"status", "requestId"})
public class LogStatusNotificationRequest implements Request {


    private static final String ACTION_NAME = "LogStatusNotification";
    private UploadLogStatus status;
    private int requestId;

    @Deprecated
    public LogStatusNotificationRequest() {
    }

    public LogStatusNotificationRequest(UploadLogStatus status) {
        this.status = status;
    }

    public String getActionName() {
        return ACTION_NAME;
    }

    public UploadLogStatus getStatus() {
        return status;
    }

    public void setStatus(UploadLogStatus status) {
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
        return false;
    }

    @Override
    public boolean validate() {
        return status != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogStatusNotificationRequest that = (LogStatusNotificationRequest) o;
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

package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "getLogRequest")
public class GetLogRequest implements Request {


    private LogType logType;
    private int requestId;
    private int retries;
    private int retryInterval;
    private LogParametersType log;


    public GetLogRequest(LogType logType, int requestId, LogParametersType log) {
        this.logType = logType;
        this.requestId = requestId;
        this.log = log;
    }

    public LogType getLogType() {
        return logType;
    }

    public void setLogType(LogType logType) {
        this.logType = logType;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
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

    public LogParametersType getLog() {
        return log;
    }

    public void setLog(LogParametersType log) {
        this.log = log;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return logType != null && requestId > 0 && log != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetLogRequest that = (GetLogRequest) o;
        return Objects.equals(logType, that.logType)
                && Objects.equals(requestId, that.requestId)
                && Objects.equals(log, that.log);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logType, requestId, log);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("logType", logType)
                .add("requestId", requestId)
                .add("log", log)
                .add("isValid", validate())
                .toString();
    }
}

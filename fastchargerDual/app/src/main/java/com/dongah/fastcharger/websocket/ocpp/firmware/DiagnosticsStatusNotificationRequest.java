package com.dongah.fastcharger.websocket.ocpp.firmware;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sent by the Charge Point to the Central System
 */
@XmlRootElement
public class DiagnosticsStatusNotificationRequest implements Request {


    private static final String ACTION_NAME = "DiagnosticsStatusNotification";
    private DiagnosticsStatus status;

    @Deprecated
    public DiagnosticsStatusNotificationRequest() {
    }

    /**
     * Set Required fields
     *
     * @param status Diagnostics status, see {@link #setStatus(DiagnosticsStatus}
     */
    public DiagnosticsStatusNotificationRequest(DiagnosticsStatus status) {
        setStatus(status);
    }

    public String getActionName() {
        return ACTION_NAME;
    }

    public DiagnosticsStatus getStatus() {
        return status;
    }

    /**
     * Required. This contains the identifier of the status
     *
     * @param status {@link DiagnosticsStatus}
     */
    public void setStatus(DiagnosticsStatus status) {
        if (status == null) {
            throw new PropertyConstraintException(null, "Diagnostic status must be present");
        }
        this.status = status;
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
        DiagnosticsStatusNotificationRequest that = (DiagnosticsStatusNotificationRequest) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("status", status)
                .add("isValid", validate())
                .toString();
    }
}

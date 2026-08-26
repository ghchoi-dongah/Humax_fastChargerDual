package com.dongah.fastcharger.websocket.ocpp.firmware;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

public class FirmwareStatusNotificationRequest implements Request {

    private static final String ACTION_NAME = "FirmwareStatusNotification";
    private FirmwareStatus status;

//    /**
//     * @deprecated use {@link #FirmwareStatusNotificationRequest(FirmwareStatus)} to be sure to set required fields
//     */
//    @Deprecated
    public FirmwareStatusNotificationRequest() {
    }

    /**
     * Handle required fields
     *
     * @param status Firmware status, see {@link #setStatus(FirmwareStatus}
     */
    public FirmwareStatusNotificationRequest(FirmwareStatus status) {
        setStatus(status);
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return status != null;
    }

    /**
     * ACTION_NAME
     *
     * @return action name
     */
    public String getActionName() {
        return ACTION_NAME;
    }

    /**
     * This contains the status
     *
     * @return connector
     */
    public FirmwareStatus getStatus() {
        return status;
    }

    /**
     * Required. This contains the identifier of the status
     *
     * @param status {@link FirmwareStatus}
     */
    @XmlElement
    public void setStatus(FirmwareStatus status) {
        if (status == null) {
            throw new PropertyConstraintException(null, "FirmwareStatus must be present");
        }
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FirmwareStatusNotificationRequest that = (FirmwareStatusNotificationRequest) o;
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

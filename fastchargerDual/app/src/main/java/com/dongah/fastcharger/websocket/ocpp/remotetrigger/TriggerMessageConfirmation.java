package com.dongah.fastcharger.websocket.ocpp.remotetrigger;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "triggerMessageResponse")
public class TriggerMessageConfirmation implements Confirmation {

    private static final String ACTION_NAME = "TriggerMessage";
    private TriggerMessageStatus status;

    /**
     * @deprecated use {@link #TriggerMessageConfirmation(TriggerMessageStatus)} to be sure to set required fields.
     */
    @Deprecated
    public TriggerMessageConfirmation() {
    }

    public String getActionName() {
        return ACTION_NAME;
    }

    /**
     * Handle required fields.
     *
     * @param status the {@link TriggerMessageStatus}, see {@link #setStatus(TriggerMessageStatus}.
     */
    public TriggerMessageConfirmation(TriggerMessageStatus status) {
        setStatus(status);
    }

    public TriggerMessageStatus getStatus() {
        return status;
    }

    /**
     * Required. This indicates the success or failure of trigger message request.
     *
     * @param status the {@link TriggerMessageStatus}
     */
    @XmlElement
    public void setStatus(TriggerMessageStatus status) {
        this.status = status;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TriggerMessageConfirmation that = (TriggerMessageConfirmation) o;
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

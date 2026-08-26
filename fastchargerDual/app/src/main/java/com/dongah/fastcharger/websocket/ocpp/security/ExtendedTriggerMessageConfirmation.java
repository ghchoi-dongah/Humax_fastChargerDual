package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.remotetrigger.TriggerMessageStatus;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class ExtendedTriggerMessageConfirmation implements Confirmation {


    private TriggerMessageStatus status;


    public ExtendedTriggerMessageConfirmation(TriggerMessageStatus status) {
        this.status = status;
    }

    public TriggerMessageStatus getStatus() {
        return status;
    }

    public void setStatus(TriggerMessageStatus status) {
        this.status = status;
    }

    @Override
    public boolean validate() {
        return status != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtendedTriggerMessageConfirmation that = (ExtendedTriggerMessageConfirmation) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("status", status)
                .add("isValid", validate())
                .toString();
    }
}

package com.dongah.fastcharger.websocket.ocpp.localauthlist;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class SendLocalListConfirmation implements Confirmation {

    private UpdateStatus status;
    private static final String ACTION_NAME = "SendLocalList";

    /**
     * @deprecated use {@link #SendLocalListConfirmation(UpdateStatus)} to be sure to set required fields.
     */
    @Deprecated
    public SendLocalListConfirmation() {
    }

    public String getActionName() {
        return ACTION_NAME;
    }

    /**
     * Handle required fields.
     *
     * @param status {@link UpdateStatus}, status of localAuthList updating, see {@link #setStatus(UpdateStatus)}
     */
    public SendLocalListConfirmation(UpdateStatus status) {
        setStatus(status);
    }

    public UpdateStatus getStatus() {
        return status;
    }

    /**
     * Required. This indicates whether the Charge Point has successfully received and applied the
     * update of the local authorization list.
     *
     * @param status {@link UpdateStatus}, status of localAuthList updating
     */
    public void setStatus(UpdateStatus status) {
        if (status == null) {
            throw new PropertyConstraintException(null, "UpdateStatus must be present");
        }
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
        SendLocalListConfirmation that = (SendLocalListConfirmation) o;
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

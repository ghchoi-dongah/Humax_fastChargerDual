package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class SignedUpdateFirmwareConfirmation implements Confirmation {


    private static final String ACTION_NAME = "SignedUpdateFirmware";


    private UpdateFirmwareStatus status;


    public SignedUpdateFirmwareConfirmation(UpdateFirmwareStatus status) {
        this.status = status;
    }


    public String getActionName() {
        return ACTION_NAME;
    }

    public UpdateFirmwareStatus getStatus() {
        return status;
    }

    public void setStatus(UpdateFirmwareStatus status) {
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
        SignedUpdateFirmwareConfirmation that = (SignedUpdateFirmwareConfirmation) o;
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

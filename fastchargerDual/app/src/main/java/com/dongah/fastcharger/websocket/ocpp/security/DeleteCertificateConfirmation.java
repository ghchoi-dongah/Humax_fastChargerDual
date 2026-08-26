package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class DeleteCertificateConfirmation implements Confirmation {


    private DeleteCertificateStatus status;


    public DeleteCertificateConfirmation(DeleteCertificateStatus status) {
        this.status = status;
    }

    public DeleteCertificateStatus getStatus() {
        return status;
    }

    public void setStatus(DeleteCertificateStatus status) {
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
        DeleteCertificateConfirmation that = (DeleteCertificateConfirmation) o;
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

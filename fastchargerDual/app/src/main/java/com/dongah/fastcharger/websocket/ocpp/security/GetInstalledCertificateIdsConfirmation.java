package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class GetInstalledCertificateIdsConfirmation implements Confirmation {

    private GetInstalledCertificateStatus status;
    private CertificateHashDataType[] certificateHashData;

    public GetInstalledCertificateIdsConfirmation(GetInstalledCertificateStatus status) {
        this.status = status;
    }

    public GetInstalledCertificateStatus getStatus() {
        return status;
    }

    public void setStatus(GetInstalledCertificateStatus status) {
        this.status = status;
    }

    public CertificateHashDataType[] getCertificateHashData() {
        return certificateHashData;
    }

    public void setCertificateHashData(CertificateHashDataType[] certificateHashData) {
        this.certificateHashData = certificateHashData;
    }

    @Override
    public boolean validate() {
        return status != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetInstalledCertificateIdsConfirmation that = (GetInstalledCertificateIdsConfirmation) o;
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

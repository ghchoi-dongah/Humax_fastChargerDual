package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class GetInstalledCertificateIdsRequest implements Request {

    private CertificateUse certificateType;


    public GetInstalledCertificateIdsRequest(CertificateUse certificateType) {
        this.certificateType = certificateType;
    }

    public CertificateUse getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateUse certificateType) {
        this.certificateType = certificateType;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return this.certificateType != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetInstalledCertificateIdsRequest that = (GetInstalledCertificateIdsRequest) o;
        return Objects.equals(certificateType, that.certificateType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateType);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("certificateType", certificateType)
                .add("isValid", validate())
                .toString();
    }

}

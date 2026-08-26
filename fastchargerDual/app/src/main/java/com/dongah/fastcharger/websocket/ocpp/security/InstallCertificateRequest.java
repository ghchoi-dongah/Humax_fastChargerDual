package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class InstallCertificateRequest implements Request {

    private CertificateUse certificateType;
    private String certificate;


    public InstallCertificateRequest(CertificateUse certificateType, String certificate) {
        this.certificateType = certificateType;
        this.certificate = certificate;
    }

    public CertificateUse getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateUse certificateType) {
        this.certificateType = certificateType;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return this.certificateType != null && this.certificate != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstallCertificateRequest that = (InstallCertificateRequest) o;
        return Objects.equals(certificateType, that.certificateType)
                && Objects.equals(certificate, that.certificate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateType, certificate);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("certificateType", certificateType)
                .add("certificate", certificate)
                .add("isValid", validate())
                .toString();
    }
}

package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class DeleteCertificateRequest implements Request {


    private CertificateHashDataType certificateHashData;


    public DeleteCertificateRequest(CertificateHashDataType certificateHashData) {
        this.certificateHashData = certificateHashData;
    }

    public CertificateHashDataType getCertificateHashData() {
        return certificateHashData;
    }

    public void setCertificateHashData(CertificateHashDataType certificateHashData) {
        this.certificateHashData = certificateHashData;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        boolean valid = false;
        if (certificateHashData != null) {
            valid &= certificateHashData.validate();
        }
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeleteCertificateRequest that = (DeleteCertificateRequest) o;
        return Objects.equals(certificateHashData, that.certificateHashData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateHashData);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("certificateHashData", certificateHashData)
                .add("isValid", validate())
                .toString();
    }
}

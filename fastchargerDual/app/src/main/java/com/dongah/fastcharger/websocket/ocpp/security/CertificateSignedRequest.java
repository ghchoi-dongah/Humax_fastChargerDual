package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class CertificateSignedRequest implements Request {


    private static final int Certificate_Chain_MAX_LENGTH = 1000;
    private static final String ERROR_MESSAGE = "Exceeded limit of " + Certificate_Chain_MAX_LENGTH + " chars";
    private String certificateChain;


    @Deprecated
    public CertificateSignedRequest() {
    }

    public CertificateSignedRequest(String certificateChain) {
        this.certificateChain = certificateChain;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }


    public String getCertificateChain() {
        return certificateChain;
    }

    public void setCertificateChain(String certificateChain) {
        this.certificateChain = certificateChain;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CertificateSignedRequest that = (CertificateSignedRequest) o;
        return Objects.equals(certificateChain, that.certificateChain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateChain);
    }

    @Override
    public boolean validate() {
        return ModelUtil.validate(certificateChain, Certificate_Chain_MAX_LENGTH);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("certificateChain", certificateChain)
                .add("isValid", validate())
                .toString();
    }

}

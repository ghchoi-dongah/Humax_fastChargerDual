package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"csr"})
public class SignCertificateRequest implements Request {

    private String csr;

    @Deprecated
    public SignCertificateRequest() {
    }

    public SignCertificateRequest(String csr) {
        this.csr = csr;
    }

    @Override
    public boolean transactionRelated() {
        return true;
    }

    @Override
    public boolean validate() {
        return this.csr != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignCertificateRequest that = (SignCertificateRequest) o;
        return Objects.equals(csr, that.csr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(csr);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("csr", csr)
                .add("isValid", validate())
                .toString();
    }
}

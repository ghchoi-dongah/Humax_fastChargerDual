package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {
        "hashAlgorithm",
        "issuerNameHash",
        "issuerKeyHash",
        "serialNumber"
})

public class CertificateHashDataType implements Validatable {


    private HashAlgorithm hashAlgorithm;
    private String issuerNameHash;
    private String issuerKeyHash;
    private String serialNumber;

    public CertificateHashDataType() {
    }

    public CertificateHashDataType(HashAlgorithm hashAlgorithm, String issuerNameHash, String issuerKeyHash, String serialNumber) {
        this.hashAlgorithm = hashAlgorithm;
        this.issuerNameHash = issuerNameHash;
        this.issuerKeyHash = issuerKeyHash;
        this.serialNumber = serialNumber;
    }

    public HashAlgorithm getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(HashAlgorithm hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public String getIssuerNameHash() {
        return issuerNameHash;
    }

    public void setIssuerNameHash(String issuerNameHash) {
        this.issuerNameHash = issuerNameHash;
    }

    public String getIssuerKeyHash() {
        return issuerKeyHash;
    }

    public void setIssuerKeyHash(String issuerKeyHash) {
        this.issuerKeyHash = issuerKeyHash;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public boolean validate() {
        boolean valid = issuerNameHash != null;
        valid &= issuerKeyHash != null;
        valid &= serialNumber != null;
        valid &= hashAlgorithm != null;
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CertificateHashDataType that = (CertificateHashDataType) o;
        return Objects.equals(issuerNameHash, that.issuerNameHash)
                && Objects.equals(issuerKeyHash, that.issuerKeyHash)
                && Objects.equals(serialNumber, that.serialNumber)
                && Objects.equals(hashAlgorithm, that.hashAlgorithm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                issuerNameHash, issuerKeyHash, serialNumber, hashAlgorithm);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("issuerNameHash", issuerNameHash)
                .add("issuerKeyHash", issuerKeyHash)
                .add("serialNumber", serialNumber)
                .add("hashAlgorithm", hashAlgorithm)
                .add("isValid", validate())
                .toString();
    }

}

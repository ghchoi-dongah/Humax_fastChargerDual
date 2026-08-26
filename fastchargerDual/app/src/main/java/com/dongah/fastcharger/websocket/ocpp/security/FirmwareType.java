package com.dongah.fastcharger.websocket.ocpp.security;

import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.time.ZonedDateTime;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"location", "retrieveDateTime", "installDateTime", "signingCertificate", "signature"})
public class FirmwareType implements Validatable {

    private String location;
    private ZonedDateTime retrieveDateTime;
    private ZonedDateTime installDateTime;
    private String signingCertificate;
    private String signature;

    @Deprecated
    public FirmwareType() {
    }

    public FirmwareType(String location, ZonedDateTime retrieveDateTime, String signingCertificate, String signature) {
        this.location = location;
        this.retrieveDateTime = retrieveDateTime;
        this.signingCertificate = signingCertificate;
        this.signature = signature;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ZonedDateTime getRetrieveDateTime() {
        return retrieveDateTime;
    }

    public void setRetrieveDateTime(ZonedDateTime retrieveDateTime) {
        this.retrieveDateTime = retrieveDateTime;
    }

    public ZonedDateTime getInstallDateTime() {
        return installDateTime;
    }

    public void setInstallDateTime(ZonedDateTime installDateTime) {
        this.installDateTime = installDateTime;
    }

    public String getSigningCertificate() {
        return signingCertificate;
    }

    public void setSigningCertificate(String signingCertificate) {
        this.signingCertificate = signingCertificate;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public boolean validate() {
        boolean valid = location != null;
        valid &= retrieveDateTime != null;
        valid &= signingCertificate != null;
        valid &= signature != null;
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FirmwareType that = (FirmwareType) o;
        return Objects.equals(location, that.location)
                && Objects.equals(retrieveDateTime, that.retrieveDateTime)
                && Objects.equals(installDateTime, that.installDateTime)
                && Objects.equals(signingCertificate, that.signingCertificate)
                && Objects.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                location,
                retrieveDateTime,
                installDateTime,
                signingCertificate,
                signature);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("location", location)
                .add("retrieveDateTime", retrieveDateTime)
                .add("installDateTime", installDateTime)
                .add("signingCertificate", signingCertificate)
                .add("signature", signature)
                .add("isValid", validate())
                .toString();
    }
}

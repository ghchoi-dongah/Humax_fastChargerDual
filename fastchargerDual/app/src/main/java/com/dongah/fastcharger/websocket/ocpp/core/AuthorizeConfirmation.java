package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sent by the Central System to the Charge Point in response to a {@link AuthorizeRequest}
 */
@XmlRootElement(name = "authorizeRequest")
public class AuthorizeConfirmation implements Confirmation {

    private IdTagInfo idTagInfo;

    /**
     * @deprecated use {@link #AuthorizeConfirmation(IdTagInfo)} to be sure to set required fields
     */
    @Deprecated
    public AuthorizeConfirmation() {
    }

    /**
     * Handle required fields
     *
     * @param idTagInfo the {@link IdTagInfo}, see {@link #setIdTagInfo(IdTagInfo)}
     */
    public AuthorizeConfirmation(IdTagInfo idTagInfo) {
        setIdTagInfo(idTagInfo);
    }

    /**
     * This contains information about authorization status, expiry and parent id.
     *
     * @return on instance of {@link IdTagInfo}
     */
    public IdTagInfo getIdTagInfo() {
        return idTagInfo;
    }

    /**
     * Required. This contains information about authorization status, expiry and parent id.
     *
     * @param idTagInfo on instance of {@link IdTagInfo}
     */
    @XmlElement
    public void setIdTagInfo(IdTagInfo idTagInfo) {
        this.idTagInfo = idTagInfo;
    }

    @Override
    public boolean validate() {
        boolean valid = true;
        if (valid &= idTagInfo != null) valid &= idTagInfo.validate();
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizeConfirmation response = (AuthorizeConfirmation) o;
        return Objects.equals(idTagInfo, response.idTagInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTagInfo);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("idTagInfo", idTagInfo)
                .add("isValid", validate())
                .toString();
    }
}

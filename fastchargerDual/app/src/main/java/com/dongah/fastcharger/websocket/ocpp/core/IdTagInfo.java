package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.time.ZonedDateTime;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

public class IdTagInfo implements Validatable {
    private ZonedDateTime expiryDate;
    private String parentIdTag;
    private AuthorizationStatus status;

    /**
     * @deprecated use {@link #IdTagInfo(AuthorizationStatus)} to be sure to set required fields
     */
    @Deprecated
    public IdTagInfo() {
    }

    /**
     * Required
     *
     * @param status the {@link AuthorizationStatus} for IfTag, see {@link #setStatus(AuthorizationStatus)}
     */
    public IdTagInfo(AuthorizationStatus status) {
        this.status = status;
    }


    public ZonedDateTime getExpiryDate() {
        return expiryDate;
    }

    @XmlElement
    public void setExpiryDate(ZonedDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Deprecated
    public ZonedDateTime objExpiryDate() {
        return expiryDate;
    }

    public String getParentIdTag() {
        return parentIdTag;
    }

    @XmlElement
    public void setParentIdTag(String parentIdTag) {
        this.parentIdTag = parentIdTag;
    }


    public AuthorizationStatus getStatus() {
        return status;
    }

    /**
     * Required. This contains whether the idTag has been accepted or not by the Central System.
     *
     * @param status the {@link AuthorizationStatus} for IdTag.
     */
    public void setStatus(AuthorizationStatus status) {
        this.status = status;
    }

    @Deprecated
    public AuthorizationStatus objStatus() {
        return status;
    }


    @Override
    public boolean validate() {
        boolean valid = true;
        valid &= this.status != null;
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdTagInfo idTagInfo = (IdTagInfo) o;
        return Objects.equals(expiryDate, idTagInfo.expiryDate)
                && Objects.equals(parentIdTag, idTagInfo.parentIdTag)
                && status == idTagInfo.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expiryDate, parentIdTag, status);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("expiryDate", expiryDate)
                .add("parentIdTag", parentIdTag)
                .add("status", status)
                .toString();
    }
}

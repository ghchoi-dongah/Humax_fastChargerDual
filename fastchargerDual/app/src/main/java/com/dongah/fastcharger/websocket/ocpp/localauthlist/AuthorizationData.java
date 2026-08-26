package com.dongah.fastcharger.websocket.ocpp.localauthlist;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;
import com.dongah.fastcharger.websocket.ocpp.core.IdTagInfo;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

public class AuthorizationData implements Validatable {

    private String idTag;
    private IdTagInfo idTagInfo;

    /**
     * @deprecated use {@link #AuthorizationData(String)} to be sure to set required fields.
     */
    @Deprecated
    public AuthorizationData() {
    }

    /**
     * Handle required fields.
     *
     * @param idTag String, the idTag, see {@link #setIdTag(String)}
     */
    public AuthorizationData(String idTag) {
        setIdTag(idTag);
    }

    public String getIdTag() {
        return idTag;
    }

    /**
     * Required. The identifier to which this authorization applies.
     *
     * @param idTag String, the idTag
     */
    @XmlElement
    public void setIdTag(String idTag) {
        if (!ModelUtil.validate(idTag, 20)) {
            throw new PropertyConstraintException(idTag, "Exceeds limit of 20 chars");
        }
        this.idTag = idTag;
    }

    public IdTagInfo getIdTagInfo() {
        return idTagInfo;
    }

    public void setIdTagInfo(IdTagInfo idTagInfo) {
        if (!idTagInfo.validate()) {
            throw new PropertyConstraintException(idTagInfo, "Failed Validation");
        }
        this.idTagInfo = idTagInfo;
    }

    @Override
    public boolean validate() {
        return ModelUtil.validate(idTag, 20) && idTagInfo.validate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizationData that = (AuthorizationData) o;
        return Objects.equals(idTag, that.idTag) && Objects.equals(idTagInfo, that.idTagInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTag, idTagInfo);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("idTag", idTag)
                .add("idTagInfo", idTagInfo)
                .toString();
    }
}

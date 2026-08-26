package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class AuthorizeRequest implements Request {

    private static final String ACTION_NAME = "Authorize";
    private static final int ID_TAG_MAX_LENGTH = 20;
    private static final String ERROR_MESSAGE = "Exceeded limit of " + ID_TAG_MAX_LENGTH + " chars";
    private String idTag;

    /**
     * @deprecated use {@link #AuthorizeRequest(String)} to be sure to set required fields
     */
    @Deprecated
    public AuthorizeRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param idTag authorized id, see {@link #setIdTag(String)}
     */
    public AuthorizeRequest(String idTag) {
        setIdTag(idTag);
    }

    /**
     * Action name
     *
     * @return String, ACTION_NAME
     */
    public String getActionName() {
        return ACTION_NAME;
    }


    /**
     * This contains the identifier that needs to be authorized.
     *
     * @return String, max 20 characters. Case insensitive.
     */
    public String getIdTag() {
        return idTag;
    }

    public void setIdTag(String idTag) {
        if (!ModelUtil.validate(idTag, ID_TAG_MAX_LENGTH)) {
            throw new PropertyConstraintException(idTag.length(), ERROR_MESSAGE);
        }
        this.idTag = idTag;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizeRequest that = (AuthorizeRequest) o;
        return Objects.equals(idTag, that.idTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTag);
    }

    @Override
    public boolean validate() {
        return ModelUtil.validate(idTag, ID_TAG_MAX_LENGTH);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("idTag", idTag)
                .add("isValid", validate())
                .toString();
    }
}

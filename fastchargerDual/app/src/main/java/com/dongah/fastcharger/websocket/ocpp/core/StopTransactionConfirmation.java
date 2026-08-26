package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class StopTransactionConfirmation implements Confirmation {

    private IdTagInfo idTagInfo;

    public IdTagInfo getIdTagInfo() {
        return idTagInfo;
    }

    /**
     * Optional. This contains onformation about authorization status, expiry and parent i. It is
     * optional, because a transaction may have been stopped without an identifier.
     *
     * @param idTagInfo the {@link IdTagInfo}
     */
    public void setIdTagInfo(IdTagInfo idTagInfo) {
        this.idTagInfo = idTagInfo;
    }

    @Override
    public boolean validate() {
        boolean valid = true;
        if (idTagInfo != null) {
            valid &= idTagInfo.validate();
        }
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StopTransactionConfirmation that = (StopTransactionConfirmation) o;
        return Objects.equals(idTagInfo, that.idTagInfo);
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

package com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.core.DataTransferStatus;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class AnnounceConfirmation implements Confirmation {


    private static final String ACTION_NAME = "announce";

    DataTransferStatus status;


    @Deprecated
    public AnnounceConfirmation() {
    }

    public AnnounceConfirmation(DataTransferStatus status) {
        setStatus(status);
    }

    public DataTransferStatus getStatus() {
        return status;
    }

    public void setStatus(DataTransferStatus status) {
        this.status = status;
    }

    public String getActionName() {
        return ACTION_NAME;
    }


    @Override
    public boolean validate() {
        return status != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnounceConfirmation that = (AnnounceConfirmation) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("status", status)
                .add("isValid", validate())
                .toString();
    }

}

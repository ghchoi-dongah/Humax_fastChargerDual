package com.dongah.fastcharger.websocket.ocpp.datatransfer.humax;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class CustomUnitPriceConfirm implements Confirmation {

    private static final Logger logger = LoggerFactory.getLogger(CustomUnitPriceConfirm.class);

    private CustomStatus status;
    private CustomUnitPriceConfirmData data;


    public CustomUnitPriceConfirm() {
    }

    public CustomStatus getStatus() {
        return status;
    }

    public void setStatus(CustomStatus status) {
        this.status = status;
    }

    public CustomUnitPriceConfirmData getData() {
        return data;
    }

    public void setData(CustomUnitPriceConfirmData data) {
        this.data = data;
    }

    @Override
    public boolean validate() {
        return status != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomUnitPriceConfirm that = (CustomUnitPriceConfirm) o;
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

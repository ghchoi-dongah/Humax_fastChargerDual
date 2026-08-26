package com.dongah.fastcharger.websocket.ocpp.datatransfer.humax;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class PaymentConfirm implements Confirmation {

    private static final Logger logger = LoggerFactory.getLogger(PaymentConfirm.class);


    private static final String ACTION_NAME = "DataTransfer";

    private CustomStatus status;
    private PaymentConfirmData data;


    public PaymentConfirm() {
    }

    public CustomStatus getStatus() {
        return status;
    }

    public void setStatus(CustomStatus status) {
        this.status = status;
    }

    public PaymentConfirmData getData() {
        return data;
    }

    public void setData(PaymentConfirmData data) {
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
        PaymentConfirm that = (PaymentConfirm) o;
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

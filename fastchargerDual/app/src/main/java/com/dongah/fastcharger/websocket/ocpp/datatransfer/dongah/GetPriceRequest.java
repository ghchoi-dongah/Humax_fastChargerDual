package com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class GetPriceRequest implements Request {

    private static final String TAG = GetPriceRequest.class.getSimpleName();
    private static final String ACTION_NAME = "DataTransfer";

    private String vendorId;
    private String messageId;
    private String data;

    public GetPriceRequest() {
    }

    public String getActionName() {
        return ACTION_NAME;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetPriceRequest that = (GetPriceRequest) o;
        return Objects.equals(vendorId, that.vendorId) && Objects.equals(messageId, that.messageId) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vendorId, messageId, data);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("vendorId", vendorId)
                .add("messageId", messageId)
                .add("isValid", validate())
                .toString();
    }
}

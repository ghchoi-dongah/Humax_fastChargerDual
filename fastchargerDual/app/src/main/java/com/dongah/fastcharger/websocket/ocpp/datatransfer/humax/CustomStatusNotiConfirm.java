package com.dongah.fastcharger.websocket.ocpp.datatransfer.humax;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class CustomStatusNotiConfirm implements Confirmation {

    private static final Logger logger = LoggerFactory.getLogger(CustomStatusNotiConfirm.class);

    private static final String ACTION_NAME = "DataTransfer";

    private String vendorId;
    private String messageId;
    private ChargerPointInfoData data;


    public CustomStatusNotiConfirm() {
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

    public ChargerPointInfoData getData() {
        return data;
    }

    public void setData(ChargerPointInfoData data) {
        this.data = data;
    }
    @Override
    public boolean validate() {
        return messageId != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomStatusNotiConfirm that = (CustomStatusNotiConfirm) o;
        return messageId.equals(that.messageId) && vendorId.equals(that.vendorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("vendorId", vendorId )
                .add("messageId", messageId)
                .add("isValid", validate())
                .toString();
    }
}

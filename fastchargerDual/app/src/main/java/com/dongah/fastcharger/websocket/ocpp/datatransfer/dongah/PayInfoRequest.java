package com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PayInfoRequest implements Request {

    private static final Logger logger = LoggerFactory.getLogger(PayInfoRequest.class);

    private static final String ACTION_NAME = "DataTransfer";
    private String vendorId;
    private String messageId;
    private String data;

    public PayInfoRequest() {
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
}

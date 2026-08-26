package com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah;

import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;

import java.util.Objects;

public class SmsMessageData implements Validatable {

    private int transactionId;              //충전시작 후에는 transaction-id 입력. 충전시작 전에는 0으로 입력
    private String teleNum;


    public SmsMessageData() {
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getTeleNum() {
        return teleNum;
    }

    public void setTeleNum(String teleNum) {
        this.teleNum = teleNum;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmsMessageData that = (SmsMessageData) o;
        return transactionId == that.transactionId && teleNum == that.teleNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, teleNum);
    }
}

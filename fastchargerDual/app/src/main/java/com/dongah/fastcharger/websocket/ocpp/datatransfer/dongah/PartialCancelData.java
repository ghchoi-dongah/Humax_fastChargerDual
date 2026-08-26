package com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah;

import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;

import java.util.Objects;

public class PartialCancelData implements Validatable {


    private int connectorId;
    private String timestamp;
    private String pgTransactionNum;
    private String payId;
    private int transactionId;              //충전시작 후에는 transaction-id 입력. 충전시작 전에는 0으로 입력
    private String startTimestamp;          //This contains timestamp in the StartTransaction.req
    private String stopTimestamp;           //This contains timestamp in the StopTransaction.req
    private int deposit;
    private int payResult;                  // 0 : 성공   1:실패

    public PartialCancelData() {
    }

    public int getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(int connectorId) {
        this.connectorId = connectorId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPgTransactionNum() {
        return pgTransactionNum;
    }

    public void setPgTransactionNum(String pgTransactionNum) {
        this.pgTransactionNum = pgTransactionNum;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(String startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public String getStopTimestamp() {
        return stopTimestamp;
    }

    public void setStopTimestamp(String stopTimestamp) {
        this.stopTimestamp = stopTimestamp;
    }

    public int getDeposit() {
        return deposit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public int getPayResult() {
        return payResult;
    }

    public void setPayResult(int payResult) {
        this.payResult = payResult;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartialCancelData that = (PartialCancelData) o;
        return connectorId == that.connectorId && transactionId == that.transactionId &&
                deposit == that.deposit && Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(pgTransactionNum, that.pgTransactionNum) && Objects.equals(payId, that.payId) &&
                Objects.equals(startTimestamp, that.startTimestamp) && Objects.equals(stopTimestamp, that.stopTimestamp) &&
                Objects.equals(payResult, that.payResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId, timestamp, pgTransactionNum, payId, transactionId,
                startTimestamp, stopTimestamp, deposit, payResult);
    }

}

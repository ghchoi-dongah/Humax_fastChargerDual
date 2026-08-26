package com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah;

import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;

import java.util.Objects;

public class PayInfoData implements Validatable {

    private int connectorId;
    private String timestamp;
    private String pgTransactionNum;        //부가 정보
    private String payId;                   //StartTransaction 내 IdTag 값으로 설정
    private String approvalNum;             //승인번호
    private String transactionDate;         //승인년월 20211128
    private String transactionTime;         //승인시간 121212
    private String authAmount;              //승인금액 (fee + tax + serviceFee)
    private String cardNum;                 //카드번호, first 6digit + * (20digit right aligned)


    public PayInfoData() {
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

    public String getApprovalNum() {
        return approvalNum;
    }

    public void setApprovalNum(String approvalNum) {
        this.approvalNum = approvalNum;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getAuthAmount() {
        return authAmount;
    }

    public void setAuthAmount(String authAmount) {
        this.authAmount = authAmount;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayInfoData that = (PayInfoData) o;
        return connectorId == that.connectorId && Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(pgTransactionNum, that.pgTransactionNum) && Objects.equals(payId, that.payId) &&
                Objects.equals(approvalNum, that.approvalNum) && Objects.equals(transactionDate, that.transactionDate) &&
                Objects.equals(transactionTime, that.transactionTime) && Objects.equals(authAmount, that.authAmount) &&
                Objects.equals(cardNum, that.cardNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId, timestamp, pgTransactionNum, payId,
                approvalNum, transactionDate, transactionTime, authAmount, cardNum);
    }
}

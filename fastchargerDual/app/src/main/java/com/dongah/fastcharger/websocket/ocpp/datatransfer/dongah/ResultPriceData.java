package com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah;

import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;

import java.util.Objects;

public class ResultPriceData implements Validatable {

    private int connectorId;
    private int transactionId;              //충전시작 후에는 transaction-id 입력. 충전시작 전에는 0으로 입력
    private double unitPrice;               //충전 단가
    private double usePower;                //충전 실 사용량
    private int resultPrice;                //실충전 금액

    public ResultPriceData() {
    }

    public int getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(int connectorId) {
        this.connectorId = connectorId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getUsePower() {
        return usePower;
    }

    public void setUsePower(double usePower) {
        this.usePower = usePower;
    }

    public int getResultPrice() {
        return resultPrice;
    }

    public void setResultPrice(int resultPrice) {
        this.resultPrice = resultPrice;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResultPriceData that = (ResultPriceData) o;
        return connectorId == that.connectorId && transactionId == that.transactionId &&
                unitPrice == that.unitPrice && usePower == that.usePower &&
                resultPrice == that.resultPrice;
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId, transactionId, unitPrice, usePower, resultPrice);
    }

}

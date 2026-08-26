package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Arrays;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 1. Charge Point meter 값에 대한 추가 정보 제공
 * 2. ChangeConfiguration.req 데이터 수집 간격 구성
 * 3. 샘플을 가져온 connector Id 기준. connector Id = 0 ==> Main meter 데이터 샘플링
 * 4. transactionId ==> 충전중일 경우 Transaction id
 * 단, 충전중이 아닐때, connector id = 0 인 경우 transaction id 생략 가능
 * 5. EV 제공하는 Current.Offered(최대전류) 및 Power.Offered(최대전력)
 */
@XmlRootElement
@XmlType(propOrder = {"connectorId", "transactionId", "meterValue"})
public class MeterValuesRequest implements Request {

    private static final String ACTION_NAME = "MeterValues";

    private Integer connectorId;
    private Integer transactionId;
    private MeterValue[] meterValue;

    /**
     * @deprecated use {@link MeterValuesRequest(Integer)} to be sure to set required fields
     */
    @Deprecated
    public MeterValuesRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param connectorId Integer, connector, see {@link #setConnectorId(Integer)}
     */
    public MeterValuesRequest(Integer connectorId) {
        setConnectorId(connectorId);
    }


    public String getActionName() {
        return ACTION_NAME;
    }

    @Override
    public boolean transactionRelated() {
        return true;
    }

    @Override
    public boolean validate() {
        boolean valid = this.connectorId != null && this.connectorId >= 0 && this.meterValue != null;

        if (valid) {
            for (MeterValue current : this.meterValue) {
                valid &= (current != null && current.validate());
            }
        }

        return valid;
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(Integer connectorId) {
        if (connectorId < 0) {
            throw new PropertyConstraintException(connectorId, "connectorId must be >= 0");
        }
        this.connectorId = connectorId;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public MeterValue[] getMeterValue() {
        return meterValue;
    }

    public void setMeterValue(MeterValue[] meterValue) {
        this.meterValue = meterValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeterValuesRequest that = (MeterValuesRequest) o;
        return Objects.equals(connectorId, that.connectorId)
                && Objects.equals(transactionId, that.transactionId)
                && Arrays.equals(meterValue, that.meterValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId, transactionId, meterValue);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("connectorId", connectorId)
                .add("transactionId", transactionId)
                .add("meterValue", meterValue)
                .add("isValid", validate())
                .toString();
    }
}

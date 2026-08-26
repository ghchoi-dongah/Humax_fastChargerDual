package com.dongah.fastcharger.websocket.ocpp.datatransfer.humax;

import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;

import java.util.Objects;

public class CustomUnitPriceData  implements Validatable {


    private int connectorId;
    private String userType;
    private String idTag;
    private String timestamp;


    public CustomUnitPriceData() {
    }

    public int getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(int connectorId) {
        this.connectorId = connectorId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getIdTag() {
        return idTag;
    }

    public void setIdTag(String idTag) {
        this.idTag = idTag;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomUnitPriceData that = (CustomUnitPriceData) o;
        return connectorId == that.connectorId && Objects.equals(userType, that.userType)
                && Objects.equals(idTag, that.idTag) && timestamp == that.timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId, userType, idTag, timestamp);
    }
}

package com.dongah.fastcharger.websocket.socket;

import androidx.annotation.NonNull;

public class Connector {
    private int connectorId;
    private int searchKey;
    private String qrUrl;

    public Connector(int connectorId, int searchKey, String qrUrl) {
        this.connectorId = connectorId;
        this.searchKey = searchKey;
        this.qrUrl = qrUrl;
    }

    // Getter 메서드
    public int getConnectorId() {
        return connectorId;
    }

    public int getSearchKey() {
        return searchKey;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setConnectorId(int connectorId) {
        this.connectorId = connectorId;
    }

    public void setSearchKey(int searchKey) {
        this.searchKey = searchKey;
    }


    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "Connector{" +
                "connectorId=" + connectorId +
                ", searchKey=" + searchKey +
                ", qrUrl='" + qrUrl + '\'' +
                '}';
    }
}

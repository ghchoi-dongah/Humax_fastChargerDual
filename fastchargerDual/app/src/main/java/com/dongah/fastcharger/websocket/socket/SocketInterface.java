package com.dongah.fastcharger.websocket.socket;

import com.dongah.fastcharger.websocket.ocpp.common.OccurenceConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;

import org.json.JSONException;

import okhttp3.WebSocket;

public interface SocketInterface {
    void onOpen(WebSocket webSocket);

    void onGetMessage(WebSocket webSocket, String text) throws JSONException;

    void onGetFailure(WebSocket webSocket, Throwable t);

    void onSend(String actionName, Request request) throws OccurenceConstraintException;

    void onSend(int connectorId, String actionName, Request request) throws OccurenceConstraintException;

    void onResultSend(String actionName, String uuid, Confirmation confirmation) throws OccurenceConstraintException;

    /**
     * 응답
     */
    void onCall(String id, String action, Object payload);
}

package com.dongah.fastcharger.websocket.ocpp.common.feature;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;

import java.util.UUID;

public interface Feature {
    Confirmation handleRequest(UUID sessionIndex, Request request);

    Class<? extends Request> getRequestType();

    Class<? extends Confirmation> getConfirmationType();

    String getAction();
}

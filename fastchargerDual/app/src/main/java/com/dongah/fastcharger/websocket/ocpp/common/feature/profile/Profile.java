package com.dongah.fastcharger.websocket.ocpp.common.feature.profile;

import com.dongah.fastcharger.websocket.ocpp.common.feature.ProfileFeature;
import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;

import java.util.UUID;

public interface Profile {
    ProfileFeature[] getFeatureList();

    Confirmation handleRequest(UUID sessionIndex, Request request);
}

package com.dongah.fastcharger.websocket.ocpp.common.feature;

import com.dongah.fastcharger.websocket.ocpp.common.feature.profile.Profile;
import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;

import java.util.UUID;

public abstract class ProfileFeature {

    private Profile profile;

    public ProfileFeature(Profile ownerProfile) {
        profile = ownerProfile;
    }

    public Confirmation handleRequest(UUID sessionIndex, Request request) {
        return profile.handleRequest(sessionIndex, request);
    }

}

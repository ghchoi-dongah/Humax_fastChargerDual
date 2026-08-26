package com.dongah.fastcharger.websocket.ocpp.common;

import com.dongah.fastcharger.websocket.ocpp.common.feature.Feature;

import java.util.Optional;

public interface IFeatureRepository {
    Optional<Feature> findFeature(Object needle);
}

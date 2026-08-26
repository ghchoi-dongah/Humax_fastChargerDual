package com.dongah.fastcharger.websocket.ocpp.common;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dongah.fastcharger.websocket.ocpp.common.feature.Feature;
import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FeatureRepository implements IFeatureRepository {

    private final Map<String, Feature> actionMap = new HashMap<>();
    private final Map<Class<?>, Feature> classMap = new HashMap<>();

    public void addFeature(Feature feature) {
        actionMap.put(feature.getAction(), feature);
        classMap.put(feature.getRequestType(), feature);
        classMap.put(feature.getConfirmationType(), feature);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Optional<Feature> findFeature(Object needle) {
        if (needle instanceof String) {
            return Optional.ofNullable(actionMap.get(needle));
        }
        if ((needle instanceof Request) || (needle instanceof Confirmation)) {
            return Optional.ofNullable(classMap.get(needle.getClass()));
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("FeatureRepository")
                .add("actionMap", actionMap)
                .add("classMap", classMap)
                .toString();
    }
}

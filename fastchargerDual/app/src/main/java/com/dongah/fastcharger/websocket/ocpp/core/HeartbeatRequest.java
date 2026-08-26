package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class HeartbeatRequest implements Request {

    private static final String ACTION_NAME = "Heartbeat";


    public String getActionName() {
        return ACTION_NAME;
    }

    public boolean validate() {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(HeartbeatRequest.class);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass();
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("isValid", validate()).toString();
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }
}

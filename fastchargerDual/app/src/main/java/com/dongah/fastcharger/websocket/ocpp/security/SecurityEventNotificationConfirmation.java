package com.dongah.fastcharger.websocket.ocpp.security;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class SecurityEventNotificationConfirmation implements Confirmation {

    private static final String ACTION_NAME = "SecurityEventNotification";

    public String getActionName() {
        return ACTION_NAME;
    }

    public SecurityEventNotificationConfirmation() {
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(LogStatusNotificationConfirmation.class);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass();
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("isValid", validate())
                .toString();
    }
}

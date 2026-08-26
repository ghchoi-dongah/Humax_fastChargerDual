package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.time.ZonedDateTime;
import java.util.Objects;

public class HeartbeatConfirmation implements Confirmation {

    private ZonedDateTime currentTime;

    public HeartbeatConfirmation() {
    }

    public HeartbeatConfirmation(ZonedDateTime currentTime) {
        this.currentTime = currentTime;
    }

    public boolean validate() {
        return currentTime != null;
    }

    public ZonedDateTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(ZonedDateTime currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTime);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HeartbeatConfirmation that = (HeartbeatConfirmation) obj;
        return Objects.equals(currentTime, that.currentTime);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("currentTime", currentTime)
                .add("isValid", validate())
                .toString();
    }
}

package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "statusNotificationResponse")
public class StatusNotificationConfirmation implements Confirmation {
    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(StatusNotificationConfirmation.class);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass();
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("isValid", validate()).toString();
    }
}

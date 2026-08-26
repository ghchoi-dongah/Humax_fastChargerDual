package com.dongah.fastcharger.websocket.ocpp.firmware;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sent by the Central System to the Charge Point in response to an {@link DiagnosticsStatusNotificationRequest}.
 */
@XmlRootElement(name = "diagnosticsStatusNotificationResponse")
public class DiagnosticsStatusNotificationConfirmation implements Confirmation {

    public DiagnosticsStatusNotificationConfirmation() {
    }

    @Override
    public int hashCode() {
        return Objects.hash(DiagnosticsStatusNotificationConfirmation.class);
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
    public boolean validate() {
        return true;
    }
}

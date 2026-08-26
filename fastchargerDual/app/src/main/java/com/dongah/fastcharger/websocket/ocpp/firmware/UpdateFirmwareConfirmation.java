package com.dongah.fastcharger.websocket.ocpp.firmware;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sent by the Charge Point to the Central System in response to an {@link UpdateFirmwareRequest}.
 */
@XmlRootElement(name = "updateFirmwareResponse")
public class UpdateFirmwareConfirmation implements Confirmation {

    public UpdateFirmwareConfirmation() {
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(UpdateFirmwareConfirmation.class);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        return this == o || o != null && getClass() == o.getClass();
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("isValid", validate()).toString();
    }
}

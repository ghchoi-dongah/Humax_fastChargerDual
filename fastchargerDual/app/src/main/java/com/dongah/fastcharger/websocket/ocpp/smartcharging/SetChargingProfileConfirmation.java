package com.dongah.fastcharger.websocket.ocpp.smartcharging;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "setChargingProfileResponse")
public class SetChargingProfileConfirmation implements Confirmation {

    private ChargingProfileStatus status;
    private static final String ACTION_NAME = "SetChargingProfile";

    /**
     * @deprecated use {@link #SetChargingProfileConfirmation(ChargingProfileStatus)} to be sure to set
     * required fields.
     */
    @Deprecated
    public SetChargingProfileConfirmation() {
    }

    /**
     * Handle required fields.
     *
     * @param status the {@link ChargingProfileStatus}, see {@link #setStatus(ChargingProfileStatus)}
     */
    public SetChargingProfileConfirmation(ChargingProfileStatus status) {
        setStatus(status);
    }

    public ChargingProfileStatus getStatus() {
        return status;
    }

    /**
     * Required. This indicates the success or failure of the change of the charging profile.
     *
     * @param status the {@link ChargingProfileStatus}
     */
    @XmlElement
    public void setStatus(ChargingProfileStatus status) {
        this.status = status;
    }

    @Deprecated
    public ChargingProfileStatus objStatus() {
        return status;
    }

    @Override
    public boolean validate() {
        return this.status != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetChargingProfileConfirmation that = (SetChargingProfileConfirmation) o;
        return status == that.status;
    }

    public String getActionName() {
        return ACTION_NAME;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("status", status)
                .add("isValid", validate())
                .toString();
    }
}

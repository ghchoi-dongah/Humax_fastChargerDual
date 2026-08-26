package com.dongah.fastcharger.websocket.ocpp.smartcharging;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "clearChargingProfileResponse")
public class ClearChargingProfileConfirmation implements Confirmation {

    private ClearChargingProfileStatus status;

    /**
     * @deprecated use {@link #ClearChargingProfileConfirmation(ClearChargingProfileStatus)} to be
     * sure to set required fields.
     */
    @Deprecated
    public ClearChargingProfileConfirmation() {
    }

    public static final String ACTION_NAME = "ClearChargingProfile";


    public String getActionName() {
        return ACTION_NAME;
    }

    /**
     * Handle required fields
     *
     * @param status the {@link ChargingProfileStatus}, see {@link #setStatus(ClearChargingProfileStatus)}
     */
    public ClearChargingProfileConfirmation(ClearChargingProfileStatus status) {
        setStatus(status);
    }

    public ClearChargingProfileStatus getStatus() {
        return status;
    }

    /**
     * Required. This indicates the success or failure of the change of the charging profile.
     *
     * @param status the {@link ClearChargingProfileStatus}
     */
    @XmlElement
    public void setStatus(ClearChargingProfileStatus status) {
        this.status = status;
    }

    @Override
    public boolean validate() {
        return this.status != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClearChargingProfileConfirmation that = (ClearChargingProfileConfirmation) o;
        return status == that.status;
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

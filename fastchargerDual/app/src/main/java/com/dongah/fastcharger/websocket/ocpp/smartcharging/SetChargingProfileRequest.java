package com.dongah.fastcharger.websocket.ocpp.smartcharging;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.core.ChargingProfile;
import com.dongah.fastcharger.websocket.ocpp.core.ChargingProfilePurposeType;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SetChargingProfileRequest implements Request {

    private Integer connectorId;
    private ChargingProfile csChargingProfiles;

    /**
     * @deprecated use {@link #SetChargingProfileRequest(Integer, ChargingProfile)} to be sure to set required fields.
     */
    @Deprecated
    public SetChargingProfileRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param connectorId        Integer. value 0, see {@link #setConnectorId(Integer)}
     * @param csChargingProfiles the {@link ChargingProfile}, see {@link #setCsChargingProfiles(ChargingProfile)}
     */
    public SetChargingProfileRequest(Integer connectorId, ChargingProfile csChargingProfiles) {
        setConnectorId(connectorId);
        setCsChargingProfiles(csChargingProfiles);
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    /**
     * Required. This identifies which connector of the Charge Point us used.
     *
     * @param connectorId integer. value 0.
     */
    @XmlElement
    public void setConnectorId(Integer connectorId) {
        if (connectorId == null || connectorId < 0) {
            throw new PropertyConstraintException(connectorId, "connectorId must be >= 0");
        }
        this.connectorId = connectorId;
    }

    public ChargingProfile getCsChargingProfiles() {
        return csChargingProfiles;
    }

    /**
     * Optional. Charging Profile to be used by the Charging Point for the requested transaction.
     * {@link ChargingProfile#setChargingProfilePurpose(ChargingProfilePurposeType)} MUST be set to TxProfile.
     *
     * @param csChargingProfiles the {@link ChargingProfile}
     */
    @XmlElement(name = "csChargingProfiles")
    public void setCsChargingProfiles(ChargingProfile csChargingProfiles) {
        this.csChargingProfiles = csChargingProfiles;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        boolean valid = connectorId != null && connectorId >= 0;
        if (csChargingProfiles != null) {
            valid &= csChargingProfiles.validate();
        }
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetChargingProfileRequest that = (SetChargingProfileRequest) o;
        return Objects.equals(connectorId, that.connectorId) && Objects.equals(csChargingProfiles, that.csChargingProfiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId, csChargingProfiles);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("connectorId", connectorId)
                .add("csChargingProfiles", csChargingProfiles)
                .add("isValid", validate())
                .toString();
    }
}

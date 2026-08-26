package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Sent to Charge Point by Central System
 */
@XmlRootElement
@XmlType(propOrder = {"connectorId", "idTag", "chargingProfile"})
public class RemoteStartTransactionRequest implements Request {

    private Integer connectorId;
    private String idTag;
    private ChargingProfile chargingProfile;

    /**
     * @deprecated use {@link #RemoteStartTransactionRequest(String)} to be sure to set required fields.
     */
    @Deprecated
    public RemoteStartTransactionRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param idTag a String with max lengrh 20, see {@link #setIdTag(String)}
     */
    public RemoteStartTransactionRequest(String idTag) {
        setIdTag(idTag);
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    /**
     * Optional. Number of the connector on which to start the transaction. ConnectorId SHALL be 0
     * 트랜잭션이 시작한 connector id
     *
     * @param connectorId Integer, connector
     */
    @XmlElement
    public void setConnectorId(Integer connectorId) {
        if (connectorId <= 0) {
            throw new PropertyConstraintException(connectorId, "connectorId must be > 0");
        }
        this.connectorId = connectorId;
    }

    public String getIdTag() {
        return idTag;
    }

    /**
     * Required. The identifier that Charge Point must use to start a transaction.
     *
     * @param idTag a String with max length 20
     */
    @XmlElement
    public void setIdTag(String idTag) {
        if (!ModelUtil.validate(idTag, 20)) {
            throw new PropertyConstraintException(idTag.length(), "Exceeded limit of 20 chars");
        }
        this.idTag = idTag;
    }

    public ChargingProfile getChargingProfile() {
        return chargingProfile;
    }

    /**
     * Optional. Charging Profile to be used by the Charging Point for the request transaction.
     * {@link ChargingProfile #setChargingProfilePurpose(ChargingProfilePurposeType)} must be set to TxProfile
     *
     * @param chargingProfile the {@link ChargingProfile}.
     */
    @XmlElement
    public void setChargingProfile(ChargingProfile chargingProfile) {
        this.chargingProfile = chargingProfile;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        boolean valid = ModelUtil.validate(idTag, 20);
        if (chargingProfile != null) {
            valid &= chargingProfile.validate();
        }
        if (connectorId != null) {
            valid &= connectorId > 0;
        }
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteStartTransactionRequest that = (RemoteStartTransactionRequest) o;
        return Objects.equals(connectorId, that.connectorId)
                && Objects.equals(idTag, that.idTag)
                && Objects.equals(chargingProfile, that.chargingProfile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId, idTag, chargingProfile);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("connectorId", connectorId)
                .add("idTag", idTag)
                .add("chargingProfile", chargingProfile)
                .add("isValid", validate())
                .toString();
    }
}

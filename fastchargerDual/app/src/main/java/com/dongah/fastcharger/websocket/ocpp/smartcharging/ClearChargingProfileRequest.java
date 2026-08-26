package com.dongah.fastcharger.websocket.ocpp.smartcharging;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.core.ChargingProfilePurposeType;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Central System => Charge Point charging profile 삭제 요청
 */
@XmlRootElement
public class ClearChargingProfileRequest implements Request {

    private Integer id;
    private Integer connectorId;
    private ChargingProfilePurposeType chargingProfilePurpose;
    private Integer stackLevel;

    public Integer getId() {
        return id;
    }

    /**
     * Optional. The ID of the charging profile to clear.
     *
     * @param id integer
     */
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    /**
     * Optional. Specifies the ID of the connector for which to clear charging profiles.
     *
     * @param connectorId integer. Value 0;
     */
    @XmlElement
    public void setConnectorId(Integer connectorId) {
        if (connectorId != null && connectorId < 0) {
            throw new PropertyConstraintException(connectorId, "connectorId must be >= 0");
        }
        this.connectorId = connectorId;
    }

    public ChargingProfilePurposeType getChargingProfilePurpose() {
        return chargingProfilePurpose;
    }

    /**
     * Optional. Specifies the purpose of the charging profiles that will be cleared, if they meet the
     * other criteria in the request.
     *
     * @param chargingProfilePurpose the {@link ChargingProfilePurposeType}
     */
    @XmlElement
    public void setChargingProfilePurpose(ChargingProfilePurposeType chargingProfilePurpose) {
        this.chargingProfilePurpose = chargingProfilePurpose;
    }

    public Integer getStackLevel() {
        return stackLevel;
    }

    /**
     * Optional. Specifies the stackLevel for which charging will be cleared, if they meet the other
     * criteria in the request.
     *
     * @param stackLevel integer. value 0;
     */
    @XmlElement
    public void setStackLevel(Integer stackLevel) {
        if (stackLevel != null && stackLevel < 0) {
            throw new PropertyConstraintException(stackLevel, "stackLevel must be >= 0");
        }
        this.stackLevel = stackLevel;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        boolean valid = connectorId == null || connectorId >= 0;
        valid &= stackLevel == null || stackLevel >= 0;

        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClearChargingProfileRequest that = (ClearChargingProfileRequest) o;
        return Objects.equals(id, that.id)
                && Objects.equals(connectorId, that.connectorId)
                && Objects.equals(chargingProfilePurpose, that.chargingProfilePurpose)
                && Objects.equals(stackLevel, that.stackLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, connectorId, chargingProfilePurpose, stackLevel);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("connectorId", connectorId)
                .add("chargingProfilePurpose", chargingProfilePurpose)
                .add("stackLevel", stackLevel)
                .add("isValid", validate())
                .toString();
    }
}

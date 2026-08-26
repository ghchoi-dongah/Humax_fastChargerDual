package com.dongah.fastcharger.websocket.ocpp.localauthlist;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Arrays;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;

/**
 * Sent by the Charge Point to the Central System in response to an {@link SendLocalListRequest}
 * Central System 은 Charge Point 가 idTag 의 승인을 위해 사용할 수 있는 Local Authorization List 송부
 * Update status == Failed/VersionMismatch (and) update Type == Differential 이면 Central System 은
 * updateType Full 을 사용하여 전테 로컬 인증 목록 send
 */
public class SendLocalListRequest implements Request {

    private Integer listVersion = 0;
    private AuthorizationData[] localAuthorizationList = null;
    private UpdateType updateType = null;

    /**
     * @deprecated use {@link #SendLocalListRequest(Integer, UpdateType)} to be sure to set required fields.
     */
    @Deprecated
    public SendLocalListRequest() {
    }

    /**
     * Handle require fields.
     *
     * @param listVersion the version number of the list, see {@link #setListVersion(Integer)}
     * @param updateType  {@link UpdateType}, see {@link #setUpdateType(UpdateType)}
     */
    public SendLocalListRequest(Integer listVersion, UpdateType updateType) {
        setListVersion(listVersion);
        setUpdateType(updateType);
    }

    public Integer getListVersion() {
        return listVersion;
    }

    /**
     * Required. In case of a full update this is the version number of the full list. In case of a
     * differential update it is the version number of the list after the update has been applied.
     *
     * @param listVersion Integer. The version number of the list
     */
    @XmlElement
    public void setListVersion(Integer listVersion) {
        if (listVersion < 1) {
            throw new PropertyConstraintException(listVersion, "listVersion must be > 0");
        }
        this.listVersion = listVersion;
    }

    public AuthorizationData[] getLocalAuthorizationList() {
        return localAuthorizationList;
    }

    /**
     * Optional. In case of a full update this contains the list of values that form the new local
     * authorization list. In case of a differential update it contains the changes to be applied to
     * the local authorization list in the Charge Point.
     *
     * @param localAuthorizationList Array of {@link AuthorizationData}
     */
    public void setLocalAuthorizationList(AuthorizationData[] localAuthorizationList) {
        this.localAuthorizationList = localAuthorizationList;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    /**
     * Required. This contains the type of update (full or differential) of this request.
     *
     * @param updateType, {@link UpdateType}
     */
    @XmlElement
    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        boolean valid = listVersion != null && listVersion >= 1 && updateType != null;
        if (localAuthorizationList != null) {
            for (AuthorizationData data : localAuthorizationList) {
                valid &= data.validate();
            }
        }
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SendLocalListRequest that = (SendLocalListRequest) o;
        return Objects.equals(listVersion, that.listVersion)
                && Arrays.equals(localAuthorizationList, that.localAuthorizationList)
                && updateType == that.updateType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(listVersion, Arrays.hashCode(localAuthorizationList), updateType);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("listVersion", listVersion)
                .add("localAuthorizationList", localAuthorizationList)
                .add("updateType", updateType)
                .add("isValid", validate())
                .toString();
    }
}

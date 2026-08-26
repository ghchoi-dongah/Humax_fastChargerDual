package com.dongah.fastcharger.websocket.ocpp.localauthlist;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class GetLocalListVersionConfirmation implements Confirmation {

    private Integer listVersion = -2;
    private static final String ACTION_NAME = "GetLocalListVersion";

    /**
     * @deprecated use {@link #GetLocalListVersionConfirmation(Integer)} to be sure to set required fields.
     */
    @Deprecated
    public GetLocalListVersionConfirmation() {
    }

    /**
     * Handle required fields.
     *
     * @param listVersion integer, version of localAuthList, see {@link #setListVersion(Integer)}
     */
    public GetLocalListVersionConfirmation(Integer listVersion) {
        setListVersion(listVersion);
    }


    public Integer getListVersion() {
        return listVersion;
    }

    public String getActionName() {
        return ACTION_NAME;
    }

    /**
     * Required. This contains the current version number of the local authorization list in the Charge Point
     * <p>
     * version number == 0 : 로컬 인증 목록이 비어 있음
     * version number == -1 : 로컬 인증 목록을 지원 안함.
     * </P>
     *
     * @param listVersion integer, version of localAuthList
     */
    public void setListVersion(Integer listVersion) {
        if (listVersion < -1) {
            throw new PropertyConstraintException(listVersion, "listVersion must be >= -1");
        }
        this.listVersion = listVersion;
    }


    @Override
    public boolean validate() {
        return listVersion != null && listVersion >= -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetLocalListVersionConfirmation that = (GetLocalListVersionConfirmation) o;
        return Objects.equals(listVersion, that.listVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listVersion);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("listVersion", listVersion)
                .add("isValid", validate())
                .toString();
    }
}

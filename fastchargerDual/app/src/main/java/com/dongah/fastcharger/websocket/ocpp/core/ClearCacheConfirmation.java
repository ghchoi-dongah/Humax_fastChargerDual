package com.dongah.fastcharger.websocket.ocpp.core;


import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Sent by the Charge Point to the Central System in response to a {@link ClearCacheRequest}
 */
@XmlRootElement(name = "clearCacheResponse")
public class ClearCacheConfirmation implements Confirmation {

    private ClearCacheStatus status;

    /**
     * @deprecated use {@link #ClearCacheConfirmation(ClearCacheStatus)} to be sure to set required fields.
     */
    @Deprecated
    public ClearCacheConfirmation() {
    }

    public static final String ACTION_NAME = "ClearCache";


    public String getActionName() {
        return ACTION_NAME;
    }

    /**
     * Handle required fields.
     *
     * @param status the {@link ClearCacheStatus}, see {@link #setStatus(ClearCacheStatus)}
     */
    public ClearCacheConfirmation(ClearCacheStatus status) {
        setStatus(status);
    }

    public ClearCacheStatus getStatus() {
        return status;
    }

    /**
     * Required. Accepted if the Change Point has executed the request, otherwiae rejected.
     *
     * @param status the {@link ClearCacheStatus}
     */
    public void setStatus(ClearCacheStatus status) {
        this.status = status;
    }

    /**
     * Accepted if the Charge Point has executed the request, otherwise rejected.
     *
     * @return the {@link ClearCacheStatus}
     */
    @Deprecated
    public ClearCacheStatus objStatus() {
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
        ClearCacheConfirmation that = (ClearCacheConfirmation) o;
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

package com.dongah.fastcharger.websocket.ocpp.localauthlist;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

public class GetLocalListVersionRequest implements Request {
    @Override
    public boolean transactionRelated() {
        return false;
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hash(GetLocalListVersionRequest.class);
    }


    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("isValid", validate()).toString();
    }
}

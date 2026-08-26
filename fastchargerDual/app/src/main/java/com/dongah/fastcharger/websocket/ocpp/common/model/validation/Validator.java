package com.dongah.fastcharger.websocket.ocpp.common.model.validation;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;

public abstract class Validator<T> {
    public boolean safeValidate(T value) {
        boolean returnValue = true;
        try {
            this.validate(value);
        } catch (Exception ex) {
            returnValue = false;
        }
        return returnValue;
    }

    public abstract void validate(T value) throws PropertyConstraintException;
}

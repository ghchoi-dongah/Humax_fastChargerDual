package com.dongah.fastcharger.websocket.ocpp.common.model.validation;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;

public interface IValidationRule {
    void validate(String value) throws PropertyConstraintException;
}

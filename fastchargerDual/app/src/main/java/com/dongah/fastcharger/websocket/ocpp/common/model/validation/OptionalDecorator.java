package com.dongah.fastcharger.websocket.ocpp.common.model.validation;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;

public class OptionalDecorator extends Validator<String> {

    private final Validator validator;

    public OptionalDecorator(Validator validator) {
        this.validator = validator;
    }

    @Override
    public void validate(String value) throws PropertyConstraintException {
        if (value == null) return;
        this.validator.validate(value);
    }
}

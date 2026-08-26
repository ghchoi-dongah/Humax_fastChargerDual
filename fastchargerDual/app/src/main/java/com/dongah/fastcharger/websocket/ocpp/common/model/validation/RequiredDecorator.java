package com.dongah.fastcharger.websocket.ocpp.common.model.validation;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;

public class RequiredDecorator extends Validator<Object> {
    private final Validator requiredValidator = new RequiredValidator();
    private final Validator decorate;

    public RequiredDecorator(Validator decorate) {
        this.decorate = decorate;
    }

    @Override
    public void validate(Object value) throws PropertyConstraintException {

    }
}

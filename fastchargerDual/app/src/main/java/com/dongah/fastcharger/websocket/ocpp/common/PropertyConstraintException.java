package com.dongah.fastcharger.websocket.ocpp.common;

public class PropertyConstraintException extends IllegalArgumentException {

    private static final String EXCEPTION_MESSAGE_TEMPLATE = "Validation failed: [%s]. Current Value: [%s]";

    public PropertyConstraintException(Object currentFiledValue, String errorMessage) {
        super(createValidationMessage(currentFiledValue, errorMessage));
    }

    private static String createValidationMessage(Object filedValue, String errorMessage) {
        return String.format(EXCEPTION_MESSAGE_TEMPLATE, errorMessage, filedValue);
    }
}

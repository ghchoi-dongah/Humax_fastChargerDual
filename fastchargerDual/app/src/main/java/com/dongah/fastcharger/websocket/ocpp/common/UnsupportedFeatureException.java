package com.dongah.fastcharger.websocket.ocpp.common;

public class UnsupportedFeatureException extends Exception {
    private static final long serialVersionUID = 9189571272082918907L;

    public UnsupportedFeatureException() {
        super();
    }

    public UnsupportedFeatureException(String message) {
        super(message);
    }
}

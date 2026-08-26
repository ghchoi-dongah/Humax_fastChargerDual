package com.dongah.fastcharger.basefunction;

public enum FileTransType {

    NONE(0),
    FIRMWARE(1),
    DIAGNOSTICS(2),
    SECURITY(3),
    SIGNED_FIRMWARE(4);

    private final int value;

    FileTransType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}

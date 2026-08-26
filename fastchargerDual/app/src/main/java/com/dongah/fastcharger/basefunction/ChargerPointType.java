package com.dongah.fastcharger.basefunction;

public enum ChargerPointType {
    NONE(0),
    AC3(1),
    CHADEMO(2),
    COMBO(3),
    BTYPE(4),
    CTYPE(5);

    ChargerPointType(int value) {
        this.value = value;
    }

    private final int value;

    public int value() {
        return value;
    }
}


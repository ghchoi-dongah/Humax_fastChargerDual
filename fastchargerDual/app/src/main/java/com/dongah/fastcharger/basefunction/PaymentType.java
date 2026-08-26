package com.dongah.fastcharger.basefunction;

public enum PaymentType {
    NONE(0),
    MEMBER(1),
    CREDIT(2),
    QR(3),
    APP(4),
    TEST(5),
    FREE(6);

    PaymentType(int value) {
        this.value = value;
    }

    private final int value;

    public int value() {
        return value;
    }
}

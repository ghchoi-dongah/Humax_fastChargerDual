package com.dongah.fastcharger.basefunction;

public enum UiSeq {
    NONE(0),
    INIT(1),
    CABLE_SELECT(2),
    AUTH_SELECT(3),
    SOC(4),
    MEMBER_CARD(5),
    MEMBER_CARD_WAIT(6),
    CREDIT_CARD(7),
    CREDIT_CARD_WAIT(8),
    QR_CODE(9),
    PLUG_CHECK(10),
    CONNECT_CHECK(11),
    RUN_CHECK(12),
    CHARGING(13),
    FAULT(14),
    FINISH_WAIT(15),
    FINISH(16),
    CHANGE_MODE(17),
    SMS(18),
    ADMIN_PASS(19),
    MESSAGE(20),
    REBOOTING(21),
    ENVIRONMENT(22),
    CONFIG_SETTING(23),
    CONTROL_BOARD_DEBUGGING(24),
    CHARGING_STOP_MESSAGE(25),
    WEB_SOCKET(26),
    LOAD_TEST(27),
    LOAD_TEST_TOTAL(28),
    LOAD_TEST_IO(29);

    private final int value;

    UiSeq(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

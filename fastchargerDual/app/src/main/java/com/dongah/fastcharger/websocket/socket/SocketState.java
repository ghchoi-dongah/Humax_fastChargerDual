package com.dongah.fastcharger.websocket.socket;

public enum SocketState {
    NONE(0),
    CLOSED(1),
    CLOSING(2),
    CONNECT_ERROR(3),
    RECONNECT_ATTEMPT(4),
    RECONNECTING(5),
    OPENING(6),
    OPEN(7);

    private final int value;

    SocketState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
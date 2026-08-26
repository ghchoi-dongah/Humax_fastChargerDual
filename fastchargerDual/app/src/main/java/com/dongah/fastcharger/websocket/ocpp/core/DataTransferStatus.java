package com.dongah.fastcharger.websocket.ocpp.core;

/**
 * Accepted values used with {@link DataTransferConfirmation}
 */
public enum DataTransferStatus {
    Accepted,
    Rejected,
    UnknownMessageId,
    UnknownVendorId
}

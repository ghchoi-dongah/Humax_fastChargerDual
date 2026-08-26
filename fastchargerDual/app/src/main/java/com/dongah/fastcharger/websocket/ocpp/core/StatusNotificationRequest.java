package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.time.ZonedDateTime;
import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Sent by the Charge Point to the Central System.
 */
@XmlRootElement
@XmlType(
        propOrder = {"connectorId", "status", "errorCode", "info",
                "timestamp", "vendorId", "vendorErrorCode"}
)

public class StatusNotificationRequest implements Request {

    private static final String ACTION_NAME = "StatusNotification";

    private static final String ERROR_MESSAGE = "Excess limit of %s chars";

    private Integer connectorId;
    private ChargePointErrorCode errorCode;
    private String info;
    private ChargePointStatus status;
    private ZonedDateTime timestamp;
    private String vendorId;
    private String vendorErrorCode;

    /**
     * @deprecated use {@link #StatusNotificationRequest(Integer, ChargePointErrorCode,
     * ChargePointStatus, ZonedDateTime)} to be sure to set required fields
     */
    @Deprecated
    public StatusNotificationRequest() {
    }

    public StatusNotificationRequest(ZonedDateTime timestamp) {
        setTimestamp(timestamp);
    }

    public StatusNotificationRequest(Integer connectorId, ChargePointErrorCode errorCode, ChargePointStatus status, ZonedDateTime timestamp) {
        setConnectorId(connectorId);
        setErrorCode(errorCode);
        setStatus(status);
        setTimestamp(timestamp);
    }

    private static String createErrorMessage(int maxLength) {
        return String.format(ERROR_MESSAGE, maxLength);
    }

    @Override
    public boolean validate() {
        boolean valid = isValidConnectorId(connectorId);
        valid &= errorCode != null;
        valid &= status != null;
        return valid;
    }

    /**
     * action name return
     *
     * @return String, Action name
     */
    public String getActionName() {
        return ACTION_NAME;
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    /**
     * (Required) The id of the connector for which the status is reported.
     *
     * @param connectorId integer, connector id. 0 = main controller.
     */
    public void setConnectorId(Integer connectorId) {
        if (!isValidConnectorId(connectorId)) {
            throw new PropertyConstraintException(connectorId, "connectorId >= 0");
        }
        this.connectorId = connectorId;
    }


    public ChargePointErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * (Required) This contains the error code reported by the Charge Point.
     *
     * @param errorCode the {@link ChargePointErrorCode}
     */
    public void setErrorCode(ChargePointErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Deprecated
    public ChargePointErrorCode objErrorCode() {
        return errorCode;
    }

    /**
     * Additional free format information related to the error
     *
     * @return Additional information
     */
    public String getInfo() {
        return info;
    }

    /**
     * Optional. Additional free format information related to the error
     *
     * @param info String, max 50 characters, case insensitive.
     */
    @XmlElement
    public void setInfo(String info) {
        if (!ModelUtil.validate(info, 50)) {
            throw new PropertyConstraintException(info.length(), createErrorMessage(50));
        }
        this.info = info;
    }

    public ChargePointStatus getStatus() {
        return status;
    }

    /**
     * (Required) This contains the current status of the Charge Point.
     *
     * @param status the {@link ChargePointStatus}
     */
    public void setStatus(ChargePointStatus status) {
        this.status = status;
    }

    @Deprecated
    public ChargePointStatus objStatus() {
        return status;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Optional. The time for which the status is reported. If absent time of receipt of the message
     * will ba assumed.
     *
     * @param timestamp ZoneDateTime, status time.
     */
    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Deprecated
    public ZonedDateTime objTimestamp() {
        return timestamp;
    }

    public String getVendorId() {
        return vendorId;
    }

    /**
     * Optional. This identifies the vendor-specific implementation.
     *
     * @param vendorId String, max 255 characters, case insensitive.
     */
    public void setVendorId(String vendorId) {
        if (!ModelUtil.validate(vendorId, 255)) {
            throw new PropertyConstraintException(vendorId.length(), createErrorMessage(255));
        }
        this.vendorId = vendorId;
    }

    public String getVendorErrorCode() {
        return vendorErrorCode;
    }

    /**
     * Optional. This contains the vendor-specific error code.
     *
     * @param vendorErrorCode String, max 50 characters, case insensitive.
     */
    @XmlElement
    public void setVendorErrorCode(String vendorErrorCode) {
        if (!ModelUtil.validate(vendorErrorCode, 50)) {
            throw new PropertyConstraintException(vendorErrorCode.length(), createErrorMessage(50));
        }
        this.vendorErrorCode = vendorErrorCode;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    private boolean isValidConnectorId(Integer connectorId) {
        return connectorId != null && connectorId >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusNotificationRequest that = (StatusNotificationRequest) o;
        return Objects.equals(connectorId, that.connectorId)
                && errorCode == that.errorCode
                && Objects.equals(info, that.info)
                && status == that.status
                && Objects.equals(timestamp, that.timestamp)
                && Objects.equals(vendorId, that.vendorId)
                && Objects.equals(vendorErrorCode, that.vendorErrorCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectorId, errorCode, info, status, timestamp, vendorId, vendorErrorCode);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("connectorId", connectorId)
                .add("errorCode", errorCode)
                .add("info", info)
                .add("status", status)
                .add("timestamp", timestamp)
                .add("vendorId", vendorId)
                .add("vendorErrorCode", vendorErrorCode)
                .add("isValid", validate())
                .toString();
    }
}

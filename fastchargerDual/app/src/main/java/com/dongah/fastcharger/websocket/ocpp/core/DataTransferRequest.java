package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Sent either by the Central System to the Charge Point or vice versa.
 * Central System ==> Charge Point or Charge Point ==> Central System 전송
 */
@XmlRootElement
@XmlType(propOrder = {"vendorId", "messageId", "data"})
public class DataTransferRequest implements Request {

    private static final String ACTION_NAME = "DataTransfer";
    private static final String ERROR_MESSAGE = "Exceeded limit of %s chars";

    private String actionName;
    private String vendorId;
    private String messageId;
    private String data;


    /**
     * @deprecated use {@link #DataTransferRequest(String, String)} to be sure to set required fields.
     */
    @Deprecated
    public DataTransferRequest() {
    }

    /**
     * Handle required fields.
     *
     * @param vendorId Vendor identification, see {@link #setMessageId(String)} {@link #setVendorId(String)}
     */
    public DataTransferRequest(String messageId, String vendorId) {
        setMessageId(messageId);
        setVendorId(vendorId);
    }

    public String getActionName() {
        return ACTION_NAME;
    }

    public String getVendorId() {
        return vendorId;
    }

    /**
     * Required. This identifies the Vendor specific implementation
     *
     * @param vendorId String, max 255 characters, case insensitive
     */
    public void setVendorId(String vendorId) {
        if (!isValidVendorId(vendorId)) {
            throw new PropertyConstraintException(vendorId.length(), createErrorMessage(255));
        }
        this.vendorId = vendorId;
    }

    private boolean isValidVendorId(String vendorId) {
        return ModelUtil.validate(vendorId, 255);
    }

    /**
     * Additional identification filed.
     *
     * @return Additional identification.
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Optional. Additional identification field.
     *
     * @param messageId String, max 50 characters, case insensitive.
     */
    @XmlElement
    public void setMessageId(String messageId) {
        if (!isValidMessageId(messageId)) {
            throw new PropertyConstraintException(messageId.length(), createErrorMessage(50));
        }
        this.messageId = messageId;
    }

    private boolean isValidMessageId(String messageId) {
        return ModelUtil.validate(messageId, 50);
    }


    /**
     * Data without specified length or format
     *
     * @return data
     */
    public String getData() {
        return data;
    }

    /**
     * Optional. Data without specified length or format
     *
     * @param data String, data
     */
    @XmlElement
    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }

    private String createErrorMessage(int maxLength) {
        return String.format(ERROR_MESSAGE, maxLength);
    }

    @Override
    public boolean validate() {
        return isValidVendorId(this.vendorId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataTransferRequest that = (DataTransferRequest) o;
        return Objects.equals(vendorId, that.vendorId)
                && Objects.equals(messageId, that.messageId)
                && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vendorId, messageId, data);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("vendorId", vendorId)
                .add("messageId", messageId)
                .add("data", data)
                .add("isValid", validate())
                .toString();
    }
}

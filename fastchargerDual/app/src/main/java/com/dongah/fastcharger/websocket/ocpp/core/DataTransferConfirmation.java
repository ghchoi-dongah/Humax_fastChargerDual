package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Sent by the Charge Point to the Central System ot vice versa in response to a {@link DataTransferRequest}
 */
@XmlRootElement(name = "dataTransferResponse")
@XmlType(propOrder = {"status", "data"})
public class DataTransferConfirmation implements Confirmation {

    private DataTransferStatus status;
    private String data;

    /**
     * @deprecated use {@link #DataTransferConfirmation(DataTransferStatus)} to be sure to set required fields.
     */
    @Deprecated
    public DataTransferConfirmation() {
    }

    /**
     * Handle required fields.
     *
     * @param status the {@link DataTransferStatus}, see {@link #setStatus(DataTransferStatus)}
     */
    public DataTransferConfirmation(DataTransferStatus status) {
        setStatus(status);
    }

    /**
     * This indicates the success or failure of the data transfer
     *
     * @return the {@link DataTransferStatus}
     */
    public DataTransferStatus getStatus() {
        return status;
    }

    /**
     * Required. This indicates the success or failure of the data transfer.
     *
     * @param status the {@link DataTransferStatus}.
     */
    public void setStatus(DataTransferStatus status) {
        this.status = status;
    }

    /**
     * This indicates the success or failure of the data transfer.
     *
     * @return the {@link DataTransferStatus}.
     */
    @Deprecated
    public DataTransferStatus objStatus() {
        return status;
    }

    public String getData() {
        return data;
    }

    /**
     * Optional. Data in response to request.
     *
     * @param data String, data
     */
    @XmlElement
    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean validate() {
        return this.status != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataTransferConfirmation that = (DataTransferConfirmation) o;
        return status == that.status && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, data);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("status", status)
                .add("data", data)
                .add("isValid", validate())
                .toString();
    }
}

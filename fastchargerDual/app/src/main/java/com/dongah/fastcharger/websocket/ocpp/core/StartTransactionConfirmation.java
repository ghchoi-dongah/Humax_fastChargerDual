package com.dongah.fastcharger.websocket.ocpp.core;

import androidx.annotation.NonNull;

import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.utilities.MoreObjects;

import java.util.Objects;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Sent by the Central System to the Charge Point in response to a {@link StartTransactionRequest}
 */
@XmlRootElement(name = "startTransactionResponse")
@XmlType(propOrder = {"transactionId", "idTagInfo"})
public class StartTransactionConfirmation implements Confirmation {

    private IdTagInfo idTagInfo;
    private Integer transactionId;

    /**
     * @deprecated use {!link #StartTransactionConfirmation(IdTagInfo,Integer)} to be sure to set
     * required fields
     */
    @Deprecated
    public StartTransactionConfirmation() {
    }

    /**
     * Handle required fields.
     *
     * @param idTagInfo     the {@link IdTagInfo}, see {@link #setIdTagInfo(IdTagInfo)}
     * @param transactionId integer, transaction, see {@link #setTransactionId(Integer)}
     */
    public StartTransactionConfirmation(IdTagInfo idTagInfo, Integer transactionId) {
        setIdTagInfo(idTagInfo);
        setTransactionId(transactionId);
    }

    public IdTagInfo getIdTagInfo() {
        return idTagInfo;
    }

    /**
     * Required. This contains information about authorization status, expiry and parent id.
     *
     * @param idTagInfo the {@link IdTagInfo}
     */
    @XmlElement
    public void setIdTagInfo(IdTagInfo idTagInfo) {
        this.idTagInfo = idTagInfo;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    /**
     * Required. This contains the transaction id supplied by the Central System.
     *
     * @param transactionId integer, transaction
     */
    @XmlElement
    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public boolean validate() {
        boolean valid = true;
        if (valid &= idTagInfo != null) valid &= idTagInfo.validate();
        valid &= transactionId != null;
        return valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StartTransactionConfirmation that = (StartTransactionConfirmation) o;
        return Objects.equals(idTagInfo, that.idTagInfo) && Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTagInfo, transactionId);
    }

    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("idTagInfo", idTagInfo)
                .add("transactionId", transactionId)
                .add("isValid", validate())
                .toString();
    }
}

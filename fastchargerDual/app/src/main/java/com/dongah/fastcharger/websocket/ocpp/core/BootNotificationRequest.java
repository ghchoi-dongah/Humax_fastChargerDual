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
 * Charge point -> Central System
 */
@XmlRootElement
@XmlType(
        propOrder = {
                "chargerPointVendor",
                "chargePointModel",
                "chargePointSerialNumber",
                "chargeBoxSerialNumber",
                "firmwareVersion",
                "iccid",
                "imsi",
                "meterType",
                "meterSerialNumber"
        }
)


public class BootNotificationRequest implements Request {
    private static final String ACTION_NAME = "BootNotification";
    private static final int STRING_20_CHAR_MAX_LENGTH = 20;
    private static final int STRING_25_CHAR_MAX_LENGTH = 25;
    private static final int STRING_50_CHAR_MAX_LENGTH = 50;
    private static final String ERROR_MESSAGE = "Exceeded limit of %s chars";

    private String chargePointVendor;
    private String chargePointModel;
    private String chargePointSerialNumber;
    private String chargeBoxSerialNumber;
    private String firmwareVersion;
    private String iccid;
    private String imsi;
    private String meterType;
    private String meterSerialNumber;
    //private IMKSocketReceiveMessage imkSocketReceiveMessage;


    @Deprecated
    public BootNotificationRequest() {
    }


    public BootNotificationRequest(String chargePointVendor, String chargePointModel) {
        setChargePointVendor(chargePointVendor);
        setChargePointModel(chargePointModel);
    }

    private static String validationErrorMessage(int maxAllowedLength) {
        return String.format(ERROR_MESSAGE, maxAllowedLength);
    }

    public String getChargePointVendor() {
        return chargePointVendor;
    }

    @XmlElement
    public void setChargePointVendor(String chargePointVendor) {
        if (!ModelUtil.validate(chargePointVendor, STRING_20_CHAR_MAX_LENGTH)) {
            throw new PropertyConstraintException(chargePointVendor.length(), validationErrorMessage(STRING_20_CHAR_MAX_LENGTH));
        }
        this.chargePointVendor = chargePointVendor;
    }

    public String getChargePointModel() {
        return chargePointModel;
    }

    @XmlElement
    public void setChargePointModel(String chargePointModel) {
        if (!ModelUtil.validate(chargePointModel, STRING_20_CHAR_MAX_LENGTH)) {
            throw new PropertyConstraintException(chargePointVendor.length(), validationErrorMessage(STRING_20_CHAR_MAX_LENGTH));
        }
        this.chargePointModel = chargePointModel;
    }

    @Deprecated
    public String getChargeBoxSerialNumber() {
        return chargeBoxSerialNumber;
    }

    @Deprecated
    public void setChargeBoxSerialNumber(String chargeBoxSerialNumber) {
        if (!ModelUtil.validate(chargeBoxSerialNumber, STRING_25_CHAR_MAX_LENGTH)) {
            throw new PropertyConstraintException(chargeBoxSerialNumber.length(), validationErrorMessage(STRING_25_CHAR_MAX_LENGTH));
        }
        this.chargeBoxSerialNumber = chargeBoxSerialNumber;
    }

    public String getChargePointSerialNumber() {
        return chargePointSerialNumber;
    }

    @XmlElement
    public void setChargePointSerialNumber(String chargePointSerialNumber) {
        if (!ModelUtil.validate(chargePointSerialNumber, STRING_25_CHAR_MAX_LENGTH)) {
            throw new PropertyConstraintException(chargeBoxSerialNumber.length(), validationErrorMessage(STRING_25_CHAR_MAX_LENGTH));
        }
        this.chargePointSerialNumber = chargePointSerialNumber;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    @XmlElement
    public void setFirmwareVersion(String firmwareVersion) {
        if (!ModelUtil.validate(firmwareVersion, STRING_50_CHAR_MAX_LENGTH)) {
            throw new PropertyConstraintException(firmwareVersion.length(), validationErrorMessage(STRING_50_CHAR_MAX_LENGTH));
        }
        this.firmwareVersion = firmwareVersion;
    }

    public String getIccid() {
        return iccid;
    }

    @XmlElement
    public void setIccid(String iccid) {
        if (!ModelUtil.validate(iccid, STRING_20_CHAR_MAX_LENGTH)) {
            throw new PropertyConstraintException(iccid.length(), validationErrorMessage(STRING_20_CHAR_MAX_LENGTH));
        }
        this.iccid = iccid;
    }

    public String getImsi() {
        return imsi;
    }

    @XmlElement
    public void setImsi(String imsi) {
        if (!ModelUtil.validate(imsi, STRING_20_CHAR_MAX_LENGTH)) {
            throw new PropertyConstraintException(imsi.length(), validationErrorMessage(STRING_20_CHAR_MAX_LENGTH));
        }
        this.imsi = imsi;
    }

    public String getMeterSerialNumber() {
        return meterSerialNumber;
    }

    @XmlElement
    public void setMeterSerialNumber(String meterSerialNumber) {
        if (!ModelUtil.validate(meterSerialNumber, STRING_25_CHAR_MAX_LENGTH)) {
            throw new PropertyConstraintException(
                    meterSerialNumber.length(), validationErrorMessage(STRING_25_CHAR_MAX_LENGTH));
        }
        this.meterSerialNumber = meterSerialNumber;
    }

    public String getMeterType() {
        return meterType;
    }

    @XmlElement
    public void setMeterType(String meterType) {
        if (!ModelUtil.validate(meterType, STRING_25_CHAR_MAX_LENGTH)) {
            throw new PropertyConstraintException(
                    meterType.length(), validationErrorMessage(STRING_25_CHAR_MAX_LENGTH));
        }
        this.meterType = meterType;
    }


    public String getActionName() {
        return ACTION_NAME;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BootNotificationRequest that = (BootNotificationRequest) o;
        return Objects.equals(chargePointVendor, that.chargePointVendor)
                && Objects.equals(chargePointModel, that.chargePointModel)
                && Objects.equals(chargeBoxSerialNumber, that.chargeBoxSerialNumber)
                && Objects.equals(chargePointSerialNumber, that.chargePointSerialNumber)
                && Objects.equals(firmwareVersion, that.firmwareVersion)
                && Objects.equals(iccid, that.iccid)
                && Objects.equals(imsi, that.imsi)
                && Objects.equals(meterSerialNumber, that.meterSerialNumber)
                && Objects.equals(meterType, that.meterType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                chargePointVendor,
                chargePointModel,
                chargeBoxSerialNumber,
                chargePointSerialNumber,
                firmwareVersion,
                iccid,
                imsi,
                meterSerialNumber,
                meterType);
    }


    @NonNull
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("chargePointVendor", chargePointVendor)
                .add("chargePointModel", chargePointModel)
                .add("chargeBoxSerialNumber", chargeBoxSerialNumber)
                .add("chargePointSerialNumber", chargePointSerialNumber)
                .add("firmwareVersion", firmwareVersion)
                .add("iccid", iccid)
                .add("imsi", imsi)
                .add("meterSerialNumber", meterSerialNumber)
                .add("meterType", meterType)
                .add("isValid", validate())
                .toString();
    }


    @Override
    public boolean validate() {
        return ModelUtil.validate(chargePointModel, STRING_20_CHAR_MAX_LENGTH)
                && ModelUtil.validate(chargePointVendor, STRING_20_CHAR_MAX_LENGTH);
    }

    @Override
    public boolean transactionRelated() {
        return false;
    }
}

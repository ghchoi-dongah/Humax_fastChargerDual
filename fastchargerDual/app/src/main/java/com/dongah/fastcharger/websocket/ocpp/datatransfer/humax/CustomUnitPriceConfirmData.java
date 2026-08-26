package com.dongah.fastcharger.websocket.ocpp.datatransfer.humax;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomUnitPriceConfirmData implements Validatable {


    private static final Logger logger = LoggerFactory.getLogger(CustomUnitPriceConfirmData.class);

    private static final int HM_CHARGING_LIMIT_FEE = 7;
    private static final String ERROR_MESSAGE = "Exceeded limit of " + HM_CHARGING_LIMIT_FEE + " chars";

    private String  hmCharingLimitFee;
    private CustomUnitPriceTariff tariff;


    public String getHmCharingLimitFee() {
        return hmCharingLimitFee;
    }

    public void setHmCharingLimitFee(String hmCharingLimitFee) {
        if (!ModelUtil.validate(hmCharingLimitFee, HM_CHARGING_LIMIT_FEE)) {
            throw new PropertyConstraintException(hmCharingLimitFee.length(), ERROR_MESSAGE);
        }
        this.hmCharingLimitFee = hmCharingLimitFee;
    }

    public CustomUnitPriceTariff getTariff() {
        return tariff;
    }

    public void setTariff(CustomUnitPriceTariff tariff) {
        this.tariff = tariff;
    }

    @Override
    public boolean validate() {
        return ModelUtil.validate(hmCharingLimitFee, HM_CHARGING_LIMIT_FEE);
    }
}

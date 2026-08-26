package com.dongah.fastcharger.websocket.ocpp.datatransfer.humax;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;

public class CustomUnitPriceTariff implements Validatable {

    private static final int HM_PRICE = 7;
    private static final String ERROR_MESSAGE = "Exceeded limit of " + HM_PRICE + " chars";

    private String startAt;
    private String endAt;
    private String price;


    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        if (!ModelUtil.validate(price, HM_PRICE)) {
            throw new PropertyConstraintException(price.length(), ERROR_MESSAGE);
        }
        this.price = price;
    }

    @Override
    public boolean validate() {
        return ModelUtil.validate(price, HM_PRICE);
    }
}

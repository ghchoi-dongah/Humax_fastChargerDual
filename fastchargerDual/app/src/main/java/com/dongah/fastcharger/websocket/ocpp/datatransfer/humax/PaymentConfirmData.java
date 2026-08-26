package com.dongah.fastcharger.websocket.ocpp.datatransfer.humax;

import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;

public class PaymentConfirmData  implements Validatable  {

    private String idTag;

    public PaymentConfirmData() {
    }

    public String getIdTag() {
        return idTag;
    }

    public void setIdTag(String idTag) {
        this.idTag = idTag;
    }

    @Override
    public boolean validate() {
        return idTag != null;
    }
}

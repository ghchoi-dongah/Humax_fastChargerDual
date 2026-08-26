package com.dongah.fastcharger.websocket.ocpp.datatransfer.dongah;

import com.dongah.fastcharger.websocket.ocpp.common.PropertyConstraintException;
import com.dongah.fastcharger.websocket.ocpp.common.model.Validatable;
import com.dongah.fastcharger.websocket.ocpp.utilities.ModelUtil;

public class GetPriceData implements Validatable {
    private static final int ID_TAG_MAX_LENGTH = 20;
    private static final String ERROR_MESSAGE = "Exceeded limit of " + ID_TAG_MAX_LENGTH + " chars";
    String timestamp;

    String idTag;

    public GetPriceData() {
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIdTag() {
        return idTag;
    }

    public void setIdTag(String idTag) {
        if (!ModelUtil.validate(idTag, ID_TAG_MAX_LENGTH)) {
            throw new PropertyConstraintException(idTag.length(), ERROR_MESSAGE);
        }
        this.idTag = idTag;
    }

    @Override
    public boolean validate() {
        return ModelUtil.validate(idTag, ID_TAG_MAX_LENGTH);
    }
}

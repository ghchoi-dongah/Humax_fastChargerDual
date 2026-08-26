package com.dongah.fastcharger.websocket.ocpp.common;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dongah.fastcharger.websocket.ocpp.common.model.CallErrorMessage;
import com.dongah.fastcharger.websocket.ocpp.common.model.CallMessage;
import com.dongah.fastcharger.websocket.ocpp.common.model.CallResultMessage;
import com.dongah.fastcharger.websocket.ocpp.common.model.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class JSONCommunicator extends Communicator {

    private static final Logger logger = LoggerFactory.getLogger(JSONCommunicator.class);
    private static final int INDEX_MESSAGE_ID = 0;
    private static final int TYPE_NUMBER_CALL = 2;
    private static final int INDEX_CALL_ACTION = 2;
    private static final int INDEX_CALL_PAYLOAD = 3;

    private static final int TYPE_NUMBER_CALL_RESULT = 3;
    private static final int INDEX_CALL_RESULT_PAYLOAD = 2;

    private static final int TYPE_NUMBER_CALL_ERROR = 4;
    private static final int INDEX_CALL_ERROR_ERROR_CODE = 2;
    private static final int INDEX_CALL_ERROR_DESCRIPTION = 3;
    private static final int INDEX_CALL_ERROR_PAYLOAD = 4;

    private static final int INDEX_UNIQUE_ID = 1;
    private static final String CALL_FORMAT = "[2, \"%s\", \"%s\", %s]";
    private static final String CALL_RESULT_FORMAT = "[3,\"%s\",%s]";
    private static final String CALL_ERROR_FORMAT = "[4,\"%s\",\"%s\",\"%s\",%s]";

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String DATE_FORMAT_WITH_MS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final int DATE_FORMAT_WITH_MS_LENGTH = 24;
    private boolean hasLongDateFormat = false;

//    public JSONCommunicator() {
//
//    }

    /**
     *
     **/
    private class ZonedDateTimeSerializer implements JsonSerializer<ZonedDateTime> {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public JsonElement serialize(ZonedDateTime zonedDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(zonedDateTime.format(DateTimeFormatter.ISO_INSTANT));
        }
    }

    public class ZonedDateTimeDeserializer implements JsonDeserializer<ZonedDateTime> {

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public ZonedDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return ZonedDateTime.parse(jsonElement.getAsJsonPrimitive().getAsString());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public <T> T unpackPayload(Object payload, Class<T> type) throws Exception {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeDeserializer());
        Gson gson = builder.create();
        return gson.fromJson(payload.toString(), type);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Object packPayload(Object payload) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeSerializer());
        Gson gson = builder.create();
        return gson.toJson(payload);
    }

    @Override
    protected Object makeCallResult(String uniqueId, String action, Object payload) {
        return String.format(CALL_RESULT_FORMAT, uniqueId, payload);
    }

    @Override
    public Object makeCall(String uniqueId, String action, Object payload) {
        return String.format(CALL_FORMAT, uniqueId, action, payload);
    }

    @Override
    protected Object makeCallError(String uniqueId, String action, String errorCode, String errorDescription) {
        return String.format(CALL_ERROR_FORMAT, uniqueId, errorCode, errorDescription, "{}");
    }

    @Override
    protected Message parse(Object json) {
        Message message;
        try {
            JsonArray array = JsonParser.parseString(json.toString()).getAsJsonArray();
            if (array.get(INDEX_MESSAGE_ID).getAsInt() == TYPE_NUMBER_CALL) {
                message = new CallMessage();
                message.setResultType(array.get(INDEX_MESSAGE_ID).getAsInt());
                message.setId(array.get(INDEX_UNIQUE_ID).getAsString());
                message.setAction(array.get(INDEX_CALL_ACTION).getAsString());
                message.setPayload(array.get(INDEX_CALL_PAYLOAD).toString());
            } else if (array.get(INDEX_MESSAGE_ID).getAsInt() == TYPE_NUMBER_CALL_RESULT) {
                message = new CallResultMessage();
                message.setResultType(array.get(INDEX_MESSAGE_ID).getAsInt());
                message.setId(array.get(INDEX_UNIQUE_ID).getAsString());
                message.setPayload(array.get(INDEX_CALL_RESULT_PAYLOAD).toString());
            } else if (array.get(INDEX_MESSAGE_ID).getAsInt() == TYPE_NUMBER_CALL_ERROR) {
                message = new CallErrorMessage();
                ((CallErrorMessage) message).setResultType(array.get(INDEX_MESSAGE_ID).getAsInt());
                ((CallErrorMessage) message).setErrorCode(array.get(INDEX_CALL_ERROR_ERROR_CODE).getAsString());
                ((CallErrorMessage) message).setErrorDescription(array.get(INDEX_CALL_ERROR_DESCRIPTION).getAsString());
                ((CallErrorMessage) message).setRawPayload(array.get(INDEX_CALL_ERROR_PAYLOAD).toString());
            } else {
                logger.error("Unknown message type of message : {}", json.toString());
                throw new IllegalArgumentException("Unknown message type ");
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = null;
        }
        return message;
    }


}

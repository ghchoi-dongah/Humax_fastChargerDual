package com.dongah.fastcharger.websocket.ocpp.common;

import com.dongah.fastcharger.websocket.ocpp.common.model.CallMessage;
import com.dongah.fastcharger.websocket.ocpp.common.model.CallResultMessage;
import com.dongah.fastcharger.websocket.ocpp.common.model.Confirmation;
import com.dongah.fastcharger.websocket.ocpp.common.model.Message;
import com.dongah.fastcharger.websocket.ocpp.common.model.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Communicator {
    private static final Logger logger = LoggerFactory.getLogger(Communicator.class);

    /**
     * Convert a formatted string into a {@link Request}/{@link Confirmation}. This is useful for call
     * results, where the confirmation type isn't given.
     *
     * @param payload the raw formatted payload.
     * @param type    the exception return type.
     * @param <T>     unpacked payload.
     * @return the unpacked payload
     * @throws Exception error occurred while converting
     */
    public abstract <T> T unpackPayload(Object payload, Class<T> type) throws Exception;


    /**
     * Convert a {@link Request}/{@link Confirmation} into a formatted string.
     *
     * @param payload yje payload model.
     * @return the payload in the form of a formatted string.
     */
    public abstract Object packPayload(Object payload);

    /**
     * Create a call result envelope to transmit.
     *
     * @param uniqueId the id the receiver expects.
     * @param action   action name of the feature.
     * @param payload  packed payload
     * @return a fully packed message ready to send
     */
    protected abstract Object makeCallResult(String uniqueId, String action, Object payload);

    /**
     * Create a call envelope to transmit to the server.
     *
     * @param uniqueId the id the received must replay with
     * @param action   action name of the feature
     * @param payload  packed payload
     * @return a fully packed message ready to send
     */
    protected abstract Object makeCall(String uniqueId, String action, Object payload);

    /**
     * Create a call error envelope to transmit.
     *
     * @param uniqueId         the id the receiver expects.
     * @param errorCode        an OCPP error code.
     * @param errorDescription an associated error description.
     * @return a fully packed message ready to send.
     */
    protected abstract Object makeCallError(String uniqueId, String action, String errorCode, String errorDescription);

    /**
     * Identify an incoming call and parse it into one of the following:
     * {@link CallMessage} a request(요청). {@link CallResultMessage} a response(응답).
     *
     * @param Message the raw message
     * @return CallMessage or {@link CallResultMessage}
     */
    protected abstract Message parse(Object Message);
}

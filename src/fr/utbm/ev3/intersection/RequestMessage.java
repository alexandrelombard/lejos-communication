package fr.utbm.ev3.intersection;

import fr.utbm.ev3.network.ObjectMessage;

import java.io.IOException;

/**
 * Represents a message containing a request.
 * @author Alexandre Lombard
 */
public class RequestMessage extends ObjectMessage<Request> {
    /**
     * Builds a request message
     * @param request the request
     */
    public RequestMessage(Request request) {
        super(IntersectionManager.REQUESTS_TOPIC, request);
    }
}

package fr.utbm.ev3.network;

import java.io.Serializable;

/**
 * Represents a message
 * @author Alexandre Lombard
 */
public class Message implements Serializable {

    private final String topic;
    private final byte[] content;

    /**
     * Builds a message
     * @param topic the topic of the message
     * @param content the content of the message
     */
    public Message(String topic, byte[] content) {
        this.topic = topic;
        this.content = content;
    }

    /**
     * Gets the topic
     * @return the topic
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Gets the content
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }
}

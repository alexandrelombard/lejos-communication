package fr.utbm.ev3.network;

import java.io.*;

/**
 * Represents a message containing a generic object
 * @author Alexandre Lombard
 */
public class ObjectMessage<T> extends Message {

    private transient T object;

    /**
     * Builds a message
     *
     * @param topic   the topic of the message
     * @param content the content of the message
     */
    public ObjectMessage(String topic, T content) {
        super(topic, serialize(content));
    }

    /**
     * Converts an object to a byte array
     * @param object the object
     * @return the serialized object
     */
    private static byte[] serialize(Object object) {
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();

            return bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Deserialize the byte array of the message to an object.
     * The deserialization is called only once, then it is cached for optimization.
     * @return the object or null if deserialization failed
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public T getObject() throws IOException, ClassNotFoundException {
        if(object == null) {
            final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(getContent()));
            this.object = (T) ois.readObject();
        }

        return this.object;
    }

    /**
     * Deserialize the byte array of the message to an object.
     * The deserialization is called only once, then it is cached for optimization.
     * @return the object or null if deserialization failed
     */
    public T unsafeGetObject() {
        try {
            return getObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}

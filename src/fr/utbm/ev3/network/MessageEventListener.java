package fr.utbm.ev3.network;

/**
 * Message listener.
 * @author Alexandre Lombard
 */
public interface MessageEventListener {
    /**
     * Called when a broadcast message is received.
     * @param message the received message
     */
    void onMessageReceived(Message message);
}

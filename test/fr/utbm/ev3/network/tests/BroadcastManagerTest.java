package fr.utbm.ev3.network.tests;

import fr.utbm.ev3.network.BroadcastManager;
import fr.utbm.ev3.network.Message;
import fr.utbm.ev3.network.MessageEventListener;

import java.io.IOException;

/**
 * Test class for the broadcast manager. Note: not a unit test (it's based on the availability of a network interface
 * supporting broadcast).
 * @author Alexandre Lombard
 */
public class BroadcastManagerTest {

    /**
     * Main function
     * @param args command line arguments (ignored)
     */
    public static void main(String[] args) throws IOException {
        final BroadcastManager broadcastManager = BroadcastManager.getInstance();

        broadcastManager.subscribe("hello", new MessageEventListener() {
            @Override
            public void onMessageReceived(Message message) {
                System.out.println(new String(message.getContent()));
            }
        });

        broadcastManager.publish(new Message("hello", "world".getBytes()));
        broadcastManager.publish(new Message("hello", "world".getBytes()));
        broadcastManager.publish(new Message("hello", "world".getBytes()));
    }

}

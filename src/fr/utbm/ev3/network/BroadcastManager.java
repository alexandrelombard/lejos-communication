package fr.utbm.ev3.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;

/**
 * Broadcast manager ensuring UDP based communication between Lejos EV3 robots and other systems.
 * @author Alexandre Lombard
 */
public final class BroadcastManager {

    /** The address used for broadcast */
    private static final InetAddress BROADCAST_ADDRESS;

    static {
        try {
            BROADCAST_ADDRESS = InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            // In case of unknown host, we throw a runtime exception to abort the execution
            throw new IllegalStateException(e);
        }
    }

    private static BroadcastManager instance;

    private final Map<String, List<MessageEventListener>> listeners = new HashMap<>();

    // region Configuration
    private int port = 4242;
    // endregion

    /** Private constructor (singleton pattern) */
    private BroadcastManager() {
        //
    }

    /**
     * Gets the instance of the broadcast manager
     * @return the instance of the broadcast manager
     */
    public static BroadcastManager getInstance() {
        if(instance == null)
            instance = new BroadcastManager();
        return instance;
    }

    /**
     * Gets the port used for broadcast communication
     * @return the UDP port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port used for broadcast communication
     * @param port the UDP port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Publishes (broadcast) a message
     * @param message the message
     * @throws IOException thrown in case of failure when initializing the socket,
     *                     or when sending the message
     */
    public void publish(Message message) throws IOException {
        // Socket initialization
        final DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);

        // Serializing and sending the message
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(message);
        out.flush();

        final byte[] messageBuffer = bos.toByteArray();
        final DatagramPacket packet =
                new DatagramPacket(messageBuffer, messageBuffer.length, BROADCAST_ADDRESS, port);
        socket.send(packet);
        socket.close();
    }

    /**
     * Subscribes to a given topic
     * @param topic the topic
     * @param listener the message event listener
     */
    public void subscribe(String topic, MessageEventListener listener) {
        if(!listeners.containsKey(topic)) {
            listeners.put(topic, new ArrayList<MessageEventListener>());
        }

        listeners.get(topic).add(listener);
    }

    /**
     * Un-subscribes to the given topic
     * @param topic the topic
     * @param listener the listener to remove
     * @return <code>true</code> if the listener was removed, <code>false</code> if nothing was done
     */
    public boolean unsubscribe(String topic, MessageEventListener listener) {
        if(listeners.containsKey(topic)) {
            return listeners.get(topic).remove(listener);
        }

        return false;
    }

    /**
     * Utility function retrieving all the broadcast addresses
     * @return the list of broadcast addresses
     * @throws SocketException thrown in case of failure when retrieving the network interfaces
     */
    private static List<InetAddress> listAllBroadcastAddresses() throws SocketException {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces
                = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            for(InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                final InetAddress broadcast = address.getBroadcast();
                if(broadcast != null) {
                    broadcastList.add(broadcast);
                }
            }
        }
        return broadcastList;
    }
}

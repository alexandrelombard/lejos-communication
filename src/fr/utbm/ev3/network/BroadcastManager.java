package fr.utbm.ev3.network;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private final Map<String, List<MessageEventListener>> listeners = new ConcurrentHashMap<>();

    /** The executor service for the listening task */
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    // region Configuration
    private int port = 4242;
    // endregion

    /** Private constructor (singleton pattern) */
    private BroadcastManager(boolean autostart) {
        if(autostart) {
            start();
        }
    }

    /**
     * Gets the instance of the broadcast manager
     * @return the instance of the broadcast manager
     */
    public static BroadcastManager getInstance() {
        return getInstance(true);
    }

    /**
     * Gets the instance of the broadcast manager.
     * @param autostart if it is the first call, setting this to <code>true</code> will auto-start the reception
     *                  of messages
     * @return the instance of the broadcast manager
     */
    public static BroadcastManager getInstance(boolean autostart) {
        if(instance == null)
            instance = new BroadcastManager(autostart);
        return instance;
    }

    /**
     * Starts receiving message (usually not called manually)
     */
    public void start() {
        final Runnable listeningTask = new Runnable() {
            @Override
            public void run() {
                try {
                    final DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));
                    final byte[] buffer = new byte[10 * 1024];

                    while (true) {
                        final DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
                        try {
                            socket.receive(packet);

                            final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buffer));
                            final Object object = ois.readObject();

                            if (object instanceof Message) {
                                fireMessageReceived((Message) object);
                            }
                            // Note: Ignoring unsupported messages
                        } catch (IOException e) {
                            // Just in case, the exception will be ignored but logged
                            System.err.println("Severe (listening): " + e.getMessage());
                        } catch (ClassNotFoundException e) {
                            // Just in case, the exception will be ignored but logged
                            System.err.println("Severe (wrong message): " + e.getMessage());
                        }
                    }
                } catch (SocketException | UnknownHostException e) {
                    // Note: fatal error
                    System.err.println("Fatal error (listening): " + e.getMessage());
                }
            }
        };

        executorService.submit(listeningTask);
    }

    /**
     * Stops receiving messages
     */
    public void stop() {
        executorService.shutdownNow();
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Restarts the reception of messages
     */
    public void restart() {
        stop();
        start();
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
        restart();
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
     * Emits the reception of a message
     * @param message the received message
     */
    private void fireMessageReceived(Message message) {
        final List<MessageEventListener> topicListeners = listeners.get(message.getTopic());

        if(topicListeners != null) {
            for(MessageEventListener l : topicListeners) {
                l.onMessageReceived(message);
            }
        }
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

package fr.utbm.ev3.intersection;

import fr.utbm.ev3.network.BroadcastManager;
import fr.utbm.ev3.network.Message;
import fr.utbm.ev3.network.MessageEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Intersection manager, in charge of receiving requests and emitting the right-of-way
 * @author Alexandre Lombard
 */
public final class IntersectionManager {

    public static final int PERIOD_MS = 1000;
    public static final String REQUESTS_TOPIC = "REQUESTS";
    public static final String PRESENCE_LIST_TOPIC = "PRESENCE_LIST";

    private static final Logger LOG = Logger.getLogger(IntersectionManager.class.getName());
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private static IntersectionManager instance;

    private List<VehicleData> presenceList = new ArrayList<>();

    private IntersectionManager() {
        // Register the listener which will process the requests
        BroadcastManager.getInstance().subscribe(REQUESTS_TOPIC, new MessageEventListener() {
            @Override
            public void onMessageReceived(Message message) {
                if(message instanceof RequestMessage) {
                    final RequestMessage requestMessage = (RequestMessage) message;
                    final Request request = requestMessage.unsafeGetObject();

                    final VehicleData vehicleData = request.getVehicleData();
                    final int index = indexOf(vehicleData);

                    LOG.info(request.toString());

                    switch (request.getRequestType()) {
                        // Note: ENTER and UPDATE don't need to be managed differently
                        case ENTER:
                        case UPDATE:
                            if(index != -1) {
                                // If already present, just refresh
                                presenceList.set(index, vehicleData);
                            } else {
                                // Else, add to the list
                                presenceList.add(vehicleData);
                            }
                            break;
                        case EXIT:
                            // We just remove the data from the presence if present
                            if(index != -1) {
                                presenceList.remove(index);
                            }
                            break;
                    }
                }
            }
        });

        // Start the emission service (auto-start)
        start();
    }

    /**
     * Starts the service
     */
    public void start() {
        // Stops currently running thread
        stop();

        // Schedules the task which will regularly emit the presence list
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    BroadcastManager.getInstance().publish(new PresenceListMessage(presenceList));
                } catch (IOException e) {
                    System.err.println("Severe (unable to send presence list): " + e.getMessage());
                }
            }
        }, 0, PERIOD_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the service
     */
    public void stop() {
        executorService.shutdown();
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Restart the service
     */
    public void restart() {
        stop();
        start();
    }

    /**
     * Gets an instance of the intersection manager.
     * @return the instance of the intersection manager
     */
    public static IntersectionManager getInstance() {
        if(instance == null)
            instance = new IntersectionManager();
        return instance;
    }

    private boolean isPresent(VehicleData vehicleData) {
        for(final VehicleData v : presenceList) {
            if(v.getId().equals(vehicleData.getId()))
                return true;
        }
        return false;
    }

    private int indexOf(VehicleData vehicleData) {
        for(int idx = 0; idx < presenceList.size(); idx++) {
            if(presenceList.get(idx).getId().equals(vehicleData.getId()))
                return idx;
        }
        return -1;
    }

}

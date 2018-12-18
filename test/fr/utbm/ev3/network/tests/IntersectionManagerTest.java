package fr.utbm.ev3.network.tests;

import fr.utbm.ev3.intersection.IntersectionManager;
import fr.utbm.ev3.intersection.Request;
import fr.utbm.ev3.intersection.RequestMessage;
import fr.utbm.ev3.intersection.VehicleData;
import fr.utbm.ev3.network.BroadcastManager;

import java.io.IOException;
import java.util.UUID;

/**
 * Test class for the intersection manager (note: this is not a unit test as it relies on the BroadcastManager)
 * @author Alexandre Lombard
 */
public class IntersectionManagerTest {

    /**
     * Main function
     * @param args command line arguments (ignored)
     */
    public static void main(String[] args) throws IOException {
        // Just starts the intersection  manager
        final IntersectionManager intersectionManager = IntersectionManager.getInstance();

        // Emit (for test)
        final BroadcastManager broadcastManager = BroadcastManager.getInstance();
        broadcastManager.publish(
                new RequestMessage(
                        new Request(
                                Request.RequestType.ENTER,
                                new VehicleData(UUID.randomUUID(), 1.0, 2))));
    }

}

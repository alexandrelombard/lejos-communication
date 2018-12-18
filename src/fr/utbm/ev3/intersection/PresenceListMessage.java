package fr.utbm.ev3.intersection;

import fr.utbm.ev3.network.ObjectMessage;

import java.util.List;

/**
 * Message containing the presence list
 * @author Alexandre Lombard
 */
public class PresenceListMessage extends ObjectMessage<List<VehicleData>> {
    /**
     * Builds a message
     *
     * @param presenceList the presence list
     */
    public PresenceListMessage(List<VehicleData> presenceList) {
        super(IntersectionManager.PRESENCE_LIST_TOPIC, presenceList);
    }
}

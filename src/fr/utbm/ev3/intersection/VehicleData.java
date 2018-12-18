package fr.utbm.ev3.intersection;

import java.io.Serializable;
import java.util.UUID;

/**
 * Contains the data about a vehicle (a robot)
 * @author Alexandre Lombard
 */
public class VehicleData implements Serializable {

    /** The identifier of the robot */
    private UUID id;

    /** The position (usually the distance from the red zone) */
    private double position;

    /** The identifier of the lane */
    private int laneId;

    /**
     * Builds a vehicle data object
     * @param id the ID of the vehicle
     * @param position the position of the vehicle
     * @param laneId the ID of the lane where the vehicle is
     */
    public VehicleData(UUID id, double position, int laneId) {
        this.id = id;
        this.position = position;
        this.laneId = laneId;
    }

    /**
     * Gets the ID of the vehicle
     * @return the ID of the vehicle
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the position of the vehicle
     * @return the position of the vehicle
     */
    public double getPosition() {
        return position;
    }

    /**
     * Gets the ID of the lane of the vehicle
     * @return the ID of the lane of the vehicle
     */
    public int getLaneId() {
        return laneId;
    }

    @Override
    public String toString() {
        return "VehicleData{" +
                "id=" + id +
                ", position=" + position +
                ", laneId=" + laneId +
                '}';
    }
}

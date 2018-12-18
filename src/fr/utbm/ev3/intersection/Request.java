package fr.utbm.ev3.intersection;

import java.io.Serializable;

/**
 * Represents a request for the right-of-way (TIM protocol)
 * @author Alexandre Lombard
 */
public class Request implements Serializable {

    /** The request type */
    private RequestType requestType;

    /** The data of the vehicle */
    private VehicleData vehicleData;

    /**
     * Builds a request for the right-of-way.
     * @param requestType the request type
     * @param vehicleData the vehicle data
     */
    public Request(RequestType requestType, VehicleData vehicleData) {
        this.requestType = requestType;
        this.vehicleData = vehicleData;
    }

    /**
     * Gets the request type
     * @return the request type
     */
    public RequestType getRequestType() {
        return requestType;
    }

    /**
     * Gets the vehicle data
     * @return the vehicle data
     */
    public VehicleData getVehicleData() {
        return vehicleData;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestType=" + requestType +
                ", vehicleData=" + vehicleData +
                '}';
    }

    /**
     * Represents a type of request
     */
    public enum RequestType {
        /** The robot is approaching the intersection and not in the presence list */
        ENTER,
        /** The robot is in the presence list and still in the intersection */
        UPDATE,
        /** The robot is still in the presence list but is leaving the intersection */
        EXIT
    }
}

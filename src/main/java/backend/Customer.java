package backend;

import org.json.JSONObject;

public class Customer {
    private boolean awaitingService;
    private double coordX;
    private double coordY;
    private double destX;
    private double destY;
    private String id;

    /**
     * Construct a Customer from a respective json String
     * @param jsonString jsonString to construct it
     */
    public Customer(String jsonString){
        JSONObject object = new JSONObject(jsonString);

        coordX = object.getDouble("coordX");
        coordY = object.getDouble("coordY");
        destX = object.getDouble("destinationX");
        destY = object.getDouble("destinationY");
        id = object.getString("id");
        awaitingService = object.getBoolean("awaitingService");
    }

    public boolean isAwaitingService() {
        return awaitingService;
    }

    public double getCoordX() {
        return coordX;
    }

    public double getCoordY() {
        return coordY;
    }

    public double getDestX() {
        return destX;
    }

    public double getDestY() {
        return destY;
    }

    public String getId() {
        return id;
    }
}

package backend;

import org.json.JSONObject;

public class Customer {
    boolean awaitingService;
    double coordX;
    double coordY;
    double destX;
    double destY;
    String id;

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
}

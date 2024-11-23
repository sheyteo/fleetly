package backend;

import org.json.JSONObject;

public class Vehicle {
    double coordX;
    double coordY;
    Customer customer;
    final String id;
    boolean isAvailable;

    int tripCounter;
    double remainingTravelTime;
    float speed;
    int activeTime;
    float distanceTravelled;

    /**
     * Construct a Vehicle from a respective JSON String
     * @param jsonString to get all Information from
     */
    public Vehicle(String jsonString) {
        // These Things have to be in the Json always
        JSONObject object = new JSONObject(jsonString);
        coordX = object.getDouble("coordX");
        coordY = object.getDouble("coordY");
        id = object.getString("id");
        isAvailable = object.getBoolean("isAvailable");

        // These things normally are not in the JSON -> Standard Values
        distanceTravelled = object.optFloat("distanceTravelled", 0.0f);
        tripCounter = object.optInt("numberOfTrips", 0);
        remainingTravelTime = object.optDouble("remainingTravelTime", 0.0d);
        speed = object.optFloat("vehicleSpeed", 0.0f);
        activeTime = object.optInt("activeTime", 0);

        // Customer is empty at start
        customer = null;
    }
}
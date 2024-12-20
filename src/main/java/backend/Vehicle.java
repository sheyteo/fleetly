package backend;

import org.json.JSONObject;

import java.time.LocalTime;
import java.time.Duration;

public class Vehicle {
    private double coordX;
    private double coordY;
    private String customerID;
    private final String id;
    private boolean isAvailable;

    private int tripCounter;
    private double remainingTravelTime;
    private LocalTime arrivesAtTime;
    private float speed;
    private int activeTime;
    private float distanceTravelled;

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
        speed = util.convertKPHtoMPS(object.optFloat("vehicleSpeed", 0.0f));
        activeTime = object.optInt("activeTime", 0);
        customerID = object.optString("customerId",""); // Empty if there is none


        arrivesAtTime = LocalTime.now().plusNanos((long)
                (remainingTravelTime * 1_000_000_000));
    }

    // Getter for the Variables
    public double getCoordX() {
        return coordX;
    }

    public double getCoordY() {
        return coordY;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public double getRemainingTravelTime() {
        return remainingTravelTime;
    }

    public double arrivesIn()
    {
        Duration duration = Duration.between(LocalTime.now(), arrivesAtTime);

        // Get the difference in seconds
        long seconds = duration.getSeconds();

        return seconds;
    }

    public float getSpeed() {
        return speed;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getId(){
        return id;
    }
}

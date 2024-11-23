package backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Scenario {
    // Variables initialized at start
    private ArrayList<Customer> customers;
    private ArrayList<Vehicle> vehicles;
    private HashSet<String> customerIDset;
    private String status;
    private final String id;
    boolean finished;
    int updateCounter;

    /**
     * Constructor of Scenario
     * @param scenarioID The ID of the Scenario on the Server, that shuould be mirrored
     */
    public Scenario(String scenarioID) {
        id = scenarioID; // Init ID
        customerIDset = new HashSet<>();// Create Hashset
        updateState(); // Init Everything
        // Fill Hashset
        for(Customer customer : customers) {
            customerIDset.add(customer.getId());
        }
        finished = false;
        updateCounter = 0;
    }

    /**
     * delete this scenario via. HTTP Delete
     */
    public void deleteScenarioById() {
        // Die URL für die DELETE-Anfrage
        String url = "http://localhost:8080/scenarios/" + id;

        // HttpClient erstellen
        HttpClient client = HttpClient.newHttpClient();

        // HttpRequest für DELETE erstellen
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE() // DELETE-Methode
                .build();

        // HTTP-Anfrage senden
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Statuscode und Antwortinhalt ausgeben
            if (response.statusCode() == 200) {
                System.out.println("Scenario with ID " + id + " deleted successfully.");
            } else {
                System.out.println("Failed to delete scenario with ID " + id + ". HTTP Status: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper Function for updateScenario()
     * warps all VehicleIDs and CustomerIDs in a JSON for the Server where the Car doesn't have a customerID linked yet
     * removes this pairs from the HashSet
     * @param pairs that should be turned into a valid JSON
     * @return The JSON String for the HTTP Put Request
     */
    private String toJSON(ArrayList<Pair> pairs){
        JSONArray vehicleJSONarray = new JSONArray();
        for(Pair pair : pairs){
            // Prepare Internals for current Vehicle
            JSONObject internal = new JSONObject();

            // Send only Vehicles that currently don't have any passengers
            if(pair.getVehicle().getCustomerID().isEmpty()) {
                internal.put("id", pair.getVehicle().getId());
                internal.put("customerId", pair.getCustomer().getId());

                // Put this in the Vehicle List
                vehicleJSONarray.put(internal);

                customerIDset.remove(pair.getCustomer().getId());
                // Debug
                System.out.println("Vehicle ["+pair.getVehicle().getId()+"] connected to Customer" + pair.getCustomer().getId());
            }
        }
        // Wrap that in a new JSON Object, as String
        if(vehicleJSONarray.isEmpty()) {
            return "";
        }
        return new JSONObject().put("vehicles", vehicleJSONarray).toString();
    }

    /**
     * Updates all Changes of the Given Pairs to the Server via HTTP
     * uses toJSON() to prepare all the Data for it.
     * used in the Constructor and at every tick
     *
     * @param pairs list of Pairs that should be forwarded to the Server
     */
    public void updateScenario(ArrayList<Pair> pairs) {

        if(customerIDset.isEmpty() && updateCounter != 0) {
            // To Check in the logic-loop for finish call
            finished = true;
            return;
        }

        // Send the List to the API
        HttpClient client = HttpClient.newHttpClient();

        String testNull = toJSON(pairs);
        if(testNull.isEmpty()) {
            // We Ski the Update to the API
            return;
        }

        // Build PUT Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8090/Scenarios/update_scenario/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(testNull))
                .build();

        // Send
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Handle Response

            JSONObject jsonObject = new JSONObject(response.body());
            if(jsonObject.has("failedToUpdate")) {
                System.out.println("Failed to update Reason: " + jsonObject.getJSONArray("failedToUpdate").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateCounter++;
    }

    /**
     * Calls the Scenario API to fetch the current Data according to this ID
     * Calls updateInternals to Save the changes to this Data
     */
    public void updateState() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8090/Scenarios/get_scenario/" + id)) // Localhost endpoint
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if the response was successful
            if (response.statusCode() == 200) {

                // Save the changes to internal
                updateInternals(response.body());

            } else {
                System.out.println("Request failed with status code: " + response.statusCode());
            }
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI syntax: " + e.getMessage());
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Updates the current Scenario Internals via the given JSON String
     * Helper-Function for updateState()
     * @param json from the API
     */
    private void updateInternals(String json){
        // make a viable Object from
        JSONObject object = new JSONObject(json);

        status = object.getString("status");

        // Create Customer List
        customers = new ArrayList<>();
        JSONArray customerJSON = object.getJSONArray("customers");
        for (int i = 0; i < object.getJSONArray("customers").length(); i++) {
            customers.add(new Customer(customerJSON.getJSONObject(i).toString()));
        }

        // Create Vehicle List
        vehicles = new ArrayList<>();
        JSONArray vehicleJSON = object.getJSONArray("vehicles");
        for (int b = 0; b < object.getJSONArray("vehicles").length(); b++) {
            vehicles.add(new Vehicle(vehicleJSON.getJSONObject(b).toString()));
        }
    }

    /**
     * Checks all vehicle.getRemainingTravelTime() for the shortest
     * @return and returns this shortest time
     */
    public double timeToArrival(){
        // Create an Array of the times
        double[] helper = new double[vehicles.size()];

        int i = 0;
        for(Vehicle vehicle : vehicles){
            helper[i] = vehicle.getRemainingTravelTime();
            i++;
        }

        // Create a stream, find min, return it
        return Arrays.stream(helper).min().getAsDouble();
    }


    // Getter for the Members
    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public HashSet<String> getCustomerIDset() {
        return customerIDset;
    }

    public String getId() {return id;}
}

package backend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Timer;

public class Scenario {
    // Variables initialized at start
    private ArrayList<Customer> customers;
    private ArrayList<Vehicle> vehicles;
    private String status;
    private String id;

    // Variables initialized on the Go
    //LocalTime endTime;
    //LocalTime startTime;

    /**
     * Constructor of Scenario
     * @param jsonString a Scenario Json String to extract all Information
     */
    public Scenario(String jsonString) {
        // make a viable Object from
        JSONObject object = new JSONObject(jsonString);

        id = object.getString("id");
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
     * creates a Scenario from a given ID
     *
     * @param scenarioID ID-String to the Scenario that should be simulated, puts it also on the
     * @return the newly created Scenario Object
     */
    public static Scenario chooseScenario(String scenarioID) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/scenarios/" + scenarioID)) // Localhost endpoint
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if the response was successful
            if (response.statusCode() == 200) {
                // Parse JSON response
                String responseBody = response.body();
                return new Scenario(responseBody);
            } else {
                System.out.println("Request failed with status code: " + response.statusCode());
            }
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI syntax: " + e.getMessage());
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
        return null;
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
     * Initialize this scenario via. HTTP Post
     */
    public void initializeScenario() {
        String url = "http://localhost:8090/Scenarios/initialize_scenario?db_scenario_id=" + id;

        HttpClient client = HttpClient.newHttpClient();
        String emptyJson = "{}"; // Leeres JSON-Objekt

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")  // Setze den Content-Type auf application/json
                .POST(HttpRequest.BodyPublishers.ofString(emptyJson))  // Setze den Body auf das leere JSON
                .build();

        // HTTP send
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Scenario with db_scenario_id " + id + " initialized successfully.");
            } else {
                System.out.println("Failed to initialize scenario with db_scenario_id " + id + ". HTTP Status: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper Function for updateScenario()
     * warps all Vehicles in a JSON
     * @return
     */
    private String toJSON(){
        JSONArray vehicleJSONarray = new JSONArray();
        for(Vehicle vehicle : vehicles){
            // Prepare Internals for current Vehicle
            JSONArray internal = new JSONArray();

            internal.put((vehicle.getCustomer() != null) ? vehicle.getCustomer().id : 0); // There is not always one!
            internal.put(vehicle.getId());
            // Put this in the Vehicle List
            vehicleJSONarray.put(internal);
        }
        // Wrap that in a new JSON Object, as Strign
        return new JSONObject().put("vehicles", vehicleJSONarray.getJSONObject(0)).toString();
    }

    /**
     * Updates all Changes in customers and vehicles via HTTP to the Server
     * uses toJSON() to prepare all the Data for it.
     */
    public void updateScenario() {
        HttpClient client = HttpClient.newHttpClient();

        // Build PUT Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8090/Scenarios/update_scenario/"))
                .PUT(HttpRequest.BodyPublishers.ofString(toJSON()))
                .build();

        // Send
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Handle Response
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

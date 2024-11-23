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
    ArrayList<Customer> customers;
    ArrayList<Vehicle> vehicles;
    String status;
    String id;

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
     * Intializes this szenario via HTTP
     */
    public void initScenario() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8090/Scenarios/initialize_scenario?db_scenario_id=" + id))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if the response was successful
            if (response.statusCode() == 200) {
                // Parse JSON response
                // TODO HERE
                System.out.println("Scenario initialized!" + response.body());
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
}

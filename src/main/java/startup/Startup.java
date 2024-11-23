package startup;

import backend.Scenario;
import frontend.HelloApplication;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Class that manages the Startup Process of everything so that it doesn't have to be managed manually
 * Gets invoked from the HelloApplication.java
 */
public class Startup {
    // MEBERS

    final private Scenario scenario;
    final public static int numberOfVehicles = 5;
    final public static int numberOfCustomers = 10;
    final public static float scenario_speed = 0.01f;

    /**
     * Constructor. Creates a new Scenario
     */
    public Startup() {
        // Call API to create
        String id = httpHelperCreate();
        // Call API to initialize Scenario
        initializeScenario(id);
        // Build the Scenario as an Object
        scenario = new Scenario(id);
        System.out.println("\nScenario ID: " + scenario.getId() + "\n");
        // Call API to Launch Scenario
        httpHelperLaunch(id);
    }

    /**
     * Helper for the Constructor
     * Calls the API to create a new Scenario
     * @return the ID of the crated Scenario
     */
    private String httpHelperCreate(){
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/scenario/create?numberOfVehicles=" + numberOfVehicles + "&numberOfCustomers=" + numberOfCustomers)) // Localhost endpoint
                    .header("Content-Type", "application/json")  // Setze den Content-Type auf application/json
                    .POST(HttpRequest.BodyPublishers.ofString("{}"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if the response was successful
            if (response.statusCode() == 200) {

                // Extract the ID of the created Scenario
                System.out.println("SUCCESS! Created new Scenario!");
                JSONObject obj = new JSONObject(response.body());
                return obj.optString("id", "");

            } else {
                System.out.println("Request failed with status code: " + response.statusCode());
            }
        } catch (URISyntaxException e) {
            System.err.println("Invalid URI syntax: " + e.getMessage());
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
        // Something went wrong
        return "";
    }

    /**
     * Helper for the Constructor
     * Makes the API POST Call to launch this scenario
     */
    private void httpHelperLaunch(String id){
        String url = "http://localhost:8090/Runner/launch_scenario/" + id +"?speed=" + scenario_speed;

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")  // Setze den Content-Type auf application/json
                .POST(HttpRequest.BodyPublishers.ofString("{}"))  // Setze den Body auf das leere JSON
                .build();

        // HTTP send
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Scenario launched successfully.");
            } else {
                System.out.println("Failed to initialize scenario. HTTP Status: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper for the Constructor
     * Initialize this scenario via. HTTP Post
     */
    private void initializeScenario(String id) {
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
                System.out.println("Scenario initialized successfully.");
            } else {
                System.out.println("Failed to initialize. HTTP-Response: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter
     * @return the scenario
     */
    public Scenario getScenario() { return scenario; }
}

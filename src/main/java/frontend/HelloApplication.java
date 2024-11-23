package frontend;

import backend.Algorithm;
import backend.Scenario;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.concurrent.Task;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {

    private List<UIPoint> customerPoints = new ArrayList<>(); // The list of points to move
    private List<VehicleInfo> vehiclePoints = new ArrayList<>(); // The list of points to move
    private GraphicsContext gc;
    private Canvas canvas;
    Scenario scenario;
    float dimension = 0;

    @Override
    public void start(Stage stage) throws Exception {
        // Load FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        // Get screen bounds
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();
        dimension = (float)screenHeight;

        // Load the scene
        Scene scene = new Scene(fxmlLoader.load(), screenWidth, screenHeight);

        // Create a canvas to draw the grid and points
        canvas = new Canvas(screenWidth / 2, screenHeight);
        gc = canvas.getGraphicsContext2D();

        // Set background color to gray
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, screenWidth / 2, screenHeight);

        // Draw the grid with thin black lines
        gc.setStroke(Color.BLACK);
        double gridSpacing = 50; // Grid spacing in pixels
        for (double x = 0; x < screenWidth / 2; x += gridSpacing) {
            gc.strokeLine(x, 0, x, screenHeight);
        }
        for (double y = 0; y < screenHeight; y += gridSpacing) {
            gc.strokeLine(0, y, screenWidth / 2, y);
        }

        // Add canvas to the scene
        HBox root = (HBox) scene.getRoot(); // Root layout
        root.getChildren().add(canvas);

        // Configure and show the stage
        stage.setTitle("Map and Dashboard");
        stage.setScene(scene);
        stage.setMaximized(true); // Optionally maximize the window
        stage.show();

        // Start the drawing loop with dynamic delay
        startDrawingLoop();

        startLogicLoop();
    }

    private void startDrawingLoop() {
        // Create the Timeline for repeated drawing
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {
                    // Draw the points and perform actions immediately
                    drawAll();
                })
        );

        // Set the timeline to repeat indefinitely
        timeline.setCycleCount(Timeline.INDEFINITE);

        // Start the timeline
        timeline.play();
    }

    private void startLogicLoop() {
        Task<Void> logicTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                scenario = new Scenario("fc036b89-aa41-4850-8083-c93afe79802a");
                while (true) {
                    var algo = new Algorithm(scenario.getCustomers(),
                            scenario.getVehicles(),scenario.getCustomerIDset());

                    scenario.updateScenario(algo.getSolution());

                    // Perform computations
                    scenario.updateState();

                    // Update points
                    customerPoints = util.customersToPoints(scenario.getCustomers(), dimension);
                    vehiclePoints = VehicleInfo.generateInfo(scenario, dimension);

                    // Calculate delay (if needed)
                    double delay = scenario.timeToArrival();

                    LocalTime adjustedTime = LocalTime.now().plusNanos((long)
                            (delay * 1_000_000_000));

                    System.out.println("Adjusted time: " + adjustedTime.getHour()
                            + ":" + adjustedTime.getMinute() + ":" + adjustedTime.getSecond());


                    delay = Math.max(0.5,(delay * 1000*0.01));

                    // Add a small sleep to prevent CPU overload
                    Thread.sleep((int) delay);
                }
            }
        };

        // Run the task on a background thread
        Thread logicThread = new Thread(logicTask);
        logicThread.setDaemon(true); // Ensures the thread stops when the application exits
        logicThread.start();
    }



    private void drawAll() {
        draw(); // Draw taxis and customers (and possibly other elements)
    }

    private void drawVehicle(UIPoint point, double width, double height, double orientation) {
        gc.save(); // Save the current state of the GraphicsContext

        // Translate to the vehicle's position (x, y)
        gc.translate(point.getX(), point.getY());

        // Rotate the canvas by the orientation angle
        gc.rotate(orientation);

        // Set the color for the triangle
        gc.setFill(point.getColor());

        // Define the triangle's vertices
        double[] xPoints = {
                0,                     // Tip of the triangle (forward direction)
                -width / 2,            // Bottom left corner
                width / 2              // Bottom right corner
        };
        double[] yPoints = {
                -height / 2,           // Tip of the triangle (forward direction)
                height / 2,            // Bottom left corner
                height / 2             // Bottom right corner
        };

        // Draw the triangle
        gc.fillPolygon(xPoints, yPoints, 3); // 3 points for a triangle

        // Restore the original GraphicsContext state
        gc.restore();
    }




    private void draw() {
        // Clear the canvas before each new drawing
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Redraw the grid
        drawGrid();

        gc.setStroke(Color.BLUE);
        // Draw all the points in the list
        for (UIPoint point : customerPoints) {
            drawPoint(point);
            //drawLine(point.().getX(), point.getUpdatedLocation().getY(),
            //        point.nextPoint().getX(), point.nextPoint().getY());
        }

        gc.setStroke(Color.GREEN);
        for (VehicleInfo point : vehiclePoints) {
            drawVehicle(point.getUpdatedLocation(),10, 20,0);
            drawLine(point.getUpdatedLocation().getX(), point.getUpdatedLocation().getY(),
                    point.nextPoint().getX(), point.nextPoint().getY());
        }
    }

    private void drawLine(double x1, double y1, double x2, double y2) {
         // Set the color of the line (can be changed)
        gc.setLineWidth(2); // Set the width of the line (can be changed)
        gc.strokeLine(x1, y1, x2, y2); // Draw the line from (x1, y1) to (x2, y2)
    }


    private void drawGrid() {
        gc.setStroke(Color.BLACK);
        double gridSpacing = 50; // Grid spacing in pixels
        for (double x = 0; x < canvas.getWidth(); x += gridSpacing) {
            gc.strokeLine(x, 0, x, canvas.getHeight());
        }
        for (double y = 0; y < canvas.getHeight(); y += gridSpacing) {
            gc.strokeLine(0, y, canvas.getWidth(), y);
        }
    }

    private void drawPoint(UIPoint point) {
        gc.setFill(point.getColor()); // Set the point color
        gc.fillOval(point.getX() - 5, point.getY() - 5, 10, 10); // Draw the point as a small circle
    }



    // This method calculates the dynamic delay based on some action or state
    private double calculateDynamicDelay() {
        // For example, the delay could be based on how many points were drawn or some other state
        // You can adjust this logic as needed.
        return Math.random() * 2 + 1; // Random delay between 1 and 3 seconds
    }

    public static void main(String[] args) {
        launch();
    }
}

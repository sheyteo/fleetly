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

import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {

    private List<UIPoint> customerPoints = new ArrayList<>(); // The list of points to move
    private List<UIPoint> vehiclePoints = new ArrayList<>(); // The list of points to move
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

    private void startLogicLoop() throws InterruptedException {
        scenario = new Scenario("fe828c1c-bac8-4441-abed-d3a7e3d34e35");
        while (true)
        {
            var algo = new Algorithm(scenario.getCustomers(),scenario.getVehicles(),
                    scenario.getCustomerIDset());

            var list = algo.getSolution();

            scenario.updateScenario(list);

            Thread.sleep(100);

            scenario.updateState();

            customerPoints = util.customersToPoints(scenario.getCustomers(),0);
            vehiclePoints = util.vehiclesToPoints(scenario.getVehicles(),0);

            System.out.println("abdfhajkfsd");

            double delay = scenario.timeToArrival();
            Thread.sleep((int)(delay*1000));
        }

    }


    private void drawAll() {
        draw(); // Draw taxis and customers (and possibly other elements)
    }

    private void draw() {
        // Clear the canvas before each new drawing
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Redraw the grid
        drawGrid();

        // Draw all the points in the list
        for (UIPoint point : customerPoints) {
            drawPoint(point);
        }

        for (UIPoint point : vehiclePoints) {
            drawPoint(point);
        }
    }

    private void drawGrid() {
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

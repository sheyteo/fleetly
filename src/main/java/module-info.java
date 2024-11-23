module com.example.fleetlyui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.net.http;
    requires org.json;

    opens com.example.fleetlyui to javafx.fxml;
    exports com.example.fleetlyui;
}
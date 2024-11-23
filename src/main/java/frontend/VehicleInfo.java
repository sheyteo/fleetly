package frontend;

import backend.Customer;
import backend.Scenario;
import backend.Vehicle;
import javafx.scene.paint.Color;

import java.util.ArrayList;

/**
 * Class that helps to display all Entities on the Map
 */
public class VehicleInfo {

    /**
     * Internal Helper Class
     */
    private static class helperPoint{
        public double x;
        public double y;

        helperPoint(double x, double y){
            this.x = x;
            this.y = y;
        }
    }

    final private Vehicle vehicle;
    final private Customer customer;
    final private double orientation; // no use currently
    final private float dimension;

    /**
     * Constructor for vehicle Info
     * @param vehicle a vehicle
     * @param customer a customer
     * @param orientation orientation as double in degrees
     * @param dimension of the UI
     */
    public VehicleInfo(Vehicle vehicle, Customer customer, double orientation, float dimension){
        this.vehicle = vehicle;
        this.customer = customer;
        this.orientation = orientation;
        this.dimension = dimension;
    }

    /**
     * Returns the last Position of the vehicle
     * @return from its internal data
     */
    public UIPoint previousPoint() {
        return UIPoint.convertToUiPoint( (float)vehicle.getCoordX(), (float)vehicle.getCoordY(), dimension);
    }

    /**
     * Returns the UIPoint where the car was at the last time.
     * @return nothing, when in idle, cust-dest, wenn loaded, cust-location whenn on the way to him
     */
    public UIPoint nextPoint() {
        if(vehicle.getCustomerID().isEmpty()) {
            // We don't have a destination
            return null;
        }
        if(vehicle.getCoordX() == customer.getCoordX() && vehicle.getCoordY() == customer.getCoordY()) {
            // We are driving with the customer to the destination
            return UIPoint.convertToUiPoint((float)customer.getDestY(), (float)customer.getDestY(), dimension);
        }
        // We're driving to the Customer
        return UIPoint.convertToUiPoint((float)customer.getCoordX(), (float)customer.getCoordY(), dimension);
    }

    /**
     * Helper Function for totalWayTime()
     * same logic as nextPoint() only for helperpoints
     * @return new light Helper Point
     */
    private helperPoint nextHelperPoint() {
        if(vehicle.getCustomerID().isEmpty()) {
            // We don't have a destination
            return null;
        }
        if(vehicle.getCoordX() == customer.getCoordX() && vehicle.getCoordY() == customer.getCoordY()) {
            // We are driving with the customer to the destination
            return new helperPoint(customer.getDestX(), customer.getDestY());
        }
        // We're driving to the Customer
        return new helperPoint(customer.getCoordX(), customer.getCoordY());
    }

    /**
     * Helper Function for totalWayTime()
     * same logic as nextPoint() only for previousPoint()
     * @return new light Helper Point of the Vehicle
     */
    private helperPoint previousHelperPoint() {
        return new helperPoint(vehicle.getCoordX(), vehicle.getCoordY());
    }

    /**
     * Computes the duration the car needs on its current trip
     * @return computed time
     */
    public float totalWayTime(){
        helperPoint a = previousHelperPoint();
        helperPoint b = nextHelperPoint();
        double lenght = backend.util.calculateDistance(a.x, a.y, b.x, b.y);
        double speed = vehicle.getSpeed();
        return (float)(lenght/speed);
    }

    /**
     * Method that gets called frequently to give the current location
     * @return compute the current location
     */
    public UIPoint getUpdatedLocation() {
        UIPoint start = previousPoint();
        UIPoint end = nextPoint();
        if(end == null)
            return start;

        double totalTime = totalWayTime();
        double timeLeft = vehicle.arrivesIn();
        UIPoint vector = new UIPoint(end.getX() - start.getX(), end.getY() - start.getY());
        double scaling = 1- (timeLeft / totalTime);
        Color col = frontend.util.uuidToColor(vehicle.getId(), 0);
        return new UIPoint( start.getX() + vector.getX() * scaling, start.getY() +vector.getY() * scaling, col);
    }

    // Getter

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Customer getCustomer() {
        return customer;
    }

    /**
     * Static method that extracts the HelpPoints for every Vehicle in the scenario
     * @param scenario to compute on
     * @param dimension of the UI Canvas
     * @return All the current needed Vehicle Info to Print it on UI in Array
     */
    public static ArrayList<VehicleInfo> generateInfo (Scenario scenario, float dimension) {
        ArrayList<VehicleInfo> info = new ArrayList<>();
        for(Vehicle vehicle : scenario.getVehicles()){
            if(vehicle.getCustomerID().isEmpty()) {
                info.add(new VehicleInfo(vehicle, null, 0, dimension));
            }
            for(Customer customer : scenario.getCustomers()) {
                if(customer.getId().equals(vehicle.getCustomerID())) {
                    info.add(new VehicleInfo(vehicle, customer, 0, dimension));
                    break;
                }
            }

        }
        return info;
    }
}

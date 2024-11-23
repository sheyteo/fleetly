package frontend;

import backend.Customer;
import backend.Scenario;
import backend.Vehicle;
import javafx.scene.paint.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Class that helps to display the Things on the Map
 */
public class VehicleInfo {
    private class helperPoint{
        public double x;
        public double y;

        helperPoint(double x, double y){
            this.x = x;
            this.y = y;
        }
    }

    private Vehicle vehicle;
    private Customer customer;
    private double orientation; // no use currently
    private float dimension;

    public VehicleInfo(Vehicle vehicle, Customer customer, double orientation, float dimension){
        this.vehicle = vehicle;
        this.customer = customer;
        this.orientation = orientation;
        this.dimension = dimension;
    }

    public UIPoint previousPoint() {
        return UIPoint.convertToUiPoint( (float)vehicle.getCoordX(), (float)vehicle.getCoordY(), dimension);
    }

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

    public helperPoint nextHelperPoint() {
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

    public helperPoint previousHelperPoint() {
        return new helperPoint(vehicle.getCoordX(), vehicle.getCoordY());
    }

    public float totalWayTime(){
        helperPoint a = previousHelperPoint();
        helperPoint b = nextHelperPoint();
        double lenght = backend.util.calculateDistance(a.x, a.y, b.x, b.y);
        double speed = vehicle.getSpeed();
        return (float)(lenght/speed);
    }

    /**
     * Method that gets called frequently to give the current location
     * @return
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

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Customer getCustomer() {
        return customer;
    }

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

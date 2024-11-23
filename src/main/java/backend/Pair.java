package backend;

public class Pair {
    private Vehicle vehicle;
    private Customer customer;

    public Pair(Vehicle vehicle, Customer customer) {
        this.vehicle = vehicle;
        this.customer = customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
    public Customer getCustomer() {
        return customer;
    }
}

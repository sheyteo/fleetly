package backend;

import java.util.ArrayList;

public class Algorithm {
private ArrayList<Customer> customers;
private ArrayList<Vehicle> vehicles;
private int[][] optimalAssignment;

    public Algorithm(ArrayList<Customer> customers, ArrayList<Vehicle> vehicles) {
        this.customers = customers;
        this.vehicles = vehicles;
    }
}

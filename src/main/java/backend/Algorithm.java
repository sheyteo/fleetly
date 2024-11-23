package backend;

import java.util.ArrayList;
import java.util.HashSet;

public class Algorithm {
private ArrayList<Customer> customers; //all customers that are still waiting (handled inside, so just provide actual list of customers(with correct flag isAwaitingService()))
private ArrayList<Vehicle> vehicles; //ALL vehicles (also those currently busy)

    public Algorithm(ArrayList<Customer> customersAll, ArrayList<Vehicle> vehicles, HashSet<String> validIds) {
        this.customers = new ArrayList<>();
        for (Customer customer : customersAll) {
            if (validIds.contains(customer.getId())) {
                this.customers.add(customer);
            }
        }

        this.vehicles = vehicles;
    }

    int[][] getOptimalAssignment() {
        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm(this.squareMatrix());
        return hungarianAlgorithm.findOptimalAssignment(); //2 by n matrix
    }

    int costFunction(Vehicle vehicle, Customer customer) {
        int cost = 0;
        if(!vehicle.getCustomerID().isEmpty()){ //not free, still driving previous customer
            //should be seconds
            cost += (int) vehicle.getRemainingTravelTime();
        }
        //everything is in m/s, m, s:
        cost += (int) (util.calculateDistance(vehicle.getCoordX(), vehicle.getCoordY(), customer.getCoordX(), customer.getCoordY())/vehicle.getSpeed());

        if(cost < 0){cost = 0;}
        return cost;
    }

    int[][] squareMatrix(){
        int numberOfVehicles = vehicles.size();
        int numberOfCustomers = customers.size();
        int biggerNumber;
        if(numberOfVehicles > numberOfCustomers){
            biggerNumber = numberOfVehicles;
        } else{
            biggerNumber = numberOfCustomers;
        }
        int[][] squareMatrix = new int[biggerNumber][biggerNumber];
        //make the matrix square through additions of null elements on either side
        if(numberOfVehicles > numberOfCustomers){
            for(int i = 0; i < (numberOfVehicles - numberOfCustomers); i++){
                customers.add(null);
            }
        }
        if(numberOfVehicles < numberOfCustomers){
            for(int i = 0; i < (numberOfCustomers - numberOfVehicles); i++){
                vehicles.add(null);
            }
        }
        //now the matrix should be square
        //update square matrix with cost function values; if customer or vehicle is null, then value should be 0
        for(int i = 0; i < numberOfVehicles; i++){
            for(int j = 0; j < numberOfCustomers; j++){
                if(customers.get(j) == null || vehicles.get(i) == null){
                    squareMatrix[i][j] = 0;
                } else {
                    squareMatrix[i][j] = costFunction(vehicles.get(i), customers.get(j));
                }
            }
        }

        return squareMatrix;
    }

    ArrayList<Pair> parseSolution(int[][] solution){
        ArrayList<Pair> solutionList = new ArrayList<>();
        for(int i = 0; i < solution.length; i++){
            int v = solution[i][0];
            int c = solution[i][1];
            solutionList.add(new Pair(vehicles.get(v), customers.get(c)));
        }
        return solutionList;
    }

    public ArrayList<Pair> getSolution(){
        int[][] solution = getOptimalAssignment();
        return parseSolution(solution);
    }


}

package backend;

import java.util.ArrayList;

public class Algorithm {
private ArrayList<Customer> customers; //all customers that are still waiting
private ArrayList<Vehicle> vehicles; //ALL vehicles (also those currently busy)

    public Algorithm(ArrayList<Customer> customers, ArrayList<Vehicle> vehicles) {
        this.customers = customers;
        this.vehicles = vehicles;
    }

    int[][] getOptimalAssignment() {
        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm(this.squareMatrix());
        return hungarianAlgorithm.findOptimalAssignment(); //2 by n matrix
    }

    int costFunction(Vehicle vehicle, Customer customer) {
        int cost = 0;
        if(vehicle.getCustomer() != null){ //not free, still driving previous customer
            cost += (int) vehicle.getRemainingTravelTime();
        }
        cost +=

        return cost;
    }

    int[][] squareMatrix(){
        int numberOfVehicles = vehicles.size();
        int numberOfCustomers = customers.size();
        int[][] squareMatrix = new int[numberOfVehicles][numberOfCustomers];
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

    ArrayList<Pair> getSolution(){
        int[][] solution = getOptimalAssignment();
        return parseSolution(solution);
    }


}

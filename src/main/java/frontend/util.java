package frontend;

import backend.Vehicle;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import backend.Vehicle;
import backend.Customer;

public class util {

    public static Color uuidToColor(String uuidString, int mode) {
        // Make sure the UUID string is valid
        if (uuidString == null || uuidString.isEmpty()) {
            throw new IllegalArgumentException("UUID string cannot be null or empty.");
        }

        // Convert part of the UUID string to an integer to control the green component
        int hash = uuidString.hashCode(); // Get a hash of the UUID string
        // Normalize the hash value to a value between 0 and 1
        float normalizedGreen = (Math.abs(hash) % 256) / 255.0f; // Use the modulus of the hash for green

        Color color;

        switch (mode)
        {
            case 0:
                color = Color.color(1.0, normalizedGreen, 0.0);
                break;

            case 1:
                color = Color.color(normalizedGreen, 0.5, 0.5);
                break;

            case 2:
                color = Color.color(0, 0.5, normalizedGreen);
                break;
            default:
                color = Color.BLACK;
        }

        // The red component is always close to 1.0 (full red), and blue stays at 0
        return color;// Red is max, green varies, blue is 0
    }

    public static ArrayList<UIPoint> vehiclesToPoints(ArrayList<Vehicle> vehicles, float dimension)
    {
        ArrayList<UIPoint> points = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            UIPoint p = UIPoint.convertToUiPoint((float)vehicle.getCoordX(), (float)vehicle.getCoordY(),dimension);
            p.setColor(uuidToColor(vehicle.getId(),0));
            points.add(p);
        }

        return points;
    }

    public static ArrayList<UIPoint> customersToPoints(ArrayList<Customer> customers, float dimension)
    {
        ArrayList<UIPoint> points = new ArrayList<>();

        for (Customer customer : customers) {
            UIPoint p = UIPoint.convertToUiPoint((float)customer.getCoordX(),
                    (float)customer.getCoordY(),dimension);
            if(customer.isAwaitingService())
            {
                p.setColor(uuidToColor(customer.getId(),1));
            }
            points.add(p);
        }
        return points;
    }

    public static UIPoint customersToPointStart(Customer customer, float dimension)
    {
        UIPoint p = UIPoint.convertToUiPoint((float)customer.getCoordX(),
                (float)customer.getCoordY(),dimension);
        if(customer.isAwaitingService())
        {
            p.setColor(uuidToColor(customer.getId(),1));
        }
        return p;
    }

    public static UIPoint customersToPointEnd(Customer customer, float dimension)
    {
        UIPoint p = UIPoint.convertToUiPoint((float)customer.getDestX(),
                (float)customer.getDestY(),dimension);
        if(customer.isAwaitingService())
        {
            p.setColor(uuidToColor(customer.getId(),1));
        }
        return p;
    }
}

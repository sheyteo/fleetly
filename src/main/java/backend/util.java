package backend;

public class util {
    /**
     * Computes the direct distance (in m) between four Points on the earth
     * Real World-Coordinates
     * uses the Haversine-Formula
     *
     * @param x1 x-Coordinate of first point
     * @param y1 y-Coordinate of first point
     * @param x2 x-Coordinate of second point
     * @param y2 y-Coordinate of second point
     * @return the distance between both points in meters
     */
    public static double calculateDistance(double x1, double y1, double x2, double y2) {
        // Radius of the earth in km
        final double EARTH_RADIUS = 6371.01;

        // Make coords to real degrees
        double lat1Rad = Math.toRadians(x1);
        double lon1Rad = Math.toRadians(y1);
        double lat2Rad = Math.toRadians(x2);
        double lon2Rad = Math.toRadians(y2);

        // Haversine-Formula
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.pow(Math.sin(deltaLat / 2), 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(deltaLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Final Distance Compute, times 1000 to give it back in meters
        return EARTH_RADIUS * c * 1000;
    }

    /**
     * Converts a speed given in km/h (or kph) to m/s (or mps)
     * @param kph speed in km/h
     * @return speed in m/s
     */
    static double convertMPStoKPH(double kph) {
        return (kph * 1000) / 3600;
    }
}

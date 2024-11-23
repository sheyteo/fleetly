package frontend;

import javafx.scene.paint.Color;

/**
 * class that describes a point on the UI
 */
public class UIPoint {
    private double x; // X Coordinate
    private double y; // Y Coordinate
    private Color color;

    /**
     * Construct a Point
     * @param x real GUI x-Coordinate
     * @param y real GUI y-Coordinate
     */
    public UIPoint(double x, double y) {
        this.x = x;
        this.y = y;
        this.color = Color.color(1,1,1);
    }

    public UIPoint(double x, double y,Color c) {
        this.x = x;
        this.y = y;
        this.color = c;
    }

    /**
     * convert from API-Coordinates to Printable GUI Coordinates
     *
     * @param lat x-Coordinate in API-Format
     * @param lon y-Coordinate in API-Format
     * @param dimension dimension of the quadratic UI
     * @return Point in printable UI-Format
     */
    public static UIPoint convertToUiPoint(double lat, double lon, final float dimension){
        final double long_API_Max = Math.toRadians(11.646708f);
        final double long_API_Min = Math.toRadians(11.503302f);
        final double lat_API_Max = Math.toRadians(48.165312f);
        final double lat_API_Min = Math.toRadians(48.113f);
        lat = Math.toRadians(lat);
        lon = Math.toRadians(lon);
        final double R = 6371.01;
        double x = R * Math.cos(lat) * Math.cos(lon);
        double y = R * Math.cos(lat) * Math.sin(lon);

        double x_API_Max = R * Math.cos(lat_API_Max) * Math.cos(long_API_Max);
        double y_API_Max = R * Math.cos(lat_API_Max) * Math.sin(long_API_Max);

        double x_API_Min = R * Math.cos(lat_API_Min) * Math.cos(long_API_Min);
        double y_API_Min = R * Math.cos(lat_API_Min) * Math.sin(long_API_Min);


        var prcx = x - x_API_Min;
        var prcy = y - y_API_Min;

        var x_rat = x_API_Max - x_API_Min;
        var y_rat = y_API_Max - y_API_Min;

        var unscaledX = prcx/x_rat;
        var unscaledY = prcy/y_rat;


        return new UIPoint(unscaledX*dimension+50,unscaledY*dimension+50);
    }

    public Color getColor() {
        return color;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

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
     * @param x x-Coordinate in API-Format
     * @param y y-Coordinate in API-Format
     * @param dimension dimension of the quadratic UI
     * @return Point in printable UI-Format
     */
    public static UIPoint convertToUiPoint(float x, float y, final float dimension){
        final float y_API_Max = 11.646708f;
        final float y_API_Min = 11.503302f;
        final float x_API_Max = 48.165312f;
        final float x_API_Min = 48.113f;
        final float xDistance = x_API_Max - x_API_Min;
        final float yDistance = y_API_Max - y_API_Min;
        return new UIPoint((1-(x_API_Max-x)/xDistance)*dimension, (1-(y_API_Max-y)/yDistance)*dimension);
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

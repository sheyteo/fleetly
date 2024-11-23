package backend;

/**
 * class that describes a point on the UI
 */
public class UIPoint {
    private double x; // X Coordinate
    private double y; // Y Coordinate

    /**
     * Construct a Point
     * @param x real GUI x-Coordinate
     * @param y real GUI y-Coordinate
     */
    public UIPoint(double x, double y) {
        this.x = x;
        this.y = y;
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
        final float y_API_Max = 11.65f;
        final float x_API_Max = 48.17f;
        float y_ration = y_API_Max / dimension;
        float x_ration = x_API_Max / dimension;
        return new UIPoint(x*x_ration, y*y_ration);
    }
}
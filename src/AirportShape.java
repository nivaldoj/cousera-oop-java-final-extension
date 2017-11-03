import processing.core.PGraphics;
import processing.core.PShape;

// warning: workaround below (i'm not really proud of it)
// this is the way i found to always have airport shape available in marker/applet when i was working on this
// since everytime a draw() is called in a marker a new SVG object is created, consuming more memory
public class AirportShape {

    // shapes for both colors
    private PShape selectedShape;
    private PShape unselectedShape;

    private static AirportShape instance;

    // constructor
    private AirportShape(PGraphics pgraphics) {
        this.selectedShape = pgraphics.loadShape("selected.svg");
        this.unselectedShape = pgraphics.loadShape("unselected.svg");
    }

    public static AirportShape instance(PGraphics pgraphics) {
        if (instance == null)
            instance = new AirportShape(pgraphics);

        return instance;
    }

    public PShape getSelectedShape() {
        return selectedShape;
    }

    public PShape getUnselectedShape() {
        return unselectedShape;
    }

}

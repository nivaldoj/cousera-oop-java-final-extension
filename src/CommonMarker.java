import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

import java.util.HashMap;

/**
 * Implements a common marker for cities and earthquakes on a earthquake map.
 *
 * @author UC San Diego Intermediate Software Development MOOC team
 */
public abstract class CommonMarker extends SimplePointMarker {

    // records whether this marker has been clicked
    private boolean clicked = false;

    public CommonMarker(Location location) {
        super(location);
    }

    public CommonMarker(Location location, HashMap<String, Object> properties) {
        super(location, properties);
    }

    // getter method for clicked field
    public boolean isClicked() {
        return clicked;
    }

    // setter method for clicked field
    public void setClicked(boolean state) {
        clicked = state;
    }

    // common piece of drawing method for markers
    public void draw(PGraphics pgraphics, float x, float y) {
        if (!hidden) {
            drawMarker(pgraphics, x, y);

            if (selected) {
                showTitle(pgraphics, x, y);
            }
        }
    }

    // abstract methods...
    public abstract void drawMarker(PGraphics pGraphics, float x, float y);
    public abstract void showTitle(PGraphics pGraphics, float x, float y);

}

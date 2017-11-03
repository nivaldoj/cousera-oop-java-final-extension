import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;

/**
 * A class to represent airport markers on a world map.
 *
 * @author Adam Setters
 *         and the UC San Diego Intermediate Software Development
 *         MOOC Team
 */
public class AirportMarker extends CommonMarker {

    public AirportMarker(Feature city) {
        super(((PointFeature) city).getLocation(), city.getProperties());
    }

    @Override
    public void drawMarker(PGraphics pgraphics, float x, float y) {
        // this was made to prevent shape allocation every time
        // when drawMarker() for every marker is called
        AirportShape holder = AirportShape.instance(pgraphics);
        PShape shape;

        if (isClicked())
            shape = holder.getSelectedShape();
        else
            shape = holder.getUnselectedShape();

        // draw shape
        pgraphics.shape(shape, x-6, y-7, 15, 15);

    }


    @Override
    public void showTitle(PGraphics pgraphics, float x, float y) {
        // informations will be shown
        String airport = String.format("Name: %s", ((String) getProperty("name")).replaceAll("\"", "") );

        pgraphics.pushStyle();

        // draw rectangle
        pgraphics.rectMode(PConstants.CORNER);
        pgraphics.fill(255, 255, 255);
        pgraphics.rect(x, y + 15, pgraphics.textWidth(airport) + 6, 18, 5);

        // draw text
        pgraphics.textSize(12);
        pgraphics.textAlign(PConstants.LEFT, PConstants.TOP);
        pgraphics.fill(0);
        pgraphics.text(airport, x + 3, y + 18);

        pgraphics.popStyle();
    }

}

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;
import processing.core.PShape;

import java.util.*;

/**
 * An applet that shows airportLocations (and routes) on a world map.
 *
 * @author Adam Setters
 *         and the UC San Diego Intermediate Software Development
 *         MOOC Team
 */
public class AirportMap extends PApplet {

    private UnfoldingMap map;

    private List<Marker> airportMarkers;
    private List<Marker> routeMarkers;
    private HashMap<Integer, Location> airportLocations;

    private CommonMarker lastSelected;
    private CommonMarker lastClicked;

    private boolean isControlsHidden = false;

    // -----------------------------

    @Override
    public void setup() {
        // initialize window size
        size(750, 550, OPENGL);

        // initialize map
        map = new UnfoldingMap(this, 0, 0, 750, 550, new Microsoft.RoadProvider());
        MapUtils.createDefaultEventDispatcher(this, map);

        // get information from airport and routes data
        List<PointFeature> airports = Parser.parseAirports(this, "airports.dat");
        List<ShapeFeature> routes = Parser.parseRoutes(this, "routes.dat");

        // initialize markers
        initializeAirportMarkers(airports);
        initializeRouteMarkers(routes);

        // add all airport markers to map
        map.addMarkers(airportMarkers);

        // hide all routes and add to map
        hideMarkers(routeMarkers);
        map.addMarkers(routeMarkers);

    }

    @Override
    public void draw() {
        // init background color
        background(color(96, 96, 96));

        // draw map
        map.draw();

        // draw key and status
        addKey();
        addStatus();

        // show a tooltip if controls is hidden
        showHiddenTooltip();
    }

    @Override
    public void mouseMoved() {
        // clear last selection if was a selection
        if (lastSelected != null) {
            lastSelected.setSelected(false);
            lastSelected = null;
        }

        selectMarkerIfHover(airportMarkers);
    }

    @Override
    public void mouseClicked() {
        // we already have a clicked marker?
        if (lastClicked != null) {
            // unhide airport markers
            unhideMarkers(airportMarkers);

            // hide all route markers
            hideMarkers(routeMarkers);

            // set unclicked
            lastClicked.setClicked(false);

            // remove reference
            lastClicked = null;

            return;
        }

        // we clicked in something?
        if (findAndSetClickedMarker()) {
            // hide all airport markers
            hideMarkers(airportMarkers);

            // show routes to of clicked marker
            showClickedMarkerRoutes();
        }

    }

    @Override
    public void keyPressed() {
        // toggle controls
        if (key == 'S' || key == 's') {
            // hide or show controls
            isControlsHidden = !isControlsHidden;
        }
    }

    // -----------------------------

    private void initializeRouteMarkers(List<ShapeFeature> routes) {
        routeMarkers = new ArrayList<>();

        for (ShapeFeature route : routes) {
            // get source and destination airport IDs
            int sourceID = Integer.parseInt((String) route.getProperty("source"));
            int destinationID = Integer.parseInt((String) route.getProperty("destination"));

            // HashMap of airportLocations contain both airport IDs?
            if (airportLocations.containsKey(sourceID) && airportLocations.containsKey(destinationID)) {
                route.addLocation(airportLocations.get(sourceID));
                route.addLocation(airportLocations.get(destinationID));
            }

            // add a marker (line marker) used to draw lines between locations
            routeMarkers.add(new SimpleLinesMarker(route.getLocations(), route.getProperties()));
        }

    }

    private void initializeAirportMarkers(List<PointFeature> airports) {
        // HashMap for fast retrieve airportLocations when matching with routes
        airportMarkers = new ArrayList<>();
        this.airportLocations = new HashMap<>();

        // parse airport data
        for (PointFeature airport : airports) {
            // create marker
            AirportMarker airportMarker = new AirportMarker(airport);
            airportMarker.setRadius(5);

            // add his airport ID to marker
            airportMarker.setId(airport.getId());

            // add into list
            airportMarkers.add(airportMarker);

            // save airport ID and location
            airportLocations.put(Integer.parseInt(airport.getId()), airport.getLocation());
        }

    }

    private void showClickedMarkerRoutes() {
        if (lastClicked == null)
            return;

        int clickedAirportId = Integer.parseInt(lastClicked.getId());

        for (Marker route : routeMarkers) {
            int routeSourceId = Integer.parseInt((String) route.getProperty("source"));
            int routeDestinationId = Integer.parseInt((String) route.getProperty("destination"));

            // source of this route is from clicked airport?
            if (routeSourceId == clickedAirportId) {
                // show marker of this destination
                for (Marker airport : airportMarkers) {
                    if (Integer.parseInt(airport.getId()) == routeDestinationId) {
                        airport.setHidden(false);
                        break;
                    }
                }

                // show this route on map
                route.setHidden(false);
            }
        }

    }

    private void hideMarkers(List<Marker> markers) {
        for (Marker marker : markers) {
            if (marker != lastClicked)
                marker.setHidden(true);
        }
    }

    private void unhideMarkers(List<Marker> markers) {
        for (Marker marker : markers) {
            marker.setHidden(false);
        }
    }

    private void selectMarkerIfHover(List<Marker> markers) {
        // marker selected?
        if (lastSelected != null)
            return;

        for (Marker marker : markers) {
            CommonMarker commonMarker = (CommonMarker) marker;

            // marker inside mouse position?
            if (commonMarker.isInside(map, mouseX, mouseY)) {
                lastSelected = commonMarker;
                commonMarker.setSelected(true);
                return;
            }
        }

    }

    private boolean findAndSetClickedMarker() {
        if (lastClicked != null)
            return false;

        boolean findMarker = false;

        for (Marker marker : airportMarkers) {
            // marker clicked?
            if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
                lastClicked = (CommonMarker) marker;
                findMarker = true;
                break;
            }
        }

        // set as clicked to change color
        if (lastClicked != null)
            lastClicked.setClicked(true);

        return findMarker;
    }

    private void showHiddenTooltip() {
        if (isControlsHidden) {
            // show a brief text to user to inform it is all hidden
            fill(0, 0, 0);
            textSize(10);
            text("Press 'S' to unhide controls!", 1, 10);
        }
    }

    private void addKey() {
        if (!isControlsHidden) {
            // rectangle
            fill(255, 250, 240);
            rect(15, 15, 130, 140, 5);

            // airport key
            fill(0);
            textAlign(LEFT, CENTER);
            textSize(14);
            text("Airport Key", 40, 30);

            // not clicked, so we show airport reference
            if (lastClicked == null) {
                // icons
                PShape airport = loadShape("unselected.svg");
                shape(airport, 30, 60, 15, 15);

                // description
                fill(0);
                textAlign(LEFT, CENTER);
                textSize(12);
                text("Airport", 60, 66);

            } else {
                // icons
                PShape destination = loadShape("unselected.svg");
                PShape source = loadShape("selected.svg");
                shape(source, 30, 60, 15, 15);
                shape(destination, 30, 85, 15, 15);
                line(30, 120, 45, 120);

                // description
                fill(0);
                textAlign(LEFT, CENTER);
                textSize(12);
                text("Source", 60, 66);
                text("Destination", 60,  92);
                text("Route", 60, 117);
            }


        }
    }

    private void addStatus() {
        if (!isControlsHidden) {
            String text;

            // can show initial stats text?
            if (lastClicked == null && lastSelected == null) {
                text = "Hover your mouse on a airport marker to show more info, " +
                        "click on a marker to show its routes " +
                        "or press 'S' to hide controls";
            } else {
                CommonMarker active;

                // guaranteed to be one of these, or both
                if (lastSelected != null)
                    active = lastSelected;
                else
                    active = lastClicked;

                // show latitude and longitude of active
                String city = ((String) active.getProperty("city")).replaceAll("\"", "");
                String country = ((String) active.getProperty("country")).replaceAll("\"", "");
                float latitude = active.getLocation().getLat();
                float longitude = active.getLocation().getLon();

                text = String.format("Location: %s, %s | Latitude: %f, Longitude: %f", city, country, latitude, longitude);
            }

            // rectangle
            fill(255, 250, 240);
            rect(15, 500, 715, 30, 5);

            // draw text
            fill(0, 0, 0);
            textSize(12);
            text(text, 20, 513);
        }
    }

    // see: https://stackoverflow.com/a/36694258
    public static void main(String[] args) {
        PApplet.main(AirportMap.class.getName());
    }

}

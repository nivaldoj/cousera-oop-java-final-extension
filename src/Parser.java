import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    // parse DAT file containing airports information
    public static List<PointFeature> parseAirports(PApplet papplet, String filename) {
        List<PointFeature> pointFeatures = new ArrayList<>();

        String[] rows = papplet.loadStrings(filename);

        for (String row : rows) {
            String[] columns = row.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

            float latitude = Float.parseFloat(columns[6]);
            float longitude = Float.parseFloat(columns[7]);

            Location location = new Location(latitude, longitude);
            PointFeature pointFeature = new PointFeature(location);

            pointFeature.setId(columns[0]);

            pointFeature.addProperty("name", columns[1]);
            pointFeature.addProperty("city", columns[2]);
            pointFeature.addProperty("country", columns[3]);

            if (!columns[4].equals(""))
                pointFeature.putProperty("code", columns[5]);
            else if (!columns[5].equals(""))
                pointFeature.putProperty("code", columns[5]);

            pointFeature.putProperty("altitude", columns[8] + 0);

            pointFeatures.add(pointFeature);
        }

        return pointFeatures;
    }

    // parse DAT file containing routes information
    public static List<ShapeFeature> parseRoutes(PApplet papplet, String filename) {
        List<ShapeFeature> routes = new ArrayList<>();

        String[] rows = papplet.loadStrings(filename);

        for (String row : rows) {
            String[] columns = row.split(",");

            ShapeFeature route = new ShapeFeature(Feature.FeatureType.LINES);

            // add to list only if both have OpenFlights identifier
            if (!columns[3].equals("\\N") && !columns[5].equals("\\N")) {
                route.putProperty("source", columns[3]);
                route.putProperty("destination", columns[5]);

                routes.add(route);
            }
        }

        return routes;
    }


}

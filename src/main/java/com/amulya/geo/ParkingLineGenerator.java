package com.amulya.geo;

import com.amulya.geo.model.*;
import com.amulya.geo.model.Properties;
import com.amulya.geo.utility.Constants;
import com.amulya.geo.utility.UtilMethods;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.operation.buffer.OffsetCurve;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;


public class ParkingLineGenerator {

    private static final Logger LOGGER = Logger.getLogger(ParkingLineGenerator.class.getName());
    public static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static void main(String[] args) throws Exception {


//        Step 1 : Load the files into records.

        //  Load Areas and Zones File
        CurbAreaRecord curbAreaRecord = loadAreas("src/main/resources/areas.json");
        CurbZonesRecord zonesRecord = loadZones("src/main/resources/zones.json");

        Map<String,CurbZone> curbZoneMap = new HashMap<>();
        zonesRecord.zones().parallelStream().forEach(zone -> {
            curbZoneMap.put(zone.curbZoneId(),zone);
        });


//        Find output for each area and their corresponding zones
        curbAreaRecord.areas().parallelStream().forEach(curbArea -> {

            try {
                createParkingLineGeoJson(curbZoneMap, curbArea);
            } catch (Exception e) {
                LOGGER.warning("Error Occurred while creating Parking Lines For "  + curbArea.curbAreaId() + " " +  e.getMessage());
            }


        });


    }

    private static CurbAreaRecord loadAreas(String pathName) throws IOException {
        return mapper.readValue(new File(pathName), CurbAreaRecord.class);
    }

    private static CurbZonesRecord loadZones(String pathName) throws IOException {
        return mapper.readValue(new File(pathName), CurbZonesRecord.class);
    }

    //Method to create Parking Line GeoJson using offset distance
    static void createParkingLineGeoJson(Map<String,CurbZone> curbZoneMap, CurbArea curbArea) throws IOException {
        List<Feature> featureList = new ArrayList<>();
        //Adding the central curb with transparent opacity for better visualization to match the output file provided.
        featureList.add(new Feature(
                "Feature",
                new Properties("Curb Area", null, null, null, "transparent"),
                curbArea.geometry()
        ));

        //Finding the Parking Lines for each zone.
        for (String curbZoneId : curbArea.curbZoneIds()) {
            CurbZone curbZone = curbZoneMap.get(curbZoneId);

            if(UtilMethods.isNull(curbZone)){
                //If curbZone does not exist in our zones file, continue to next one
                continue;
            }

            LineString curbZoneLine = (LineString) curbZone.geometry();

            if (UtilMethods.isNull(curbZoneLine) || UtilMethods.isNull(curbZone.locationReferences())) {
                //In case there is a data issue, we are continuing with other data points
                continue;
            }

            String side = curbZone.locationReferences().getFirst().side();

            //Finding the offsetDistance based on side and parking line, ignoring num spaces for the moment.
//            double offsetDist = UtilMethods.getOffsetDistance(side, curbZone.parkingAngle, curbZone.numSpaces);
            double offsetDist = UtilMethods.getOffsetDistance(side, curbZone.parkingAngle());

            //Using side and parking angle to create offset line (parking line)
            //OffsetCurve only works for LineStrings, which is fine here since zones are line strings
            Geometry parkingLine = OffsetCurve.getCurveJoined(curbZoneLine, offsetDist);

            //Making sure that the parking line is between curb zone and central curb area, and they are not intersecting.
            Geometry centerIntersection = parkingLine.intersection(curbArea.geometry());

            if (!UtilMethods.isNull(centerIntersection) && centerIntersection.isEmpty()) {

                //Adding the parking line with red colour for better visualization.
                featureList.add(new Feature(
                        "Feature",
                        new Properties("Parking Line", Constants.HEX_RED, 2, 1, null),
                        parkingLine
                ));

                // Commenting out the curb zone line to make sure the gaps in parking line (because of parking angle) are seen and not overridden
//                featureList.add(new Feature(
//                        "Feature",
//                        new Properties("Zone Line",null,null,null,null),
//                        curbZoneLine
//                ));


            }


        }

        //Write the GeoJson created into separate output files based on the area id.
        UtilMethods.writeFile(new GeoJsonRecord("FeatureCollection", featureList), Path.of("src/main/resources/output_" + curbArea.curbAreaId() + ".json"));
    }

//    Tried fetching zones for each area but not suitable until we do this computation in parallel
//    private static List<CurbZone> fetchZonesForArea(Set<String> curbZoneIds, File zonesFile, ObjectMapper mapper) throws IOException {
//        List<CurbZone> result = new ArrayList<>();
//        JsonFactory jf = mapper.getFactory();
//
//        try (JsonParser jp = jf.createParser(zonesFile)) {
//            // Advance to the start of the "zones" array
//            while (jp.nextToken() != JsonToken.FIELD_NAME
//                    || !"zones".equals(jp.currentName())) {
//                //Skipping until I find the zones
//            }
//
//            jp.nextToken(); // now at START_ARRAY
//
//            //Map the CurbZone record to each zone
//            ObjectReader reader = mapper.readerFor(CurbZone.class);
//
//            // Read each zone object; stop when we have all wanted IDs
//            while (jp.nextToken() == JsonToken.START_OBJECT && !curbZoneIds.isEmpty()) {
//                JsonNode node = jp.readValueAsTree();
//                String id = node.get("curb_zone_id").asText();
//                if (curbZoneIds.remove(id)) {
//                    result.add(reader.readValue(node));
//                }
//            }
//        }
//        return result;
//    }


}

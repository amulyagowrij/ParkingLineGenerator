package com.amulya.geo.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

public class UtilMethods {

    public static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    //Utility Methods to write value into a file based on target path.
    public static void writeFile(Object value, Path targetPath) throws IOException {
        File file = targetPath.toFile();
        mapper.writeValue(file, value);
    }

    //Utility Method for finding if any data type is null

    public static boolean isNull(String value) {
        return value == null;
    }

    public static boolean isNull(Boolean value) {
        return value == null;
    }

    public static boolean isNull(Number value) {
        return value == null;
    }

    public static boolean isNull(Collection<?> value) {
        return value == null;
    }

    public static boolean isNull(Map<?, ?> value) {
        return value == null;
    }

    public static boolean isNull(Object value) {
        return value == null;
    }


    //Find offset distance based on parking angle
    //The file has only parallel and angled but this encompasses all the possible cases.
    public static double getOffsetDistance(String side, String parkingAngle ) {
        double meters = switch (parkingAngle == null ? "DEFAULT" : parkingAngle.toUpperCase()) {
            case "PERPENDICULAR" -> 3.5;
            case "ANGLE", "ANGLED", "DIAGONAL" -> 3.0;
            case "PARALLEL" -> 2.5;
            default -> 0.0;
        };

        // Convert meters to degrees (approx: 1° ≈ 111,000 m)
        double degrees = meters / 111000.0;

        return "LEFT".equalsIgnoreCase(side) ? -degrees : degrees;
    }

    //Find offset distance based on parking angle and num spaces
    public static double getOffsetDistance(String side, String parkingAngle, Integer numSpaces ) {
        if(numSpaces<=0){
            return 0;
        }

        double meters = switch (parkingAngle == null ? "DEFAULT" : parkingAngle.toUpperCase()) {
            case "PERPENDICULAR" -> 3.5;
            case "ANGLE", "ANGLED", "DIAGONAL" -> 3.0;
            case "PARALLEL" -> 2.5;
            default -> 0.0;
        };

        // Convert meters to degrees (approx: 1° ≈ 111,000 m)
        double degrees = meters / 111000.0;

        return "LEFT".equalsIgnoreCase(side) ? -degrees : degrees;
    }

}

package com.amulya.geo.utility;

/*

        The purpose of this class is to
        Avoid Serialization because of geometry.
        Making Sure Jackson dependency is able to serialize Geometry without facing issue

*/

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

import java.io.IOException;

public class GeometrySerializer extends JsonSerializer<Geometry> {

    @Override
    public void serialize(Geometry geometry, JsonGenerator gen, SerializerProvider provider) throws IOException {
        GeoJsonWriter geoJsonWriter = new GeoJsonWriter();
        geoJsonWriter.setEncodeCRS(false);

        String geoJson = geoJsonWriter.write(geometry);
        gen.writeRawValue(geoJson);
    }
}

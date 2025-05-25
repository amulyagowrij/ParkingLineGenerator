# ParkingLineGenerator

ParkingLineGenerator is a robust Java tool that automates the generation of curbside parking lines using geospatial data. Given area and zone JSON/GeoJSON files, it computes offset lines (left/right parking lines) for each road, producing GeoJSON files ready for mapping, visualization, and analysis.
---

## Approach

### *1. Data Loading*

* Reads input files (areas.json, zones.json) using Jackson for JSON parsing.
* Deserializes areas and zones into Java record types for type safety and code clarity.

### *2. Data Mapping*

* Zones are indexed into a Map<String, CurbZone> for fast lookup by zone ID.
* Each CurbArea lists the zone IDs it connects to; relevant zones are mapped for each area.

### *3. Parallel Processing*

* Curb Area processing utilizes Java parallel streams, crucial for large datasets.

### *4. Geometry Processing*

* For each curb zone in an area:

  * Computes the offset line (parking line) using JTS’s robust geometry tools (OffsetCurve).
  * Determines the correct offset distance based on the zone's attributes (side, parking angle).
  * Ensures that the generated parking line is valid (parking line not intersecting with the central curb area).

### *5. Output Generation*

* Collects all generated features into a GeoJSON FeatureCollection.
* Outputs one file per area: output_<area_id>.json for easy visualization (e.g., [geojson.io](https://geojson.io/)).

---

## Pros and Cons

### *Pros*

* *Type-Safe & Maintainable:* Uses Java records and clear data models for readability and maintainability.
* *Visualization Friendly Output:* Standardized GeoJSON files compatible with mapping and GIS tools.
* *Easy Integration:* Modular codebase ready to plug into larger city-planning or mapping systems.
* *Clear Error Handling:* Graceful skipping of incomplete or invalid data.

### *Cons*

* *Memory Usage:* Loads all zones into memory, which may become limiting for extremely large datasets (e.g., millions of zones).
* *Single-Machine Bound:* All computation runs on a single server/instance; no distributed processing out of the box.
* *IO Bound:* Reading/writing large GeoJSON files may become a bottleneck for very big datasets.
* *No API/GUI:* Command-line tool only (but easy to wrap as a web service if needed).

---

## Future Scalability & Improvements

* *Distributed Processing:* Integrate frameworks like Apache Spark or Flink to scale processing horizontally across multiple machines.
* *Streaming Data Handling:* Switch to streaming JSON parsing (Jackson’s Streaming API) to handle files that don’t fit into memory.
* *Batch Output:* Write output in configurable batches or chunked files for extremely large city datasets.
* *Configurable Parameters:* Allow more user configuration (output format, buffer distance, etc.) via command-line arguments or config files.

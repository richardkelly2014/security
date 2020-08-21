package com.redisGeo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeohashHelper {

    private static final double D_R = Math.PI / 180.0;
    private static final double R_MAJOR = 6378137.0;
    private static final double R_MINOR = 6356752.3142;
    private static final double RATIO = R_MINOR / R_MAJOR;
    private static final double ECCENT = Math.sqrt(1.0 - (RATIO * RATIO));
    private static final double COM = 0.5 * ECCENT;

    private static final double DEG_TO_RAD = 0.017453292519943295769236907684886;
    private static final double EARTH_RADIUS_IN_METERS = 6372797.560856;

    private static final double MERCATOR_MAX = 20037726.37;
    private static final double MERCATOR_MIN = -20037726.37;

    private GeoHash geoHash = new GeoHash();

    public long geohashAlign52Bits(GeoHashBits hash) {
        long bits = hash.bits;
        bits <<= (52 - hash.step * 2);
        return bits;
    }

    public GeoHashRadius geohashGetAreasByRadiusWGS84(double longitude, double latitude,
                                                      double radius_meters) {
        return geohashGetAreasByRadius(longitude, latitude, radius_meters);
    }

    GeoHashRadius geohashGetAreasByRadius(double longitude, double latitude, double radius_meters) {
        GeoHashRadius radius = new GeoHashRadius();
        GeoHashBits hash = new GeoHashBits();
        GeoHashNeighbors neighbors = new GeoHashNeighbors();
        GeoHashArea area = new GeoHashArea();

        double min_lon, max_lon, min_lat, max_lat;
        double[] bounds = new double[4];
        int steps;

        //矩形
        geohashBoundingBox(longitude, latitude, radius_meters, bounds);
        min_lon = bounds[0];
        min_lat = bounds[1];
        max_lon = bounds[2];
        max_lat = bounds[3];

        //估算step
        steps = geohashEstimateStepsByRadius(radius_meters, latitude);

        //计算hash
        geoHash.geohashEncodeWGS84(longitude, latitude, steps, hash);

        //hash周围hash
        geoHash.geohashNeighbors(hash, neighbors);

        //hash，解码区域
        geoHash.geohashDecodeWGS84(hash, area);

        int decrease_step = 0;
        {
            GeoHashArea north = new GeoHashArea(), south = new GeoHashArea(), east = new GeoHashArea(), west = new GeoHashArea();

            geoHash.geohashDecodeWGS84(neighbors.north, north);
            geoHash.geohashDecodeWGS84(neighbors.south, south);
            geoHash.geohashDecodeWGS84(neighbors.east, east);
            geoHash.geohashDecodeWGS84(neighbors.west, west);


            if (geohashGetDistance(longitude, latitude, longitude, north.latitude.max)
                    < radius_meters) {
                decrease_step = 1;
            }
            if (geohashGetDistance(longitude, latitude, longitude, south.latitude.min)
                    < radius_meters) {
                decrease_step = 1;
            }
            if (geohashGetDistance(longitude, latitude, east.longitude.max, latitude)
                    < radius_meters) {
                decrease_step = 1;
            }
            if (geohashGetDistance(longitude, latitude, west.longitude.min, latitude)
                    < radius_meters) {
                decrease_step = 1;
            }
        }

        if (steps > 1 && decrease_step > 0) {
            steps--;
            geoHash.geohashEncodeWGS84(longitude, latitude, steps, hash);
            geoHash.geohashNeighbors(hash, neighbors);
            geoHash.geohashDecodeWGS84(hash, area);
        }

        /* Exclude the search areas that are useless. */
        if (steps >= 2) {
            if (area.latitude.min < min_lat) {
                GZERO(neighbors.south);
                GZERO(neighbors.south_west);
                GZERO(neighbors.south_east);
            }
            if (area.latitude.max > max_lat) {
                GZERO(neighbors.north);
                GZERO(neighbors.north_east);
                GZERO(neighbors.north_west);
            }
            if (area.longitude.min < min_lon) {
                GZERO(neighbors.west);
                GZERO(neighbors.south_west);
                GZERO(neighbors.north_west);
            }
            if (area.longitude.max > max_lon) {
                GZERO(neighbors.east);
                GZERO(neighbors.south_east);
                GZERO(neighbors.north_east);
            }
        }
        radius.hash = hash;
        radius.neighbors = neighbors;
        radius.area = area;
        return radius;
    }

    //估算step
    int geohashEstimateStepsByRadius(double range_meters, double lat) {
        if (range_meters == 0) return 26;
        int step = 1;
        while (range_meters < MERCATOR_MAX) {
            range_meters *= 2;
            step++;
        }
        step -= 2; /* Make sure range is included in most of the base cases. */

        /* Wider range torwards the poles... Note: it is possible to do better
         * than this approximation by computing the distance between meridians
         * at this latitude, but this does the trick for now. */
        if (lat > 66 || lat < -66) {
            step--;
            if (lat > 80 || lat < -80) step--;
        }

        /* Frame to valid range. */
        if (step < 1) step = 1;
        if (step > 26) step = 26;
        return step;
    }

    //计算矩形 方块坐标
    int geohashBoundingBox(double longitude, double latitude, double radius_meters,
                           double[] bounds) {

        bounds[0] = longitude - rad_deg(radius_meters / EARTH_RADIUS_IN_METERS / Math.cos(deg_rad(latitude)));
        bounds[2] = longitude + rad_deg(radius_meters / EARTH_RADIUS_IN_METERS / Math.cos(deg_rad(latitude)));
        bounds[1] = latitude - rad_deg(radius_meters / EARTH_RADIUS_IN_METERS);
        bounds[3] = latitude + rad_deg(radius_meters / EARTH_RADIUS_IN_METERS);
        return 1;
    }

    //2点距离
    public double geohashGetDistance(double lon1d, double lat1d, double lon2d, double lat2d) {
        double lat1r, lon1r, lat2r, lon2r, u, v;
        lat1r = deg_rad(lat1d);
        lon1r = deg_rad(lon1d);
        lat2r = deg_rad(lat2d);
        lon2r = deg_rad(lon2d);
        u = Math.sin((lat2r - lat1r) / 2);
        v = Math.sin((lon2r - lon1r) / 2);
        return 2.0 * EARTH_RADIUS_IN_METERS *
                Math.asin(Math.sqrt(u * u + Math.cos(lat1r) * Math.cos(lat2r) * v * v));
    }


    public static void GZERO(GeoHashBits s) {
        s.bits = s.step = 0;
    }

    public static boolean GISZERO(GeoHashBits s) {
        return s.bits <= 0 && s.step < 0;
    }

    public static boolean GISNOTZERO(GeoHashBits s) {
        return s.bits > 0 || s.step > 0;
    }

    /**
     * 角度->弧度
     *
     * @param ang
     * @return
     */
    protected static double deg_rad(double ang) {
        return ang * D_R;
    }

    /**
     * 弧度->角度
     *
     * @param ang
     * @return
     */
    protected static double rad_deg(double ang) {
        return ang / D_R;
    }

}

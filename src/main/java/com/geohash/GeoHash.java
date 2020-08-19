package com.geohash;

public class GeoHash {

    private final static int GEO_STEP_MAX = 26;/* 26*2 = 52 bits. */

    private static final double GEO_LAT_MIN = -85.05112878d;
    private static final double GEO_LAT_MAX = 85.05112878d;

    private static final double GEO_LONG_MIN = -180d;
    private static final double GEO_LONG_MAX = 180d;

    private static final double D_R = Math.PI / 180.0;
    private static final double R_MAJOR = 6378137.0;
    private static final double R_MINOR = 6356752.3142;
    private static final double RATIO = R_MINOR / R_MAJOR;
    private static final double ECCENT = Math.sqrt(1.0 - (RATIO * RATIO));
    private static final double COM = 0.5 * ECCENT;

    private static final double EARTH_RADIUS_IN_METERS = 6372797.560856;

    private static final double MERCATOR_MAX = 20037726.37;
    private static final double MERCATOR_MIN = -20037726.37;

    private static final GeoHashRange longRange = new GeoHashRange(GEO_LONG_MAX, GEO_LONG_MIN);
    private static final GeoHashRange latRange = new GeoHashRange(GEO_LAT_MAX, GEO_LAT_MIN);

    public long geohashEncodeType(double longitude, double latitude, int step, GeoHashBits hash) {
        return geoHashEncode(longRange, latRange, longitude, latitude, step, hash);
    }

    public long geohashEncodeWGS84(double longitude, double latitude, int step, GeoHashBits hash) {
        return geohashEncodeType(longitude, latitude, step, hash);
    }

    public long geohashAlign52Bits(GeoHashBits hash) {
        long bits = hash.bits;
        bits <<= (52 - hash.step * 2);
        return bits;
    }


    public GeoHashRadius geohashGetAreasByRadius(double longitude, double latitude, double radius_meters) {
        GeoHashRange long_range, lat_range;
        GeoHashRadius radius;
        GeoHashBits hash;
        GeoHashNeighbors neighbors;
        GeoHashArea area;
        double min_lon, max_lon, min_lat, max_lat;
        double[] bounds = new double[4];
        int steps;

        geohashBoundingBox(longitude, latitude, radius_meters, bounds);
        min_lon = bounds[0];
        min_lat = bounds[1];
        max_lon = bounds[2];
        max_lat = bounds[3];

        return null;
    }

    int geohashBoundingBox(double longitude, double latitude, double radius_meters,
                           double[] bounds) {


        bounds[0] = longitude - rad_deg(radius_meters / EARTH_RADIUS_IN_METERS / Math.cos(deg_rad(latitude)));
        bounds[2] = longitude + rad_deg(radius_meters / EARTH_RADIUS_IN_METERS / Math.cos(deg_rad(latitude)));
        bounds[1] = latitude - rad_deg(radius_meters / EARTH_RADIUS_IN_METERS);
        bounds[3] = latitude + rad_deg(radius_meters / EARTH_RADIUS_IN_METERS);
        return 1;
    }

    private static long geoHashEncode(GeoHashRange longRange, GeoHashRange latRange, double longitude, double latitude, int step,
                                      GeoHashBits hash) {
        if (step > 32 || step == 0) {
            return 0;
        }

        if (rangepIsZero(latRange) || rangepIsZero(longRange)) {
            return 0;
        }

        if (longitude > 180d || longitude < -180d || latitude > 85.05112878d || latitude < -85.05112878d) {
            return 0;
        }

        if (latitude < latRange.min || latitude > latRange.max || longitude < longRange.min || longitude > longRange.max) {
            return 0;
        }
        hash.bits = 0;
        hash.step = step;

        double lat_offset = (latitude - latRange.min) / (latRange.max - latRange.min);
        double long_offset = (longitude - longRange.min) / (longRange.max - longRange.min);

        lat_offset *= (1 << step);
        long_offset *= (1 << step);

        hash.bits = interLeave64(lat_offset, long_offset);
        return hash.bits;
    }

    private static boolean rangepIsZero(GeoHashRange range) {
        if (range == null) {
            return true;
        }

        if (range.min == 0 && range.max == 0) {
            return true;
        }

        return false;
    }

    private static final long[] B = {
            0x5555555555555555l,
            0x3333333333333333l,
            0x0F0F0F0F0F0F0F0Fl,
            0x00FF00FF00FF00FFl,
            0x0000FFFF0000FFFFl
    };

    private static final int[] S = {1, 2, 4, 8, 16};

    private static long interLeave64(double xlo, double ylo) {
        long x = Double.valueOf(xlo).intValue();
        long y = Double.valueOf(ylo).intValue();

        x = (x | (x << S[4])) & B[4];
        y = (y | (y << S[4])) & B[4];

        x = (x | (x << S[3])) & B[3];
        y = (y | (y << S[3])) & B[3];

        x = (x | (x << S[2])) & B[2];
        y = (y | (y << S[2])) & B[2];

        x = (x | (x << S[1])) & B[1];
        y = (y | (y << S[1])) & B[1];

        x = (x | (x << S[0])) & B[0];
        y = (y | (y << S[0])) & B[0];

        return x | (y << 1);
    }

    private static final long[] deB = {
            0x5555555555555555l,
            0x3333333333333333l,
            0x0F0F0F0F0F0F0F0Fl,
            0x00FF00FF00FF00FFl,
            0x0000FFFF0000FFFFl,
            0x00000000FFFFFFFFl};

    private static final int[] deS = {0, 1, 2, 4, 8, 16};

    private static long deinterleave64(long interleaved) {

        long x = interleaved;
        long y = interleaved >> 1;

        x = (x | (x >> deS[0])) & deB[0];
        y = (y | (y >> deS[0])) & deB[0];

        x = (x | (x >> deS[1])) & deB[1];
        y = (y | (y >> deS[1])) & deB[1];

        x = (x | (x >> deS[2])) & deB[2];
        y = (y | (y >> deS[2])) & deB[2];

        x = (x | (x >> deS[3])) & deB[3];
        y = (y | (y >> deS[3])) & deB[3];

        x = (x | (x >> deS[4])) & deB[4];
        y = (y | (y >> deS[4])) & deB[4];

        x = (x | (x >> deS[5])) & deB[5];
        y = (y | (y >> deS[5])) & deB[5];

        return x | (y << 32);
    }

    /**
     * 角度->弧度
     *
     * @param ang
     * @return
     */
    double deg_rad(double ang) {
        return ang * D_R;
    }

    /**
     * 弧度->角度
     *
     * @param ang
     * @return
     */
    double rad_deg(double ang) {
        return ang / D_R;
    }
}

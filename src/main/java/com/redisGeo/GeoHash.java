package com.redisGeo;

public class GeoHash {

    private static final double GEO_LAT_MIN = -85.05112878d;
    private static final double GEO_LAT_MAX = 85.05112878d;
    private static final double GEO_LONG_MIN = -180d;
    private static final double GEO_LONG_MAX = 180d;

    public final static int GEO_STEP_MAX = 26;
    public static final GeoHashRange longRange = new GeoHashRange(GEO_LONG_MAX, GEO_LONG_MIN);
    public static final GeoHashRange latRange = new GeoHashRange(GEO_LAT_MAX, GEO_LAT_MIN);


    public static boolean HASHISZERO(GeoHashBits s) {
        return s.bits <= 0 && s.step <= 0;
    }

    public static boolean RANGEISZERO(GeoHashRange r) {
        return r.max <= 0 && r.min <= 0;
    }

    public static boolean RANGEPISZERO(GeoHashRange r) {
        return r == null || RANGEISZERO(r);
    }

    //坐标转 hash
    public int geohashEncodeWGS84(double longitude, double latitude, GeoHashBits hash) {

        return geohashEncode(longRange, latRange, longitude, latitude, GEO_STEP_MAX, hash);
    }

    public int geohashDecodeWGS84(GeoHashBits hash, GeoHashArea area) {
        return geohashDecode(longRange, latRange, hash, area);
    }

    public int geohashDecodeToLongLatWGS84(GeoHashBits hash, double[] xy) {
        GeoHashArea area = new GeoHashArea();
        if (geohashDecode(longRange, latRange, hash, area) <= 0) {
            return 0;
        }
        return geohashDecodeAreaToLongLat(area, xy);
    }

    //周边
    public void geohashNeighbors(GeoHashBits hash, GeoHashNeighbors neighbors) {
        neighbors.east = hash.cl();
        neighbors.west = hash.cl();
        neighbors.north = hash.cl();
        neighbors.south = hash.cl();
        neighbors.south_east = hash.cl();
        neighbors.south_west = hash.cl();
        neighbors.north_east = hash.cl();
        neighbors.north_west = hash.cl();

        geohash_move_x(neighbors.east, 1);
        geohash_move_y(neighbors.east, 0);

        geohash_move_x(neighbors.west, -1);
        geohash_move_y(neighbors.west, 0);

        geohash_move_x(neighbors.south, 0);
        geohash_move_y(neighbors.south, -1);

        geohash_move_x(neighbors.north, 0);
        geohash_move_y(neighbors.north, 1);

        geohash_move_x(neighbors.north_west, -1);
        geohash_move_y(neighbors.north_west, 1);

        geohash_move_x(neighbors.north_east, 1);
        geohash_move_y(neighbors.north_east, 1);

        geohash_move_x(neighbors.south_east, 1);
        geohash_move_y(neighbors.south_east, -1);

        geohash_move_x(neighbors.south_west, -1);
        geohash_move_y(neighbors.south_west, -1);
    }

    int geohashEncode(GeoHashRange longRange, GeoHashRange latRange,
                      double longitude, double latitude, int step, GeoHashBits hash) {
        if (hash == null) {
            hash = new GeoHashBits();
        }

        if (step > 32 || step == 0) {
            return 0;
        }
        if (longitude > GEO_LONG_MAX || longitude < GEO_LONG_MIN ||
                latitude > GEO_LAT_MAX || latitude < GEO_LAT_MIN) {
            return 0;
        }

        hash.bits = 0;
        hash.step = step;

        if (latitude < latRange.min || latitude > latRange.max ||
                longitude < longRange.min || longitude > longRange.max) {
            return 0;
        }

        double lat_offset =
                (latitude - latRange.min) / (latRange.max - latRange.min);
        double long_offset =
                (longitude - longRange.min) / (longRange.max - longRange.min);

        /* convert to fixed point based on the step size */
        lat_offset *= (1 << step);
        long_offset *= (1 << step);
        hash.bits = interleave64(lat_offset, long_offset);
        return 1;

    }

    // hash 转 area
    int geohashDecode(GeoHashRange longRange, GeoHashRange latRange,
                      GeoHashBits hash, GeoHashArea area) {
        if (area == null) {
            area = new GeoHashArea();
        }
        area.hash = hash;
        int step = hash.step;
        long hash_sep = deinterleave64(hash.bits); /* hash = [LAT][LONG] */

        double lat_scale = latRange.max - latRange.min;
        double long_scale = longRange.max - longRange.min;

        int ilato = (int) (hash_sep);       /* get lat part of deinterleaved hash */
        int ilono = (int) (hash_sep >> 32); /* shift over to get long part of hash */

    /* divide by 2**step.
     * Then, for 0-1 coordinate, multiply times scale and add
       to the min to get the absolute coordinate. */
        area.latitude.min =
                latRange.min + (ilato * 1.0 / (1 << step)) * lat_scale;

        area.latitude.max =
                latRange.min + ((ilato + 1) * 1.0 / (1 << step)) * lat_scale;


        area.longitude.min =
                longRange.min + (ilono * 1.0 / (1 << step)) * long_scale;
        area.longitude.max =
                longRange.min + ((ilono + 1) * 1.0 / (1 << step)) * long_scale;

        return 1;
    }

    int geohashDecodeAreaToLongLat(GeoHashArea area, double[] xy) {

        xy[0] = (area.longitude.min + area.longitude.max) / 2;
        if (xy[0] > GEO_LONG_MAX) {
            xy[0] = GEO_LONG_MAX;
        }
        if (xy[0] < GEO_LONG_MIN) {
            xy[0] = GEO_LONG_MIN;
        }
        xy[1] = (area.latitude.min + area.latitude.max) / 2;
        if (xy[1] > GEO_LAT_MAX) {
            xy[1] = GEO_LAT_MAX;
        }
        if (xy[1] < GEO_LAT_MIN) {
            xy[1] = GEO_LAT_MIN;
        }
        return 1;
    }

    protected static long interleave64(double xlo, double ylo) {
        long B[] = {
                0x5555555555555555l,
                0x3333333333333333l,
                0x0F0F0F0F0F0F0F0Fl,
                0x00FF00FF00FF00FFl,
                0x0000FFFF0000FFFFl
        };
        int S[] = {1, 2, 4, 8, 16};

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

    protected static long deinterleave64(long interleaved) {
        //6
        long B[] = {
                0x5555555555555555l,
                0x3333333333333333l,
                0x0F0F0F0F0F0F0F0Fl,
                0x00FF00FF00FF00FFl,
                0x0000FFFF0000FFFFl,
                0x00000000FFFFFFFFl};
        //6
        int S[] = {0, 1, 2, 4, 8, 16};

        long x = interleaved;
        long y = interleaved >> 1;

        x = (x | (x >> S[0])) & B[0];
        y = (y | (y >> S[0])) & B[0];

        x = (x | (x >> S[1])) & B[1];
        y = (y | (y >> S[1])) & B[1];

        x = (x | (x >> S[2])) & B[2];
        y = (y | (y >> S[2])) & B[2];

        x = (x | (x >> S[3])) & B[3];
        y = (y | (y >> S[3])) & B[3];

        x = (x | (x >> S[4])) & B[4];
        y = (y | (y >> S[4])) & B[4];

        x = (x | (x >> S[5])) & B[5];
        y = (y | (y >> S[5])) & B[5];

        return x | (y << 32);
    }

    static void geohash_move_x(GeoHashBits hash, int d) {
        if (d == 0)
            return;

        long x = hash.bits & 0xaaaaaaaaaaaaaaaal;
        long y = hash.bits & 0x5555555555555555l;

        long zz = 0x5555555555555555l >> (64 - hash.step * 2);

        if (d > 0) {
            x = x + (zz + 1);
        } else {
            x = x | zz;
            x = x - (zz + 1);
        }

        x &= (0xaaaaaaaaaaaaaaaal >> (64 - hash.step * 2));
        hash.bits = (x | y);
    }

    static void geohash_move_y(GeoHashBits hash, int d) {
        if (d == 0)
            return;

        long x = hash.bits & 0xaaaaaaaaaaaaaaaal;
        long y = hash.bits & 0x5555555555555555l;

        long zz = 0xaaaaaaaaaaaaaaaal >> (64 - hash.step * 2);
        if (d > 0) {
            y = y + (zz + 1);
        } else {
            y = y | zz;
            y = y - (zz + 1);
        }
        y &= (0x5555555555555555l >> (64 - hash.step * 2));
        hash.bits = (x | y);
    }


}

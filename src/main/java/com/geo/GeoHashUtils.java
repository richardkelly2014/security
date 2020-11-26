package com.geo;

import com.github.davidmoten.geo.GeoHash;
import com.github.davidmoten.geo.LatLong;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;


import java.util.List;
import java.util.Map;

/**
 * Created by jiangfei on 2018/7/5.
 */
@UtilityClass
public class GeoHashUtils {

    private final static Map<Integer, LatLngBit> bits = Maps.newConcurrentMap();

    static {
        bits.put(1, LatLngBit.builder().latBit(2).lngBit(3).build());
        bits.put(2, LatLngBit.builder().latBit(5).lngBit(5).build());
        bits.put(3, LatLngBit.builder().latBit(7).lngBit(8).build());
        bits.put(4, LatLngBit.builder().latBit(10).lngBit(10).build());
        bits.put(5, LatLngBit.builder().latBit(12).lngBit(13).build());
        bits.put(6, LatLngBit.builder().latBit(15).lngBit(15).build());
        bits.put(7, LatLngBit.builder().latBit(17).lngBit(18).build());
        bits.put(8, LatLngBit.builder().latBit(20).lngBit(20).build());
        bits.put(9, LatLngBit.builder().latBit(22).lngBit(23).build());
        bits.put(10, LatLngBit.builder().latBit(25).lngBit(25).build());
        bits.put(11, LatLngBit.builder().latBit(27).lngBit(28).build());
        bits.put(12, LatLngBit.builder().latBit(30).lngBit(30).build());
    }

    /**
     * a）在纬度相等的情况下：
     * 经度每隔0.00001度，距离相差约1米；
     * 每隔0.0001度，距离相差约10米；
     * 每隔0.001度，距离相差约100米；
     * 每隔0.01度，距离相差约1000米；
     * 每隔0.1度，距离相差约10000米。
     * <p>
     * b）在经度相等的情况下：
     * 纬度每隔0.00001度，距离相差约1.1米；
     * 每隔0.0001度，距离相差约11米；
     * 每隔0.001度，距离相差约111米；
     * 每隔0.01度，距离相差约1113米；
     * 每隔0.1度，距离相差约11132米。
     * <p>
     * geohash的
     * 位数是9位数的时候，大概为附近2米
     * 位数是8时，方块宽：20，高：17 大概为附近20米
     * 位数是7时，方块宽：77，高：203 大概为附近150米
     * 位数是6时，方块宽：620，高：548 大概为附近600米
     * 位数是5时，方块宽：3876，高：5000 大概为附近4900米
     *
     * @param latitude
     * @param longitude
     * @param length
     * @return
     */
    public static String getGeoHash(final double latitude,
                                    final double longitude,
                                    final int length) {
        return GeoHash.encodeHash(latitude, longitude, length);
    }

    public static String geoHash(final double longitude,
                                 final double latitude,
                                 final int length) {
        return GeoHash.encodeHash(latitude, longitude, length);
    }

    public static List<String> getNeighboursGeoHashList(final String geoHash) {

        return GeoHash.neighbours(geoHash);
    }


    public static Map<String, Double> bbox(String geoHash) {
        int len = geoHash.length();
        LatLong latLong = GeoHash.decodeHash(geoHash);

        LatLngBit bit = bits.get(len);
        int lat_bits = bit.getLatBit();
        int lon_bits = bit.getLngBit();

        double latitude_delta = 180.0 / (1 << lat_bits);
        double longitude_delta = 360.0 / (1 << lon_bits);

        Map<String, Double> locations = Maps.newConcurrentMap();
        locations.put("s", latLong.getLat());
        locations.put("w", latLong.getLon());

        locations.put("n", latLong.getLat() + latitude_delta);
        locations.put("e", latLong.getLon() + longitude_delta);

        return locations;
    }

    @Data
    @Builder
    private final static class LatLngBit {
        private int latBit;
        private int lngBit;
    }
}

package com.geo;

import lombok.experimental.UtilityClass;

/**
 * Created by jiangfei on 2018/7/5.
 */
@UtilityClass
public class LatitudeAndLongitudeUtils {

    /**
     * 地球半径
     */
    public static final int EARTH_RADIUS = 6378137;

    /**
     * 计算经纬度距离 精确到米
     *
     * @param point1
     * @param point2
     * @return
     */
    public long calculateDistanceAccurateToMeter(final LatitudeLongitudePair point1,
                                                 final LatitudeLongitudePair point2) {
        return calculateDistanceAccurateToMeter(point1.getLongitude(),
                point1.getLatitude(), point2.getLongitude(), point2.getLatitude());
    }

    /**
     * 计算经纬度距离 精确到米
     *
     * @return
     */
    public long calculateDistanceAccurateToMeter(final double longitude1,
                                                 final double latitude1,
                                                 final double longitude2,
                                                 final double latitude2) {
        return (long) calculateDistance(longitude1, latitude1, longitude2, latitude2);
    }


    /**
     * 计算经纬度距离
     *
     * @return
     */
    public double calculateDistance(final double longitude1,
                                    final double latitude1,
                                    final double longitude2,
                                    final double latitude2) {
        final double lat1 = latitude1 * Math.PI / 180.0;
        final double lat2 = latitude2 * Math.PI / 180.0;
        final double a = lat1 - lat2;
        final double b = (longitude1 - longitude2) * Math.PI / 180.0;
        final double sa2 = Math.sin(a / 2.0);
        final double sb2 = Math.sin(b / 2.0);
        return (EARTH_RADIUS << 1)
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
    }

}

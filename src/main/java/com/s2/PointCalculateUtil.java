package com.s2;


public class PointCalculateUtil {

    // 地球半径(米)
    private static final int EARTH_RADIUS = 6378137;

    /**
     * @param lat1  维度1
     * @param long1 经度1
     * @param lat2  纬度2
     * @param long2 经度2
     * @return
     */
    public static double getDistance(double long1, double lat1, double long2, double lat2) {
        double a, b;
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * EARTH_RADIUS * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
        return d;
    }

    /**
     * 计算三个点形成的夹角度数
     * (地球作为平面)
     *
     * @param long1
     * @param lat1
     * @param long2
     * @param lat2
     * @param long3
     * @param lat3
     * @return
     */
    public static double getAngleOnPlane(double long1, double lat1, double long2, double lat2, double long3, double lat3) {
        double distance12Pow2 = Math.pow(long2 - long1, 2) + Math.pow(lat2 - lat1, 2);
        double distance13Pow2 = Math.pow(long3 - long1, 2) + Math.pow(lat3 - lat1, 2);
        double distance23Pow2 = Math.pow(long3 - long2, 2) + Math.pow(lat3 - lat2, 2);
        if (distance23Pow2 == 0) {
            return 0;
        } else {
            double consineAngle = (distance12Pow2 + distance23Pow2 - distance13Pow2) / (2 * Math.sqrt(distance12Pow2) * Math.sqrt(distance23Pow2));
            double angle = Math.acos(consineAngle);
            angle = Math.toDegrees(angle);
            return angle;
        }
    }


    /**
     * 把两点看成在一个球面内
     * 与赤道平面的夹角
     *
     * @param lng1 经度1
     * @param lat1 纬度1
     * @param lng2 经度2
     * @param lat2 纬度2
     * @return
     */
    public static double getAngleOnSphere(double lng1, double lat1, double lng2, double lat2) {
        double x = Math.sin(lng2 - lng1) * Math.cos(lat2);
        double y = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lng2 - lng1);
        double brng = Math.atan2(y, x);
        brng = Math.toDegrees(brng);
        if (brng < 0) {
            brng = brng + 360;
        }
        return brng;

    }


    /**
     * 把两点看成在一个平面内
     * (地球作为平面)
     * 与赤道的夹角
     *
     * @param lng1 经度1
     * @param lat1 纬度1
     * @param lng2 经度2
     * @param lat2 纬度2
     * @return
     */
    public static double getAngleOnPlane(double lng1, double lat1, double lng2, double lat2) {
        double y = lat2 - lat1;
        double x = lng2 - lng1;
        double brng = Math.atan2(y, x);
        brng = Math.toDegrees(brng);
        if (brng < 0) {
            brng = brng + 360;
        }
        return brng;
    }

}

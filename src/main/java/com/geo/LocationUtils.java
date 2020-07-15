package com.geo;

import java.util.List;
import java.util.Random;

/**
 * Created by jiangfei on 2018/7/5.
 */
public class LocationUtils {

    static final double EARTH_RADIUS = 6372.796924;
    static final double PI = 3.1415926535898;

    /**
     * 根据中心坐标获取指定距离的随机坐标点
     *
     * @param center   中心坐标
     * @param distance 离中心坐标距离（单位：米）
     * @return 随机坐标
     */
    public static LatitudeLongitudePair getRandomLocation(LatitudeLongitudePair center, double distance) {
        if (distance <= 0) distance = 50;
        double lat, lon, brg;

        double dist = 0;
        double rad360 = 2 * PI;
        distance = distance / 1000;

        LatitudeLongitudePair location = new LatitudeLongitudePair();

        double maxdist = distance;
        maxdist = maxdist / EARTH_RADIUS;

        double startlat = rad(center.getLatitude());
        double startlon = rad(center.getLongitude());

        double cosdif = Math.cos(maxdist) - 1;
        double sinstartlat = Math.sin(startlat);
        double cosstartlat = Math.cos(startlat);
        dist = Math.acos((new Random().nextDouble() * cosdif + 1));

        brg = rad360 * new Random().nextDouble();
        lat = Math.asin(sinstartlat * Math.cos(dist) + cosstartlat * Math.sin(dist) * Math.cos(brg));

        lon = deg(normalizeLongitude(startlon * 1 + Math.atan2(Math.sin(brg) * Math.sin(dist) * cosstartlat, Math.cos(dist) - sinstartlat * Math.sin(lat))));
        lat = deg(lat);
        location.setLatitude(padZeroRight(lat));
        location.setLongitude(padZeroRight(lon));
        return location;
    }

    /**
     * 获取两点间的距离(单位：米)
     *
     * @param start 起始坐标
     * @param end   结束坐标
     * @return 距离
     */
    public static double getDistance(LatitudeLongitudePair start, LatitudeLongitudePair end) {
        double radLat1 = rad(start.getLatitude());
        double radLat2 = rad(end.getLatitude());
        double a = radLat1 - radLat2;
        double b = rad(start.getLongitude()) - rad(end.getLongitude());
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = (int) (s * 10000000) / 10000;
        return s;
    }

    /**
     * 是否在范围内
     *
     * @param point
     * @param pts
     * @return
     */
    public static boolean IsPtInPoly(LatitudeLongitudePair point, List<LatitudeLongitudePair> pts) {

        int N = pts.size();
        boolean boundOrVertex = true; //如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
        int intersectCount = 0;//cross points count of x
        double precision = 2e-10; //浮点类型计算时候与0比较时候的容差
        LatitudeLongitudePair p1, p2;//neighbour bound vertices
        LatitudeLongitudePair p = point; //当前点

        p1 = pts.get(0);//left vertex
        for (int i = 1; i <= N; ++i) {//check all rays
            if (p.equals(p1)) {
                return boundOrVertex;//p is an vertex
            }

            p2 = pts.get(i % N);//right vertex
            if (p.getLongitude() < Math.min(p1.getLongitude(), p2.getLongitude()) ||
                    p.getLongitude() > Math.max(p1.getLongitude(), p2.getLongitude())) {//ray is outside of our interests
                p1 = p2;
                continue;//next ray left point
            }

            if (p.getLongitude() > Math.min(p1.getLongitude(), p2.getLongitude()) &&
                    p.getLongitude() < Math.max(p1.getLongitude(), p2.getLongitude())) {//ray is crossing over by the algorithm (common part of)
                if (p.getLatitude() <= Math.max(p1.getLatitude(), p2.getLatitude())) {//x is before of ray
                    if (p1.getLongitude() == p2.getLongitude() &&
                            p.getLatitude() >= Math.min(p1.getLatitude(), p2.getLatitude())) {//overlies on a horizontal ray
                        return boundOrVertex;
                    }

                    if (p1.getLatitude() == p2.getLatitude()) {//ray is vertical
                        if (p1.getLatitude() == p.getLatitude()) {//overlies on a vertical ray
                            return boundOrVertex;
                        } else {//before ray
                            ++intersectCount;
                        }
                    } else {//cross point on the left side
                        double xinters = (p.getLongitude() - p1.getLongitude()) *
                                (p2.getLatitude() - p1.getLatitude()) / (p2.getLongitude() - p1.getLongitude()) + p1.getLatitude();//cross point of y
                        if (Math.abs(p.getLatitude() - xinters) < precision) {//overlies on a ray
                            return boundOrVertex;
                        }

                        if (p.getLatitude() < xinters) {//before ray
                            ++intersectCount;
                        }
                    }
                }
            } else {//special case when ray is crossing through the vertex
                if (p.getLongitude() == p2.getLongitude() && p.getLatitude() <= p2.getLatitude()) {//p crossing over p2
                    LatitudeLongitudePair p3 = pts.get((i + 1) % N); //next vertex
                    if (p.getLongitude() >= Math.min(p1.getLongitude(), p3.getLongitude()) &&
                            p.getLongitude() <= Math.max(p1.getLongitude(), p3.getLongitude())) {//p.x lies between p1.x & p3.x
                        ++intersectCount;
                    } else {
                        intersectCount += 2;
                    }
                }
            }
            p1 = p2;//next ray left point
        }

        if (intersectCount % 2 == 0) {//偶数在多边形外
            return false;
        } else { //奇数在多边形内
            return true;
        }

    }

    //=====================================分割线========================//


    /**
     * 弧度
     *
     * @param d
     * @return
     */
    private static double rad(double d) {
        return d * PI / 180.0;
    }

    /**
     * 角度
     *
     * @param rd
     * @return
     */
    private static double deg(double rd) {
        return (rd * 180 / Math.PI);
    }

    private static double normalizeLongitude(double lon) {
        double n = PI;
        if (lon > n) {
            lon = lon - 2 * n;
        } else if (lon < -n) {
            lon = lon + 2 * n;
        }
        return lon;
    }

    private static double padZeroRight(double s) {
        double sigDigits = 8;
        s = Math.round(s * Math.pow(10, sigDigits)) / Math.pow(10, sigDigits);
        return s;
    }

}

package com.s2;

/**
 * 坐标
 */
public strictfp class S2LatLng {
    public static final double EARTH_RADIUS_METERS = 6367000.0;

    public static final S2LatLng CENTER = new S2LatLng(0.0, 0.0);

    private final double latRadians;
    private final double lngRadians;

    public static S2LatLng fromRadians(double latRadians, double lngRadians) {
        return new S2LatLng(latRadians, lngRadians);
    }

    public static S2LatLng fromDegrees(double latDegrees, double lngDegrees) {
        return new S2LatLng(S1Angle.degrees(latDegrees), S1Angle.degrees(lngDegrees));
    }

    public static S2LatLng fromLocation(double lng, double lat) {
        return fromDegrees(lat, lng);
    }

    //计算 纬度弧度 atan2（ z,  开根号(x*x + y*y)）
    public static S1Angle latitude(S2Point p) {
        return S1Angle.radians(
                Math.atan2(p.get(2), Math.sqrt(p.get(0) * p.get(0) + p.get(1) * p.get(1))));
    }

    //计算 经度弧度 atan2（y,x)
    public static S1Angle longitude(S2Point p) {
        // Note that atan2(0, 0) is defined to be zero.
        return S1Angle.radians(Math.atan2(p.get(1), p.get(0)));
    }

    private S2LatLng(double latRadians, double lngRadians) {
        this.latRadians = latRadians;
        this.lngRadians = lngRadians;
    }

    public S2LatLng(S1Angle lat, S1Angle lng) {

        this(lat.radians(), lng.radians());
    }

    public S2LatLng() {

        this(0, 0);
    }

    public S2LatLng(S2Point p) {
        this(Math.atan2(p.z, Math.sqrt(p.x * p.x + p.y * p.y)), Math.atan2(p.y, p.x));
    }

    public S1Angle lat() {
        return S1Angle.radians(latRadians);
    }

    /**
     * 纬弧度
     */
    public double latRadians() {
        return latRadians;
    }

    /**
     * 纬度
     */
    public double latDegrees() {
        return 180.0 / Math.PI * latRadians;
    }

    public S1Angle lng() {
        return S1Angle.radians(lngRadians);
    }

    /**
     * 经弧度
     */
    public double lngRadians() {
        return lngRadians;
    }

    /**
     * 经度
     */
    public double lngDegrees() {
        return 180.0 / Math.PI * lngRadians;
    }


    public S2Point toPoint() {
        double phi = lat().radians(); //纬度弧度 90-θ
        double theta = lng().radians(); // 经度弧度 φ
        double cosphi = Math.cos(phi);  // sin(90-phi)
        // cos φ * sin θ ,sin φ * sin θ, sin(phi)
        return new S2Point(Math.cos(theta) * cosphi, Math.sin(theta) * cosphi, Math.sin(phi));
    }


    public S1Angle getDistance(final S2LatLng o) {

        double lat1 = lat().radians();      //纬度弧度1
        double lat2 = o.lat().radians();    //纬度弧度2

        double lng1 = lng().radians();      //经度弧度1
        double lng2 = o.lng().radians();    //经度弧度2

        double dlat = Math.sin(0.5 * (lat2 - lat1));  //z

        double dlng = Math.sin(0.5 * (lng2 - lng1));  //y


        double x = dlat * dlat + dlng * dlng * Math.cos(lat1) * Math.cos(lat2);

        return S1Angle.radians(2 * Math.atan2(Math.sqrt(x), Math.sqrt(Math.max(0.0, 1.0 - x))));
    }

    public double getDistance(final S2LatLng o, double radius) {
        return getDistance(o).radians() * radius;
    }

    public double getEarthDistance(final S2LatLng o) {
        return getDistance(o, EARTH_RADIUS_METERS);
    }


    @Override
    public boolean equals(Object that) {
        if (that instanceof S2LatLng) {
            S2LatLng o = (S2LatLng) that;
            return (latRadians == o.latRadians) && (lngRadians == o.lngRadians);
        }
        return false;
    }

    @Override
    public int hashCode() {
        long value = 17;
        value += 37 * value + Double.doubleToLongBits(latRadians);
        value += 37 * value + Double.doubleToLongBits(lngRadians);
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public String toString() {
        return "(" + latRadians + ", " + lngRadians + ")";
    }

    public String toStringDegrees() {
        return "(" + latDegrees() + ", " + lngDegrees() + ")";
    }

}

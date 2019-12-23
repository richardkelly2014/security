package com.s2;

/**
 * 角度
 */
public final strictfp class S1Angle implements Comparable<S1Angle> {

    /**
     * 弧度
     */
    private final double radians;

    public double radians() {
        return radians;
    }

    /**
     * 弧度->角度
     *
     * @return
     */
    public double degrees() {
        return radians * (180 / Math.PI);
    }

    public S1Angle() {
        this.radians = 0;
    }

    private S1Angle(double radians) {
        this.radians = radians;
    }


    public static S1Angle radians(double radians) {
        return new S1Angle(radians);
    }

    /**
     * 角度->弧度
     *
     * @param degrees
     * @return
     */
    public static S1Angle degrees(double degrees) {
        return new S1Angle(degrees * (Math.PI / 180));
    }

    public boolean lessThan(S1Angle that) {
        return this.radians() < that.radians();
    }

    public boolean greaterThan(S1Angle that) {
        return this.radians() > that.radians();
    }

    public boolean lessOrEquals(S1Angle that) {
        return this.radians() <= that.radians();
    }

    public boolean greaterOrEquals(S1Angle that) {
        return this.radians() >= that.radians();
    }

    public static S1Angle max(S1Angle left, S1Angle right) {
        return right.greaterThan(left) ? right : left;
    }

    public static S1Angle min(S1Angle left, S1Angle right) {
        return right.greaterThan(left) ? left : right;
    }


    //

    @Override
    public boolean equals(Object that) {
        if (that instanceof S1Angle) {
            return this.radians() == ((S1Angle) that).radians();
        }
        return false;
    }

    @Override
    public int hashCode() {
        long value = Double.doubleToLongBits(radians);
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public String toString() {
        return degrees() + "d";
    }

    @Override
    public int compareTo(S1Angle that) {
        return this.radians < that.radians ? -1 : this.radians > that.radians ? 1 : 0;
    }
}

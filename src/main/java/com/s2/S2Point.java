package com.s2;

/**
 * 三维坐标系
 */
public strictfp class S2Point implements Comparable<S2Point> {

    public final double x;
    public final double y;
    public final double z;

    public S2Point() {
        x = y = z = 0;
    }

    public S2Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    //减
    public static S2Point minus(S2Point p1, S2Point p2) {
        return sub(p1, p2);
    }

    //相反对称点
    public static S2Point neg(S2Point p) {
        return new S2Point(-p.x, -p.y, -p.z);
    }

    //三维坐标系 点 到原点 的长度 （r*r = x*x + y*y + z*z
    public double norm2() {
        return x * x + y * y + z * z;
    }

    //三维坐标系 点 到原点 的长度 开根号
    public double norm() {
        return Math.sqrt(norm2());
    }

    //三维坐标系 ，向量叉积 （a*b)
    public static S2Point crossProd(final S2Point p1, final S2Point p2) {
        return new S2Point(
                p1.y * p2.z - p1.z * p2.y, p1.z * p2.x - p1.x * p2.z, p1.x * p2.y - p1.y * p2.x);
    }

    //三维坐标系 ，向量点积 （a.b),两个向量之间的角度
    //>0 锐角 同方向
    //=0 直角 垂直
    //<0 钝角 反方向
    public double dotProd(S2Point that) {

        return this.x * that.x + this.y * that.y + this.z * that.z;
    }

    /**
     * 返回两个矢量之间的弧度角
     *
     * @param va
     * @return
     */
    public double angle(S2Point va) {

        return Math.atan2(crossProd(this, va).norm(), this.dotProd(va));
    }

    // 加
    public static S2Point add(final S2Point p1, final S2Point p2) {
        return new S2Point(p1.x + p2.x, p1.y + p2.y, p1.z + p2.z);
    }

    //减
    public static S2Point sub(final S2Point p1, final S2Point p2) {
        return new S2Point(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
    }

    //乘
    public static S2Point mul(final S2Point p, double m) {

        return new S2Point(m * p.x, m * p.y, m * p.z);
    }

    //除
    public static S2Point div(final S2Point p, double m) {

        return new S2Point(p.x / m, p.y / m, p.z / m);
    }

    public static S2Point fabs(S2Point p) {

        return new S2Point(Math.abs(p.x), Math.abs(p.y), Math.abs(p.z));
    }

    public static S2Point normalize(S2Point p) {
        double norm = p.norm();
        if (norm != 0) {
            norm = 1.0 / norm;
        }
        return S2Point.mul(p, norm);
    }

    /**
     * 计算3个点夹角
     *
     * @param start
     * @param middle
     * @param end
     * @return
     */
    public static double angle(S2Point start, S2Point middle, S2Point end) {
        double x1 = start.x, y1 = start.y, z1 = start.z;
        double x2 = middle.x, y2 = middle.y, z2 = middle.z;
        double x3 = end.x, y3 = end.y, z3 = end.z;

        double _p1p2 = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
        double _p2p3 = Math.sqrt(Math.pow(x3 - x2, 2) + Math.pow(y3 - y2, 2) + Math.pow(z3 - z2, 2));

        double _p = (x1 - x2) * (x3 - x2) + (y1 - y2) * (y3 - y2) + (z1 - z2) * (z3 - z2);

        double radians = Math.acos(_p / (_p1p2 * _p2p3));

        double degrees = radians * (180 / Math.PI);
        return degrees;
    }

    public double get(int axis) {
        return (axis == 0) ? x : (axis == 1) ? y : z;
    }


    @Override
    public boolean equals(Object that) {
        if (!(that instanceof S2Point)) {
            return false;
        }
        S2Point thatPoint = (S2Point) that;
        return this.x == thatPoint.x && this.y == thatPoint.y && this.z == thatPoint.z;
    }

    public boolean lessThan(S2Point vb) {
        if (x < vb.x) {
            return true;
        }
        if (vb.x < x) {
            return false;
        }
        if (y < vb.y) {
            return true;
        }
        if (vb.y < y) {
            return false;
        }
        if (z < vb.z) {
            return true;
        }
        return false;
    }

    // Required for Comparable
    @Override
    public int compareTo(S2Point other) {
        return (lessThan(other) ? -1 : (equals(other) ? 0 : 1));
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public String toDegreesString() {
        S2LatLng s2LatLng = new S2LatLng(this);
        return "(" + Double.toString(s2LatLng.latDegrees()) + ", "
                + Double.toString(s2LatLng.lngDegrees()) + ")";
    }

    @Override
    public int hashCode() {
        long value = 17;
        value += 37 * value + Double.doubleToLongBits(Math.abs(x));
        value += 37 * value + Double.doubleToLongBits(Math.abs(y));
        value += 37 * value + Double.doubleToLongBits(Math.abs(z));
        return (int) (value ^ (value >>> 32));
    }
}

package com.way;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WayLine {

    private WayPoint start;

    private WayPoint end;

    /**
     * 计算与赤道的角度
     *
     * @return
     */
    public double angle() {
        double x = end.getLat() - start.getLat();
        double y = end.getLng() - start.getLng();

        double brng = Math.atan2(x, y);
        double xrng = Math.toDegrees(brng);

        System.out.println(xrng);

        if (xrng < 0) {
            xrng = xrng + 360;
        }

        return xrng;
    }

    /**
     * 2条线夹角
     *
     * @param other
     * @return
     */
    public double angle(WayLine other) {

        double angle1 = this.angle();
        double angle2 = other.angle();

        double delta_angle = Math.abs(angle2 - angle1);

        return delta_angle;
    }
}

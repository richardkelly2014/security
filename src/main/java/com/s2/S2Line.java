package com.s2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class S2Line {

    /**
     * 开始点
     */
    private S2LatLng start;

    /**
     * 结束点
     */
    private S2LatLng end;

    /**
     * 2条线夹角
     *
     * @param other
     * @return
     */
    public double angle(S2Line other) {

        S2Point s1 = this.getEnd().toPoint(),
                m1 = this.getStart().toPoint(),
                e1 = other.getStart().toPoint();

        S2Point s2 = this.getStart().toPoint(),
                m2 = other.getStart().toPoint(),
                e2 = other.getEnd().toPoint();

        double angle1 = S2Point.angle(s1, m1, e1);
        double angle2 = S2Point.angle(s2, m2, e2);

        double angle = Math.abs(angle1 + angle2 - 180);

        return angle;
    }
}

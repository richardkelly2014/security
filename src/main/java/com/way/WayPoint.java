package com.way;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WayPoint {

    private double lng;

    private double lat;

    /**
     * 计算 3个点中间 夹角
     *
     * @param start
     * @param middle
     * @param end
     * @return
     */
    public static double angle(WayPoint start, WayPoint middle, WayPoint end) {
        double distance12 = Math.pow(middle.getLng() - start.getLng(), 2) +
                Math.pow(middle.getLat() - start.getLat(), 2);

        double distance13 = Math.pow(end.getLng() - start.getLng(), 2) +
                Math.pow(end.getLat() - start.getLat(), 2);

        double distance23 = Math.pow(end.getLng() - middle.getLng(), 2) +
                Math.pow(end.getLat() - middle.getLat(), 2);

        if (distance23 == 0) {
            return 0;
        } else {
            double cosine = (distance12 + distance23 - distance13) /
                    (2 * Math.sqrt(distance12) * Math.sqrt(distance23));
            double angle = Math.acos(cosine);
            double degrees = Math.toDegrees(angle);
            return degrees;
        }

    }
}

package com.s2;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class AngleTest {

    @Test
    public void test1() {
        log.info("{}", S1Angle.degrees(130.432).radians());
    }

    @Test
    public void test2() {

        S2LatLng latLng = S2LatLng.fromLocation(116.352435, 40.009696);
        S2LatLng latLng1 = S2LatLng.fromLocation(117.844945, 40.007904);

        log.info("{}", latLng.getEarthDistance(latLng1));


        S2Point s2Point = latLng.toPoint();
        S2Point s2Point1 = latLng1.toPoint();

        S1Angle angle = S1Angle.radians(s2Point.angle(s2Point1));

        log.info("{},{}", angle.radians(), angle.degrees());

    }

    @Test
    public void test3() {

        test2();

        double x = 40.009696 - 40.007904;

        double y = 116.352435 - 117.844945;

        double brng = Math.atan2(x, y);

        double xrng = Math.toDegrees(brng);

        log.info("{},{}", brng, xrng);

    }

}

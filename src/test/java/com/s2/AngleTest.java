package com.s2;

import com.way.WayLine;
import com.way.WayPoint;
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

        S2LatLng latLng = S2LatLng.fromLocation(116.352435, 40.009909);
        S2LatLng latLng1 = S2LatLng.fromLocation(116.356426, 40.013443);

        log.info("{}", latLng.getEarthDistance(latLng1));


        S2Point s2Point = latLng.toPoint();
        S2Point s2Point1 = latLng1.toPoint();

        S1Angle angle = S1Angle.radians(s2Point.angle(s2Point1));

        log.info("{},{}", angle.radians(), angle.degrees());

    }

    @Test
    public void test4() {
        WayLine line = WayLine.builder()
                .start(WayPoint.builder().lng(116.352456).lat(40.009778).build())
                .end(WayPoint.builder().lng(116.355053).lat(40.008956).build())
                .build();

        WayLine other = WayLine.builder()
                .start(WayPoint.builder().lng(116.354967).lat(40.009909).build())
                .end(WayPoint.builder().lng(116.360567).lat(40.010879).build())
                .build();

        log.info("{}", line.angle(other));

        log.info("{}", PointCalculateUtil.getAngleOnPlane(116.352456, 40.009778,
                116.355053, 40.008956));

        log.info("{}", PointCalculateUtil.getAngleOnPlane(116.354967, 40.009909,
                116.360567, 40.010879));

        S2Line s2Line = S2Line.builder()
                .start(S2LatLng.fromLocation(116.352456, 40.009778))
                .end(S2LatLng.fromLocation(116.355053, 40.008956))
                .build();

        S2Line s2LineOther = S2Line.builder()
                .start(S2LatLng.fromLocation(116.354967, 40.009909))
                .end(S2LatLng.fromLocation(116.360567, 40.010879))
                .build();

        log.info("{}", s2Line.angle(s2LineOther));
    }

    @Test
    public void test3() {

        //test2();

//        log.info("{}", t1(116.352435, 40.009909, 116.356426, 40.013443));
//
//        WayLine line = WayLine.builder()
//                .start(WayPoint.builder().lng(116.352435).lat(40.009909).build())
//                .end(WayPoint.builder().lng(116.356426).lat(40.013443).build())
//                .build();
//
//        log.info("{}", line.angle());

//        WayPoint start = WayPoint.builder().lng(116.35722).lat(40.011881).build();
//        WayPoint middle = WayPoint.builder().lng(116.352413).lat(40.007723).build();
//        WayPoint end = WayPoint.builder().lng(116.359044).lat(40.008331).build();

//        S2Point startPoint = S2LatLng.fromLocation(116.35722, 40.011881).toPoint();
//        S2Point middlePoint = S2LatLng.fromLocation(116.352413, 40.007723).toPoint();
//        S2Point endPoint = S2LatLng.fromLocation(116.359044, 40.008331).toPoint();

        S2Point startPoint = S2LatLng.fromLocation(0, 3).toPoint();
        S2Point middlePoint = S2LatLng.fromLocation(0, 0).toPoint();
        S2Point endPoint = S2LatLng.fromLocation(5, 0).toPoint();

        WayPoint start = WayPoint.builder().lng(0).lat(3).build();
        WayPoint middle = WayPoint.builder().lng(0).lat(0).build();
        WayPoint end = WayPoint.builder().lng(5).lat(0).build();

        log.info("{}", S2Point.angle(startPoint, middlePoint, endPoint));

        log.info("{}", WayPoint.angle(start, middle, end));


    }


}

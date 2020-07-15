package com.disruptor;

import com.geo.GeoHashUtils;
import com.geo.LatitudeAndLongitudeUtils;
import com.github.davidmoten.geo.Base32;
import org.junit.Test;

public class GeoTest {

    @Test
    public void test1() {

        String geoHash = GeoHashUtils.getGeoHash(39.940715, 116.354885, 12);


        System.out.println("====>" + geoHash);

//        System.out.println(Base32.decodeBase32("wx4g8s000000"));
//        System.out.println(Base32.decodeBase32("wx4gbs000000"));
//        System.out.println(Base32.decodeBase32("wx4exs000000"));
        System.out.println(Base32.decodeBase32("wx4epwzp92ft"));
//
        System.out.println(Base32.encodeBase32(1041613197650725337L));

//        System.out.println(GeoHashUtils.getNeighboursGeoHashList(geoHash));
//
//        System.out.println("=====>" + GeoHashUtils.bbox(geoHash));

        System.out.println(LatitudeAndLongitudeUtils.calculateDistanceAccurateToMeter(116.354885, 39.940715,
                0.0, 0.0));

    }

}

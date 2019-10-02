package com.disruptor;

import com.geo.GeoHashUtils;
import com.github.davidmoten.geo.Base32;
import org.junit.Test;

public class GeoTest {

    @Test
    public void test1() {
        String geoHash = GeoHashUtils.getGeoHash(40.05615234375, 116.38916015625, 12);
        System.out.println("====>" + geoHash);

        System.out.println(Base32.decodeBase32("wx4g8s000000"));
        System.out.println(Base32.decodeBase32("wx4gbs000000"));
        System.out.println(Base32.decodeBase32("wx4exs000000"));
        System.out.println(Base32.decodeBase32("wx4ezs000000"));

        System.out.println(Base32.encodeBase32(1041614944639909888L));

        //System.out.println(GeoHashUtils.getNeighboursGeoHashList(geoHash));

        //System.out.println("=====>" + GeoHashUtils.bbox(geoHash));

    }

}

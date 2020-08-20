package com.disruptor;

import com.geo.GeoHashUtils;
import com.geo.LatitudeAndLongitudeUtils;
import com.github.davidmoten.geo.Base32;
import com.redisGeo.GeoHash;
import com.redisGeo.GeoHashArea;
import com.redisGeo.GeoHashBits;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ConcurrentSkipListMap;

@Slf4j
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

    @Test
    public void test2() {
        GeoHash geoHash = new GeoHash();

        GeoHashBits bits = new GeoHashBits();
        geoHash.geohashEncodeWGS84(116.354885, 39.940715, bits);

        log.info("{}", bits);
        GeoHashArea area = new GeoHashArea();
        geoHash.geohashDecodeWGS84(bits, area);
        log.info("{}", area);

        double[] xy = new double[2];
        geoHash.geohashDecodeToLongLatWGS84(bits, xy);
        log.info("{}", xy);
    }

    @Test
    public void test3() {

        ConcurrentSkipListMap<Integer, Integer> csl = new ConcurrentSkipListMap<>();
        csl.put(3, 1);

        csl.put(2, 2);

        csl.put(5, 3);

        log.info("{}", csl);
        log.info("{}", Math.cos(Math.toRadians(60)));

    }
}

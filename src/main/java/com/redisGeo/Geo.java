package com.redisGeo;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Slf4j
public class Geo {

    private GeoHash geoHash = new GeoHash();
    private GeohashHelper helper = new GeohashHelper();

    private ConcurrentMap<Integer, Long> keyScores = new ConcurrentHashMap<>();
    private ConcurrentSkipListMap<Long, ConcurrentMap<Integer, Long>> scoreKeys = new ConcurrentSkipListMap<>();

    /**
     * 搜索
     *
     * @param longitude
     * @param latitude
     * @param distance
     */
    public void search(double longitude, double latitude, double distance) {
        GeoHashRadius radius = helper.geohashGetAreasByRadiusWGS84(longitude, latitude, distance);
        GeoHashBits[] neighbors = new GeoHashBits[9];
        int i, count = 0, last_processed = 0;


        neighbors[0] = radius.hash;
        neighbors[1] = radius.neighbors.north;
        neighbors[2] = radius.neighbors.south;
        neighbors[3] = radius.neighbors.east;
        neighbors[4] = radius.neighbors.west;
        neighbors[5] = radius.neighbors.north_east;
        neighbors[6] = radius.neighbors.north_west;
        neighbors[7] = radius.neighbors.south_east;
        neighbors[8] = radius.neighbors.south_west;

        for (i = 0; i < 9; i++) {
            if (GeoHash.HASHISZERO(neighbors[i])) {
                log.info("hash zero");
                continue;
            }
            if (last_processed > 0 && neighbors[i].bits == neighbors[last_processed].bits &&
                    neighbors[i].step == neighbors[last_processed].step) {
                log.info("hash same as previous");
                continue;
            }
            count += membersOfGeoHashBox(neighbors[i], longitude, latitude, distance);
            last_processed = i;
        }
    }

    /**
     * 添加
     *
     * @param longitude
     * @param latitude
     * @param key
     */
    public void add(double longitude, double latitude, Integer key) {
        GeoHashBits hash = new GeoHashBits();
        geoHash.geohashEncodeWGS84(longitude, latitude, hash);

        long bits = helper.geohashAlign52Bits(hash);

        if (keyScores.containsKey(key)) {
            //已经存在
            long oldScore = keyScores.get(key);

            if ((oldScore - bits) != 0) {
                //需处理
                //移除原score下面对应的key
                ConcurrentMap<Integer, Long> oldMap = scoreKeys.get(oldScore);
                oldMap.remove(key);

                addNew(key, bits);
            }
        } else {
            //不存在
            addNew(key, bits);
        }

    }

    /**
     * remove
     *
     * @param key
     */
    public void remove(Integer key) {
        if (keyScores.containsKey(key)) {

            long score = keyScores.remove(key);

            ConcurrentMap<Integer, Long> map = scoreKeys.get(score);
            map.remove(key);
        }
    }


    /**
     * 新添加
     *
     * @param key
     * @param bits
     */
    private void addNew(Integer key, Long bits) {
        keyScores.put(key, bits);

        //当前bits 在 score 中是否存在
        if (!scoreKeys.containsKey(bits)) {
            //不存在
            ConcurrentMap<Integer, Long> map = new ConcurrentHashMap<>();
            map.put(key, bits);
            scoreKeys.put(bits, map);
        } else {
            //存在,添加
            ConcurrentMap<Integer, Long> map = scoreKeys.get(bits);
            map.put(key, bits);
        }
    }

    int membersOfGeoHashBox(GeoHashBits hash, double lon, double lat, double radius) {
        long min, max;
        min = helper.geohashAlign52Bits(hash);
        hash.bits++;
        max = helper.geohashAlign52Bits(hash);

        return geoGetPointsInRange(min, max, lon, lat, radius);
    }

    int geoGetPointsInRange(long min, long max, double lon, double lat, double radius) {
        ConcurrentNavigableMap<Long, ConcurrentMap<Integer, Long>> subMap = scoreKeys.subMap(min, true, max, true);
        if (subMap != null && subMap.size() > 0) {

            for (Map.Entry<Long, ConcurrentMap<Integer, Long>> element : subMap.entrySet()) {
                long score = element.getKey();
                ConcurrentMap<Integer, Long> keys = element.getValue();
                geoAppendIfWithinRadius(lon, lat, radius, score);
            }

            return 1;
        } else {
            return 0;
        }
    }


    int geoAppendIfWithinRadius(double lon, double lat, double radius, long score) {

        double[] xy = new double[2];

        double distance = helper.geohashGetDistance(lon, lat, xy[0], xy[1]);
        log.info("{},{}", distance, radius);
        return 1;
    }

    int decodeGeohash(long bits, double[] xy) {

        GeoHashBits hash = new GeoHashBits();
        hash.bits = bits;
        hash.step = GeoHash.GEO_STEP_MAX;

        return geoHash.geohashDecodeToLongLatWGS84(hash, xy);
    }

}

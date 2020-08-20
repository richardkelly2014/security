package com.redisGeo;

import lombok.ToString;

@ToString
public class GeoHashRadius {

    public GeoHashBits hash;
    public GeoHashArea area;
    public GeoHashNeighbors neighbors;

}

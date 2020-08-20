package com.redisGeo;


import lombok.ToString;

/**
 * 相邻geohash
 */
@ToString
public class GeoHashNeighbors {
    public GeoHashBits north;
    public GeoHashBits east;
    public GeoHashBits west;
    public GeoHashBits south;
    public GeoHashBits north_east;
    public GeoHashBits south_east;
    public GeoHashBits north_west;
    public GeoHashBits south_west;
}

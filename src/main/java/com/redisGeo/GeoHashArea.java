package com.redisGeo;

import lombok.ToString;

@ToString
public class GeoHashArea {
    public GeoHashBits hash = new GeoHashBits();
    public GeoHashRange longitude = new GeoHashRange();
    public GeoHashRange latitude = new GeoHashRange();
}

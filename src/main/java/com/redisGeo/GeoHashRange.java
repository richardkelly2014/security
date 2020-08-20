package com.redisGeo;

import lombok.ToString;

@ToString
public class GeoHashRange {
    public double min;
    public double max;

    public GeoHashRange(double max, double min) {
        this.max = max;
        this.min = min;

    }

    public GeoHashRange() {
    }

}

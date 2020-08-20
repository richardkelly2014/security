package com.redisGeo;


import lombok.ToString;

@ToString
public class GeoHashBits {
    public long bits;
    public int step;

    /**
     * 克隆一下
     *
     * @return
     */
    GeoHashBits cl() {
        GeoHashBits geoHashBits = new GeoHashBits();
        geoHashBits.bits = this.bits;
        geoHashBits.step = this.step;

        return geoHashBits;
    }
}

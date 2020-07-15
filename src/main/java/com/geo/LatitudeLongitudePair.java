package com.geo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by jiangfei on 2018/7/5.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
public class LatitudeLongitudePair {
	
	private double longitude; // 经度
	private double latitude; // 纬度
	private String adCode;
	public final static int DEFAULT_ROUND_MODE = 7;

	public LatitudeLongitudePair(final double longitude, final double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	/**
	 * 纬度-经度
	 *
	 * @param longitude 经度
	 * @param latitude  纬度
	 * @param scala     小数点位数
	 * @return
	 */
	public static LatitudeLongitudePair of(final double longitude, final double latitude, final int scala) {
		return new LatitudeLongitudePair(
				new BigDecimal(longitude).setScale(scala, BigDecimal.ROUND_HALF_EVEN).doubleValue(),
				new BigDecimal(latitude).setScale(scala, BigDecimal.ROUND_HALF_EVEN).doubleValue());
	}

	/**
	 * 纬度-经度
	 *
	 * @param longitude 经度
	 * @param latitude  纬度
	 * @return
	 */
	public static LatitudeLongitudePair of(final double longitude, final double latitude) {
		return of(longitude, latitude, DEFAULT_ROUND_MODE);
	}

	/**
	 * 是否0
	 *
	 * @return
	 */
	public boolean isZero() {

		return this.longitude == 0 && this.latitude == 0;
	}
}

package com.dns.android.authoritative.domain;

import java.io.Serializable;

public class CityList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3052241088003374883L;

	City[] cities ;

	Meta meta ;

	public City[] getCities() {
		return cities;
	}

	public void setCities(City[] cities) {
		this.cities = cities;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}

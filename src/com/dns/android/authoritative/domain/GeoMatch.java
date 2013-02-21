package com.dns.android.authoritative.domain;

import java.io.Serializable;
import java.util.Date;

public class GeoMatch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8677521290180264453L;

	Integer id ;

	String geo_group ;

	String country ;

	String region ;

	String city ;

	Date date_created ;

	Date date_last_modified ;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGeo_group() {
		return geo_group;
	}

	public void setGeo_group(String geo_group) {
		this.geo_group = geo_group;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getDate_created() {
		return date_created;
	}

	public void setDate_created(Date date_created) {
		this.date_created = date_created;
	}

	public Date getDate_last_modified() {
		return date_last_modified;
	}

	public void setDate_last_modified(Date date_last_modified) {
		this.date_last_modified = date_last_modified;
	}
}

package com.dns.android.authoritative.domain;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "cities")
public class City implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4522000425053219287L;

	@DatabaseField(id = true)
	Integer id ;

	@DatabaseField(index = true)
	String name ;

	@DatabaseField(foreign = true, columnName = "region_id")
	Region region ;

	@DatabaseField
	String area_code ;

	@DatabaseField
	String charset ;

	@DatabaseField(index = true)
	Float latitude ;

	@DatabaseField(index = true)
	Float longitude ;

	@DatabaseField(index = true)
	String postal_code ;

	public City() {
		super() ;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public String getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}
}

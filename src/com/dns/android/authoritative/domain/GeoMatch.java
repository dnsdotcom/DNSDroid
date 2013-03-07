package com.dns.android.authoritative.domain;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "geo_matches")
public class GeoMatch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8677521290180264453L;

	@DatabaseField(id = true)
	Integer id ;

	@DatabaseField(foreign = true, columnName = "geoGroup_id", canBeNull = true, index = true)
	GeoGroup geo_group ;

	@DatabaseField(foreign = true, columnName = "country_id", canBeNull = true)
	Country country ;

	@DatabaseField(foreign = true, columnName = "region_id", canBeNull = true)
	Region region ;

	@DatabaseField(foreign = true, columnName = "city_id", canBeNull = true)
	City city ;

	@DatabaseField(index = true)
	Date date_created ;

	@DatabaseField(index = true)
	Date date_last_modified ;

	public GeoMatch() {
		super() ;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public GeoGroup getGeoGroup() {
		return geo_group;
	}

	public void setGeoGroup(GeoGroup geo_group) {
		this.geo_group = geo_group;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
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

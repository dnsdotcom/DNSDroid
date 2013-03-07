package com.dns.android.authoritative.domain;

import java.io.Serializable;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "countries")
public class Country implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3114092338610139465L;

	@DatabaseField(id = true)
	Integer id ;

	@ForeignCollectionField(foreignFieldName = "country", eager = false, orderColumnName = "name", orderAscending = true)
	ForeignCollection<Region> regions ;

	@DatabaseField
	String continent_code ;

	@DatabaseField
	String iso_code ;

	@DatabaseField
	String iso_num ;

	@DatabaseField(index = true)
	String name ;

	public Country() {
		super() ;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ForeignCollection<Region> getRegions() {
		return regions;
	}

	public void addRegion(Region region) {
		this.regions.add(region) ;
	}

	public String getContinent_code() {
		return continent_code;
	}

	public void setContinent_code(String continent_code) {
		this.continent_code = continent_code;
	}

	public String getIso_code() {
		return iso_code;
	}

	public void setIso_code(String iso_code) {
		this.iso_code = iso_code;
	}

	public String getIso_num() {
		return iso_num;
	}

	public void setIso_num(String iso_num) {
		this.iso_num = iso_num;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

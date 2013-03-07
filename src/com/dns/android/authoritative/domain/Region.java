package com.dns.android.authoritative.domain;

import java.io.Serializable;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "regions")
public class Region implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8443613043357773492L;

	@DatabaseField(id = true)
	Integer id ;

	@DatabaseField(foreign = true, columnName = "country_id")
	Country country ;

	@DatabaseField
	String code ;

	@DatabaseField(index = true)
	String name ;

	@ForeignCollectionField(eager = false, foreignFieldName = "region")
	ForeignCollection<City> cities ;

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
}

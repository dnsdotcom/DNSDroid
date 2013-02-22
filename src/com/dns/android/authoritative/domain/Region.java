package com.dns.android.authoritative.domain;

import java.io.Serializable;

public class Region implements Serializable, GenericEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8443613043357773492L;

	String[] cities ;

	String country ;

	String code ;

	Integer id ;

	String name ;

	public String[] getCities() {
		return cities;
	}

	public void setCities(String[] cities) {
		this.cities = cities;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
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

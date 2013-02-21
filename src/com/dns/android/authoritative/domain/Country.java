package com.dns.android.authoritative.domain;

import java.io.Serializable;

public class Country implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3114092338610139465L;

	Integer id ;

	String[] regions ;

	String continent_code ;

	String iso_code ;

	String iso_num ;

	String name ;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String[] getRegions() {
		return regions;
	}

	public void setRegions(String[] regions) {
		this.regions = regions;
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

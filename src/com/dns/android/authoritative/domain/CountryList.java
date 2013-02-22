package com.dns.android.authoritative.domain;

import java.io.Serializable;

public class CountryList implements Serializable, GenericEntityList<Country> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1528417109848185491L;

	Country[] countries ;

	Meta meta ;

	public Country[] getItems() {
		return countries;
	}

	public void setItems(Country[] countries) {
		this.countries = countries;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}

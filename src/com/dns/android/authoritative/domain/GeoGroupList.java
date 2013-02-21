package com.dns.android.authoritative.domain;

import java.io.Serializable;

public class GeoGroupList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7692520864137127560L;

	GeoGroup[] geo_groups ;

	Meta meta ;

	public GeoGroup[] getGeo_groups() {
		return geo_groups;
	}

	public void setGeo_groups(GeoGroup[] geo_groups) {
		this.geo_groups = geo_groups;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}

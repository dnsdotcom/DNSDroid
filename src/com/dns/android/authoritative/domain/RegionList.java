package com.dns.android.authoritative.domain;

import java.io.Serializable;

public class RegionList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2709734864605853368L;

	Region[] regions ;

	Meta meta ;

	public Region[] getRegions() {
		return regions;
	}

	public void setRegions(Region[] regions) {
		this.regions = regions;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}

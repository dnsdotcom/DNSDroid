package com.dns.android.authoritative.domain;

import java.io.Serializable;

public class GeoMatchList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4043154436705213589L;

	GeoMatch[] matches ;

	Meta meta ;

	public GeoMatch[] getMatches() {
		return matches;
	}

	public void setMatches(GeoMatch[] matches) {
		this.matches = matches;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}

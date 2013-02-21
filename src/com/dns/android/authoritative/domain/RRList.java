package com.dns.android.authoritative.domain;

import java.io.Serializable;

public class RRList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5135437546954015444L;

	RR[] rrs ;

	Meta meta ;

	public RR[] getRrs() {
		return rrs;
	}

	public void setRrs(RR[] rrs) {
		this.rrs = rrs;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}

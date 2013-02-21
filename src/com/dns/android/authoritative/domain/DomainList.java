package com.dns.android.authoritative.domain;

import java.io.Serializable;

public class DomainList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1329000418447456192L;

	Domain[] domains ;

	Meta meta ;

	public Domain[] getDomains() {
		return domains;
	}

	public void setDomains(Domain[] domains) {
		this.domains = domains;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}

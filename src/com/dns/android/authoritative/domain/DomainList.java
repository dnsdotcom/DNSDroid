package com.dns.android.authoritative.domain;

import java.io.Serializable;

public class DomainList implements Serializable, GenericEntityList<Domain> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1329000418447456192L;

	Domain[] domains ;

	Meta meta ;

	public Domain[] getItems() {
		return domains;
	}

	public void setItems(Domain[] domains) {
		this.domains = domains;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}

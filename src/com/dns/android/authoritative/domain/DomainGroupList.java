package com.dns.android.authoritative.domain;

import java.io.Serializable;

public class DomainGroupList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1667440229735354343L;

	DomainGroup[] domain_groups ;

	Meta meta ;

	public DomainGroup[] getDomain_groups() {
		return domain_groups;
	}

	public void setDomain_groups(DomainGroup[] domain_groups) {
		this.domain_groups = domain_groups;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}

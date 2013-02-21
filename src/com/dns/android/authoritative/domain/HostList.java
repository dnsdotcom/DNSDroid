package com.dns.android.authoritative.domain;

import java.io.Serializable;

public class HostList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6867584698558150138L;

	Host[] hosts ;

	Meta meta ;

	public Host[] getHosts() {
		return hosts;
	}

	public void setHosts(Host[] hosts) {
		this.hosts = hosts;
	}

	public Meta getMeta() {
		return meta;
	}

	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}

package com.dns.mobile.data;

public class Domain {

	private long domainId = 0L ;
	private String name = null ;
	private boolean isGroupedDomain = false ;

	public long getDomainId() {
		return domainId;
	}

	public void setDomainId(long domainId) {
		this.domainId = domainId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isGroupedDomain() {
		return isGroupedDomain;
	}

	public void setGroupedDomain(boolean isGroupedDomain) {
		this.isGroupedDomain = isGroupedDomain;
	}
}

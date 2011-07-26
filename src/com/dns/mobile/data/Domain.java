package com.dns.mobile.data;

public class Domain {

	private long domainId = 0L ;
	private String name = null ;
	private boolean isGroupedDomain = false ;
	private String domainGroup = null ;

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

	public String getDomainGroup() {
		return domainGroup;
	}

	public void setDomainGroup(String domainGroup) {
		this.domainGroup = domainGroup;
	}
}

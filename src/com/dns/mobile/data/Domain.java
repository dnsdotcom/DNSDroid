package com.dns.mobile.data;

import java.io.Serializable;

public class Domain implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3792664259727486921L;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domainGroup == null) ? 0 : domainGroup.hashCode());
		result = prime * result + (int) (domainId ^ (domainId >>> 32));
		result = prime * result + (isGroupedDomain ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Domain other = (Domain) obj;
		if (domainGroup == null) {
			if (other.domainGroup != null)
				return false;
		} else if (!domainGroup.equals(other.domainGroup))
			return false;
		if (domainId != other.domainId)
			return false;
		if (isGroupedDomain != other.isGroupedDomain)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}

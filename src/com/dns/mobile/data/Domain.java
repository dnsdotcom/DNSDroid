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
	private String rName = null ;
	private boolean isXfr = false ;
	private String master = null ;
	private int refresh = 0 ;
	private String errMsgString = null ;

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

	public String getrName() {
		return rName;
	}

	public void setrName(String rName) {
		this.rName = rName;
	}

	public boolean isXfr() {
		return isXfr;
	}

	public void setXfr(boolean isXfr) {
		this.isXfr = isXfr;
	}

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}

	public int getRefresh() {
		return refresh;
	}

	public void setRefresh(int refresh) {
		this.refresh = refresh;
	}

	public String getErrMsgString() {
		return errMsgString;
	}

	public void setErrMsgString(String errMsgString) {
		this.errMsgString = errMsgString;
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
		if (domainId != other.domainId)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}

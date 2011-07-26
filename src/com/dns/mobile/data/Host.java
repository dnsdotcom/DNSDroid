package com.dns.mobile.data;

import java.io.Serializable;

public class Host implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2930345343669521947L;

	private String name = null ;
	private long recordCount = 0 ;
	private long hostId = 0 ;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;
	}

	public long getHostId() {
		return hostId;
	}

	public void setHostId(long hostId) {
		this.hostId = hostId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (hostId ^ (hostId >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (recordCount ^ (recordCount >>> 32));
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
		Host other = (Host) obj;
		if (hostId != other.hostId)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (recordCount != other.recordCount)
			return false;
		return true;
	}
}

package com.dns.mobile.data;

public class Host {

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
}

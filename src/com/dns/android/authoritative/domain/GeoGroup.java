package com.dns.android.authoritative.domain;

import java.io.Serializable;
import java.util.Date;

public class GeoGroup implements Serializable, GenericEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4309585396443686603L;

	Integer id ;

	String[] members ;

	Date date_created ;

	String name ;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String[] getMembers() {
		return members;
	}

	public void setMembers(String[] members) {
		this.members = members;
	}

	public Date getDate_created() {
		return date_created;
	}

	public void setDate_created(Date date_created) {
		this.date_created = date_created;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

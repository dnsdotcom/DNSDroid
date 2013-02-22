package com.dns.android.authoritative.domain;

import java.io.Serializable;
import java.util.Date;

public class Domain implements Serializable, GenericEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6169626002872712239L;

	Integer id ;

	String[] hosts ;

	String domainGroup ;

	Boolean has_ns ;

	Date date_created ;

	Date date_last_modified ;

	Boolean is_active ;

	String mode ;

	String name ;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String[] getHosts() {
		return hosts;
	}

	public void setHosts(String[] hosts) {
		this.hosts = hosts;
	}

	public String getDomainGroup() {
		return domainGroup;
	}

	public void setDomainGroup(String domainGroup) {
		this.domainGroup = domainGroup;
	}

	public Boolean getHas_ns() {
		return has_ns;
	}

	public void setHas_ns(Boolean has_ns) {
		this.has_ns = has_ns;
	}

	public Date getDate_created() {
		return date_created;
	}

	public void setDate_created(Date date_created) {
		this.date_created = date_created;
	}

	public Date getDate_last_modified() {
		return date_last_modified;
	}

	public void setDate_last_modified(Date date_last_modified) {
		this.date_last_modified = date_last_modified;
	}

	public Boolean getIs_active() {
		return is_active;
	}

	public void setIs_active(Boolean is_active) {
		this.is_active = is_active;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

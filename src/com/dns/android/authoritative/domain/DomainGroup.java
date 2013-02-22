package com.dns.android.authoritative.domain;

import java.io.Serializable;
import java.util.Date;

public class DomainGroup implements Serializable, GenericEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9113336038100874762L;

	Integer id ;

	String[] hosts ;

	Date date_created ;

	Date date_last_modified ;

	Boolean is_active ;

	String name ;

	Boolean allow_zone_token ;

	Boolean has_ns ;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getAllow_zone_token() {
		return allow_zone_token;
	}

	public void setAllow_zone_token(Boolean allow_zone_token) {
		this.allow_zone_token = allow_zone_token;
	}

	public Boolean getHas_ns() {
		return has_ns;
	}

	public void setHas_ns(Boolean has_ns) {
		this.has_ns = has_ns;
	}
}

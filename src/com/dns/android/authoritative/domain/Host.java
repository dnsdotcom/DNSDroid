package com.dns.android.authoritative.domain;

import java.io.Serializable;
import java.util.Date;

public class Host implements Serializable, GenericEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -10814381008309958L;

	Integer id ;

	Date date_created ;

	Date date_last_modified ;

	String name ;

	String domain ;

	String domainGroup ;

	Boolean is_active ;

	Boolean is_urlforward ;

	String[] rrs ;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDomainGroup() {
		return domainGroup;
	}

	public void setDomainGroup(String domainGroup) {
		this.domainGroup = domainGroup;
	}

	public Boolean getIs_active() {
		return is_active;
	}

	public void setIs_active(Boolean is_active) {
		this.is_active = is_active;
	}

	public Boolean getIs_urlforward() {
		return is_urlforward;
	}

	public void setIs_urlforward(Boolean is_urlforward) {
		this.is_urlforward = is_urlforward;
	}

	public String[] getRrs() {
		return rrs;
	}

	public void setRrs(String[] rrs) {
		this.rrs = rrs;
	}
}

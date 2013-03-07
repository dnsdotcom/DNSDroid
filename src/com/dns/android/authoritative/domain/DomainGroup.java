package com.dns.android.authoritative.domain;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "domain_groups")
public class DomainGroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9113336038100874762L;

	@DatabaseField(id = true)
	Integer id ;

	@DatabaseField(index = true)
	Date date_created ;

	@DatabaseField(index = true)
	Date date_last_modified ;

	@DatabaseField
	Boolean is_active ;

	@DatabaseField(index = true)
	String name ;

	@DatabaseField
	Boolean allow_zone_token ;

	@DatabaseField
	Boolean has_ns ;

	@ForeignCollectionField(eager = false, foreignFieldName = "domainGroup")
	ForeignCollection<Host> hosts ;

	@ForeignCollectionField(eager = false, foreignFieldName = "domainGroup")
	ForeignCollection<Domain> domains ;

	public DomainGroup() {
		super() ;
	}

	public void addHost(Host host) {
		hosts.add(host) ;
	}

	public ForeignCollection<Host> getHosts() {
		return hosts ;
	}

	public void addDomain(Domain domain) {
		domains.add(domain) ;
	}

	public ForeignCollection<Domain> getDomains() {
		return domains ;
	}

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

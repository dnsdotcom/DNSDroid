package com.dns.android.authoritative.domain;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "domains")
public class Domain implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6169626002872712239L;

	@DatabaseField(id = true)
	Integer id ;

	@DatabaseField(foreign = true, columnName = "domainGroup_id", canBeNull = true, index = true)
	DomainGroup domainGroup ;

	@DatabaseField
	Boolean has_ns ;

	@DatabaseField(index = true)
	Date date_created ;

	@DatabaseField(index = true)
	Date date_last_modified ;

	@DatabaseField
	Boolean is_active ;

	@DatabaseField
	String mode ;

	@DatabaseField(index = true)
	String name ;

	@ForeignCollectionField(foreignFieldName = "domain", eager=false, orderColumnName = "date_last_modified")
	ForeignCollection<Host> hosts ;

	public Domain() {
		super() ;
	}

	public void addHost(Host host) {
		hosts.add(host) ;
	}

	public ForeignCollection<Host> getHosts() {
		return hosts ;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public DomainGroup getDomainGroup() {
		return domainGroup;
	}

	public void setDomainGroup(DomainGroup domainGroup) {
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

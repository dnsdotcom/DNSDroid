package com.dns.android.authoritative.domain;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "hosts")
public class Host implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -10814381008309958L;

	@DatabaseField(id = true)
	private Integer id ;

	@DatabaseField(index = true)
	private Date date_created ;

	@DatabaseField(index = true)
	private Date date_last_modified ;

	@DatabaseField(index = true)
	private String name ;

	@DatabaseField(foreign = true, canBeNull = true, columnName = "domainGroup_id")
	private DomainGroup domainGroup ;

	@DatabaseField(index = true)
	private Boolean is_active ;

	@DatabaseField
	private Boolean is_urlforward ;

	@ForeignCollectionField(eager = false, columnName = "id", orderColumnName = "date_last_modified")
	private ForeignCollection<RR> rrs ;

	@DatabaseField(foreign = true, columnName = "domain_id", canBeNull = true)
	private Domain domain ;

	public Host() {
		super() ;
	}

	public void addRR(RR record) {
		rrs.add(record) ;
	}

	public ForeignCollection<RR> getRecords() {
		return rrs ;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public DomainGroup getDomainGroup() {
		return domainGroup;
	}

	public void setDomainGroup(DomainGroup domainGroup) {
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
}

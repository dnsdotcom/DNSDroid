package com.dns.android.authoritative.domain;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "geo_groups")
public class GeoGroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4309585396443686603L;

	@DatabaseField(id = true)
	Integer id ;

	@DatabaseField(index = true)
	Date date_created ;

	@DatabaseField(index = true)
	String name ;

	@ForeignCollectionField(eager = false, foreignFieldName = "geo_group", orderColumnName = "date_last_modified")
	ForeignCollection<GeoMatch> matches ;

	public GeoGroup() {
		super() ;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

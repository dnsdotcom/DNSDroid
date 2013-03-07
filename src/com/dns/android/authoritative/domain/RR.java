package com.dns.android.authoritative.domain;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "rrs")
public class RR implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4201607633946878916L;

	@DatabaseField(id = true)
	Integer id ;

	@DatabaseField(index = true)
	String answer ;

	@DatabaseField(index = true)
	String type ;

	@DatabaseField(index = true)
	Date date_created ;

	@DatabaseField(index = true)
	Date date_last_modified ;

	@DatabaseField
	Boolean is_wildcard ;

	@DatabaseField
	Integer expire ;

	@DatabaseField
	Integer minimum ;

	@DatabaseField
	Integer retry ;

	@DatabaseField
	Integer ttl ;

	@DatabaseField
	Integer priority ;

	@DatabaseField
	Integer weight ;

	@DatabaseField
	Integer port ;

	@DatabaseField
	String description ;

	@DatabaseField
	String keywords ;

	@DatabaseField
	String title ;

	@DatabaseField(foreign = true, canBeNull = true, columnName = "geoGroup_id")
	GeoGroup geoGroup ;

	@DatabaseField(foreign = true, canBeNull = true, columnName = "country_id")
	Country country ;

	@DatabaseField(foreign = true, canBeNull = true, columnName = "region_id")
	Region region ;

	@DatabaseField(foreign = true, canBeNull = true, columnName = "city_id")
	City city ;

	@DatabaseField(foreign = true, canBeNull = false)
	private Host parent ;

	public Host getParent() {
		return parent;
	}

	public void setParent(Host parent) {
		this.parent = parent;
	}

	public RR() {
		super() ;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getType() {
		return this.type ;
	}

	public void setType(String type) {
		this.type = type ;
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getIs_wildcard() {
		return is_wildcard;
	}

	public void setIs_wildcard(Boolean is_wildcard) {
		this.is_wildcard = is_wildcard;
	}

	public Integer getExpire() {
		return expire;
	}

	public void setExpire(Integer expire) {
		this.expire = expire;
	}

	public Integer getMinimum() {
		return minimum;
	}

	public void setMinimum(Integer minimum) {
		this.minimum = minimum;
	}

	public Integer getRetry() {
		return retry;
	}

	public void setRetry(Integer retry) {
		this.retry = retry;
	}

	public Integer getTtl() {
		return ttl;
	}

	public void setTtl(Integer ttl) {
		this.ttl = ttl;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public GeoGroup getGeoGroup() {
		return geoGroup;
	}

	public void setGeoGroup(GeoGroup geoGroup) {
		this.geoGroup = geoGroup;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}
}

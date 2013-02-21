package com.dns.android.authoritative.domain;

import java.io.Serializable;
import java.util.Date;

import org.xbill.DNS.Type;

public class RR implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4201607633946878916L;

	String[] hosts ;

	String answer ;

	Integer type ;

	Date date_created ;

	Date date_last_modified ;

	Integer id ;

	String geo_group ;

	String country ;

	String region ;

	String city ;

	Boolean is_wildcard ;

	Integer expire ;

	Integer minimum ;

	Integer retry ;

	Integer ttl ;

	Integer priority ;

	Integer weight ;

	Integer port ;

	String description ;

	String keywords ;

	String title ;

	public String[] getHosts() {
		return hosts;
	}

	public void setHosts(String[] hosts) {
		this.hosts = hosts;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getType() {
		return Type.string(this.type) ;
	}

	public void setType(String type) {
		this.type = org.xbill.DNS.Type.value(type) ;
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

	public String getGeo_group() {
		return geo_group;
	}

	public void setGeo_group(String geo_group) {
		this.geo_group = geo_group;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
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
}

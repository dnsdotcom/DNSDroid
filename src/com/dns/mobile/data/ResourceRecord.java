/**
 * 
 */
package com.dns.mobile.data;

import java.util.Date;

import android.util.Log;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class ResourceRecord {

	private String answer = new String("") ;

	private Integer cityId = null ;

	private Integer type = null ;

	private String countryId = null ;

	private Date dateCreated = new Date() ;

	private Date dateLastModified = new Date() ;
    
	private String geoGroup = null ;

	private boolean isActive = false ;

	private boolean isWildcard = false ;

	private Integer regionId = null ;

	private Long prioExpire = null ;

	private Integer weight = null ;

	private Long portRefresh = null ;

	private Long priority = null ;

	private Long minimum = null ;

	private Long retry = null ;

	private Long ttl = 3600L ;

	private Long xfr = null ;

	private Long hostId = null ;

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public void setType(String type) {
		if (type.contentEquals("SOA")) {
			this.type = 6 ;
		} else if (type.contentEquals("A")) {
			this.type = 1 ;
		} else if (type.contentEquals("TXT")) {
			this.type = 16 ;
		} else if (type.contentEquals("MX")) {
			this.type = 15 ;
		} else if (type.contentEquals("NS")) {
			this.type = 2 ;
		} else if (type.contentEquals("AAAA")) {
			this.type = 28 ;
		} else if (type.contentEquals("CNAME")) {
			this.type = 5 ;
		} else if (type.contentEquals("SRV")) {
			this.type = 33 ;
		} else if (type.contentEquals("302")) {
			this.type = 80000 ;
		} else if (type.contentEquals("301")) {
			this.type = 80001 ;
		} else if (type.contentEquals("FRAME")) {
			this.type = 80002 ;
		} else {
			Log.e("ResourceRecord", "Unknown record type '"+type+"'") ;
		}
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateLastModified() {
		return dateLastModified;
	}

	public void setDateLastModified(Date dateLastModified) {
		this.dateLastModified = dateLastModified;
	}

	public String getGeoGroup() {
		return geoGroup;
	}

	public void setGeoGroup(String geoGroup) {
		this.geoGroup = geoGroup;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isWildcard() {
		return isWildcard;
	}

	public void setWildcard(boolean isWildcard) {
		this.isWildcard = isWildcard;
	}

	public Integer getRegionId() {
		return regionId;
	}

	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
	}

	public Long getExpire() {
		return prioExpire;
	}

	public void setExpire(Long prioExpire) {
		this.prioExpire = prioExpire;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Long getRefresh() {
		return portRefresh;
	}

	public void setRefresh(Long portRefresh) {
		this.portRefresh = portRefresh;
	}

	public Long getPort() {
		return portRefresh;
	}

	public void setPort(Long portRefresh) {
		this.portRefresh = portRefresh;
	}

	public Long getPriority() {
		return priority;
	}

	public void setPriority(Long priority) {
		this.priority = priority;
		this.prioExpire = priority ;
	}

	public Long getMinimum() {
		return minimum;
	}

	public void setMinimum(Long minimum) {
		this.minimum = minimum;
	}

	public Long getRetry() {
		return retry;
	}

	public void setRetry(Long retry) {
		this.retry = retry;
	}

	public Long getTtl() {
		return ttl;
	}

	public void setTtl(Long ttl) {
		this.ttl = ttl;
	}

	public Long getXfr() {
		return xfr;
	}

	public void setXfr(Long xfr) {
		this.xfr = xfr;
	}

	public Long getHostId() {
		return hostId;
	}

	public void setHostId(Long hostId) {
		this.hostId = hostId;
	}

	public static String getTypeAsString(int recordType) {
		String retVal = null ;
		switch (recordType) {
			case 1:
				retVal = "A" ;
				break ;
			case 6:
				retVal = "SOA" ;
				break ;
			case 2:
				retVal = "NS" ;
				break ;
			case 5:
				retVal = "CNAME" ;
				break ;
			case 15:
				retVal = "MX" ;
				break ;
			case 16:
				retVal = "TXT" ;
				break ;
			case 28:
				retVal = "AAAA" ;
				break ;
			case 33:
				retVal = "SRV" ;
				break ;
			case 80000:
				retVal = "302" ;
				break ;
			case 80001:
				retVal = "301" ;
				break ;
			case 80002:
				retVal = "FRAME" ;
				break ;
		}
		return retVal ;
	}
}

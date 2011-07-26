/**
 * 
 */
package com.dns.mobile.data;

import java.io.Serializable;
import java.util.Date;

import android.util.Log;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class ResourceRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1983184253918263364L;

	private Long id = 0L ;

	private String answer = new String("") ;

	private Integer cityId = null ;

	private Integer type = null ;

	private String countryId = null ;

	private Date dateCreated = new Date() ;

	private Date dateLastModified = new Date() ;
    
	private String geoGroup = null ;

	private boolean isGroup = false ;

	private String hostName = null ;

	private String domainName = null ;

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

	/**
	 * Returns the RR ID
	 * @return
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Set the RR ID
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the currently set answer/alias/Responsible Party value for the RR
	 * @return
	 */
	public String getAnswer() {
		return answer;
	}

	/**
	 * Sets the answer/alias/Responsible Party value for the RR
	 * @return
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}

	/**
	 * Returns the City ID for this RR is it is set or NULL if not.
	 * @return
	 */
	public Integer getCityId() {
		return cityId;
	}

	/**
	 * Sets the city ID for this RR
	 * @param cityId
	 */
	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	/**
	 * Returns the current Type value (One of 1,2,5,6,15,16,28,33,80000,80001,80002) or NULL if not set.
	 * @return
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * Set the RR type value (One of 1,2,5,6,15,16,28,33,80000,80001,80002)
	 * @param type
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * Set the RR type value based on a string representation of the type.
	 * @param type
	 */
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

	public boolean isGroup() {
		return isGroup;
	}

	public void setGroup(boolean isGroup) {
		this.isGroup = isGroup;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((answer == null) ? 0 : answer.hashCode());
		result = prime * result + ((cityId == null) ? 0 : cityId.hashCode());
		result = prime * result
				+ ((countryId == null) ? 0 : countryId.hashCode());
		result = prime * result
				+ ((dateCreated == null) ? 0 : dateCreated.hashCode());
		result = prime
				* result
				+ ((dateLastModified == null) ? 0 : dateLastModified.hashCode());
		result = prime * result
				+ ((domainName == null) ? 0 : domainName.hashCode());
		result = prime * result
				+ ((geoGroup == null) ? 0 : geoGroup.hashCode());
		result = prime * result + ((hostId == null) ? 0 : hostId.hashCode());
		result = prime * result
				+ ((hostName == null) ? 0 : hostName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result + (isGroup ? 1231 : 1237);
		result = prime * result + (isWildcard ? 1231 : 1237);
		result = prime * result + ((minimum == null) ? 0 : minimum.hashCode());
		result = prime * result
				+ ((portRefresh == null) ? 0 : portRefresh.hashCode());
		result = prime * result
				+ ((prioExpire == null) ? 0 : prioExpire.hashCode());
		result = prime * result
				+ ((priority == null) ? 0 : priority.hashCode());
		result = prime * result
				+ ((regionId == null) ? 0 : regionId.hashCode());
		result = prime * result + ((retry == null) ? 0 : retry.hashCode());
		result = prime * result + ((ttl == null) ? 0 : ttl.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
		result = prime * result + ((xfr == null) ? 0 : xfr.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceRecord other = (ResourceRecord) obj;
		if (answer == null) {
			if (other.answer != null)
				return false;
		} else if (!answer.equals(other.answer))
			return false;
		if (cityId == null) {
			if (other.cityId != null)
				return false;
		} else if (!cityId.equals(other.cityId))
			return false;
		if (countryId == null) {
			if (other.countryId != null)
				return false;
		} else if (!countryId.equals(other.countryId))
			return false;
		if (dateCreated == null) {
			if (other.dateCreated != null)
				return false;
		} else if (!dateCreated.equals(other.dateCreated))
			return false;
		if (dateLastModified == null) {
			if (other.dateLastModified != null)
				return false;
		} else if (!dateLastModified.equals(other.dateLastModified))
			return false;
		if (domainName == null) {
			if (other.domainName != null)
				return false;
		} else if (!domainName.equals(other.domainName))
			return false;
		if (geoGroup == null) {
			if (other.geoGroup != null)
				return false;
		} else if (!geoGroup.equals(other.geoGroup))
			return false;
		if (hostId == null) {
			if (other.hostId != null)
				return false;
		} else if (!hostId.equals(other.hostId))
			return false;
		if (hostName == null) {
			if (other.hostName != null)
				return false;
		} else if (!hostName.equals(other.hostName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isActive != other.isActive)
			return false;
		if (isGroup != other.isGroup)
			return false;
		if (isWildcard != other.isWildcard)
			return false;
		if (minimum == null) {
			if (other.minimum != null)
				return false;
		} else if (!minimum.equals(other.minimum))
			return false;
		if (portRefresh == null) {
			if (other.portRefresh != null)
				return false;
		} else if (!portRefresh.equals(other.portRefresh))
			return false;
		if (prioExpire == null) {
			if (other.prioExpire != null)
				return false;
		} else if (!prioExpire.equals(other.prioExpire))
			return false;
		if (priority == null) {
			if (other.priority != null)
				return false;
		} else if (!priority.equals(other.priority))
			return false;
		if (regionId == null) {
			if (other.regionId != null)
				return false;
		} else if (!regionId.equals(other.regionId))
			return false;
		if (retry == null) {
			if (other.retry != null)
				return false;
		} else if (!retry.equals(other.retry))
			return false;
		if (ttl == null) {
			if (other.ttl != null)
				return false;
		} else if (!ttl.equals(other.ttl))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		if (xfr == null) {
			if (other.xfr != null)
				return false;
		} else if (!xfr.equals(other.xfr))
			return false;
		return true;
	}
}

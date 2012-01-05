/**
 * 
 */
package com.dns.mobile.data;

import java.io.Serializable;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class DomainGroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4105205478072674948L;

	private String name ;

	private long members ;

	private long groupId ;

	private String errorMessage ;

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getMembers() {
		return members;
	}

	public void setMembers(long members) {
		this.members = members;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (groupId ^ (groupId >>> 32));
		result = prime * result + (int) (members ^ (members >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		DomainGroup other = (DomainGroup) obj;
		if (groupId != other.groupId)
			return false;
		if (members != other.members)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name ;
	}
}

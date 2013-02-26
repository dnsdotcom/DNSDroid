/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.callbacks;

import com.dns.android.authoritative.domain.GenericEntity;

/**
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
public interface ParentedListView {

	/**
	 * Set the parent object for the ListView fragment
	 * @param parent An object which implements {@link GenericEntity} and should be the parent
	 */
	public void setParent(GenericEntity parent) ;

	/**
	 * Get the parent object for this ListView fragment
	 * @return A {@link GenericEntity}
	 */
	public GenericEntity getParent() ;
}

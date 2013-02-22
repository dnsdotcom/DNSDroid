/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.domain;

/**
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
public interface GenericEntityList<T> {

	public T[] getItems() ;

	public Meta getMeta() ;
}

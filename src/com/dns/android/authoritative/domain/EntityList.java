/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.domain;

/**
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
public class EntityList<T> implements GenericEntityList<T> {
	private T[] items ;
	private Meta meta ;

	/**
	 * @return the items
	 */
	public T[] getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(T[] items) {
		this.items = items;
	}

	/**
	 * @return the meta
	 */
	public Meta getMeta() {
		return meta;
	}

	/**
	 * @param meta the meta to set
	 */
	public void setMeta(Meta meta) {
		this.meta = meta;
	}
}

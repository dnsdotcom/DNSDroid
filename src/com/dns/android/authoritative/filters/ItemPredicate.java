/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.filters;

import android.annotation.SuppressLint;

import com.dns.android.authoritative.domain.GenericEntity;
import com.google.common.base.Predicate;

/**
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
public class ItemPredicate<T extends GenericEntity> implements Predicate<T> {

	private String filter = null ;

	public ItemPredicate(String filter) {
		this.filter = filter ;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public boolean apply(T arg0) {
		if (((T)arg0).getName().toLowerCase().contains(filter)) {
			return true ;
		}
		return false;
	}
	
}

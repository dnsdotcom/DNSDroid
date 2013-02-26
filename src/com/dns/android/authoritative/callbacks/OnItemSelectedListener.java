/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.callbacks;

import android.support.v4.app.Fragment;

import com.dns.android.authoritative.domain.GenericEntity;

/**
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
public interface OnItemSelectedListener {
	public void onItemSelected(GenericEntity parent, Class<? extends Fragment> type) ;
}

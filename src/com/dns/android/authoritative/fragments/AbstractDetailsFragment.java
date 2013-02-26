/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import com.dns.android.authoritative.callbacks.OnItemSelectedListener;
import com.dns.android.authoritative.domain.GenericEntity;

/**
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
public abstract class AbstractDetailsFragment extends SherlockFragment {

	protected Bundle savedInstanceState = null ;
	protected GenericEntity parent ;
	protected OnItemSelectedListener mListener ;
	protected String basePath = "" ;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnItemSelectedListener) activity ;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()+" must implement OnItemSelectedListener") ;
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View retVal = super.onCreateView(inflater, container, savedInstanceState);
		this.savedInstanceState = savedInstanceState ;
		return retVal ;
	}

	public abstract void onViewsCreated() ;

	public void setTarget(GenericEntity parent) {
		this.parent = parent ;
	}

	public GenericEntity getTarget() {
		return this.parent ;
	}
}

package com.dns.android.authoritative.fragments;

import com.actionbarsherlock.app.SherlockFragment;
import com.dns.android.authoritative.domain.RR;
import com.googlecode.androidannotations.annotations.EFragment;

/**
 * A fragment which allows for the viewing and editing of a resource record.
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
@EFragment
public class RRDetailFragment extends SherlockFragment {

	protected RR record ;

	public void setTargetRR(RR target) {
		this.record = target ;
	}
}

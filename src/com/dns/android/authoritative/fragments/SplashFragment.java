/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.fragments;

import android.content.Intent;
import android.net.Uri;

import com.actionbarsherlock.app.SherlockFragment;
import com.dns.android.authoritative.R;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;

/**
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
@EFragment(R.layout.splash)
public class SplashFragment extends SherlockFragment {

	@Click(R.id.dnsLogo)
	protected void handleLogoClick() {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.dns.com/")) ;
		getActivity().startActivity(intent) ;
	}
}

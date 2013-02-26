/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.fragments;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.springframework.web.client.RestClientException;

import com.actionbarsherlock.app.SherlockFragment;
import com.dns.android.authoritative.R;
import com.dns.android.authoritative.domain.Domain;
import com.dns.android.authoritative.rest.RestClient;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.mapsaurus.paneslayout.FragmentLauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * This {@link Fragment} displays details about the requested Domain and allows the user to edit those details.
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
@EFragment(R.layout.domain_details)
public class DomainDetailFragment extends SherlockFragment {

	@Bean
	protected RestClient client ;

	@ViewById(R.id.domainName)
	protected TextView domainName ;

	@ViewById(R.id.inDomainGroupToggle)
	protected ToggleButton inDomainGroup ;

	@ViewById(R.id.hasVanityNSCheckbox)
	protected ToggleButton hasVanityNS ;

	@ViewById(R.id.domainDateCreated)
	protected TextView dateCreated ;

	@ViewById(R.id.domainDateLastModified)
	protected TextView dateModified ;

	@ViewById(R.id.domainUpdateBusyIndicator)
	protected ProgressBar domainUpdateBusyIndicator ;

	protected Bundle savedInstanceState = null ;
	protected Domain parent ;
	protected String basePath = "/domains/" ;

	protected DateFormat df ;

	public void setDomain(Domain parent) {
		this.parent = parent ;
	}

	@AfterViews
	public void onViewsCreated() {
		df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault()) ;
		Domain parentDomain = (Domain) parent ;
		domainName.setText(parent.getName()) ;
		if (parentDomain.getDomainGroup()!=null) {
			inDomainGroup.setChecked(true) ;
		} else {
			inDomainGroup.setChecked(false) ;
		}
		hasVanityNS.setChecked(parentDomain.getHas_ns()) ;
		dateCreated.setText(df.format(parentDomain.getDate_created())) ;
		if (parentDomain.getDate_last_modified()!=null) {
			dateModified.setText(df.format(parentDomain.getDate_last_modified())) ;
		} else {
			dateModified.setText(df.format(parentDomain.getDate_created())) ;
		}
	}

	@Click(R.id.viewDomainHostsButton)
	protected void showHosts() {
		// create a new fragment
		HostListFragment_ f = new HostListFragment_();
		f.setParentDomain(parent) ;

		// get the activity and add the new fragment after this one!
		Activity a = getActivity();
		if (a != null && a instanceof FragmentLauncher)
			((FragmentLauncher) a).addFragment(this, f) ;
	}

	@Click(R.id.hasVanityNSCheckbox)
	protected void handleVanityNSToggle() {
		hasVanityNS.setEnabled(false) ;
		domainUpdateBusyIndicator.setVisibility(View.VISIBLE) ;
		updateDomain() ;
	}

	@Background
	protected void updateDomain() {
		Domain parentDomain = (Domain) parent ;
		Domain requested = new Domain() ;
		requested.setDate_created(parentDomain.getDate_created()) ;
		requested.setHas_ns(hasVanityNS.isChecked()) ;
		requested.setDate_last_modified(parentDomain.getDate_last_modified()) ;
		requested.setId(parentDomain.getId()) ;
		requested.setDomainGroup(parentDomain.getDomainGroup()) ;
		requested.setMode(parentDomain.getMode()) ;
		requested.setName(parentDomain.getName()) ;
		try {
			Domain result = client.postObject(Domain.class, requested, basePath+parent.getId()+"/") ;
			((Domain)parent).setHas_ns(result.getHas_ns()) ;
			handleDomainUpdateSuccess() ;
		} catch (RestClientException rce) {
			String errMsg = rce.getLocalizedMessage().split("Response body contained: ")[1] ;
			handleDomainUpdateFailure(getActivity().getResources().getString(R.string.vanityNsToggleErrorTitle), errMsg) ;
		} catch (Throwable t) {
			String errMsg = t.getLocalizedMessage() ;
			handleDomainUpdateFailure(getActivity().getResources().getString(R.string.unexpectedError), errMsg) ;
		}
	}

	@UiThread
	protected void handleDomainUpdateSuccess() {
		hasVanityNS.setChecked(((Domain)parent).getHas_ns()) ;
		domainUpdateBusyIndicator.setVisibility(View.GONE) ;
		hasVanityNS.setEnabled(true) ;
	}

	@UiThread
	protected void handleDomainUpdateFailure(String title, String message) {
		hasVanityNS.setChecked(((Domain)parent).getHas_ns()) ;
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()) ;
		builder.setTitle(title) ;
		builder.setMessage(message) ;
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss() ;
			}
		}) ;
		builder.show() ;
		domainUpdateBusyIndicator.setVisibility(View.GONE) ;
		hasVanityNS.setEnabled(true) ;
	}
}

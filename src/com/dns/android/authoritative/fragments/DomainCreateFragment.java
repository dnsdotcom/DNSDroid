/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.fragments;

import org.springframework.web.client.RestClientException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.mapsaurus.paneslayout.PanesActivity;

/**
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
@EFragment(R.layout.domain_create_fragment)
public class DomainCreateFragment extends SherlockFragment {

	protected final String TAG = "DomainCreateFragment" ;

	@Bean
	protected static RestClient client ;

	@ViewById(R.id.domainNameInput)
	protected EditText domainNameInput ;

	@ViewById(R.id.newDomainSaveButton)
	protected Button newDomainSaveButton ;

	@AfterViews
	protected void setupUi() {
		domainNameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				saveDomain() ;
				return false;
			}
		}) ;
	}

	@Click(R.id.newDomainSaveButton)
	protected void saveDomain() {
		domainNameInput.setEnabled(false) ;
		newDomainSaveButton.setEnabled(false) ;
		doNewDomainInBackground() ;
	}

	@Background
	protected void doNewDomainInBackground() {
		Domain newDomain = new Domain() ;
		newDomain.setName(domainNameInput.getText().toString()) ;
		newDomain.setDomainGroup(null) ;
		newDomain.setIs_active(Boolean.TRUE) ;
		newDomain.setMode("advanced") ;
		try {
			Domain result = client.postObject(Domain.class, newDomain, "/domains/") ;
			domainSuccessfullyCreated(result) ;
		} catch(RestClientException rce) {
			Log.e(TAG, rce.getLocalizedMessage(), rce) ;
			domainCreationFailed(newDomain, rce) ;
		}
	}

	@UiThread
	protected void domainCreationFailed(Domain domain, RestClientException rce) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()) ;
		String recordType = getActivity().getResources().getString(R.string.domain) ;
		String title = getActivity().getResources().getString(R.string.record_create_failed_title).replace("[[RECORDTYPE]]", recordType) ;
		builder.setTitle(title) ;
		StringBuilder message = new StringBuilder() ;
		message.append(getActivity().getResources().getString(R.string.record_create_failed_message_1).replace("[[RECORDTYPE]]", recordType).replace("[[RECORDNAME]]", domain.getName())) ;
		message.append("\n\n") ;
		message.append(getActivity().getResources().getString(R.string.record_create_failed_message_2)) ;
		builder.setMessage(message.toString()) ;
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss() ;
			}
		}) ;
		builder.show() ;
		domainNameInput.setEnabled(true) ;
		newDomainSaveButton.setEnabled(true) ;
	}

	@UiThread
	protected void domainSuccessfullyCreated(Domain result) {
		// get the activity and add the new fragment after this one!
		Activity a = getActivity();
		if (a != null && a instanceof FragmentLauncher) {
			Fragment menuFragment = ((PanesActivity) a).getMenuFragment() ;
			Fragment domainListFragment = new DomainListFragment_() ;
			((PanesActivity) a).addFragment(menuFragment, domainListFragment) ;
		}
	}
}

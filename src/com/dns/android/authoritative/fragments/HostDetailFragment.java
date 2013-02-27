/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.fragments;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.springframework.web.client.RestClientException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.actionbarsherlock.app.SherlockFragment;
import com.dns.android.authoritative.domain.Host;
import com.dns.android.authoritative.rest.RestClient;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.mapsaurus.paneslayout.FragmentLauncher;
import com.dns.android.authoritative.R;

/**
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
@EFragment(R.layout.host_details)
public class HostDetailFragment extends SherlockFragment {

	protected Bundle savedInstanceState = null ;
	protected Host parent ;
	protected String basePath = "" ;

	@Bean
	protected RestClient client ;

	@ViewById(R.id.hostName)
	protected EditText hostName ;

	@ViewById(R.id.isUrlForward)
	protected ToggleButton isUrlForward ;

	@ViewById(R.id.hostDateCreated)
	protected TextView hostDateCreated ;

	@ViewById(R.id.hostDateLastModified)
	protected TextView hostDateModified ;

	@ViewById(R.id.hostUpdateBusyIndicator)
	protected ProgressBar hostUpdateBusyIndicator ;

	public void setHost(Host host) {
		this.parent = host ;
	}

	@AfterViews
	public void onViewsCreated() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault()) ;
		Host host = (Host) parent ;
		if (host.getName().length()==0) {
			hostName.setHint("(root)") ;
		}
		hostName.setText(host.getName()) ;
		isUrlForward.setChecked(host.getIs_urlforward()) ;
		hostDateCreated.setText(df.format(host.getDate_created())) ;
		if (host.getDate_last_modified()==null) {
			hostDateModified.setText(df.format(host.getDate_created())) ;
		} else {
			hostDateModified.setText(df.format(host.getDate_last_modified())) ;
		}
	}

	@Click(R.id.viewHostRRListButton)
	protected void handleShowRRButton() {
		// create a new fragment
		RRListFragment_ f = new RRListFragment_();
		f.setParentHost(parent) ;

		// get the activity and add the new fragment after this one!
		Activity a = getActivity();
		if (a != null && a instanceof FragmentLauncher)
			((FragmentLauncher) a).addFragment(this, f) ;
	}

	@Click(R.id.isUrlForward)
	protected void handleToggleUrlForward() {
		isUrlForward.setEnabled(false) ;
		hostUpdateBusyIndicator.setVisibility(View.VISIBLE) ;
		sendHostUpdateRequest() ;
	}

	@Background
	protected void sendHostUpdateRequest() {
		Host host = (Host) parent ;
		Host request = new Host() ;
		request.setDate_created(host.getDate_created()) ;
		request.setDate_last_modified(host.getDate_last_modified()) ;
		request.setDomain(host.getDomain()) ;
		request.setDomainGroup(host.getDomainGroup()) ;
		request.setId(host.getId()) ;
		request.setIs_urlforward(isUrlForward.isChecked()) ;
		try {
			Host response = client.putObject(Host.class, request, "/hosts/"+host.getId()+"/") ;
			host.setIs_urlforward(response.getIs_urlforward()) ;
		} catch (RestClientException rce) {
			String errMsg = rce.getLocalizedMessage() ;
			handleHostUpdateFailed(getActivity().getResources().getString(R.string.vanityNsToggleErrorTitle), errMsg) ;
		} catch (Throwable t) {
			String errMsg = t.getLocalizedMessage() ;
			handleHostUpdateFailed(getActivity().getResources().getString(R.string.unexpectedError), errMsg) ;
		}
	}

	@UiThread
	protected void handleHostUpdateSuccess() {
		isUrlForward.setChecked(((Host)parent).getIs_urlforward()) ;
		hostUpdateBusyIndicator.setVisibility(View.GONE) ;
		isUrlForward.setEnabled(true) ;
	}

	@UiThread
	protected void handleHostUpdateFailed(String title, String message) {
		isUrlForward.setChecked(((Host)parent).getIs_urlforward()) ;
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
		hostUpdateBusyIndicator.setVisibility(View.GONE) ;
		isUrlForward.setEnabled(true) ;
	}

}

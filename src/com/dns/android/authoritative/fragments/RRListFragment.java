package com.dns.android.authoritative.fragments;

import java.util.ArrayList;

import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.dns.android.authoritative.R;
import com.dns.android.authoritative.domain.Host;
import com.dns.android.authoritative.rest.RestClient;
import com.dns.android.authoritative.utils.DNSPrefs_;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EFragment(R.layout.rr_list_fragment)
public class RRListFragment extends SherlockFragment {

	@ViewById(R.id.rrsFragmentLabel)
	protected TextView rrsFragmentLabel ;

	@ViewById(R.id.rrListView)
	protected ListView rrsListView ;

	@ViewById(R.id.rrListBusyIndicator)
	protected ProgressBar rrsLoadingIndicator ;

	@ViewById(R.id.rrFilter)
	protected EditText rrFilter ;

	@Pref
	protected DNSPrefs_ prefs ;

	@Bean
	protected static RestClient client ;

	protected final String TAG = "RRListFragment" ;
	protected static String rrFilterValue ;
	protected ArrayList<Host> rrList ;
	protected int totalCount = 0 ;
	protected int limit = 20 ;
	protected int offset = 0 ;

	protected Host parent ;

	public void setParentHost(Host host) {
		this.parent = host ;
	}

	@AfterViews
	protected void setupUi() {
		Log.d(TAG, "Fragment loaded and now setting up UI.") ;
		String fragmentLabel = getActivity().getResources().getString(R.string.rr_list_fragment_label)+" "+parent.getName() ;
		rrsFragmentLabel.setText(fragmentLabel) ;
	}
}

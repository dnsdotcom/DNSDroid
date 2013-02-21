package com.dns.android.authoritative.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import com.dns.android.authoritative.R;
import com.dns.android.authoritative.utils.DNSPrefs_;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EFragment(R.layout.geo_groups_fragment)
public class GeoGroupsFragment extends SherlockFragment {

	@Pref
	DNSPrefs_ prefs ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.geo_groups_fragment, container, false);
        return view;
    }
}

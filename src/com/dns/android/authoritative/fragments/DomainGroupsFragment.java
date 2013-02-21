package com.dns.android.authoritative.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.dns.android.authoritative.R;
import com.googlecode.androidannotations.annotations.EFragment;

@EFragment(R.layout.domain_groups_fragment)
public class DomainGroupsFragment extends SherlockFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.domain_groups_fragment, container, false);
        return view;
    }
}

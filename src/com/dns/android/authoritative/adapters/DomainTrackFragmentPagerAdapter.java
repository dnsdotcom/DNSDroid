/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.adapters;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragment;
import com.dns.android.authoritative.callbacks.SizedFragmentPage;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
public class DomainTrackFragmentPagerAdapter extends FragmentStatePagerAdapter {

	ArrayList<SherlockFragment> fragmentList ;

	/**
	 * @param fm
	 */
	public DomainTrackFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		fragmentList = new ArrayList<SherlockFragment>() ;
	}

	public void addFragment(SherlockFragment fragment) {
		fragmentList.add(fragment) ;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentStatePagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int index) {
		return fragmentList.get(index) ;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return fragmentList.size() ;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#getPageWidth(int)
	 */
	@Override
	public float getPageWidth(int index) {
		SizedFragmentPage page = (SizedFragmentPage) fragmentList.get(index) ;
		return page.getSize() ;
	}
}

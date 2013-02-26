/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.fragments;

import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.dns.android.authoritative.R;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ViewById;
import com.mapsaurus.paneslayout.FragmentLauncher;

/**
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
@EFragment
public class MenuFragment extends SherlockFragment {

	protected final String TAG = "MenuFragment" ;

	protected View parent ;

	@ViewById(R.id.menu_list_view)
	protected ListView menuListView ;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setRetainInstance(true) ;

		parent = inflater.inflate(R.layout.menu_fragment, container, false) ;
		return parent ;
	}

	protected class MenuListAdapter extends BaseAdapter {

		ArrayList<String> menuItems = null ;

		/**
		 * 
		 */
		public MenuListAdapter() {
			menuItems = new ArrayList<String>() ;
			CharSequence[] items = getActivity().getResources().getTextArray(R.array.locations) ;
			for (CharSequence item: items) {
				menuItems.add(item.toString()) ;
			}
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return menuItems.size() ;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return menuItems.get(position) ;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return menuItems.get(position).hashCode() ;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView!=null) {
				if (!(convertView instanceof TextView)) {
					convertView = new TextView(getActivity()) ;
				}
			} else {
				convertView = new TextView(getActivity()) ;
			}
			((TextView) convertView).setText(menuItems.get(position));
			((TextView) convertView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 24) ;
			((TextView) convertView).setTextColor(getActivity().getResources().getColor(android.R.color.white));
			convertView.setBackgroundColor(getActivity().getResources().getColor(R.color.dns_red)) ;
			convertView.setPadding(5, 5, 2, 5) ;
			return convertView;
		}
		
	}

	@AfterViews
	protected void setupUi() {
		menuListView.setAdapter(new MenuListAdapter()) ;
	}

	@ItemClick(R.id.menu_list_view)
	protected void handleMenuItemClick(Object item) {
		String selected = (String) item ;
		Log.d(TAG, "Got ItemClick for: "+selected) ;
		Fragment f = null ;
		if (selected.contentEquals("Domains")) {
			f = new DomainListFragment_() ;
		} else if (selected.contentEquals("Domain Groups")) {
			f = new DomainGroupListFragment_() ;
		} else if (selected.contentEquals("GeoGroups")) {
			f = new GeoGroupListFragment_() ;
		} else if (selected.contentEquals("Tools")) {
			f = new ToolsFragment_() ;
		}
		Activity a = getActivity() ;
		if (f != null && a != null && a instanceof FragmentLauncher) {
			((FragmentLauncher)a).addFragment(this, f) ;
		}
	}
}

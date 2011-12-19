/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dns.mobile;

import com.dns.mobile.R;
import com.dns.mobile.activities.ConfigurationActivity;
import com.dns.mobile.activities.domains.DomainListActivity;
import com.dns.mobile.activities.geo.GeoGroupsListActivity;
import com.dns.mobile.activities.groups.DomainGroupsListActivity;
import com.dns.mobile.activities.tools.DomainNameToolsActivity;
import com.dns.mobile.util.LogoOnClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This class provides a basic demonstration of how to write an Android
 * activity. Inside of its window, it places a single view: an EditText that
 * displays and edits some internal text.
 */
public class DNSActivity extends Activity {

	private static final String TAG = "DNSActivity" ;

	public DNSActivity() {
	}

	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("DNSActivity", "Creating DNSActivity") ;
		super.onCreate(savedInstanceState);

		// Inflate our UI from its XML layout description.
		setContentView(R.layout.dns_activity);
		ImageView dnsLogo = (ImageView) findViewById(R.id.dnsLogo) ;
		if (dnsLogo==null) {
			Log.e(TAG, "Unable to retrieve reference to dnsLogo") ;
		}
		dnsLogo.setOnClickListener(new LogoOnClickListener(this));

		ListView mainMenu = (ListView) findViewById(R.id.mainMenuListView) ;
		mainMenu.setAdapter(new MainMenuListAdapter()) ;

		AdapterView.OnItemClickListener menuListener = new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> menuListView, View selectedView, int position, long viewId) {
				final ConnectivityManager mgr = (ConnectivityManager) selectedView.getContext().getSystemService(CONNECTIVITY_SERVICE) ;
				final android.net.NetworkInfo wifi = mgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI) ;
				final android.net.NetworkInfo mobile = mgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) ;
				if (wifi.isConnectedOrConnecting() || mobile.isConnectedOrConnecting()) {
					Log.d(TAG,"User clicked '"+((TextView)selectedView).getText().toString()+"' with ID of '"+position+"'") ;

					@SuppressWarnings("rawtypes")
					Class targetActivity = null ;

					Intent i = new Intent("android.intent.action.VIEW") ;
					switch (position) {
						case 0:
							targetActivity = DomainListActivity.class ;
							break ;
						case 1:
							targetActivity = DomainGroupsListActivity.class ;
							break ;
						case 2:
							targetActivity = GeoGroupsListActivity.class ;
							break ;
						case 3:
							targetActivity = DomainNameToolsActivity.class ;
							break ;
					}
					i.setClass(getApplicationContext(), targetActivity) ;
					startActivity(i) ;
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(menuListView.getContext()) ;
					builder.setTitle("Network Error") ;
					builder.setMessage("Not currently connected to any networks. You must connect to the Internet " +
							"through WiFi or Cellular in order to use this application.")
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss() ;
							}
						}) ;
					builder.show() ;
				}
			}
			
		};
		mainMenu.setOnItemClickListener(menuListener) ;

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		if (settings.contains("auth.token")) {
			Log.d(TAG, "auth.token found in shared preferences.") ;
		} else {
			Log.d(TAG, "auth.token NOT found in shared preferences.") ;
			AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
			builder.setTitle("Configuration") ;
			builder.setMessage("It appears that you have not yet configured this application. Would you like to do so now?") ;
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					Intent configurationIntent = new Intent() ;
					configurationIntent.setClass(getBaseContext(), ConfigurationActivity.class) ;
					startActivity(configurationIntent) ;
				}
			}) ;
			builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					finish() ;
				}
			}) ;
			builder.show() ;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem settings = menu.add(Menu.NONE, 0, 0, "Settings");
		settings.setIcon(android.R.drawable.ic_menu_preferences) ;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				startActivity(new Intent(this, ConfigurationActivity.class));
				return true;
		}
		return false;
	}

	private class MainMenuListAdapter extends BaseAdapter {

		public int getCount() {
			return 4;
		}

		public Object getItem(int position) {
			switch (position) {
				case 0:
					return getResources().getString(R.string.main_menu_my_domains) ;
				case 1:
					return getResources().getString(R.string.main_menu_my_domain_groups) ;
				case 2:
					return getResources().getString(R.string.main_menu_my_geogroups) ;
				case 3:
					return getResources().getString(R.string.main_menu_dns_tools) ;
			}
			return null ;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView menuItem = new TextView(getBaseContext()) ;
			menuItem.setHeight(90) ;
			menuItem.setTextColor(Color.WHITE) ;
			menuItem.setGravity(Gravity.CENTER) ;
			menuItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25) ;
			switch (position) {
				case 0:
					menuItem.setText(R.string.main_menu_my_domains) ;
					break ;
				case 1:
					menuItem.setText(R.string.main_menu_my_domain_groups) ;
					break ;
				case 2:
					menuItem.setText(R.string.main_menu_my_geogroups) ;
					break;
				case 3:
					menuItem.setText(R.string.main_menu_dns_tools) ;
					break ;
				default:
					menuItem.setText("ERROR") ;
			}
			return menuItem ;
		}
	}

	/**
	 * Called when the activity is about to start interacting with the user.
	 */
	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this) ;
		if (!settings.contains("auth.token")) {
			Log.d(TAG, "auth.token NOT found in shared preferences.") ;
			AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
			builder.setTitle("Configuration") ;
			builder.setMessage("It appears that you have not yet configured this application. Would you like to do so now?") ;
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					Intent configurationIntent = new Intent() ;
					configurationIntent.setClass(getBaseContext(), ConfigurationActivity.class) ;
					startActivity(configurationIntent) ;
				}
			}) ;
			builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					finish() ;
				}
			}) ;
			builder.show() ;
		} else {
			Log.d(TAG, "auth.token found in shared preferences.") ;
		}
	}
}

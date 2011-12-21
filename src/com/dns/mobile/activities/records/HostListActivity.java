package com.dns.mobile.activities.records;

import java.util.ArrayList;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.Host ;
import com.dns.mobile.util.LogoOnClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HostListActivity extends Activity {

	private static final String TAG = "HostListActivity" ;
	protected ArrayList<Host> hostList = null ;
	protected String filter = new String("") ;
	protected String domainName = null ;
	protected boolean isDomainGroup = false ;

	private class HostListApiTask extends AsyncTask<String, Void, JSONObject> {


		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected JSONObject doInBackground(String... params) {
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String apiHost = null ;
			boolean useSSL = false ;
			if (settings.getBoolean("use.sandbox", true)) {
				apiHost = "sandbox.dns.com" ;
				useSSL = false ;
			} else {
				useSSL = true ;
				apiHost = "www.dns.com" ;
			}

			ManagementAPI api = new ManagementAPI(apiHost, useSSL, settings.getString("auth.token", "")) ;

			if (isDomainGroup) {
				return api.getHostnamesForGroup(params[0]) ;
			} else {
				return api.getHostnamesForDomain(params[0]) ;
			}
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			boolean apiRequestSucceeded = false ;
			findViewById(R.id.hostListProgressBar).setVisibility(View.GONE) ;
			if (result.has("meta")) {
				try {
					if (result.getJSONObject("meta").getInt("success")==1) {
						apiRequestSucceeded = true ;
					} else {
						Log.e(TAG, "API Error: "+result.getJSONObject("meta").getString("error")) ;
						AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext()) ;
						builder.setTitle(R.string.api_request_failed) ;
						builder.setMessage(result.getJSONObject("meta").getString("error")) ;
						builder.show() ;
					}
				} catch (JSONException jsone) {
					Log.e(TAG, "JSONException encountered while trying to parse domain list.", jsone) ;
					AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext()) ;
					builder.setTitle(R.string.api_request_failed) ;
					builder.setMessage(jsone.getLocalizedMessage()) ;
					builder.show() ;
				}
			}

			if (apiRequestSucceeded) {
				try {
					JSONArray data = result.getJSONArray("data") ;
					for (int x=0; x<data.length(); x++) {
						JSONObject currentData = data.getJSONObject(x) ;
						Host currentHost = new Host() ;
						String hostName = currentData.getString("name").contentEquals("")?"(root)":currentData.getString("name") ;
						currentHost.setName(hostName) ;
						Log.d(TAG, "Adding host '"+currentData.getString("name")+"' to hostList") ;
						currentHost.setHostId(currentData.getLong("id")) ;
						currentHost.setRecordCount(currentData.getLong("num_rr")) ;
						hostList.add(currentHost) ;
					}
					findViewById(R.id.hostListView).setVisibility(View.VISIBLE) ;
					((ListView)findViewById(R.id.hostListView)).invalidateViews() ;
					Log.d(TAG, "Finished parsing JSON response into domainList") ;
				} catch (JSONException jsone) {
					Log.e(TAG, "JSONException encountered while trying to parse domain list.", jsone) ;
				}
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext()) ;
				builder.setTitle(R.string.api_request_failed) ;
				builder.setMessage("Unknown error experienced while trying to make an API call to DNS.com.") ;
				builder.show() ;
			}
		}
	}

	private class HostDeleteApiTask extends AsyncTask<String, Void, JSONObject> {


		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected JSONObject doInBackground(String... params) {
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String apiHost = null ;
			boolean useSSL = false ;
			if (settings.getBoolean("use.sandbox", true)) {
				apiHost = "sandbox.dns.com" ;
				useSSL = false ;
			} else {
				useSSL = true ;
				apiHost = "www.dns.com" ;
			}

			ManagementAPI api = new ManagementAPI(apiHost, useSSL, settings.getString("auth.token", "")) ;
			JSONObject retVal = api.removeHostname(params[0], Boolean.valueOf(params[1]), params[2], Boolean.valueOf(params[3])) ;
			try {
				retVal.put("itemPosition", Integer.valueOf(params[4])) ;
			} catch (NumberFormatException e) {
				Log.e(TAG, "Unable to add item position to the JSONObject return value", e) ;
			} catch (JSONException e) {
				Log.e(TAG, "Unable to add item position to the JSONObject return value", e) ;
			}

			return retVal ;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			boolean apiRequestSucceeded = false ;
			findViewById(R.id.hostListProgressBar).setVisibility(View.GONE) ;
			if (result.has("meta")) {
				try {
					if (result.getJSONObject("meta").getInt("success")==1) {
						apiRequestSucceeded = true ;
					} else {
						Log.e(TAG, "API Error: "+result.getJSONObject("meta").getString("error")) ;
						AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext()) ;
						builder.setTitle(R.string.api_request_failed) ;
						builder.setMessage(result.getJSONObject("meta").getString("error")) ;
						builder.show() ;
					}
				} catch (JSONException jsone) {
					Log.e(TAG, "JSONException encountered while trying to parse delete result.", jsone) ;
					AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext()) ;
					builder.setTitle(R.string.api_request_failed) ;
					builder.setMessage(jsone.getLocalizedMessage()) ;
					builder.show() ;
				}
			}

			if (apiRequestSucceeded) {
				try {
					String itemPosition = result.getString("itemPosition") ;
					Host item = (Host) ((ListView)findViewById(R.id.hostListView)).getItemAtPosition(Integer.valueOf(itemPosition)) ;
					int itemIndex = hostList.indexOf(item) ;
					hostList.remove(itemIndex) ;
				} catch (JSONException jsone) {
					Log.e(TAG, "Error parsing delete command's return value", jsone) ;
				}
				((ListView)findViewById(R.id.hostListView)).invalidateViews() ;
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext()) ;
				builder.setTitle(R.string.api_request_failed) ;
				builder.setMessage("Unknown error experienced while trying to make an API call to DNS.com.") ;
				builder.show() ;
			}
		}
	}

	private class HostListBaseAdapter extends BaseAdapter {
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout listItemLayout = new LinearLayout(getBaseContext()) ;
			listItemLayout.setOrientation(LinearLayout.HORIZONTAL) ;
			listItemLayout.setGravity(Gravity.CENTER_VERTICAL&Gravity.LEFT) ;

			TextView hostItem = new TextView(parent.getContext()) ;
			hostItem.setTextColor(Color.WHITE) ;
			hostItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25) ;
			hostItem.setBackgroundColor(Color.TRANSPARENT) ;

			@SuppressWarnings("unchecked")
			ArrayList<Host> filteredList = (ArrayList<Host>) CollectionUtils.select(hostList, new org.apache.commons.collections.Predicate() {
				
				public boolean evaluate(Object object) {
					Host current = (Host) object ;
					if (current.getName().toLowerCase().contains(filter.toLowerCase())) {
						return true ;
					} else {
						return false;
					}
				}
			}) ;

			if (position==0) {
				hostItem.setText("[New Host]") ;
			} else {
				TextView rrCount = new TextView(getBaseContext()) ;
				rrCount.setBackgroundDrawable(getResources().getDrawable(R.drawable.count_background)) ;
				rrCount.setTextColor(Color.WHITE) ;
				rrCount.setText(filteredList.get(position-1).getRecordCount()+"") ;
				rrCount.setGravity(Gravity.CENTER) ;
				listItemLayout.addView(rrCount) ;

				Host currentHost = filteredList.get(position-1);
				hostItem.setText(currentHost.getName());
			}
			listItemLayout.addView(hostItem) ;
			return listItemLayout ;
		}
		
		public long getItemId(int position) {
			return position+200 ;
		}
		
		public Object getItem(int position) {

			if (position>0) {
				@SuppressWarnings("unchecked")
				ArrayList<Host> filteredList = (ArrayList<Host>) CollectionUtils
						.select(hostList,
								new org.apache.commons.collections.Predicate() {

									public boolean evaluate(Object object) {
										Host current = (Host) object;
										if (current
												.getName()
												.toLowerCase()
												.contains(
														filter.toLowerCase())) {
											return true;
										} else {
											return false;
										}
									}
								});
				return filteredList.get(position - 1);
			} else {
				return null ;
			}
		}
		
		public int getCount() {

			@SuppressWarnings("unchecked")
			ArrayList<Host> filteredList = (ArrayList<Host>) CollectionUtils.select(hostList, new org.apache.commons.collections.Predicate() {
				
				public boolean evaluate(Object object) {
					Host current = (Host) object ;
					if (current.getName().toLowerCase().contains(filter.toLowerCase())) {
						return true ;
					} else {
						return false;
					}
				}
			}) ;
			return filteredList.size()+1 ;
		}		
	}

	private class ItemPressListener implements AdapterView.OnItemClickListener {
		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		public void onItemClick(AdapterView<?> hostListView, View hostItemView, int position, long itemId) {
			Host selectedHost = (Host) hostListView.getAdapter().getItem(position) ;
			Intent rrListActivity = null ;
			if (position==0) {
				rrListActivity = new Intent(getApplicationContext(), CreateNewHostActivity.class) ;
			} else {
				rrListActivity = new Intent(getApplicationContext(), HostRecordListActivity.class) ;
				rrListActivity.putExtra("hostName", selectedHost.getName().contentEquals("(root)")?"":selectedHost.getName()) ;
			}
			rrListActivity.putExtra("domainName", domainName) ;
			rrListActivity.putExtra("isDomainGroup", isDomainGroup) ;
			startActivity(rrListActivity) ;
		}		
	}

	private class ItemLongPressListener implements AdapterView.OnItemLongClickListener {
		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		public boolean onItemLongClick(AdapterView<?> hostListView, View hostItemView, int position, long itemId) {
			final Host deleteHost = (Host) hostListView.getAdapter().getItem(position) ;
			AlertDialog.Builder builder = new AlertDialog.Builder(HostListActivity.this) ;
			String title1 = HostListActivity.this.getResources().getString(R.string.host_delete_dialog_title) ;
			String dialogTitle = title1 + " '" + deleteHost.getName() + "'?" ;
			builder.setTitle(dialogTitle) ;
			builder.setMessage(R.string.host_delete_dialog_message) ;
			final int itemPosition = position ;
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss() ;
					findViewById(R.id.hostListProgressBar).setVisibility(View.VISIBLE) ;
					new HostDeleteApiTask().execute(domainName, Boolean.toString(isDomainGroup), deleteHost.getName(), "true", itemPosition+"") ;
				}
			}) ;
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss() ;
				}
			}) ;
			builder.show() ;
			return true ;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hostList = new ArrayList<Host>() ;
		setContentView(R.layout.domain_hosts_activity) ;
		domainName = this.getIntent().getExtras().getString("domainName") ;
		isDomainGroup = this.getIntent().getExtras().getBoolean("isDomainGroup") ;
		((TextView)findViewById(R.id.hostHeaderLabel)).setText(domainName) ;
		findViewById(R.id.dnsLogo).setOnClickListener(new LogoOnClickListener(this));

		ListView hostListView = (ListView) findViewById(R.id.hostListView) ;

		hostListView.setOnItemClickListener(new ItemPressListener()) ;

		hostListView.setOnItemLongClickListener(new ItemLongPressListener()) ;

		hostListView.setAdapter(new HostListBaseAdapter()) ;

		findViewById(R.id.hostListView).setVisibility(View.GONE) ;
		findViewById(R.id.hostListProgressBar).setVisibility(View.VISIBLE) ;
		new HostListApiTask().execute(domainName) ;

		// Catch inputs on the filter input and update the filter value. Then invalidate the ListView in 
		// order to have it update the list of displayed hosts.
		EditText filterInput = (EditText) findViewById(R.id.filterInput) ;
		filterInput.setOnKeyListener(new View.OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				EditText filterInput = (EditText) v ;
				filter = filterInput.getText().toString() ;
				((ListView)findViewById(R.id.hostListView)).invalidateViews() ;
				return false;
			}
		}) ;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
/*	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		domainName = intent.getExtras().getString("domainName") ;
		isDomainGroup = intent.getExtras().getBoolean("isDomainGroup") ;
		((TextView)findViewById(R.id.hostHeaderLabel)).setText(domainName) ;
		findViewById(R.id.dnsLogo).setOnClickListener(new LogoOnClickListener(this));

		ListView hostListView = (ListView) findViewById(R.id.hostListView) ;

		hostListView.setOnItemClickListener(new ItemPressListener()) ;

		hostListView.setOnItemLongClickListener(new ItemLongPressListener()) ;

		hostListView.setAdapter(new HostListBaseAdapter()) ;

		findViewById(R.id.hostListView).setVisibility(View.GONE) ;
		findViewById(R.id.hostListProgressBar).setVisibility(View.VISIBLE) ;
		new HostListApiTask().execute(domainName) ;

		// Catch inputs on the filter input and update the filter value. Then invalidate the ListView in 
		// order to have it update the list of displayed hosts.
		EditText filterInput = (EditText) findViewById(R.id.filterInput) ;
		filterInput.setOnKeyListener(new View.OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				EditText filterInput = (EditText) v ;
				filter = filterInput.getText().toString() ;
				((ListView)findViewById(R.id.hostListView)).invalidateViews() ;
				return false;
			}
		}) ;

	}*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!isDomainGroup) {
			MenuItem setXfr = menu.add(Menu.NONE, 1, 1, "Zone Transfer");
			setXfr.setIcon(android.R.drawable.ic_menu_upload) ;
			MenuItem addToGroup = menu.add(Menu.NONE, 2, 2, "Add To Group");
			addToGroup.setIcon(android.R.drawable.ic_menu_add);
			MenuItem disableDomain = menu.add(Menu.NONE, 3, 3, "Delete Domain");
			disableDomain.setIcon(android.R.drawable.ic_menu_delete);
		}
		MenuItem refreshHosts = menu.add(Menu.NONE, 0, 0, "Refresh");
		refreshHosts.setIcon(R.drawable.ic_menu_refresh) ;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 2:
				// TODO: Move to domain group logic
				return true ;
			case 1:
				// TODO: Zone Transfer Logic
				return true ;
			case 0:
				findViewById(R.id.hostListView).setVisibility(View.GONE) ;
				findViewById(R.id.hostListProgressBar).setVisibility(View.VISIBLE) ;
				hostList.clear() ;
				new HostListApiTask().execute(domainName) ;
				return true ;
		}
		return false;
	}
}

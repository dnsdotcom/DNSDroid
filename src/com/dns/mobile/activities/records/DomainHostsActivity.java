package com.dns.mobile.activities.records;

import java.util.ArrayList;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.Host ;
import com.dns.mobile.data.ResourceRecord;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
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

public class DomainHostsActivity extends Activity {

	private static final String TAG = "DomainHostsActivity" ;
	protected ArrayList<Host> hostList = null ;
	protected String filter = new String("") ;
	protected String domainName = null ;
	protected boolean isDomainGroup = false ;
	protected ListView hostListView = null ;

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

			return api.removeHostname(params[0], Boolean.valueOf(params[1]), params[2], Boolean.valueOf(params[3])) ;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			DomainHostsActivity.this.hostListView.invalidateViews() ;
		}
	}

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
						Log.e("DomainHostsActivity", "API Error: "+result.getJSONObject("meta").getString("error")) ;
						AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext()) ;
						builder.setTitle(R.string.api_request_failed) ;
						builder.setMessage(result.getJSONObject("meta").getString("error")) ;
						builder.show() ;
					}
				} catch (JSONException jsone) {
					Log.e("DomainHostsActivity", "JSONException encountered while trying to parse domain list.", jsone) ;
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
						Log.d("DomainHostsActivity", "Adding host '"+currentData.getString("name")+"' to hostList") ;
						currentHost.setHostId(currentData.getLong("id")) ;
						currentHost.setRecordCount(currentData.getLong("num_rr")) ;
						hostList.add(currentHost) ;
					}
					findViewById(R.id.hostListView).setVisibility(View.VISIBLE) ;
					((ListView)findViewById(R.id.hostListView)).invalidateViews() ;
					Log.d("DomainHostsActivity", "Finished parsing JSON response into domainList") ;
				} catch (JSONException jsone) {
					Log.e("DomainHostsActivity", "JSONException encountered while trying to parse domain list.", jsone) ;
				}
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext()) ;
				builder.setTitle(R.string.api_request_failed) ;
				builder.setMessage("Unknown error experienced while trying to make an API call to DNS.com.") ;
				builder.show() ;
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "Entered onCreate method.") ;
		super.onCreate(savedInstanceState);
		hostList = new ArrayList<Host>() ;
		setContentView(R.layout.domain_hosts_activity) ;
		domainName = this.getIntent().getExtras().getString("domainName") ;
		isDomainGroup = this.getIntent().getExtras().getBoolean("isDomainGroup") ;
		((TextView)findViewById(R.id.hostHeaderLabel)).setText(domainName) ;
		findViewById(R.id.dnsLogo).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext()) ;
				builder.setTitle(R.string.open_web_confirmation_title) ;
				builder.setTitle(R.string.open_web_confirmation_msg) ;
				builder.setPositiveButton(R.string.open_web_confirmation_yes, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						Uri uri = Uri.parse("http://www.dns.com/") ;
						startActivity(new Intent(Intent.ACTION_VIEW, uri)) ;
					}
				}) ;
				builder.setNegativeButton(R.string.open_web_confirmation_no, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
					}
				}) ;
				builder.show() ;
			}
		});

		hostListView = (ListView) findViewById(R.id.hostListView) ;

		hostListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
					rrListActivity.putExtra("hostName", selectedHost.getName()) ;
				}
				rrListActivity.putExtra("domainName", domainName) ;
				rrListActivity.putExtra("isDomainGroup", isDomainGroup) ;
				startActivity(rrListActivity) ;
			}
		}) ;

		hostListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			/* (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
			 */
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.d(TAG, "Item long pressed.") ;
				AlertDialog.Builder builder = new AlertDialog.Builder(DomainHostsActivity.this) ;
				final ResourceRecord deleteTarget = (ResourceRecord) arg0.getAdapter().getItem(arg2) ;
				String hostName = deleteTarget.getHostName() ;
				Log.d(TAG, "Hostname '"+hostName+"' long pressed.") ;
				builder.setTitle(getResources().getString(R.string.host_delete_dialog_title).replaceAll("||RECORDNAME||", hostName)) ;
				builder.setMessage(getResources().getString(R.string.host_delete_dialog_message)) ;
				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						new HostDeleteApiTask().execute(deleteTarget.getDomainName(), deleteTarget.isGroup()?"true":"false", deleteTarget.getHostName(), "true") ;
						dialog.dismiss() ;
					}
				}) ;

				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
					}
				}) ;
				builder.show() ;
				return false;
			}
		}) ;

		Log.d(TAG, "Setting ListView adapter.") ;
		hostListView.setAdapter(new BaseAdapter() {
			
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
				return filteredList.get(position-1) ;
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
		}) ;

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
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		domainName = intent.getExtras().getString("domainName") ;
		isDomainGroup = intent.getExtras().getBoolean("isDomainGroup") ;
		((TextView)findViewById(R.id.hostHeaderLabel)).setText(domainName) ;
		findViewById(R.id.dnsLogo).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext()) ;
				builder.setTitle(R.string.open_web_confirmation_title) ;
				builder.setTitle(R.string.open_web_confirmation_msg) ;
				builder.setPositiveButton(R.string.open_web_confirmation_yes, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						Uri uri = Uri.parse("http://www.dns.com/") ;
						startActivity(new Intent(Intent.ACTION_VIEW, uri)) ;
					}
				}) ;
				builder.setNegativeButton(R.string.open_web_confirmation_no, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
					}
				}) ;
				builder.show() ;
			}
		});

		hostListView = (ListView) findViewById(R.id.hostListView) ;

		hostListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
					rrListActivity.putExtra("hostName", selectedHost.getName()) ;
				}
				rrListActivity.putExtra("domainName", domainName) ;
				rrListActivity.putExtra("isDomainGroup", isDomainGroup) ;
				startActivity(rrListActivity) ;
			}
		}) ;

		hostListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			/* (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
			 */
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.d(TAG, "Item long pressed.") ;
				AlertDialog.Builder builder = new AlertDialog.Builder(DomainHostsActivity.this) ;
				final ResourceRecord deleteTarget = (ResourceRecord) arg0.getAdapter().getItem(arg2) ;
				String hostName = deleteTarget.getHostName() ;
				Log.d(TAG, "Hostname '"+hostName+"' long pressed.") ;
				builder.setTitle(getResources().getString(R.string.host_delete_dialog_title).replaceAll("||RECORDNAME||", hostName)) ;
				builder.setMessage(getResources().getString(R.string.host_delete_dialog_message)) ;
				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						new HostDeleteApiTask().execute(deleteTarget.getDomainName(), deleteTarget.isGroup()?"true":"false", deleteTarget.getHostName(), "true") ;
						dialog.dismiss() ;
					}
				}) ;

				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
					}
				}) ;
				builder.show() ;
				return false;
			}
		}) ;

		hostListView.setAdapter(new BaseAdapter() {
			
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
				return filteredList.get(position-1) ;
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
		}) ;

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem addToGroup = menu.add(Menu.NONE, 0, 0, "Add To Group");
		addToGroup.setIcon(android.R.drawable.ic_menu_add) ;
		MenuItem disableDomain = menu.add(Menu.NONE, 1, 1, "Delete Domain");
		disableDomain.setIcon(android.R.drawable.ic_menu_delete) ;
		MenuItem refreshHosts = menu.add(Menu.NONE, 2, 2, "Refresh");
		refreshHosts.setIcon(R.drawable.ic_menu_refresh) ;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				// TODO: Move to domain group logic
				return true ;
			case 1:
				// TODO: Domain delete logic.
				return true ;
			case 2:
				findViewById(R.id.hostListView).setVisibility(View.GONE) ;
				findViewById(R.id.hostListProgressBar).setVisibility(View.VISIBLE) ;
				hostList.clear() ;
				new HostListApiTask().execute(domainName) ;
				return true ;
		}
		return false;
	}
}

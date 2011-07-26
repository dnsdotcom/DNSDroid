package com.dns.mobile.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.Host ;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DomainHostsActivity extends Activity {

	protected ArrayList<Host> hostList = null ;

	private class HostListApiTask extends AsyncTask<String, Void, JSONObject> {


		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected JSONObject doInBackground(String... params) {
			findViewById(R.id.hostListView).setVisibility(View.GONE) ;
			findViewById(R.id.hostListProgressBar).setVisibility(View.VISIBLE) ;
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

			return api.getHostnamesForDomain(params[0]) ;
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
						AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext()) ;
						builder.setTitle(R.string.api_request_failed) ;
						builder.setMessage(result.getJSONObject("meta").getString("error")) ;
					}
				} catch (JSONException jsone) {
					Log.e("DomainHostsActivity", "JSONException encountered while trying to parse domain list.", jsone) ;
					AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext()) ;
					builder.setTitle(R.string.api_request_failed) ;
					builder.setMessage(jsone.getLocalizedMessage()) ;
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
				showDialog(R.string.api_request_failed) ;
			}
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
		final String domainName = this.getIntent().getExtras().getString("domainName") ;
		final boolean isDomainGroup = this.getIntent().getExtras().getBoolean("isDomainGroup") ;
		((TextView)findViewById(R.id.hostHeaderLabel)).setText(domainName) ;

		ListView hostListView = (ListView) findViewById(R.id.hostListView) ;

		hostListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			/* (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
			 */
			public void onItemClick(AdapterView<?> hostListView, View hostItemView, int position, long itemId) {
				Intent rrListActivity = new Intent(getApplicationContext(), HostRecordListActivity.class) ;
				rrListActivity.putExtra("domainName", domainName) ;
				rrListActivity.putExtra("hostName", hostList.get(position-1).getName()) ;
				rrListActivity.putExtra("isDomainGroup", isDomainGroup) ;
				startActivity(rrListActivity) ;
			}
		}) ;

		hostListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			/* (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
			 */
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				return false;
			}
		}) ;

		hostListView.setAdapter(new BaseAdapter() {
			
			public View getView(int position, View convertView, ViewGroup parent) {
				LinearLayout listItemLayout = new LinearLayout(getBaseContext()) ;
				listItemLayout.setOrientation(LinearLayout.HORIZONTAL) ;

				TextView hostItem = new TextView(parent.getContext()) ;
				hostItem.setTextColor(Color.WHITE) ;
				hostItem.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10) ;
				hostItem.setBackgroundColor(Color.TRANSPARENT) ;
				if (position==0) {
					hostItem.setText("[New Host]") ;
				} else {
					TextView rrCount = new TextView(getBaseContext()) ;
					rrCount.setBackgroundDrawable(getResources().getDrawable(R.drawable.count_background)) ;
					rrCount.setTextColor(Color.WHITE) ;
					rrCount.setText(hostList.get(position-1).getRecordCount()+"") ;
					rrCount.setGravity(Gravity.CENTER) ;
					listItemLayout.addView(rrCount) ;

					Host currentHost = hostList.get(position-1);
					hostItem.setText(currentHost.getName());
				}
				listItemLayout.addView(hostItem) ;
				return listItemLayout ;
			}
			
			public long getItemId(int position) {
				return position+200 ;
			}
			
			public Object getItem(int position) {
				return hostList.get(position-1) ;
			}
			
			public int getCount() {
				return hostList.size()+1 ;
			}
		}) ;

		new HostListApiTask().execute(domainName) ;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem addToGroup = menu.add(Menu.NONE, 0, 0, "Add To Group");
		addToGroup.setIcon(android.R.drawable.ic_menu_add) ;
		MenuItem disableDomain = menu.add(Menu.NONE, 1, 1, "Delete Domain");
		disableDomain.setIcon(android.R.drawable.ic_input_delete) ;
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
		}
		return false;
	}
}

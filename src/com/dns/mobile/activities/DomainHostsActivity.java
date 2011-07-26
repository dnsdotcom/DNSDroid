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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
			findViewById(R.id.hostListView).setVisibility(View.INVISIBLE) ;
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
			findViewById(R.id.hostListProgressBar).setVisibility(View.INVISIBLE) ;
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
						Log.d("DomainHostsActivity", "Adding domain '"+currentData.getString("name")+"' to domainList") ;
						currentHost.setHostId(currentData.getLong("id")) ;
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
		String domainName = this.getIntent().getExtras().getString("domainName") ;
		((TextView)findViewById(R.id.hostHeaderLabel)).setText(domainName) ;

		ListView hostListView = (ListView) findViewById(R.id.hostListView) ;

		hostListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			/* (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
			 */
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
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
				TextView hostItem = new TextView(parent.getContext()) ;
				hostItem.setTextColor(Color.WHITE) ;
				hostItem.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10) ;
				hostItem.setBackgroundColor(Color.TRANSPARENT) ;
				hostItem.setWidth(LayoutParams.FILL_PARENT) ;
				if (position==0) {
					hostItem.setText("[New Host]") ;
				} else {
					Host currentHost = hostList.get(position-1);
					hostItem.setText(currentHost.getName());
				}
				return hostItem ;
			}
			
			public long getItemId(int position) {
				return position+100 ;
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
}

package com.dns.mobile.activities.groups;

import java.util.ArrayList;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dns.api.compiletime.ManagementAPI;
import com.dns.mobile.R;
import com.dns.mobile.data.Domain;
import com.dns.mobile.util.LogoOnClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * An <code>Activity</code> which shows a list of domains for the given user's API token
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class GroupedDomainSelectActivity extends Activity {

	private static final String TAG = "GroupedDomainSelectActivity" ;
	protected ArrayList<Domain> domainList = null ;
	protected String filter = new String("") ;
	protected String domainGroup = null ;
	protected ProgressDialog busyDialog = null ;

	private class DomainListApiTask extends AsyncTask<Void, Void, JSONObject> {
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected JSONObject doInBackground(Void... params) {
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

			return api.getDomains("") ;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			if (result!=null) {
				boolean apiRequestSucceeded = false;
				findViewById(R.id.viewRefreshButton).setVisibility(View.VISIBLE) ;
				findViewById(R.id.viewRefreshProgressBar).setVisibility(View.GONE) ;
				if (result.has("meta")) {
					try {
						if (result.getJSONObject("meta").getInt("success") == 1) {
							apiRequestSucceeded = true;
						} else {
							Log.e(TAG, "API Error: " + result.getJSONObject("meta").getString("error"));
							AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
							builder.setTitle(R.string.api_request_failed);
							builder.setMessage(result.getJSONObject("meta").getString("error"));
						}
					} catch (JSONException jsone) {
						Log.e(TAG, "JSONException encountered while trying to parse domain list.", jsone);
						AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
						builder.setTitle(R.string.api_request_failed);
						builder.setMessage(jsone.getLocalizedMessage());
					}
				}
				if (apiRequestSucceeded) {
					try {
						JSONArray data = result.getJSONArray("data");
						for (int x = 0; x < data.length(); x++) {
							JSONObject currentData = data.getJSONObject(x);
							Log.d(TAG, "JSON: " + currentData.toString());
							Domain currentDomain = new Domain();
							currentDomain.setName(currentData.getString("name"));
							Log.d(TAG, "Adding domain '" + currentData.getString("name") + "' to domainList");
							currentDomain.setDomainId(currentData.getLong("id"));
							currentDomain.setGroupedDomain(currentData.getString("mode").contentEquals("group"));
							if (currentDomain.isGroupedDomain()) {
								currentDomain.setDomainGroup(currentData.getString("group"));
							}
							domainList.add(currentDomain);
						}
						findViewById(R.id.domainListView).setVisibility(View.VISIBLE);
						((ListView) findViewById(R.id.domainListView)).invalidateViews();
						Log.d(TAG, "Finished parsing JSON response into domainList");
					} catch (JSONException jsone) {
						Log.e(TAG, "JSONException encountered while trying to parse domain list.", jsone);
					}
				} else {
					showDialog(R.string.api_request_failed);
				}
			} else {
				showDialog(R.string.api_request_failed) ;
			}
		}
	}

	private class AddDomainToGroupTask extends AsyncTask<Domain, Void, Domain> {
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Domain doInBackground(Domain... params) {

			Domain selected = params[0] ;
			selected.setGroupedDomain(true) ;
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
			Log.d(TAG, "Sending API request to add '"+selected.getName()+"' to domain group '"+domainGroup+"'") ;
			JSONObject result = api.assignDomainMode(selected.getName(), "group", domainGroup) ;

			try {
				if (result.has("meta")) {
					if (result.getJSONObject("meta").getInt("success")==1) {
						selected.setGroupedDomain(false) ;
					}
				}
			} catch (JSONException jsone) {
				Log.e(TAG, "JSONException encountered parsing domain group removal result", jsone) ;
			}

			return selected ;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Domain result) {
			super.onPostExecute(result);
			busyDialog.dismiss() ;
			if (result.isGroupedDomain()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(GroupedDomainSelectActivity.this) ;
				String message = "'"+result.getName()+"' "+getResources().getString(R.string.add_group_member_fail_message)+" '"+domainGroup+"'" ;
				builder.setTitle(R.string.add_group_member_fail_title) ;
				builder.setMessage(message) ;
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
					}
				}) ;
				builder.show() ;
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(GroupedDomainSelectActivity.this) ;
				String message = "'"+result.getName()+"' "+getResources().getString(R.string.add_group_member_success_message)+" '"+domainGroup+"'" ;
				builder.setTitle(R.string.add_group_member_success_title) ;
				builder.setMessage(message) ;
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
					}
				}) ;
				builder.show() ;
			}
			findViewById(R.id.viewRefreshButton).setVisibility(View.VISIBLE) ;
			findViewById(R.id.viewRefreshProgressBar).setVisibility(View.GONE) ;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.domain_list_activity) ;
		domainList = new ArrayList<Domain>() ;
		findViewById(R.id.dnsLogo).setOnClickListener(new LogoOnClickListener(this));
		domainGroup = this.getIntent().getStringExtra("domainGroup") ;

		ListView domainListView = (ListView) findViewById(R.id.domainListView) ;
		domainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> domainListView, View selectedView, int position, long itemId) {
				busyDialog = new ProgressDialog(GroupedDomainSelectActivity.this) ;
				Domain selected = (Domain) domainListView.getAdapter().getItem(position) ;
				busyDialog.setTitle(R.string.domain_group_add_busy_title) ;
				busyDialog.setIndeterminate(true) ;
				busyDialog.show() ;
				new AddDomainToGroupTask().execute(selected) ;
			}
			
		}) ;

		domainListView.setAdapter(new BaseAdapter() {
			
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView domainItem = new TextView(parent.getContext()) ;
				domainItem.setTextColor(Color.WHITE) ;
				domainItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25) ;
				domainItem.setBackgroundColor(Color.TRANSPARENT) ;
				domainItem.setWidth(LayoutParams.FILL_PARENT) ;

				@SuppressWarnings("unchecked")
				ArrayList<Domain> filteredList = (ArrayList<Domain>) CollectionUtils.select(domainList, new org.apache.commons.collections.Predicate() {
					
					public boolean evaluate(Object object) {
						Domain current = (Domain) object ;
						if (current.getName().toLowerCase().contains(filter.toLowerCase())) {
							return true ;
						} else {
							return false;
						}
					}
				}) ;

				Domain currentDomain = filteredList.get(position);
				domainItem.setText(currentDomain.getName());
				return domainItem ;
			}

			public long getItemId(int position) {
				return position+100 ;
			}
			
			public Object getItem(int position) {

				@SuppressWarnings("unchecked")
				ArrayList<Domain> filteredList = (ArrayList<Domain>) CollectionUtils.select(domainList, new org.apache.commons.collections.Predicate() {
					
					public boolean evaluate(Object object) {
						Domain current = (Domain) object ;
						if (current.getName().toLowerCase().contains(filter.toLowerCase())) {
							return true ;
						} else {
							return false;
						}
					}
				}) ;
				return filteredList.get(position) ;
			}
			
			public int getCount() {

				@SuppressWarnings("unchecked")
				ArrayList<Domain> filteredList = (ArrayList<Domain>) CollectionUtils.select(domainList, new org.apache.commons.collections.Predicate() {
					
					public boolean evaluate(Object object) {
						Domain current = (Domain) object ;
						if (current.getName().toLowerCase().contains(filter.toLowerCase())) {
							return true ;
						} else {
							return false;
						}
					}
				}) ;

				return filteredList.size() ;
			}
		}) ;

		// Catch inputs on the filter input and update the filter value. Then invalidate the ListView in 
		// order to have it update the list of displayed domains.
		EditText filterInput = (EditText) findViewById(R.id.filterInput) ;
		filterInput.setOnKeyListener(new View.OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				EditText filterInput = (EditText) v ;
				filter = filterInput.getText().toString() ;
				((ListView)findViewById(R.id.domainListView)).invalidateViews() ;
				return false;
			}
		}) ;

		findViewById(R.id.domainListView).setVisibility(View.GONE) ;
		findViewById(R.id.viewRefreshButton).setVisibility(View.GONE) ;
		findViewById(R.id.viewRefreshProgressBar).setVisibility(View.VISIBLE) ;
		new DomainListApiTask().execute() ;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		findViewById(R.id.domainListView).setVisibility(View.GONE) ;
		findViewById(R.id.viewRefreshButton).setVisibility(View.GONE) ;
		findViewById(R.id.viewRefreshProgressBar).setVisibility(View.VISIBLE) ;
		new DomainListApiTask().execute() ;
	}
}
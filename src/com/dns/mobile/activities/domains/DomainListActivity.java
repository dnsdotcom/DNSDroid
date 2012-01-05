package com.dns.mobile.activities.domains;

import java.util.ArrayList;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dns.api.compiletime.ManagementAPI;
import com.dns.mobile.R;
import com.dns.mobile.activities.groups.DomainGroupDetailsActivity;
import com.dns.mobile.data.Domain;
import com.dns.mobile.util.LogoOnClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * An <code>Activity</code> which shows a list of domains for the given user's API token
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class DomainListActivity extends Activity {

	private static final String TAG = "DomainListActivity" ;
	protected ArrayList<Domain> domainList = null ;
	protected String filter = new String("") ;
	protected ProgressDialog busyDialog = null ;

	private class DomainDeleteApiTask extends AsyncTask<Domain, Void, Domain> {
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Domain doInBackground(Domain... params) {
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

			Domain target = params[0] ;

			ManagementAPI api = new ManagementAPI(apiHost, useSSL, settings.getString("auth.token", "")) ;
			
			JSONObject result = api.deleteDomain(target.getName(), true) ;

			Domain failure = new Domain() ;
			failure.setName(target.getName()) ;
			failure.setDomainId(-1) ;
			if (result.has("meta")) {
				try {
					if (result.getJSONObject("meta").has("success")) {
						if (result.getJSONObject("meta").getInt("success")==1) {
							return target ;
						} else {
							Log.e(TAG, "API Delete request failed."+result.getJSONObject("meta").getString("error")) ;
							failure.setErrMsgString(result.getJSONObject("meta").getString("error")) ;
						}
					} else {
						Log.e(TAG, "result JSON does not have a 'success' object: "+result.toString()) ;
						failure.setErrMsgString(getResources().getString(R.string.api_result_invalid)) ;
					}
				} catch (JSONException e) {
					Log.e(TAG, "JSONException while trying to read the 'meta' JSONObject: "+result.toString(), e) ;
					failure.setErrMsgString(getResources().getString(R.string.api_result_invalid)) ;
				}
			} else {
				Log.e(TAG, "result JSON does not have a 'meta' object: "+result.toString()) ;
				failure.setErrMsgString(getResources().getString(R.string.api_result_invalid)) ;
			}
			return failure ;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Domain result) {
			super.onPostExecute(result);

			if (result.getDomainId()>0) {
				// API call succeeded
				Log.d(TAG, "Domain list count before: "+domainList.size()) ;
				domainList.remove(result) ;
				Log.d(TAG, "Domain list count after: "+domainList.size()) ;
				((ListView)findViewById(R.id.domainListView)).invalidateViews() ;
				busyDialog.dismiss() ;
			} else {
				// API call failed

				busyDialog.dismiss() ;
				AlertDialog.Builder builder = new AlertDialog.Builder(DomainListActivity.this) ;
				builder.setTitle(R.string.api_request_failed) ;
				builder.setMessage(result.getErrMsgString()) ;
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
					}
				}) ;
				builder.show() ;
			}

		}
	}

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
						domainList.clear() ;
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

	private class DomainListViewAdapter extends BaseAdapter {

		
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

			if (position==0) {
				domainItem.setText("[New Domain]") ;
			} else {
				Domain currentDomain = filteredList.get(position-1);
				domainItem.setText(currentDomain.getName());
			}
			return domainItem ;
		}

		public long getItemId(int position) {
			return position+100 ;
		}
		
		public Object getItem(int position) {

			if (position>0) {
				@SuppressWarnings("unchecked")
				ArrayList<Domain> filteredList = (ArrayList<Domain>) CollectionUtils
						.select(domainList,
								new org.apache.commons.collections.Predicate() {

									public boolean evaluate(Object object) {
										Domain current = (Domain) object;
										if (current.getName().toLowerCase()
												.contains(filter.toLowerCase())) {
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

			return filteredList.size()+1 ;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.domain_list_activity) ;
		findViewById(R.id.dnsLogo).setOnClickListener(new LogoOnClickListener(this));
		((TextView)findViewById(R.id.headerLabel)).setText(R.string.create_new_domain_label) ;

		domainList = new ArrayList<Domain>() ;
		ListView domainListView = (ListView) findViewById(R.id.domainListView) ;
		domainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> domainListView, View selectedView, int position, long itemId) {
				if (position!=0) {
					Domain selected = (Domain) domainListView.getAdapter().getItem(position) ;
					if (selected.isGroupedDomain()) {
						Intent domainGroupDetails = new Intent(getApplicationContext(), DomainGroupDetailsActivity.class) ;
						domainGroupDetails.putExtra("domainGroupName", selected.getDomainGroup()) ;
						startActivity(domainGroupDetails) ;
					} else {
						Intent hostListIntent = new Intent(getApplicationContext(), DomainDetails.class);
						hostListIntent.putExtra("domainName", ((TextView) selectedView).getText().toString());
						hostListIntent.putExtra("isDomainGroup", domainList.get(position - 1).isGroupedDomain());
						startActivity(hostListIntent);
					}
				} else {
					Intent newDomainIntent = new Intent(getApplicationContext(), CreateNewDomainActivity.class) ;
					startActivity(newDomainIntent) ;
				}
			}
			
		}) ;
		domainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> domainListView, View selectedView, int position, long itemId) {
				AlertDialog.Builder confirm = new AlertDialog.Builder(DomainListActivity.this) ;
				confirm.setTitle(R.string.rr_list_delete_dialog_title) ;
				final Domain target = (Domain) domainListView.getItemAtPosition(position) ;
				String confirmationMsg = getResources().getString(R.string.domain_delete_confirmation_message).replace("DOMAINNAME", "'"+target.getName()+"'") ;
				confirm.setMessage(confirmationMsg) ;
				confirm.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
						busyDialog = new ProgressDialog(DomainListActivity.this) ;
						busyDialog.setTitle(R.string.deleting) ;
						busyDialog.show() ;
						new DomainDeleteApiTask().execute(target) ;
					}
				}) ;
				confirm.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
					}
				}) ;
				confirm.show() ;
				return true;
			}
			
		}) ;

		domainListView.setAdapter(new DomainListViewAdapter()) ;

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

		((ImageView)findViewById(R.id.viewRefreshButton)).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				findViewById(R.id.domainListView).setVisibility(View.GONE) ;
				findViewById(R.id.viewRefreshButton).setVisibility(View.GONE) ;
				findViewById(R.id.viewRefreshProgressBar).setVisibility(View.VISIBLE) ;
				domainList.clear() ;
				new DomainListApiTask().execute() ;
			}
		}) ;

		findViewById(R.id.domainListView).setVisibility(View.GONE) ;
		findViewById(R.id.viewRefreshButton).setVisibility(View.GONE) ;
		findViewById(R.id.viewRefreshProgressBar).setVisibility(View.VISIBLE) ;
		new DomainListApiTask().execute() ;
	}
}
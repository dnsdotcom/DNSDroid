package com.dns.mobile.activities.domains;

import java.util.ArrayList;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.Domain;
import com.dns.mobile.util.LogoOnClickListener;
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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
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
public class DomainListActivity extends Activity {

	protected ArrayList<Domain> domainList = null ;
	protected String filter = new String("") ;

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
				findViewById(R.id.domainListProgressBar).setVisibility(View.GONE);
				if (result.has("meta")) {
					try {
						if (result.getJSONObject("meta").getInt("success") == 1) {
							apiRequestSucceeded = true;
						} else {
							Log.e("DomainListActivity", "API Error: " + result.getJSONObject("meta").getString("error"));
							AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
							builder.setTitle(R.string.api_request_failed);
							builder.setMessage(result.getJSONObject("meta").getString("error"));
						}
					} catch (JSONException jsone) {
						Log.e("DomainListActivity", "JSONException encountered while trying to parse domain list.", jsone);
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
							Log.d("DomainListActivity", "JSON: " + currentData.toString());
							Domain currentDomain = new Domain();
							currentDomain.setName(currentData.getString("name"));
							Log.d("DomainListActivity", "Adding domain '" + currentData.getString("name") + "' to domainList");
							currentDomain.setDomainId(currentData.getLong("id"));
							currentDomain.setGroupedDomain(currentData.getString("mode").contentEquals("group"));
							if (currentDomain.isGroupedDomain()) {
								currentDomain.setDomainGroup(currentData.getString("group"));
							}
							domainList.add(currentDomain);
						}
						findViewById(R.id.domainListView).setVisibility(View.VISIBLE);
						((ListView) findViewById(R.id.domainListView)).invalidateViews();
						Log.d("DomainListActivity", "Finished parsing JSON response into domainList");
					} catch (JSONException jsone) {
						Log.e("DomainListActivity", "JSONException encountered while trying to parse domain list.", jsone);
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
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.domain_list_activity) ;
		domainList = new ArrayList<Domain>() ;
		findViewById(R.id.dnsLogo).setOnClickListener(new LogoOnClickListener(this));

		ListView domainListView = (ListView) findViewById(R.id.domainListView) ;
		domainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> domainListView, View selectedView, int position, long itemId) {
				if (position!=0) {
					Domain selected = (Domain) domainListView.getAdapter().getItem(position) ;
					if (selected.isGroupedDomain()) {
						// TODO: Add Intent for showing the members of the associated domain group
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
				
				return false;
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
				return filteredList.get(position-1) ;
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
		findViewById(R.id.domainListProgressBar).setVisibility(View.VISIBLE) ;
		new DomainListApiTask().execute() ;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem refreshDomains = menu.add(Menu.NONE, 0, 0, "Refresh");
		refreshDomains.setIcon(R.drawable.ic_menu_refresh) ;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				findViewById(R.id.domainListView).setVisibility(View.GONE) ;
				findViewById(R.id.domainListProgressBar).setVisibility(View.VISIBLE) ;
				domainList.clear() ;
				new DomainListApiTask().execute() ;
				return true;
		}
		return false;
	}
}
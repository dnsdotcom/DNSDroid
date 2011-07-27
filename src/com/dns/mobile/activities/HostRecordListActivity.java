package com.dns.mobile.activities;

import java.util.ArrayList;

import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.ResourceRecord;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HostRecordListActivity extends Activity {

	protected ArrayList<ResourceRecord> rrList = null ;
	protected String filter = new String("") ;
	protected boolean isDomainGroup = false ;
	protected String domainName = null ;
	protected String hostName = null ;

	private class RRListApiTask extends AsyncTask<String, Void, JSONObject> {

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

			return api.getRRSetForHostname(params[0], isDomainGroup, params[1].contentEquals("(root)")?"":params[1]) ;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			boolean apiRequestSucceeded = false ;
			findViewById(R.id.rrListProgressBar).setVisibility(View.GONE) ;
			if (result.has("meta")) {
				try {
					if (result.getJSONObject("meta").getInt("success")==1) {
						apiRequestSucceeded = true ;
					} else {
						Log.e("HostRecordListActivity", "API Error: "+result.getJSONObject("meta").getString("error")) ;
						AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext()) ;
						builder.setTitle(R.string.api_request_failed) ;
						builder.setMessage(result.getJSONObject("meta").getString("error")) ;
					}
				} catch (JSONException jsone) {
					Log.e("HostRecordListActivity", "JSONException encountered while trying to parse domain list.", jsone) ;
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
						ResourceRecord currentRR = new ResourceRecord() ;
						currentRR.setAnswer(currentData.getString("answer")) ;
						Log.d("HostRecordListActivity", "Adding RR '"+currentData.getString("answer")+"' to rrList") ;
						currentRR.setHostId(currentData.getLong("id")) ;
						currentRR.setType(currentData.getString("type")) ;
						currentRR.setId(currentData.getLong("id")) ;
						currentRR.setTtl(currentData.getLong("ttl")) ;
						if (currentData.getString("country_iso2").length()>0) {
							currentRR.setCountryId(currentData.getString("country_iso2")) ;
							if (!currentData.getString("region").contentEquals("None")) {
								currentRR.setRegionId(currentData.getInt("region")) ;
								if (!currentData.getString("city").contentEquals("None")) {
									currentRR.setCityId(currentData.getInt("city")) ;
								}
							}
						}
						currentRR.setWildcard(currentData.getBoolean("is_wildcard")) ;
						if (!currentData.getString("geoGroup").contentEquals("None")) {
							currentRR.setGeoGroup(currentData.getString("geoGroup")) ;
							currentRR.setGroup(true) ;
						}
						if (currentData.has("retry")) {
							if (!currentData.getString("retry").contentEquals("null")) {
								currentRR.setRetry(currentData.getLong("retry"));
							}
						}
						if (currentData.has("minimum")) {
							if (!currentData.getString("minimum").contentEquals("null")) {
								currentRR.setMinimum(currentData.getLong("minimum"));
							}
						}
						if (currentData.has("expire")) {
							if (!currentData.getString("expire").contentEquals("null")) {
								currentRR.setExpire(currentData.getLong("expire")) ;
							}
						}
						if (currentData.has("priority")) {
							if (!currentData.getString("priority").contentEquals("null")) {
								currentRR.setPriority(currentData.getLong("priority")) ;
							}
						}
						if (currentData.has("weight")) {
							if (!currentData.getString("weight").contentEquals("null")) {
								currentRR.setWeight(currentData.getInt("weight")) ;
							}
						}
						if (currentData.has("port")) {
							if (!currentData.getString("port").contentEquals("null")) {
								currentRR.setPort(currentData.getLong("port")) ;
							}
						}
						rrList.add(currentRR) ;
					}
					findViewById(R.id.rrListView).setVisibility(View.VISIBLE) ;
					((ListView)findViewById(R.id.rrListView)).invalidateViews() ;
					Log.d("HostRecordListActivity", "Finished parsing JSON response into domainList") ;
				} catch (JSONException jsone) {
					Log.e("HostRecordListActivity", "JSONException encountered while trying to parse domain list.", jsone) ;
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
		super.onCreate(savedInstanceState) ;
		setContentView(R.layout.host_rr_activity) ;
		rrList = new ArrayList<ResourceRecord>() ;
		domainName = this.getIntent().getExtras().getString("domainName") ;
		hostName = this.getIntent().getExtras().getString("hostName") ;
		isDomainGroup = this.getIntent().getExtras().getBoolean("isDomainGroup") ;
		String fqdn = (hostName.contentEquals("")?"(root).":hostName+".")+domainName ;
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

		((TextView)findViewById(R.id.rrHeaderLabel)).setText(fqdn) ;
		ListView rrListView = (ListView) findViewById(R.id.rrListView) ;
		rrListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			/* (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
			 */
			public void onItemClick(AdapterView<?> rrListView, View selectedView, int position, long viewId) {
				Intent rrDetailsActivity = new Intent(getApplicationContext(), RecordDetailActivity.class) ;
				ResourceRecord clickedRR = (ResourceRecord) rrListView.getAdapter().getItem(position) ;
				clickedRR.setHostName(hostName) ;
				clickedRR.setDomainName(domainName) ;
				rrDetailsActivity.putExtra("rrData", clickedRR) ;
				startActivity(rrDetailsActivity) ;
			}
		}) ;
		rrListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			/* (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
			 */
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				return false;
			}
		}) ;

		rrListView.setAdapter(new BaseAdapter() {
			
			public View getView(int position, View convertView, ViewGroup parent) {
				LinearLayout listItemLayout = new LinearLayout(getBaseContext()) ;
				listItemLayout.setOrientation(LinearLayout.HORIZONTAL) ;

				TextView hostItem = new TextView(parent.getContext()) ;
				hostItem.setTextColor(Color.WHITE) ;
				hostItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25) ;
				hostItem.setBackgroundColor(Color.TRANSPARENT) ;

				@SuppressWarnings("unchecked")
				ArrayList<ResourceRecord> filteredList = (ArrayList<ResourceRecord>) CollectionUtils.select(rrList, new org.apache.commons.collections.Predicate() {
					
					public boolean evaluate(Object object) {
						ResourceRecord current = (ResourceRecord) object ;
						if (current.getAnswer().toLowerCase().contains(filter.toLowerCase())) {
							return true ;
						} else {
							return false;
						}
					}
				}) ;

				if (position==0) {
					hostItem.setText("[New RR]") ;
				} else {
					TextView rrType = new TextView(getBaseContext()) ;
					rrType.setBackgroundDrawable(getResources().getDrawable(R.drawable.type_background)) ;
					rrType.setTextColor(Color.WHITE) ;
					rrType.setText(ResourceRecord.getTypeAsString(filteredList.get(position-1).getType())) ;
					rrType.setGravity(Gravity.CENTER) ;
					listItemLayout.addView(rrType) ;
					
					ResourceRecord currentRR = filteredList.get(position-1);

					Drawable indicatorIcon = getResources().getDrawable(R.drawable.globe) ;
					if (((!currentRR.getCountryId().contentEquals("")) && (!currentRR.getCountryId().contentEquals("null"))) || (currentRR.getGeoGroup()!=null)) {
						Log.d("HostRecordListActivity", "Country Code is: "+currentRR.getCountryId()) ;
						indicatorIcon = getResources().getDrawable(R.drawable.pushpin_blue) ;
					}

					ImageView geoIndicator = new ImageView(getBaseContext()) ;
					geoIndicator.setImageDrawable(indicatorIcon) ;
					listItemLayout.addView(geoIndicator) ;

					hostItem.setText(currentRR.getAnswer()) ;
				}
				listItemLayout.addView(hostItem) ;
				return listItemLayout ;
			}
			
			public long getItemId(int position) {
				return position+300 ;
			}
			
			public Object getItem(int position) {

				@SuppressWarnings("unchecked")
				ArrayList<ResourceRecord> filteredList = (ArrayList<ResourceRecord>) CollectionUtils.select(rrList, new org.apache.commons.collections.Predicate() {
					
					public boolean evaluate(Object object) {
						ResourceRecord current = (ResourceRecord) object ;
						if (current.getAnswer().toLowerCase().contains(filter.toLowerCase())) {
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
				ArrayList<ResourceRecord> filteredList = (ArrayList<ResourceRecord>) CollectionUtils.select(rrList, new org.apache.commons.collections.Predicate() {
					
					public boolean evaluate(Object object) {
						ResourceRecord current = (ResourceRecord) object ;
						if (current.getAnswer().toLowerCase().contains(filter.toLowerCase())) {
							return true ;
						} else {
							return false;
						}
					}
				}) ;
				return filteredList.size()+1 ;
			}
		}) ;

		findViewById(R.id.rrListView).setVisibility(View.GONE) ;
		findViewById(R.id.rrListProgressBar).setVisibility(View.VISIBLE) ;
		new RRListApiTask().execute(domainName, hostName) ;

		// Catch inputs on the filter input and update the filter value. Then invalidate the ListView in 
		// order to have it update the list of displayed hosts.
		EditText filterInput = (EditText) findViewById(R.id.filterInput) ;
		filterInput.setOnKeyListener(new View.OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				EditText filterInput = (EditText) v ;
				filter = filterInput.getText().toString() ;
				((ListView)findViewById(R.id.rrListView)).invalidateViews() ;
				return false;
			}
		}) ;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem refreshRecords = menu.add(Menu.NONE, 0, 0, "Refresh");
		refreshRecords.setIcon(R.drawable.ic_menu_refresh) ;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				findViewById(R.id.rrListView).setVisibility(View.GONE) ;
				findViewById(R.id.rrListProgressBar).setVisibility(View.VISIBLE) ;
				rrList.clear() ;
				new RRListApiTask().execute(domainName, hostName) ;
				return true;
		}
		return false;
	}
}

package com.dns.mobile.activities.records;

import java.util.ArrayList;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.ResourceRecord;
import com.dns.mobile.util.LogoOnClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
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

	private static final String TAG = "HostRecordListActivity" ;
	protected ArrayList<ResourceRecord> rrList = null ;
	protected String filter = new String("") ;
	protected boolean isDomainGroup = false ;
	protected String domainName = null ;
	protected String hostName = null ;
	protected ListView rrListView = null ;
	protected ProgressDialog busyDialog = null ;

	private class RRListApiItemDeleteTask extends AsyncTask<ResourceRecord, Void, ResourceRecord> {
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected ResourceRecord doInBackground(ResourceRecord... params) {
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
			JSONObject retVal = api.removeRR(params[0].getId().intValue(), true) ;
			
			boolean apiRequestSucceeded = false ;
			if (retVal.has("meta")) {
				try {
					if (retVal.getJSONObject("meta").getInt("success")==1) {
						apiRequestSucceeded = true ;
					} else {
						Log.e(TAG, "API Error: "+retVal.getJSONObject("meta").getString("error")) ;
						AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext()) ;
						builder.setTitle(R.string.api_request_failed) ;
						builder.setMessage(retVal.getJSONObject("meta").getString("error")) ;
					}
				} catch (JSONException jsone) {
					Log.e(TAG, "JSONException encountered while trying to parse delete response.", jsone) ;
					AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext()) ;
					builder.setTitle(R.string.api_request_failed) ;
					builder.setMessage(jsone.getLocalizedMessage()) ;
				}
			}

			if (apiRequestSucceeded) {
				return params[0] ;
			} else {
				ResourceRecord error = params[0] ;
				error.setActive(false) ;
				return error ;
			}
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(ResourceRecord result) {
			super.onPostExecute(result);

			Log.d(TAG, "onPostExecute for item delete.") ;
			busyDialog.dismiss() ;
			if (result.isActive()) {
				Log.d(TAG, "result record is marked as active.") ;
				int itemIndex = rrList.indexOf(result) ;
				rrList.remove(itemIndex) ;
				((ListView)findViewById(R.id.rrListView)).invalidateViews() ;
			} else {
				Log.d(TAG, "result record is marked as inactive.") ;
				AlertDialog.Builder builder = new AlertDialog.Builder(HostRecordListActivity.this) ;
				builder.setTitle(R.string.rr_delete_failed_title) ;
				String messageBody = HostRecordListActivity.this.getResources().getString(R.string.rr_delete_failed_message).replaceAll("||REPLACE||", result.getId()+"") ;
				builder.setMessage(messageBody) ;
				builder.show() ;
			}
		}
	}

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
			findViewById(R.id.viewRefreshButton).setVisibility(View.VISIBLE) ;
			findViewById(R.id.viewRefreshProgressBar).setVisibility(View.GONE) ;
			if (result.has("meta")) {
				try {
					if (result.getJSONObject("meta").getInt("success")==1) {
						apiRequestSucceeded = true ;
					} else {
						Log.e(TAG, "API Error: "+result.getJSONObject("meta").getString("error")) ;
						AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext()) ;
						builder.setTitle(R.string.api_request_failed) ;
						builder.setMessage(result.getJSONObject("meta").getString("error")) ;
					}
				} catch (JSONException jsone) {
					Log.e(TAG, "JSONException encountered while trying to parse domain list.", jsone) ;
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
						Log.d(TAG, "Adding RR '"+currentData.getString("answer")+"' to rrList") ;
						currentRR.setHostId(currentData.getLong("id")) ;
						currentRR.setType(currentData.getString("type")) ;
						currentRR.setId(currentData.getLong("id")) ;
						currentRR.setTtl(currentData.getInt("ttl")) ;
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
								currentRR.setRetry(currentData.getInt("retry"));
							}
						}
						if (currentData.has("minimum")) {
							if (!currentData.getString("minimum").contentEquals("null")) {
								currentRR.setMinimum(currentData.getInt("minimum"));
							}
						}
						if (currentData.has("expire")) {
							if (!currentData.getString("expire").contentEquals("null")) {
								currentRR.setExpire(currentData.getInt("expire")) ;
							}
						}
						if (currentData.has("priority")) {
							if (!currentData.getString("priority").contentEquals("null")) {
								currentRR.setPriority(currentData.getInt("priority")) ;
							}
						}
						if (currentData.has("weight")) {
							if (!currentData.getString("weight").contentEquals("null")) {
								currentRR.setWeight(currentData.getInt("weight")) ;
							}
						}
						if (currentData.has("port")) {
							if (!currentData.getString("port").contentEquals("null")) {
								currentRR.setPort(currentData.getInt("port")) ;
							}
						}
						if (currentData.has("title")) {
							if (!currentData.getString("title").contentEquals("null")) {
								currentRR.setTitle(currentData.getString("title")) ;
							}
						}
						if (currentData.has("keywords")) {
							if (!currentData.getString("keywords").contentEquals("null")) {
								currentRR.setKeywords(currentData.getString("keywords")) ;
							}
						}
						if (currentData.has("description")) {
							if (!currentData.getString("description").contentEquals("null")) {
								currentRR.setDescription(currentData.getString("description")) ;
							}
						}
						rrList.add(currentRR) ;
					}
					findViewById(R.id.rrListView).setVisibility(View.VISIBLE) ;
					((ListView)findViewById(R.id.rrListView)).invalidateViews() ;
					Log.d(TAG, "Finished parsing JSON response into domainList") ;
				} catch (JSONException jsone) {
					Log.e(TAG, "JSONException encountered while trying to parse domain list.", jsone) ;
				}
			} else {
				showDialog(R.string.api_request_failed) ;
			}
		}
	}

	private class ListItemOnClickListener implements AdapterView.OnItemClickListener {

		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		public void onItemClick(AdapterView<?> rrListView, View selectedView, int position, long viewId) {
			Intent rrDetailsActivity = new Intent(getApplicationContext(), RecordDetailActivity.class) ;
			ResourceRecord clickedRR = null ;
			if (position!=0) {
				clickedRR = (ResourceRecord) rrListView.getAdapter().getItem(position) ;
			} else {
				clickedRR = new ResourceRecord() ;
				clickedRR.setGroup(isDomainGroup) ;
			}
			clickedRR.setHostName(hostName) ;
			clickedRR.setDomainName(domainName) ;
			clickedRR.setGroup(isDomainGroup) ;
			rrDetailsActivity.putExtra("rrData", clickedRR) ;
			startActivity(rrDetailsActivity) ;
		}
	}

	private class ListItemOnLongClickListener implements AdapterView.OnItemLongClickListener {
		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		public boolean onItemLongClick(AdapterView<?> rrListView, View selectedView, int position, long viewId) {

			final int itemPosition = position ;
			AlertDialog.Builder builder = new AlertDialog.Builder(HostRecordListActivity.this) ;
			builder.setTitle(R.string.rr_list_delete_dialog_title) ;
			builder.setMessage(R.string.rr_list_delete_dialog_message) ;
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					busyDialog = new ProgressDialog(HostRecordListActivity.this) ;
					busyDialog.setTitle(R.string.deleting) ;
					busyDialog.show() ;
					ResourceRecord selected = (ResourceRecord) ((ListView)findViewById(R.id.rrListView)).getItemAtPosition(itemPosition) ;
					selected.setActive(true) ;
					new RRListApiItemDeleteTask().execute(selected) ;
				}
			}) ;
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss() ;
				}
			}) ;
			builder.show() ;
			return true;
		}
	}

	private class RRListAdapter extends BaseAdapter {
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
					Log.d(TAG, "Country Code is: "+currentRR.getCountryId()) ;
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
		Log.d(TAG,"Setting host/domain name to: "+hostName+"/"+domainName) ;
		isDomainGroup = this.getIntent().getExtras().getBoolean("isDomainGroup") ;
		String fqdn = (hostName.contentEquals("")?"(root).":hostName+".")+domainName ;
		findViewById(R.id.dnsLogo).setOnClickListener(new LogoOnClickListener(this));

		((TextView)findViewById(R.id.headerLabel)).setText(fqdn) ;
		rrListView = (ListView) findViewById(R.id.rrListView) ;
		rrListView.setOnItemClickListener(new ListItemOnClickListener()) ;
		rrListView.setOnItemLongClickListener(new ListItemOnLongClickListener()) ;

		rrListView.setAdapter(new RRListAdapter()) ;

		findViewById(R.id.viewRefreshButton).setVisibility(View.GONE) ;
		findViewById(R.id.viewRefreshProgressBar).setVisibility(View.VISIBLE) ;
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

		((ImageView)findViewById(R.id.viewRefreshButton)).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				findViewById(R.id.viewRefreshButton).setVisibility(View.GONE) ;
				findViewById(R.id.viewRefreshProgressBar).setVisibility(View.VISIBLE) ;
				rrList.clear() ;
				findViewById(R.id.rrListView).invalidate() ;
				new RRListApiTask().execute(domainName, hostName) ;
			}
		}) ;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		findViewById(R.id.viewRefreshButton).setVisibility(View.GONE) ;
		findViewById(R.id.viewRefreshProgressBar).setVisibility(View.VISIBLE) ;
		rrList.clear() ;
		findViewById(R.id.rrListView).invalidate() ;
		new RRListApiTask().execute(domainName, hostName) ;
	}
}

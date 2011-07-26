package com.dns.mobile.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.ResourceRecord;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HostRecordListActivity extends Activity {

	protected ArrayList<ResourceRecord> rrList = null ;

	private class RRListApiTask extends AsyncTask<String, Void, JSONObject> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected JSONObject doInBackground(String... params) {
			findViewById(R.id.rrListView).setVisibility(View.INVISIBLE) ;
			findViewById(R.id.rrListProgressBar).setVisibility(View.VISIBLE) ;
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

			return api.getRRSetForHostname(params[0], false, params[1].contentEquals("(root)")?"":params[1]) ;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			boolean apiRequestSucceeded = false ;
			findViewById(R.id.rrListProgressBar).setVisibility(View.INVISIBLE) ;
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
						if (currentData.getString("country_iso2").length()>0) {
							currentRR.setCountryId(currentData.getString("country_iso2")) ;
						}
						if (!currentData.getString("geoGroup").contentEquals("None")) {
							currentRR.setGeoGroup(currentData.getString("geoGroup")) ;
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
		final String domainName = this.getIntent().getExtras().getString("domainName") ;
		final String hostName = this.getIntent().getExtras().getString("hostName") ;
		String fqdn = (hostName.contentEquals("")?"(root).":hostName+".")+domainName ;

		((TextView)findViewById(R.id.rrHeaderLabel)).setText(fqdn) ;
		ListView rrListView = (ListView) findViewById(R.id.rrListView) ;
		rrListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			/* (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
			 */
			public void onItemClick(AdapterView<?> rrListView, View selectedView, int position, long viewId) {
				Intent rrDetailsActivity = new Intent(getApplicationContext(), RecordDetailActivity.class) ;
				rrDetailsActivity.putExtra("rr_id", rrList.get(position-1).getId().longValue()) ;
				rrDetailsActivity.putExtra("rr_type", rrList.get(position-1).getType()) ;
				rrDetailsActivity.putExtra("domainName", domainName) ;
				rrDetailsActivity.putExtra("hostName", hostName) ;
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
				hostItem.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10) ;
				hostItem.setBackgroundColor(Color.TRANSPARENT) ;
				if (position==0) {
					hostItem.setText("[New RR]") ;
				} else {
					TextView rrType = new TextView(getBaseContext()) ;
					rrType.setBackgroundDrawable(getResources().getDrawable(R.drawable.type_background)) ;
					rrType.setTextColor(Color.WHITE) ;
					rrType.setText(ResourceRecord.getTypeAsString(rrList.get(position-1).getType())) ;
					rrType.setGravity(Gravity.CENTER) ;
					listItemLayout.addView(rrType) ;
					
					ResourceRecord currentRR = rrList.get(position-1);

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
				return rrList.get(position-1) ;
			}
			
			public int getCount() {
				return rrList.size()+1 ;
			}
		}) ;

		new RRListApiTask().execute(domainName, hostName) ;
	}
}

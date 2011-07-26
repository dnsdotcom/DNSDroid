/**
 * 
 */
package com.dns.mobile.activities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.ResourceRecord;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class RecordDetailActivity extends Activity {

	ResourceRecord currentRR = null ;

	private class RRFetchApiTask extends AsyncTask<String, Void, JSONObject> {

		private long rrId = 0L ;

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected JSONObject doInBackground(String... params) {
			try {
				rrId = Long.parseLong(params[3]) ;
			} catch (NumberFormatException nfe) {
				Log.e("RecordDetailActivity", "NumberFormatException trying to parse the RR ID", nfe) ;
			}
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
						Log.e("RecordDetailActivity", "API Error: "+result.getJSONObject("meta").getString("error")) ;
						AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext()) ;
						builder.setTitle(R.string.api_request_failed) ;
						builder.setMessage(result.getJSONObject("meta").getString("error")) ;
					}
				} catch (JSONException jsone) {
					Log.e("RecordDetailActivity", "JSONException encountered while trying to parse domain list.", jsone) ;
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
						if (currentData.getLong("id")==rrId) {
							currentRR
									.setAnswer(currentData.getString("answer"));
							Log.d("RecordDetailActivity", "Adding RR '"
									+ currentData.getString("answer")
									+ "' to rrList");
							currentRR.setHostId(currentData.getLong("id"));
							currentRR.setType(currentData.getString("type"));
							currentRR.setId(currentData.getLong("id"));
							if (currentData.getString("country_iso2").length() > 0) {
								currentRR.setCountryId(currentData
										.getString("country_iso2"));
							}
							if (!currentData.getString("geoGroup")
									.contentEquals("None")) {
								currentRR.setGeoGroup(currentData
										.getString("geoGroup"));
							}
						}
					}
					findViewById(R.id.rrListView).setVisibility(View.VISIBLE) ;
					((ListView)findViewById(R.id.rrListView)).invalidateViews() ;
					Log.d("RecordDetailActivity", "Finished parsing JSON response into ResourceRecord") ;
				} catch (JSONException jsone) {
					Log.e("RecordDetailActivity", "JSONException encountered while trying to parse rr details.", jsone) ;
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
		setContentView(R.layout.rr_details_view) ;
		long rrId = this.getIntent().getLongExtra("rr_id", 0L) ;
		int rrType = this.getIntent().getIntExtra("rr_type", 1) ;
		String domainName = this.getIntent().getStringExtra("domainName") ;
		String hostName = this.getIntent().getStringExtra("hostName") ;
		currentRR = new ResourceRecord() ;

		// Set up the appropriate views for the record type.
		switch (rrType) {
			case 1:
				// A Record
				findViewById(R.id.rrExpire).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrExpireLabel).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrMinimum).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrMinimumLabel).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrRetryInterval).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrRetryLabel).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrPriority).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrPriorityLabel).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrResponsibleParty).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrResponsiblePartyLabel).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrSrvPort).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrSrvPortLabel).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrSrvWeight).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrSrvWeightLabel).setVisibility(View.INVISIBLE) ;
				break ;
			case 2:
				// NS Record
				findViewById(R.id.rrExpire).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrExpireLabel).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrMinimum).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrMinimumLabel).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrRetryInterval).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrRetryLabel).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrPriority).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrPriorityLabel).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrResponsibleParty).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrResponsiblePartyLabel).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrSrvPort).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrSrvPortLabel).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrSrvWeight).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrSrvWeightLabel).setVisibility(View.INVISIBLE) ;
				break ;
			case 6:
				// SOA Record
				findViewById(R.id.rrAnswer).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrPriority).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrPriorityLabel).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrSrvPort).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrSrvPortLabel).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrSrvWeight).setVisibility(View.INVISIBLE) ;
				findViewById(R.id.rrSrvWeightLabel).setVisibility(View.INVISIBLE) ;
				
		}

		new RRFetchApiTask().doInBackground(domainName, hostName, rrId+"") ;
	}
}

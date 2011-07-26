/**
 * 
 */
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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class RecordDetailActivity extends Activity {

	ResourceRecord currentRR = null ;

	/**
	 * A class which extends the AsyncTask AbstractClass to perform network operations in the background.
	 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
	 *
	 */
	@SuppressWarnings("unused")
	private class RRSaveViaAPI extends AsyncTask<ResourceRecord, Void, JSONObject> {

		private long rrId = 0L ;

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected JSONObject doInBackground(ResourceRecord... params) {
			ResourceRecord rr = params[0] ;
			try {
				rrId = params[0].getId() ;
			} catch (NumberFormatException nfe) {
				Log.e("RecordDetailActivity", "NumberFormatException trying to parse the RR ID", nfe) ;
			}
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

			return api.getRRSetForHostname(rr.getDomainName(), false, rr.getHostName().contentEquals("(root)")?"":rr.getHostName()) ;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			boolean apiRequestSucceeded = false ;
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
		currentRR = (ResourceRecord) this.getIntent().getSerializableExtra("rrData") ;
		final String hostName = currentRR.getHostName() ;
		final String domainName = currentRR.getDomainName() ;
		TextView header = (TextView) findViewById(R.id.rrHeaderLabel) ;
		StringBuilder sb = new StringBuilder(header.getText()) ;
		sb.append(" ") ;
		sb.append(hostName.contentEquals("")?"(root)":hostName) ;
		sb.append(".") ;
		sb.append(domainName) ;
		header.setText(sb.toString()) ;

		ArrayList<String> typeList = new ArrayList<String>() ;
		for (String temp: getResources().getStringArray(R.array.recordTypes)) {
			typeList.add(temp) ;
		}

		// Set up the appropriate views for the record type.
		switch (currentRR.getType()) {
			case 1:
				// A Record
				((EditText)findViewById(R.id.rrAnswer)).setText(currentRR.getAnswer()) ;
				findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrTtlInput)).setText(currentRR.getTtl()+"") ;
				findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				((Spinner)findViewById(R.id.rrTypeSpinner)).setSelection(typeList.indexOf(ResourceRecord.getTypeAsString(currentRR.getType()))) ;
				break ;
			case 2:
				// NS Record
				((EditText)findViewById(R.id.rrAnswer)).setText(currentRR.getAnswer()) ;
				findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrTtlInput)).setText(currentRR.getTtl()+"") ;
				findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				((Spinner)findViewById(R.id.rrTypeSpinner)).setSelection(typeList.indexOf(ResourceRecord.getTypeAsString(currentRR.getType()))) ;
				break ;
			case 6:
				// SOA Record
				((EditText)findViewById(R.id.rrTtlInput)).setText(currentRR.getTtl()+"") ;
				findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrExpire)).setText(currentRR.getExpire()+"") ;
				findViewById(R.id.rrExpire).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrExpireLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrMinimum)).setText(currentRR.getMinimum()+"") ;
				findViewById(R.id.rrMinimum).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrMinimumLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrRetryInterval)).setText(currentRR.getRetry()+"") ;
				findViewById(R.id.rrRetryInterval).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrRetryLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrPriority)).setText(currentRR.getPriority()+"") ;
				findViewById(R.id.rrPriority).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrPriorityLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrResponsibleParty)).setText(currentRR.getAnswer()+"") ;
				findViewById(R.id.rrResponsibleParty).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrResponsiblePartyLabel).setVisibility(View.VISIBLE) ;
				((Spinner)findViewById(R.id.rrTypeSpinner)).setSelection(typeList.indexOf(ResourceRecord.getTypeAsString(currentRR.getType()))) ;
				break ;
			case 5:
				// CNAME Record
				((EditText)findViewById(R.id.rrAnswer)).setText(currentRR.getAnswer()) ;
				findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrTtlInput)).setText(currentRR.getTtl()+"") ;
				findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				((Spinner)findViewById(R.id.rrTypeSpinner)).setSelection(typeList.indexOf(ResourceRecord.getTypeAsString(currentRR.getType()))) ;
				break ;
			case 15:
				// MX Record
				((EditText)findViewById(R.id.rrAnswer)).setText(currentRR.getAnswer()) ;
				findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrTtlInput)).setText(currentRR.getTtl()+"") ;
				findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrPriority)).setText(currentRR.getPriority()+"") ;
				findViewById(R.id.rrPriority).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrPriorityLabel).setVisibility(View.VISIBLE) ;
				((Spinner)findViewById(R.id.rrTypeSpinner)).setSelection(typeList.indexOf(ResourceRecord.getTypeAsString(currentRR.getType()))) ;
				break ;
			case 16:
				// TXT Record
				((EditText)findViewById(R.id.rrAnswer)).setText(currentRR.getAnswer()) ;
				findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrTtlInput)).setText(currentRR.getTtl()+"") ;
				findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				((Spinner)findViewById(R.id.rrTypeSpinner)).setSelection(typeList.indexOf(ResourceRecord.getTypeAsString(currentRR.getType()))) ;
				break ;
			case 28:
				// AAAA Record
				((EditText)findViewById(R.id.rrAnswer)).setText(currentRR.getAnswer()) ;
				findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrTtlInput)).setText(currentRR.getTtl()+"") ;
				findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				((Spinner)findViewById(R.id.rrTypeSpinner)).setSelection(typeList.indexOf(ResourceRecord.getTypeAsString(currentRR.getType()))) ;
				break ;
			case 33:
				// SRV Record
				((EditText)findViewById(R.id.rrAnswer)).setText(currentRR.getAnswer()) ;
				findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrTtlInput)).setText(currentRR.getTtl()+"") ;
				findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrPriority)).setText(currentRR.getPriority()+"") ;
				findViewById(R.id.rrPriority).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrPriorityLabel).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrSrvPortLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrSrvPort)).setText(currentRR.getPort()+"") ;
				findViewById(R.id.rrSrvPort).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrSrvWeightLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrSrvWeight)).setText(currentRR.getWeight()+"") ;
				findViewById(R.id.rrSrvWeight).setVisibility(View.VISIBLE) ;
				((Spinner)findViewById(R.id.rrTypeSpinner)).setSelection(typeList.indexOf(ResourceRecord.getTypeAsString(currentRR.getType()))) ;
				break ;
			case 80000:
				// URL 302 Record
				((EditText)findViewById(R.id.rrAnswer)).setText(currentRR.getAnswer()) ;
				findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrTtlInput)).setText(currentRR.getTtl()+"") ;
				findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				((Spinner)findViewById(R.id.rrTypeSpinner)).setSelection(typeList.indexOf(ResourceRecord.getTypeAsString(currentRR.getType()))) ;
				break ;
			case 80001:
				// URL 301 Record
				((EditText)findViewById(R.id.rrAnswer)).setText(currentRR.getAnswer()) ;
				findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrTtlInput)).setText(currentRR.getTtl()+"") ;
				findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				((Spinner)findViewById(R.id.rrTypeSpinner)).setSelection(typeList.indexOf(ResourceRecord.getTypeAsString(currentRR.getType()))) ;
				break ;
			case 80002:
				// URL Frame Record
				((EditText)findViewById(R.id.rrAnswer)).setText(currentRR.getAnswer()) ;
				findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrTtlInput)).setText(currentRR.getTtl()+"") ;
				findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				((Spinner)findViewById(R.id.rrTypeSpinner)).setSelection(typeList.indexOf(ResourceRecord.getTypeAsString(currentRR.getType()))) ;
				break ;
			default:
				((EditText)findViewById(R.id.rrAnswer)).setText(currentRR.getAnswer()) ;
				findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
				((EditText)findViewById(R.id.rrTtlInput)).setText(currentRR.getTtl()+"") ;
				findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				((Spinner)findViewById(R.id.rrTypeSpinner)).setSelection(0) ;
		}
	}
}

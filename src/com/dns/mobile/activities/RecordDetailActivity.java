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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class RecordDetailActivity extends Activity {

	protected ResourceRecord currentRR = null ;
	protected ArrayList<String> typeList = null ;
	protected String domainName = null ;
	protected String hostName = null ;
	protected boolean isExistingRecord = true ;

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
		if (currentRR==null) {
			currentRR = new ResourceRecord() ;
			currentRR.setType(1) ;
			isExistingRecord = false ;
		} else {
			hostName = currentRR.getHostName() ;
			domainName = currentRR.getDomainName() ;
		}
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

		TextView header = (TextView) findViewById(R.id.rrHeaderLabel) ;
		StringBuilder sb = new StringBuilder(header.getText()) ;
		sb.append(" ") ;
		sb.append(hostName.contentEquals("")?"(root)":hostName) ;
		sb.append(".") ;
		sb.append(domainName) ;
		header.setText(sb.toString()) ;

		typeList = new ArrayList<String>() ;
		for (String temp: getResources().getStringArray(R.array.recordTypes)) {
			typeList.add(temp) ;
		}

		// Set up the appropriate views for the record type.
		switch (currentRR.getType()) {
			case 6:
				// SOA Record
				if (isExistingRecord) {
					((EditText)findViewById(R.id.rrTtlInput)).setText(currentRR.getTtl()+"") ;
					((EditText)findViewById(R.id.rrExpire)).setText(currentRR.getExpire()+"") ;
					((EditText)findViewById(R.id.rrMinimum)).setText(currentRR.getMinimum()+"") ;
					((EditText)findViewById(R.id.rrRetryInterval)).setText(currentRR.getRetry()+"") ;
					((EditText)findViewById(R.id.rrPriority)).setText(currentRR.getPriority()+"") ;
					((EditText)findViewById(R.id.rrResponsibleParty)).setText(currentRR.getAnswer()+"") ;
				}
				findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrExpire).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrExpireLabel).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrMinimum).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrMinimumLabel).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrRetryInterval).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrRetryLabel).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrPriority).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrPriorityLabel).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrResponsibleParty).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrResponsiblePartyLabel).setVisibility(View.VISIBLE) ;
				break ;
			case 15:
				if (isExistingRecord) {
					// MX Record
					((EditText) findViewById(R.id.rrAnswer)).setText(currentRR.getAnswer());
					((EditText) findViewById(R.id.rrTtlInput)).setText(currentRR.getTtl() + "");
					((EditText) findViewById(R.id.rrPriority)).setText(currentRR.getPriority() + "");
				}
				findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrPriority).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrPriorityLabel).setVisibility(View.VISIBLE) ;
				break ;
			case 33:
				if (isExistingRecord) {
					// SRV Record
					((EditText) findViewById(R.id.rrAnswer)).setText(currentRR.getAnswer());
					((EditText) findViewById(R.id.rrTtlInput)).setText(currentRR.getTtl() + "");
					((EditText) findViewById(R.id.rrPriority)).setText(currentRR.getPriority() + "");
					((EditText) findViewById(R.id.rrSrvPort)).setText(currentRR.getPort() + "");
					((EditText) findViewById(R.id.rrSrvWeight)).setText(currentRR.getWeight() + "");
				}
				findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrPriority).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrPriorityLabel).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrSrvPortLabel).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrSrvPort).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrSrvWeightLabel).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrSrvWeight).setVisibility(View.VISIBLE) ;
				break ;
			default:
				if (isExistingRecord) {
					((EditText) findViewById(R.id.rrAnswer)).setText(currentRR.getAnswer());
					((EditText) findViewById(R.id.rrTtlInput)).setText(currentRR.getTtl() + "");
				}
				findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
		}
		((Spinner)findViewById(R.id.rrTypeSpinner)).setSelection(typeList.indexOf(ResourceRecord.getTypeAsString(currentRR.getType()))) ;

		((Spinner)findViewById(R.id.rrTypeSpinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			/* (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
			 */
			public void onItemSelected(AdapterView<?> recordTypeList, View selectedView, int position, long itemId) {

				findViewById(R.id.rrAnswer).setVisibility(View.GONE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrTtlInput).setVisibility(View.GONE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrPriority).setVisibility(View.GONE) ;
				findViewById(R.id.rrPriorityLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrSrvPortLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrSrvPort).setVisibility(View.GONE) ;
				findViewById(R.id.rrSrvWeightLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrSrvWeight).setVisibility(View.GONE) ;
				findViewById(R.id.rrExpire).setVisibility(View.GONE) ;
				findViewById(R.id.rrExpireLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrMinimum).setVisibility(View.GONE) ;
				findViewById(R.id.rrMinimumLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrRetryInterval).setVisibility(View.GONE) ;
				findViewById(R.id.rrRetryLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrPriority).setVisibility(View.GONE) ;
				findViewById(R.id.rrPriorityLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrResponsibleParty).setVisibility(View.GONE) ;
				findViewById(R.id.rrResponsiblePartyLabel).setVisibility(View.GONE) ;
				switch (ResourceRecord.getTypeForIdentifier((String)recordTypeList.getSelectedItem())) {
					case 6:
						// SOA Record
						findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrExpire).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrExpireLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrMinimum).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrMinimumLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrRetryInterval).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrRetryLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrPriority).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrPriorityLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrResponsibleParty).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrResponsiblePartyLabel).setVisibility(View.VISIBLE) ;
						break ;
					case 15:
						findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrPriority).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrPriorityLabel).setVisibility(View.VISIBLE) ;
						break ;
					case 33:
						findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrPriority).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrPriorityLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrSrvPortLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrSrvPort).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrSrvWeightLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrSrvWeight).setVisibility(View.VISIBLE) ;
						break ;
					default:
						findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				}
			}

			/* (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
			 */
			public void onNothingSelected(AdapterView<?> recordTypeList) {
				recordTypeList.setSelection(0) ;
			}
		}) ;
	}
}

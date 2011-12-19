/**
 * 
 */
package com.dns.mobile.activities.records;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xbill.DNS.Type;
import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.ResourceRecord;
import com.dns.mobile.util.LogoOnClickListener;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class RecordDetailActivity extends Activity {

	private static final String TAG = "RecordDetailActivity" ;
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
	private class RRSaveViaAPI extends AsyncTask<ResourceRecord, Void, JSONObject> {

		private int rrId = 0 ;

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected JSONObject doInBackground(ResourceRecord... params) {
			ResourceRecord rr = params[0] ;
			try {
				rrId = rr.getId().intValue() ;
			} catch (NumberFormatException nfe) {
				Log.e(TAG, "NumberFormatException trying to parse the RR ID", nfe) ;
			}
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()) ;
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

			// Replace this call with updateRRData
			if (isExistingRecord) {
				return api.updateRRData(rrId, rr.getAnswer(), rr.getTtl(), rr.getPriority(), rr.isWildcard(), rr.getRetry(), rr.getExpire(), rr.getMinimum(), rr.getWeight(), rr.getPort(), rr.getTitle(), rr.getKeywords(), rr.getDescription()) ;
			} else {
				JSONObject retVal = null ;
				switch (rr.getType()) {
					case Type.A:
						retVal = api.createARecord(rr.getDomainName(), rr.isGroup(), rr.getHostName(), rr.getAnswer(), rr.isWildcard(), rr.getGeoGroup(), rr.getCountryId()+"", rr.getRegionId()+"", rr.getCityId()+"", rr.getTtl()) ;
						break ;
					case Type.AAAA:
						retVal = api.createAAAARecord(rr.getDomainName(), rr.isGroup(), rr.getHostName(), rr.getAnswer(), rr.isWildcard(), rr.getGeoGroup(), rr.getCountryId()+"", rr.getRegionId()+"", rr.getCityId()+"", rr.getTtl()) ;
						break ;
					case Type.SOA:
						retVal = api.createSOARecord(rr.getDomainName(), rr.isGroup(), rr.getHostName(), rr.getAnswer(), rr.getRetry(), rr.getExpire(), rr.getMinimum(), rr.isWildcard(), rr.getGeoGroup(), rr.getCountryId()+"", rr.getRegionId()+"",rr.getCityId()+"", rr.getTtl()) ;
						break ;
					case Type.SRV:
						retVal = api.createSRVRecord(rr.getDomainName(), rr.isGroup(), rr.getHostName(), rr.getAnswer(), rr.getWeight(), rr.getPriority(), rr.getPort(), rr.isWildcard(), rr.getGeoGroup(), rr.getCountryId()+"", rr.getRegionId()+"", rr.getCountryId()+"", rr.getTtl()) ;
						break ;
					case Type.TXT:
						retVal = api.createTXTRecord(rr.getDomainName(), rr.isGroup(), rr.getHostName(), rr.getAnswer(), rr.isWildcard(), rr.getGeoGroup(), rr.getCountryId()+"", rr.getRegionId()+"", rr.getCityId()+"", rr.getTtl()) ;
						break ;
					case Type.MX:
						retVal = api.createMXRecord(rr.getDomainName(), rr.isGroup(), rr.getHostName(), rr.getAnswer(), rr.getPriority(), rr.isWildcard(), rr.getGeoGroup(), rr.getCountryId()+"", rr.getRegionId()+"", rr.getCityId()+"", rr.getTtl()) ;
						break ;
					case Type.CNAME:
						retVal = api.createCNAMERecord(rr.getDomainName(), rr.isGroup(), rr.getHostName(), rr.getAnswer(), rr.isWildcard(), rr.getGeoGroup(), rr.getCountryId()+"", rr.getRegionId()+"", rr.getCityId()+"", rr.getTtl()) ;
						break ;
					case 80000:
						retVal = api.createURL302Record(rr.getDomainName(), rr.isGroup(), rr.getHostName(), rr.getAnswer(), rr.isWildcard(), rr.getGeoGroup(), rr.getCountryId()+"", rr.getRegionId()+"", rr.getCityId()+"", rr.getTtl()) ;
						break ;
					case 80001:
						retVal = api.createURL301Record(rr.getDomainName(), rr.isGroup(), rr.getHostName(), rr.getAnswer(), rr.isWildcard(), rr.getGeoGroup(), rr.getCountryId()+"", rr.getRegionId()+"", rr.getCityId()+"", rr.getTtl()) ;
						break ;
					case 80002:
						retVal = api.createURLFrameRecord(rr.getDomainName(), rr.isGroup(), rr.getHostName(), rr.getAnswer(), rr.getTitle(), rr.getDescription(), rr.getKeywords(), rr.isWildcard(), rr.getGeoGroup(), rr.getCountryId()+"", rr.getRegionId()+"", rr.getCityId()+"", rr.getTtl()) ;
						break ;
					default:
						retVal = api.createARecord(rr.getDomainName(), rr.isGroup(), rr.getHostName(), rr.getAnswer(), rr.isWildcard(), rr.getGeoGroup(), rr.getCountryId()+"", rr.getRegionId()+"", rr.getCityId()+"", rr.getTtl()) ;
				}
				return retVal ;
			}
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
						AlertDialog.Builder builder = new AlertDialog.Builder(findViewById(R.id.rr_details_view).getContext()) ;
						builder.setTitle(R.string.rr_update_saved_title) ;
						builder.setMessage(R.string.rr_update_saved_body) ;
						builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss() ;
							}
						}) ;
						builder.show() ;
					} else {
						Log.e(TAG, "API Error: "+result.getJSONObject("meta").getString("error")) ;
						AlertDialog.Builder builder = new AlertDialog.Builder(findViewById(R.id.rr_details_view).getContext()) ;
						builder.setTitle(R.string.api_request_failed) ;
						builder.setMessage(result.getJSONObject("meta").getString("error")) ;
					}
				} catch (JSONException jsone) {
					Log.e(TAG, "JSONException encountered while trying to parse domain list.", jsone) ;
					AlertDialog.Builder builder = new AlertDialog.Builder(findViewById(R.id.rr_details_view).getContext()) ;
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
							Log.d(TAG, "Adding RR '"
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
					Log.d(TAG, "Finished parsing JSON response into ResourceRecord") ;
				} catch (JSONException jsone) {
					Log.e(TAG, "JSONException encountered while trying to parse rr details.", jsone) ;
				}
			} else {
				Log.e(TAG, "API Call failed.") ;
				AlertDialog.Builder builder = new AlertDialog.Builder(findViewById(R.id.rr_details_view).getContext()) ;
				builder.setTitle(R.string.api_request_failed) ;
				builder.setMessage(result.toString()) ;
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
		if (currentRR.getAnswer()==null) {
			currentRR.setType(1) ;
			isExistingRecord = false ;
		}
		hostName = currentRR.getHostName() ;
		domainName = currentRR.getDomainName() ;
		Log.d(TAG,"Setting host/domain name to: "+hostName+"/"+domainName+" and displaying RR: "+currentRR.getId()) ;

		((Button)findViewById(R.id.rrSaveButton)).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				currentRR.setType(Type.value(((Spinner)findViewById(R.id.rrTypeSpinner)).getSelectedItem().toString())) ;
				currentRR.setActive(true) ;
				currentRR.setAnswer(((EditText)findViewById(R.id.rrAnswer)).getText().toString()) ;
				currentRR.setTtl(Integer.parseInt(((EditText)findViewById(R.id.rrTtlInput)).getText().toString())) ;
				switch(currentRR.getType()) {
					case 6:
						currentRR.setAnswer(((EditText)findViewById(R.id.rrResponsibleParty)).getText().toString()) ;
						currentRR.setMinimum(Integer.parseInt(((EditText)findViewById(R.id.rrMinimum)).getText().toString())) ;
						currentRR.setExpire(Integer.parseInt(((EditText)findViewById(R.id.rrExpire)).getText().toString())) ;
						currentRR.setRetry(Integer.parseInt(((EditText)findViewById(R.id.rrRetryInterval)).getText().toString())) ;
						break ;
					case 15:
						currentRR.setPriority(Integer.parseInt(((EditText)findViewById(R.id.rrPriority)).getText().toString())) ;
						break ;
					case 33:
						currentRR.setPort(Integer.parseInt(((EditText)findViewById(R.id.rrSrvPort)).getText().toString())) ;
						currentRR.setPriority(Integer.parseInt(((EditText)findViewById(R.id.rrPriority)).getText().toString())) ;
						currentRR.setWeight(Integer.parseInt(((EditText)findViewById(R.id.rrSrvWeight)).getText().toString())) ;
						break ;
					case 80000:
						break ;
					case 80001:
						break ;
					case 80002:
						break ;
					default:
						
				}
				new RRSaveViaAPI().execute(currentRR) ;
			}
		}) ;

		findViewById(R.id.dnsLogo).setOnClickListener(new LogoOnClickListener(this));

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
					((EditText)findViewById(R.id.rrExpire)).setText(currentRR.getExpire()==null?"1209600":currentRR.getExpire()+"") ;
					((EditText)findViewById(R.id.rrMinimum)).setText(currentRR.getMinimum()==null?"1800":currentRR.getMinimum()+"") ;
					((EditText)findViewById(R.id.rrRetryInterval)).setText(currentRR.getRetry()==null?"1200":currentRR.getRetry()+"") ;
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

	/* (non-Javadoc)
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		currentRR = (ResourceRecord) intent.getSerializableExtra("rrData") ;
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

package com.dns.mobile.activities.records;

import org.json.JSONException;
import org.json.JSONObject;
import org.xbill.DNS.Type;
import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.ResourceRecord;
import com.dns.mobile.util.LogoOnClickListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

public class CreateNewHostActivity extends Activity {

	private static String TAG = "CreateNewHostActivity" ;
	protected ResourceRecord currentRR = new ResourceRecord() ;
	protected String domainName = null ;
	protected boolean isExistingRecord = false ;
	protected ProgressDialog busyDialog = null ;

	/**
	 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
	 *
	 */
	private final class RecordTypeSelectHandler implements
			AdapterView.OnItemSelectedListener {
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
	}

	/**
	 * A class which extends the AsyncTask AbstractClass to perform network operations in the background.
	 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
	 *
	 */
	private class RRSaveAdvancedViaAPI extends AsyncTask<ResourceRecord, Void, JSONObject> {

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

			if (isExistingRecord) {
				return api.updateRRData(rrId, rr.getAnswer(), rr.getTtl(), rr.getPriority(), rr.isWildcard(), rr.getRetry(), rr.getExpire(), rr.getMinimum(), rr.getWeight(), rr.getPort(), rr.getTitle(), rr.getKeywords(), rr.getDescription()) ;
			} else {
				JSONObject hostResult = api.createHostname(rr.getDomainName(), rr.isGroup(), rr.getHostName(), (rr.getType()>=80000), null) ;
				boolean hostCreated = false ;

				if (hostResult.has("meta")) {
					try {
						if (hostResult.getJSONObject("meta").getInt("success")==1) {
							hostCreated = true ;
						}
					} catch (JSONException e) {
						Log.e(TAG, "JSONException while trying to parse host creation response.", e) ;
						Log.d(TAG, hostResult.toString()) ;
						hostCreated = false ;
					}
				}

				if (hostCreated) {
					JSONObject retVal = new JSONObject() ;
					Log.d(TAG, "Creating record of type: "+ResourceRecord.getTypeAsString(rr.getType())+"("+rr.getType()+")") ;
					switch (rr.getType()) {
						case Type.A:
							retVal = api.createARecord(rr.getDomainName(), rr.isGroup(), rr.getHostName(), rr.getAnswer(), rr.isWildcard(), rr.getGeoGroup(), rr.getCountryId()+"", rr.getRegionId()+"", rr.getCityId()+"", rr.getTtl()) ;
							break ;
						case Type.NS:
							retVal = api.createNSRecord(rr.getDomainName(), rr.isGroup(), rr.getHostName(), rr.getAnswer(), rr.isWildcard(), rr.getGeoGroup(), rr.getCountryId()+"", rr.getRegionId()+"", rr.getCityId()+"", rr.getTtl()) ;
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
				} else {
					return hostResult ;
				}
			}
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			if (busyDialog!=null) {
				busyDialog.dismiss() ;
			}
			if (result.has("meta")) {
				try {
					if (result.getJSONObject("meta").getInt("success")==1) {
						currentRR.setId(result.getJSONObject("meta").getLong("id")) ;
						AlertDialog.Builder builder = new AlertDialog.Builder(findViewById(R.id.scrollView1).getContext()) ;
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
						AlertDialog.Builder builder = new AlertDialog.Builder(findViewById(R.id.scrollView1).getContext()) ;
						builder.setTitle(R.string.api_request_failed) ;
						builder.setMessage(result.getJSONObject("meta").getString("error")) ;
					}
				} catch (JSONException jsone) {
					Log.e(TAG, "JSONException encountered while trying to parse domain list.", jsone) ;
					AlertDialog.Builder builder = new AlertDialog.Builder(findViewById(R.id.scrollView1).getContext()) ;
					builder.setTitle(R.string.api_request_failed) ;
					builder.setMessage(jsone.getLocalizedMessage()) ;
				}
			} else {
				Log.e(TAG, "API Call failed.") ;
				AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewHostActivity.this) ;
				builder.setTitle(R.string.api_request_failed) ;
				builder.setMessage(result.toString()) ;
			}

			findViewById(R.id.rrSaveButton).setClickable(true) ;
			findViewById(R.id.rrSaveButton).setEnabled(true) ;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_host_layout) ;
		currentRR.setDomainName(this.getIntent().getStringExtra("domainName")) ;
		currentRR.setGroup(this.getIntent().getBooleanExtra("isDomainGroup", false)) ;

		domainName = this.getIntent().getStringExtra("domainName") ;
		
		findViewById(R.id.dnsLogo).setOnClickListener(new LogoOnClickListener(this));
		Log.d(TAG, "domainName: "+domainName) ;
		TextView newHostHeader = (TextView)findViewById(R.id.newHostHeader) ;
		String hostHeaderContent = newHostHeader.getText().toString()+": "+domainName ;
		newHostHeader.setText(hostHeaderContent) ;

		((Spinner)findViewById(R.id.hostType)).setOnItemSelectedListener(new RecordTypeSelectHandler()) ;

		Button saveButton = (Button) findViewById(R.id.rrSaveButton) ;
		saveButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) { // The user clicked the save button.
				v.setEnabled(false) ;
				v.setClickable(false) ;
				busyDialog = new ProgressDialog(CreateNewHostActivity.this) ;
				busyDialog.setTitle(R.string.saving) ;
				busyDialog.setIndeterminate(true) ;
				busyDialog.show() ;
				currentRR.setType(ResourceRecord.getTypeForIdentifier(((Spinner)findViewById(R.id.hostType)).getSelectedItem().toString())) ;
				currentRR.setHostName(((EditText)findViewById(R.id.newHostName)).getText().toString()) ;
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
						
				}
				new RRSaveAdvancedViaAPI().execute(currentRR);
			}
		}) ;
	}
}

package com.dns.mobile.activities.domains;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.json.JSONException;
import org.json.JSONObject;
import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.Domain;
import com.dns.mobile.util.LogoOnClickListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

public class CreateNewDomainActivity extends Activity {

	private static final String TAG = "CreateNewDomainActivity" ;

	/**
	 * Handle the API call to create the domain/set up XFR in the background
	 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
	 *
	 */
	private class CreateDomainAsyncTask extends AsyncTask<Domain, Void, JSONObject> {
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected JSONObject doInBackground(Domain... params) {
			Domain newDomain = params[0] ;
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

			return api.createDomain(newDomain.isGroupedDomain()?"group":"advanced", newDomain.getName(), newDomain.isGroupedDomain()?newDomain.getDomainGroup():null, newDomain.getrName(), null, null, null, null) ;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			boolean apiRequestSucceeded = false ;
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
					Log.e(TAG, "JSONException encountered while trying to parse domain group list.", jsone) ;
					AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext()) ;
					builder.setTitle(R.string.api_request_failed) ;
					builder.setMessage(jsone.getLocalizedMessage()) ;
				}
			}

			if (apiRequestSucceeded) {

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

		setContentView(R.layout.new_domain_layout) ;
		findViewById(R.id.dnsLogo).setOnClickListener(new LogoOnClickListener(this));
		((TextView)findViewById(R.id.headerLabel)).setText(R.string.create_new_domain_label) ;

		findViewById(R.id.viewRefreshProgressBar).setVisibility(View.GONE) ;

		((CheckBox)findViewById(R.id.setSlave)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					findViewById(R.id.masterLabel).setVisibility(View.VISIBLE) ;
					findViewById(R.id.masterAddr).setVisibility(View.VISIBLE) ;
					findViewById(R.id.ttlLabel).setVisibility(View.VISIBLE) ;
					findViewById(R.id.ttlValue).setVisibility(View.VISIBLE) ;
				} else {
					findViewById(R.id.masterLabel).setVisibility(View.GONE) ;
					findViewById(R.id.masterAddr).setVisibility(View.GONE) ;
					findViewById(R.id.ttlLabel).setVisibility(View.GONE) ;
					findViewById(R.id.ttlValue).setVisibility(View.GONE) ;
				}
			}
		}) ;

		((Button)findViewById(R.id.saveNewDomain)).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Domain newDomain = new Domain() ;
				newDomain.setName(((EditText)findViewById(R.id.domainName)).getText().toString()) ;
				boolean informationIsValid = true ;
				if (!newDomain.getName().matches("[a-zA-Z0-9]*(\\.[a-z]*)?")) {
					informationIsValid = false ;
					findViewById(R.id.domainName).requestFocus() ;
				}
				newDomain.setrName(((EditText)findViewById(R.id.responsibleParty)).getText().toString()) ;
				if (!newDomain.getrName().matches("") && informationIsValid) {
					informationIsValid = false ;
					findViewById(R.id.responsibleParty).requestFocus() ;
				}
				newDomain.setXfr(((CheckBox) findViewById(R.id.setSlave)).isChecked()) ;
				if (newDomain.isXfr()) {
					newDomain.setMaster(((EditText)findViewById(R.id.masterAddr)).getText().toString()) ;
					try {
						InetAddress.getByName(newDomain.getMaster()) ;
					} catch (UnknownHostException uhe) {
						informationIsValid = false ;
						findViewById(R.id.masterAddr).requestFocus() ;
					}

					try {
						newDomain.setRefresh(Integer.parseInt(((EditText)findViewById(R.id.ttlValue)).getText().toString())) ;
					} catch (NumberFormatException nfe) {
						informationIsValid = false ;
						findViewById(R.id.ttlValue).requestFocus() ;
					}
				}
				
				if (informationIsValid) {
					new CreateDomainAsyncTask().execute(newDomain) ;
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext()) ;
					builder.setTitle(R.string.add_domain_input_error_title) ;
					builder.setMessage(R.string.add_domain_input_error_message) ;
					builder.setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {
						/* (non-Javadoc)
						 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
						 */
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss() ;
						}
					}) ;
					builder.show() ;
				}
			}
		}) ;

		// When not focused on one of the text fields, hide the keyboard.
		((EditText)findViewById(R.id.domainName)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			public void onFocusChange(View v, boolean hasFocus) {
				InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		}) ;

		((EditText)findViewById(R.id.responsibleParty)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			public void onFocusChange(View v, boolean hasFocus) {
				InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		}) ;

		((EditText)findViewById(R.id.masterAddr)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			public void onFocusChange(View v, boolean hasFocus) {
				InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		}) ;

		((EditText)findViewById(R.id.ttlValue)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			public void onFocusChange(View v, boolean hasFocus) {
				InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		}) ;
	}
}

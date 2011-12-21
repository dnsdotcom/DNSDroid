package com.dns.mobile.activities.groups;

import org.json.JSONException;
import org.json.JSONObject;
import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
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
import android.widget.Button;
import android.widget.EditText;

public class CreateNewDomainGroup extends Activity {

	private static final String TAG = "CreateNewDomainGroup" ;
	protected ProgressDialog busyDialog = null ;

	private class CreateDomainGroupTask extends AsyncTask<String, Void, String> {
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(String... params) {
			String selected = params[0] ;
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
			JSONObject result = api.createDomainGroup(selected, null, null, null, null, null) ;

			try {
				if (result.has("meta")) {
					if (result.getJSONObject("meta").getInt("success")==1) {
						return "SUCCESS" ;
					} else {
						return result.getJSONObject("meta").getString("error") ;
					}
				} else {
					return getResources().getString(R.string.api_request_failed) ;
				}
			} catch (JSONException jsone) {
				Log.e(TAG, "JSONException encountered checking results of domainGroup creation.", jsone) ;
				return jsone.getLocalizedMessage() ;
			}
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (busyDialog!=null) {
				busyDialog.dismiss() ;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewDomainGroup.this) ;
			if (result.contentEquals("SUCCESS")) {
				builder.setTitle(R.string.domain_group_created_title) ;
				builder.setMessage(R.string.domain_group_created_message) ;
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
						CreateNewDomainGroup.this.finish() ;
					}
				}) ;
			} else {
				builder.setTitle(R.string.error) ;
				builder.setMessage(result) ;
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
					}
				}) ;
			}
			builder.show() ;
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.new_domain_group_activity) ;

	    Button createGroup = (Button) findViewById(R.id.createNewGroupButton) ;
	    createGroup.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				EditText nameInput = (EditText) findViewById(R.id.newGroupName) ;
				if (nameInput.getText().toString().length()<4) {
					AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewDomainGroup.this) ;
					builder.setTitle(R.string.domain_group_name_too_short_title) ;
					builder.setMessage(R.string.domain_group_name_too_short_message) ;
					builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss() ;
						}
					}) ;
					builder.show() ;
				} else {
					busyDialog = new ProgressDialog(CreateNewDomainGroup.this) ;
					busyDialog.setTitle(R.string.saving) ;
					busyDialog.show();
					new CreateDomainGroupTask().execute(nameInput.getText().toString()) ;
				}
			}
		}) ;
	}

}

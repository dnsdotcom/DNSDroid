package com.dns.mobile.activities.groups;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.Domain;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

public class DomainGroupMembersActivity extends Activity {

	private final static String TAG = "DomainGroupMembersActivity" ;
	protected String domainGroupName = null ;
	protected ArrayList<Domain> memberDomains = null ;

	private class DomainGroupMemberTask extends AsyncTask<String, Void, ArrayList<Domain>> {
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected ArrayList<Domain> doInBackground(String... params) {
			String dgName = params[0] ;
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

			Log.d(TAG, "Sending API request to '"+apiHost+"'") ;
			ManagementAPI api = new ManagementAPI(apiHost, useSSL, settings.getString("auth.token", "")) ;
			JSONObject domainGroups = api.getDomainsInGroup(dgName) ;
			Log.d(TAG, "Got API response with content: \n"+domainGroups.toString()) ;

			ArrayList<Domain> domainArray = new ArrayList<Domain>() ;
			try {
				if (domainGroups.has("meta")) {
					JSONArray jsonDomains = domainGroups.getJSONArray("") ;
					for (int x=0; x<jsonDomains.length(); x++) {
						JSONObject entry = jsonDomains.getJSONObject(x) ;
						Domain current = new Domain() ;
						current.setDomainGroup(dgName) ;
						current.setDomainId(entry.getInt("id")) ;

						domainArray.add(current) ;
					}
				}
			} catch (JSONException jsone) {
				Log.e(TAG, "", jsone) ;
				domainArray = null ;
			}
			
			return domainArray ;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(ArrayList<Domain> result) {
			super.onPostExecute(result);
			memberDomains = result ;
			if (memberDomains!=null) {
				// The API call succeeded and we should now update the UI
			} else {
				// The API call failed to retrieve the list of member domains.
			}
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.domain_group_members) ;

	    domainGroupName = this.getIntent().getStringExtra("domainGroupName") ;
	    Log.d(TAG, "Preparing to display members of domain group '"+domainGroupName+"'") ;

	    ((TextView)findViewById(R.id.domainGroupLabel)).setText(domainGroupName) ;

	    new DomainGroupMemberTask().execute(domainGroupName) ;
	}

}

/**
 * 
 */
package com.dns.mobile.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dns.api.compiletime.ManagementAPI;
import com.dns.mobile.R;
import com.dns.mobile.data.DomainGroup;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class DomainGroupListTask extends AsyncTask<Void, Void, JSONObject> {

	private static final String TAG = "DomainGroupListTask" ;
	private Activity parent = null ;
	private View listView = null ;
	private ArrayList<DomainGroup> domainGroups = null ;

	/**
	 * 
	 */
	public DomainGroupListTask(Activity parent, ArrayList<DomainGroup> domainGroups, View listView) {
		super() ;
		this.parent = parent ;
		this.domainGroups = domainGroups ;
		this.listView = listView ;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected JSONObject doInBackground(Void... params) {
		domainGroups.clear() ;
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(parent.getApplicationContext());
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

		return api.getDomainGroups("") ;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		boolean apiRequestSucceeded = false ;
		parent.findViewById(R.id.viewRefreshProgressBar).setVisibility(View.GONE) ;
		parent.findViewById(R.id.viewRefreshButton).setVisibility(View.VISIBLE) ;
		if (result.has("meta")) {
			try {
				if (result.getJSONObject("meta").getInt("success")==1) {
					apiRequestSucceeded = true ;
				} else {
					Log.e(TAG, "API Error: "+result.getJSONObject("meta").getString("error")) ;
					AlertDialog.Builder builder = new AlertDialog.Builder(parent.getBaseContext()) ;
					builder.setTitle(R.string.api_request_failed) ;
					builder.setMessage(result.getJSONObject("meta").getString("error")) ;
				}
			} catch (JSONException jsone) {
				Log.e(TAG, "JSONException encountered while trying to parse domain group list.", jsone) ;
				AlertDialog.Builder builder = new AlertDialog.Builder(parent.getBaseContext()) ;
				builder.setTitle(R.string.api_request_failed) ;
				builder.setMessage(jsone.getLocalizedMessage()) ;
			}
		}

		if (apiRequestSucceeded) {
			try {
				JSONArray data = result.getJSONArray("data") ;
				for (int x=0; x<data.length(); x++) {
					JSONObject currentData = data.getJSONObject(x) ;
					DomainGroup currentGroup = new DomainGroup() ;
					Log.d(TAG, "Adding group '"+currentData.getString("name")+"' to domainGroupList") ;
					currentGroup.setGroupId(currentData.getLong("id")) ;
					currentGroup.setName(currentData.getString("name")) ;
					currentGroup.setMembers(currentData.getLong("num_domains")) ;
					domainGroups.add(currentGroup) ;
				}
				listView.setEnabled(true) ;
				listView.invalidate() ;
				Log.d(TAG, "Finished parsing JSON response into domainList") ;
			} catch (JSONException jsone) {
				Log.e(TAG, "JSONException encountered while trying to parse domain list.", jsone) ;
			}
		} else {
			parent.showDialog(R.string.api_request_failed) ;
		}
	}

}

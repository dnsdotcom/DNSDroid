package com.dns.mobile.activities;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.DomainGroup;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * An <code>Activity</code> which shows a list of domains for the given user's API token
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class DomainGroupsListActivity extends Activity {

	protected ArrayList<DomainGroup> domainGroupList = null ;

	private class DomainGroupListApiTask extends AsyncTask<Void, Void, JSONObject> {
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected JSONObject doInBackground(Void... params) {
			findViewById(R.id.groupListView).setVisibility(View.GONE) ;
			findViewById(R.id.groupListProgressBar).setVisibility(View.VISIBLE) ;
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

			return api.getDomainGroups("") ;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			boolean apiRequestSucceeded = false ;
			findViewById(R.id.groupListProgressBar).setVisibility(View.GONE) ;
			if (result.has("meta")) {
				try {
					if (result.getJSONObject("meta").getInt("success")==1) {
						apiRequestSucceeded = true ;
					} else {
						Log.e("DomainGroupsListActivity", "API Error: "+result.getJSONObject("meta").getString("error")) ;
						AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext()) ;
						builder.setTitle(R.string.api_request_failed) ;
						builder.setMessage(result.getJSONObject("meta").getString("error")) ;
					}
				} catch (JSONException jsone) {
					Log.e("DomainGroupsListActivity", "JSONException encountered while trying to parse domain group list.", jsone) ;
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
						DomainGroup currentGroup = new DomainGroup() ;
						Log.d("DomainGroupsListActivity", "Adding group '"+currentData.getString("name")+"' to domainGroupList") ;
						currentGroup.setGroupId(currentData.getLong("id")) ;
						currentGroup.setName(currentData.getString("name")) ;
						currentGroup.setMembers(currentData.getLong("num_domains")) ;
						domainGroupList.add(currentGroup) ;
					}
					findViewById(R.id.groupListView).setVisibility(View.VISIBLE) ;
					((ListView)findViewById(R.id.groupListView)).invalidateViews() ;
					Log.d("DomainGroupsListActivity", "Finished parsing JSON response into domainList") ;
				} catch (JSONException jsone) {
					Log.e("DomainGroupsListActivity", "JSONException encountered while trying to parse domain list.", jsone) ;
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
		setContentView(R.layout.domain_groups_activity) ;
		domainGroupList = new ArrayList<DomainGroup>() ;

		ListView domainListView = (ListView) findViewById(R.id.groupListView) ;
		domainListView.setBackgroundResource(R.drawable.list_view_color_states) ;
		domainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> domainListView, View selectedView, int position, long itemId) {
				DomainGroup selected = domainGroupList.get(position-1) ;
				Intent hostListIntent = new Intent(getApplicationContext(), DomainHostsActivity.class) ;
				hostListIntent.putExtra("domainName", selected.getName()) ;
				startActivity(hostListIntent) ;
			}
			
		}) ;
		domainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> domainListView, View selectedView, int position, long itemId) {
				
				return false;
			}
			
		}) ;

		domainListView.setAdapter(new BaseAdapter() {
			
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView domainItem = new TextView(parent.getContext()) ;
				domainItem.setTextColor(Color.WHITE) ;
				domainItem.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10) ;
				domainItem.setBackgroundColor(Color.TRANSPARENT) ;
				domainItem.setWidth(LayoutParams.FILL_PARENT) ;
				if (position==0) {
					domainItem.setText("[New Group]") ;
				} else {
					DomainGroup currentDomain = domainGroupList.get(position-1);
					domainItem.setText(currentDomain.getName());
				}
				return domainItem ;
			}
			
			public long getItemId(int position) {
				return position+400 ;
			}
			
			public Object getItem(int position) {
				return domainGroupList.get(position-1) ;
			}
			
			public int getCount() {
				return domainGroupList.size()+1 ;
			}
		}) ;

		new DomainGroupListApiTask().execute() ;
	}
}

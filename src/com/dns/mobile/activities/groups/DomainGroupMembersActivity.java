package com.dns.mobile.activities.groups;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.Domain;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DomainGroupMembersActivity extends Activity {

	private final static String TAG = "DomainGroupMembersActivity" ;
	protected String domainGroupName = null ;
	protected ArrayList<Domain> memberDomains = new ArrayList<Domain>() ;

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
					JSONArray jsonDomains = domainGroups.getJSONArray("data") ;
					for (int x=0; x<jsonDomains.length(); x++) {
						JSONObject entry = jsonDomains.getJSONObject(x) ;
						Domain current = new Domain() ;
						current.setDomainGroup(dgName) ;
						current.setDomainId(entry.getInt("id")) ;
						current.setGroupedDomain(true) ;
						current.setName(entry.getString("name")) ;

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
			findViewById(R.id.groupMemberProgress).setVisibility(View.GONE) ;
			findViewById(R.id.domainGroupMemberList).setVisibility(View.VISIBLE) ;
			memberDomains = result ;
			if (memberDomains!=null) {
				Log.d(TAG, "Loading member domains list into the ListView") ;
				memberDomains = result ;
				((ListView)DomainGroupMembersActivity.this.findViewById(R.id.domainGroupMemberList)).invalidateViews() ;
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(DomainGroupMembersActivity.this) ;
				builder.setTitle(R.string.group_member_api_failed_title) ;
				builder.setMessage(R.string.group_member_api_failed_message) ;
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						DomainGroupMembersActivity.this.finish() ;
					}
				}) ;
				builder.show() ;
			}

		    ListView memberListView = (ListView) findViewById(R.id.domainGroupMemberList) ;
		    memberListView.setAdapter(new GroupMembersListAdapter()) ;
		}
	}

	private class GroupMembersListAdapter extends BaseAdapter {

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		public int getCount() {
			Log.d(TAG, "Adapter retrieved count") ;
			if (memberDomains!=null) {
				return memberDomains.size() ;
			} else {
				return 0 ;
			}
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		public Object getItem(int position) {
			if (memberDomains!=null) {
				Log.d(TAG, "Adapter retrieved object.") ;
				return memberDomains.get(position) ;
			} else {
				Log.d(TAG, "Adapter retrieved NULL.") ;
				return null ;
			}
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		public long getItemId(int position) {
			Log.d(TAG, "Adapter retrieved item ID.") ;
			return memberDomains.get(position).getDomainId() ;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			if (!LinearLayout.class.isInstance(convertView)) {
				convertView = new TextView(DomainGroupMembersActivity.this) ;
			}
			Domain selectedDomain = memberDomains.get(position) ;
			String domainLabel = selectedDomain.getName() ;
			Log.d(TAG, "Adapter retrieved View for '"+domainLabel+"'.") ;

			TextView newLayout = (TextView) convertView ;
			newLayout.setTextColor(Color.WHITE) ;
			newLayout.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25) ;
			newLayout.setBackgroundColor(Color.TRANSPARENT) ;
			newLayout.setText(domainLabel) ;

			return newLayout ;
		}
		
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.domain_group_members) ;

	    domainGroupName = this.getIntent().getStringExtra("domainGroupName") ;
	    String groupMembersActivityLabel = getResources().getString(R.string.group_member_activity_label) ;
	    ((TextView)findViewById(R.id.domainGroupLabel)).setText(groupMembersActivityLabel+" "+domainGroupName) ;
	    Log.d(TAG, "Preparing to display members of domain group '"+domainGroupName+"'") ;

	    new DomainGroupMemberTask().execute(domainGroupName) ;
	}

	
}

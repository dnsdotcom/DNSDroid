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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

			try {
				if (domainGroups.has("meta")) {
					if (domainGroups.getJSONObject("meta").getInt("success")==1) {
						ArrayList<Domain> domainArray = new ArrayList<Domain>() ;
						if (domainGroups.has("data")) {
							JSONArray jsonDomains = domainGroups
									.getJSONArray("data");
							for (int x = 0; x < jsonDomains.length(); x++) {
								JSONObject entry = jsonDomains.getJSONObject(x);
								Domain current = new Domain();
								current.setDomainGroup(dgName);
								current.setDomainId(entry.getInt("id"));
								current.setGroupedDomain(true);
								current.setName(entry.getString("name"));

								domainArray.add(current);
							}
						}
						return domainArray ;
					}
				}
			} catch (JSONException jsone) {
				Log.e(TAG, "", jsone) ;
			}
			
			return null ;
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
				return memberDomains.size()+1 ;
			} else {
				return 1 ;
			}
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		public Object getItem(int position) {
			if (memberDomains!=null) {
				Log.d(TAG, "Adapter retrieved object.") ;
				if (position>0) {
					return memberDomains.get(position - 1);
				} else {
					return null ;
				}
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
			return position+200 ;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(TAG, "Getting view for position: "+position) ;
			if (!LinearLayout.class.isInstance(convertView)) {
				convertView = new TextView(DomainGroupMembersActivity.this) ;
			}
			String domainLabel ;
			if (position>0) {
				Log.d(TAG, "member list has '"+memberDomains.size()+"' elements and we are selecting element: "+(position-1)) ;
				Domain selectedDomain = memberDomains.get(position-1);
				domainLabel = selectedDomain.getName();
			} else {
				domainLabel = "[Add Domain]" ;
			}
			Log.d(TAG, "Adapter retrieved View for '"+domainLabel+"'.") ;

			TextView newLayout = (TextView) convertView ;
			newLayout.setTextColor(Color.WHITE) ;
			newLayout.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25) ;
			newLayout.setBackgroundColor(Color.TRANSPARENT) ;
			newLayout.setText(domainLabel) ;

			return newLayout ;
		}
		
	}

	private class DomainGroupMemberDeleteTask extends AsyncTask<Domain, Void, Domain> {
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Domain doInBackground(Domain... params) {
			Domain selected = params[0] ;
			selected.setGroupedDomain(true) ;
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
			Log.d(TAG, "Sending API request to remove '"+selected.getName()+"' from domain group '"+domainGroupName+"'") ;
			JSONObject result = api.assignDomainMode(selected.getName(), "advanced", null) ;

			try {
				if (result.has("meta")) {
					if (result.getJSONObject("meta").getInt("success")==1) {
						selected.setGroupedDomain(false) ;
					}
				}
			} catch (JSONException jsone) {
				Log.e(TAG, "JSONException encountered parsing domain group removal result", jsone) ;
			}

			return selected ;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Domain result) {
			super.onPostExecute(result);
			findViewById(R.id.groupMemberProgress).setVisibility(View.GONE) ;
			if (result.isGroupedDomain()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(DomainGroupMembersActivity.this) ;
				String message = "'"+result.getName()+"' "+getResources().getString(R.string.remove_group_member_fail_message)+" '"+domainGroupName+"'" ;
				builder.setTitle(R.string.remove_group_member_fail_title) ;
				builder.setMessage(message) ;
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
					}
				}) ;
			} else {
				int domainIndex = memberDomains.indexOf(result) ;
				memberDomains.remove(domainIndex) ;
				((ListView) findViewById(R.id.domainGroupMemberList)).invalidateViews() ;
			}
			findViewById(R.id.domainGroupMemberList).setVisibility(View.VISIBLE) ;
		}
	}

	private class DomainItemClickListener implements AdapterView.OnItemClickListener {
		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			if (position==0) {
				Intent addDomainToGroup = new Intent(DomainGroupMembersActivity.this, GroupedDomainSelectActivity.class) ;
				addDomainToGroup.putExtra("domainGroup", domainGroupName) ;
				startActivity(addDomainToGroup) ;
			}
		}
	}

	private class DomainItemLongClickListener implements AdapterView.OnItemLongClickListener {

		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long itemId) {
			GroupMembersListAdapter adapter = (GroupMembersListAdapter) arg0.getAdapter() ;
			final Domain selectedMember = (Domain) adapter.getItem(position) ;
			AlertDialog.Builder builder = new AlertDialog.Builder(DomainGroupMembersActivity.this) ;
			String message = "'"+selectedMember.getName()+"'"+getResources().getString(R.string.group_member_delete_dialog_message) ;
			builder.setTitle(R.string.group_member_delete_dialog_title) ;
			builder.setMessage(message) ;
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					findViewById(R.id.domainGroupMemberList).setVisibility(View.INVISIBLE) ;
					findViewById(R.id.groupMemberProgress).setVisibility(View.VISIBLE) ;
					new DomainGroupMemberDeleteTask().execute(selectedMember) ;
				}
			}) ;

			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss() ;
				}
			}) ;
			builder.show() ;
			return true ;
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

	    ListView dgListView = (ListView) findViewById(R.id.domainGroupMemberList) ; 
	    dgListView.setAdapter(new GroupMembersListAdapter()) ;
	    dgListView.setOnItemLongClickListener(new DomainItemLongClickListener()) ;
	    dgListView.setOnItemClickListener(new DomainItemClickListener()) ;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem refreshDomains = menu.add(Menu.NONE, 0, 0, "Refresh");
		refreshDomains.setIcon(R.drawable.ic_menu_refresh) ;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				findViewById(R.id.domainGroupMemberList).setVisibility(View.GONE) ;
				findViewById(R.id.groupMemberProgress).setVisibility(View.VISIBLE) ;
				new DomainGroupMemberTask().execute(domainGroupName) ;
				return true;
		}
		return false;
	}
}

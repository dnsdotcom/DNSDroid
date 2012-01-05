package com.dns.mobile.activities.groups;

import java.util.ArrayList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.json.JSONException;
import org.json.JSONObject;

import com.dns.api.compiletime.ManagementAPI;
import com.dns.mobile.R;
import com.dns.mobile.data.DomainGroup;
import com.dns.mobile.util.DomainGroupListTask;
import com.dns.mobile.util.LogoOnClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * An <code>Activity</code> which shows a list of domains for the given user's API token
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class DomainGroupsListActivity extends Activity {

	private static final String TAG = "DomainGroupsListActivity" ;
	protected ArrayList<DomainGroup> domainGroupList = null ;
	protected ListView groupListView = null ;
	protected Dialog busyDialog = null ;

	private class DomainGroupDeleteTask extends AsyncTask<String, Void, DomainGroup> {
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected DomainGroup doInBackground(String... params) {

			final String groupName = params[0] ;

			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(DomainGroupsListActivity.this.getApplicationContext());
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
			JSONObject result = api.removeDomainGroup(params[0], true) ;
			DomainGroup failure = new DomainGroup() ;
			failure.setGroupId(-1) ;
			failure.setName(groupName) ;
			if (result.has("meta")) {
				Log.d(TAG, "API result has 'meta' stanza") ;
				try {
					if (result.getJSONObject("meta").has("success")) {
						Log.d(TAG, "API result has 'success' object.") ;
						if (result.getJSONObject("meta").getInt("success")==1) {
							Log.d(TAG, "API result says that the request was successful") ;
							ArrayList<DomainGroup> filtered = new ArrayList<DomainGroup>() ;
							filtered.addAll(domainGroupList) ;
							CollectionUtils.filter(filtered, new Predicate() {
								
								public boolean evaluate(Object object) {
									DomainGroup dg = (DomainGroup) object ;
									if (dg.getName().contentEquals(groupName)) {
										return true ;
									}
									return false;
								}
							}) ;

							return filtered.get(0) ;
						} else {
							failure.setErrorMessage(result.getJSONObject("meta").getString("error")) ;
						}
					} else {
						failure.setErrorMessage(getResources().getString(R.string.api_result_invalid)+": "+result.toString()) ;
					}
				} catch (JSONException e) {
					Log.e(TAG, "JSONException encountered while checking the results of the API request.", e) ;
				}
			} else {
				failure.setErrorMessage(getResources().getString(R.string.unk_api_error)) ;
			}
			return failure;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(DomainGroup result) {
			super.onPostExecute(result);
			if (result.getGroupId()!=-1) {
				Log.d(TAG, "Domain group '"+result.getName()+"' was successfully deleted.") ;
				Log.d(TAG, "Domain group count before: "+domainGroupList.size()) ;
				domainGroupList.remove(result);
				Log.d(TAG, "Domain group count after: "+domainGroupList.size()) ;
				groupListView.invalidate();
				busyDialog.dismiss();
			} else {
				Log.d(TAG, "Domain group was not successfully deleted.") ;
				AlertDialog.Builder builder1 = new AlertDialog.Builder(DomainGroupsListActivity.this) ;
				String alertTitle = getResources().getString(R.string.dg_delete_failed_title)+" '"+result.getName()+"'" ;
				builder1.setTitle(alertTitle) ;
				String alertMsg = getResources().getString(R.string.dg_delete_failed_message)+": "+result.getErrorMessage() ;
				builder1.setMessage(alertMsg) ;
				builder1.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
					}
				}) ;
				builder1.show() ;
			}
		}
	}

	private class DomainGroupAdapter extends BaseAdapter {

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView domainItem = new TextView(parent.getContext()) ;
			domainItem.setTextColor(Color.WHITE) ;
			domainItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25) ;
			domainItem.setBackgroundColor(Color.TRANSPARENT) ;
			domainItem.setWidth(LayoutParams.FILL_PARENT) ;
			if (position==0) {
				domainItem.setText("[New Group]") ;
			} else {
				if (domainGroupList.size()>0) {
					DomainGroup currentDomain = domainGroupList.get(position - 1);
					domainItem.setText(currentDomain.getName());
				}
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

	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.domain_groups_activity) ;
		findViewById(R.id.dnsLogo).setOnClickListener(new LogoOnClickListener(this));
		((TextView)findViewById(R.id.headerLabel)).setText(R.string.domain_groups_list_label) ;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume() ;

		domainGroupList = new ArrayList<DomainGroup>() ;
		findViewById(R.id.viewRefreshProgressBar).setVisibility(View.VISIBLE) ;
		findViewById(R.id.viewRefreshButton).setVisibility(View.GONE) ;

		groupListView = (ListView) findViewById(R.id.groupListView) ;
		groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> domainListView, View selectedView, int position, long itemId) {
				Log.d(TAG, "Item at position '"+position+"' pressed.") ;
				DomainGroup selected ;
				Intent domainGroupDetailsActivity ;
				if (position>0) {
					selected = domainGroupList.get(position - 1);
					domainGroupDetailsActivity = new Intent(getApplicationContext(), DomainGroupDetailsActivity.class);
					domainGroupDetailsActivity.putExtra("domainGroupName", selected.getName()) ;
				} else {
					domainGroupDetailsActivity = new Intent(getApplicationContext(), CreateNewDomainGroup.class) ;
				}
				startActivity(domainGroupDetailsActivity) ;
			}
			
		}) ;
		groupListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> domainListView, View selectedView, int position, long itemId) {
				Log.d(TAG, "Item at position '"+position+"' long pressed.") ;
				final String groupName = ((DomainGroup)groupListView.getItemAtPosition(position)).getName() ;
				AlertDialog.Builder builder = new AlertDialog.Builder(DomainGroupsListActivity.this) ;
				String alertTitle = getResources().getString(R.string.delete) + " '"+groupName+"'?" ;
				builder.setTitle(alertTitle) ;
				builder.setMessage(R.string.domain_group_delete_confirmation) ;
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
						busyDialog = new ProgressDialog(DomainGroupsListActivity.this) ;
						busyDialog.setTitle(R.string.deleting) ;
						busyDialog.show() ;
						
						new DomainGroupDeleteTask().execute(groupName) ;
					}
				}) ;
				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
					}
				}) ;
				builder.show() ;
				return true;
			}
			
		}) ;

		groupListView.setAdapter(new DomainGroupAdapter()) ;

		new DomainGroupListTask(this, domainGroupList, groupListView).execute() ;

		((ImageView)findViewById(R.id.viewRefreshButton)).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				findViewById(R.id.viewRefreshButton).setVisibility(View.GONE) ;
				findViewById(R.id.viewRefreshProgressBar).setVisibility(View.VISIBLE) ;
				domainGroupList.clear() ;
				new DomainGroupListTask(DomainGroupsListActivity.this, domainGroupList, groupListView).execute() ;
			}
		}) ;
	}
}

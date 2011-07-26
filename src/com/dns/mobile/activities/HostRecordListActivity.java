package com.dns.mobile.activities;

import java.util.ArrayList;
import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.ResourceRecord;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class HostRecordListActivity extends Activity {

	/**
	 * Constructor for the Domain List Activity.
	 */
	public HostRecordListActivity() {
	}

	private class SearchInputHandler implements View.OnKeyListener {

		private ListView hostList = null ;
		private StringBuffer filter = null ;

		public SearchInputHandler(StringBuffer filter, ListView hostList) {
			this.filter = filter ;
			this.hostList = hostList ;
		}

		public boolean onKey(View v, int keyCode, KeyEvent event) {
			EditText box = (EditText) v;
			filter.delete(0, filter.length());
			filter.append(box.getText().toString());
			Log.d("SearchInputHandler","Keypress: "+box.getText().toString()) ;
			hostList.invalidateViews() ;
			return false;
		}
		
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
		Log.d("HostRecordListActivity","Creating") ;
		setContentView(R.layout.domain_hosts_activity) ;

		StringBuffer filter = new StringBuffer("") ;
		super.onStart() ;
		ProgressDialog spinner = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER) ;
		spinner.setTitle("Fetching RR List") ;
		ArrayList<ResourceRecord> rrList = new ArrayList<ResourceRecord>() ;
		String domainName = this.getIntent().getStringExtra("domainName") ;
		String hostName = this.getIntent().getStringExtra("hostName").contentEquals("")?"":this.getIntent().getStringExtra("hostName")+"." ;
		((TextView)findViewById(R.id.hostHeaderLabel)).setText(hostName+domainName) ;
		spinner.show() ;
		BackgroundRequestHandler apiInstance = new BackgroundRequestHandler(filter, spinner, this, rrList) ;

		new Thread(apiInstance).start() ;
		Log.d("HostRecordListActivity","Created View") ;
	}

	private class BackgroundRequestHandler implements Runnable {

		private ArrayList<ResourceRecord> rrList = null ;
		private boolean isClean = true ;
		private String errorMessage = null ;
		private Activity parent = null ;
		private ProgressDialog spinner = null ;
		private StringBuffer filter = null ;

		public BackgroundRequestHandler(StringBuffer filter, ProgressDialog spinner, Activity parent, ArrayList<ResourceRecord> rrList) {
			this.rrList = rrList ;
			this.parent = parent ;
			this.spinner = spinner ;
			this.filter = filter ;
		}

		public void run() {

			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(parent);
			String apiHost = null ;
			if (settings.getBoolean("use.sandbox", true)) {
				apiHost = "sandbox.dns.com" ;
			} else {
				apiHost = "www.dns.com" ;
			}

			ManagementAPI api = new ManagementAPI(apiHost, !settings.getBoolean("use.sandbox", true), settings.getString("auth.token", "")) ;

			try {
				Log.d("HostRecordListActivity", "Starting API call.") ;
				JSONObject recordsObject = api.getRRSetForHostname(parent.getIntent().getStringExtra("domainName"), false, parent.getIntent().getStringExtra("hostName")) ;
				Log.d("HostRecordListActivity", "Finished API call") ;
				
				try {
					if (recordsObject.get("error")!=null) {
						isClean = false ;
						errorMessage = recordsObject.getString("error") ;
					}
				} catch (JSONException jsone) {
					// Ignore
				}
				
				JSONObject meta = null ;
				if (isClean) {
					meta = recordsObject.getJSONObject("meta") ;
					if (meta==null) {
						isClean = false ;
						errorMessage = "'meta' node of the JSON response is NULL" ;
					}
				}
				
				if (isClean) {
					int success = 0 ;
					try {
						success = meta.getInt("success") ;
					} catch (JSONException jsone) {
						success = 0 ;
					}
					
					if (success==0) {
						isClean = false ;
						errorMessage = "The meta node does not have a valid success value." ;
					} else {
						JSONArray data = recordsObject.getJSONArray("data") ;
						if (data==null) {
							isClean = false ;
							errorMessage = "The data array for the active hosts list is NULL" ;
						} else {
							int loopIndex = data.length() ;
							for (int x=0; x<loopIndex; x++) {
								ResourceRecord currentRR = new ResourceRecord() ;
								currentRR.setAnswer(data.getJSONObject(x).getString("answer")) ;
								currentRR.setType(data.getJSONObject(x).getString("type")) ;
								currentRR.setActive(true) ;
								if (data.getJSONObject(x).get("country_iso2")!=null) {
									currentRR.setCountryId(data.getJSONObject(x).getString("country_iso2")) ;
								}
								rrList.add(currentRR);
								Log.d("HostRecordListActivity","Adding record '"+data.getJSONObject(x).getString("answer")+"'");
							}
						}
					}
				}
			} catch (JSONException jsone) {
				Log.e("HostRecordListActivity", "JSONException while attempting to get host list.", jsone) ;
				errorMessage = new String("JSONException while attempting to get host list.") ;
				isClean = false ;
			}
			PostRequestUiChanges uiUpdate = new PostRequestUiChanges(filter, spinner, isClean, errorMessage, parent, rrList) ;
			findViewById(R.id.domain_hosts_activity).post(uiUpdate) ;
		}
		
	}

	private class PostRequestUiChanges implements Runnable {

		private boolean isClean = false ;
		private String errorMessage = null ;
		private Activity parent = null ;
		private ArrayList<ResourceRecord> rrList = null ;
		private ProgressDialog spinner = null ;
		private StringBuffer filter = null ;

		public PostRequestUiChanges(StringBuffer filter, ProgressDialog spinner, boolean isClean, String errorMessage, Activity parent, ArrayList<ResourceRecord> rrList) {
			this.isClean = isClean ;
			this.errorMessage = errorMessage ;
			this.parent = parent ;
			this.rrList = rrList ;
			this.spinner = spinner ;
			this.filter = filter ;
		}

		public void run() {

			spinner.dismiss() ;
			if (isClean) {
				ListView hostsListView = new ListView(findViewById(R.id.domain_hosts_activity).getContext()) ;
				HostItemClickedListener hostClickListener = new HostItemClickedListener() ;
				hostsListView.setOnItemClickListener(hostClickListener) ;
				EditText searchField = new EditText(parent) ;
				searchField.setHint("Filter") ;
				SearchInputHandler inputHandler = new SearchInputHandler(filter, hostsListView) ;
				searchField.setOnKeyListener(inputHandler) ;
				((LinearLayout)findViewById(R.id.domain_hosts_activity)).addView(searchField) ;
				hostsListView.setAdapter(new RecordListViewAdapter(parent, rrList, filter)) ;
				hostsListView.setBackgroundResource(R.drawable.list_view_color_states) ;
				((LinearLayout)findViewById(R.id.domain_hosts_activity)).addView(hostsListView) ;
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
				ErrorAlertDialogListener dialogListener = new ErrorAlertDialogListener(parent) ;
				builder.setMessage(errorMessage)
					.setCancelable(false)
					.setPositiveButton("OK", dialogListener) ;
			}
			TextView helpHint = new TextView(findViewById(R.id.domain_hosts_activity).getContext()) ;
			helpHint.setText("Long press to delete an entry.") ;
			helpHint.setGravity(Gravity.CENTER_HORIZONTAL&Gravity.BOTTOM) ;
			helpHint.setWidth(LayoutParams.FILL_PARENT) ;
			helpHint.setBackgroundColor(Color.GRAY) ;
			helpHint.setTextColor(Color.WHITE) ;
			((LinearLayout)findViewById(R.id.domain_hosts_activity)).addView(helpHint) ;
		}
		
	}

	private class RecordListViewAdapter extends BaseAdapter {

		private ArrayList<ResourceRecord> rrList = null ;
		private StringBuffer filter = null ;

		public RecordListViewAdapter(Activity parent, ArrayList<ResourceRecord> rrList, StringBuffer filter) {
			super() ;
			this.rrList = rrList ;
			this.filter = filter ;
		}

		public int getCount() {
			Iterable<ResourceRecord> filteredRecords = Iterables.filter(rrList, new Predicate<ResourceRecord>() {
				public boolean apply(ResourceRecord input) {
					if (input.getAnswer().toLowerCase().contains(filter.toString().toLowerCase())) {
						return true ;
					} else {
						return false;
					}
				}
			}) ;
			return (Iterables.size(filteredRecords)+1) ;
		}

		public Object getItem(int item) {
			Iterable<ResourceRecord> filteredHosts = Iterables.filter(rrList, new Predicate<ResourceRecord>() {
				public boolean apply(ResourceRecord input) {
					if (input.getAnswer().toLowerCase().contains(filter.toString().toLowerCase())) {
						return true ;
					} else {
						return false;
					}
				}
			}) ;
			return Iterables.get(filteredHosts, item-1) ;
		}

		public long getItemId(int arg0) {
			return arg0 ;
		}

		public View getView(int arg0, View arg1, ViewGroup arg2) {
			LinearLayout hostItem = new LinearLayout(getBaseContext()) ;
			hostItem.setOrientation(LinearLayout.HORIZONTAL) ;
			hostItem.setMinimumHeight(26) ;
			hostItem.setMinimumWidth(LayoutParams.FILL_PARENT) ;
			hostItem.setGravity(Gravity.FILL_HORIZONTAL) ;
			hostItem.setBackgroundResource(R.drawable.list_view_color_states) ;
			TextView item = null ;
			item = new TextView(getBaseContext()) ;
			item.setTextColor(Color.WHITE) ;
			item.setGravity(Gravity.CENTER_VERTICAL&Gravity.LEFT) ;
			item.setPadding(8, 0, 0, 0) ;
			if (arg0==0) {
				item.setText("[New Resource Record]") ;
				hostItem.addView(item) ;
			} else {
				Iterable<ResourceRecord> filteredHosts = Iterables.filter(rrList, new Predicate<ResourceRecord>() {
					public boolean apply(ResourceRecord input) {
						if (input.getAnswer().toLowerCase().contains(filter.toString().toLowerCase())) {
							return true ;
						} else {
							return false;
						}
					}
				}) ;
				ResourceRecord currentHost = Iterables.get(filteredHosts,arg0-1) ;
				String name = currentHost.getAnswer().trim().length()==0?"(root)":currentHost.getAnswer() ;
				item.setText(name) ;
				TextView rrType = new TextView(getBaseContext()) ;
				rrType.setBackgroundDrawable(getResources().getDrawable(R.drawable.type_background)) ;
				rrType.setTextColor(Color.WHITE) ;
				rrType.setGravity(Gravity.CENTER) ;
				rrType.setPadding(2, 1, 2, 1) ;
				rrType.setWidth(75) ;
				rrType.setText(ResourceRecord.getTypeAsString(currentHost.getType())) ;

				ImageView locationIndicator = new ImageView(getBaseContext()) ;
				Drawable locationIcon = null ;
				if (currentHost.getCountryId().contentEquals("")) {
					locationIcon = getResources().getDrawable(R.drawable.pushpin_blue) ;
				} else {
					locationIcon = getResources().getDrawable(R.drawable.globe) ;
				}
				locationIndicator.setImageDrawable(locationIcon) ;

				hostItem.addView(rrType) ;
				hostItem.addView(locationIndicator) ;
				hostItem.addView(item) ;
			}
			
			return hostItem ;
		}
		
	}

	private class ErrorAlertDialogListener implements DialogInterface.OnClickListener {

		private Activity parent = null ;

		public ErrorAlertDialogListener(Activity parent) {
			this.parent = parent ;
		}

		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss() ;
			parent.finish() ;
		}
	}

	private class HostItemClickedListener implements AdapterView.OnItemClickListener {

		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			TextView selectedItem = (TextView) arg1 ;
			String hostName = selectedItem.getText().toString() ;
			if (hostName.trim().contentEquals("[New Host]")) {
				Intent i = new Intent(getBaseContext(), CreateNewDomainActivity.class) ;
				startActivity(i) ;
			} else {
				Intent i = new Intent(getBaseContext(), HostRecordListActivity.class) ;
				i.putExtra("hostName", hostName) ;
				startActivity(i) ;
			}
		}
		
	}
}

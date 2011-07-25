package com.dns.mobile.activities;

import java.util.ArrayList;
import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.Host;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class DomainHostsActivity extends Activity {

	/**
	 * Constructor for the Domain List Activity.
	 */
	public DomainHostsActivity() {
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
		Log.d("DomainHostsActivity","Creating") ;
		setContentView(R.layout.domain_hosts_activity) ;
		findViewById(R.id.dnsLogo).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext()) ;
				builder.setTitle(R.string.open_web_confirmation_title) ;
				builder.setTitle(R.string.open_web_confirmation_msg) ;
				builder.setPositiveButton(R.string.open_web_confirmation_yes, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						Uri uri = Uri.parse("http://www.dns.com/") ;
						startActivity(new Intent(Intent.ACTION_VIEW, uri)) ;
					}
				}) ;
				builder.setNegativeButton(R.string.open_web_confirmation_no, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
					}
				}) ;
				builder.show() ;
			}
		});

		StringBuffer filter = new StringBuffer("") ;
		super.onStart() ;
		ProgressDialog spinner = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER) ;
		spinner.setTitle("Fetching Host List") ;
		ArrayList<Host> hostList = new ArrayList<Host>() ;
		((TextView)findViewById(R.id.domainHeaderLabel)).setText(this.getIntent().getStringExtra("domainName")) ;
		spinner.show() ;
		BackgroundRequestHandler apiInstance = new BackgroundRequestHandler(filter, spinner, this, hostList, "WU^E1O1Q83O~^!^@*RJ06I^RVT06", "www.dns.com", true) ;

		new Thread(apiInstance).start() ;
		Log.d("DomainHostsActivity","Created View") ;
	}

	private class BackgroundRequestHandler implements Runnable {

		private ArrayList<Host> hostList = null ;
		private String authToken = null ;
		private String host = null ;
		private boolean useSSL = false ;
		private boolean isClean = true ;
		private String errorMessage = null ;
		private Activity parent = null ;
		private ProgressDialog spinner = null ;
		private StringBuffer filter = null ;

		public BackgroundRequestHandler(StringBuffer filter, ProgressDialog spinner, Activity parent, ArrayList<Host> hostList, String authToken, String host, boolean useSSL) {
			this.hostList = hostList ;
			this.authToken = authToken ;
			this.host = host ;
			this.useSSL = useSSL ;
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
				Log.d("DomainHostActivity", "Starting API call.") ;
				JSONObject hostsObject = api.getHostnamesForDomain(parent.getIntent().getStringExtra("domainName")) ;
				Log.d("DomainHostActivity", "Finished API call") ;
				
				try {
					if (hostsObject.get("error")!=null) {
						isClean = false ;
						errorMessage = hostsObject.getString("error") ;
					}
				} catch (JSONException jsone) {
					// Ignore
				}
				
				JSONObject meta = null ;
				if (isClean) {
					meta = hostsObject.getJSONObject("meta") ;
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
						JSONArray data = hostsObject.getJSONArray("data") ;
						if (data==null) {
							isClean = false ;
							errorMessage = "The data array for the active hosts list is NULL" ;
						} else {
							int loopIndex = data.length() ;
							for (int x=0; x<loopIndex; x++) {
								Host currentHost = new Host();
								currentHost.setHostId(data.getJSONObject(x).getInt("id"));
								currentHost.setName(data.getJSONObject(x).getString("name"));
								currentHost.setRecordCount(data.getJSONObject(x).getInt("num_rr")) ;
								hostList.add(currentHost);
								Log.d("DomainHostsActivity","Adding host '"+data.getJSONObject(x).getString("name")+"'");
							}
						}
					}
				}
			} catch (JSONException jsone) {
				Log.e("DomainHostsActivity", "JSONException while attempting to get host list.", jsone) ;
				errorMessage = new String("JSONException while attempting to get host list.") ;
				isClean = false ;
			}
			PostRequestUiChanges uiUpdate = new PostRequestUiChanges(filter, spinner, isClean, errorMessage, parent, hostList) ;
			findViewById(R.id.domain_hosts_activity).post(uiUpdate) ;
		}
		
	}

	private class PostRequestUiChanges implements Runnable {

		private boolean isClean = false ;
		private String errorMessage = null ;
		private Activity parent = null ;
		private ArrayList<Host> hostList = null ;
		private ProgressDialog spinner = null ;
		private StringBuffer filter = null ;

		public PostRequestUiChanges(StringBuffer filter, ProgressDialog spinner, boolean isClean, String errorMessage, Activity parent, ArrayList<Host> hostList) {
			this.isClean = isClean ;
			this.errorMessage = errorMessage ;
			this.parent = parent ;
			this.hostList = hostList ;
			this.spinner = spinner ;
			this.filter = filter ;
		}

		public void run() {

			spinner.dismiss() ;
			if (isClean) {
				ListView hostsListView = new ListView(findViewById(R.id.domain_hosts_activity).getContext()) ;
				HostItemClickedListener hostClickListener = new HostItemClickedListener(parent.getIntent().getStringExtra("domainName")) ;
				hostsListView.setOnItemClickListener(hostClickListener) ;
				EditText searchField = new EditText(parent) ;
				searchField.setHint("Filter") ;
				SearchInputHandler inputHandler = new SearchInputHandler(filter, hostsListView) ;
				searchField.setOnKeyListener(inputHandler) ;
				((LinearLayout)findViewById(R.id.domain_hosts_activity)).addView(searchField) ;
				hostsListView.setAdapter(new HostListViewAdapter(parent, hostList, filter)) ;
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

	private class HostListViewAdapter extends BaseAdapter {

		private ArrayList<Host> hosts = null ;
		private StringBuffer filter = null ;

		public HostListViewAdapter(Activity parent, ArrayList<Host> hosts, StringBuffer filter) {
			super() ;
			this.hosts = hosts ;
			this.filter = filter ;
		}

		public int getCount() {
			Iterable<Host> filteredHosts = Iterables.filter(hosts, new Predicate<Host>() {
				public boolean apply(Host input) {
					if (input.getName().toLowerCase().contains(filter.toString().toLowerCase())) {
						return true ;
					} else {
						return false;
					}
				}
			}) ;
			return (Iterables.size(filteredHosts)+1) ;
		}

		public Object getItem(int item) {
			Iterable<Host> filteredHosts = Iterables.filter(hosts, new Predicate<Host>() {
				public boolean apply(Host input) {
					if (input.getName().toLowerCase().contains(filter.toString().toLowerCase())) {
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
			Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			Log.d("DomainHostsActivity", "Display size: "+display.getWidth()+"x"+display.getHeight()) ;
			LinearLayout hostItem = new LinearLayout(getBaseContext()) ;
			hostItem.setOrientation(LinearLayout.HORIZONTAL) ;
			hostItem.setMinimumHeight(26) ;
			hostItem.setMinimumWidth(LayoutParams.FILL_PARENT) ;
			hostItem.setGravity(Gravity.FILL_HORIZONTAL&Gravity.CENTER_VERTICAL) ;
			hostItem.setBackgroundResource(R.drawable.list_view_color_states) ;
			TextView item = null ;
			item = new TextView(getBaseContext()) ;
			item.setTextColor(Color.WHITE) ;
			item.setGravity(Gravity.CENTER_VERTICAL&Gravity.LEFT) ;
			item.setPadding(8, 0, 0, 0) ;
			if (arg0==0) {
				item.setText("[New Host]") ;
				hostItem.addView(item) ;
			} else {
				Iterable<Host> filteredHosts = Iterables.filter(hosts, new Predicate<Host>() {
					public boolean apply(Host input) {
						if (input.getName().toLowerCase().contains(filter.toString().toLowerCase())) {
							return true ;
						} else {
							return false;
						}
					}
				}) ;
				Host currentHost = Iterables.get(filteredHosts,arg0-1) ;
				String name = currentHost.getName().trim().length()==0?"(root)":currentHost.getName() ;
				long hostId = currentHost.getHostId() ;
				item.setText(name) ;
				TextView rrDisplay = new TextView(getBaseContext()) ;
				rrDisplay.setText(currentHost.getRecordCount()+"") ;
				rrDisplay.setBackgroundDrawable(getResources().getDrawable(R.drawable.count_background)) ;
				rrDisplay.setTextColor(Color.WHITE) ;
				rrDisplay.setGravity(Gravity.CENTER) ;

				TextView hostIdHolder = new TextView(getBaseContext()) ;
				hostIdHolder.setText(hostId+"") ;
				hostIdHolder.setVisibility(View.INVISIBLE) ;
				hostItem.addView(rrDisplay) ;
				hostItem.addView(item) ;
				hostItem.addView(hostIdHolder) ;
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

		private String domainName = null ;

		public HostItemClickedListener(String domainName) {
			this.domainName = domainName ;
		}

		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			LinearLayout item = (LinearLayout)arg1 ;
			int childIdx = item.getChildCount() ;
			Log.d("DomainHostsActivity", "The ListView item has '"+childIdx+"' child nodes.") ;
			TextView hostNameHld = (TextView) item.getChildAt(childIdx-2) ;
			String hostName = hostNameHld.getText().toString().contentEquals("(root)")?"":hostNameHld.getText().toString() ;
			Log.d("DomainHostsActivity", "Selected hostname: "+hostName) ;
			if (hostName.trim().contentEquals("[New Host]")) {
				Intent i = new Intent(getBaseContext(), CreateNewHostActivity.class) ;
				i.putExtra("domainName", domainName) ;
				startActivity(i) ;
			} else {
				Intent i = new Intent(getBaseContext(), HostRecordListActivity.class) ;
				i.putExtra("hostName", hostName) ;
				i.putExtra("domainName", domainName) ;
				startActivity(i) ;
			}
		}
		
	}
}

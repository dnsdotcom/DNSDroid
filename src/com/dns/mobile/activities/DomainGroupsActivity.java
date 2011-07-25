package com.dns.mobile.activities;

import java.util.ArrayList;
import com.dns.mobile.R;
import com.dns.mobile.api.compiletime.ManagementAPI;
import com.dns.mobile.data.Domain;
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
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DomainGroupsActivity extends Activity {

	private LinearLayout domainListMain = null ;

	/**
	 * Constructor for the Domain List Activity.
	 */
	public DomainGroupsActivity() {
	}

	public void onResume() {
		super.onResume() ;
	}

	private class SearchInputHandler implements View.OnKeyListener {

		private ListView domainList = null ;
		private StringBuffer filter = null ;

		public SearchInputHandler(StringBuffer filter, ListView domainList) {
			this.filter = filter ;
			this.domainList = domainList ;
		}

		public boolean onKey(View v, int keyCode, KeyEvent event) {
			EditText box = (EditText) v;
			filter.delete(0, filter.length());
			filter.append(box.getText().toString());
			Log.d("SearchInputHandler","Keypress: "+box.getText().toString()) ;
			domainList.invalidateViews() ;
			return false;
		}
		
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
		Log.d("DomainListActivity","Creating") ;
		domainListMain = new LinearLayout(getBaseContext()) ;
		domainListMain.setOrientation(LinearLayout.VERTICAL) ;
		domainListMain.setBackgroundColor(Color.RED) ;
		ImageView dnsLogo = new ImageView(getBaseContext()) ;
		dnsLogo.setImageResource(R.drawable.dns_android_banner) ;
		domainListMain.addView(dnsLogo) ;
		setContentView(domainListMain) ;
		Log.d("DomainListActivity","Created View") ;
	}

	private class BackgroundRequestHandler implements Runnable {

		private ArrayList<Domain> domainList = null ;
		private String authToken = null ;
		private String host = null ;
		private boolean useSSL = false ;
		private boolean isClean = true ;
		private String errorMessage = null ;
		private LinearLayout main = null ;
		private Activity parent = null ;
		private ProgressDialog spinner = null ;
		private StringBuffer filter = null ;

		public BackgroundRequestHandler(StringBuffer filter, ProgressDialog spinner, Activity parent, LinearLayout main, ArrayList<Domain> domainList, String authToken, String host, boolean useSSL) {
			this.domainList = domainList ;
			this.main = main ;
			this.authToken = authToken ;
			this.host = host ;
			this.useSSL = useSSL ;
			this.parent = parent ;
			this.spinner = spinner ;
			this.filter = filter ;
		}

		public void run() {
			ManagementAPI api = new ManagementAPI(host, useSSL, authToken) ;
			try {
				Log.d("DomainListActivity", "Starting API call.") ;
				JSONObject domainsObject = api.getDomains("") ;
				Log.d("DomainListActivity", "Finished API call") ;
				
				try {
					if (domainsObject.get("error")!=null) {
						isClean = false ;
						errorMessage = domainsObject.getString("error") ;
					}
				} catch (JSONException jsone) {
					// Ignore
				}
				
				JSONObject meta = null ;
				if (isClean) {
					meta = domainsObject.getJSONObject("meta") ;
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
						JSONArray data = domainsObject.getJSONArray("data") ;
						if (data==null) {
							isClean = false ;
							errorMessage = "The data array for the active domains list is NULL" ;
						} else {
							int loopIndex = data.length() ;
							for (int x=0; x<loopIndex; x++) {
								Domain currentDomain = new Domain();
								if (data.getJSONObject(x).getString("mode").contentEquals("group")) {
									currentDomain.setGroupedDomain(true) ;
								} else {
									currentDomain.setGroupedDomain(false) ;
								}
								currentDomain.setDomainId(data.getJSONObject(x).getInt("id"));
								currentDomain.setName(data.getJSONObject(x).getString("name"));
								domainList.add(currentDomain);
								Log.d("DomainListActivity","Adding domain '"+data.getJSONObject(x).getString("name")+"'");
							}
						}
					}
				}
			} catch (JSONException jsone) {
				Log.e("DomainListActivity", "JSONException while attempting to get domain list.", jsone) ;
				errorMessage = new String("JSONException while attempting to get domain list.") ;
				isClean = false ;
			}
			PostRequestUiChanges uiUpdate = new PostRequestUiChanges(filter, spinner, main, isClean, errorMessage, parent, domainList) ;
			main.post(uiUpdate) ;
		}
		
	}

	private class PostRequestUiChanges implements Runnable {

		private LinearLayout main = null ;
		private boolean isClean = false ;
		private String errorMessage = null ;
		private Activity parent = null ;
		private ArrayList<Domain> domainList = null ;
		private ProgressDialog spinner = null ;
		private StringBuffer filter = null ;

		public PostRequestUiChanges(StringBuffer filter, ProgressDialog spinner, LinearLayout main, boolean isClean, String errorMessage, Activity parent, ArrayList<Domain> domainList) {
			this.main = main ;
			this.isClean = isClean ;
			this.errorMessage = errorMessage ;
			this.parent = parent ;
			this.domainList = domainList ;
			this.spinner = spinner ;
			this.filter = filter ;
		}

		public void run() {

			spinner.dismiss() ;
			if (isClean) {
				ListView domainListView = new ListView(main.getContext()) ;
				EditText searchField = new EditText(parent) ;
				searchField.setHint("Filter") ;
				SearchInputHandler inputHandler = new SearchInputHandler(filter, domainListView) ;
				searchField.setOnKeyListener(inputHandler) ;
				main.addView(searchField) ;
				domainListView.setAdapter(new DomainListViewAdapter(parent, domainList, filter)) ;
				domainListView.setBackgroundResource(R.drawable.list_view_color_states) ;
				domainListMain.addView(domainListView) ;
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
				ErrorAlertDialogListener dialogListener = new ErrorAlertDialogListener(parent) ;
				builder.setMessage(errorMessage)
					.setCancelable(false)
					.setPositiveButton("OK", dialogListener) ;
			}
		}
		
	}

	public void onStart() {
		StringBuffer filter = new StringBuffer("") ;
		super.onStart() ;
		ProgressDialog spinner = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER) ;
		spinner.setTitle("Fetching Domain List") ;
		ArrayList<Domain> domainList = new ArrayList<Domain>() ;
		spinner.show() ;
		BackgroundRequestHandler apiInstance = new BackgroundRequestHandler(filter, spinner, this, domainListMain, domainList, "WU^E1O1Q83O~^!^@*RJ06I^RVT06", "www.dns.com", true) ;
//		BackgroundRequestHandler apiInstance = new BackgroundRequestHandler(filter, spinner, this, domainListMain, domainList, "E9!36*F~WBDW0P*HH@9YOJXY6NKO", "sandbox.dns.com", false) ;

		new Thread(apiInstance).start() ;
	}

	private class DomainListViewAdapter extends BaseAdapter {

		private ArrayList<Domain> domains = null ;
		private Activity parent = null ;
		private StringBuffer filter = null ;

		public DomainListViewAdapter(Activity parent, ArrayList<Domain> domains, StringBuffer filter) {
			super() ;
			this.domains = domains ;
			this.parent = parent ;
			this.filter = filter ;
		}

		public int getCount() {
			Iterable<Domain> filteredDomains = Iterables.filter(domains, new Predicate<Domain>() {
				public boolean apply(Domain input) {
					if (input.getName().toLowerCase().contains(filter.toString().toLowerCase())) {
						return true ;
					} else {
						return false;
					}
				}
			}) ;
			return (Iterables.size(filteredDomains)+1) ;
		}

		public Object getItem(int item) {
			Iterable<Domain> filteredDomains = Iterables.filter(domains, new Predicate<Domain>() {
				public boolean apply(Domain input) {
					if (input.getName().toLowerCase().contains(filter.toString().toLowerCase())) {
						return true ;
					} else {
						return false;
					}
				}
			}) ;
			return Iterables.get(filteredDomains, item-1) ;
		}

		public long getItemId(int arg0) {
			return arg0 ;
		}

		public View getView(int arg0, View arg1, ViewGroup arg2) {
			TextView item = null ;
			if (arg0==0) {
				item = new TextView(getBaseContext()) ;
				item.setText(R.string.new_entry) ;
				item.setTextColor(Color.WHITE) ;
				item.setHeight(26) ;
				item.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						Log.d("New Domain OnClick Listener","Got item click event.") ;
						Intent i = new Intent(getBaseContext(), CreateNewDomainActivity.class) ;
						startActivity(i) ;
					}
				}) ;
			} else {
				Iterable<Domain> filteredDomains = Iterables.filter(domains, new Predicate<Domain>() {
					public boolean apply(Domain input) {
						if (input.getName().toLowerCase().contains(filter.toString().toLowerCase())) {
							return true ;
						} else {
							return false;
						}
					}
				}) ;
				item = new TextView(parent) ;
				item.setHeight(26) ;
				item.setTextColor(Color.WHITE) ;
				item.setText(Iterables.get(filteredDomains,arg0-1).getName()) ;
				if (Iterables.get(filteredDomains,arg0-1).isGroupedDomain()) {
					item.setTextColor(Color.GRAY) ;
				} else {
					DomainOnClickListener domainListener = new DomainOnClickListener(Iterables.get(filteredDomains,arg0-1).getDomainId(), Iterables.get(filteredDomains,arg0-1).getName()) ;
					item.setOnClickListener(domainListener) ;
				}
			}
			return item ;
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

	private class DomainOnClickListener implements View.OnClickListener {

		private long domainId = 0 ;
		private String domainName = null ;

		public DomainOnClickListener(long domainId, String domainName) {
			this.domainId = domainId ;
			this.domainName = domainName ;
		}
		
		public void onClick(View v) {
			Log.d("DomainOnClickListener","Got domain item click event.") ;
			Intent i = new Intent(getBaseContext(), DomainHostsActivity.class) ;
			i.putExtra("domainId", domainId) ;
			i.putExtra("domainName", domainName) ;
			startActivity(i) ;
		}
		
	}
}

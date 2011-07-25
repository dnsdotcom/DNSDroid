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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * An <code>Activity</code> which shows a list of domains for the given user's API token
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class DomainListActivity extends Activity {

	/**
	 * Constructor for the Domain List Activity.
	 */
	public DomainListActivity() {
	}

	protected void onResume() {
		super.onResume() ;
	}

	/**
	 * Handles events for the search/filter input text area. When the search/filter value is changed, it invalidates the ListView, causing it to be updated with the new filter string.
	 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
	 *
	 */
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

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
		Log.d("DomainListActivity","Creating") ;
		setContentView(R.layout.domain_list_activity) ;
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
		Log.d("DomainListActivity","Created View") ;
		StringBuffer filter = new StringBuffer("") ;
		super.onStart() ;
		ProgressDialog spinner = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER) ;
		spinner.setTitle("Downloading") ;
		spinner.setMessage("Retrieving the domain list from DNS.com") ;
		ArrayList<Domain> domainList = new ArrayList<Domain>() ;
		spinner.show() ;

		BackgroundRequestHandler apiInstance = new BackgroundRequestHandler(
				filter, spinner, this, domainList) ;

		new Thread(apiInstance).start() ;
	}

	/**
	 * An implementation of <code>Runnable</code> which performs the HTTP(S) API call
	 * in a seperate thread and then submits a UI update request via a call to View.post().
	 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
	 *
	 */
	private class BackgroundRequestHandler implements Runnable {

		private ArrayList<Domain> domainList = null ;
		private boolean isClean = true ;
		private String errorMessage = null ;
		private Activity parent = null ;
		private ProgressDialog spinner = null ;
		private StringBuffer filter = null ;

		public BackgroundRequestHandler(StringBuffer filter, ProgressDialog spinner, Activity parent, ArrayList<Domain> domainList) {
			this.domainList = domainList ;
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
			PostRequestUiChanges uiUpdate = new PostRequestUiChanges(filter, spinner, isClean, errorMessage, parent, domainList) ;
			findViewById(R.id.domains_activity_layout).post(uiUpdate) ;
		}
		
	}

	/**
	 * An implementation of <code>Runnable</code> which takes care of updating the UI
	 * inside of the UI thread after <code>BackgroundRequestHandler</code> performs the API
	 * call.
	 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
	 *
	 */
	private class PostRequestUiChanges implements Runnable {

		private boolean isClean = false ;
		private String errorMessage = null ;
		private Activity parent = null ;
		private ArrayList<Domain> domainList = null ;
		private ProgressDialog spinner = null ;
		private StringBuffer filter = null ;

		public PostRequestUiChanges(StringBuffer filter, ProgressDialog spinner, boolean isClean, String errorMessage, Activity parent, ArrayList<Domain> domainList) {
			this.isClean = isClean ;
			this.errorMessage = errorMessage ;
			this.parent = parent ;
			this.domainList = domainList ;
			this.spinner = spinner ;
			this.filter = filter ;
		}

		/**
		 * Runs the UI updates in the UI thread after the background thread finishes pulling data via the DNS.com API.
		 */
		public void run() {

			spinner.dismiss() ;
			if (isClean) {
				ListView domainListView = new ListView(findViewById(R.id.domains_activity_layout).getContext()) ;
				DomainItemClickedListener domainListener = new DomainItemClickedListener() ;
				domainListView.setOnItemClickListener(domainListener) ;
				EditText searchField = new EditText(parent) ;
				searchField.setHint("Filter") ;
				SearchInputHandler inputHandler = new SearchInputHandler(filter, domainListView) ;
				searchField.setOnKeyListener(inputHandler) ;
				((LinearLayout)findViewById(R.id.domains_activity_layout)).addView(searchField) ;
				domainListView.setAdapter(new DomainListViewAdapter(domainList, filter)) ;
				domainListView.setBackgroundResource(R.drawable.list_view_color_states) ;
				((LinearLayout)findViewById(R.id.domains_activity_layout)).addView(domainListView) ;
			} else {
				if (errorMessage.trim().contentEquals("Auth Token Not Valid")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getBaseContext());
					builder.setTitle("Invalid auth token")
							.setMessage("The auth token you have stored is not valid. Would you like to change your auth token now?")
							.setCancelable(false)
							.setPositiveButton("OK", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									Intent configIntent = new Intent(getBaseContext(), ConfigurationActivity.class) ;
									startActivity(configIntent) ;
								}
							})
							.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									finish() ;
								}
							}) ;
					builder.show() ;
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getBaseContext());
					ErrorAlertDialogListener dialogListener = new ErrorAlertDialogListener(
							parent);
					builder.setMessage(errorMessage).setCancelable(false)
							.setPositiveButton("OK", dialogListener);
				}
			}
			TextView helpHint = new TextView(findViewById(R.id.domains_activity_layout).getContext()) ;
			helpHint.setText("Long press to delete an entry.") ;
			helpHint.setGravity(Gravity.CENTER_HORIZONTAL&Gravity.BOTTOM) ;
			helpHint.setWidth(LayoutParams.FILL_PARENT) ;
			helpHint.setBackgroundColor(Color.GRAY) ;
			helpHint.setTextColor(Color.WHITE) ;
			((LinearLayout)findViewById(R.id.domains_activity_layout)).addView(helpHint) ;
		}
		
	}

	/**
	 * An implementation of <code>BaseAdapter</code> to populate the 
	 * <code>ListView</code> which displays the domains associated with the current
	 * user.
	 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
	 *
	 */
	private class DomainListViewAdapter extends BaseAdapter {

		private ArrayList<Domain> domains = null ;
		private StringBuffer filter = null ;

		public DomainListViewAdapter(ArrayList<Domain> domains, StringBuffer filter) {
			super() ;
			this.domains = domains ;
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
			item = new TextView(getBaseContext()) ;
			item.setHeight(26) ;
			item.setTextColor(Color.WHITE) ;
			item.setBackgroundResource(R.drawable.list_view_color_states) ;
			if (arg0==0) {
				item.setText("[New Domain]") ;
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
				item.setText(Iterables.get(filteredDomains,arg0-1).getName()) ;
				if (Iterables.get(filteredDomains,arg0-1).isGroupedDomain()) {
					item.setTextColor(Color.GRAY) ;
				}
			}
			return item ;
		}
		
	}

	/**
	 * An event handler which implements <code>DialogInterface.OnClickListener</code> in
	 * order to dismiss a dialog which tells the user that the Internet is currently 
	 * unavailable.
	 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
	 *
	 */
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

	private class DomainItemClickedListener implements AdapterView.OnItemClickListener {

		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			TextView selectedItem = (TextView) arg1 ;
			String domainName = selectedItem.getText().toString() ;
			if (domainName.trim().contentEquals("[New Domain]")) {
				Intent i = new Intent(getBaseContext(), CreateNewDomainActivity.class) ;
				startActivity(i) ;
			} else {
				Intent i = new Intent(getBaseContext(), DomainHostsActivity.class) ;
				i.putExtra("domainName", domainName) ;
				startActivity(i) ;
			}
		}
	}

	private class DomainItemLongClickedListener implements AdapterView.OnItemLongClickListener {

		private class OnYesButtonListener implements DialogInterface.OnClickListener {
			private String domainName = null ;
			private View parent = null ;

			/**
			 * An implementation of <code>Runnable</code> which performs the HTTP(S) API call
			 * in a seperate thread and then submits a UI update request via a call to View.post().
			 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
			 *
			 */
			private class DeleteRequestHandler implements Runnable {

				private String domainName = null ;
				private Activity parent = null ;
				private ProgressDialog spinner = null ;

				private class PostDeleteUiChanges implements Runnable {
					private ProgressDialog spinner = null ;
					private Activity parent = null ;

					/**
					 * 
					 */
					public PostDeleteUiChanges(ProgressDialog spinner, Activity parent) {
						this.spinner = spinner ;
						this.parent = parent ;
					}

					public void run() {
						spinner.dismiss() ;
					}
					
				}

				public DeleteRequestHandler(ProgressDialog spinner, Activity parent, String domainName) {
					this.domainName = domainName ;
					this.parent = parent ;
					this.spinner = spinner ;
				}

				public void run() {

					SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(parent);
					String apiHost = null ;
					if (settings.getBoolean("use.sandbox", true)) {
						apiHost = "sandbox.dns.com" ;
					} else {
						apiHost = "www.dns.com" ;
					}

					boolean isClean = true ;
					String errorMessage = null ;

					ManagementAPI api = new ManagementAPI(apiHost, !settings.getBoolean("use.sandbox", true), settings.getString("auth.token", "")) ;

					try {
						Log.d("DomainListActivity", "Starting API call.") ;
						JSONObject domainsObject = api.deleteDomain(domainName, true) ;
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
									
								}
							}
						}
					} catch (JSONException jsone) {
						Log.e("DomainListActivity", "JSONException while attempting to get domain list.", jsone) ;
						errorMessage = new String("JSONException while attempting to get domain list.") ;
						isClean = false ;
					}
					PostDeleteUiChanges uiUpdate = new PostDeleteUiChanges(spinner, parent) ;
					findViewById(R.id.domains_activity_layout).post(uiUpdate) ;
				}
				
			}

			private class DomainDeleteThread implements Runnable {
				private String domainName = null ;
				private View parent = null ;
				private ProgressDialog spinner = null ;

				public DomainDeleteThread(String domainName, View parent, ProgressDialog spinner) {
					this.domainName = domainName ;
					this.parent = parent ;
					this.spinner = spinner ;
				}

				/* (non-Javadoc)
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					
				}
			}

			public void setDomainName(String domainName) {
				this.domainName = domainName ;
			}

			public void setListView(View parent) {
				this.parent = parent ;
			}

			/* (non-Javadoc)
			 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
			 */
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss() ;
				ProgressDialog spinner = new ProgressDialog(parent.getContext(), ProgressDialog.STYLE_SPINNER) ;
				spinner.setTitle("Deleting") ;
				spinner.setMessage("Deleting the domain '"+domainName+"' from DNS.com") ;
				spinner.show() ;
				DomainDeleteThread thread = new DomainDeleteThread(domainName, parent, spinner) ;
			}
		}

		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			TextView selected = (TextView)arg1 ;
			AlertDialog.Builder builder = new AlertDialog.Builder(selected.getContext()) ;
			builder.setTitle("Confirmation?") ;
			builder.setMessage("Are you sure you would like to delete the domain '"+selected.getText()+"'?") ;
			OnYesButtonListener yesListener = new OnYesButtonListener() ;
			yesListener.setDomainName(selected.getText().toString()) ;
			yesListener.setListView(arg0) ;

			builder.setPositiveButton("Yes", yesListener) ;
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss() ;
				}
			}) ;
			return false;
		}
	}
}

/**
 * 
 */
package com.dns.android.authoritative.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.commonsware.cwac.endless.EndlessAdapter;
import com.dns.android.authoritative.R;
import com.dns.android.authoritative.domain.Domain;
import com.dns.android.authoritative.domain.Host;
import com.dns.android.authoritative.domain.HostList;
import com.dns.android.authoritative.rest.RestClient;
import com.dns.android.authoritative.utils.DNSPrefs_;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ItemLongClick;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

/**
 * @author dphillips
 *
 */
@EFragment(R.layout.hosts_fragment)
public class HostsFragment extends SherlockFragment {

	@ViewById(R.id.hostsFragmentLabel)
	protected TextView hostsFragmentLabel ;

	@ViewById(R.id.hostListView)
	protected ListView hostsListView ;

	@ViewById(R.id.hostListBusyIndicator)
	protected ProgressBar hostsLoadingIndicator ;

	@ViewById(R.id.hostFilter)
	protected EditText hostFilter ;

	@Pref
	protected DNSPrefs_ prefs ;

	@Bean
	protected static RestClient client ;

	protected final String TAG = "HostsFragment" ;
	protected Domain parent ;
	protected static String hostFilterValue ;
	protected ArrayList<Host> hostList ;
	protected int totalCount = 0 ;
	protected int limit = 20 ;
	protected int offset = 0 ;
	protected HostListAdapter baseAdapter ;
	protected HostListEndlessAdapter endlessHostAdapter;
	protected OnHostSelectedListener mListener ;

	public interface OnHostSelectedListener {
		public void onHostSelected(Host host) ;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnHostSelectedListener) activity ;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()+" must implement OnHostSelectedListener") ;
		}
	}

	public void setParentDomain(Domain domain) {
		this.parent = domain ;
	}

	@AfterViews
	protected void setupUi() {
		Log.d(TAG, "Fragment loaded and now setting up UI.") ;
		String fragmentLabel = getActivity().getResources().getString(R.string.hosts_fragment_label)+" "+parent.getName() ;
		hostsFragmentLabel.setText(fragmentLabel) ;
		loadInitialHosts() ;
	}

	@ItemClick(R.id.hostListView)
	protected void handleHostClick(Host clickHost) {
		mListener.onHostSelected(clickHost) ;
	}

	@Background
	protected void deleteHost(Host host) {
		try {
			if (client.deleteObject("/hosts/"+host.getId()+"/")) {
				hostList.remove(host) ;
				totalCount-- ;
				hostDeleteSuceeded(host) ;
			} else {
				hostDeleteFailed(host) ;
			}
		} catch (Throwable e) {
			hostDeleteFailed(host) ;
		}
	}

	@UiThread
	protected void hostDeleteFailed(Host host) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()) ;
		builder.setMessage(getActivity().getResources().getString(R.string.record_delete_failed_title)) ;
		String message = getActivity().getResources().getString(R.string.record_delete_failed_message).replace("[[RECORDNAME]]", host.getName()) ;
		builder.setMessage(message) ;
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss() ;
			}
		}) ;
		builder.show() ;
	}

	@UiThread
	protected void hostDeleteSuceeded(Host host) {
		baseAdapter.notifyDataSetChanged() ;
	}

	@ItemLongClick(R.id.hostListView)
	protected void handleHostLongClicked(final Host longClickHost) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		String message = getActivity()
				.getResources()
				.getString(R.string.record_delete_confirmation_message)
				.replace("[[HOSTNAME]]", longClickHost.getName());
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(
							DialogInterface dialog,
							int which) {
						deleteHost(longClickHost) ;
					}
				});
		builder.setNegativeButton(android.R.string.no,
				new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(
							DialogInterface dialog,
							int which) {
						dialog.dismiss() ;
					}
				});
		builder.show();
	}

	public class HostListAdapter extends ArrayAdapter<Host> {

		protected ArrayList<Host> hostList = null ;
		protected ArrayList<Host> filteredHostList = null ;
		protected String filterString = null ;

		@Override
		public Host getItem(int position) {
			if (filteredHostList!=null) {
				if (filteredHostList.size()>0) {
					return filteredHostList.get(position) ;
				}
			}
			return hostList.get(position) ;
		}

		public void setHostList(ArrayList<Host> list) {
			hostList = list ;
			if (filteredHostList==null) {
				filteredHostList = hostList ;
			}
		}

		public HostListAdapter(Context context) {
			super(context, android.R.id.text1);
		}

		public HostListAdapter(Context context, String filter) {
			super(context, android.R.id.text1);
		}

		public String getFilterString() {
			return filterString ;
		}

		public void addHostsToList(ArrayList<Host> hosts) {
			this.filteredHostList.addAll(Collections2.filter(hosts, new HostPredicate(hostFilterValue==null?"":hostFilterValue))) ;
			this.hostList.addAll(hosts) ;
			notifyDataSetChanged() ;
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {

				@SuppressLint("DefaultLocale")
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults() ;
					ArrayList<Host> filteredHostNames = new ArrayList<Host>() ;

					if (constraint==null || constraint.length()==0) {
						Log.d("DomainListAdapter Filter", "No filter value") ;
						results.count = hostList.size() ;
						results.values = hostList ;
					} else {
						Log.d("DomainListAdapter Filter", "Filtering for: "+constraint) ;
						constraint = constraint.toString().toLowerCase() ;
						filteredHostNames = new ArrayList<Host>(Collections2.filter(hostList, new HostPredicate(constraint.toString()))) ;
						results.count = filteredHostNames.size() ;
						results.values = filteredHostNames ;
					}

					Log.d("DomainListAdapter Filter", "Count: "+results.count) ;
					return results ;
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					filteredHostList = (ArrayList<Host>) results.values ;
					notifyDataSetChanged() ;
				}
			} ;
			return filter ;
		}

		@Override
		public int getCount() {
			return filteredHostList.size() ;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final RelativeLayout row = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.host_row, null) ;
			TextView domainLabel = (TextView) row.getChildAt(0) ;
			domainLabel.setText(filteredHostList.get(position).getName().trim().length()==0?"(root)":filteredHostList.get(position).getName());
			return row;
		}
	}

	@Background
	protected void loadInitialHosts() {
		HashMap<String, String> params = new HashMap<String, String>() ;
		params.put("limit", limit+"") ;
		params.put("offset", offset+"") ;
		HostList results = client.getObject(HostList.class, "/hosts/domain/"+parent.getId()+"/", params) ;
		if (results.getMeta()!=null) {
			totalCount = results.getMeta().getTotal_count();
		} else {
			totalCount = results.getHosts().length ;
		}
		hostList = new ArrayList<Host>() ;
		for (Host item: results.getHosts()) {
			hostList.add(item) ;
		}
		setListViewAdapter() ;
	}

	@UiThread
	protected void setListViewAdapter() {
		baseAdapter = new HostListAdapter(getActivity()) ;
		baseAdapter.setHostList(hostList) ;
		if (hostsListView==null) {
			hostsListView = (ListView) getActivity().findViewById(R.id.domainListView) ;
		}
		endlessHostAdapter = new HostListEndlessAdapter(baseAdapter) ;
		endlessHostAdapter.setRunInBackground(true) ;
		hostsListView.setAdapter(endlessHostAdapter) ;
		hostsLoadingIndicator.setVisibility(View.GONE) ;
		if (hostFilter!=null) {
			hostFilter.clearFocus() ;
		}
	}

	@Click(R.id.hostFilterApply)
	public void onSearchApply() {
		Log.d(TAG, "Filter button pressed.") ;
		hostFilterValue = hostFilter.getText().toString() ;
		hostsLoadingIndicator.setVisibility(View.VISIBLE) ;
		limit = 0 ;
		offset = 0 ;
		loadFilteredHosts() ;
	}

	@Background
	protected void loadFilteredHosts() {
		Log.d(TAG, "Repopulating the domainList for the list view.") ;
		HashMap<String, String> params = new HashMap<String, String>() ;
		if ((hostFilterValue!=null) && (hostFilterValue.length()>0)) {
			params.put("name__icontains", hostFilterValue) ;
		}
		params.put("limit", limit+"") ;
		params.put("offset", offset+"") ;
		HostList results = client.getObject(HostList.class, "/hosts/domain/"+parent.getId()+"/", params) ;
		if (results.getMeta()!=null) {
			totalCount = results.getMeta().getTotal_count();
		} else {
			totalCount = results.getHosts().length ;
		}
		hostList = new ArrayList<Host>() ;
		for (Host item: results.getHosts()) {
			hostList.add(item) ;
		}
		updateListAdapter() ;
	}

	@UiThread
	protected void updateListAdapter() {
		Log.d(TAG, "Resetting the list view adapter to display a new list.") ;
		baseAdapter = new HostListAdapter(getActivity()) ;
		baseAdapter.setHostList(hostList) ;
		baseAdapter.getFilter().filter(hostFilterValue) ;
		if (hostsListView==null) {
			hostsListView = (ListView) getActivity().findViewById(R.id.hostListView) ;
		}
		endlessHostAdapter = new HostListEndlessAdapter(baseAdapter) ;
		endlessHostAdapter.setRunInBackground(true) ;
		hostsListView.setAdapter(endlessHostAdapter) ;
		hostsLoadingIndicator.setVisibility(View.GONE) ;
	}

	private class HostListEndlessAdapter extends EndlessAdapter {

		private View pendingView = null;
		private ArrayList<Host> newHosts = null ;
		private HostListAdapter wrapped = null ;
		protected int newTotalCount = 0 ;

		public HostListEndlessAdapter(HostListAdapter wrapped) {
			super(wrapped) ;
			this.wrapped = wrapped ;
			newTotalCount = totalCount ;
		}

		public ListAdapter getWrappedAdapter() {
			return wrapped ;
		}

		@Override
		protected View getPendingView(ViewGroup parent) {
			View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_row, null);

			pendingView = row.findViewById(android.R.id.text1);
			pendingView.setVisibility(View.VISIBLE);
			pendingView = row.findViewById(R.id.throbber);
			pendingView.setVisibility(View.VISIBLE);

			return (row);
		}

		@Override
		protected void appendCachedData() {
			if (totalCount!=newTotalCount) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()) ;
				builder.setTitle(getActivity().getResources().getString(R.string.data_changed_title)) ;
				builder.setMessage(getActivity().getResources().getString(R.string.data_changed_message)) ;
				builder.setPositiveButton(getActivity().getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d(TAG, "Skipping load and refreshing list view.") ;
						hostsLoadingIndicator.setVisibility(View.VISIBLE) ;
						limit = 0 ;
						offset = 0 ;
						loadFilteredHosts() ;
						dialog.dismiss() ;
					}
				}) ;
				builder.setNegativeButton(getActivity().getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
					}
				}) ;
				builder.show() ;
			}
			if (hostList.size() < totalCount) {
				wrapped.addHostsToList(newHosts);
				Log.d(TAG, "Adding '" + newHosts.size()
						+ "' domains to the list.");
			}
		}

		@Override
		protected boolean cacheInBackground() throws Exception {
			if (hostList.size()<totalCount) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("limit", limit + "");
				params.put("offset", offset + "");
				String filterString = wrapped.getFilterString();
				if (filterString != null) {
					params.put("name__icontains", filterString);
				}
				HostList results = client.getObject(HostList.class,
						"/hosts/domain/"+parent.getId()+"/", params);
				if (newHosts == null) {
					newHosts = new ArrayList<Host>();
				} else {
					newHosts.clear();
				}
				for (Host item : results.getHosts()) {
					newHosts.add(item);
				}
				limit = results.getMeta().getLimit();
				offset = results.getMeta().getOffset() + limit;
				if (results.getMeta()!=null) {
					newTotalCount = results.getMeta().getTotal_count();
					limit = results.getMeta().getLimit();
					offset = results.getMeta().getOffset() + limit;
				} else {
					newTotalCount = results.getHosts().length ;
				}
				newTotalCount = results.getMeta().getTotal_count() ;
				String nextVal = results.getMeta().getNext() == null ? "null"
						: results.getMeta().getNext();
				Log.d(TAG, "limit=" + limit + "&offset=" + offset + "&next="
						+ nextVal);
				return results.getMeta().getNext() != null;
			} else {
				return false ;
			}
		}
	}

	public class HostPredicate implements Predicate<Host> {

		private String filter = null ;

		public HostPredicate(String filter) {
			this.filter = filter ;
		}

		@SuppressLint("DefaultLocale")
		@Override
		public boolean apply(Host arg0) {
			if (((Host)arg0).getName().toLowerCase().contains(filter)) {
				return true ;
			}
			return false;
		}
		
	}
}

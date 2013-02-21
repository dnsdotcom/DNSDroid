package com.dns.android.authoritative.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ItemLongClick;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;
import com.commonsware.cwac.endless.EndlessAdapter;
import com.dns.android.authoritative.R;
import com.dns.android.authoritative.domain.Domain;
import com.dns.android.authoritative.domain.DomainList;
import com.dns.android.authoritative.rest.RestClient;
import com.dns.android.authoritative.utils.DNSPrefs_;

@EFragment(R.layout.domains_fragment)
public class DomainsFragment extends SherlockFragment {

	protected final String TAG = "DomainsFragment" ;

	@Pref
	protected DNSPrefs_ prefs ;

	@Bean
	protected static RestClient client ;

	@ViewById(R.id.domainListView)
	protected ListView domainListView ;

	@ViewById(R.id.domainFilter)
	protected EditText domainFilter ;

	@ViewById(R.id.domFilterApply)
	protected ImageView domFilterApply ;

	@ViewById(R.id.domainListBusyIndicator)
	protected ProgressBar domainsLoadingIndicator ;

	protected String domainFilterValue ;
	protected String filterType ;
	protected static int limit = 20 ;
	protected static int offset = 0 ;
	protected static int totalCount = 0 ;
	protected ArrayList<Domain> domainList ;
	protected DomainListAdapter baseAdapter ;
	protected DomainListEndlessAdapter endlessDomainAdapter ;
	protected OnDomainSelectedListener mListener ;

	public interface OnDomainSelectedListener {
		public void onDomainSelected(Domain domain) ;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnDomainSelectedListener) activity ;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()+" must implement OnDomainSelectedListener") ;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return null ;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (domainList==null) {
			onSearchApply();
		} else if (domainList.size()==0) {
			onSearchApply();
		}
	}

	@AfterViews
	protected void getTheBallRolling() {
		uiSetup() ;
	}

	@Background
	protected void deleteDomain(Domain domain) {
		try {
			if (client.deleteObject("/domains/"+domain.getId()+"/")) {
				domainList.remove(domain) ;
				totalCount-- ;
				domainDeleteSuceeded(domain) ;
			} else {
				domainDeleteFailed(domain) ;
			}
		} catch (Throwable e) {
			domainDeleteFailed(domain) ;
		}
	}

	@UiThread
	protected void domainDeleteFailed(Domain domain) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()) ;
		builder.setMessage(getActivity().getResources().getString(R.string.record_delete_failed_title)) ;
		String message = getActivity().getResources().getString(R.string.record_delete_failed_message).replace("[[RECORDNAME]]", domain.getName()) ;
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
	protected void domainDeleteSuceeded(Domain domain) {
		baseAdapter.notifyDataSetChanged() ;
	}

	@ItemClick(R.id.domainListView)
	protected void handleDomainClick(final Domain clickedDomain) {
		mListener.onDomainSelected(clickedDomain) ;
	}

	@ItemLongClick(R.id.domainListView)
	protected void handleDomainLongClick(final Domain longClickedDomain) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		String message = getActivity()
				.getResources()
				.getString(R.string.record_delete_confirmation_message)
				.replace("[[DOMAINNAME]]", longClickedDomain.getName());
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(
							DialogInterface dialog,
							int which) {
						deleteDomain(longClickedDomain) ;
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

	@UiThread
	protected void uiSetup() {
		if (domainListView==null) {
			domainListView = (ListView) getActivity().findViewById(R.id.domainListView) ;
		}
		if (domainListView.getAdapter()==null) {
			offset=0;
			loadInitialDomains() ;
		} else {
			domainsLoadingIndicator.setVisibility(View.GONE) ;
		}
	}

	@Background
	protected void loadInitialDomains() {
		HashMap<String, String> params = new HashMap<String, String>() ;
		if ((domainFilterValue!=null) && (domainFilterValue.length()>0)) {
			params.put(filterType, domainFilterValue) ;
		}
		params.put("limit", limit+"") ;
		params.put("offset", offset+"") ;
		DomainList results = client.getObject(DomainList.class, "/domains/", params) ;
		if (results.getMeta()!=null) {
			totalCount = results.getMeta().getTotal_count();
		} else {
			totalCount = results.getDomains().length ;
		}
		domainList = new ArrayList<Domain>() ;
		for (Domain item: results.getDomains()) {
			domainList.add(item) ;
		}
		setListViewAdapter() ;
	}

	@UiThread
	protected void setListViewAdapter() {
		baseAdapter = new DomainListAdapter(getActivity()) ;
		baseAdapter.setDomainList(domainList) ;
		if (domainListView==null) {
			domainListView = (ListView) getActivity().findViewById(R.id.domainListView) ;
		}
		endlessDomainAdapter = new DomainListEndlessAdapter(baseAdapter) ;
		endlessDomainAdapter.setRunInBackground(true) ;
		domainListView.setAdapter(endlessDomainAdapter) ;
		domainsLoadingIndicator.setVisibility(View.GONE) ;
		if (domainFilter!=null) {
			domainFilter.clearFocus() ;
		}
	}

	@Click(R.id.domFilterApply)
	public void onSearchApply() {
		Log.d(TAG, "Filter button pressed.") ;
		domainFilterValue = domainFilter.getText().toString() ;
		domainsLoadingIndicator.setVisibility(View.VISIBLE) ;
		limit = 0 ;
		offset = 0 ;
		loadFilteredDomains() ;
	}

	@Background
	protected void loadFilteredDomains() {
		Log.d(TAG, "Repopulating the domainList for the list view.") ;
		HashMap<String, String> params = new HashMap<String, String>() ;
		if ((domainFilterValue!=null) && (domainFilterValue.length()>0)) {
			params.put("name__icontains", domainFilterValue) ;
		}
		params.put("limit", limit+"") ;
		params.put("offset", offset+"") ;
		DomainList results = client.getObject(DomainList.class, "/domains/", params) ;
		if (results.getMeta()!=null) {
			totalCount = results.getMeta().getTotal_count();
		} else {
			totalCount = results.getDomains().length ;
		}
		domainList = new ArrayList<Domain>() ;
		for (Domain item: results.getDomains()) {
			domainList.add(item) ;
		}
		updateListAdapter() ;
	}

	@UiThread
	protected void updateListAdapter() {
		Log.d(TAG, "Resetting the list view adapter to display a new list.") ;
		baseAdapter = new DomainListAdapter(getActivity()) ;
		baseAdapter.setDomainList(domainList) ;
		baseAdapter.getFilter().filter(domainFilterValue) ;
		if (domainListView==null) {
			domainListView = (ListView) getActivity().findViewById(R.id.domainListView) ;
		}
		endlessDomainAdapter = new DomainListEndlessAdapter(baseAdapter) ;
		endlessDomainAdapter.setRunInBackground(true) ;
		domainListView.setAdapter(endlessDomainAdapter) ;
		domainsLoadingIndicator.setVisibility(View.GONE) ;
	}

	public class DomainListAdapter extends ArrayAdapter<Domain> {

		protected ArrayList<Domain> domainList = null ;
		protected ArrayList<Domain> filteredDomainList = null ;
		protected String filterString = null ;

		@Override
		public Domain getItem(int position) {
			if (filteredDomainList!=null) {
				if (filteredDomainList.size()>0) {
					return filteredDomainList.get(position) ;
				}
			}
			return domainList.get(position) ;
		}

		public void setDomainList(ArrayList<Domain> list) {
			domainList = list ;
			if (filteredDomainList==null) {
				filteredDomainList = domainList ;
			}
		}

		public DomainListAdapter(Context context) {
			super(context, android.R.id.text1);
		}

		public String getFilterString() {
			return filterString ;
		}

		public void addDomainsToList(ArrayList<Domain> domains) {
			this.filteredDomainList.addAll(Collections2.filter(domains, new DomainPredicate(domainFilterValue==null?"":domainFilterValue))) ;
			this.domainList.addAll(domains) ;
			notifyDataSetChanged() ;
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {

				@SuppressLint("DefaultLocale")
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults() ;
					ArrayList<Domain> filteredDomainNames = new ArrayList<Domain>() ;

					if (constraint==null || constraint.length()==0) {
						Log.d("DomainListAdapter Filter", "No filter value") ;
						results.count = domainList.size() ;
						results.values = domainList ;
					} else {
						Log.d("DomainListAdapter Filter", "Filtering for: "+constraint) ;
						constraint = constraint.toString().toLowerCase() ;
						filteredDomainNames = new ArrayList<Domain>(Collections2.filter(domainList, new DomainPredicate(constraint.toString()))) ;
						results.count = filteredDomainNames.size() ;
						results.values = filteredDomainNames ;
					}

					Log.d("DomainListAdapter Filter", "Count: "+results.count) ;
					return results ;
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					filteredDomainList = (ArrayList<Domain>) results.values ;
					notifyDataSetChanged() ;
				}
			} ;
			return filter ;
		}

		@Override
		public int getCount() {
			return filteredDomainList.size() ;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final RelativeLayout row = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.domain_row, null) ;
			ImageView domainConfigButton = (ImageView) row.getChildAt(0) ;
			domainConfigButton.setImageResource(android.R.drawable.ic_menu_edit);
			domainConfigButton.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO: Handle 
						}
					});
			domainConfigButton.setPadding(0, 0, 10, 0) ;
			TextView domainLabel = (TextView) row.getChildAt(1) ;
			domainLabel.setText(filteredDomainList.get(position).getName());
			return row;
		}
	}

	

	private class DomainListEndlessAdapter extends EndlessAdapter {

		private View pendingView = null;
		private ArrayList<Domain> newDomains = null ;
		private DomainListAdapter wrapped = null ;
		protected int newTotalCount = 0 ;

		public DomainListEndlessAdapter(DomainListAdapter wrapped) {
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
						domainsLoadingIndicator.setVisibility(View.VISIBLE) ;
						limit = 0 ;
						offset = 0 ;
						loadFilteredDomains() ;
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
			if (domainList.size() < totalCount) {
				wrapped.addDomainsToList(newDomains);
				Log.d(TAG, "Adding '" + newDomains.size()
						+ "' domains to the list.");
			}
		}

		@Override
		protected boolean cacheInBackground() throws Exception {
			if (domainList.size()<totalCount) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("limit", limit + "");
				params.put("offset", offset + "");
				String filterString = wrapped.getFilterString();
				if (filterString != null) {
					params.put("name__icontains", filterString);
				}
				DomainList results = client.getObject(DomainList.class,
						"/domains/", params);
				if (newDomains == null) {
					newDomains = new ArrayList<Domain>();
				} else {
					newDomains.clear();
				}
				for (Domain item : results.getDomains()) {
					newDomains.add(item);
				}
				if (results.getMeta()!=null) {
					newTotalCount = results.getMeta().getTotal_count();
					limit = results.getMeta().getLimit();
					offset = results.getMeta().getOffset() + limit;
				} else {
					newTotalCount = results.getDomains().length ;
				}
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

	public class DomainPredicate implements Predicate<Domain> {

		private String filter = null ;

		public DomainPredicate(String filter) {
			this.filter = filter ;
		}

		@SuppressLint("DefaultLocale")
		@Override
		public boolean apply(Domain arg0) {
			if (((Domain)arg0).getName().toLowerCase().contains(filter)) {
				return true ;
			}
			return false;
		}
		
	}
}

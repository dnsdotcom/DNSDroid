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
import com.dns.android.authoritative.domain.Host;
import com.dns.android.authoritative.domain.RR;
import com.dns.android.authoritative.domain.RRList;
import com.dns.android.authoritative.rest.RestClient;
import com.dns.android.authoritative.utils.DNSPrefs_;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

/**
 * A fragment which displays a {@link ListView} of resource records for the selected host.
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
@EFragment(R.layout.rr_list_fragment)
public class RRListFragment extends SherlockFragment {

	@ViewById(R.id.rrsFragmentLabel)
	protected TextView rrsFragmentLabel ;

	@ViewById(R.id.rrListView)
	protected ListView rrsListView ;

	@ViewById(R.id.rrListBusyIndicator)
	protected ProgressBar rrsLoadingIndicator ;

	@ViewById(R.id.rrFilter)
	protected EditText rrFilter ;

	@Pref
	protected DNSPrefs_ prefs ;

	@Bean
	protected static RestClient client ;

	protected final String TAG = "RRListFragment" ;
	protected static String rrFilterValue ;
	protected ArrayList<RR> rrList ;
	protected int totalCount = 0 ;
	protected int limit = 20 ;
	protected int offset = 0 ;
	protected RRListAdapter baseAdapter ;
	protected RRListEndlessAdapter endlessRRAdapter;
	protected OnRRSelectedListener mListener ;
	protected String path ;

	public interface OnRRSelectedListener {
		public void onRRSelected(RR record) ;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnRRSelectedListener) activity ;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()+" must implement OnRRSelectedListener") ;
		}
	}

	protected Host parent ;

	public void setParentHost(Host host) {
		this.parent = host ;
	}

	@AfterViews
	protected void setupUi() {
		Log.d(TAG, "Fragment loaded and now setting up UI.") ;
		String fragmentLabel = getActivity().getResources().getString(R.string.rr_list_fragment_label)+" "+(parent.getName().length()==0?"(root)":parent.getName()) ;
		rrsFragmentLabel.setText(fragmentLabel) ;
		path = "/rrs/host/"+parent.getId()+"/" ;
		Log.d(TAG, "Loading initial RR list.") ;
		loadInitialRRs() ;
	}

	@Background
	protected void loadInitialRRs() {
		HashMap<String, String> params = new HashMap<String, String>() ;
		params.put("limit", limit+"") ;
		params.put("offset", offset+"") ;
		RRList results = client.getObject(RRList.class, path, params) ;
		if (results.getMeta()!=null) {
			totalCount = results.getMeta().getTotal_count();
		} else {
			totalCount = results.getRrs().length ; // This may be obsolete cruft left over from an ancient bug in the API
		}
		rrList = new ArrayList<RR>() ;
		for (RR item: results.getRrs()) {
			Log.d(TAG, "Adding RR: "+item.getAnswer()+"-"+item.getType()) ;
			rrList.add(item) ;
		}
		setListViewAdapter() ;
	}

	@UiThread
	protected void setListViewAdapter() {
		Log.d(TAG, "Setting up ListView adapters") ;
		baseAdapter = new RRListAdapter(getActivity()) ;
		baseAdapter.setRRList(rrList) ;
		if (rrsListView==null) {
			rrsListView = (ListView) getActivity().findViewById(R.id.rrListView) ;
		}
		endlessRRAdapter = new RRListEndlessAdapter(baseAdapter) ;
		endlessRRAdapter.setRunInBackground(true) ;
		rrsListView.setAdapter(endlessRRAdapter) ;
		rrsLoadingIndicator.setVisibility(View.GONE) ;
		if (rrFilter!=null) {
			rrFilter.clearFocus() ;
		}
	}

	@Click(R.id.rrFilterApply)
	public void onSearchApply() {
		Log.d(TAG, "Filter button pressed.") ;
		rrFilterValue = rrFilter.getText().toString() ;
		rrsLoadingIndicator.setVisibility(View.VISIBLE) ;
		limit = 0 ;
		offset = 0 ;
		loadFilteredRRs() ;
	}

	@Background
	protected void loadFilteredRRs() {
		Log.d(TAG, "Repopulating the domainList for the list view.") ;
		HashMap<String, String> params = new HashMap<String, String>() ;
		if ((rrFilterValue!=null) && (rrFilterValue.length()>0)) {
			params.put("answer__icontains", rrFilterValue) ;
		}
		params.put("limit", limit+"") ;
		params.put("offset", offset+"") ;
		RRList results = client.getObject(RRList.class, path, params) ;
		if (results.getMeta()!=null) {
			totalCount = results.getMeta().getTotal_count();
		} else {
			totalCount = results.getRrs().length ; // This may be obsolete cruft left over from an ancient bug in the API
		}
		rrList = new ArrayList<RR>() ;
		for (RR item: results.getRrs()) {
			Log.d(TAG, "Adding RR: "+item.getAnswer()+"-"+item.getType()) ;
			rrList.add(item) ;
		}
		updateListAdapter() ;
	}

	@UiThread
	protected void updateListAdapter() {
		Log.d(TAG, "Resetting the list view adapter to display a new list.") ;
		baseAdapter = new RRListAdapter(getActivity()) ;
		baseAdapter.setRRList(rrList) ;
		baseAdapter.getFilter().filter(rrFilterValue) ;
		if (rrsListView==null) {
			rrsListView = (ListView) getActivity().findViewById(R.id.rrListView) ;
		}
		endlessRRAdapter = new RRListEndlessAdapter(baseAdapter) ;
		endlessRRAdapter.setRunInBackground(true) ;
		rrsListView.setAdapter(endlessRRAdapter) ;
		rrsLoadingIndicator.setVisibility(View.GONE) ;
	}

	public class RRListAdapter extends ArrayAdapter<RR> {

		protected ArrayList<RR> rrList = null ;
		protected ArrayList<RR> filteredRRList = null ;
		protected String filterString = null ;

		@Override
		public RR getItem(int position) {
			if (filteredRRList!=null) {
				if (filteredRRList.size()>0) {
					return filteredRRList.get(position) ;
				}
			}
			return rrList.get(position) ;
		}

		public void setRRList(ArrayList<RR> list) {
			rrList = list ;
			if (filteredRRList==null) {
				filteredRRList = rrList ;
			}
		}

		public RRListAdapter(Context context) {
			super(context, android.R.id.text1);
			Log.d(TAG, "Created RRListAdapter") ;
		}

		public RRListAdapter(Context context, String filter) {
			super(context, android.R.id.text1);
			Log.d(TAG, "Created RRListAdapter") ;
		}

		public String getFilterString() {
			return filterString ;
		}

		public void addRRsToList(ArrayList<RR> rrs) {
			this.filteredRRList.addAll(Collections2.filter(rrs, new RRPredicate(rrFilterValue==null?"":rrFilterValue))) ;
			this.rrList.addAll(rrs) ;
			notifyDataSetChanged() ;
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {

				@SuppressLint("DefaultLocale")
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults() ;
					ArrayList<RR> filteredRRNames = new ArrayList<RR>() ;

					if (constraint==null || constraint.length()==0) {
						Log.d("RRListAdapter Filter", "No filter value") ;
						results.count = rrList.size() ;
						results.values = rrList ;
					} else {
						Log.d("RRListAdapter Filter", "Filtering for: "+constraint) ;
						constraint = constraint.toString().toLowerCase() ;
						filteredRRNames = new ArrayList<RR>(Collections2.filter(rrList, new RRPredicate(constraint.toString()))) ;
						results.count = filteredRRNames.size() ;
						results.values = filteredRRNames ;
					}

					Log.d("RRListAdapter Filter", "Count: "+results.count) ;
					return results ;
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					filteredRRList = (ArrayList<RR>) results.values ;
					notifyDataSetChanged() ;
				}
			} ;
			return filter ;
		}

		@Override
		public int getCount() {
			return filteredRRList.size() ;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final RelativeLayout row = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.rr_row, null) ;
			RR record = filteredRRList.get(position) ;
			String answer = record.getAnswer() ;
			Log.d(TAG, "Generating view for: "+answer+" - "+record.getType()) ;
			TextView domainLabel = (TextView) row.getChildAt(0) ;
			domainLabel.setText(record.getAnswer().trim()) ;
			TextView typeLabel = (TextView) row.getChildAt(1) ;
			typeLabel.setText(record.getType()) ;
			return row;
		}
	}

	private class RRListEndlessAdapter extends EndlessAdapter {

		private View pendingView = null;
		private ArrayList<RR> newRRs = null ;
		private RRListAdapter wrapped = null ;
		protected int newTotalCount = 0 ;

		public RRListEndlessAdapter(RRListAdapter wrapped) {
			super(wrapped) ;
			Log.d(TAG, "Created RRListEndlessAdapter") ;
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
						rrsLoadingIndicator.setVisibility(View.VISIBLE) ;
						limit = 0 ;
						offset = 0 ;
						loadFilteredRRs() ;
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
			if (rrList.size() < totalCount) {
				wrapped.addRRsToList(newRRs);
				Log.d(TAG, "Adding '" + newRRs.size()
						+ "' domains to the list.");
			}
		}

		@Override
		protected boolean cacheInBackground() throws Exception {
			if (rrList.size()<totalCount) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("limit", limit + "");
				params.put("offset", offset + "");
				String filterString = wrapped.getFilterString();
				if (filterString != null) {
					params.put("name__icontains", filterString);
				}
				RRList results = client.getObject(RRList.class, path, params);
				if (newRRs == null) {
					newRRs = new ArrayList<RR>();
				} else {
					newRRs.clear();
				}
				for (RR item : results.getRrs()) {
					newRRs.add(item);
				}
				limit = results.getMeta().getLimit();
				offset = results.getMeta().getOffset() + limit;
				if (results.getMeta()!=null) {
					newTotalCount = results.getMeta().getTotal_count();
					limit = results.getMeta().getLimit();
					offset = results.getMeta().getOffset() + limit;
				} else {
					newTotalCount = results.getRrs().length ;
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

	public class RRPredicate implements Predicate<RR> {

		private String filter = null ;

		public RRPredicate(String filter) {
			this.filter = filter ;
		}

		@SuppressLint("DefaultLocale")
		@Override
		public boolean apply(RR arg0) {
			if (((RR)arg0).getAnswer().toLowerCase().contains(filter)) {
				return true ;
			}
			return false;
		}
		
	}
}

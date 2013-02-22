/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
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
import com.commonsware.cwac.endless.EndlessAdapter;
import com.dns.android.authoritative.R;
import com.dns.android.authoritative.callbacks.OnItemSelectedListener;
import com.dns.android.authoritative.domain.EntityList;
import com.dns.android.authoritative.domain.GenericEntity;
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
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
@EFragment(R.layout.generic_list_fragment)
public class DNSListFragment<T extends GenericEntity> extends SherlockFragment {
	protected final String TAG = "" ;

	@Pref
	protected DNSPrefs_ prefs ;

	@Bean
	protected static RestClient client ;

	@ViewById(R.id.itemListView)
	protected ListView itemListView ;

	@ViewById(R.id.itemFilter)
	protected EditText itemFilter ;

	@ViewById(R.id.itemFilterApply)
	protected ImageView domFilterApply ;

	@ViewById(R.id.itemListBusyIndicator)
	protected ProgressBar itemsLoadingIndicator ;

	protected String itemFilterValue ;
	protected String filterType ;
	protected static int limit = 20 ;
	protected static int offset = 0 ;
	protected static int totalCount = 0 ;
	protected ArrayList<T> itemList ;
	protected ItemListAdapter baseAdapter ;
	protected ItemListEndlessAdapter endlessTAdapter ;
	protected OnItemSelectedListener mListener ;
	protected String basePath = "" ;
	protected int rowLayout ;

	@SuppressWarnings("rawtypes")
	protected Class childType ;
	@SuppressWarnings("rawtypes")
	protected Class type ;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnItemSelectedListener) activity ;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()+" must implement OnItemSelectedListener") ;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return null ;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (itemList==null) {
			onSearchApply();
		} else if (itemList.size()==0) {
			onSearchApply();
		}
	}

	@AfterViews
	protected void getTheBallRolling() {
		uiSetup() ;
	}

	@Background
	protected void deleteItem(GenericEntity item) {
		try {
			if (client.deleteObject(basePath+item.getId()+"/")) {
				itemList.remove(item) ;
				totalCount-- ;
				itemDeleteSuceeded(item) ;
			} else {
				itemDeleteFailed(item) ;
			}
		} catch (Throwable e) {
			itemDeleteFailed(item) ;
		}
	}

	@UiThread
	protected void itemDeleteFailed(GenericEntity item) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()) ;
		builder.setMessage(getActivity().getResources().getString(R.string.record_delete_failed_title)) ;
		String message = getActivity().getResources().getString(R.string.record_delete_failed_message).replace("[[RECORDNAME]]", item.getName()) ;
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
	protected void itemDeleteSuceeded(GenericEntity item) {
		baseAdapter.notifyDataSetChanged() ;
	}

	@ItemClick(R.id.itemListView)
	protected void handleItemClick(final GenericEntity clickedItem) {
		mListener.onItemSelected(clickedItem.getId(), childType) ;
	}

	@ItemLongClick(R.id.itemListView)
	protected void handleItemLongClick(final GenericEntity longClickItem) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		String message = getActivity()
				.getResources()
				.getString(R.string.record_delete_confirmation_message)
				.replace("[[RECORDNAME]]", longClickItem.getName());
		builder.setMessage(message);
		builder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
	
					@Override
					public void onClick(
							DialogInterface dialog,
							int which) {
						deleteItem(longClickItem) ;
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
		if (itemListView==null) {
			itemListView = (ListView) getActivity().findViewById(R.id.itemListView) ;
		}
		if (itemListView.getAdapter()==null) {
			offset=0;
			loadInitialItems() ;
		} else {
			itemsLoadingIndicator.setVisibility(View.GONE) ;
		}
	}

	@Background
	@SuppressWarnings("unchecked")
	protected void loadInitialItems() {
		HashMap<String, String> params = new HashMap<String, String>() ;
		if ((itemFilterValue!=null) && (itemFilterValue.length()>0)) {
			params.put(filterType, itemFilterValue) ;
		}
		params.put("limit", limit+"") ;
		params.put("offset", offset+"") ;
		EntityList<T> results = client.getObjectList(type, basePath, params) ;
		if (results.getMeta()!=null) {
			totalCount = results.getMeta().getTotal_count();
		} else {
			totalCount = results.getItems().length ;
		}
		itemList = new ArrayList<T>() ;
		for (T item: results.getItems()) {
			itemList.add(item) ;
		}
		setListViewAdapter() ;
	}

	@UiThread
	protected void setListViewAdapter() {
		baseAdapter = new ItemListAdapter(getActivity()) ;
		baseAdapter.setTList(itemList) ;
		if (itemListView==null) {
			itemListView = (ListView) getActivity().findViewById(R.id.itemListView) ;
		}
		endlessTAdapter = new ItemListEndlessAdapter(baseAdapter) ;
		endlessTAdapter.setRunInBackground(true) ;
		itemListView.setAdapter(endlessTAdapter) ;
		itemsLoadingIndicator.setVisibility(View.GONE) ;
		if (itemFilter!=null) {
			itemFilter.clearFocus() ;
		}
	}

	@Click(R.id.domFilterApply)
	public void onSearchApply() {
		Log.d(TAG, "Filter button pressed.") ;
		itemFilterValue = itemFilter.getText().toString() ;
		itemsLoadingIndicator.setVisibility(View.VISIBLE) ;
		limit = 0 ;
		offset = 0 ;
		loadFilteredItems() ;
	}

	@SuppressWarnings("unchecked")
	@Background
	protected void loadFilteredItems() {
		Log.d(TAG, "Repopulating the itemList for the list view.") ;
		HashMap<String, String> params = new HashMap<String, String>() ;
		if ((itemFilterValue!=null) && (itemFilterValue.length()>0)) {
			params.put("name__icontains", itemFilterValue) ;
		}
		params.put("limit", limit+"") ;
		params.put("offset", offset+"") ;
		EntityList<T> results = client.getObjectList(type, basePath, params) ;
		if (results.getMeta()!=null) {
			totalCount = results.getMeta().getTotal_count();
		} else {
			totalCount = results.getItems().length ;
		}
		itemList = new ArrayList<T>() ;
		for (T item: results.getItems()) {
			itemList.add(item) ;
		}
		updateListAdapter() ;
	}

	@UiThread
	protected void updateListAdapter() {
		Log.d(TAG, "Resetting the list view adapter to display a new list.") ;
		baseAdapter = new ItemListAdapter(getActivity()) ;
		baseAdapter.setTList(itemList) ;
		baseAdapter.getFilter().filter(itemFilterValue) ;
		if (itemListView==null) {
			itemListView = (ListView) getActivity().findViewById(R.id.itemListView) ;
		}
		endlessTAdapter = new ItemListEndlessAdapter(baseAdapter) ;
		endlessTAdapter.setRunInBackground(true) ;
		itemListView.setAdapter(endlessTAdapter) ;
		itemsLoadingIndicator.setVisibility(View.GONE) ;
	}

	public class ItemListAdapter extends ArrayAdapter<T> {

		protected ArrayList<T> itemList = null ;
		protected ArrayList<T> filteredTList = null ;
		protected String filterString = null ;

		@Override
		public T getItem(int position) {
			if (filteredTList!=null) {
				if (filteredTList.size()>0) {
					return filteredTList.get(position) ;
				}
			}
			return itemList.get(position) ;
		}

		public void setTList(ArrayList<T> list) {
			itemList = list ;
			if (filteredTList==null) {
				filteredTList = itemList ;
			}
		}

		public ItemListAdapter(Context context) {
			super(context, android.R.id.text1);
		}

		public String getFilterString() {
			return filterString ;
		}

		public void addTsToList(ArrayList<T> items) {
			this.filteredTList.addAll(Collections2.filter(items, new ItemPredicate(itemFilterValue==null?"":itemFilterValue))) ;
			this.itemList.addAll(items) ;
			notifyDataSetChanged() ;
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {

				@SuppressLint("DefaultLocale")
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults results = new FilterResults() ;
					ArrayList<T> filteredTNames = new ArrayList<T>() ;

					if (constraint==null || constraint.length()==0) {
						Log.d("TListAdapter Filter", "No filter value") ;
						results.count = itemList.size() ;
						results.values = itemList ;
					} else {
						Log.d("TListAdapter Filter", "Filtering for: "+constraint) ;
						constraint = constraint.toString().toLowerCase() ;
						filteredTNames = new ArrayList<T>(Collections2.filter(itemList, new ItemPredicate(constraint.toString()))) ;
						results.count = filteredTNames.size() ;
						results.values = filteredTNames ;
					}

					Log.d("TListAdapter Filter", "Count: "+results.count) ;
					return results ;
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					filteredTList = (ArrayList<T>) results.values ;
					notifyDataSetChanged() ;
				}
			} ;
			return filter ;
		}

		@Override
		public int getCount() {
			return filteredTList.size() ;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final RelativeLayout row = (RelativeLayout) getActivity().getLayoutInflater().inflate(rowLayout, null) ;
			ImageView itemConfigButton = (ImageView) row.getChildAt(0) ;
			itemConfigButton.setImageResource(android.R.drawable.ic_menu_edit);
			itemConfigButton.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO: Handle 
						}
					});
			itemConfigButton.setPadding(0, 0, 10, 0) ;
			TextView itemLabel = (TextView) row.getChildAt(1) ;
			itemLabel.setText(filteredTList.get(position).getName());
			return row;
		}
	}

	

	private class ItemListEndlessAdapter extends EndlessAdapter {

		private View pendingView = null;
		private ArrayList<T> newTs = null ;
		private ItemListAdapter wrapped = null ;
		protected int newTotalCount = 0 ;

		public ItemListEndlessAdapter(ItemListAdapter wrapped) {
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
						itemsLoadingIndicator.setVisibility(View.VISIBLE) ;
						limit = 0 ;
						offset = 0 ;
						loadFilteredItems() ;
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
			if (itemList.size() < totalCount) {
				wrapped.addTsToList(newTs);
				Log.d(TAG, "Adding '" + newTs.size()
						+ "' items to the list.");
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		protected boolean cacheInBackground() throws Exception {
			if (itemList.size()<totalCount) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("limit", limit + "");
				params.put("offset", offset + "");
				String filterString = wrapped.getFilterString();
				if (filterString != null) {
					params.put("name__icontains", filterString);
				}
				EntityList<T> results = client.getObjectList(type, basePath, params);
				if (newTs == null) {
					newTs = new ArrayList<T>();
				} else {
					newTs.clear();
				}
				for (T item : results.getItems()) {
					newTs.add(item);
				}
				if (results.getMeta()!=null) {
					newTotalCount = results.getMeta().getTotal_count();
					limit = results.getMeta().getLimit();
					offset = results.getMeta().getOffset() + limit;
				} else {
					newTotalCount = results.getItems().length ;
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

	public class ItemPredicate implements Predicate<T> {

		private String filter = null ;

		public ItemPredicate(String filter) {
			this.filter = filter ;
		}

		@SuppressLint("DefaultLocale")
		@Override
		public boolean apply(T arg0) {
			if (((T)arg0).getName().toLowerCase().contains(filter)) {
				return true ;
			}
			return false;
		}
		
	}
}

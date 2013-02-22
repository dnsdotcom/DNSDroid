/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.dns.android.authoritative.R;
import com.dns.android.authoritative.adapters.ItemListAdapter;
import com.dns.android.authoritative.adapters.ItemListEndlessAdapter;
import com.dns.android.authoritative.callbacks.OnItemSelectedListener;
import com.dns.android.authoritative.domain.EntityList;
import com.dns.android.authoritative.domain.GenericEntity;
import com.dns.android.authoritative.rest.RestClient;
import com.dns.android.authoritative.utils.DNSPrefs_;
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
public class DNSListFragment extends SherlockFragment {
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
	protected ArrayList<GenericEntity> itemList ;
	protected ItemListAdapter baseAdapter ;
	protected ItemListEndlessAdapter endlessTAdapter ;
	protected OnItemSelectedListener mListener ;
	protected Integer parentId = null ;

	protected Class<GenericEntity> childType ;

	protected Class<GenericEntity> type ;
	protected String basePath = "" ;
	protected int rowLayout ;

	public void setParentId(Integer id) {
		this.parentId = id ;
	}

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

	@AfterViews
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
	protected void loadInitialItems() {
		HashMap<String, String> params = new HashMap<String, String>() ;
		if ((itemFilterValue!=null) && (itemFilterValue.length()>0)) {
			params.put(filterType, itemFilterValue) ;
		}
		params.put("limit", limit+"") ;
		params.put("offset", offset+"") ;
		EntityList<GenericEntity> results = client.getObjectList(type, basePath, params) ;
		if (results.getMeta()!=null) {
			totalCount = results.getMeta().getTotal_count();
		} else {
			totalCount = results.getItems().length ;
		}
		itemList = new ArrayList<GenericEntity>() ;
		for (GenericEntity item: results.getItems()) {
			itemList.add(item) ;
		}
		setListViewAdapter() ;
	}

	@UiThread
	protected void setListViewAdapter() {
		baseAdapter = new ItemListAdapter(getActivity()).setFilterString("").setmActivity(getActivity()).setRowLayout(rowLayout) ;
		baseAdapter.setItemList(itemList) ;
		if (itemListView==null) {
			itemListView = (ListView) getActivity().findViewById(R.id.itemListView) ;
		}
		endlessTAdapter = new ItemListEndlessAdapter(baseAdapter).setBasePath(basePath).setRestClient(client).setType(type) ;
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

	@Background
	protected void loadFilteredItems() {
		Log.d(TAG, "Repopulating the itemList for the list view.") ;
		HashMap<String, String> params = new HashMap<String, String>() ;
		if ((itemFilterValue!=null) && (itemFilterValue.length()>0)) {
			params.put("name__icontains", itemFilterValue) ;
		}
		params.put("limit", limit+"") ;
		params.put("offset", offset+"") ;
		EntityList<GenericEntity> results = client.getObjectList(type, basePath, params) ;
		if (results.getMeta()!=null) {
			totalCount = results.getMeta().getTotal_count();
		} else {
			totalCount = results.getItems().length ;
		}
		itemList = new ArrayList<GenericEntity>() ;
		for (GenericEntity item: results.getItems()) {
			itemList.add(item) ;
		}
		updateListAdapter() ;
	}

	@UiThread
	protected void updateListAdapter() {
		Log.d(TAG, "Resetting the list view adapter to display a new list.") ;
		baseAdapter = new ItemListAdapter(getActivity()).setFilterString(itemFilterValue).setmActivity(getActivity()).setRowLayout(rowLayout) ;
		baseAdapter.setItemList(itemList) ;
		baseAdapter.getFilter().filter(itemFilterValue) ;
		if (itemListView==null) {
			itemListView = (ListView) getActivity().findViewById(R.id.itemListView) ;
		}
		endlessTAdapter = new ItemListEndlessAdapter(baseAdapter).setBasePath(basePath).setRestClient(client).setType(type) ;
		endlessTAdapter.setRunInBackground(true) ;
		itemListView.setAdapter(endlessTAdapter) ;
		itemsLoadingIndicator.setVisibility(View.GONE) ;
	}
}

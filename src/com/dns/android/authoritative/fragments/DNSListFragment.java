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
import android.content.SharedPreferences;
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
import com.dns.android.authoritative.callbacks.ParentedListView;
import com.dns.android.authoritative.domain.EntityList;
import com.dns.android.authoritative.domain.GenericEntity;
import com.dns.android.authoritative.rest.RestClient;
import com.dns.android.authoritative.utils.DNSPrefs_;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ItemLongClick;

/**
 * A generic implementation of {@link SherlockFragment} which allows for display of lists of items which implement {@link GenericEntity}
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
public class DNSListFragment extends SherlockFragment implements ParentedListView {
	protected final String TAG = "" ;

	/**
	 * An instance of this application's {@link SharedPreferences} object
	 */
	protected DNSPrefs_ prefs ;

	/**
	 * A ReST API client for accessing the DNS.com API
	 */
	protected static RestClient client ;

	/**
	 * The {@link ListView} instance which is displayed in this fragment.
	 */
	protected ListView itemListView ;

	/**
	 * An instance of {@link EditText} where the user can input a filter string to filter the {@link ListView}
	 */
	protected EditText itemFilter ;

	/**
	 * An {@link ImageView} which looks like a funnel and when clicked on caused the filter to be applied.
	 */
	protected ImageView itemFilterApply ;

	/**
	 * And instance of {@link ProgressBar} which is displayed when the {@link ItemListAdapter} and {@link ItemListEndlessAdapter} are being updated
	 */
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

	protected Class<? extends GenericEntity> childType ;

	protected Class<? extends GenericEntity> type ;
	protected String basePath = "" ;
	protected String deletePath = "" ;
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

	/**
	 * Sends a ReSTful request to the server to delete the specified {@link GenericEntity}. If it is successful, it will call itemDeleteSuceeded to
	 * remove the item from the {@link ItemListAdapter}. On failure, itemDeleteFailed is called to let the user know that the call failed.
	 * @param item An object which implements {@link GenericEntity} and is to be deleted.
	 */
	protected void deleteItem(GenericEntity item) {
		try {
			if (client.deleteObject(deletePath+item.getId()+"/")) {
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

	/**
	 * Lets the user know that the call to deleteItem failed.
	 * @param item The {@link GenericEntity} item which the application attempted to delete via ReSTful call
	 */
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

	/**
	 * Removes the specified {@link GenericEntity} from the {@link ItemListAdapter} and notifies the adapter that the data changed.
	 * @param item The item which was successfully removed by a call to deleteItem({@link GenericEntity})
	 */
	protected void itemDeleteSuceeded(GenericEntity item) {
		baseAdapter.notifyDataSetChanged() ;
	}

	/**
	 * Handles an {@link ItemClick} event for the {@link ListView}
	 * @param clickedItem The {@link GenericEntity} item which was clicked on in the {@link ListView}
	 */
	protected void handleItemClick(final GenericEntity clickedItem) {
		mListener.onItemSelected(clickedItem.getId(), childType) ;
	}

	/**
	 * Handles an {@link ItemLongClick} event for the {@link ListView}
	 * @param clickedItem The {@link GenericEntity} item which was clicked on in the {@link ListView}
	 */
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

	/**
	 * Sets up the user interface and starts the ball rolling for populating the {@link ListView}
	 */
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

	/**
	 * Performs a ReST request to load the initial list of {@link GenericEntity} objects.
	 */
	protected void loadInitialItems() {
		HashMap<String, String> params = new HashMap<String, String>() ;
		if ((itemFilterValue!=null) && (itemFilterValue.length()>0)) {
			params.put(filterType, itemFilterValue) ;
		}
		params.put("limit", limit+"") ;
		params.put("offset", offset+"") ;
		EntityList<? extends GenericEntity> results = client.getObjectList(type, basePath, params) ;
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

	/**
	 * Creates the {@link ItemListAdapter} and {@link ItemListEndlessAdapter} objects and then assigns them accordingly
	 */
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

	/**
	 * Handles the {@link Click} event for the {@link ImageView} itemFilterApply
	 */
	public void onSearchApply() {
		Log.d(TAG, "Filter button pressed.") ;
		itemFilterValue = itemFilter.getText().toString() ;
		itemsLoadingIndicator.setVisibility(View.VISIBLE) ;
		limit = 0 ;
		offset = 0 ;
		loadFilteredItems() ;
	}

	/**
	 * Performs a ReST request in the background in order to load a filtered list of {@link GenericEntity} objects 
	 */
	public void loadFilteredItems() {
		Log.d(TAG, "Repopulating the itemList for the list view.") ;
		HashMap<String, String> params = new HashMap<String, String>() ;
		if ((itemFilterValue!=null) && (itemFilterValue.length()>0)) {
			params.put("name__icontains", itemFilterValue) ;
		}
		params.put("limit", limit+"") ;
		params.put("offset", offset+"") ;
		EntityList<? extends GenericEntity> results = client.getObjectList(type, basePath, params) ;
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

	/**
	 * Once the loadFilteredItem() call has completed, it calls this method inside of the UI thread to update the {@link ItemListAdapter}
	 */
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

	/* (non-Javadoc)
	 * @see com.dns.android.authoritative.callbacks.ParentedListView#getParentId()
	 */
	@Override
	public Integer getParentId() {
		return parentId ;
	}
}

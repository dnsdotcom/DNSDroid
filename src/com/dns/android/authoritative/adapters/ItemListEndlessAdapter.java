/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.commonsware.cwac.endless.EndlessAdapter;
import com.dns.android.authoritative.R;
import com.dns.android.authoritative.domain.EntityList;
import com.dns.android.authoritative.domain.GenericEntity;
import com.dns.android.authoritative.fragments.DNSListFragment;
import com.dns.android.authoritative.rest.RestClient;

/**
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
public class ItemListEndlessAdapter extends EndlessAdapter {

	private final String TAG = "ItemListEndlessAdapter" ;

	private View pendingView = null;
	private ArrayList<GenericEntity> newItems = null ;
	private ItemListAdapter wrapped = null ;
	protected int newTotalCount = 0 ;
	protected Activity mActivity ;
	protected DNSListFragment parentFragment ;

	protected int limit = 20 ;
	protected int offset = 0 ;
	protected int totalCount = 0 ;
	protected RestClient client = null ;
	protected Class<? extends GenericEntity> type = null ;
	protected String basePath ;

	public ItemListEndlessAdapter(ItemListAdapter wrapped) {
		super(wrapped) ;
		this.wrapped = wrapped ;
		newTotalCount = totalCount ;
	}

	public ItemListEndlessAdapter setRestClient(RestClient client) {
		this.client = client ;
		return this ;
	}

	public ItemListEndlessAdapter setType(Class<? extends GenericEntity> type2) {
		this.type = type2 ;
		return this ;
	}

	public ItemListEndlessAdapter setBasePath(String basePath) {
		this.basePath = basePath ;
		return this ;
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
			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity) ;
			builder.setTitle(mActivity.getResources().getString(R.string.data_changed_title)) ;
			builder.setMessage(mActivity.getResources().getString(R.string.data_changed_message)) ;
			builder.setPositiveButton(mActivity.getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.d(TAG, "Skipping load and refreshing list view.") ;
					mActivity.findViewById(R.id.itemListBusyIndicator).setVisibility(View.VISIBLE) ;
					limit = 0 ;
					offset = 0 ;
					parentFragment.loadFilteredItems() ;
					dialog.dismiss() ;
				}
			}) ;
			builder.setNegativeButton(mActivity.getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss() ;
				}
			}) ;
			builder.show() ;
		}
		if (wrapped.getItemList().size() < totalCount) {
			wrapped.addItemsToList(newItems);
			Log.d(TAG, "Adding '" + newItems.size()
					+ "' items to the list.");
		}
	}

	@Override
	protected boolean cacheInBackground() throws Exception {
		if (wrapped.getItemList().size()<totalCount) {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("limit", limit + "");
			params.put("offset", offset + "");
			String filterString = wrapped.getFilterString();
			if (filterString != null) {
				params.put("name__icontains", filterString);
			}
			EntityList<? extends GenericEntity> results = client.getObjectList(type, basePath, params) ;
			if (newItems == null) {
				newItems = new ArrayList<GenericEntity>();
			} else {
				newItems.clear();
			}
			for (GenericEntity item : results.getItems()) {
				newItems.add(item);
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

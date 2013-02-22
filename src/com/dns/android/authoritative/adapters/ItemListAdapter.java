/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.adapters;

import java.util.ArrayList;

import com.dns.android.authoritative.domain.GenericEntity;
import com.dns.android.authoritative.filters.ItemPredicate;
import com.google.common.collect.Collections2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
public class ItemListAdapter extends ArrayAdapter<GenericEntity> {
	protected ArrayList<GenericEntity> itemList = null ;
	protected ArrayList<GenericEntity> filteredItemList = null ;
	private final String TAG = "ItemListAdapter" ;

	public ArrayList<GenericEntity> getItemList() {
		return itemList;
	}

	public Activity getmActivity() {
		return mActivity;
	}

	public ItemListAdapter setmActivity(Activity mActivity) {
		this.mActivity = mActivity;
		return this ;
	}

	public int getRowLayout() {
		return rowLayout;
	}

	public ItemListAdapter setRowLayout(int rowLayout) {
		this.rowLayout = rowLayout;
		return this ;
	}

	public ItemListAdapter setFilterString(String filterString) {
		this.filterString = filterString;
		return this ;
	}

	protected String filterString = null ;
	protected Activity mActivity ;
	protected int rowLayout ;

	@Override
	public GenericEntity getItem(int position) {
		if (filteredItemList!=null) {
			if (filteredItemList.size()>0) {
				return filteredItemList.get(position) ;
			}
		}
		return itemList.get(position) ;
	}

	public void setItemList(ArrayList<GenericEntity> list) {
		itemList = list ;
		if (filteredItemList==null) {
			filteredItemList = itemList ;
		}
	}

	public ItemListAdapter(Context context) {
		super(context, android.R.id.text1);
	}

	public String getFilterString() {
		return filterString ;
	}

	public void addItemsToList(ArrayList<GenericEntity> items) {
		this.filteredItemList.addAll(Collections2.filter(items, new ItemPredicate<GenericEntity>(filterString==null?"":filterString))) ;
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
				ArrayList<GenericEntity> filteredItemNames = new ArrayList<GenericEntity>() ;

				if (constraint==null || constraint.length()==0) {
					Log.d(TAG, "No filter value") ;
					results.count = itemList.size() ;
					results.values = itemList ;
				} else {
					Log.d("TListAdapter Filter", "Filtering for: "+constraint) ;
					constraint = constraint.toString().toLowerCase() ;
					filteredItemNames = new ArrayList<GenericEntity>(Collections2.filter(itemList, new ItemPredicate<GenericEntity>(constraint.toString()))) ;
					results.count = filteredItemNames.size() ;
					results.values = filteredItemNames ;
				}

				Log.d(TAG, "Count: "+results.count) ;
				return results ;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				filteredItemList = (ArrayList<GenericEntity>) results.values ;
				notifyDataSetChanged() ;
			}
		} ;
		return filter ;
	}

	@Override
	public int getCount() {
		return filteredItemList.size() ;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final RelativeLayout row = (RelativeLayout) mActivity.getLayoutInflater().inflate(rowLayout, null) ;
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
		itemLabel.setText(filteredItemList.get(position).getName());
		return row;
	}
}

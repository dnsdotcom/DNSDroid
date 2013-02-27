package com.dns.android.authoritative.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.springframework.util.StringUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.dns.android.authoritative.R;
import com.dns.android.authoritative.domain.RR;
import com.googlecode.androidannotations.annotations.AfterInject;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EFragment;

/**
 * A fragment which allows for the viewing and editing of a resource record.
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
@EFragment
public class RRDetailFragment extends SherlockFragment {

	protected final String TAG = "RRDetailFragment" ;

	protected HashMap<String, String[]> fieldMappings ;

	protected RR record ;

	protected View root = null ;

	public void setTargetRR(RR target) {
		this.record = target ;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			root = inflater.inflate(R.layout.rr_detail_fragment, null) ;
			return root ;
	}

	@AfterInject
	protected void configureValues() {
		fieldMappings = new HashMap<String, String[]>() ;
		fieldMappings.put("SOA", StringUtils.tokenizeToStringArray("answer,type,id,geo_group,country,region,city,expire,minimum,retry,ttl", ",")) ;
		fieldMappings.put("MX", StringUtils.tokenizeToStringArray("priority,answer,type,id,geo_group,country,region,city,ttl", ",")) ;
		fieldMappings.put("URLFrame", StringUtils.tokenizeToStringArray("title,keywords,description,answer,type,id,geo_group,country,region,city,ttl", ",")) ;
		fieldMappings.put("SRV", StringUtils.tokenizeToStringArray("answer,type,id,geo_group,country,region,city,ttl,weight,port,priority", ",")) ;
		fieldMappings.put("default", StringUtils.tokenizeToStringArray("answer,type,id,geo_group,country,region,city,ttl", ",")) ;
	}

	@AfterViews
	protected void setupUi() {
		String[] fields = fieldMappings.get(record.getType()) ;
		if (fields==null) {
			fields = fieldMappings.get("default") ;
		}
		if (root==null) {
			Log.e(TAG, "root view is NULL") ;
		}
		for (String field: fields) {
			View temp = root.findViewWithTag(field+"_layout") ;
			if (temp!=null) {
				temp.setVisibility(View.VISIBLE) ;
			}
			temp = root.findViewWithTag(field+"_input") ;
			if (temp instanceof EditText) {
				Field f = null ;
				
				try {
					f = RR.class.getDeclaredField(field);
					f.setAccessible(true) ;
					if (field.contentEquals("type")) {
						Spinner input = (Spinner) temp ;
						String[] typeArray = getActivity().getResources().getStringArray(R.array.record_types) ;
						ArrayList<String> typeList = (ArrayList<String>) Arrays.asList(typeArray) ;
						int index = typeList.indexOf(record.getType()) ;
						input.setSelection(index) ;
					} else if (f.getType().getSimpleName().contentEquals("String")) {
						EditText input = (EditText) temp ;
						String value = (String)f.get(record) ;
						if (value!=null) {
							input.setText(value) ;
						}
					} else if (f.getType().getSimpleName().contentEquals("Integer")) {
						EditText input = (EditText) temp ;
						Integer value = ((Integer)f.get(record)) ;
						if (value!=null) {
							input.setText(value.intValue()+"") ;
						}
					} else if (f.getType().getSimpleName().contentEquals("Boolean")) {
						ToggleButton input = (ToggleButton) temp ;
						Boolean value = f.getBoolean(record) ;
						if (value!=null) {
							input.setChecked(f.getBoolean(record)) ;
						}
					}
				} catch (NoSuchFieldException e) {
					Log.e(TAG, "Field '"+field+"' does not exist") ;
				} catch (IllegalArgumentException e) {
					Log.e(TAG, "Field '"+field+"' does not exist: " + e.getLocalizedMessage()) ;
				} catch (IllegalAccessException e) {
					Log.e(TAG, "Field '"+field+"' not accessible: " + e.getLocalizedMessage()) ;
				}
			}
		}
	}
}

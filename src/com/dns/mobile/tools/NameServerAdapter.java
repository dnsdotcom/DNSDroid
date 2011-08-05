/**
 * 
 */
package com.dns.mobile.tools;

import com.dns.mobile.activities.tools.NameServerItemLayout;
import com.dns.mobile.data.NameServers;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class NameServerAdapter extends BaseAdapter {

	private NameServers nameServers = null ;
	private static NameServerAdapter instance = null ;

	private NameServerAdapter() {
		nameServers = NameServers.getInstance() ;
	}

	public static NameServerAdapter getInstance() {
		if (instance == null) {
			instance = new NameServerAdapter() ;
		}
		return instance ;
	}
	
	public View getView(int position, View selectedView, ViewGroup nsListView) {
		View itemView = new NameServerItemLayout(nsListView.getContext(), nameServers.getNameServer(position), position, getCount()) ;
		itemView.setFocusable(false) ;
		itemView.setFocusableInTouchMode(false) ;
		itemView.setClickable(false) ;
		itemView.setEnabled(false) ;
		return itemView ;
	}
	
	public long getItemId(int position) {
		return position + 48294 ;
	}
	
	public Object getItem(int position) {
		return nameServers.getNameServer(position) ;
	}
	
	public int getCount() {
		return nameServers.getNameServers().size() ;
	}
}

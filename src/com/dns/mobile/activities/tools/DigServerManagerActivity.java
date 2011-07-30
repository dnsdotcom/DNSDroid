/**
 * 
 */
package com.dns.mobile.activities.tools;

import com.dns.mobile.R;
import com.dns.mobile.data.NameServers;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class DigServerManagerActivity extends Activity {

	protected NameServers nameServers = null ;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dns_tools_dig_name_server_config) ;

		nameServers = new NameServers(getBaseContext()) ;

		ListView serverList = (ListView) findViewById(R.id.digNameServerListView) ;
		serverList.setEnabled(false) ;

		serverList.setAdapter(new BaseAdapter() {
			
			public View getView(int position, View selectedView, ViewGroup nsListView) {
				View itemView = new NameServerItemLayout(nsListView.getContext(), nameServers.getNameServer(position), position) ;
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
		}) ;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		nameServers = new NameServers(getBaseContext()) ;

		ListView serverList = (ListView) findViewById(R.id.digNameServerListView) ;
		serverList.setEnabled(false) ;

		serverList.setAdapter(new BaseAdapter() {
			
			public View getView(int position, View selectedView, ViewGroup nsListView) {
				View itemView = new NameServerItemLayout(nsListView.getContext(), nameServers.getNameServer(position), position) ;
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
		}) ;

	}
}

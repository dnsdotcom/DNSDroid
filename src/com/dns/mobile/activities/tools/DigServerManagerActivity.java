/**
 * 
 */
package com.dns.mobile.activities.tools;

import com.dns.mobile.R;
import com.dns.mobile.data.NameServers;
import com.dns.mobile.tools.NameServerAdapter;
import com.dns.mobile.util.LogoOnClickListener;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class DigServerManagerActivity extends Activity {

	private static final String TAG = "DigServerManagerActivity" ;
	protected NameServers nameServers = null ;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dns_tools_dig_name_server_config) ;
		findViewById(R.id.dnsLogo).setOnClickListener(new LogoOnClickListener(this));

		nameServers = NameServers.getInstance(getBaseContext()) ;

		ListView serverList = (ListView) findViewById(R.id.digNameServerListView) ;
		serverList.setEnabled(false) ;

		Log.d(TAG, "Setting up name server list.") ;
		serverList.setAdapter(NameServerAdapter.getInstance()) ;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem addNameServer = menu.add(Menu.NONE, 0, 0, "Add");
		addNameServer.setIcon(android.R.drawable.ic_menu_add) ;
		return super.onCreateOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				Dialog addSrvDialog = new DigAddServerDialog(this) ;
				addSrvDialog.show() ;
				return true;
		}
		return false;
	}
}

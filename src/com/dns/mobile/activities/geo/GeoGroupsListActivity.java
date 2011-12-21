/**
 * 
 */
package com.dns.mobile.activities.geo;

import com.dns.mobile.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class GeoGroupsListActivity extends Activity {

	private static final String TAG = "GeoGroupsListActivity" ;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Starting the GeoGroupsListActivity") ;
		setContentView(R.layout.geo_groups_activity) ;
	}
}

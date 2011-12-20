package com.dns.mobile.activities.groups;

import com.dns.mobile.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class DomainGroupDetailsActivity extends Activity {

	protected String domainGroupName = null ;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.domain_group_details) ;

	    ListView domainGroupDetailsList = (ListView) findViewById(R.id.domainGroupDetailsList) ;
	    domainGroupDetailsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	/* (non-Javadoc)
	    	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	    	 */
	    	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	    		switch (arg2) {
	    			case 0: 
	    				
	    				break ;
	    			case 1: 
	    				break ;
	    			default:
	    				
	    		}
	    	}
		}) ;
	}

}

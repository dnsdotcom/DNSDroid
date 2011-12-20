package com.dns.mobile.activities.groups;

import com.dns.mobile.R;
import com.dns.mobile.activities.records.HostListActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

	    domainGroupName = this.getIntent().getStringExtra("domainGroupName") ;
	    if (domainGroupName==null) {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this) ;
	    	builder.setTitle(R.string.domain_group_null_alert_title) ;
	    	builder.setMessage(R.string.domain_group_null_alert_message) ;
	    	builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					DomainGroupDetailsActivity.this.finish() ;
				}
			}) ;
	    	builder.show();
	    }

	    ListView domainGroupDetailsList = (ListView) findViewById(R.id.domainGroupDetailsList) ;
	    domainGroupDetailsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	/* (non-Javadoc)
	    	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
	    	 */
	    	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	    		switch (arg2) {
	    			case 0: 
	    				// The user asked to view the list of member domains for this domain group.
	    				Intent memberActivity = new Intent(DomainGroupDetailsActivity.this, null) ;
	    				memberActivity.putExtra("domainGroupName", "domainGroupName") ;
	    				DomainGroupDetailsActivity.this.startActivity(memberActivity) ;
	    				break ;
	    			case 1: 
	    				// The user asked to view the host records associated with this domain group
	    				Intent recordsActivity = new Intent(DomainGroupDetailsActivity.this, HostListActivity.class) ;
	    				recordsActivity.putExtra("domainName", domainGroupName) ;
	    				recordsActivity.putExtra("isDomainGroup", true) ;
	    				DomainGroupDetailsActivity.this.startActivity(recordsActivity) ;
	    				break ;
	    			default:
	    				
	    		}
	    	}
		}) ;
	}

}

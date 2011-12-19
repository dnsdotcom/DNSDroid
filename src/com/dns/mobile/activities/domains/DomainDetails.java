package com.dns.mobile.activities.domains;

import com.dns.mobile.R;
import com.dns.mobile.activities.records.CreateNewHostActivity;
import com.dns.mobile.activities.records.HostListActivity;
import com.dns.mobile.util.LogoOnClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class DomainDetails extends Activity {

	protected String domainName = null;
	protected boolean isGroupedDomain = false;
	private static String TAG = "DomainDetails" ;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.domain_details_activity);

		findViewById(R.id.dnsLogo).setOnClickListener(new LogoOnClickListener(this));
		domainName = this.getIntent().getStringExtra("domainName");
		isGroupedDomain = this.getIntent().getBooleanExtra("isDomainGroup", false);
		Log.d(TAG, "domainName: "+domainName) ;
		Log.d(TAG, "isGroupedDomain: "+(isGroupedDomain?"Y":"N")) ;

		((TextView) findViewById(R.id.domainNameLabel)).setText(domainName);
		((TextView) findViewById(R.id.domainNameLabel)).setVisibility(View.VISIBLE) ;

		ListView domainDetailOptions = (ListView) findViewById(R.id.domainDetailsOptionList);
		domainDetailOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.widget.AdapterView.OnItemClickListener#onItemClick(android
			 * .widget.AdapterView, android.view.View, int, long)
			 */
			public void onItemClick(AdapterView<?> listView, View selectedView, int position, long viewId) {
				switch (position) {
					case 0:
						Intent hostListIntent = new Intent(getApplicationContext(), HostListActivity.class);
						hostListIntent.putExtra("domainName", DomainDetails.this.domainName);
						hostListIntent.putExtra("isDomainGroup", DomainDetails.this.isGroupedDomain) ;
						DomainDetails.this.startActivity(hostListIntent) ;
						break;
					case 1:
						Intent newHostIntent = new Intent(getApplicationContext(), CreateNewHostActivity.class) ;
						newHostIntent.putExtra("domainName", DomainDetails.this.domainName) ;
						newHostIntent.putExtra("isDomainGroup", DomainDetails.this.isGroupedDomain) ;
						DomainDetails.this.startActivity(newHostIntent) ;
						break;
					case 2:
						break;
					case 3:
						break;
					case 4:
						break;
					default:
				}
			}
		});
	}

}

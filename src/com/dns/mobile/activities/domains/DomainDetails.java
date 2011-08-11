package com.dns.mobile.activities.domains;

import com.dns.mobile.R;
import com.dns.mobile.activities.records.HostListActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class DomainDetails extends Activity {

	protected String domainName = null;
	protected boolean isGroupedDomain = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.domain_details_activity);

		domainName = this.getIntent().getStringExtra("domainName");
		isGroupedDomain = this.getIntent().getBooleanExtra("isDomainGroup", false);

		((TextView) findViewById(R.id.domainNameLabel)).setText(domainName);
		((TextView) findViewById(R.id.domainNameLabel)).setVisibility(View.VISIBLE);
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
						break;
					case 2:
						break;
					case 3:
						break;
					case 4:
						break;
					case 5:
						break;
					default:
				}
			}
		});
	}

}

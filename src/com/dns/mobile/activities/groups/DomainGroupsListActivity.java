package com.dns.mobile.activities.groups;

import java.util.ArrayList;
import com.dns.mobile.R;
import com.dns.mobile.data.DomainGroup;
import com.dns.mobile.tools.DomainGroupAdapter;
import com.dns.mobile.util.DomainGroupListTask;
import com.dns.mobile.util.LogoOnClickListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * An <code>Activity</code> which shows a list of domains for the given user's API token
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class DomainGroupsListActivity extends Activity {

	private static final String TAG = "DomainGroupsListActivity" ;
	protected ArrayList<DomainGroup> domainGroupList = null ;
	protected ListView groupListView = null ;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.domain_groups_activity) ;
		findViewById(R.id.dnsLogo).setOnClickListener(new LogoOnClickListener(this));
		((TextView)findViewById(R.id.headerLabel)).setText(R.string.domain_groups_list_label) ;

		domainGroupList = new ArrayList<DomainGroup>() ;
		findViewById(R.id.viewRefreshProgressBar).setVisibility(View.VISIBLE) ;
		findViewById(R.id.viewRefreshButton).setVisibility(View.GONE) ;

		groupListView = (ListView) findViewById(R.id.groupListView) ;
		groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> domainListView, View selectedView, int position, long itemId) {
				Log.d(TAG, "Item at position '"+position+"' pressed.") ;
				DomainGroup selected ;
				Intent domainGroupDetailsActivity ;
				if (position>0) {
					selected = domainGroupList.get(position - 1);
					domainGroupDetailsActivity = new Intent(getApplicationContext(), DomainGroupDetailsActivity.class);
					domainGroupDetailsActivity.putExtra("domainGroupName", selected.getName()) ;
				} else {
					domainGroupDetailsActivity = new Intent(getApplicationContext(), CreateNewDomainGroup.class) ;
				}
				startActivity(domainGroupDetailsActivity) ;
			}
			
		}) ;
		groupListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> domainListView, View selectedView, int position, long itemId) {
				Log.d(TAG, "Item at position '"+position+"' long pressed.") ;
				return true;
			}
			
		}) ;

		groupListView.setAdapter(new DomainGroupAdapter(domainGroupList)) ;

		new DomainGroupListTask(this, domainGroupList, groupListView).execute() ;

		((ImageView)findViewById(R.id.viewRefreshButton)).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				findViewById(R.id.viewRefreshButton).setVisibility(View.GONE) ;
				findViewById(R.id.viewRefreshProgressBar).setVisibility(View.VISIBLE) ;
				new DomainGroupListTask(DomainGroupsListActivity.this, domainGroupList, groupListView).execute() ;
			}
		}) ;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		findViewById(R.id.viewRefreshButton).setVisibility(View.GONE) ;
		findViewById(R.id.viewRefreshProgressBar).setVisibility(View.VISIBLE) ;
		domainGroupList.clear() ;
		new DomainGroupListTask(DomainGroupsListActivity.this, domainGroupList, groupListView).execute() ;
	}
}

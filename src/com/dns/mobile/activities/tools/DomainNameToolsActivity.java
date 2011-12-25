/**
 * 
 */
package com.dns.mobile.activities.tools;

import java.util.ArrayList;

import com.dns.mobile.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class DomainNameToolsActivity extends Activity {

	protected ArrayList<String> menuOptions = null ;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState) ;
		Log.d("DomainNameToolsActivity", "Starting DomainNameToolsActivity") ;
		setContentView(R.layout.dns_tools_layout) ;
		findViewById(R.id.viewRefreshProgressBar).setVisibility(View.GONE) ;
		((TextView)findViewById(R.id.headerLabel)).setText(R.string.dns_tools_header_label) ;

		Resources res = getResources() ;

		menuOptions = new ArrayList<String>() ;
		menuOptions.add(res.getString(R.string.dns_tools_whois)) ;
		menuOptions.add(res.getString(R.string.dns_tools_dig)) ;

		ListView toolsMenuList = ((ListView)findViewById(R.id.dns_tools_menu_list)) ;
		toolsMenuList.setAdapter(new BaseAdapter() {
			
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView item = new TextView(getBaseContext()) ;
				Log.d("DomainNameToolsActivity", "Creating view for: "+menuOptions.get(position)) ;
				item.setText(menuOptions.get(position)) ;
				item.setTextColor(Color.WHITE) ;
				item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25) ;
				item.setPadding(2, 4, 2, 4) ;
				return item;
			}
			
			public long getItemId(int position) {
				return (position+600);
			}
			
			public Object getItem(int position) {
				return menuOptions.get(position);
			}
			
			public int getCount() {
				return menuOptions.size() ;
			}
		}) ;

		toolsMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			/* (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
			 */
			public void onItemClick(AdapterView<?> toolsMenuList, View selectedView, int position, long itemId) {
				Intent toolActivity = new Intent("android.intent.action.VIEW") ;
				switch (position) {
					case 0:
						toolActivity.setClass(getApplicationContext(), WhoisActivity.class) ;
						break ;
					case 1:
						toolActivity.setClass(getApplicationContext(), DigDnsLookupActivity.class) ;
						break ;
				}
				startActivity(toolActivity) ;
			}
		}) ;
		toolsMenuList.invalidateViews() ;
	}
}

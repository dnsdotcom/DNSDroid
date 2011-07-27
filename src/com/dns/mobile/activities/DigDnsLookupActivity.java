/**
 * 
 */
package com.dns.mobile.activities;

import com.dns.mobile.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class DigDnsLookupActivity extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dns_tool_dig_layout) ;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem sendEmail = menu.add(Menu.NONE, 0, 0, "E-Mail");
		sendEmail.setIcon(android.R.drawable.ic_menu_send) ;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				String digResults = ((EditText)findViewById(R.id.digResponseArea)).getText().toString() ;
				String resolvedHost = ((EditText)findViewById(R.id.digFqdnInput)).getText().toString() ;
				String subjectLine = "DIG Result for: "+resolvedHost ;
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND) ;
				emailIntent.setType("plain/text") ;
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subjectLine) ;
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "The following DNS lookup was done from the DNS.com mobile app.\n\n"+digResults) ;
				startActivity(emailIntent) ;
				return true;
		}
		return false;
	}
}

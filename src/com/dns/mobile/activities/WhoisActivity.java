/**
 * 
 */
package com.dns.mobile.activities;

import java.io.IOException;

import com.dns.mobile.R;
import com.dns.mobile.tools.Whois;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class WhoisActivity extends Activity {

	private class WhoisTask extends AsyncTask<String, Void, String> {
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(String... params) {
			String domainName = params[0] ;

			try {
				return Whois.lookup(domainName) ;
			} catch (IOException e) {
				return e.getLocalizedMessage() ;
			}
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			((EditText)findViewById(R.id.whoisResultTextArea)).setText(result) ;
			findViewById(R.id.whoisProgressBar).setVisibility(View.GONE) ;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dns_tool_whois_layout) ;

		((Button)findViewById(R.id.whoisButton)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				findViewById(R.id.whoisProgressBar).setVisibility(View.VISIBLE) ;
				String domainName = ((EditText)findViewById(R.id.whoisDomainInput)).getText().toString() ;
				new WhoisTask().execute(domainName) ;
			}
		}) ;

		((EditText)findViewById(R.id.whoisDomainInput)).setOnKeyListener(new View.OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode==KeyEvent.KEYCODE_ENTER) {
					findViewById(R.id.whoisProgressBar).setVisibility(View.VISIBLE) ;
					String domainName = ((EditText)findViewById(R.id.whoisDomainInput)).getText().toString() ;
					new WhoisTask().execute(domainName) ;
					return true ;
				}
				return false;
			}
		}) ;
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
				String whoisResults = ((EditText)findViewById(R.id.whoisResultTextArea)).getText().toString() ;
				String resolvedHost = ((EditText)findViewById(R.id.whoisDomainInput)).getText().toString() ;
				String subjectLine = "WHOIS Result for: "+resolvedHost ;
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND) ;
				emailIntent.setType("plain/text") ;
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subjectLine) ;
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "The following WHOIS lookup was done from the DNS.com mobile app.\n\n"+whoisResults) ;
				startActivity(emailIntent) ;
				return true;
		}
		return false;
	}
}

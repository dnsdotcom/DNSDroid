/**
 * 
 */
package com.dns.mobile.activities;

import java.io.IOException;

import com.dns.mobile.R;
import com.dns.mobile.tools.Whois;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
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
	}
}

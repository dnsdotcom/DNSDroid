/**
 * 
 */
package com.dns.mobile.activities;

import com.dns.mobile.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class ConfigurationActivity extends PreferenceActivity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences) ;
	}
}

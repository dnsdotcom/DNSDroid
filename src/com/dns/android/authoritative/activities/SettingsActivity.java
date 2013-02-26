package com.dns.android.authoritative.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.dns.android.authoritative.R;
import com.dns.android.authoritative.rest.RestClient;
import com.dns.android.authoritative.utils.DNSPrefs_;
import com.dns.android.authoritative.utils.DNSPrefs_.DNSPrefsEditor_;
import com.googlecode.androidannotations.annotations.AfterTextChange;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

@EActivity(R.layout.settings_layout)
public class SettingsActivity extends SherlockActivity {

	protected final String TAG = "SettingsActivity" ;

	@Pref
	DNSPrefs_ prefs ;

	@ViewById(R.id.tokenRetrieveButton)
	protected Button getTokenButton ;

	@ViewById(R.id.loadingAuthTokenIndicator)
	protected ProgressBar loadingAuthTokenIndicator ;

	@ViewById(R.id.tokenInput)
	protected EditText tokenInput ;

	@ViewById(R.id.appUrlInput)
	protected EditText webAppURL ;

	@Bean
	protected RestClient client ;

	@Click(R.id.tokenRetrieveButton)
	protected void showAuthTokenDialog() {
		final Dialog tokenDialog = new Dialog(SettingsActivity.this) ;
		tokenDialog.setContentView(R.layout.token_auth_dialog) ;
		tokenDialog.setTitle(tokenDialog.getContext().getResources().getString(R.string.token_retrieve_button_label)) ;
		Button tokenSubmit = (Button) tokenDialog.findViewById(R.id.tokenSubmit) ;
		tokenSubmit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String username = ((EditText)tokenDialog.findViewById(R.id.usernameInput)).getText().toString() ;
				String password = ((EditText)tokenDialog.findViewById(R.id.passwordInput)).getText().toString() ;
				tokenDialog.dismiss() ;
				loadingAuthTokenIndicator.setVisibility(View.VISIBLE) ;
				getAuthToken(username, password) ;
			}
		}) ;
		tokenDialog.show() ;
	}

	@AfterTextChange(R.id.appUrlInput)
	protected void saveChangedAppURL() {
		prefs.edit().getBaseAddress().put(webAppURL.getText().toString()).apply() ;
		Log.d(TAG, "Saved URL.") ;
	}

	@AfterTextChange(R.id.tokenInput)
	protected void saveChangedAuthToken() {
		prefs.edit().getAuthToken().put(tokenInput.getText().toString()).apply() ;
		Log.d(TAG, "Saved token.") ;
	}

	@AfterViews
	public void configureListeners() {
		webAppURL.setText(prefs.getBaseAddress().get()) ;
		tokenInput.setText(prefs.getAuthToken().get()) ;
	}

	@Background
	protected void getAuthToken(String username, String password) {
		try {
			String authToken = client.getToken(username, password) ;
			prefs.edit().getAuthToken().put(authToken).apply() ;
			Log.d(TAG, "Saved token.") ;
			authTokenSuccess() ;
		} catch (Throwable e) {
			authTokenFailure() ;
		}
	}

	@UiThread
	protected void authTokenSuccess() {
		tokenInput.setText(prefs.getAuthToken().get()) ;
		loadingAuthTokenIndicator.setVisibility(View.GONE) ;
	}

	@UiThread
	protected void authTokenFailure() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext()) ;
		builder.setTitle(this.getResources().getString(R.string.auth_token_failure_title)) ;
		builder.setMessage(this.getResources().getString(R.string.auth_token_failure_message)) ;
	}

	@Override
	protected void onPause() {
		super.onPause();
		DNSPrefsEditor_ editor = prefs.edit() ;
		editor.getAuthToken().put(tokenInput.getText().toString()) ;
		editor.getBaseAddress().put(webAppURL.getText().toString()) ;
		editor.apply() ;
		Log.d(TAG, "Saved preferences.") ;
	}
}

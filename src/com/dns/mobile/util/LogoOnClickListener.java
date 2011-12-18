/**
 * 
 */
package com.dns.mobile.util;

import com.dns.mobile.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class LogoOnClickListener implements View.OnClickListener {

	private Activity callingActivity = null ;

	public LogoOnClickListener(Activity callingActivity) {
		this.callingActivity = callingActivity ;
	}

	public void onClick(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext()) ;
		builder.setTitle(R.string.open_web_confirmation_title) ;
		builder.setTitle(R.string.open_web_confirmation_msg) ;
		builder.setPositiveButton(R.string.open_web_confirmation_yes, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				Uri uri = Uri.parse("http://www.dns.com/") ;
				callingActivity.startActivity(new Intent(Intent.ACTION_VIEW, uri)) ;
			}
		}) ;
		builder.setNegativeButton(R.string.open_web_confirmation_no, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss() ;
			}
		}) ;
	}
}

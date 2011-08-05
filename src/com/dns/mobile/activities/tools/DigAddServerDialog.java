/**
 * 
 */
package com.dns.mobile.activities.tools;

import com.dns.mobile.R;
import com.dns.mobile.data.NameServers;
import com.dns.mobile.tools.NameServerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class DigAddServerDialog extends Dialog {

	/**
	 * @param context
	 */
	public DigAddServerDialog(Context context) {
		super(context);
	}

	/* (non-Javadoc)
	 * @see android.app.Dialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dig_edit_server_dialog) ;
		this.setTitle(R.string.dig_edit_server_dialog_title) ;
		WindowManager.LayoutParams params = getWindow().getAttributes() ;
		params.width = LayoutParams.FILL_PARENT ;
		getWindow().setAttributes(params) ;

		((Button)findViewById(R.id.digAddServerSaveButton)).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String serverName = ((EditText)DigAddServerDialog.this.findViewById(R.id.diggAddServerNameInput)).getText().toString() ;
				String serverAddr = ((EditText)DigAddServerDialog.this.findViewById(R.id.digAddServerAddrInput)).getText().toString() ;
				NameServers.getInstance().addNameServer(serverName, serverAddr) ;
				NameServerAdapter.getInstance().notifyDataSetChanged() ;
				DigAddServerDialog.this.dismiss() ;
			}
		}) ;

		((Button)findViewById(R.id.digAddServerCancelButton)).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				DigAddServerDialog.this.dismiss() ;
			}
		}) ;
	}

}

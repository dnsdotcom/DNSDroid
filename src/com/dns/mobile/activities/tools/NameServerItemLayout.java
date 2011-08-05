/**
 * 
 */
package com.dns.mobile.activities.tools;

import com.dns.mobile.R;
import com.dns.mobile.data.NameServer;
import com.dns.mobile.data.NameServers;
import com.dns.mobile.tools.NameServerAdapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class NameServerItemLayout extends RelativeLayout {

	/**
	 * @param context
	 */
	public NameServerItemLayout(Context context) {
		super(context);
	}

	public NameServerItemLayout(Context context, NameServer ns, int position, int count) {
		super(context) ;
		final Context ctx = context ;
		final String serverName = ns.getName() ;

		TextView nameLabel = new TextView(context) ;
		nameLabel.setText(ns.getName()) ;
		nameLabel.setTextColor(Color.WHITE) ;
		nameLabel.setId(18) ;
		nameLabel.setHeight(30) ;
		nameLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14) ;
		nameLabel.setOnLongClickListener(new View.OnLongClickListener() {

			// The user long clicked on a name server and should be prompted to be sure they wish to delete it
			public boolean onLongClick(View selectedView) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ctx) ;
				builder.setTitle(R.string.dig_config_delete_title) ;
				final String nameServerName = ((TextView)selectedView).getText().toString() ;
				String deleteMessage = getResources().getString(R.string.dig_config_delete_message).replaceAll("NAMESERVER", nameServerName) ;
				builder.setMessage(deleteMessage) ;

				builder.setPositiveButton(getResources().getString(R.string.dig_config_delete_positive), new DialogInterface.OnClickListener() {
					// The user confirmed the delete of a name server from the UI
					public void onClick(DialogInterface dialog, int which) {
						NameServers.getInstance().deleteNameServer(nameServerName) ;
						NameServerAdapter.getInstance().notifyDataSetChanged() ;
						dialog.dismiss() ;
					}
				}) ;

				builder.setNegativeButton(getResources().getString(R.string.dig_config_delete_negative), new DialogInterface.OnClickListener() {
					// The user cancelled the delete of the name server
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss() ;
					}
				}) ;

				builder.show() ;

				((ListView)selectedView.getParent().getParent()).invalidateViews() ;
				return false;
			}
		}) ;

		
		ImageView moveUp = new ImageView(context) ;
		moveUp.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_up_float)) ;
		moveUp.setId(19) ;
		moveUp.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View selectedView) {
				NameServers.getInstance(ctx).moveUp(serverName) ;
				NameServerAdapter.getInstance().notifyDataSetChanged() ;
			}
		}) ;

		ImageView moveDown = new ImageView(context) ;
		moveDown.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float)) ;
		moveDown.setId(20) ;
		moveDown.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View selectedView) {
				NameServers.getInstance(ctx).moveDown(serverName) ;
				NameServerAdapter.getInstance().notifyDataSetChanged() ;
			}
		}) ;

		RelativeLayout.LayoutParams nameParms = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) ;
		nameParms.addRule(RelativeLayout.ALIGN_PARENT_LEFT) ;
		nameLabel.setLayoutParams(nameParms) ;

		if (position!=0) {
			RelativeLayout.LayoutParams upParms = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) ;
			upParms.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			moveUp.setLayoutParams(upParms) ;

			this.addView(moveUp) ;
		}

		if ((position+1)!=count) {
			RelativeLayout.LayoutParams downParms = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) ;
			if (position==0) {
				downParms.addRule(RelativeLayout.ALIGN_PARENT_RIGHT) ;
			} else {
				downParms.addRule(RelativeLayout.LEFT_OF, moveUp.getId()) ;
			}
			moveDown.setLayoutParams(downParms) ;
			this.addView(moveDown) ;
		}
		this.addView(nameLabel) ;
	}
}

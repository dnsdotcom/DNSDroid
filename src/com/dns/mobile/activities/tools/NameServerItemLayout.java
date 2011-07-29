/**
 * 
 */
package com.dns.mobile.activities.tools;

import com.dns.mobile.data.NameServer;
import com.dns.mobile.data.NameServers;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
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

	public NameServerItemLayout(Context context, NameServer ns, int position) {
		super(context) ;
		final Context ctx = context ;
		final int pos = position ;

		TextView nameLabel = new TextView(context) ;
		nameLabel.setText(ns.getName()) ;
		nameLabel.setTextColor(Color.WHITE) ;
		nameLabel.setId(18) ;
		nameLabel.setHeight(30) ;
		nameLabel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14) ;

		
		ImageView moveUp = new ImageView(context) ;
		moveUp.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_up_float)) ;
		moveUp.setId(19) ;
		moveUp.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				new NameServers(ctx).moveUp(pos) ;
			}
		}) ;

		ImageView moveDown = new ImageView(context) ;
		moveDown.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float)) ;
		moveDown.setId(20) ;
		moveDown.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				new NameServers(ctx).moveUp(pos+1) ;
			}
		}) ;

		RelativeLayout.LayoutParams nameParms = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) ;
		nameParms.addRule(RelativeLayout.ALIGN_PARENT_LEFT) ;
		nameLabel.setLayoutParams(nameParms) ;

		RelativeLayout.LayoutParams upParms = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) ;
		upParms.addRule(RelativeLayout.ALIGN_PARENT_RIGHT) ;
		moveDown.setLayoutParams(upParms) ;

		RelativeLayout.LayoutParams downParms = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT) ;
		downParms.addRule(RelativeLayout.LEFT_OF, moveDown.getId()) ;
		moveUp.setLayoutParams(downParms) ;

		if (position!=0) {
			this.addView(moveUp) ;
		}
		this.addView(nameLabel) ;
		this.addView(moveDown) ;
	}
}

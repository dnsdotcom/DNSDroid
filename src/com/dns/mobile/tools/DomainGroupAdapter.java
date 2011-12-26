/**
 * 
 */
package com.dns.mobile.tools;

import java.util.ArrayList;

import com.dns.mobile.data.DomainGroup;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class DomainGroupAdapter extends BaseAdapter {

	ArrayList<DomainGroup> dgList = null ;

	/**
	 * 
	 */
	public DomainGroupAdapter(ArrayList<DomainGroup> domainGroups) {
		super() ;
		this.dgList = domainGroups ;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		TextView domainItem = new TextView(parent.getContext()) ;
		domainItem.setTextColor(Color.WHITE) ;
		domainItem.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25) ;
		domainItem.setBackgroundColor(Color.TRANSPARENT) ;
		domainItem.setWidth(LayoutParams.FILL_PARENT) ;
		if (position==0) {
			domainItem.setText("[New Group]") ;
		} else {
			if (dgList.size()>0) {
				DomainGroup currentDomain = dgList.get(position - 1);
				domainItem.setText(currentDomain.getName());
			}
		}
		return domainItem ;
	}
	
	public long getItemId(int position) {
		return position+400 ;
	}
	
	public Object getItem(int position) {
		return dgList.get(position-1) ;
	}
	
	public int getCount() {
		return dgList.size()+1 ;
	}

}

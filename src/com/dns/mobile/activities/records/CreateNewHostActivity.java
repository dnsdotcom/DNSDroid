package com.dns.mobile.activities.records;

import com.dns.mobile.R;
import com.dns.mobile.data.ResourceRecord;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateNewHostActivity extends Activity {

	protected String domainName = null ;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_host_layout) ;

		domainName = this.getIntent().getStringExtra("domainName") ;
		TextView newHostHeader = (TextView)findViewById(R.id.newHostHeader) ;
		String hostHeaderContent = newHostHeader.getText().toString()+": "+domainName ;
		newHostHeader.setText(hostHeaderContent) ;

		((Spinner)findViewById(R.id.hostType)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			/* (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
			 */
			public void onItemSelected(AdapterView<?> recordTypeList, View selectedView, int position, long itemId) {

				findViewById(R.id.rrAnswer).setVisibility(View.GONE) ;
				findViewById(R.id.rrAnswerLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrTtlInput).setVisibility(View.GONE) ;
				findViewById(R.id.rrTtlLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrPriority).setVisibility(View.GONE) ;
				findViewById(R.id.rrPriorityLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrSrvPortLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrSrvPort).setVisibility(View.GONE) ;
				findViewById(R.id.rrSrvWeightLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrSrvWeight).setVisibility(View.GONE) ;
				findViewById(R.id.rrExpire).setVisibility(View.GONE) ;
				findViewById(R.id.rrExpireLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrMinimum).setVisibility(View.GONE) ;
				findViewById(R.id.rrMinimumLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrRetryInterval).setVisibility(View.GONE) ;
				findViewById(R.id.rrRetryLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrPriority).setVisibility(View.GONE) ;
				findViewById(R.id.rrPriorityLabel).setVisibility(View.GONE) ;
				findViewById(R.id.rrResponsibleParty).setVisibility(View.GONE) ;
				findViewById(R.id.rrResponsiblePartyLabel).setVisibility(View.GONE) ;
				switch (ResourceRecord.getTypeForIdentifier((String)recordTypeList.getSelectedItem())) {
					case 6:
						// SOA Record
						findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrExpire).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrExpireLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrMinimum).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrMinimumLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrRetryInterval).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrRetryLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrResponsibleParty).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrResponsiblePartyLabel).setVisibility(View.VISIBLE) ;
						break ;
					case 15:
						findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrPriority).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrPriorityLabel).setVisibility(View.VISIBLE) ;
						break ;
					case 33:
						findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrPriority).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrPriorityLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrSrvPortLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrSrvPort).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrSrvWeightLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrSrvWeight).setVisibility(View.VISIBLE) ;
						break ;
					default:
						findViewById(R.id.rrAnswer).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrAnswerLabel).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrTtlInput).setVisibility(View.VISIBLE) ;
						findViewById(R.id.rrTtlLabel).setVisibility(View.VISIBLE) ;
				}
			}

			/* (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
			 */
			public void onNothingSelected(AdapterView<?> recordTypeList) {
				recordTypeList.setSelection(0) ;
			}
		}) ;

	}
}

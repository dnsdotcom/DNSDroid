/**
 * 
 */
package com.dns.android.authoritative.fragments;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.dns.android.authoritative.R;
import com.dns.android.authoritative.callbacks.SizedFragmentPage;
import com.dns.android.authoritative.domain.Domain;
import com.dns.android.authoritative.domain.GenericEntity;
import com.dns.android.authoritative.domain.Host;
import com.dns.android.authoritative.rest.RestClient;
import com.dns.android.authoritative.utils.DNSPrefs_;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.ItemClick;
import com.googlecode.androidannotations.annotations.ItemLongClick;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

/**
 * A fragment which displays a {@link ListView} of hosts for the selected domain.
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
@EFragment(R.layout.hosts_fragment)
public class HostsFragment extends DNSListFragment implements SizedFragmentPage {
	protected final String TAG = "DomainsFragment" ;

	@Pref
	protected DNSPrefs_ prefs ;

	@Bean
	protected static RestClient client ;

	@ViewById(R.id.itemListView)
	protected ListView itemListView ;

	@ViewById(R.id.itemFilter)
	protected EditText itemFilter ;

	@ViewById(R.id.itemFilterApply)
	protected ImageView domFilterApply ;

	@ViewById(R.id.itemListBusyIndicator)
	protected ProgressBar itemsLoadingIndicator ;

	/* (non-Javadoc)
	 * @see com.dns.android.authoritative.fragments.DNSListFragment#deleteItem(com.dns.android.authoritative.domain.GenericEntity)
	 */
	@Override
	@UiThread
	protected void deleteItem(GenericEntity item) {
		super.deleteItem(item);
	}

	/* (non-Javadoc)
	 * @see com.dns.android.authoritative.fragments.DNSListFragment#handleItemClick(com.dns.android.authoritative.domain.GenericEntity)
	 */
	@Override
	@ItemClick(R.id.itemListView)
	protected void handleItemClick(GenericEntity clickedItem) {
		super.handleItemClick(clickedItem);
	}

	/* (non-Javadoc)
	 * @see com.dns.android.authoritative.fragments.DNSListFragment#handleItemLongClick(com.dns.android.authoritative.domain.GenericEntity)
	 */
	@Override
	@ItemLongClick(R.id.itemListView)
	protected void handleItemLongClick(GenericEntity longClickItem) {
		super.handleItemLongClick(longClickItem);
	}

	/* (non-Javadoc)
	 * @see com.dns.android.authoritative.fragments.DNSListFragment#itemDeleteFailed(com.dns.android.authoritative.domain.GenericEntity)
	 */
	@UiThread
	protected void itemDeleteFailed(GenericEntity item) {
		super.itemDeleteFailed(item);
	}

	/* (non-Javadoc)
	 * @see com.dns.android.authoritative.fragments.DNSListFragment#itemDeleteSuceeded(com.dns.android.authoritative.domain.GenericEntity)
	 */
	@Override
	@UiThread
	protected void itemDeleteSuceeded(GenericEntity item) {
		super.itemDeleteSuceeded(item);
	}

	/* (non-Javadoc)
	 * @see com.dns.android.authoritative.fragments.DNSListFragment#loadFilteredItems()
	 */
	@Override
	@Background
	public void loadFilteredItems() {
		super.loadFilteredItems();
	}

	/* (non-Javadoc)
	 * @see com.dns.android.authoritative.fragments.DNSListFragment#loadInitialItems()
	 */
	@Override
	@Background
	protected void loadInitialItems() {
		super.loadInitialItems();
	}

	/* (non-Javadoc)
	 * @see com.dns.android.authoritative.fragments.DNSListFragment#uiSetup()
	 */
	@Override
	@AfterViews
	protected void uiSetup() {
		super.uiSetup();

		childType = Host.class ;
		type = Domain.class ;
		basePath = "/hosts/domain/" + parentId + "/" ;
		deletePath = "/hosts/" ;
		rowLayout = R.layout.host_row ;
	}

	/* (non-Javadoc)
	 * @see com.dns.android.authoritative.fragments.DNSListFragment#updateListAdapter()
	 */
	@Override
	@UiThread
	protected void updateListAdapter() {
		super.updateListAdapter();
	}

	/* (non-Javadoc)
	 * @see com.dns.android.authoritative.fragments.DNSListFragment#setListViewAdapter()
	 */
	@Override
	@UiThread
	protected void setListViewAdapter() {
		super.setListViewAdapter();
	}

	/* (non-Javadoc)
	 * @see com.dns.android.authoritative.callbacks.SizedFragmentPage#getSize()
	 */
	@Override
	public float getSize() {
		return 0.3f;
	}
}

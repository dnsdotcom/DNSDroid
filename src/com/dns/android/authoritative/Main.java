package com.dns.android.authoritative;

import java.util.HashMap;
import java.util.Stack;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dns.android.authoritative.activities.SettingsActivity_;
import com.dns.android.authoritative.domain.Domain;
import com.dns.android.authoritative.domain.Host;
import com.dns.android.authoritative.domain.RR;
import com.dns.android.authoritative.fragments.DomainGroupsFragment_;
import com.dns.android.authoritative.fragments.DomainsFragment_;
import com.dns.android.authoritative.fragments.DomainsFragment;
import com.dns.android.authoritative.fragments.GeoGroupsFragment_;
import com.dns.android.authoritative.fragments.HostsFragment_;
import com.dns.android.authoritative.fragments.RRDetailFragment_;
import com.dns.android.authoritative.fragments.RRListFragment_;
import com.dns.android.authoritative.fragments.ToolsFragment_;
import com.dns.android.authoritative.utils.DNSPrefs_;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

/**
 * Main activity which manages all fragments and transitions to other activities
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
@EActivity(R.layout.activity_main)
public class Main 
	extends SherlockFragmentActivity 
	implements DomainsFragment.OnDomainSelectedListener, HostsFragment_.OnHostSelectedListener, RRListFragment_.OnRRSelectedListener {

//	private final static String TAG = "Main";

	protected TabHost mTabHost;
	protected TabManager mTabsAdapter;
	protected static Fragment current ;
	protected static Stack<Fragment> breadCrumbs = new Stack<Fragment>() ;

	@Pref
	protected DNSPrefs_ prefs ;

	protected Bundle savedInstanceState ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.savedInstanceState = savedInstanceState ;
	}

	@AfterViews
	protected void setupActivity() {
		if (!prefs.getAuthToken().exists()) {
			Intent settingsIntent = new Intent() ;
			settingsIntent.setClass(getApplicationContext(), SettingsActivity_.class) ;
			startActivity(settingsIntent) ;
		}

		mTabHost = (TabHost)findViewById(android.R.id.tabhost) ;
		mTabHost.setup() ;


		mTabsAdapter = new TabManager(this, mTabHost, R.id.realtabcontent) ;

		mTabsAdapter.addTab(mTabHost.newTabSpec("domains").setIndicator("Domains"), DomainsFragment_.class, null) ;
		mTabsAdapter.addTab(mTabHost.newTabSpec("domain_groups").setIndicator("Domain Groups"), DomainGroupsFragment_.class, null) ;
		mTabsAdapter.addTab(mTabHost.newTabSpec("geo_groups").setIndicator("Geo Groups"), GeoGroupsFragment_.class, null) ;
		mTabsAdapter.addTab(mTabHost.newTabSpec("tools").setIndicator("Tools"), ToolsFragment_.class, null) ;

		if (savedInstanceState!=null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")) ;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(getResources().getString(R.string.menu_settings))
			.setIcon(android.R.drawable.ic_menu_preferences)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM) ;
		
		return super.onCreateOptionsMenu(menu) ;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent() ;
		intent.setClass(this.getApplicationContext(), SettingsActivity_.class) ;
		startActivity(intent) ;
		return super.onOptionsItemSelected(item);
	}

	/**
     * This is a helper class that implements a generic mechanism for
     * associating fragments with the tabs in a tab host.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between fragments.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabManager supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct fragment shown in a separate content area
     * whenever the selected tab changes.
     */
    public static class TabManager implements TabHost.OnTabChangeListener {
        private final FragmentActivity mActivity;
        private final TabHost mTabHost;
        private final int mContainerId;
        private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
        TabInfo mLastTab;

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;
            private Fragment fragment;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }

            public Fragment getFragment() {
            	return fragment ;
            }
        }

        public Fragment getCurrentTabFragment() {
        	return mLastTab.getFragment() ;
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
            mActivity = activity;
            mTabHost = tabHost;
            mContainerId = containerId;
            mTabHost.setOnTabChangedListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mActivity));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
            }

            mTabs.put(tag, info);
            mTabHost.addTab(tabSpec);
        }

        @Override
        public void onTabChanged(String tabId) {
            TabInfo newTab = mTabs.get(tabId);
            if (mLastTab != newTab) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                if (mLastTab != null) {
                    if (mLastTab.fragment != null) {
                        breadCrumbs.push(mLastTab.fragment) ;
                        ft.addToBackStack(null) ;
                        ft.detach(mLastTab.fragment);
                    }
                }
                if (newTab != null) {
                    if (newTab.fragment == null) {
                        newTab.fragment = Fragment.instantiate(mActivity, newTab.clss.getName(), newTab.args);
                        ft.add(mContainerId, newTab.fragment, newTab.tag);
                    } else {
                        ft.attach(newTab.fragment);
                    }
                }
                mLastTab = newTab;
                current = mLastTab.fragment ;
                ft.commit();
                mActivity.getSupportFragmentManager().executePendingTransactions();
            }
        }
    }

	@Override
	public void onDomainSelected(Domain domain) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction() ;
		ft.addToBackStack(null) ;
		breadCrumbs.push(current) ;
		Fragment frag = Fragment.instantiate(getApplicationContext(), HostsFragment_.class.getName()) ;
		((HostsFragment_)frag).setParentDomain(domain) ;
		ft.replace(R.id.realtabcontent, frag) ;
		ft.setCustomAnimations(R.anim.slide_out_left, R.anim.slide_in_left) ;
		current = frag ;
		ft.commit() ;
	}

	@Override
	public void onHostSelected(Host host) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction() ;
		ft.addToBackStack(null) ;
		breadCrumbs.push(current) ;
		Fragment frag = Fragment.instantiate(getApplicationContext(), RRListFragment_.class.getName()) ;
		((RRListFragment_)frag).setParentHost(host) ;
		ft.replace(R.id.realtabcontent, frag) ;
		ft.setCustomAnimations(R.anim.slide_out_left, R.anim.slide_in_left) ;
		current = frag ;
		ft.commit() ;
	}

	@Override
	public void onRRSelected(RR record) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction() ;
		ft.addToBackStack(null) ;
		breadCrumbs.push(current) ;
		Fragment frag = Fragment.instantiate(getApplicationContext(), RRDetailFragment_.class.getName()) ;
		((RRDetailFragment_)frag).setTargetRR(record) ;
		ft.replace(R.id.realtabcontent, frag) ;
		ft.setCustomAnimations(R.anim.slide_out_left, R.anim.slide_in_left) ;
		current = frag ;
		ft.commit() ;
	}

	@Override
	public void onBackPressed() {
		if (!breadCrumbs.isEmpty()) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.disallowAddToBackStack();
			current = null;
			current = breadCrumbs.pop() ;
			ft.replace(R.id.realtabcontent, current);
			ft.setCustomAnimations(R.anim.slide_out_right, R.anim.slide_in_right);
			ft.commit();
		}
	}
}

package com.dns.android.authoritative;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.dns.android.authoritative.fragments.MenuFragment_;
import com.dns.android.authoritative.fragments.SplashFragment_;
import com.googlecode.androidannotations.annotations.EActivity;
import com.mapsaurus.paneslayout.PanesActivity;
import com.mapsaurus.paneslayout.PanesSizer.PaneSizer;

/**
 * Main activity which manages all fragments and transitions to other activities
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
@EActivity
public class Main extends PanesActivity {

	protected final String TAG = "Main" ;

	private class FragmentPaneSizer implements PaneSizer {

		public static final int DEFAULT_PANE_TYPE = 0 ;
		public static final int LIST_VIEW_PANE_TYPE = 1 ;
		public static final int DETAILS_VIEW_PANE_TYPE = 2 ;

		/* (non-Javadoc)
		 * @see com.mapsaurus.paneslayout.PanesSizer.PaneSizer#getWidth(int, int, int, int)
		 */
		@Override
		public int getWidth(int index, int type, int parentWidth, int parentHeight) {
			if (parentWidth > parentHeight) {
				if (type == DEFAULT_PANE_TYPE && index == 0)
					return (int) (0.25 * parentWidth);
				else if (type == DEFAULT_PANE_TYPE)
					return (int) (0.375 * parentWidth);
				else if (type == LIST_VIEW_PANE_TYPE)
					return (int) (0.375 * parentWidth);
				else if (type == DETAILS_VIEW_PANE_TYPE)
					return parentWidth ;
				else throw new IllegalStateException("Pane has unknown type");
			} else {
				if (type == DEFAULT_PANE_TYPE && index == 0)
					return (int) (0.4 * parentWidth);
				else if (type == DEFAULT_PANE_TYPE)
					return (int) (0.6 * parentWidth);
				else if (type == LIST_VIEW_PANE_TYPE)
					return (int) (0.6 * parentWidth);
				else if (type == DETAILS_VIEW_PANE_TYPE)
					return parentWidth ;
				else throw new IllegalStateException("Pane has unknown type");
			}
		}

		/* (non-Javadoc)
		 * @see com.mapsaurus.paneslayout.PanesSizer.PaneSizer#getType(java.lang.Object)
		 */
		@Override
		public int getType(Object o) {
			Log.d(TAG, "Object Type: "+o.getClass().getSimpleName()) ;
			if (o.getClass().getSimpleName().contains("List")) {
				return LIST_VIEW_PANE_TYPE ;
			} else if (o.getClass().getSimpleName().contains("Detail")) {
				return DETAILS_VIEW_PANE_TYPE ;
			} else if (o.getClass().getSimpleName().contains("Splash")) {
				return DETAILS_VIEW_PANE_TYPE ;
			}
			return DEFAULT_PANE_TYPE ;
		}

		/* (non-Javadoc)
		 * @see com.mapsaurus.paneslayout.PanesSizer.PaneSizer#getFocused(java.lang.Object)
		 */
		@Override
		public boolean getFocused(Object o) {
			return false;
		}
		
	}

	/* (non-Javadoc)
	 * @see com.mapsaurus.paneslayout.PanesActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_dns) ;

		setPaneSizer(new FragmentPaneSizer()) ;

		if (savedInstanceState==null) {
			Fragment menuFragment = new MenuFragment_() ;
			Fragment splashFragment = new SplashFragment_() ;
			setMenuFragment(menuFragment) ;
			addFragment(menuFragment, splashFragment) ;
		}
	}

	/* (non-Javadoc)
	 * @see com.mapsaurus.paneslayout.PanesActivity#updateFragment(android.support.v4.app.Fragment)
	 */
	@Override
	public void updateFragment(Fragment f) {
	}

}

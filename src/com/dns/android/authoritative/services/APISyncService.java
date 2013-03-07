/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.services;

import java.util.Date;

import com.dns.android.authoritative.database.DBHelper;
import com.dns.android.authoritative.rest.RestClient;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EService;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.support.ConnectionSource;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
@EService
public class APISyncService extends IntentService {

	@Bean
	protected static RestClient client ;

	ConnectionSource source = null ;
	DBHelper helper = null ;

	/**
	 * @param name
	 */
	public APISyncService() {
		super("DNSComAPISyncService");
		helper = OpenHelperManager.getHelper(getBaseContext(), DBHelper.class) ;
		source = new AndroidConnectionSource(helper) ;
	}

	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {

		Date last_updated = new Date(intent.getExtras().getLong("latest_record_update", 0)) ;

		// Run the API sync operation and get a success/failure status.
		boolean syncStatus = syncContent(last_updated) ;

		// Build an intent in order to send out a status notification
		Intent statusIntent = new Intent("com.dns.authoritative.api.SYNC_STATUS") ;
		statusIntent.getExtras().putBoolean("sync_complete", syncStatus) ;
		statusIntent.getExtras().putLong("sync_complete_timestamp", (new Date()).getTime()) ;

		// Send the status notifications to all activities which are registered to catch the broadcast.
		LocalBroadcastManager.getInstance(this).sendBroadcast(statusIntent) ;
	}

	protected boolean syncContent(Date latest_record_update) {
		// TODO: Grab all API changes and update since the last API sync completed or ALL updates if a sync was never performed.
		return false ;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onUnbind(android.content.Intent)
	 */
	@Override
	public boolean onUnbind(Intent intent) {
		OpenHelperManager.releaseHelper() ;
		return super.onUnbind(intent);
	}
}

/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.services;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import com.dns.android.authoritative.database.DBHelper;
import com.dns.android.authoritative.domain.City;
import com.dns.android.authoritative.domain.CityList;
import com.dns.android.authoritative.domain.Country;
import com.dns.android.authoritative.domain.CountryList;
import com.dns.android.authoritative.domain.Domain;
import com.dns.android.authoritative.domain.DomainGroup;
import com.dns.android.authoritative.domain.DomainGroupList;
import com.dns.android.authoritative.domain.DomainList;
import com.dns.android.authoritative.domain.GeoGroup;
import com.dns.android.authoritative.domain.GeoGroupList;
import com.dns.android.authoritative.domain.GeoMatch;
import com.dns.android.authoritative.domain.GeoMatchList;
import com.dns.android.authoritative.domain.Host;
import com.dns.android.authoritative.domain.HostList;
import com.dns.android.authoritative.domain.RR;
import com.dns.android.authoritative.domain.RRList;
import com.dns.android.authoritative.domain.Region;
import com.dns.android.authoritative.domain.RegionList;
import com.dns.android.authoritative.rest.RestClient;
import com.dns.android.authoritative.utils.DNSPrefs_;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EService;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;
import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * A Service which runs in the background while the DNS.com app is running in order to perform synchronization
 * of data via API calls and stores the data in the local SQLite DB.
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
@EService
public class APISyncService extends IntentService {

	protected final String TAG = "APISyncService" ;

	public final static int SYNC_STARTING = 0 ;
	public final static int SYNC_RUNNING = 1 ;
	public final static int SYNC_FINISHED = 2 ;
	public final static int SYNC_ERROR = 4 ;
	public final static int SYNC_FAILED = 8 ;

	@Bean
	protected static RestClient client ;

	@Pref
	protected DNSPrefs_ prefs ;

	protected DateFormat df ;

	ConnectionSource source = null ;
	DBHelper helper = null ;
	Dao<Domain, Integer> domainDao ;
	Dao<Host, Integer> hostDao ;
	Dao<RR, Integer> rrDao ;
	Dao<DomainGroup, Integer> dgDao ;
	Dao<Country, Integer> countryDao ;
	Dao<Region, Integer> regionDao ;
	Dao<City, Integer> cityDao ;
	Dao<GeoGroup, Integer> ggDao ;
	Dao<GeoMatch, Integer> gmDao ;

	/**
	 * @param name
	 */
	public APISyncService() {
		super("DNSComAPISyncService");
		helper = OpenHelperManager.getHelper(getBaseContext(), DBHelper.class) ;
		source = new AndroidConnectionSource(helper) ;
		try {
			domainDao = DaoManager.createDao(source, Domain.class) ;
			hostDao = DaoManager.createDao(source, Host.class) ;
			rrDao = DaoManager.createDao(source, RR.class) ;
			dgDao = DaoManager.createDao(source, DomainGroup.class) ;
			countryDao = DaoManager.createDao(source, Country.class) ;
			regionDao = DaoManager.createDao(source, Region.class) ;
			cityDao = DaoManager.createDao(source, City.class) ;
			ggDao = DaoManager.createDao(source, GeoGroup.class) ;
			gmDao = DaoManager.createDao(source, GeoMatch.class) ;
		} catch (SQLException e) {
			Log.e(TAG, e.getLocalizedMessage(), e) ;
			sendStatusBroadcast(SYNC_FAILED, "Init", 0F, (new Date()).getTime(), e) ;
		} 
	}

	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		df = new SimpleDateFormat("YYYY-MM-dd kk:mm", Locale.getDefault()) ;

		Date last_updated = new Date(intent.getExtras().getLong("latest_record_update", 0)) ;

		// Run the API sync operation and get a success/failure status.
		try {
			syncContent(last_updated) ;
			sendStatusBroadcast(SYNC_FINISHED, "MAIN", 0F, (new Date()).getTime());
		} catch (SQLException e) {
			Log.e(TAG, e.getLocalizedMessage(), e) ;
			sendStatusBroadcast(SYNC_ERROR, "INPROGRESS", 0F, (new Date()).getTime(), e) ;
		}

	}

	/**
	 * Send a broadcast to subscribed listeners letting them know the current status of sync operations.
	 * @param syncStatus TRUE if the sync is complete
	 * @param syncItem A {@link String} letting the application know which portion of the sync is in progress
	 * @param percentComplete A percentage value ranging from 0 to 100 to indicate how far along in the sync we have progressed
	 * @param timestamp The time at which this message was sent.
	 */
	protected void sendStatusBroadcast(int syncStatus, String syncItem, float percentComplete, long timestamp) {
		// Build an intent in order to send out a status notification
		Intent statusIntent = new Intent("com.dns.authoritative.api.SYNC_STATUS") ;
		statusIntent.getExtras().putInt("sync_status", syncStatus) ;
		statusIntent.getExtras().putString("sync_item", syncItem) ;
		statusIntent.getExtras().putFloat("percent_complete", percentComplete) ;
		statusIntent.getExtras().putLong("sync_complete_timestamp", timestamp) ;

		// Send the status notifications to all activities which are registered to catch the broadcast.
		LocalBroadcastManager.getInstance(this).sendBroadcast(statusIntent) ;
	}

	/**
	 * Send a broadcast to subscribed listeners letting them know the current status of sync operations. Includes
	 * an Exception error.
	 * @param syncStatus
	 * @param syncItem
	 * @param percentComplete
	 * @param timestamp
	 */
	protected void sendStatusBroadcast(int syncStatus, String syncItem, float percentComplete, long timestamp, Exception e) {
		// Build an intent in order to send out a status notification
		Intent statusIntent = new Intent("com.dns.authoritative.api.SYNC_STATUS") ;
		statusIntent.getExtras().putInt("sync_status", SYNC_ERROR) ;
		statusIntent.getExtras().putString("sync_item", syncItem) ;
		statusIntent.getExtras().putFloat("percent_complete", percentComplete) ;
		statusIntent.getExtras().putLong("sync_complete_timestamp", timestamp) ;
		statusIntent.getExtras().putSerializable("error", e) ;

		// Send the status notifications to all activities which are registered to catch the broadcast.
		LocalBroadcastManager.getInstance(this).sendBroadcast(statusIntent) ;
	}

	/**
	 * Given the date of the most recent changes, download all records from the DNS.com site which have 
	 * changed since and store them locally
	 * @param latest_record_update {@link Date} representing the date and time of the last completed sync
	 * @throws SQLException If there is an error persisting the updates via ORMlite.
	 */
	protected void syncContent(Date latest_record_update) throws SQLException {
		sendStatusBroadcast(SYNC_RUNNING, "loading_domains", 0F, (new Date()).getTime()) ;
		HashMap<String, String> params = new HashMap<String, String>() ;
		params.put("date_last_modified__gt", df.format(latest_record_update)) ;
		sendStatusBroadcast(SYNC_RUNNING, "Domains", 0F, (new Date()).getTime()) ;
		boolean proceed = true ;
		while (proceed) {
			DomainList result = client.getObject(DomainList.class, "/domains/", params) ;
			if (result.getMeta().getNext()==null) {
				proceed = false ;
			} else {
				params.put("offset", result.getMeta().getOffset()+result.getMeta().getLimit()+"") ;
			}
			for (Domain item: result.getDomains()) {
				try {
					domainDao.update(item) ;
				} catch (SQLException sqle) {
					domainDao.create(item) ;
				}
			}
			float percent_complete = ((result.getMeta().getOffset() + result.getDomains().length)/result.getMeta().getTotal_count())*100 ;
			sendStatusBroadcast(SYNC_RUNNING, "loading_domains", percent_complete, (new Date()).getTime()) ;
		}

		sendStatusBroadcast(SYNC_RUNNING, "loading_hosts", 0F, (new Date()).getTime()) ;
		proceed = true ;
		params.remove("offset") ;
		while (proceed) {
			HostList result = client.getObject(HostList.class, "/hosts/domains/", params) ;
			if (result.getMeta().getNext()==null) {
				proceed = false ;
			} else {
				params.put("offset", result.getMeta().getOffset()+result.getMeta().getLimit()+"") ;
			}
			for (Host item: result.getHosts()) {
				try {
					hostDao.update(item) ;
				} catch (SQLException sqle) {
					hostDao.create(item) ;
				}
			}
			float percent_complete = ((result.getMeta().getOffset() + result.getHosts().length)/result.getMeta().getTotal_count())*50 ;
			sendStatusBroadcast(SYNC_RUNNING, "loading_hosts", percent_complete, (new Date()).getTime()) ;
		}

		sendStatusBroadcast(SYNC_RUNNING, "loading_hosts", 50F, (new Date()).getTime()) ;
		proceed = true ;
		params.remove("offset") ;
		while (proceed) {
			HostList result = client.getObject(HostList.class, "/hosts/domain_groups/", params) ;
			if (result.getMeta().getNext()==null) {
				proceed = false ;
			} else {
				params.put("offset", result.getMeta().getOffset()+result.getMeta().getLimit()+"") ;
			}
			for (Host item: result.getHosts()) {
				try {
					hostDao.update(item) ;
				} catch (SQLException sqle) {
					hostDao.create(item) ;
				}
			}
			float percent_complete = ((result.getMeta().getOffset() + result.getHosts().length)/result.getMeta().getTotal_count())*50+50 ;
			sendStatusBroadcast(SYNC_RUNNING, "loading_hosts", percent_complete, (new Date()).getTime()) ;
		}

		sendStatusBroadcast(SYNC_RUNNING, "loading_records", 0F, (new Date()).getTime()) ;
		proceed = true ;
		params.remove("offset") ;
		while (proceed) {
			RRList result = client.getObject(RRList.class, "/rrs/domains/", params) ;
			if (result.getMeta().getNext()==null) {
				proceed = false ;
			} else {
				params.put("offset", result.getMeta().getOffset()+result.getMeta().getLimit()+"") ;
			}
			for (RR item: result.getRrs()) {
				try {
					rrDao.update(item) ;
				} catch (SQLException sqle) {
					rrDao.create(item) ;
				}
			}
			float percent_complete = ((result.getMeta().getOffset() + result.getRrs().length)/result.getMeta().getTotal_count())*50 ;
			sendStatusBroadcast(SYNC_RUNNING, "loading_records", percent_complete, (new Date()).getTime()) ;
		}

		sendStatusBroadcast(SYNC_RUNNING, "loading_records", 50F, (new Date()).getTime()) ;
		proceed = true ;
		params.remove("offset") ;
		while (proceed) {
			RRList result = client.getObject(RRList.class, "/rrs/domain_groups/", params) ;
			if (result.getMeta().getNext()==null) {
				proceed = false ;
			} else {
				params.put("offset", result.getMeta().getOffset()+result.getMeta().getLimit()+"") ;
			}
			for (RR item: result.getRrs()) {
				try {
					rrDao.update(item) ;
				} catch (SQLException sqle) {
					rrDao.create(item) ;
				}
			}
			float percent_complete = ((result.getMeta().getOffset() + result.getRrs().length)/result.getMeta().getTotal_count())*50+50 ;
			sendStatusBroadcast(SYNC_RUNNING, "loading_records", percent_complete, (new Date()).getTime()) ;
		}

		sendStatusBroadcast(SYNC_RUNNING, "loading_domain_groups", 0F, (new Date()).getTime()) ;
		proceed = true ;
		params.remove("offset") ;
		while (proceed) {
			DomainGroupList result = client.getObject(DomainGroupList.class, "/domain_groups/", params) ;
			if (result.getMeta().getNext()==null) {
				proceed = false ;
			} else {
				params.put("offset", result.getMeta().getOffset()+result.getMeta().getLimit()+"") ;
			}
			for (DomainGroup item: result.getDomain_groups()) {
				try {
					dgDao.update(item) ;
				} catch (SQLException sqle) {
					dgDao.create(item) ;
				}
			}
			float percent_complete = ((result.getMeta().getOffset() + result.getDomain_groups().length)/result.getMeta().getTotal_count())*100 ;
			sendStatusBroadcast(SYNC_RUNNING, "loading_domain_groups", percent_complete, (new Date()).getTime()) ;
		}

		sendStatusBroadcast(SYNC_RUNNING, "loading_geo_groups", 0F, (new Date()).getTime()) ;
		proceed = true ;
		params.remove("offset") ;
		while (proceed) {
			GeoGroupList result = client.getObject(GeoGroupList.class, "/geo_groups/", params) ;
			if (result.getMeta().getNext()==null) {
				proceed = false ;
			} else {
				params.put("offset", result.getMeta().getOffset()+result.getMeta().getLimit()+"") ;
			}
			for (GeoGroup item: result.getGeo_groups()) {
				try {
					ggDao.update(item) ;
				} catch (SQLException sqle) {
					ggDao.create(item) ;
				}
			}
			float percent_complete = ((result.getMeta().getOffset() + result.getGeo_groups().length)/result.getMeta().getTotal_count())*100 ;
			sendStatusBroadcast(SYNC_RUNNING, "loading_geo_groups", percent_complete, (new Date()).getTime()) ;
		}

		sendStatusBroadcast(SYNC_RUNNING, "loading_geo_matches", 0F, (new Date()).getTime()) ;
		proceed = true ;
		params.remove("offset") ;
		while (proceed) {
			GeoMatchList result = client.getObject(GeoMatchList.class, "/matches/", params) ;
			if (result.getMeta().getNext()==null) {
				proceed = false ;
			} else {
				params.put("offset", result.getMeta().getOffset()+result.getMeta().getLimit()+"") ;
			}
			for (GeoMatch item: result.getMatches()) {
				try {
					gmDao.update(item) ;
				} catch (SQLException sqle) {
					gmDao.create(item) ;
				}
			}
			float percent_complete = ((result.getMeta().getOffset() + result.getMatches().length)/result.getMeta().getTotal_count())*100 ;
			sendStatusBroadcast(SYNC_RUNNING, "loading_geo_matches", percent_complete, (new Date()).getTime()) ;
		}
		
		if (!prefs.getLocationSyncStatus().get()) {
			// If we are here, it means we have not yet synchronized the location data. This is a LARGE operation.

			sendStatusBroadcast(SYNC_RUNNING, "loading_countries", 0F, (new Date()).getTime()) ;
			proceed = true ;
			params.remove("offset") ;
			while (proceed) {
				CountryList result = client.getObject(CountryList.class, "/countries/", params) ;
				if (result.getMeta().getNext()==null) {
					proceed = false ;
				} else {
					params.put("offset", result.getMeta().getOffset()+result.getMeta().getLimit()+"") ;
				}
				for (Country item: result.getCountries()) {
					try {
						countryDao.update(item) ;
					} catch (SQLException sqle) {
						countryDao.create(item) ;
					}
				}
				float percent_complete = ((result.getMeta().getOffset() + result.getCountries().length)/result.getMeta().getTotal_count())*100 ;
				sendStatusBroadcast(SYNC_RUNNING, "loading_countries", percent_complete, (new Date()).getTime()) ;
			}

			sendStatusBroadcast(SYNC_RUNNING, "loading_regions", 0F, (new Date()).getTime()) ;
			proceed = true ;
			params.remove("offset") ;
			while (proceed) {
				RegionList result = client.getObject(RegionList.class, "/regions/", params) ;
				if (result.getMeta().getNext()==null) {
					proceed = false ;
				} else {
					params.put("offset", result.getMeta().getOffset()+result.getMeta().getLimit()+"") ;
				}
				for (Region item: result.getRegions()) {
					try {
						regionDao.update(item) ;
					} catch (SQLException sqle) {
						regionDao.create(item) ;
					}
				}
				float percent_complete = ((result.getMeta().getOffset() + result.getRegions().length)/result.getMeta().getTotal_count())*100 ;
				sendStatusBroadcast(SYNC_RUNNING, "loading_regions", percent_complete, (new Date()).getTime()) ;
			}

			sendStatusBroadcast(SYNC_RUNNING, "loading_cities", 0F, (new Date()).getTime()) ;
			proceed = true ;
			params.remove("offset") ;
			while (proceed) {
				CityList result = client.getObject(CityList.class, "/cities/", params) ;
				if (result.getMeta().getNext()==null) {
					proceed = false ;
				} else {
					params.put("offset", result.getMeta().getOffset()+result.getMeta().getLimit()+"") ;
				}
				for (City item: result.getCities()) {
					try {
						cityDao.update(item) ;
					} catch (SQLException sqle) {
						cityDao.create(item) ;
					}
				}
				float percent_complete = ((result.getMeta().getOffset() + result.getCities().length)/result.getMeta().getTotal_count())*100 ;
				sendStatusBroadcast(SYNC_RUNNING, "loading_cities", percent_complete, (new Date()).getTime()) ;
			}
		}
		Date completed = new Date() ;
		sendStatusBroadcast(SYNC_RUNNING, "loading_cities", 100F, completed.getTime()) ;
		prefs
			.edit()
			.getLastApiSync().put(completed.getTime())
			.getLocationSyncStatus().put(true)
			.getPopulationState().put(true)
			.apply() ;
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onUnbind(android.content.Intent)
	 */
	@Override
	public boolean onUnbind(Intent intent) {
		OpenHelperManager.releaseHelper() ;

		domainDao = null ;
		hostDao = null ;
		rrDao = null ;
		dgDao = null ;
		countryDao = null ;
		regionDao = null ;
		cityDao = null ;
		ggDao = null ;
		gmDao = null ;
		try {
			source.close() ;
		} catch (SQLException e) {
			Log.e(TAG, e.getLocalizedMessage(), e) ;
		}
		source = null ;
		return super.onUnbind(intent);
	}
}

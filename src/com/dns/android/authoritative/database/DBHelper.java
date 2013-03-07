/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.database;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.dns.android.authoritative.domain.City;
import com.dns.android.authoritative.domain.Country;
import com.dns.android.authoritative.domain.Domain;
import com.dns.android.authoritative.domain.DomainGroup;
import com.dns.android.authoritative.domain.GeoGroup;
import com.dns.android.authoritative.domain.GeoMatch;
import com.dns.android.authoritative.domain.Host;
import com.dns.android.authoritative.domain.RR;
import com.dns.android.authoritative.domain.Region;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

	private final String TAG = "DBHelper" ;

	/**
	 * @param context
	 * @param databaseName
	 * @param factory
	 * @param databaseVersion
	 */
	public DBHelper(Context context, CursorFactory factory) {
		super(context, "com.dns.authoritative", factory, 1);
	}

	/* (non-Javadoc)
	 * @see com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase, com.j256.ormlite.support.ConnectionSource)
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource source) {
		try {
			Log.d(TAG, "Creating database for DNSDroid") ;
			TableUtils.createTable(source, City.class) ;
			TableUtils.createTable(source, Region.class) ;
			TableUtils.createTable(source, Country.class) ;
			TableUtils.createTable(source, Domain.class) ;
			TableUtils.createTable(source, DomainGroup.class) ;
			TableUtils.createTable(source, Host.class) ;
			TableUtils.createTable(source, RR.class) ;
			TableUtils.createTable(source, GeoGroup.class) ;
			TableUtils.createTable(source, GeoMatch.class) ;
			Log.d(TAG, "Successfully created all required tables.") ;
		} catch (SQLException sqle) {
			Log.e(TAG, "Error creating database tables.", sqle) ;
			throw new RuntimeException(sqle) ;
		}
	}

	/* (non-Javadoc)
	 * @see com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, com.j256.ormlite.support.ConnectionSource, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource source, int currentDbVersion, int newDbVersion) {
	}
}

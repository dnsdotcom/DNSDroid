package com.dns.android.authoritative.utils;

import android.content.Context;

import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultLong;
import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultString;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref.Scope;

@SharedPref(mode=Context.MODE_PRIVATE, value=Scope.APPLICATION_DEFAULT)
public interface DNSPrefs {

	@DefaultString("http://home.zanclus.com:7080/rest/v2")
	String getBaseAddress() ;

	String getAuthToken() ;

	@DefaultBoolean(false)
	boolean getDebugMessagesState() ;

	@DefaultBoolean(true)
	boolean getWifiWarningState() ;

	@DefaultBoolean(false)
	boolean getBackgroundSyncState() ;

	@DefaultBoolean(false)
	boolean getPopulationState() ;

	@DefaultLong(0L)
	long getLastApiSync() ;
}

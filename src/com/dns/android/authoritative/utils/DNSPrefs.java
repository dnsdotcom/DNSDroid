package com.dns.android.authoritative.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.googlecode.androidannotations.annotations.sharedpreferences.DefaultString;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref;
import com.googlecode.androidannotations.annotations.sharedpreferences.SharedPref.Scope;

@SharedPref(mode=Context.MODE_PRIVATE, value=Scope.APPLICATION_DEFAULT)
public interface DNSPrefs {

	@DefaultString("http://home.zanclus.com:7080/rest/v2")
	String getBaseAddress() ;

//	@DefaultString("eb349f3e-9174-4a43-a176-ead7076cf402")
//	@DefaultString("R/2^*W#DZ7#31SZ2Q$*LC<PAXRE@")
	String getAuthToken() ;
}

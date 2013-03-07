/**
 * Copyright 2013, DNS.com, LLC
 * All Rights Reserved
 */
package com.dns.android.authoritative.database;

import com.dns.android.authoritative.domain.City;
import com.dns.android.authoritative.domain.Country;
import com.dns.android.authoritative.domain.Domain;
import com.dns.android.authoritative.domain.DomainGroup;
import com.dns.android.authoritative.domain.GeoGroup;
import com.dns.android.authoritative.domain.GeoMatch;
import com.dns.android.authoritative.domain.Host;
import com.dns.android.authoritative.domain.RR;
import com.dns.android.authoritative.domain.Region;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

/**
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws Exception {
		Class[] classes = {
				Domain.class, 
				Host.class, 
				RR.class, 
				City.class, 
				Region.class, 
				Country.class, 
				GeoGroup.class, 
				GeoMatch.class, 
				DomainGroup.class
		} ;
		writeConfigFile("ormlite_config.txt", classes) ;
	}
}

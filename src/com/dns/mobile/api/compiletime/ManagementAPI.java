/*
 * DNS.com Java API - Copyright 2011, DNS, Inc. - All rights reserved.
 * This code is released under the terms of the BSD License. See LICENSE file in the root
 * of this code base for more information.
 */

package com.dns.mobile.api.compiletime;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.util.Log;

/**
 * An implementation of the Management API which validates arguments at compile time
 * @author Deven Phillips <deven.phillips@gmail.com>
 */
public class ManagementAPI extends GenericAPI {

	/**
	 * Constructor
	 * @param apiHost The host name of the server to make API calls against.
	 * @param useSSL Should we use HTTPS connections for API calls?
	 * @param apiToken The API Token for authenticating requests.
	 */
	public ManagementAPI(String apiHost, boolean useSSL, String apiToken) {
		super(apiHost, useSSL, apiToken) ;
	}

	/**
	 * Returns the JSON results of an appendToGeoGroup API call.
	 * @param name The name of the GeoGroup to be appended to
	 * @param iso2Code The 2 character country code for this entry
	 * @param region (OPTIONAL) Either the region name or <code>null</code>
	 * @param city (OPTIONAL) Either the city name or <code>null</code>
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject appendToGeoGroup(String name, String iso2Code, String region, String city) {
		StringBuilder uriBuilder = new StringBuilder("/api/appendToGeoGroup/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;
		uriBuilder.append("&name="+name) ;
		uriBuilder.append("&iso2_code="+iso2Code) ;
		if (region!=null) {
			uriBuilder.append("region="+region) ;
		}
		if (city!=null) {
			uriBuilder.append("city="+city) ;
		}
		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Changes the mode of the specified domain and assigns domain to a group if requested.
	 * @param domain The domain to change the mode on
	 * @param mode The mode to change the domain to (either "advanced" or "group"
	 * @param group The group to assign the domain to
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject assignDomainMode(String domain, String mode, String group) {
		StringBuilder uriBuilder = new StringBuilder("/api/assignDomainMode/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;
		uriBuilder.append("&domain="+domain) ;
		uriBuilder.append("&mode="+mode) ;
		if (group!=null) {
			uriBuilder.append("&group="+group) ;
		}
		
		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Creates a new domain entry in the system using the specified mode and domain name.
	 * @param domain The domain to change the mode on
	 * @param mode The mode to change the domain to (either "advanced" or "group"
	 * @param group (OPTIONAL) The group to assign the domain to or <code>null</code>
	 * @param rname (OPTIONAL) Contact for SOA of this zone. (ex: admin@example.com. is admin.example.com) or <code>null</code>
	 * @param ns (OPTIONAL) List of name servers to use or <code>null</code>
	 * @param primary_wildcard (OPTIONAL) IP or CNAME destination for a wildcard on the primary zone or <code>null</code>
	 * @param primary_wildcard_qtype (OPTIONAL) Either A or CNAME  or <code>null</code>. IS REQUIRED IF `primary_wildcard` IS SUPPLIED
	 * @param default_mx (OPTIONAL) MX Destination for email or <code>null</code>
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createDomain(
			String mode,
			String domain,
			String group,
			String rname,
			String[] ns,
			String primary_wildcard,
			String primary_wildcard_qtype,
			String default_mx) {
		StringBuilder uriBuilder = new StringBuilder("/api/createDomain/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;
		uriBuilder.append("&domain="+domain) ;
		uriBuilder.append("&mode="+mode) ;
		if (group!=null) {
			uriBuilder.append("&group="+group) ;
		}
		if (rname!=null) {
			uriBuilder.append("&rname="+rname) ;
		}
		if (ns!=null) {
			for (int x=0; x<ns.length;x++) {
				uriBuilder.append("&ns="+ns[x]) ;
			}
		}
		if (primary_wildcard!=null) {
			uriBuilder.append("&primary_wildcard="+primary_wildcard) ;
		}
		if (primary_wildcard_qtype!=null) {
			uriBuilder.append("&primary_wildcard_qtype="+primary_wildcard_qtype) ;
		}
		if (default_mx!=null) {
			uriBuilder.append("&default_mx="+default_mx) ;
		}
		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Create a new domain group with the specified default settings
	 * @param name The name of the group to create
	 * @param rname (OPTIONAL) Contact for SOA of this zone. (ex: admin@example.com. is admin.example.com) or <code>null</code>
	 * @param ns (OPTIONAL) List of name servers to use or <code>null</code>
	 * @param primary_wildcard (OPTIONAL) IP or CNAME destination for a wildcard on the primary zone or <code>null</code>
	 * @param primary_wildcard_qtype (OPTIONAL) Either A or CNAME  or <code>null</code>. IS REQUIRED IF `primary_wildcard` IS SUPPLIED
	 * @param default_mx (OPTIONAL) MX Destination for email or <code>null</code>
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createDomainGroup(
				String name,
				String rname,
				String[] ns,
				String primary_wildcard,
				String primary_wildcard_qtype,
				String default_mx) {
		StringBuilder uriBuilder = new StringBuilder("/api/createDomainGroup/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;
		uriBuilder.append("&name="+name) ;
		if (rname!=null) {
			uriBuilder.append("&rname="+rname) ;
		}
		if (ns!=null) {
			for (int x=0; x<ns.length;x++) {
				uriBuilder.append("&ns="+ns[x]) ;
			}
		}
		if (primary_wildcard!=null) {
			uriBuilder.append("&primary_wildcard="+primary_wildcard) ;
		}
		if (primary_wildcard_qtype!=null) {
			uriBuilder.append("&primary_wildcard_qtype="+primary_wildcard_qtype) ;
		}
		if (default_mx!=null) {
			uriBuilder.append("&default_mx="+default_mx) ;
		}
		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Create multiple new domains and optionally assign them to a domain group.
	 * @param domains A list of domains to be added.
	 * @param mode The mode to set for the domains (either "advanced" or "group"
	 * @param group (OPTIONAL) The domain group to assign the domains to or <code>null</code>
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createDomains(String mode, String[] domains, String group) {
		StringBuilder uriBuilder = new StringBuilder("/api/createDomainGroup/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		uriBuilder.append("&mode="+mode) ;
		for (int x=0; x<domains.length; x++) {
			uriBuilder.append("&domains="+domains[x]) ;
		}
		if (group!=null) {
			uriBuilder.append("&group="+group) ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Create a new domain group with the specified name
	 * @param group The name of the new domain group to be created.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createGeoGroup(String group) {
		StringBuilder uriBuilder = new StringBuilder("/api/createDomainGroup/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		uriBuilder.append("&group="+group) ;

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Create a new hostname in the specifed domain/group
	 * @param name The name of the domain/group to have the host added to
	 * @param isGroup Is this change for a domain or a group?
	 * @param host The name of the host to be added
	 * @param isUrlForward Is this a URL forward host?
	 * @param defaultAddr (OPTIONAL) The default address to have undefined traffic sent to or <code>null</code>
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createHostname(String name, boolean isGroup, String host, boolean isUrlForward, String defaultAddr) {
		StringBuilder uriBuilder = new StringBuilder("/api/createHostname/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		if (isGroup) {
			uriBuilder.append("&group="+name) ;
		} else {
			uriBuilder.append("&domain="+name) ;
		}

		uriBuilder.append("&host="+host) ;

		if (isUrlForward) {
			uriBuilder.append("&is_urlforward=true") ;
		}

		if (defaultAddr!=null) {
			uriBuilder.append("&default="+defaultAddr) ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Create a new A record using the specified options
	 * @param name The name of the domain/group to have the record added to
	 * @param isGroup Is this a domain or a domain group?
	 * @param host The host name to have the record added to
	 * @param rdata The response data for this A record.
	 * @param type The type of record to create (This method supports A, AAAA, TXT, NS, and CNAME)
	 * @param isWildcard Is this record a wildcard?
	 * @param geoGroup (OPTIONAL) GeoGroup to set for this record or <code>null</code>
	 * @param iso2Code (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code>
	 * @param region (OPTIONAL) The region to set for this record or <code>null</code>
	 * @param city (OPTIONAL) The city to set for this record or <code>null</code>
	 * @param ttl (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createDefaultRecord(
				String name,
				boolean isGroup,
				String host,
				String rdata,
				String type,
				boolean isWildcard,
				String geoGroup,
				String iso2Code,
				String region,
				String city,
				Integer ttl) {
		StringBuilder uriBuilder = new StringBuilder("/api/createRRData/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		if (isGroup) {
			uriBuilder.append("&group="+name) ;
		} else {
			uriBuilder.append("&domain="+name) ;
		}

		uriBuilder.append("&host="+host) ;
		uriBuilder.append("&type="+type) ;

		if (isWildcard) {
			uriBuilder.append("&is_wildcard=true") ;
		}

		uriBuilder.append("&rdata="+rdata) ;

		if (geoGroup!=null&&(geoGroup.compareTo("null")!=0)) {
			uriBuilder.append("&geoGroup="+geoGroup) ;
		}
		if (iso2Code!=null&&(iso2Code.compareTo("null")!=0)) {
			uriBuilder.append("&country_iso2="+iso2Code) ;
		}
		if (region!=null&&(region.compareTo("null")!=0)) {
			uriBuilder.append("&region="+region) ;
		}
		if (city!=null&&(city.compareTo("null")!=0)) {
			uriBuilder.append("&city="+city) ;
		}
		if (ttl!=null) {
			uriBuilder.append("&ttl="+ttl) ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Create a new A record using the specified options
	 * @param name The name of the domain/group to have the record added to
	 * @param isGroup Is this a domain or a domain group?
	 * @param host The host name to have the record added to
	 * @param rdata The response data for this A record.
	 * @param isWildcard Is this record a wildcard?
	 * @param geoGroup (OPTIONAL) GeoGroup to set for this record or <code>null</code>
	 * @param iso2Code (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code>
	 * @param region (OPTIONAL) The region to set for this record or <code>null</code>
	 * @param city (OPTIONAL) The city to set for this record or <code>null</code>
	 * @param ttl (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createARecord(
				String name,
				boolean isGroup,
				String host,
				String rdata,
				boolean isWildcard,
				String geoGroup,
				String iso2Code,
				String region,
				String city,
				Integer ttl) {
		return createDefaultRecord(name, isGroup, host, rdata, "A", isWildcard, geoGroup, iso2Code, region, city, ttl) ;
	}

	/**
	 * Create a new AAAA record using the specified options
	 * @param name The name of the domain/group to have the record added to
	 * @param isGroup Is this a domain or a domain group?
	 * @param host The host name to have the record added to
	 * @param rdata The response data for this A record.
	 * @param isWildcard Is this record a wildcard?
	 * @param geoGroup (OPTIONAL) GeoGroup to set for this record or <code>null</code>
	 * @param iso2Code (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code>
	 * @param region (OPTIONAL) The region to set for this record or <code>null</code>
	 * @param city (OPTIONAL) The city to set for this record or <code>null</code>
	 * @param ttl (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createAAAARecord(
				String name,
				boolean isGroup,
				String host,
				String rdata,
				boolean isWildcard,
				String geoGroup,
				String iso2Code,
				String region,
				String city,
				Integer ttl) {
		return createDefaultRecord(name, isGroup, host, rdata, "AAAA", isWildcard, geoGroup, iso2Code, region, city, ttl) ;
	}

	/**
	 * Create a new TXT record using the specified options
	 * @param name The name of the domain/group to have the record added to
	 * @param isGroup Is this a domain or a domain group?
	 * @param host The host name to have the record added to
	 * @param rdata The response data for this A record.
	 * @param isWildcard Is this record a wildcard?
	 * @param geoGroup (OPTIONAL) GeoGroup to set for this record or <code>null</code>
	 * @param iso2Code (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code>
	 * @param region (OPTIONAL) The region to set for this record or <code>null</code>
	 * @param city (OPTIONAL) The city to set for this record or <code>null</code>
	 * @param ttl (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createTXTRecord(
				String name,
				boolean isGroup,
				String host,
				String rdata,
				boolean isWildcard,
				String geoGroup,
				String iso2Code,
				String region,
				String city,
				Integer ttl) {
		String safeRData = null ;
		try {
			safeRData = URLEncoder.encode(rdata, "US-ASCII") ;
		} catch (UnsupportedEncodingException uee) {
			safeRData = "" ;
			uee.printStackTrace() ;
		}
		return createDefaultRecord(name, isGroup, host, safeRData, "TXT", isWildcard, geoGroup, iso2Code, region, city, ttl) ;
	}

	/**
	 * Create a new CNAME record using the specified options
	 * @param name The name of the domain/group to have the record added to
	 * @param isGroup Is this a domain or a domain group?
	 * @param host The host name to have the record added to
	 * @param rdata The response data for this A record.
	 * @param isWildcard Is this record a wildcard?
	 * @param geoGroup (OPTIONAL) GeoGroup to set for this record or <code>null</code>
	 * @param iso2Code (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code>
	 * @param region (OPTIONAL) The region to set for this record or <code>null</code>
	 * @param city (OPTIONAL) The city to set for this record or <code>null</code>
	 * @param ttl (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createCNAMERecord(
				String name,
				boolean isGroup,
				String host,
				String rdata,
				boolean isWildcard,
				String geoGroup,
				String iso2Code,
				String region,
				String city,
				Integer ttl) {
		return createDefaultRecord(name, isGroup, host, rdata, "CNAME", isWildcard, geoGroup, iso2Code, region, city, ttl) ;
	}

	/**
	 * Create a new NS record using the specified options
	 * @param name The name of the domain/group to have the record added to
	 * @param isGroup Is this a domain or a domain group?
	 * @param host The host name to have the record added to
	 * @param rdata The response data for this A record.
	 * @param isWildcard Is this record a wildcard?
	 * @param geoGroup (OPTIONAL) GeoGroup to set for this record or <code>null</code>
	 * @param iso2Code (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code>
	 * @param region (OPTIONAL) The region to set for this record or <code>null</code>
	 * @param city (OPTIONAL) The city to set for this record or <code>null</code>
	 * @param ttl (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createNSRecord(
				String name,
				boolean isGroup,
				String host,
				String rdata,
				boolean isWildcard,
				String geoGroup,
				String iso2Code,
				String region,
				String city,
				Integer ttl) {
		return createDefaultRecord(name, isGroup, host, rdata, "NS", isWildcard, geoGroup, iso2Code, region, city, ttl) ;
	}

	/**
	 * Create a new SOA record using the specified options
	 * @param name The name of the domain/group to have the record added to
	 * @param isGroup Is this a domain or a domain group?
	 * @param host The host name to have the record added to
	 * @param rdata The response data for this A record.
	 * @param retry (OPTIONAL) The integer value for retry or <code>null</code> for the default value.
	 * @param minimum (OPTIONAL) The integer value for minimum or <code>null</code> for the default value.
	 * @param expire (OPTIONAL) The integer value for expire or <code>null</code> for the default value.
	 * @param isWildcard Is this record a wildcard?
	 * @param geoGroup (OPTIONAL) GeoGroup to set for this record or <code>null</code>
	 * @param iso2Code (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code>
	 * @param region (OPTIONAL) The region to set for this record or <code>null</code>
	 * @param city (OPTIONAL) The city to set for this record or <code>null</code>
	 * @param ttl (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createSOARecord(
				String name,
				Boolean isGroup,
				String host,
				String rdata,
				Integer retry,
				Integer expire,
				Integer minimum,
				Boolean isWildcard,
				String geoGroup,
				String iso2Code,
				String region,
				String city,
				Integer ttl) {
		StringBuilder uriBuilder = new StringBuilder("/api/createRRData/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		if (isGroup) {
			uriBuilder.append("&group="+name) ;
		} else {
			uriBuilder.append("&domain="+name) ;
		}

		uriBuilder.append("&host="+host) ;
		uriBuilder.append("&type=SOA") ;

		if (isWildcard!=null) {
			if (isWildcard.booleanValue()) {
				uriBuilder.append("&is_wildcard=true") ;
			}
		}

		uriBuilder.append("&rdata="+rdata) ;

		if (geoGroup!=null && (!geoGroup.toLowerCase().contentEquals("null"))) {
			uriBuilder.append("&geoGroup="+geoGroup) ;
		}
		if (iso2Code!=null && (!iso2Code.toLowerCase().contains("null"))) {
			uriBuilder.append("&country_iso2="+geoGroup) ;
		}
		if (region!=null && (!region.toLowerCase().contentEquals("null"))) {
			uriBuilder.append("&region="+region) ;
		}
		if (city!=null && (!city.toLowerCase().contentEquals("null"))) {
			uriBuilder.append("&city="+city) ;
		}
		if (ttl!=null) {
			uriBuilder.append("&ttl="+ttl) ;
		}
		if (minimum!=null) {
			uriBuilder.append("&minimum="+minimum) ;
		}
		if (retry!=null) {
			uriBuilder.append("&retry="+retry) ;
		}
		if (expire!=null) {
			uriBuilder.append("&expire="+expire) ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Create a new SRV record using the specified options
	 * @param name The name of the domain/group to have the record added to
	 * @param isGroup Is this a domain or a domain group?
	 * @param host The host name to have the record added to
	 * @param rdata The response data for this A record.
	 * @param weight The preference weight for this record
	 * @param priority The priority preference for this record
	 * @param port The TCP/UDP port for this service
	 * @param isWildcard Is this record a wildcard?
	 * @param geoGroup (OPTIONAL) GeoGroup to set for this record or <code>null</code>
	 * @param iso2Code (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code>
	 * @param region (OPTIONAL) The region to set for this record or <code>null</code>
	 * @param city (OPTIONAL) The city to set for this record or <code>null</code>
	 * @param ttl (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createSRVRecord(
				String name,
				boolean isGroup,
				String host,
				String rdata,
				int weight,
				int priority,
				int port,
				boolean isWildcard,
				String geoGroup,
				String iso2Code,
				String region,
				String city,
				Integer ttl) {
		StringBuilder uriBuilder = new StringBuilder("/api/createRRData/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		if (isGroup) {
			uriBuilder.append("&group="+name) ;
		} else {
			uriBuilder.append("&domain="+name) ;
		}

		uriBuilder.append("&host="+host) ;

		if (isWildcard) {
			uriBuilder.append("&is_wildcard=true") ;
		}

		uriBuilder.append("&rdata="+rdata) ;
		uriBuilder.append("&weight="+weight) ;
		uriBuilder.append("&port="+port) ;
		uriBuilder.append("&priority="+priority) ;
		uriBuilder.append("&type=SRV") ;

		if (geoGroup!=null && (!geoGroup.toLowerCase().contentEquals("null"))) {
			uriBuilder.append("&geoGroup="+geoGroup) ;
		}
		if (iso2Code!=null && (!iso2Code.toLowerCase().contains("null"))) {
			uriBuilder.append("&country_iso2="+geoGroup) ;
		}
		if (region!=null && (!region.toLowerCase().contentEquals("null"))) {
			uriBuilder.append("&region="+region) ;
		}
		if (city!=null && (!city.toLowerCase().contentEquals("null"))) {
			uriBuilder.append("&city="+city) ;
		}
		if (ttl!=null) {
			uriBuilder.append("&ttl="+ttl) ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Create a new MX record using the specified options
	 * @param name The name of the domain/group to have the record added to
	 * @param isGroup Is this a domain or a domain group?
	 * @param host The host name to have the record added to
	 * @param rdata The response data for this A record.
	 * @param isWildcard Is this record a wildcard?
	 * @param geoGroup (OPTIONAL) GeoGroup to set for this record or <code>null</code>
	 * @param iso2Code (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code>
	 * @param region (OPTIONAL) The region to set for this record or <code>null</code>
	 * @param city (OPTIONAL) The city to set for this record or <code>null</code>
	 * @param ttl (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createMXRecord(
				String name,
				boolean isGroup,
				String host,
				String rdata,
				int priority,
				boolean isWildcard,
				String geoGroup,
				String iso2Code,
				String region,
				String city,
				Integer ttl) {
		StringBuilder uriBuilder = new StringBuilder("/api/createRRData/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		if (isGroup) {
			uriBuilder.append("&group="+name) ;
		} else {
			uriBuilder.append("&domain="+name) ;
		}

		uriBuilder.append("&host="+host) ;
		uriBuilder.append("&type=MX") ;
		uriBuilder.append("&priority="+priority) ;

		if (isWildcard) {
			uriBuilder.append("&is_wildcard=true") ;
		}

		uriBuilder.append("&rdata="+rdata) ;

		if (geoGroup!=null && (!geoGroup.toLowerCase().contentEquals("null"))) {
			uriBuilder.append("&geoGroup="+geoGroup) ;
		}
		if (iso2Code!=null && (!iso2Code.toLowerCase().contains("null"))) {
			uriBuilder.append("&country_iso2="+geoGroup) ;
		}
		if (region!=null && (!region.toLowerCase().contentEquals("null"))) {
			uriBuilder.append("&region="+region) ;
		}
		if (city!=null && (!city.toLowerCase().contentEquals("null"))) {
			uriBuilder.append("&city="+city) ;
		}
		if (ttl!=null) {
			uriBuilder.append("&ttl="+ttl) ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Create a new URL301 record using the specified options
	 * @param name The name of the domain/group to have the record added to
	 * @param isGroup Is this a domain or a domain group?
	 * @param host The host name to have the record added to
	 * @param rdata The response data for this A record.
	 * @param isWildcard Is this record a wildcard?
	 * @param geoGroup (OPTIONAL) GeoGroup to set for this record or <code>null</code>
	 * @param iso2Code (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code>
	 * @param region (OPTIONAL) The region to set for this record or <code>null</code>
	 * @param city (OPTIONAL) The city to set for this record or <code>null</code>
	 * @param ttl (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createURL301Record(
				String name,
				boolean isGroup,
				String host,
				String rdata,
				boolean isWildcard,
				String geoGroup,
				String iso2Code,
				String region,
				String city,
				Integer ttl) {
		return createDefaultRecord(name, isGroup, host, rdata, "URL301", isWildcard, geoGroup, iso2Code, region, city, ttl) ;
	}

	/**
	 * Create a new URLFrame record using the specified options
	 * @param name The name of the domain/group to have the record added to
	 * @param isGroup Is this a domain or a domain group?
	 * @param host The host name to have the record added to
	 * @param rdata The response data for this A record.
	 * @param title (OPTIONAL) The page title for the frame or <code>null</code>
	 * @param description (OPTIONAL) The HTML Meta description tag content or <code>null</code>
	 * @param keywords (OPTIONAL) The HTML Meta keywords tag content or <code>null</code>
	 * @param isWildcard Is this record a wildcard?
	 * @param geoGroup (OPTIONAL) GeoGroup to set for this record or <code>null</code>
	 * @param iso2Code (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code>
	 * @param region (OPTIONAL) The region to set for this record or <code>null</code>
	 * @param city (OPTIONAL) The city to set for this record or <code>null</code>
	 * @param ttl (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createURLFrameRecord(
				String name,
				boolean isGroup,
				String host,
				String rdata,
				String title,
				String description,
				String keywords,
				boolean isWildcard,
				String geoGroup,
				String iso2Code,
				String region,
				String city,
				Integer ttl) {
		String encTitle = null ;
		String encDesc = null ;
		String encKeywords = null ;
		try {
			encTitle = URLEncoder.encode(title, "US-ASCII") ;
			encDesc = URLEncoder.encode(description, "US-ASCII") ;
			encKeywords = URLEncoder.encode(keywords, "US-ASCII") ;
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace() ;
		}

		StringBuilder uriBuilder = new StringBuilder("/api/createRRData/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		if (isGroup) {
			uriBuilder.append("&group="+name) ;
		} else {
			uriBuilder.append("&domain="+name) ;
		}

		uriBuilder.append("&host="+host) ;
		uriBuilder.append("&type=URLFrame") ;

		if (isWildcard) {
			uriBuilder.append("&is_wildcard=true") ;
		}

		uriBuilder.append("&rdata="+rdata) ;

		if (geoGroup!=null && (!geoGroup.toLowerCase().contentEquals("null"))) {
			uriBuilder.append("&geoGroup="+geoGroup) ;
		}
		if (iso2Code!=null && (!iso2Code.toLowerCase().contains("null"))) {
			uriBuilder.append("&country_iso2="+geoGroup) ;
		}
		if (region!=null && (!region.toLowerCase().contentEquals("null"))) {
			uriBuilder.append("&region="+region) ;
		}
		if (city!=null && (!city.toLowerCase().contentEquals("null"))) {
			uriBuilder.append("&city="+city) ;
		}
		if (ttl!=null) {
			uriBuilder.append("&ttl="+ttl) ;
		}
		if (title!=null) {
			uriBuilder.append("&title="+encTitle) ;
		}
		if (keywords!=null) {
			uriBuilder.append("&keywords="+encKeywords) ;
		}
		if (description!=null) {
			uriBuilder.append("&description="+encDesc) ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Create a new URL302 record using the specified options
	 * @param name The name of the domain/group to have the record added to
	 * @param isGroup Is this a domain or a domain group?
	 * @param host The host name to have the record added to
	 * @param rdata The response data for this A record.
	 * @param isWildcard Is this record a wildcard?
	 * @param geoGroup (OPTIONAL) GeoGroup to set for this record or <code>null</code>
	 * @param iso2Code (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code>
	 * @param region (OPTIONAL) The region to set for this record or <code>null</code>
	 * @param city (OPTIONAL) The city to set for this record or <code>null</code>
	 * @param ttl (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createURL302Record(
				String name,
				boolean isGroup,
				String host,
				String rdata,
				boolean isWildcard,
				String geoGroup,
				String iso2Code,
				String region,
				String city,
				Integer ttl) {
		return createDefaultRecord(name, isGroup, host, rdata, "URL302", isWildcard, geoGroup, iso2Code, region, city, ttl) ;
	}

	/**
	 * Deletes the specified domain
	 * @param domain The name of the domain to delete.
	 * @param confirm Confirm that this is that action required, otherwise the request will fail.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject deleteDomain(String domain, boolean confirm) {
		StringBuilder uriBuilder = new StringBuilder("/api/deleteDomain/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		uriBuilder.append("&domain="+domain) ;
		if(confirm) {
			uriBuilder.append("&confirm=true") ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Disables the specified domain
	 * @param domain The name of the domain to disable.
	 * @param confirm Confirm that this is that action required, otherwise the request will fail.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject disableDomain(String domain, boolean confirm) {
		StringBuilder uriBuilder = new StringBuilder("/api/disableDomain/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		uriBuilder.append("&domain="+domain) ;
		if(confirm) {
			uriBuilder.append("&confirm=true") ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Enables the specified domain
	 * @param domain The name of the domain to enable.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject enableDomain(String domain, boolean confirm) {
		StringBuilder uriBuilder = new StringBuilder("/api/enableDomain/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		uriBuilder.append("&domain="+domain) ;
		if(confirm) {
			uriBuilder.append("&confirm=true") ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Return a list of domain groups matching the specified filter string.
	 * @param filter A case insensitive search filter or "" for all.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject getDomainGroups(String filter) {
		StringBuilder uriBuilder = new StringBuilder("/api/getDomainGroups/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		uriBuilder.append("&search_term="+filter) ;

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Return a list of domains matching the specified filter string.
	 * @param filter A case insensitive search filter or "" for all.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject getDomains(String filter) {
		StringBuilder uriBuilder = new StringBuilder("/api/getDomains/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		uriBuilder.append("&search_term="+filter) ;

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Return a list of domains which are members of the specified domain group.
	 * @param group The name of the group to get a list of member domains for.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject getDomainsInGroup(String group) {
		StringBuilder uriBuilder = new StringBuilder("/api/getDomainsInGroup/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		uriBuilder.append("&group="+group) ;

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Return the details about a specific GeoGroup.
	 * @param group The name of the geogroup to get the details for.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject getGeoGroupDetails(String name) {
		StringBuilder uriBuilder = new StringBuilder("/api/getGeoGroupDetails/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		uriBuilder.append("&name="+name) ;

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Return a list of GeoGroups which match the specified filter string.
	 * @param filter A case insensitive search filter or "" for all.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject getGeoGroups(String filter) {
		StringBuilder uriBuilder = new StringBuilder("/api/getGeoGroups/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		uriBuilder.append("&search_term="+filter) ;

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Get all hostnames for the specified domain
	 * @param domain The name of the domain to enumerate hosts for.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject getHostnamesForDomain(String domain) {
		StringBuilder uriBuilder = new StringBuilder("/api/getHostnamesForDomain/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		uriBuilder.append("&domain="+domain) ;

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Get all hostnames for the specified group
	 * @param group The name of the group to enumerate hosts for.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject getHostnamesForGroup(String group) {
		StringBuilder uriBuilder = new StringBuilder("/api/getHostnamesForGroup/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		uriBuilder.append("&group="+group) ;

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Return the resource record data for a given domain/group and hostname.
	 * @param name The name of the domain/group to pull from.
	 * @param isGroup Is this a domain or a domainGroup?
	 * @param hostname The host name to get the resource records for.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject getRRSetForHostname(String name, boolean isGroup, String hostname) {
		StringBuilder uriBuilder = new StringBuilder("/api/getRRSetForHostname/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		if (isGroup) {
			uriBuilder.append("&group="+name) ;
		} else {
			uriBuilder.append("&domain="+name) ;
		}

		uriBuilder.append("&host="+hostname) ;

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Request that the domain be rebuilt and redistributed to the authoritative name servers.
	 * @param name The name of the domain/group to be rebuilt.
	 * @param isGroup Is this a domain or a domain group?
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject rebuild(String name, boolean isGroup) {
		StringBuilder uriBuilder = new StringBuilder("/api/rebuild/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		if (isGroup) {
			uriBuilder.append("&group="+name) ;
		} else {
			uriBuilder.append("&domain="+name) ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Remove the specified domain group
	 * @param group The name of the domain group to remove.
	 * @param confirm Set to "true" in order to authorize the call, otherwise the call will fail.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject removeDomainGroup(String group, boolean confirm) {
		StringBuilder uriBuilder = new StringBuilder("/api/removeDomainGroup/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		uriBuilder.append("&group="+group) ;

		if(confirm) {
			uriBuilder.append("&confirm=true") ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Remove the specified host from the specified domain/group configuration.
	 * @param name The name of the domain/group in which to find this host
	 * @param isGroup Is this request for a domain or a domain group?
	 * @param host The name of the host to remove.
	 * @param confirm Set to true or this request will fail.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject removeHostname(String name, boolean isGroup, String host, boolean confirm) {
		StringBuilder uriBuilder = new StringBuilder("/api/removeHostname/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		if (isGroup) {
			uriBuilder.append("&group="+name) ;
		} else {
			uriBuilder.append("&domain="+name) ;
		}

		uriBuilder.append("&host="+host) ;

		if (confirm) {
			uriBuilder.append("&confirm=true") ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Remove the resource record identified by rrId
	 * @param rrId The record ID to be removed, can be found by pulling data from getRRSetForHostname()
	 * @param confirm Set to true or this request will fail.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject removeRR(int rrId, boolean confirm) {
		StringBuilder uriBuilder = new StringBuilder("/api/removeRR/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;
		uriBuilder.append("&rr_id="+rrId) ;

		if (confirm) {
			uriBuilder.append("&confirm=true") ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Update the root wildcard address for the listed domains and/or group
	 * @param address The IPv4 address to set as the rdata for the listed domains and groups
	 * @param group (OPTIONAL) The domain group to update the root wildcard for or <code>null</code>
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject updateRootWildcardForList(String address, String group) {
		StringBuilder uriBuilder = new StringBuilder("/api/removeHostname/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		uriBuilder.append("&IP="+address) ;

		if (group!=null) {
			uriBuilder.append("&group="+group) ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Update the root wildcard address for the listed domains and/or group
	 * @param address The IPv4 address to set as the rdata for the listed domains and groups
	 * @param domains (OPTIONAL) The list of domains to update the root wildcard for or <code>null</code>
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject updateRootWildcardForList(String address, String[] domains) {
		StringBuilder uriBuilder = new StringBuilder("/api/removeHostname/?") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		uriBuilder.append("&IP="+address) ;

		for (int x=0; x<domains.length; x++) {
			uriBuilder.append("&domains="+domains[x]) ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Update the resource record identified by the rrId (As found by a call to getRRSetForHostname())
	 * @param rrId The resource record ID
	 * @param rdata The new response data to be set
	 * @param ttl (OPTIONAL) The Time-to-live to be set or <code>null</code> for the default TTL
	 * @param priority (OPTIONAL) The priority to set for MX/SRV records or <code>null</code> to leave unchanged.
	 * @param isWildcard (OPTIONAL) Set TRUE if this is a wildcard record, or <code>null</code> or FALSE.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject updateRRData(int rrId, String rdata, Integer ttl, Integer priority, Boolean isWildcard, 
			Integer retry, Integer expire, Integer minimum, Integer weight, Integer port, String title, 
			String keywords, String description) {
		/*
@required integer `rr_id` rdata id as queried from server.
@required string `rdata` answer data

@optional integer `ttl` Time To Live for resolvers to hold cache of rdata, between 1 and 604800 ( 7 days )
@optional integer `priority` 0,5,15,20,25 MX preferences
@optioanl boolean `is_wildcard` is this a wildcard record or not

@optional string `retry` retry for SOA record, between 0 and 2147483647L (32bit)
@optional string `expire` expire for SOA record, between 0 and 2147483647L (32bit)
@optional string `minimum` minimum for SOA record, between 0 and 2147483647L (32bit)

@optional string `weight` weight for SRV record, between 0 and 65535 (16bit)
@optional string `port` port for SRV record, between 0 and 65535 (16bit)
@optional string `priority` priority  for SRV record, between 0 and 2147483647L (32bit)

@optional string `title` title for URLFrame record
@optional string `keywords` keywords for URLFrame record-
@optional string `description` description  for URLFrame record
		 */
		StringBuilder uriBuilder = new StringBuilder("/api/updateRRData/?") ;
		Log.d("ManagementAPI","Setting API Token") ;
		uriBuilder.append("AUTH_TOKEN="+apiToken) ;

		uriBuilder.append("&rr_id="+rrId) ;
		Log.d("ManagementAPI","Setting RR ID") ;
		uriBuilder.append("&rdata="+rdata) ;
		Log.d("ManagementAPI","Setting RR rdata") ;
		if (ttl!=null) {
			Log.d("ManagementAPI","Setting RR TTL") ;
			uriBuilder.append("&ttl="+ttl) ;
		}
		if (priority!=null) {
			Log.d("ManagementAPI","Setting RR priority") ;
			uriBuilder.append("&priority="+priority) ;
		}
		if (isWildcard.booleanValue()) {
			Log.d("ManagementAPI","Setting RR isWildcard") ;
			uriBuilder.append("&is_wildcard="+Boolean.toString(isWildcard)) ;
		}
		if (retry!=null) {
			Log.d("ManagementAPI","Setting RR retry") ;
			uriBuilder.append("&retry="+retry) ;
		}
		if (expire!=null) {
			Log.d("ManagementAPI","Setting RR expire") ;
			uriBuilder.append("&expire="+expire) ;
		}
		if (minimum!=null) {
			Log.d("ManagementAPI","Setting RR minimum") ;
			uriBuilder.append("&minimum="+minimum) ;
		}
		if (weight!=null) {
			Log.d("ManagementAPI","Setting RR weight") ;
			uriBuilder.append("&weight="+weight) ;
		}
		if (port!=null) {
			Log.d("ManagementAPI","Setting RR port") ;
			uriBuilder.append("&port="+port) ;
		}
		if (title!=null) {
			Log.d("ManagementAPI","Setting RR title") ;
			uriBuilder.append("&title="+URLEncoder.encode(title)) ;
		}
		if (keywords!=null) {
			Log.d("ManagementAPI","Setting RR keywords") ;
			uriBuilder.append("&keywords="+URLEncoder.encode(keywords)) ;
		}
		if (description!=null) {
			Log.d("ManagementAPI","Setting RR description") ;
			uriBuilder.append("&description="+URLEncoder.encode(description)) ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}
}

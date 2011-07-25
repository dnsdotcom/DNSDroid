/*
 * DNS.com Java API - Copyright 2011, DNS, Inc. - All rights reserved.
 * This code is released under the terms of the BSD License. See LICENSE file in the root
 * of this code base for more information.
 */

package com.dns.mobile.api.compiletime;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

/**
 * An implementation of the Billing API which validates arguments at compile time
 * @author Deven Phillips <deven.phillips@gmail.com>
 */
public class BillingAPI extends GenericAPI {

	/**
	 * Constructor
	 * @param apiHost The host name of the server to make API calls against.
	 * @param useSSL Should we use HTTPS connections for API calls?
	 * @param apiToken The API Token for authenticating requests.
	 */
	public BillingAPI(String apiHost, boolean useSSL, String apiToken) {
		super(apiHost, useSSL, apiToken) ;
	}

	/**
	 * Get the hit count for the specified domain and the optionally specified start/end dates
	 * @param domain The domain to retrieve the hit count for
	 * @param start (OPTIONAL) The start date for the window to retrieve stats for
	 * @param end (OPTIONAL) The end date for the window to retrieve stats for
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject getHits(String domain, Date start, Date end) {
		StringBuilder uriBuilder = new StringBuilder("/api/getHits/?") ;
		uriBuilder.append("API_TOKEN="+apiToken) ;
		uriBuilder.append("&domain="+domain) ;

		if (start!=null) {
			String startDate = (new SimpleDateFormat("yyyy-MM-dd")).format(start) ;
			uriBuilder.append("&start="+startDate) ;
		}
		if (end!=null) {
			String endDate = (new SimpleDateFormat("yyyy-MM-dd")).format(end) ;
			uriBuilder.append("&end="+endDate) ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}
}

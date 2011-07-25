/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dns.mobile.api.runtime;

import java.util.Date;
import java.util.HashMap;

import org.json.JSONObject;

/**
 * A version of the DNS.com Billing API which validates arguments at runtime.
 * @author Deven Phillips <deven.phillips@gmail.com>
 */
public class BillingAPI extends com.dns.mobile.api.compiletime.BillingAPI {

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
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>domain The domain to retrieve the hit count for<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>start (OPTIONAL) The start date for the window to retrieve stats for<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>end (OPTIONAL) The end date for the window to retrieve stats for<br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject getHits(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("domain")!=null) {
			return super.getHits((String)args.get("domain"), (Date)args.get("start"), (Date)args.get("end")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}
}

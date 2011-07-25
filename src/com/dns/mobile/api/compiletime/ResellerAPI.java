/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dns.mobile.api.compiletime;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An implementation of the Reseller API which validates arguments at compile time
 * @author Deven Phillips <deven.phillips@gmail.com>
 */
public class ResellerAPI extends GenericAPI {

	/**
	 * Constructor
	 * @param apiHost The host name of the server to make API calls against.
	 * @param useSSL Should we use HTTPS connections for API calls?
	 * @param apiToken The API Token for authenticating requests.
	 */
	public ResellerAPI(String apiHost, boolean useSSL, String apiToken) {
		super(apiHost, useSSL, apiToken) ;
	}

	/**
	 * Create a new user associated with the specified reseller ID
	 * @param resellerCode The unique ID of the reseller creating the use new user account
	 * @param eMail The new user's e-mail address
	 * @param password The new user's password
	 * @param passConfirm The re-entered user password confirming that the input was correct
	 * @param forename The first name of the new user
	 * @param surname The new user's family name
	 * @param phone (OPTIONAL) The new user's telephone number
	 * @param addr1 (OPTIONAL) The first line of the new user's street address
	 * @param addr2 (OPTIONAL) The second line of the new user's street address
	 * @param city (OPTIONAL) The new user's city of address
	 * @param province (OPTIONAL) The new user's province (if any)
	 * @param postCode (OPTIONAL) The new user's postal code
	 * @param country (OPTIONAL) The new user's 2 character ISO2 country code
	 * @param fax (OPTIONAL) The new user's fax number
	 * @param company (OPTIONAL) The new user's company name
	 * @param sendWelcomeMail (OPTIONAL) Set to "yes" or "no" to send the new user a welcome e-mail.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject createNewUser(String resellerCode, String eMail, String password,
			String passConfirm, String forename, String surname, String phone, String addr1,
			String addr2, String city, String province, String postCode, String country,
			String fax, String company, String sendWelcomeMail) {
		StringBuilder uriBuilder = new StringBuilder("/api/createNewUser/?") ;
		uriBuilder.append("API_TOKEN="+apiToken) ;
		uriBuilder.append("&resellerCode="+resellerCode) ;
		uriBuilder.append("&user_email="+eMail) ;
		if (password.equals(passConfirm)) {
			uriBuilder.append("&user_password="+password) ;
			uriBuilder.append("&user_password_confirm="+passConfirm) ;
			uriBuilder.append("&user_first_name="+forename) ;
			uriBuilder.append("&user_last_name="+surname) ;

			if (phone!=null) {
				uriBuilder.append("&user_phone="+phone) ;
			}
			if (addr1!=null) {
				uriBuilder.append("&user_address1="+addr1) ;
			}
			if (addr2!=null) {
				uriBuilder.append("&user_address2="+addr2) ;
			}
			if (city!=null) {
				uriBuilder.append("&user_city="+city) ;
			}
			if (province!=null) {
				uriBuilder.append("&user_state_province="+province) ;
			}
			if (postCode!=null) {
				uriBuilder.append("&user_postal_code="+postCode) ;
			}
			if (country!=null) {
				uriBuilder.append("&user_country_iso2="+country) ;
			}
			if (fax!=null) {
				uriBuilder.append("&user_fax="+fax) ;
			}
			if (company!=null) {
				uriBuilder.append("&user_company="+company) ;
			}
			if (sendWelcomeMail!=null) {
				uriBuilder.append("&send_welcome_email="+sendWelcomeMail) ;
			}

			return makeHttpRequest("") ;
		} else {
			JSONObject response = new JSONObject() ;
			try {
				response.put("meta", new JSONObject("\"success\": 0, \"error\": \"Passwords do not match\"")) ;
				response.put("data", null) ;
			} catch (JSONException je) {
				je.printStackTrace() ;
			}
			return response ;
		}
	}

	/**
	 * Get the specified user's details
	 * @param resellerCode The unique ID of the reseller creating the use new user account
	 * @param eMail The new user's e-mail address
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject getUserDetails(String resellerCode, String eMail) {
		StringBuilder uriBuilder = new StringBuilder("/api/getUserDetails/?") ;
		uriBuilder.append("API_TOKEN="+apiToken) ;
		uriBuilder.append("&resellerCode="+resellerCode) ;
		uriBuilder.append("&email="+eMail) ;

		return makeHttpRequest(uriBuilder.toString()) ;
	}

	/**
	 * Get a list of users for the given reseller ID and optionally filtered by the filter argument
	 * @param resellerCode The unique ID of the reseller creating the use new user account
	 * @param filter A case insensitive search filter to limit which users accounts are retrieved
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 */
	public JSONObject getUserList(String resellerCode, String filter) {
		StringBuilder uriBuilder = new StringBuilder("/api/getUserList/?") ;
		uriBuilder.append("API_TOKEN="+apiToken) ;
		uriBuilder.append("&resellerCode="+resellerCode) ;

		if (filter!=null) {
			uriBuilder.append("&search_term="+filter) ;
		}

		return makeHttpRequest(uriBuilder.toString()) ;
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dns.mobile.api.runtime;

import java.util.HashMap;

import org.json.JSONObject;

/**
 * A version of the DNS.com Reseller API which validates arguments at runtime.
 * @author Deven Phillips <deven.phillips@gmail.com>
 */
public class ResellerAPI extends com.dns.mobile.api.compiletime.ResellerAPI {

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
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>resellerCode</code> - The unique ID of the reseller creating the use new user account<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>eMail</code> - The new user's e-mail address<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>password</code> - The new user's password<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>passConfirm</code> - The re-entered user password confirming that the input was correct<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>forename</code> - The first name of the new user<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>surname</code> - The new user's family name<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>phone</code> - (OPTIONAL) The new user's telephone number<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>addr1</code> - (OPTIONAL) The first line of the new user's street address<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>addr2</code> - (OPTIONAL) The second line of the new user's street address<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>city</code> - (OPTIONAL) The new user's city of address<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>province</code> - (OPTIONAL) The new user's province (if any)<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>postCode</code> - (OPTIONAL) The new user's postal code<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>country</code> - (OPTIONAL) The new user's 2 character ISO2 country code<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>fax</code> - (OPTIONAL) The new user's fax number<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>company</code> - (OPTIONAL) The new user's company name<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>sendWelcomeMail</code> - (OPTIONAL) Set to "yes" or "no" to send the new user a welcome e-mail.<br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createNewUser(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("resellerCode")!=null && args.get("eMail")!=null && args.get("password")!=null && args.get("passConfirm")!=null &&
				args.get("forename")!=null && args.get("surname")!=null) {
			return super.createNewUser((String)args.get("resellerCode"), (String)args.get("eMail"), (String)args.get("password"), (String)args.get("passConfirm"), (String)args.get("forename"), (String)args.get("surname"), (String)args.get("phone"), (String)args.get("addr1"), (String)args.get("addr2"), (String)args.get("city"), (String)args.get("province"), (String)args.get("postCode"), (String)args.get("country"), (String)args.get("fax"), (String)args.get("company"), (String)args.get("sendWelcomeMail")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Get the specified user's details
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>resellerCode</code> - The unique ID of the reseller creating the use new user account<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>eMail</code> - The new user's e-mail address<br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject getUserDetails(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("resellerCode")!=null && args.get("eMail")!=null) {
			return super.getUserDetails((String)args.get("resellerCode"), (String)args.get("eMail")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Get a list of users for the given reseller ID and optionally filtered by the filter argument
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>resellerCode</code> - The unique ID of the reseller creating the use new user account<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>filter</code> - A case insensitive search filter to limit which users accounts are retrieved<br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject getUserList(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("resellerCode")!=null && args.get("filter")!=null) {
			return super.getUserList((String)args.get("resellerCode"), (String)args.get("filter")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}
}

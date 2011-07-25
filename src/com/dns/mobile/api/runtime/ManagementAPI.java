/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dns.mobile.api.runtime;

import java.util.HashMap;

import org.json.JSONObject;

/**
 * An implementation of the Billing API which validates arguments at runtime
 * @author Deven Phillips <deven.phillips@gmail.com>
 */
public class ManagementAPI extends com.dns.mobile.api.compiletime.ManagementAPI {

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
	 * Appends the specified geolocation parameters to the named GeoGroup via a HashMap of arguments
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the GeoGroup to be appended to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>iso2Code</code> - The 2 character country code for this entry<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>region</code> - (OPTIONAL) Either the region name or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>city</code> - (OPTIONAL) Either the city name or <code>null</code><br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject appendToGeoGroup(HashMap<String,String> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("iso2Code")!=null) {
			return super.appendToGeoGroup(args.get("name"), args.get("iso2Code"), args.get("region"), args.get("city")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Changes the mode of the specified domain and assigns domain to a group if requested.
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>domain</code> - The domain to change the mode on<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>mode</code> - The mode to change the domain to (either "advanced" or "group"<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>group</code> - The group to assign the domain to<br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject assignDomainMode(HashMap<String,String> args) throws InvalidArgumentsException {
		if (args.get("domain")!=null && args.get("mode")!=null && args.get("group")!=null) {
			return super.assignDomainMode(args.get("domain"), args.get("mode"), args.get("group")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Creates a new domain entry in the system using the specified mode and domain name.
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>domain</code> - The domain to change the mode on<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>mode</code> - The mode to change the domain to (either "advanced" or "group"<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>group</code> - (OPTIONAL) The group to assign the domain to or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rname</code> - (OPTIONAL) Contact for SOA of this zone. (ex: admin@example.com. is admin.example.com) or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ns</code> - (OPTIONAL) A <code>String[]</code> of name servers to use or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>primary_wildcard</code> - (OPTIONAL) IP or CNAME destination for a wildcard on the primary zone or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>primary_wildcard_qtype</code> - (OPTIONAL) Either A or CNAME  or <code>null</code>. IS REQUIRED IF `primary_wildcard` IS SUPPLIED<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>default_mx</code> - (OPTIONAL) MX Destination for email or <code>null</code><br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createDomain(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("domain")!=null && args.get("mode")!=null) {
			return super.createDomain((String)args.get("mode"), (String)args.get("domain"), (String)args.get("group"),
					(String)args.get("rname"), (String[])args.get("ns"), (String)args.get("primary_wildcard"),
					(String)args.get("primary_wildcard_qtype"), (String)args.get("default_mx")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Create a new domain group with the specified default settings
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the group to create<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rname</code> - (OPTIONAL) Contact for SOA of this zone. (ex: admin@example.com. is admin.example.com) or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ns</code> - (OPTIONAL) List of name servers to use or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>primary_wildcard</code> - (OPTIONAL) IP or CNAME destination for a wildcard on the primary zone or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>primary_wildcard_qtype</code> - (OPTIONAL) Either A or CNAME  or <code>null</code>. IS REQUIRED IF `primary_wildcard` IS SUPPLIED<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>default_mx</code> - (OPTIONAL) MX Destination for email or <code>null</code><br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createDomainGroup(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null) {
			return super.createDomainGroup((String)args.get("name"), (String)args.get("rname"),
					(String[])args.get("ns"), (String)args.get("primary_wildcard"),
					(String)args.get("primary_wildcard_qtype"), (String)args.get("default_mx")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Create multiple new domains and optionally assign them to a domain group.
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>domains</code> - A list of domains to be added.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>mode</code> - The mode to set for the domains (either "advanced" or "group"<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>group</code> - (OPTIONAL) The domain group to assign the domains to or <code>null</code><br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createDomains(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("domains")!=null && args.get("mode")!=null) {
			return super.createDomains((String)args.get("mode"), (String[])args.get("domains"), (String)args.get("group")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Create a new domain group with the specified name
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>group</code> - The name of the new domain group to be created.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createGeoGroup(HashMap<String, String> args) throws InvalidArgumentsException {
		if (args.get("group")!=null) {
			return super.createGeoGroup(args.get("group")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Create a new hostname in the specifed domain/group
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the domain/group to have the host added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isGroup</code> - Is this change for a domain or a group?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>host</code> - The name of the host to be added<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isUrlForward</code> - Is this a URL forward host?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>defaultAddr</code> - (OPTIONAL) The default address to have undefined traffic sent to or <code>null</code>
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createHostname(HashMap<String,Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("isGroup")!=null && args.get("host")!=null && args.get("isUrlForward")!=null) {
			return super.createHostname((String)args.get("name"), (Boolean)args.get("isGroup"),
					(String)args.get("host"), (Boolean)args.get("isUrlForward"), (String)args.get("defaultAddr")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Create a new record using the specified options
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the domain/group to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isGroup</code> - Is this a domain or a domain group?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>host</code> - The host name to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rdata</code> - The response data for this A record.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>type</code> - The type of record to create (This method supports A, AAAA, TXT, NS, and CNAME)<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isWildcard</code> - Is this record a wildcard?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>geoGroup</code> - (OPTIONAL) GeoGroup to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>iso2Code</code> - (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>region</code> - (OPTIONAL) The region to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>city</code> - (OPTIONAL) The city to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ttl</code> - (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createDefaultRecord(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("isGroup")!=null && args.get("host")!=null && args.get("rdata")!=null && args.get("type")!=null && args.get("isWildcard")!=null) {
			return super.createDefaultRecord((String)args.get("name"), (Boolean)args.get("isGroup"),
					(String)args.get("host"), (String)args.get("rdata"), (String)args.get("type"),
					(Boolean)args.get("isWildcard"), (String)args.get("geoGroup"),
					(String)args.get("iso2Code"), (String)args.get("region"),
					(String)args.get("city"), (Integer)args.get("ttl")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Create a new "A" record using the specified options
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the domain/group to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isGroup</code> - Is this a domain or a domain group?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>host</code> - The host name to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rdata</code> - The response data for this A record.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isWildcard</code> - Is this record a wildcard?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>geoGroup</code> - (OPTIONAL) GeoGroup to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>iso2Code</code> - (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>region</code> - (OPTIONAL) The region to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>city</code> - (OPTIONAL) The city to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ttl</code> - (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createARecord(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("isGroup")!=null && args.get("host")!=null && args.get("rdata")!=null && args.get("isWildcard")!=null) {
			return super.createDefaultRecord((String)args.get("name"), (Boolean)args.get("isGroup"),
					(String)args.get("host"), (String)args.get("rdata"), "A",
					(Boolean)args.get("isWildcard"), (String)args.get("geoGroup"),
					(String)args.get("iso2Code"), (String)args.get("region"),
					(String)args.get("city"), (Integer)args.get("ttl")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Create a new "AAAA" record using the specified options
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the domain/group to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isGroup</code> - Is this a domain or a domain group?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>host</code> - The host name to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rdata</code> - The response data for this A record.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isWildcard</code> - Is this record a wildcard?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>geoGroup</code> - (OPTIONAL) GeoGroup to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>iso2Code</code> - (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>region</code> - (OPTIONAL) The region to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>city</code> - (OPTIONAL) The city to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ttl</code> - (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createAAAARecord(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("isGroup")!=null && args.get("host")!=null && args.get("rdata")!=null && args.get("isWildcard")!=null) {
			return super.createDefaultRecord((String)args.get("name"), (Boolean)args.get("isGroup"),
					(String)args.get("host"), (String)args.get("rdata"), "AAAA",
					(Boolean)args.get("isWildcard"), (String)args.get("geoGroup"),
					(String)args.get("iso2Code"), (String)args.get("region"),
					(String)args.get("city"), (Integer)args.get("ttl")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Create a new "CNAME" record using the specified options
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the domain/group to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isGroup</code> - Is this a domain or a domain group?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>host</code> - The host name to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rdata</code> - The response data for this A record.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isWildcard</code> - Is this record a wildcard?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>geoGroup</code> - (OPTIONAL) GeoGroup to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>iso2Code</code> - (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>region</code> - (OPTIONAL) The region to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>city</code> - (OPTIONAL) The city to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ttl</code> - (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createCNAMERecord(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("isGroup")!=null && args.get("host")!=null && args.get("rdata")!=null && args.get("isWildcard")!=null) {
			return super.createDefaultRecord((String)args.get("name"), (Boolean)args.get("isGroup"),
					(String)args.get("host"), (String)args.get("rdata"), "CNAME",
					(Boolean)args.get("isWildcard"), (String)args.get("geoGroup"),
					(String)args.get("iso2Code"), (String)args.get("region"),
					(String)args.get("city"), (Integer)args.get("ttl")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Create a new "TXT" record using the specified options
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the domain/group to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isGroup</code> - Is this a domain or a domain group?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>host</code> - The host name to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rdata</code> - The response data for this A record.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isWildcard</code> - Is this record a wildcard?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>geoGroup</code> - (OPTIONAL) GeoGroup to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>iso2Code</code> - (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>region</code> - (OPTIONAL) The region to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>city</code> - (OPTIONAL) The city to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ttl</code> - (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createTXTRecord(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("isGroup")!=null && args.get("host")!=null && args.get("rdata")!=null && args.get("isWildcard")!=null) {
			return super.createTXTRecord((String)args.get("name"), (Boolean)args.get("isGroup"),
					(String)args.get("host"), (String)args.get("rdata"),
					(Boolean)args.get("isWildcard"), (String)args.get("geoGroup"),
					(String)args.get("iso2Code"), (String)args.get("region"),
					(String)args.get("city"), (Integer)args.get("ttl")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Create a new "NS" record using the specified options
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the domain/group to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isGroup</code> - Is this a domain or a domain group?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>host</code> - The host name to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rdata</code> - The response data for this A record.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isWildcard</code> - Is this record a wildcard?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>geoGroup</code> - (OPTIONAL) GeoGroup to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>iso2Code</code> - (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>region</code> - (OPTIONAL) The region to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>city</code> - (OPTIONAL) The city to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ttl</code> - (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createNSRecord(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("isGroup")!=null && args.get("host")!=null && args.get("rdata")!=null && args.get("isWildcard")!=null) {
			return super.createDefaultRecord((String)args.get("name"), (Boolean)args.get("isGroup"),
					(String)args.get("host"), (String)args.get("rdata"), "NS",
					(Boolean)args.get("isWildcard"), (String)args.get("geoGroup"),
					(String)args.get("iso2Code"), (String)args.get("region"),
					(String)args.get("city"), (Integer)args.get("ttl")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Create a new "URL301" record using the specified options
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the domain/group to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isGroup</code> - Is this a domain or a domain group?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>host</code> - The host name to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rdata</code> - The response data for this A record.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isWildcard</code> - Is this record a wildcard?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>geoGroup</code> - (OPTIONAL) GeoGroup to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>iso2Code</code> - (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>region</code> - (OPTIONAL) The region to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>city</code> - (OPTIONAL) The city to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ttl</code> - (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createURL301Record(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("isGroup")!=null && args.get("host")!=null && args.get("rdata")!=null && args.get("isWildcard")!=null) {
			return super.createDefaultRecord((String)args.get("name"), (Boolean)args.get("isGroup"),
					(String)args.get("host"), (String)args.get("rdata"), "URL301",
					(Boolean)args.get("isWildcard"), (String)args.get("geoGroup"),
					(String)args.get("iso2Code"), (String)args.get("region"),
					(String)args.get("city"), (Integer)args.get("ttl")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Create a new "URL302" record using the specified options
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the domain/group to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isGroup</code> - Is this a domain or a domain group?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>host</code> - The host name to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rdata</code> - The response data for this A record.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isWildcard</code> - Is this record a wildcard?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>geoGroup</code> - (OPTIONAL) GeoGroup to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>iso2Code</code> - (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>region</code> - (OPTIONAL) The region to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>city</code> - (OPTIONAL) The city to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ttl</code> - (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createURL302Record(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("isGroup")!=null && args.get("host")!=null && args.get("rdata")!=null && args.get("isWildcard")!=null) {
			return super.createDefaultRecord((String)args.get("name"), (Boolean)args.get("isGroup"),
					(String)args.get("host"), (String)args.get("rdata"), "URL302",
					(Boolean)args.get("isWildcard"), (String)args.get("geoGroup"),
					(String)args.get("iso2Code"), (String)args.get("region"),
					(String)args.get("city"), (Integer)args.get("ttl")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Create a new SOA record using the specified options
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the domain/group to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isGroup</code> - Is this a domain or a domain group?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>host</code> - The host name to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rdata</code> - The response data for this A record.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>retry</code> - (OPTIONAL) The integer value for retry or <code>null</code> for the default value.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>minimum</code> - (OPTIONAL) The integer value for minimum or <code>null</code> for the default value.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>expire</code> - (OPTIONAL) The integer value for expire or <code>null</code> for the default value.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isWildcard</code> - Is this record a wildcard?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>geoGroup</code> - (OPTIONAL) GeoGroup to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>iso2Code</code> - (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>region</code> - (OPTIONAL) The region to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>city</code> - (OPTIONAL) The city to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ttl</code> - (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createSOARecord(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("isGroup")!=null && args.get("host")!=null && args.get("rdata")!=null) {
			return super.createSOARecord((String)args.get("name"), (Boolean)args.get("isGroup"),
					(String)args.get("host"), (String)args.get("rdata"),
					(Integer)args.get("retry"), (Integer)args.get("expire"),
					(Integer)args.get("minimum"), (Boolean)args.get("isWildcard"),
					(String)args.get("geoGroup"), (String)args.get("iso2Code"),
					(String)args.get("region"), (String)args.get("city"), (Integer)args.get("ttl")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Create a new SRV record using the specified options
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the domain/group to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isGroup</code> - Is this a domain or a domain group?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>host</code> - The host name to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rdata</code> - The response data for this A record.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>weight</code> - The preference weight for this record<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>priority</code> - The priority preference for this record<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>port</code> - The TCP/UDP port for this service<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isWildcard</code> - Is this record a wildcard?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>geoGroup</code> - (OPTIONAL) GeoGroup to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>iso2Code</code> - (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>region</code> - (OPTIONAL) The region to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>city</code> - (OPTIONAL) The city to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ttl</code> - (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createSRVRecord(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("isGroup")!=null && args.get("host")!=null && args.get("rdata")!=null) {
			return super.createSRVRecord((String)args.get("name"), (Boolean)args.get("isGroup"),
					(String)args.get("host"), (String)args.get("rdata"),
					(Integer)args.get("weight"), (Integer)args.get("priority"), 
					(Integer)args.get("port"),  (Boolean)args.get("isWildcard"),
					(String)args.get("geoGroup"), (String)args.get("iso2Code"),
					(String)args.get("region"), (String)args.get("city"), (Integer)args.get("ttl")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Create a new MX record using the specified options
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the domain/group to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isGroup</code> - Is this a domain or a domain group?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>host</code> - The host name to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rdata</code> - The response data for this A record.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isWildcard</code> - Is this record a wildcard?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>geoGroup</code> - (OPTIONAL) GeoGroup to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>iso2Code</code> - (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>region</code> - (OPTIONAL) The region to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>city</code> - (OPTIONAL) The city to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ttl</code> - (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createMXRecord(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("isGroup")!=null && args.get("host")!=null && args.get("rdata")!=null) {
			return super.createMXRecord((String)args.get("name"), (Boolean)args.get("isGroup"),
					(String)args.get("host"), (String)args.get("rdata"),
					(Integer)args.get("priority"), (Boolean)args.get("isWildcard"),
					(String)args.get("geoGroup"), (String)args.get("iso2Code"),
					(String)args.get("region"), (String)args.get("city"), (Integer)args.get("ttl")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Create a new URLFrame record using the specified options
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the domain/group to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isGroup</code> - Is this a domain or a domain group?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>host</code> - The host name to have the record added to<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rdata</code> - The response data for this A record.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>title</code> - (OPTIONAL) The page title for the frame or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>description</code> - (OPTIONAL) The HTML Meta description tag content or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>keywords</code> - (OPTIONAL) The HTML Meta keywords tag content or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isWildcard</code> - Is this record a wildcard?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>geoGroup</code> - (OPTIONAL) GeoGroup to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>iso2Code</code> - (OPTIONAL) The 2 character ISO country code to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>region</code> - (OPTIONAL) The region to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>city</code> - (OPTIONAL) The city to set for this record or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ttl</code> - (OPTIONAL) The Time-To-Live for resolvers to hold cache of rdata, between 1 and
	 * 65535 OR <code>null</code> to use the default of 1440
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject createURLFrameRecord(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("isGroup")!=null && args.get("host")!=null && args.get("rdata")!=null) {
			return super.createURLFrameRecord((String)args.get("name"), (Boolean)args.get("isGroup"),
					(String)args.get("host"), (String) args.get("rdata"),
					(String)args.get("title"), (String) args.get("description"),
					(String)args.get("keywords"), (Boolean)args.get("isWildcard"),
					(String)args.get("geoGroup"), (String)args.get("iso2Code"),
					(String)args.get("region"), (String)args.get("city"),
					(Integer)args.get("ttl")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Deletes the specified domain
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>domain</code> - The name of the domain to delete.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>confirm</code> - Confirm that this is that action required, otherwise the request will fail.<br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject deleteDomain(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("domain")!=null && args.get("confirm")!=null) {
			return super.deleteDomain((String)args.get("domain"), (Boolean)args.get("confirm")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Disables the specified domain
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>domain</code> - The name of the domain to disable.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>confirm</code> - Confirm that this is that action required, otherwise the request will fail.<br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject disableDomain(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("domain")!=null && args.get("confirm")!=null) {
			return super.disableDomain((String)args.get("domain"), (Boolean)args.get("confirm")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Enables the specified domain
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>domain</code> - The name of the domain to enable.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>confirm</code> - Confirm that this is that action required, otherwise the request will fail.<br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject enableDomain(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("domain")!=null && args.get("confirm")!=null) {
			return super.enableDomain((String)args.get("domain"), (Boolean)args.get("confirm")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Return a list of domain groups matching the specified filter string.
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>filter</code> - A case insensitive search filter or "" for all.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject getDomainGroups(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("filter")!=null) {
			return super.getDomainGroups((String)args.get("filter")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Return a list of domains matching the specified filter string.
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>filter</code> - A case insensitive search filter or "" for all.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject getDomains(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("filter")!=null) {
			return super.getDomains((String)args.get("filter")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Return a list of domains which are members of the specified domain group.
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>group</code> - The name of the group to get a list of member domains for.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject getDomainsInGroup(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("group")!=null) {
			return super.getDomainsInGroup((String)args.get("group")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Return the details about a specific GeoGroup.
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>group</code> - The name of the geogroup to get the details for.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject getGeoGroupDetails(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("group")!=null) {
			return super.getGeoGroupDetails((String)args.get("group")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Return a list of GeoGroups which match the specified filter string.
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>filter</code> - A case insensitive search filter or "" for all.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject getGeoGroups(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("filter")!=null) {
			return super.getGeoGroups((String)args.get("filter")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Get all hostnames for the specified domain
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>domain</code> - The name of the domain to enumerate hosts for.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject getHostnamesForDomain(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("domain")!=null) {
			return super.getHostnamesForDomain((String)args.get("domain")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Get all hostnames for the specified group
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>group</code> - The name of the group to enumerate hosts for.
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject getHostnamesForGroup(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("group")!=null) {
			return super.getHostnamesForGroup((String)args.get("group")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Return the resource record data for a given domain/group and hostname.
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the domain/group to pull from.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isGroup</code> - Is this a domain or a domainGroup?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>hostname</code> - The host name to get the resource records for.<br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject getRRSetForHostname(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("isGroup")!=null && args.get("hostname")!=null) {
			return super.getRRSetForHostname((String)args.get("name"), (Boolean)args.get("isGroup"), (String)args.get("hostname")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Request that the domain be rebuilt and redistributed to the authoritative name servers.
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the domain/group to be rebuilt.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isGroup</code> - Is this a domain or a domain group?<br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject rebuild(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("isGroup")!=null) {
			return super.rebuild((String)args.get("name"),(Boolean)args.get("isGroup")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Remove the specified domain group
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>group</code> - The name of the domain group to remove.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>confirm</code> - Set to "true" in order to authorize the call, otherwise the call will fail.<br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject removeDomainGroup(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("group")!=null && args.get("confirm")!=null) {
			return super.removeDomainGroup((String)args.get("group"), (Boolean)args.get("confirm")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Remove the specified host from the specified domain/group configuration.
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>name</code> - The name of the domain/group in which to find this host<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isGroup</code> - Is this request for a domain or a domain group?<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>host</code> - The name of the host to remove.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>confirm</code> - Set to true or this request will fail.<br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject removeHostname(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("name")!=null && args.get("isGroup")!=null && args.get("host")!=null && args.get("confirm")!=null) {
			return super.removeHostname((String)args.get("name"), (Boolean)args.get("isGroup"), (String)args.get("host"), (Boolean)args.get("confirm")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Remove the resource record identified by rrId
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rrId</code> - The record ID to be removed, can be found by pulling data from getRRSetForHostname()<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>confirm</code> - Set to true or this request will fail.<br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject removeRR(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("rrId")!=null && args.get("confirm")!=null) {
			return super.removeRR((Integer)args.get("rrId"), (Boolean)args.get("confirm")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Update the root wildcard address for the listed domains and/or group
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>address</code> - The IPv4 address to set as the rdata for the listed domains and groups<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>group</code> - (OPTIONAL) The domain group to update the root wildcard for or <code>null</code><br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>domains</code> - (OPTIONAL) A list of domains to update the root wildcard for or <code>null</code><br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject updateRootWildcardForList(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("address")!=null && args.get("group")!=null) {
			return super.updateRootWildcardForList((String)args.get("address"), (String)args.get("group")) ;
		} else if (args.get("address")!=null && args.get("domains")!=null) {
			return super.updateRootWildcardForList((String)args.get("address"), (String[])args.get("domains")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}

	/**
	 * Update the resource record identified by the rrId (As found by a call to getRRSetForHostname())
	 * @param args A <code>HashMap</code> of arguments as listed below<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rrId</code> - The resource record ID<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>rdata</code> - The new response data to be set<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ttl</code> - (OPTIONAL) The Time-to-live to be set or <code>null</code> for the default TTL<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>priority</code> - (OPTIONAL) The priority to set for MX/SRV records or <code>null</code> to leave unchanged.<br />
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>isWildcard</code> - (OPTIONAL) Set TRUE if this is a wildcard record, or <code>null</code> or FALSE.<br />
	 * @return A <code>com.dns.mobile.json.JSONObject</code> containing the JSON response or an error code.
	 * @throws <code>InvalidArgumentsException</code> If the required arguments are not specified
	 */
	public JSONObject updateRRData(HashMap<String, Object> args) throws InvalidArgumentsException {
		if (args.get("rrId")!=null && args.get("rdata")!=null) {
			return super.updateRRData((Integer)args.get("rrId"), (String)args.get("rdata"), (Integer)args.get("ttl"), (Integer)args.get("priority"), (Boolean)args.get("isWildcard")) ;
		} else {
			throw new InvalidArgumentsException("One or more required arguments are missing.") ;
		}
	}
}

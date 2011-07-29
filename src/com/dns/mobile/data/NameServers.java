/**
 * 
 */
package com.dns.mobile.data;

import java.io.Serializable;
import java.util.ArrayList;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class NameServers implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1710845195608836748L;

	private ArrayList<NameServer> nameServers = null ;

	private SharedPreferences prefs = null ;

	public NameServers(Context ctx) {
		prefs = PreferenceManager.getDefaultSharedPreferences(ctx) ;
		nameServers = new ArrayList<NameServer>() ;
		String[] serverNames = prefs.getString("serverNames", "ns1.dns.com,ns2.dns.com,ns3.dns.com,ns4.dns.com").split(",") ;
		String[] serverAddr = prefs.getString("serverAddr", "ns1.dns.com,ns2.dns.com,ns3.dns.com,ns4.dns.com").split(",") ;
		for (int x=0; x<serverNames.length; x++) {
			Log.d("NameServers", "Adding '"+serverNames[x]+"' with address of '"+serverAddr[x]+"'") ;
			nameServers.add(new NameServer(serverNames[x], serverAddr[x])) ;
		}
	}

	public void addNameServer(String name, String address) {
		NameServer newServer = new NameServer(name, address) ;
		this.nameServers.add(newServer) ;
		saveNameServers() ;
	}

	public ArrayList<NameServer> getNameServers() {
		return nameServers ;
	}

	public NameServer getNameServer(int index) {
		return nameServers.get(index) ;
	}

	public void deleteNameServer(int itemIndex) {
		nameServers.remove(itemIndex) ;
		saveNameServers() ;
	}

	public void moveUp(int item) {
		NameServer temp = nameServers.get(item-1) ;
		nameServers.set(item-1, nameServers.get(item)) ;
		nameServers.set(item, temp) ;
	}

	private String getServerNameList() {
		StringBuilder sb = new StringBuilder() ;
		for (int x=0; x<nameServers.size(); x++) {
			sb.append(nameServers.get(x).getName()) ;
			if ((x+1)<nameServers.size()) {
				sb.append(",") ;
			}
		}
		return sb.toString() ;
	}

	private String getServerAddrList() {
		StringBuilder sb = new StringBuilder() ;
		for (int x=0; x<nameServers.size(); x++) {
			sb.append(nameServers.get(x).getAddress()) ;
			if ((x+1)<nameServers.size()) {
				sb.append(",") ;
			}
		}
		return sb.toString() ;
	}

	private void saveNameServers() {
		prefs.edit().putString("serverNames", getServerNameList()) ;
		prefs.edit().putString("serverAddr", getServerAddrList()) ;
		prefs.edit().commit() ;
	}
}

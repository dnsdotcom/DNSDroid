/**
 * 
 */
package com.dns.mobile.data;

import java.io.Serializable;
import java.util.ArrayList;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
	private static NameServers instance = null ;

	public static NameServers getInstance(Context ctx) {
		if (instance==null) {
			instance = new NameServers(ctx) ;
		}

		return instance ;
	}

	public static NameServers getInstance() {
		return instance ;
	}

	protected NameServers(Context ctx) {
		prefs = ctx.getSharedPreferences("dnsMobile", Context.MODE_PRIVATE) ;
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

	public void deleteNameServer(String serverName) {
		nameServers.remove(getIndexForName(serverName)) ;
		saveNameServers() ;
	}

	public void moveUp(int item) {
		NameServer temp = nameServers.get(item-1) ;
		nameServers.set(item-1, nameServers.get(item)) ;
		nameServers.set(item, temp) ;
		saveNameServers() ;
	}

	public void moveDown(String server) {
		int index = getIndexForName(server) ;
		NameServer temp = nameServers.get(index+1) ;
		nameServers.set(index+1, nameServers.get(index)) ;
		nameServers.set(index, temp) ;
		saveNameServers() ;
	}

	public void moveUp(String server) {
		int index = getIndexForName(server) ;
		NameServer temp = nameServers.get(index-1) ;
		nameServers.set(index-1, nameServers.get(index)) ;
		nameServers.set(index, temp) ;
		saveNameServers() ;
	}

	public int getIndexForName(String name) {
		int retVal = 0 ;

		for (int x=0; x<nameServers.size(); x++) {
			NameServer current = nameServers.get(x) ;
			if (current.getName().contentEquals(name)) {
				retVal = x ;
			}
			current = null ;
		}

		return retVal ;
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
		Editor prefEditor = prefs.edit() ;
		prefEditor.putString("serverNames", getServerNameList()) ;
		prefEditor.putString("serverAddr", getServerAddrList()) ;
		prefEditor.commit() ;
	}
}

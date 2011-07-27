/**
 * 
 */
package com.dns.mobile.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class Whois {

	public static final String lookup(String domainName) throws IOException {
		int lastDot = domainName.lastIndexOf('.')+1 ;
		String tld = domainName.substring(lastDot) ;
		String whoisServer = tld+".whois-servers.net" ;
		StringBuilder sb = new StringBuilder() ;
        int c=-1 ; //default to no data to display
        Socket s = new Socket(whoisServer, 43) ; // port reserved for whois
        s.setSoTimeout(20000) ; // give up after 20 seconds
        InputStream in = s.getInputStream() ;
        OutputStream out = s.getOutputStream() ;
        String host = "=" + domainName + "\n" ;
        byte buf[] = host.getBytes() ;
        out.write(buf) ;
        while ((c = in.read()) != -1) {
        	sb.append((char) c) ;
        }
        s.close() ;
        int responseEnd = 0 ;
        responseEnd = sb.toString().indexOf("<<<")+3 ;
        String retVal = sb.toString().substring(0, responseEnd) ;
        return retVal ;
	}
}

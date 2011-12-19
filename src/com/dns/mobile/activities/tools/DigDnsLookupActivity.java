/**
 * 
 */
package com.dns.mobile.activities.tools;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.Address;
import org.xbill.DNS.SOARecord;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.NSRecord;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Opcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import com.dns.mobile.R;
import com.dns.mobile.data.NameServer;
import com.dns.mobile.data.NameServers;
import com.dns.mobile.util.LogoOnClickListener;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * @author <a href="mailto:deven@dns.com">Deven Phillips</a>
 *
 */
public class DigDnsLookupActivity extends Activity {

	private static final String TAG = "DigDnsLookupActivity" ;
	protected ArrayAdapter<String> nameServerAdapter = null ;

	private class DigLookupTask extends AsyncTask<Message, Void, Message> {
		protected Date start = null ;
		protected Date end = null ;

		/**
		 * Pretty Printer for DNS records. Returns a string to show a record in master file format.
		 * @param rec
		 * @return A formatted string representation of the provided record.
		 */
		private String pp(Record rec, boolean isQuestion) {
			String retVal = null ;

			try {
				switch (rec.getType()) {
					case Type.A:
							if (isQuestion) {
								retVal = String.format(";%-32s%8s%8s\n", rec.getName().toString(),DClass.string(rec.getDClass()),Type.string(rec.getType())) ;
							} else {
								ARecord a = (ARecord)rec ;
								retVal = String.format("%-24s%7d%7s%8s %s\n", a.getName().toString(),
										a.getTTL(),DClass.string(a.getDClass()),Type.string(a.getType()),
										Address.toDottedQuad(a.getAddress().getAddress())) ;
							}
							break ;
					case Type.CNAME:
							if (isQuestion) {
								retVal = String.format(";%-32s%8s%8s\n", rec.getName().toString(),DClass.string(rec.getDClass()),Type.string(rec.getType())) ;
							} else {
								CNAMERecord cname = (CNAMERecord)rec ;
								retVal = String.format("%-24s%8d%8s%8s %s\n",cname.getName().toString(),
										cname.getTTL(),DClass.string(cname.getDClass()),Type.string(cname.getType()),cname.getAlias().toString()) ;
							}
							break ;
					case Type.SOA:
							if (isQuestion) {
								retVal = String.format(";%-32s%8s%8s\n", rec.getName().toString(),DClass.string(rec.getDClass()),Type.string(rec.getType())) ;
							} else {
								SOARecord soa = (SOARecord)rec ;
								retVal = String.format(""
										+ "%-24s"
										+ "%8d"
										+ "%8s"
										+ "%8s "
										+ "%s "
										+ "%s "
										+ "%d "
										+ "%d "
										+ "%d "
										+ "%d\n",
										soa.getName().toString(),
										soa.getTTL(),
										DClass.string(soa.getDClass()),
										Type.string(soa.getType()),
										soa.getHost().toString(),
										soa.getAdmin().toString(),
										soa.getSerial(),
										soa.getRefresh(),
										soa.getRetry(),
										soa.getExpire(),
										soa.getMinimum()) ;
							}
							break ;
					case Type.MX:
							if (isQuestion) {
								retVal = String.format(";%-32s%8s%8s\n", rec.getName().toString(),DClass.string(rec.getDClass()),Type.string(rec.getType())) ;
							} else {
								MXRecord mx = (MXRecord)rec ;
								retVal = String.format("%-24s%8d%8s%8s%3d %s\n",mx.getName().toString(),mx.getTTL(),
										DClass.string(mx.getDClass()),Type.string(mx.getType()),mx.getPriority(),
										mx.getTarget()) ;
							}
							break ;
					case Type.TXT:
							if (isQuestion) {
								retVal = String.format(";%-32s%8s%8s\n", rec.getName().toString(),DClass.string(rec.getDClass()),Type.string(rec.getType())) ;
							} else {
								TXTRecord txt = (TXTRecord)rec ;
								retVal = String.format("%-24s%8d%8s%8s %s\n", txt.getName().toString(),
										txt.getTTL(),DClass.string(txt.getDClass()),Type.string(txt.getType()),
										txt.getStrings().get(0)) ;
							}
							break ;
					case Type.SRV:
							if (isQuestion) {
								retVal = String.format(";%-32s%8s%8s\n", rec.getName().toString(),DClass.string(rec.getDClass()),Type.string(rec.getType())) ;
							} else {
								SRVRecord srv = (SRVRecord)rec ;
								retVal = String.format("%-24s%8d%8s%8s %3d %3d %6d %s\n",srv.getName().toString(),srv.getTTL(),
										DClass.string(srv.getDClass()),Type.string(srv.getType()),srv.getPriority(),
										srv.getWeight(),srv.getPort(),srv.getTarget()) ;
							}
							break ;
					case Type.AAAA:
							if (isQuestion) {
								retVal = String.format(";%-32s%8s%8s\n", rec.getName().toString(),DClass.string(rec.getDClass()),Type.string(rec.getType())) ;
							} else {
								AAAARecord aaaa = (AAAARecord)rec ;
								retVal = String.format("%-24s%8d%8s%8s %s\n", aaaa.getName().toString(),
										aaaa.getTTL(),DClass.string(aaaa.getDClass()),Type.string(aaaa.getType()),
										Address.toDottedQuad(aaaa.getAddress().getAddress())) ;
							}
							break ;
					case Type.NS:
							if (isQuestion) {
								retVal = String.format(";%-32s%8s%8s\n", rec.getName().toString(),DClass.string(rec.getDClass()),Type.string(rec.getType())) ;
							} else {
								NSRecord ns = (NSRecord)rec ;
								retVal = String.format("%-24s%8d%8s%8s %s\n", ns.getName().toString(),
										ns.getTTL(),DClass.string(ns.getDClass()),Type.string(ns.getType()),
										ns.getTarget()) ;
							}
							break ;
					default:
							retVal = "Unknown record type." ;
							break ;
				}
			} catch (Throwable e) {
				Log.e(TAG, e.getLocalizedMessage(), e) ;
			}
			return retVal.toString() ;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Message doInBackground(Message... params) {
			Message query = params[0] ;
			NameServer ns = (NameServer) ((Spinner)findViewById(R.id.digNameServerCombo)).getSelectedItem() ;
			SimpleResolver resolver = null ;
			try {
				String nameServer = ns.getAddress() ;
				Log.d(TAG, "Using name server: "+nameServer) ;
				resolver = new SimpleResolver(nameServer) ;
				resolver.setPort(53) ;
				start = new Date() ;
				try {
					Message rsp = resolver.send(query) ;
					end = new Date() ;
					return rsp ;
				} catch (IOException ioe) {
					((TextView)findViewById(R.id.digResponseArea)).setText(ioe.getLocalizedMessage()) ;
					Log.e(TAG, ioe.getLocalizedMessage(), ioe) ;
				}
			} catch (UnknownHostException uhe) {
				((TextView)findViewById(R.id.digResponseArea)).setText(uhe.getLocalizedMessage()) ;
				Log.e(TAG, uhe.getLocalizedMessage(), uhe) ;
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Message rsp) {
			super.onPostExecute(rsp);

			StringBuilder resultArea = new StringBuilder() ;

			if (rsp!=null) {
				Header hdr = rsp.getHeader();
				resultArea.append(";; Got Answer:\n");
				resultArea.append(";; -<<HEADER<<- opcode: " + Opcode.string(hdr.getOpcode()) + ", status");
				int QCount = rsp.getSectionArray(Section.QUESTION).length;
				int AnsCount = rsp.getSectionArray(Section.ANSWER).length;
				int AuCount = rsp.getSectionArray(Section.AUTHORITY).length;
				int AdCount = rsp.getSectionArray(Section.ADDITIONAL).length;
				resultArea.append(";; flags: " + hdr.printFlags() + "; QUERY: " + QCount + ", ANSWER: " + AnsCount + ", AUTHORITY: " + AuCount + ", ADDITIONAL: " + AdCount + "\n");
				if (QCount > 0) {
					resultArea.append(";; QUESTION SECTION:\n");
					Record[] questions = rsp.getSectionArray(Section.QUESTION);
					for (Record query : questions) {
						resultArea.append("" + this.pp(query, true));
					}
					resultArea.append("\n\n");
				}
				if (AnsCount > 0) {
					resultArea.append(";; ANSWER SECTION:\n");
					Record[] answers = rsp.getSectionArray(Section.ANSWER);
					for (Record answer : answers) {
						resultArea.append(this.pp(answer, false));
					}
					resultArea.append("\n\n");
				}
				if (AuCount > 0) {
					resultArea.append(";; AUTHORITY SECTION:\n");
					Record[] authorities = rsp.getSectionArray(Section.AUTHORITY);
					for (Record authority : authorities) {
						resultArea.append(this.pp(authority, false));
					}
					resultArea.append("\n\n");
				}
				if (AdCount > 0) {
					resultArea.append(";; ADDITIONAL SECTION:\n");
					Record[] addtions = rsp.getSectionArray(Section.ADDITIONAL);
					for (Record record : addtions) {
						resultArea.append(this.pp(record, false));
					}
					resultArea.append("\n\n");
				}
				resultArea.append(";; Query time: " + (end.getTime() - start.getTime()) + " msec\n");
				resultArea.append(";; MSG SIZE:  rcvd: " + rsp.numBytes() + " bytes\n");
			} else {
				resultArea.append("No response was received for the DNS lookup.") ;
			}
			((TextView)findViewById(R.id.digResponseArea)).setText(resultArea.toString()) ;
			findViewById(R.id.digResponseArea).setVisibility(View.VISIBLE) ;
			findViewById(R.id.digProgressBar).setVisibility(View.GONE) ;
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dns_tool_dig_layout) ;

		ImageView dnsLogo = (ImageView) findViewById(R.id.dnsLogo) ;
		if (dnsLogo==null) {
			Log.e(TAG, "Unable to retrieve reference to dnsLogo") ;
		}
		dnsLogo.setOnClickListener(new LogoOnClickListener(this));

		ArrayAdapter<NameServer> nsAdapter = new ArrayAdapter<NameServer>(getBaseContext(), android.R.layout.simple_spinner_item, NameServers.getInstance(getBaseContext()).getNameServers()) ;
		nsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) ;

		((Spinner)findViewById(R.id.digNameServerCombo)).setAdapter(nsAdapter) ;

		((EditText)findViewById(R.id.digFqdnInput)).setOnKeyListener(new View.OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode==KeyEvent.KEYCODE_ENTER) {
					runDnsQuery();
					return true ;
				}
				return false;
			}
		}) ;

		((Button)findViewById(R.id.digButton)).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				runDnsQuery();
			}
		}) ;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem configNameServers = menu.add(Menu.NONE, 0, 0, "Config Servers");
		configNameServers.setIcon(android.R.drawable.ic_menu_edit) ;
		MenuItem sendEmail = menu.add(Menu.NONE, 1, 1, "E-Mail");
		sendEmail.setIcon(android.R.drawable.ic_menu_send) ;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 1:
				String digResults = ((TextView)findViewById(R.id.digResponseArea)).getText().toString() ;
				String resolvedHost = ((EditText)findViewById(R.id.digFqdnInput)).getText().toString() ;
				Spinner nsSpinner = (Spinner)findViewById(R.id.digNameServerCombo) ;
				NameServer selected = (NameServer) nsSpinner.getSelectedItem() ;
				String nsAddress = selected.getAddress() ;
				String nsName = selected.getName() ;
				String subjectLine = "DIG Result for: "+resolvedHost ;
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND) ;
				emailIntent.setType("plain/text") ;
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subjectLine) ;
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "The following DNS lookup was done from the DNS.com mobile app.\nQueried Server was '"+nsName+"("+nsAddress+")'\n"+digResults) ;
				String[] recipients = {"support@dns.com"} ;
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients) ;
				startActivity(emailIntent) ;
				return true;
			case 0:
				Intent configNameServers = new Intent(getApplicationContext(), DigServerManagerActivity.class) ;
				startActivityForResult(configNameServers, 1) ;
				break ;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==1) {
			((Spinner)findViewById(R.id.digNameServerCombo)).invalidate() ;
		}
	}

	/**
	 * 
	 */
	private void runDnsQuery() {
		findViewById(R.id.digResponseArea).setVisibility(View.GONE) ;
		findViewById(R.id.digProgressBar).setVisibility(View.VISIBLE) ;
		String temp = ((EditText)findViewById(R.id.digFqdnInput)).getText().toString() ;
		String fqdn = null ;
		if (temp.endsWith(".")) {
			fqdn = temp ;
		} else {
			fqdn = temp+"." ;
		}
		String[] types = getResources().getStringArray(R.array.digRecordTypes) ;
		int type = Type.value(types[((Spinner)findViewById(R.id.digRecordTypeCombo)).getSelectedItemPosition()]) ;
		if (type<0) {
			type = Type.ANY ;
		}
		try {
			Record query = Record.newRecord(new Name(fqdn), type, DClass.ANY) ;
			Message lookupRequest = Message.newQuery(query) ;
			new DigLookupTask().execute(lookupRequest) ;
		} catch (TextParseException e) {
			((EditText)findViewById(R.id.digResponseArea)).setText(e.getLocalizedMessage()) ;
			Log.e(TAG, e.getLocalizedMessage(), e) ;
		}
	}
}

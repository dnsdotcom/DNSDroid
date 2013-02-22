package com.dns.android.authoritative.fragments;

import android.widget.ListView;

import com.dns.android.authoritative.R;
import com.dns.android.authoritative.domain.Domain;
import com.dns.android.authoritative.domain.Host;

/**
 * A fragment which displays a {@link ListView} of domains for the configured account.
 * @author <a href="mailto: deven@dns.com">Deven Phillips</a>
 *
 */
public class DomainsFragment extends DNSListFragment {

	@SuppressWarnings("rawtypes")
	protected Class childType = Host.class ;
	@SuppressWarnings("rawtypes")
	protected Class type = Domain.class ;
	protected String basePath = "/domains/" ;
	protected int rowLayout = R.layout.domain_row ;
}

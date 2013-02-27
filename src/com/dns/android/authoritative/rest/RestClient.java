package com.dns.android.authoritative.rest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import android.util.Log;
import com.dns.android.authoritative.domain.Token;
import com.dns.android.authoritative.utils.DNSPrefs_;
import com.google.gson.GsonBuilder;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref; 
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@EBean
public class RestClient {

	protected String lastError = null ;

	public String getLastError() {
		return this.lastError;
	}

	protected final String TAG = "RestClient" ;

	@Pref
	DNSPrefs_ prefs ;

	public <T> T getObject(Class<T> type, String path, HashMap<String, String> parameters) throws RestClientException {
		RestTemplate template = new RestTemplate() ;
		template.setErrorHandler(new DetailedErrorHandler()) ;
		GsonBuilder builder = new GsonBuilder() ;
		builder.setDateFormat("E, d MMM yyyy HH:mm:ss Z") ;
		template.getMessageConverters().add(new GsonHttpMessageConverter(builder.create())) ;
		HttpHeaders hdrs = new HttpHeaders() ;
		hdrs.add("AUTH_TOKEN", prefs.getAuthToken().get()) ;
		Log.d(TAG, "Using AUTH_TOKEN: "+prefs.getAuthToken().get()) ;
		hdrs.add("Accept", "application/json") ;
		HttpEntity<?> requestEntity = new HttpEntity<T>(hdrs) ;
		StringBuilder queryBuilder = new StringBuilder("?") ;
		Iterator<String> keyIterator = parameters.keySet().iterator() ;
		while (keyIterator.hasNext()) {
			String key = keyIterator.next() ;
			String value = parameters.get(key) ;
			queryBuilder.append(key+"="+value) ;
			if (keyIterator.hasNext()) {
				queryBuilder.append("&") ;
			}
		}
		String url = prefs.getBaseAddress().get()+path+queryBuilder.toString() ;
		Log.d(TAG, "GET: "+url) ;
		try {
			ResponseEntity<T> response = template.exchange(url, HttpMethod.GET, requestEntity, type) ;
			if (response.getStatusCode()==HttpStatus.OK) {
				return response.getBody() ;
			} else {
				this.lastError = "HTTP Response Code was '"+response.getStatusCode().value()+"' - '"+response.getStatusCode().getReasonPhrase()+"'. Response body contained: "+response.getBody().toString() ;
				throw new RestClientException(lastError) ;
			}
		} catch (RestClientException rce) {
			this.lastError = rce.getMessage() ;
			throw rce ;
		}
	}

	public <T> T postObject(Class<T> type, T value, String path) throws RestClientException {
		RestTemplate template = new RestTemplate() ;
		template.setErrorHandler(new DetailedErrorHandler()) ;
		GsonBuilder builder = new GsonBuilder() ;
		builder.setDateFormat("E, d MMM yyyy HH:mm:ss Z") ;
		template.getMessageConverters().add(new GsonHttpMessageConverter(builder.create())) ;
		HttpHeaders hdrs = new HttpHeaders() ;
		hdrs.add("AUTH_TOKEN", prefs.getAuthToken().get()) ;
		hdrs.add("Accept", "application/json") ;
		HttpEntity<?> requestEntity = new HttpEntity<T>(value, hdrs) ;
		String url = prefs.getBaseAddress().get()+path ;
		Log.d(TAG, "POST: "+url) ;
		try {
			ResponseEntity<T> response = template.exchange(url, HttpMethod.POST, requestEntity, type) ;
			if (response.getStatusCode()==HttpStatus.CREATED) {
				return response.getBody() ;
			} else {
				this.lastError = "HTTP Response Code was '"+response.getStatusCode().value()+"' - '"+response.getStatusCode().getReasonPhrase()+"'. Response body contained: "+response.getBody().toString() ;
				throw new RestClientException(lastError) ;
			}
		} catch (RestClientException rce) {
			this.lastError = rce.getMessage() ;
			throw rce ;
		}
	}

	public <T> T putObject(Class<T> type, T value, String path) throws RestClientException {
		RestTemplate template = new RestTemplate() ;
		template.setErrorHandler(new DetailedErrorHandler()) ;
		GsonBuilder builder = new GsonBuilder() ;
		builder.setDateFormat("E, d MMM yyyy HH:mm:ss Z") ;
		template.getMessageConverters().add(new GsonHttpMessageConverter(builder.create())) ;
		HttpHeaders hdrs = new HttpHeaders() ;
		hdrs.add("AUTH_TOKEN", prefs.getAuthToken().get()) ;
		hdrs.add("Accept", "application/json") ;
		HttpEntity<?> requestEntity = new HttpEntity<T>(value, hdrs) ;
		String url = prefs.getBaseAddress().get()+path ;
		Log.d(TAG, "PUT: "+url) ;
		try {
			ResponseEntity<T> response = template.exchange(url, HttpMethod.PUT, requestEntity, type) ;
			if (response.getStatusCode()==HttpStatus.ACCEPTED) {
				return response.getBody() ;
			} else {
				this.lastError = "HTTP Response Code was '"+response.getStatusCode().value()+"' - '"+response.getStatusCode().getReasonPhrase()+"'. Response body contained: "+response.getBody().toString() ;
				throw new RestClientException(lastError) ;
			}
		} catch (RestClientException rce) {
			this.lastError = rce.getMessage() ;
			throw rce ;
		}
	}

	public boolean deleteObject(String path) throws RestClientException {
		RestTemplate template = new RestTemplate() ;
		template.setErrorHandler(new DetailedErrorHandler()) ;
		GsonBuilder builder = new GsonBuilder() ;
		builder.setDateFormat("E, d MMM yyyy HH:mm:ss Z") ;
		template.getMessageConverters().add(new GsonHttpMessageConverter(builder.create())) ;
		HttpHeaders hdrs = new HttpHeaders() ;
		hdrs.add("AUTH_TOKEN", prefs.getAuthToken().get()) ;
		hdrs.add("Accept", "application/json") ;
		HttpEntity<?> requestEntity = new HttpEntity<Object>(hdrs) ;

		String url = prefs.getBaseAddress().get()+path ;
		Log.d(TAG, "DELETE: "+url) ;
		try {
			ResponseEntity<Object> response = template.exchange(url, HttpMethod.DELETE, requestEntity, Object.class) ;
			if (response.getStatusCode()==HttpStatus.NO_CONTENT) {
				return true ;
			} else {
				this.lastError = "HTTP Response Code was '"+response.getStatusCode().value()+"' - '"+response.getStatusCode().getReasonPhrase()+"'. Response body contained: "+response.getBody().toString() ;
				throw new RestClientException(lastError) ;
			}
		} catch (RestClientException rce) {
			this.lastError = rce.getMessage() ;
			throw rce ;
		}
	}

	public String getToken(String username, String password) throws RestClientException {
		RestTemplate template = new RestTemplate() ;
		template.setErrorHandler(new DetailedErrorHandler()) ;
		template.getMessageConverters().add(new GsonHttpMessageConverter()) ;

		String url = prefs.getBaseAddress().get()+"/token/?username="+username+"&password="+password ;
		Log.d(TAG, "TOKEN: "+url) ;
		Token response = template.getForObject(url, Token.class) ;
		return response.getToken() ;
	}

	private class DetailedErrorHandler implements ResponseErrorHandler {

		/* (non-Javadoc)
		 * @see org.springframework.web.client.ResponseErrorHandler#handleError(org.springframework.http.client.ClientHttpResponse)
		 */
		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
			if (response!=null) {
				if (response.getStatusCode()!=null) {
					String msg = null;
					BufferedInputStream bis = new BufferedInputStream(response.getBody());
					StringBuilder body = new StringBuilder();
					byte[] buffer = new byte[1024];
					while (bis.read(buffer) > 0) {
						body.append(new String(buffer));
					}
					if (body.length() > 0) {
						msg = response.getStatusText() + "::" + body.toString().trim() ;
					}
					throw new RestClientException(msg);
				}
			}
			throw new IOException("No response data received.") ;
		}

		/* (non-Javadoc)
		 * @see org.springframework.web.client.ResponseErrorHandler#hasError(org.springframework.http.client.ClientHttpResponse)
		 */
		@Override
		public boolean hasError(ClientHttpResponse response) throws IOException {
			if (response!=null) {
				if (response.getStatusCode()!=null) {
					if (response.getRawStatusCode()>=400) {
						return true ;
					} else {
						return false ;
					}
				} else {
					return true ;
				}
			} else {
				return true;
			}
		}
		
	}
}
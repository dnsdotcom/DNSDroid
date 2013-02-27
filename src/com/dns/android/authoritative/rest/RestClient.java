package com.dns.android.authoritative.rest;

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
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@EBean
public class RestClient {

	protected final String TAG = "RestClient" ;

	@Pref
	DNSPrefs_ prefs ;

	public <T> T getObject(Class<T> type, String path, HashMap<String, String> parameters) throws RestClientException {
		RestTemplate template = new RestTemplate() ;
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

		ResponseEntity<T> response = (ResponseEntity<T>) template.exchange(url, HttpMethod.GET, requestEntity, type) ;
		T retVal = response.getBody() ;

		return retVal ;
	}

	public <T> T postObject(Class<T> type, T value, String path) throws RestClientException {
		RestTemplate template = new RestTemplate() ;
		GsonBuilder builder = new GsonBuilder() ;
		builder.setDateFormat("E, d MMM yyyy HH:mm:ss Z") ;
		template.getMessageConverters().add(new GsonHttpMessageConverter(builder.create())) ;
		HttpHeaders hdrs = new HttpHeaders() ;
		hdrs.add("AUTH_TOKEN", prefs.getAuthToken().get()) ;
		hdrs.add("Accept", "application/json") ;
		HttpEntity<?> requestEntity = new HttpEntity<T>(value, hdrs) ;
		String url = prefs.getBaseAddress().get()+path ;
		Log.d(TAG, "POST: "+url) ;
		ResponseEntity<T> response = template.exchange(url, HttpMethod.POST, requestEntity, type) ;
		if (response.getStatusCode()==HttpStatus.CREATED) {
			return response.getBody() ;
		} else {
			throw new RestClientException("HTTP Response Code was '"+response.getStatusCode().value()+"' - '"+response.getStatusCode().getReasonPhrase()+"'. Response body contained: "+response.getBody().toString()) ;
		}
	}

	public <T> T putObject(Class<T> type, T value, String path) throws RestClientException {
		RestTemplate template = new RestTemplate() ;
		GsonBuilder builder = new GsonBuilder() ;
		builder.setDateFormat("E, d MMM yyyy HH:mm:ss Z") ;
		template.getMessageConverters().add(new GsonHttpMessageConverter(builder.create())) ;
		HttpHeaders hdrs = new HttpHeaders() ;
		hdrs.add("AUTH_TOKEN", prefs.getAuthToken().get()) ;
		hdrs.add("Accept", "application/json") ;
		HttpEntity<?> requestEntity = new HttpEntity<T>(value, hdrs) ;
		String url = prefs.getBaseAddress().get()+path ;
		Log.d(TAG, "PUT: "+url) ;
		ResponseEntity<T> response = template.exchange(url, HttpMethod.PUT, requestEntity, type) ;
		if (response.getStatusCode()==HttpStatus.ACCEPTED) {
			return response.getBody() ;
		} else {
			throw new RestClientException("HTTP Response Code was '"+response.getStatusCode().value()+"' - '"+response.getStatusCode().getReasonPhrase()+"'. Response body contained: "+response.getBody().toString()) ;
		}
	}

	public boolean deleteObject(String path) throws RestClientException {
		RestTemplate template = new RestTemplate() ;
		GsonBuilder builder = new GsonBuilder() ;
		builder.setDateFormat("E, d MMM yyyy HH:mm:ss Z") ;
		template.getMessageConverters().add(new GsonHttpMessageConverter(builder.create())) ;
		HttpHeaders hdrs = new HttpHeaders() ;
		hdrs.add("AUTH_TOKEN", prefs.getAuthToken().get()) ;
		hdrs.add("Accept", "application/json") ;
		HttpEntity<?> requestEntity = new HttpEntity<Object>(hdrs) ;

		String url = prefs.getBaseAddress().get()+path ;
		Log.d(TAG, "DELETE: "+url) ;
		ResponseEntity<Object> response = template.exchange(url, HttpMethod.DELETE, requestEntity, Object.class) ;
		if (response.getStatusCode()==HttpStatus.NO_CONTENT) {
			return true ;
		} else {
			throw new RestClientException("HTTP Response Code was '"+response.getStatusCode().value()+"' - '"+response.getStatusCode().getReasonPhrase()+"'. Response body contained: "+response.getBody().toString()) ;
		}
	}

	public String getToken(String username, String password) throws RestClientException {
		RestTemplate template = new RestTemplate() ;
		template.getMessageConverters().add(new GsonHttpMessageConverter()) ;

		String url = prefs.getBaseAddress().get()+"/token/?username="+username+"&password="+password ;
		Log.d(TAG, "TOKEN: "+url) ;
		Token response = template.getForObject(url, Token.class) ;
		return response.getToken() ;
	}
}
package com.dns.android.authoritative.domain;

import java.io.Serializable;

public class Meta implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4714350302239772932L;

	Integer limit ;

	String next ;

	Integer offset ;

	String previous ;

	Integer total_count ;

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public String getPrevious() {
		return previous;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	public Integer getTotal_count() {
		return total_count;
	}

	public void setTotal_count(Integer total_count) {
		this.total_count = total_count;
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dns.mobile.api.runtime;

/**
 * An extension of <code>java.lang.Exception</code> which shows errors in runtime arguments
 * @author Deven Phillips <deven.phillips@gmail.com>
 */
public class InvalidArgumentsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4404997522161376886L;

	public InvalidArgumentsException(String rootCause) {
		super(rootCause) ;
	}
}

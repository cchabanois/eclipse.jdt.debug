/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.sun.jdi;


public class InternalException extends RuntimeException {
    
    /**
     * All serializable objects should have a stable serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
	public InternalException() { }
	
	public InternalException(int errorCode) {
		error = errorCode;
	}
	
	public InternalException(java.lang.String s) {
		super(s);
	}
	
	public InternalException(java.lang.String s, int errorCode) {
		super(s);
		error = errorCode;
	}
	
	public int errorCode() { 
		return error; 
	}
	
	private int error;
}

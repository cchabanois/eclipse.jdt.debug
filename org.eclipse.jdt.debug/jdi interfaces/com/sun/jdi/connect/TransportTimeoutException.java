/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.sun.jdi.connect;

import java.io.IOException;

public class TransportTimeoutException extends IOException {
    
    /**
     * All serializable objects should have a stable serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    
	public TransportTimeoutException() {
	}
	public TransportTimeoutException(String arg1) {
		super(arg1);
	}
}

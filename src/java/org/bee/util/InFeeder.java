/* bee - InFeeder.java
 * Copyright (C) 1999-2004 Dmitriy Rogatkin.  All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  $Id: InFeeder.java,v 1.4 2005/06/15 08:02:23 rogatkin Exp $
 * Created on Aug 12, 2004
 */

package org.bee.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * @author dmitriy
 * @author Rohtash Singh Lakra
 * 
 *         TODO: consider encoding sometime
 */
public class InFeeder extends Thread {
	protected InputStream is;
	protected String value;
	protected OutputStream os;
	protected boolean runs;
	int buf_size = 256;
	
	/**
	 * 
	 * @param is
	 * @param os
	 */
	public InFeeder(InputStream is, OutputStream os) {
		this.is = is;
		this.os = os;
		setName("In stream reader " + is);
	}
	
	public InFeeder(String iv, OutputStream os) {
		value = iv;
		this.os = os;
		setName("Value reader ");
	}
	
	/**
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		runs = true;
		try {
			if (value != null) {
				// encoding?
				os.write(value.getBytes());
			} else {
				byte buf[] = new byte[buf_size];
				int n;
				while (runs) {
					if (is.available() > 0) {
						n = is.read(buf);
						if (n > 0) {
							os.write(buf, 0, n);
							os.flush();
						} else {
							runs = false;
						}
					} else {
						try {
							
							Thread.sleep(100);
						} catch (InterruptedException ie) {
						}
					}
				}
			}
		} catch (IOException ex) {
			Logger.getLogger("Bee").severe("Exception:" + ex);
		}
	}
	
	/**
	 * 
	 */
	public void terminate() {
		runs = false;
	}
}
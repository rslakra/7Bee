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

import jdepend.framework.BeeHelper;

/**
 * @author dmitriy
 * @author Rohtash Singh Lakra
 * 
 *         TODO: consider encoding sometime
 */
public class InFeeder extends Thread {
	protected InputStream inputStream;
	protected String value;
	protected OutputStream outputStream;
	protected boolean running;
	final int BUFER_SIZE = 256;
	
	/**
	 * 
	 * @param inputStream
	 * @param outputStream
	 */
	public InFeeder(InputStream inputStream, OutputStream outputStream) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		setName("In stream reader " + inputStream);
	}
	
	/**
	 * 
	 * @param value
	 * @param outputStream
	 */
	public InFeeder(String value, OutputStream outputStream) {
		this.value = value;
		this.outputStream = outputStream;
		setName("Value reader ");
	}
	
	/**
	 * Returns the value of the <code>running</code> property.
	 * 
	 * @return
	 */
	public final boolean isRunning() {
		return running;
	}
	
	/**
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		running = true;
		try {
			if (value != null) {
				// encoding?
				outputStream.write(value.getBytes());
			} else {
				byte buffer[] = new byte[BUFER_SIZE];
				int length;
				while (isRunning()) {
					if (inputStream.available() > 0) {
						length = inputStream.read(buffer);
						if (length > 0) {
							outputStream.write(buffer, 0, length);
							outputStream.flush();
						} else {
							terminate();
						}
					} else {
						try {
							Thread.sleep(100);
						} catch (InterruptedException ex) {
							// ignore me!
						}
					}
				}
			}
		} catch (IOException ex) {
			Logger.getLogger("Bee").severe("Exception:" + BeeHelper.toString(ex));
		}
	}
	
	/**
	 * 
	 */
	public void terminate() {
		running = false;
	}
}
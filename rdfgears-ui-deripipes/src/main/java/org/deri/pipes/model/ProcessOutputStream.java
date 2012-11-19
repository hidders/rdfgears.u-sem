/*
 * Copyright (c) 2008-2009,
 * 
 * Digital Enterprise Research Institute, National University of Ireland, 
 * Galway, Ireland
 * http://www.deri.org/
 * http://pipes.deri.org/
 *
 * Semantic Web Pipes is distributed under New BSD License.
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright 
 *    notice, this list of conditions and the following disclaimer in the 
 *    documentation and/or other materials provided with the distribution and 
 *    reference to the source code.
 *  * The name of Digital Enterprise Research Institute, 
 *    National University of Ireland, Galway, Ireland; 
 *    may not be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.deri.pipes.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.lf5.util.StreamUtils;
import org.deri.pipes.core.ExecBuffer;

/**
 * 
 * A hack to combine stdout and stderr stream in InputStreamProvider. 
 *  
 * 
 * Author: Eric Feliksik
 *
 */
public class ProcessOutputStream implements ExecBuffer,InputStreamProvider { 

	private InputStream stdout;
	private InputStream stderr;
	
	public ProcessOutputStream(Process p){
		this.stdout = p.getInputStream();
		this.stderr = p.getErrorStream();
	}
	
	/* (non-Javadoc)
	 * @see org.deri.pipes.core.ExecBuffer#stream(org.deri.pipes.core.ExecBuffer)
	 */
	@Override
	public void stream(ExecBuffer outputBuffer) throws IOException {
		throw new IOException("stream(ExecBuffer) not implemented. Use getInputStream() instead");
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.core.ExecBuffer#stream(org.deri.pipes.core.ExecBuffer, java.lang.String)
	 */
	@Override
	public void stream(ExecBuffer outputBuffer, String context)
			throws IOException {
		throw new IOException("stream(ExecBuffer,String) not implemented. Use getInputStream() instead");
		
	}
	
	/* (non-Javadoc)
	 * @see org.deri.pipes.core.ExecBuffer#stream(java.io.OutputStream)
	 */
	@Override
	public void stream(OutputStream output) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
		
		/**
		 * First output everything from stdout
		 */
		BufferedReader outputReader = new BufferedReader(new InputStreamReader(stdout));
		String line;
		while ((line=outputReader.readLine()) != null){
			writer.write(line);
			writer.write('\n');
		}
		writer.flush();
		
		/**
		 * now output everything from stderr
		 */
		BufferedReader errorReader = new BufferedReader(new InputStreamReader(stderr));
		
		boolean alreadyOutputErrorLine = false;
		while ((line=errorReader.readLine()) != null){
			if (!alreadyOutputErrorLine){
				/* print an error header */
				writer.write("\n\n****************************\n");
				writer.write("RDF Gears output on stderr:");
				writer.write("\n\n****************************\n");
				alreadyOutputErrorLine = true;
			}
			writer.write(line);
			writer.write('\n');

		}
		writer.flush();
	}
	
	/**
	 * Get the underlying input stream.
	 * @return
	 * @throws IOException 
	 */
	public synchronized InputStream getInputStream() throws IOException{
		// WARNING, we are only returning stdout. 
		return stdout;
	}
	
	
	
}

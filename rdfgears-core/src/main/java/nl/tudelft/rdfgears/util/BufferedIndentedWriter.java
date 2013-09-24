package nl.tudelft.rdfgears.util;

/*
 * #%L
 * RDFGears
 * %%
 * Copyright (C) 2013 WIS group at the TU Delft (http://www.wis.ewi.tudelft.nl/)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * A tool to do indented writing
 * 
 * 
 * @author Eric Feliksik
 *
 */
public class BufferedIndentedWriter extends java.io.Writer {
	private BufferedWriter writer;
	private int currentIndent = 0;
	private String indentationStr = "  "; // some spaces
	
	private boolean newlineFlag = false;
	
	public BufferedIndentedWriter(OutputStream out){
		this(new OutputStreamWriter(out));
	}

	public BufferedIndentedWriter(Writer writer){
		this.writer = new BufferedWriter(writer);
	}
	
	
	public void print(String s) throws IOException{
		writer.write(s);
	}
	
	public void incIndent(){
		currentIndent++;
	}
	
	public void outdent(){
		currentIndent--;
	}
	
	private void writeIndentation() throws IOException{
		for (int i=0; i<currentIndent; i++){
			writer.write(indentationStr);
		}
	}
	
	public void resetNewlineFlag(){
		newlineFlag = false;
	}
	public boolean getNewlineFlag(){
		return newlineFlag;
	}
	
	public void newline() throws IOException{
		newlineFlag = true;
		writer.newLine();
		writeIndentation();
	}

	public void flush() throws IOException {
		writer.flush();
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		writer.write(cbuf, off, len);
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}
}

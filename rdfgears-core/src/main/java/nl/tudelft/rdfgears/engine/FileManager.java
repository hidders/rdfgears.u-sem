package nl.tudelft.rdfgears.engine;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * A manager providing BufferedWriter objects for files that have been opened. 
 * @author Eric Feliksik
 *
 */
public class FileManager {
	private Map<String, BufferedWriter> writerMap = new HashMap<String, BufferedWriter>(); 

	protected FileManager(){
		
	}
	
	/**
	 * Returns a buffered writer to the file with given name. 
	 * 
	 * Filename is relative to the path_to_work_files directory specified in the config.  
	 * 
	 * If the file cannot be opened, a RuntimeException will be thrown.
	 * The writer should *NOT* be closed the the user of this function. 
	 * At the end of the workflow execution Engine.close() will be called
	 * to close it.   
	 * 
	 * @param filename
	 * @return
	 */
	public Writer getFileWriter(String fileName){
		String fullFileName = Engine.getConfig().getPathToWorkFiles() + "/" + fileName;
		BufferedWriter bufferedWriter = writerMap.get(fullFileName);
		
		if (bufferedWriter==null){
			FileOutputStream fout;
			try
			{
			    // Open an output stream
			    fout = new FileOutputStream(fullFileName);
			    OutputStreamWriter outWriter = new OutputStreamWriter(fout, "UTF-8");
			    bufferedWriter = new BufferedWriter(outWriter);
			    writerMap.put(fullFileName, bufferedWriter);
			}
			catch (IOException e)
			{
				Engine.getLogger().warn("Unable to write to file '"+fullFileName+"'");
				Engine.getLogger().warn("Does the directory exist? ");
				throw new RuntimeException(e);
			}
			
			Engine.getLogger().info("Opened file "+fullFileName+" for writing");
		}
		
		return bufferedWriter;
	}
	
	/**
	 * close all openened writers. This flushes the files and closes them.  
	 */
	public void close(){
		for (Writer writer : writerMap.values()){
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

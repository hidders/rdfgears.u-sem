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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.xml.stream.XMLStreamException;

import nl.tudelft.rdfgears.rgl.datamodel.type.GraphType;
import nl.tudelft.rdfgears.rgl.datamodel.type.TypedValue;
import nl.tudelft.rdfgears.rgl.datamodel.type.TypedValueImpl;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.serialization.rglxml.RGLXMLParser;
import nl.tudelft.rdfgears.rgl.datamodel.value.serialization.rglxml.ValueXMLSerializer;

import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Model;


/**
 * A valuestore to which RGL values can be written and read from. 
 * 
 * 
 * @author Eric Feliksik
 *
 */
public class SimpleValueStore {
	
	private int valueCounter = 1;
	private final static String STORAGE_PATH = Config.getStoragePath() +"/valuestore/";
	
	public SimpleValueStore(){
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws IOException 
	 */
	public TypedValue getValue(String id) throws IOException{
		
		String fileName = STORAGE_PATH+id;
		FileInputStream fin = new FileInputStream(fileName);
		
		try {
			RGLXMLParser parser = new RGLXMLParser(fin);
			return parser;
			
		} catch (XMLStreamException e) {
			/* maybe it was RDF/XML, not RGL/XML. Try a generic Jena reader */
			
			System.err.println("Could not read file "+fileName+" as RGL/XML. Trying as RDF/XML"); 
			e.printStackTrace();
			try {
			
				JenaReader rdfReader = new JenaReader();
				FileInputStream fin2 = new FileInputStream(STORAGE_PATH+id);
				Model model = ValueFactory.createModel();
				rdfReader.read(model, fin2, null);
				GraphValue graph = ValueFactory.createGraphValue(model);
				return new TypedValueImpl(GraphType.getInstance(), graph);
					
			} catch (Exception e2){
				throw new IOException("Could not read file "+fileName+" as RDF/XML: "+e.getMessage());	
			}
			
		} finally {
			fin.close();
		}
	}
	

	/**
	 * store a value
	 * @param value The value to store
	 * @param id the id to use. If null, a new id will be generated.  
	 * @return the id under which the value was stored
	 * @throws IOException 
	 */
	public String putValue(TypedValueImpl typedValue, String id) throws IOException {
		/* mkdir, if it does not yet exist */
		File file = new File(STORAGE_PATH);
		file.mkdirs();
		
		if (id==null){
			id = getNewId();
		}
		
		FileOutputStream fout;
		fout = new FileOutputStream(STORAGE_PATH+id);
//		(new ValueXMLSerializerWithExternalGraphs(fout, STORAGE_PATH)).serialize(typedValue.getValue());
		(new ValueXMLSerializer(fout)).serialize(typedValue.getValue());
		fout.close();
		return id; 
	}

	private String getNewId() {
		return UUID.randomUUID().toString();
	}
}

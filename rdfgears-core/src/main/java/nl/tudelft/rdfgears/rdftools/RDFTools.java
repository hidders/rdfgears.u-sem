package nl.tudelft.rdfgears.rdftools;

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

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import nl.tudelft.rdfgears.engine.ValueFactory;
import com.hp.hpl.jena.query.DataSource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;


public class RDFTools {
	static final String HEXES = "0123456789ABCDEF";
	

	
	private static String getHex( byte [] raw ) {
		if ( raw == null ) {
		  return null;
		}
		final StringBuilder hex = new StringBuilder( 2 * raw.length );
		for ( final byte b : raw ) {
		  hex.append(HEXES.charAt((b & 0xF0) >> 4))
		     .append(HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}
	

	
	public static Model constructQuery(DataSource dataSource, String queryStr){
		Query query = QueryFactory.create(queryStr) ;
		QueryExecution qexec = QueryExecutionFactory.create(query, dataSource);
		Model resultModel = qexec.execConstruct() ;
		qexec.close(); 
		return resultModel;
	}
	

	
	
	public static Model constructQuery(String queryService, String queryStr, boolean useCache){
		Model returnModel;
		String hash = "no-hash";
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest((queryService+queryStr).getBytes());
			hash = getHex(digest);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String cacheFileName = "/tmp/"+hash+".xml";

		
		if (useCache){
			try {
				returnModel = ValueFactory.createModel().read(new FileInputStream(cacheFileName), "http://dont_care");
				return returnModel;
			}
			catch(Exception e){
				System.out.println("No cache file found: "+cacheFileName+", loading from service");
			}
		}
		
		Query query = QueryFactory.create(queryStr);
		
		assert(false): "not implemented"; 
		/*
		QueryExecution qexec = QueryExecutionFactory.create(query, dataSource);
		returnModel = (new Operation(queryStr)).executeConstruct(queryService);
		// save to cache  
		ModelTools.writeModel(returnModel, cacheFileName);
		
		return returnModel;
		
		*/
		return null;
		
	}
	
}

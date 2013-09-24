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


import java.util.List;

import nl.tudelft.rdfgears.engine.valuefactory.MemoryValueFactory;
import nl.tudelft.rdfgears.engine.valuefactory.ValueFactoryIface;
import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.BooleanValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.LiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RDFValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.ModifiableRecord;
import nl.tudelft.rdfgears.util.row.FieldIndexMap;
import nl.tudelft.rdfgears.util.row.ValueRow;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;


/**
 * A static facade to the configured implementation of ValueFactoryIface, for your convenience.
 * 
 * @author Eric Feliksik
 *
 */
public class ValueFactory {
	
	private static long valueId = 0; // counter for values, every value will have unique id
	
	/*******************************************************************************
	 * Factory configuration methods 
	 */
	
	/**
	 * The factory implementation that is used by this facade
	 * It may later become configurable (then it can no longer be final). 
	 */
	private static final ValueFactoryIface factory = new MemoryValueFactory();
	//private static final ValueFactory factory = new DiskValueFactory();
//	
//	private static final ValueFactoryIface getFactory() {
//		if (Engine.getConfig().isDiskBased()) {
//			return new DiskValueFactory();
//		} else {
//			return new MemoryValueFactory();
//		}
//	}
//	
	/**
	 * Get a unique id for a value
	 * @return
	 */
	public static long getNewId() {
		return valueId++;
	}
	

	/***********************************************************************
	 * RDF Value methods
	 */
	
	
	/**
	 * Create an RDF value given a Jena RDFNode object. 
	 * @param node
	 * @return
	 */
	public static RDFValue createRDFValue(RDFNode node){
		return factory.createRDFValue(node);
	}
	
	/**
	 * Create a URI 
	 * @param uri
	 * @return
	 */
	public static RDFValue createURI(String uri){
		return factory.createURI(uri);
	}
	
	/***********************************************************************
	 * Literal methods
	 */
	
	/**
	 * Create a typed literal, given an object and it's RDFDatatype. 
	 * Internally this will often resort to a Jena literal. Note that value may be an objects string 
	 * representation, but a natural object (e.g. a Double) will often be faster.  
	 */
	public static LiteralValue createLiteralTyped(Object value, RDFDatatype dtype){
		return factory.createLiteralTyped(value, dtype);
	}
	
	/**
	 * Create a double literal. 
	 * @param d
	 * @return
	 */
	public static LiteralValue createLiteralDouble(double d) {
		return factory.createLiteralDouble(d);
		// don't do Double.toString(d), but do new Double(d)! it's much much faster because it doesn't require string parsing
	}
	
	/**
	 * Creat a plain literal. 
	 * 
	 * @param str the value string
	 * @param language the language string, may be null. 
	 * @return
	 */
	public static LiteralValue createLiteralPlain(String str, String language){
		return factory.createLiteralPlain(str, language);
	}
	

	public static BooleanValue createFalse(){
		return factory.createFalse();
	}
	public static BooleanValue createTrue(){
		return factory.createTrue();
	}
	
	public static RGLValue createNull(String string){
		return factory.createNull(string);
	}
	
	
	/***********************************************************************
	 * Graph methods
	 */
	
	/***
	 * Create a local RGL graph value
	 * @param model
	 * @return
	 */
	public static GraphValue createGraphValue(Model model){
		return factory.createGraphValue(model);
	}
	
	/**
	 * Create a Jena model
	 */
	public static Model createModel(){
		return factory.createModel();
	}
	
	/***********************************************************************
	 * Bag methods
	 */
		
	/**
	 * Create a List that can be used as backing structure for the createBagValue() call.
	 * @return
	 */
	public static List<RGLValue> createBagBackingList(){
		return factory.createBagBackingList();
	}
	
	/**
	 * Create a singleton bag with the given element as only element. 
	 * @param value
	 * @return
	 */
	public static BagValue createBagSingleton(RGLValue value){
		return factory.createBagSingleton(value);
	}
	
	/**
	 * Return an empty bag (can not be filled with elements). 
	 * @return
	 */
	public static BagValue createBagEmpty(){
		return factory.createBagEmpty();
	}
	
	/***********************************************************************
	 * Record methods
	 */
	
	/**
	 * Create a record, given a row over values. 
	 */
	public static RecordValue createRecordValue(ValueRow row){
		return factory.createRecordValue(row);
	}
	
	/**
	 * create an empty, modifyable record. 
	 * It can be filled with the put(key, value) for every key in the FieldIndexMap
	 * @param row
	 * @return
	 */
	public static ModifiableRecord createModifiableRecordValue(FieldIndexMap map){
		return factory.createModifiableRecordValue(map);
	}

	
	
	
}

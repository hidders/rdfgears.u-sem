package nl.tudelft.rdfgears.engine.valuefactory;

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

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.BooleanValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.LiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RDFValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLNull;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.URIValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.MemoryGraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.MemoryLiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.MemoryRecordValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.MemoryURIValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.ModifiableRecord;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.bags.SingletonBag;
import nl.tudelft.rdfgears.util.row.FieldIndexMap;
import nl.tudelft.rdfgears.util.row.ValueRow;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class MemoryValueFactory implements ValueFactoryIface {
	
	private RGLNull error = new RGLNull();

	@Override
	public Model createModel() {
		// FIXME: learn to better understand the model factory concept
		// does this give a memory model by definition? 
		return ModelFactory.createDefaultModel();
	}
	
	@Override
	public GraphValue createGraphValue(Model model){
		return new MemoryGraphValue(model);
	}

	@Override
	public List<RGLValue> createBagBackingList(){
		return new ArrayList<RGLValue>();
		//return new DiskList();
	}
	
	
	@Override
	public RecordValue createRecordValue(ValueRow row){
		return new MemoryRecordValue(row);
	}

	@Override
	public ModifiableRecord createModifiableRecordValue(FieldIndexMap map) {
		return new ModifiableRecord(map);
	}

	
	@Override
	public RDFValue createRDFValue(RDFNode node){
		if (node.isLiteral()){
			return MemoryLiteralValue.createLiteral(node.asLiteral());
		} else if (node.isURIResource()){
			return new MemoryURIValue(node);
		}
		
		assert(false): "Fixme, not implemented";
		return null;
	}
	
	@Override
	public LiteralValue createLiteralTyped(Object value, RDFDatatype dtype){
		return MemoryLiteralValue.createLiteralTyped(value, dtype);
	}

	@Override
	public LiteralValue createLiteralDouble(double d) {
		return MemoryLiteralValue.createLiteralTyped(new Double(d), XSDDatatype.XSDdouble);
	}
		
	@Override
	public URIValue createURI(String uri){
		return new MemoryURIValue(uri);
	}
//
//	public static RGLValue createFalse(){
//		return BooleanValueImpl.getFalseInstance();
//	}
//	public static RGLValue createTrue(){
//		return BooleanValueImpl.getTrueInstance();
//	}
//	

	@Override
	public LiteralValue createLiteralPlain(String str, String language) {
		return MemoryLiteralValue.createPlainLiteral(str, language);
	}

	@Override
	public BagValue createBagSingleton(RGLValue value) {
		return new SingletonBag(value);
	}

	@Override
	public RGLValue createNull(String string) {
		if (string==null)
			return error;
		
		return new RGLNull(string);
	}

	public BooleanValue createFalse(){
		return falseValue;
	}
	public BooleanValue createTrue(){
		return trueValue;
	}

	@Override
	public BagValue createBagEmpty() {
		return emptyBag;
	}
	
	
}

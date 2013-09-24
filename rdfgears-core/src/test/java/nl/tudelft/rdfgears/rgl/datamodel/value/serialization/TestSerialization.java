package nl.tudelft.rdfgears.rgl.datamodel.value.serialization;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.ModifiableRecord;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.bags.ListBackedBagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.serialization.rglxml.RGLXMLParser;
import nl.tudelft.rdfgears.rgl.datamodel.value.serialization.rglxml.ValueXMLSerializer;
import nl.tudelft.rdfgears.util.row.FieldIndexMap;
import nl.tudelft.rdfgears.util.row.FieldIndexMapFactory;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResourceFactory;


public class TestSerialization {
	private final static String field1 = "field1";
	private final static String field2 = "field2";

	@Before 
	public void initialize() {
	}

	String[] files = {
			"./data/rglvalues/silkResult.xml",
			"./data/rglvalues/simple.xml",
	};
	
	@Test 
	public void testDeserializer(){
		for (int i=0; i<files.length; i++){
			deserializeTest(files[i]);
		}
	}
	
	public void deserializeTest(String fileName){
		FileInputStream fi;
		try {
			fi = new FileInputStream(fileName);
			
			RGLXMLParser parser = new RGLXMLParser(fi);
			RGLValue value = parser.getValue();
			
			System.out.println("Parsed value of type "+parser.getType());
			System.out.println("*********************************************************");
			assertTrue("Parsing should not return null", value!=null);
			ValueXMLSerializer ser = new ValueXMLSerializer(System.out);
			ser.serialize(value);
			
		} catch (FileNotFoundException e) {
			assertTrue("Could not find file "+fileName, false);
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	
	
	@Test
	public void testRGLXML(){
		BagValue bag = createComplexValue();
		ByteArrayOutputStream baOut = new ByteArrayOutputStream();
		
		ValueXMLSerializer serializer = new ValueXMLSerializer(baOut);
		serializer.serialize(bag);
		
		/* ok, that was serialization. Now deserialize. 
		 */
		ByteArrayInputStream baIn = new ByteArrayInputStream(baOut.toByteArray());
		
		try {
			RGLXMLParser parser = new RGLXMLParser(baIn);
			RGLValue value = parser.getValue();
			RGLType type = parser.getType();
			
			System.out.println("Parsed a value of type "+type.toString());
			
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			System.err.println("Could not parse the value: ");
			ValueXMLSerializer ser = new ValueXMLSerializer(System.err);
			ser.serialize(bag);
			e.printStackTrace();
			assertTrue("parsing failed: "+e.getMessage(), false);
			
		}
		
		
		
		String s = baOut.toString();
		System.out.println(s);
	}
	
	
	/* create a value of type 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	private BagValue createComplexValue() {
		Model model = ValueFactory.createModel(); // one triple is enough, we trust Jena for (de)serialization of RDF 
		model.add(ResourceFactory.createResource("http://example.com/item/resource1"),
				ResourceFactory.createProperty("http://example.com/ont/prop1"),
				ResourceFactory.createResource("http://example.com/item/resource2")); 
		
		FieldIndexMap fiMap = FieldIndexMapFactory.create(field1, field2);
		
		ModifiableRecord rec1 = ValueFactory.createModifiableRecordValue(fiMap);
		rec1.put(field1, ValueFactory.createNull(null)); 
		rec1.put(field2, ValueFactory.createLiteralDouble(0.2)); 
		ModifiableRecord rec2 = ValueFactory.createModifiableRecordValue(fiMap);
		rec2.put(field1, ValueFactory.createBagEmpty()); 
		rec2.put(field2, ValueFactory.createNull(null));
		
		ModifiableRecord rec3 = ValueFactory.createModifiableRecordValue(fiMap);
		
		List<RGLValue> listOfGraphs = ValueFactory.createBagBackingList();
		listOfGraphs.add(ValueFactory.createGraphValue(model));
		listOfGraphs.add(ValueFactory.createGraphValue(model));
		
		rec3.put(field1, new ListBackedBagValue(listOfGraphs)); 
		rec3.put(field2, ValueFactory.createNull(null));
		
		List<RGLValue> list = ValueFactory.createBagBackingList();
		list.add(rec1);
		list.add(rec2);
		list.add(rec3);
		
		return new ListBackedBagValue(list);
		
	}
}

package tests.profiling;

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
import java.util.Map;
import java.util.TreeMap;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.value.LiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.util.row.FieldIndexMap;
import nl.tudelft.rdfgears.util.row.FieldIndexMapFactory;
import nl.tudelft.rdfgears.util.row.FieldMappedValueRow;

import org.junit.Before;
import org.junit.Test;

import tools.Profiling;
import tools.Timer;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class TestDSSize {
	public static final int MAPSIZE = 7;
	public static final int MAPS = 1000*10;
	
	RGLValue valueArray[] = new RGLValue[MAPSIZE];
	String keyArray[] = new String[MAPSIZE];
	private boolean initialized = false;
	
	/* enum */
	private static final int MAP_TREE = 1;
	private static final int MAP_FIELDINDEX = 2;
	
	
	@Before public void init(){
		if (!initialized){

			for (int i=0; i<MAPSIZE; i++){
				/* create key */
				keyArray[i] = "theKey_"+i;
				
				/* create value */
				valueArray[i] = ValueFactory.createLiteralPlain("theLiteral_"+i, null); // untyped
			}	
		}
	}
	
	@Test public void testAll(){
		createMap(MAP_FIELDINDEX);
		createMap(MAP_TREE);
		runRDFValues();
	}

	private void runRDFValues() {
		System.out.println( XSDDatatype.XSDdouble.getURI());
		Timer timer = new Timer();
		timer.start("Making "+MAPS+" RGL double values. This needs a lot of memory!");
		ArrayList<RDFNode> nodeList = new ArrayList<RDFNode>();
		ArrayList<LiteralValue> valList = new ArrayList<LiteralValue>();
		for(int i=0; i<MAPS; i++){
			/*
			 * RDFNode's are heavier than RGLValues, therefore the Factory and classes postpone creating them.   
			 */
			
//			/* slower & more memory*/
//			Literal rdfLit = Engine.getDefaultModel().createTypedLiteral(new Double(i), XSDDatatype.XSDdouble);
//			nodeList.add(rdfLit);
			
			/* faster & lighter */
			LiteralValue val = ValueFactory.createLiteralTyped(new Double(i), XSDDatatype.XSDdouble);
			valList.add(val);
		}
		
//		System.out.println(nodeList.get(0)); 
		
		System.out.println(valList.get(0));
		
		timer.end();
		Profiling.collectGarbage();

	}
	
	
	private void createMap(int type) {
		init();
		
		
		Timer timer = new Timer();
		timer.start("Making "+MAPS+" Maps of type "+(type==MAP_FIELDINDEX ? "FieldIndex" : "TreeMap"));
		
		@SuppressWarnings("rawtypes")
		ArrayList list ;  // intention raw, unparametrized type
		
		if(type==MAP_TREE){
			list = new ArrayList<Map<String,RGLValue>>();
			for(int i=0; i<MAPS; i++){
				Map<String,RGLValue> map = new TreeMap<String, RGLValue>(); // alternative is HashMap
				for (int j=0; j<MAPSIZE; j++){
					map.put(keyArray[j], valueArray[j]);
				}
				list.add(map);
			}
				
		} else {
			
			/* create fieldIndexMap */
			FieldIndexMap fiMap = FieldIndexMapFactory.create(keyArray);
			
			list = new ArrayList<FieldMappedValueRow>();
			for(int i=0; i<MAPS; i++){
				FieldMappedValueRow map = new FieldMappedValueRow(fiMap);
				for (int j=0; j<MAPSIZE; j++){
					map.put(keyArray[j], valueArray[j]);
				}
				list.add(map);
			}
			
		}
		
		
		
		timer.end();
		
		Profiling.printUsedMemoryString();
		
		Profiling.collectGarbage();
		
		
		System.out.println("\n\n\n\n\n");
		
    }
	
	
    
}

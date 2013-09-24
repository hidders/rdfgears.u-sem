package nl.tudelft.rdfgears.rgl.datamodel.type.tests;

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

import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.BooleanType;
import nl.tudelft.rdfgears.rgl.datamodel.type.GraphType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.SubType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RecordType;
import nl.tudelft.rdfgears.rgl.datamodel.type.SuperTypePattern;
import nl.tudelft.rdfgears.util.row.TypeRow;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestTypes {
	
	String fieldA = "A";
	String fieldB = "B";
	
	int[][] specs ; 
	private final static int TOTAL_TYPES = 19; 
	
	RGLType[] types = new RGLType[TOTAL_TYPES];
	
	
	/** 
	 * make a typerow. ar should have even number of elements. 
	 * At even index, element should be String. 
	 * At odd index, element is RGLType. 
	 */
	private TypeRow typeRow(Object... ar){
		TypeRow temp = new TypeRow();
		for (int i=0; i<ar.length; i+=2){
			temp.put(
					(String)ar[i],  /* show classcastexception on correct line */
					(RGLType) ar[i+1]
				);
		}
		return temp;
		
	}
	
	boolean initialized = false;
	private String[] relation_desc = { 
			" ==",  /* is equal to */
			" << ", /* is subtype of */
			" >> ", /* is supertype of */
			" != ", /* incompatible */ 
	}; 
	
	@Before public void initialize() {
		if(initialized)
			return;
		
		/**
		 * Subtype specifications.
		 * 
		 *  The rows are called A. The columns are called B. 
		 *  The indices are the index of the types[] array. 
		 *  
		 *  So T=specs[iA][iB] defines whether type A (types[iA]) is a subtype of type B (types[iB]).
		 *  
		 *  If T==0, then the values are equal. 
		 *  If T==1, then A is a subtype of B 
		 *  If T==2, then A is a supertype of B 
		 *  If T==3, then there is no direct supertype / subtype relation
		 *  
		 *  If T<0, it is undefined and we don't want to check it. 
		 *  The table is only half, as it is symmetic. 
		 * 
		 */
		int[][] specs = { 	 
			/*	A \ B		0	1	2	3	4	5	6	7	8	9	10	11	12	13	14	15	16	17	18 */
			/* 0 */ 	{ 	0,	},
			/* 1 */ 	{ 	3,	0,	},
			/* 2 */ 	{ 	3,	3,	0,	},
			/* 3 */ 	{ 	1,	1,	1,	0,	},
			/* 4 */ 	{ 	2,	2,	2,	2,	0,	},
			/* 5 */ 	{ 	2,	2,	3,	2,	-1,	-1,	},
			/* 6 */ 	{ 	3,	3,	3,	2,	1,	3,	0,	},
			/* 7 */ 	{ 	3,	3,	3,	2,	1,	3,	1,	0,	},
			/* 8 */ 	{ 	3,	3,	3,	2,	1,	3,	3,	3,	0,	},
			/* 9 */ 	{ 	3,	3,	3,	2,	1,	3,	1,	3,	3,	0,	},
			/* 10 */ 	{ 	3,	3,	3,	2,	1,	3,	3,	3,	3,	3,	0,	},
			/* 11 */ 	{ 	3,	3,	3,	2,	1,	3,	3,	3,	3,	3,	3,	0,	},
			/* 12 */ 	{ 	3,	3,	3,	2,	1,	3,	3,	3,	3,	3,	3,	3,	0,	},
			/* 13 */ 	{ 	3,	3,	3,	2,	1,	3,	3,	3,	3,	3,	3,	3,	1,	0,	},
			/* 14 */ 	{ 	3,	3,	3,	2,	1,	3,	3,	3,	3,	3,	3,	3,	1,	0,	0,	},
			/* 15 */ 	{ 	3,	3,	3,	2,	1,	3,	3,	3,	3,	3,	3,	3,	1,	3,	3,	0,	},
			/* 16 */ 	{ 	3,	3,	3,	2,	1,	3,	3,	3,	3,	3,	3,	3,	3,	3,	3,	3,	0,	},
			
			
			/* 17 */ 	{ 	3,	3,	3,	2,	1,	3,	2,	2,	2,	2,	3,	3,	3,	3,	3,	3,	3,	-1,	},
			/* 18 */ 	{ 	3,	3,	3,	2,	1,	3,	2,	2,	2,	2,	2,	3,	3,	3,	3,	3,	3,	-1,	-1,	},
				
			}; 
		this.specs = specs;
		
		types[0]  = GraphType.getInstance(); 													/* Graph */
		types[1]  = RDFType.getInstance(); 														/* RDF */
		types[2]  = BooleanType.getInstance(); 													/* Boolean */
		types[3]  = SubType.getInstance(); 														/* SUB */
		types[4]  = new SuperTypePattern(); 													/* SuperAll */
		types[5]  = new SuperTypePattern(types[0], types[1]); 									/* SuperGraphOrRDF */
		types[6]  = RecordType.getInstance(typeRow( fieldA, types[0]));         			/* [A:Graph] */  
		types[7]  = RecordType.getInstance(typeRow(fieldA, types[0], fieldB, types[6]));	/* [A:Graph, B:[A:Graph]] */ 
		types[8]  = RecordType.getInstance(typeRow(fieldA, types[1], fieldB, types[6]));	/* [A:RDF,   B:[A:Graph]] */ 
		types[9]  = RecordType.getInstance(typeRow(fieldA, types[0], fieldB, types[0]));	/* [A:Graph, B:Graph ] */
		/* skip to 14 */
		types[14] = BagType.getInstance(types[7]);												/* {{ [A:Graph, B:[A:Graph]] }} */
		types[10] = RecordType.getInstance(typeRow(fieldA, types[14], fieldB, types[6])); 	/* [A:{{ [A:Graph, B:[A:Graph]] }}, B:[A:graph] ] */
		types[11] = BagType.getInstance(types[0]);											 	/* {{ Graph }} */ 
		types[12] = BagType.getInstance(types[6]);											 	/*  {{ [A:graph] }}  */
		/* 14 was already set */
		types[13] = BagType.getInstance(types[7]);											 	/* {{ [A:Graph, B:[A:Graph]] }} */
		types[15] = BagType.getInstance(types[9]);											 	/* {{ [A:Graph, B:Graph ] }} */
		types[16] = BagType.getInstance(types[8]);											 	/* {{ [A:RDF,   B:[A:Graph]] }} */
		
		types[17] = RecordType.getInstance(typeRow(fieldA, types[5]));						/* [A:SuperGraphOrRDF] */
		types[18] = RecordType.getInstance(typeRow(fieldA, types[4]));						/* [A:SuperAll] */
		
		
		
	}

    @Test public void testRecord2() {
    	initialize();
    	
//		
//    	assertTrue("must be instantiated", g1!=null);
//    	assertTrue("must be instantiated", g2!=null);
//    	assertTrue("must be instantiated", t3_r_AG!=null);
//    	assertTrue("must be instantiated", t4_r_AG_Bt3!=null);
//    	assertTrue("must be instantiated", t5_r_AG_Bt3!=null);
//    	assertTrue("must be instantiated", t7_r_Ab3_Bt3!=null);
//    	
//    	
//    	assertTrue("must be instantiated", t9_b_G!=null);
//    	assertTrue("must be instantiated", t10_b_t3!=null);
//    	assertTrue("must be instantiated", t11_b_t4!=null);
//    	assertTrue("must be instantiated", t12_b_t6!=null);
//    	assertTrue("must be instantiated", t13_b_t5!=null);
//    	
//    	assertTrue("graphtypes are identical objects of singleton ", g1==g2);
//    	assertTrue("test", true);
//    	
//    	assertTrue("graphtypes must always be equal", g1.equals(g1));
//    	
//    	//assertTrue("graphtypes must always be equal", g1_a.equals(g1_b));
//    	    	
//		assertTrue("Graph must not be equal to row", ! g1.equals(t3_r_AG));
//		assertTrue("Rows must be different ", ! t3_r_AG.equals(t4_r_AG_Bt3));
//		assertTrue("Row equals itself", t3_r_AG.equals(t3_r_AG));
//		assertTrue("Rows must be equal", t4_r_AG_Bt3.equals(t5_r_AG_Bt3));
//		assertTrue("Bag can not equal record", !t9_b_G.equals(t3_r_AG));
//		assertTrue("Bag equals itself", t10_b_t3.equals(t10_b_t3));
//		assertTrue("Bag can not equal graph", !t9_b_G.equals(g1));
//		assertTrue("Bags must be equal ", t11_b_t4.equals(t12_b_t6));
//		
//		assertTrue("Bags must be equal ", t11_b_t4.equals(t13_b_t5));
//		assertTrue("Records must not be equal, although keys are the same", ! t4_r_AG_Bt3.equals(t7_r_Ab3_Bt3));
//		assertTrue("Record does not equal Bag", ! t9_b_G.equals(t7_r_Ab3_Bt3));
//		assertTrue("Record does not equal Graph", ! g1.equals(t7_r_Ab3_Bt3));
//		   

    }
    
    /**
     *  check the relation between typeA at iA and typeB at iB, and see if it conforms with the specs[] table 
     */
    private void checkRelation(int iA, int iB){

		int relation = specs[iA][iB];
		
		if (relation<0)
			return;
		
		RGLType tA = types[iA];
		RGLType tB = types[iB];
		
		
		String errorMsg = "Checking: As specs["+iA+"]["+iB+"]=="+relation+", it should be so that that A="+tA+" "+relation_desc [relation]+" B="+tB;
		System.out.println(errorMsg);
		switch(relation){
		case 0: // types are identical
			assertTrue(errorMsg, tA.isSubtypeOf(tB));
			assertTrue(errorMsg, tA.acceptsAsSubtype(tB));
			break;
		case 1: // tA < tB 
			assertTrue(errorMsg, tA.isSubtypeOf(tB));
			assertTrue(errorMsg, tB.acceptsAsSubtype(tA));
			assertTrue(errorMsg, ! tA.acceptsAsSubtype(tB));
			break;
		case 2: // tA > tB
			assertTrue(errorMsg, tA.acceptsAsSubtype(tB));
			assertTrue(errorMsg, ! tA.isSubtypeOf(tB));
			break;
		case 3: // types are incompatible 
			assertTrue(errorMsg, ! tA.acceptsAsSubtype(tB));
			assertTrue(errorMsg, ! tA.isSubtypeOf(tB));
			break;
		case -1: 
			// ignore this one 
			break;
		default: 
			throw new RuntimeException("Unknown relation spec: specs["+iA+"]["+iB+"]=="+relation+" . ");
			
		}
				
		
		
    }
   
	
    @Test public void testSubtypingType() {
    	initialize();

    	for (int iA=0; iA<TOTAL_TYPES; iA++){
    		
        	for (int iB=0; iB < specs[iA].length; iB++){
        		checkRelation(iA, iB);
        	}
    	}
    }
    
}

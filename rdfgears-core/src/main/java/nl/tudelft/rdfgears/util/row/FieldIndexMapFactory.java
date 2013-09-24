package nl.tudelft.rdfgears.util.row;

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

import java.util.Collection;
public class FieldIndexMapFactory {

	/** 
	 * Create a FieldIndexMap from the given two sets of range values.
	 * They MUST be disjoint, otherwise Stuff Will Fail. 
	 */
	public static FieldIndexMap create(Collection<String> range1, Collection<String> range2){
		FieldIndexHashMap fiMap = new FieldIndexHashMap();
		for (String s : range1)
			fiMap.addFieldName(s);
		for (String s : range2)
			fiMap.addFieldName(s);
		
		return fiMap;
	}
	

	/** 
	 * Create a FieldIndexMap from the given two sets of range values.
	 * They MUST be disjoint, otherwise Stuff Will Fail. 
	 */
	public static FieldIndexMap create(Collection<String> range){
		
		// create hashmap  -- seems not much slower than fieldIndexArrayMap 
		FieldIndexHashMap fiMap = new FieldIndexHashMap();
		for(String s: range)
			fiMap.addFieldName(s);
		
		return fiMap;
	}
	

	/** 
	 * Create a FieldIndexMap from any number of of fields. Must not contain duplicates/null 
	 */
	public static FieldIndexMap create(String... fields){
		// create hashmap  -- seems not much slower than fieldIndexArrayMap 
		FieldIndexHashMap fiMap = new FieldIndexHashMap();
		for(String s: fields)
			fiMap.addFieldName(s);
		
		return fiMap;
	}
	

	/**
	 * Create FieldIndexMap with no fields (for empty row) 
	 */
	public static FieldIndexMap create(){
		return new FieldIndexHashMap();
	}
//	
//	/**
//	 * Create FieldIndexMap with 1 field 
//	 */
//	public static FieldIndexMap create(String field1){
//		FieldIndexHashMap fiMap = new FieldIndexHashMap();
//		fiMap.addFieldName(field1);
//		return fiMap;
//	}
//
//	/**
//	 * Create FieldIndexMap with 2 fields 
//	 */
//	public static  FieldIndexMap create(String field1, String field2){
//		FieldIndexHashMap fiMap = new FieldIndexHashMap();
//		fiMap.addFieldName(field1);
//		fiMap.addFieldName(field2);
//		return fiMap;
//	}
//
//	/**
//	 * Create FieldIndexMap with 3 fields 
//	 */
//	public static FieldIndexMap create(String field1, String field2, String field3){
//		FieldIndexHashMap fiMap = new FieldIndexHashMap();
//		fiMap.addFieldName(field1);
//		fiMap.addFieldName(field2);
//		fiMap.addFieldName(field3);
//		return fiMap;
//	}
//	
//	/**
//	 * Create FieldIndexMap with 4 fields 
//	 */
//	public static FieldIndexMap create(String field1, String field2, String field3, String field4){
//		FieldIndexHashMap fiMap = new FieldIndexHashMap();
//		fiMap.addFieldName(field1);
//		fiMap.addFieldName(field2);
//		fiMap.addFieldName(field3);
//		fiMap.addFieldName(field4);
//		return fiMap;
//	}
	
	
}
	

//	
//
//
//package nl.tudelft.rdfgears.util.row;
//
//import java.util.Collection;
//
//public class FieldIndexMapFactory {
//
//	/** 
//	 * Create a FieldIndexMap from the given two sets of range values.
//	 * They MUST be disjoint, otherwise Stuff Will Fail. 
//	 */
//	public static FieldIndexMap create(Collection<String> range1, Collection<String> range2){
//		String fieldAr[] = new String[range1.size() + range2.size()];
//		int i=0;
//		for (String s: range1){
//			fieldAr[i++] = s;
//		}
//		for (String s : range2){
//			fieldAr[i++] = s;
//		}
//		return new FieldIndexArrayMap(fieldAr);
//	}
//	
//
//	/** 
//	 * Create a FieldIndexMap from the given two sets of range values.
//	 * They MUST be disjoint, otherwise Stuff Will Fail. 
//	 */
//	public static FieldIndexMap create(Collection<String> range){
//		
//		// create hashmap  -- seems not much slower than fieldIndexArrayMap 
////		FieldIndexHashMap fiMap = new FieldIndexHashMap();
////		for(String field : range){
////			fiMap.addFieldName(field);
////		}
////		return fiMap;
//		
//		
////		// create arraymap 
//		String fieldAr[] = new String[range.size()];
//		int i=0;
//		for (String s: range){
//			fieldAr[i++] = s;
//		}
//		return new FieldIndexArrayMap(fieldAr);
//	}
//	
//
//	/** 
//	 * Create a FieldIndexMap from an array of fields. Must not contain duplicates/null 
//	 */
//	public static FieldIndexMap create(String[] fields){
//		// create hashmap  -- seems not much slower than fieldIndexArrayMap 
////		FieldIndexHashMap fiMap = new FieldIndexHashMap();
////		for(String field : fields){
////			fiMap.addFieldName(field);
////		}
////		return fiMap;
//		
//		// create arraymap 
//		return new FieldIndexArrayMap(fields);
//	}
//	
//
//	/**
//	 * Create FieldIndexMap with no fields (for empty row) 
//	 */
//	public static FieldIndexMap create(){
//		return new FieldIndexArrayMap(new String[0]);
//	}
//	
//	/**
//	 * Create FieldIndexMap with 1 field 
//	 */
//	public static FieldIndexMap create(String field1){
//		String[] fields = new String[1];
//		fields[0] = field1;
//		return new FieldIndexArrayMap(fields);
//	}
//
//	/**
//	 * Create FieldIndexMap with 2 fields 
//	 */
//	public static  FieldIndexMap create(String field1, String field2){
//		String[] fields = new String[2];
//		fields[0] = field1;
//		fields[1] = field2;
//		return new FieldIndexArrayMap(fields);
//
//	}
//
//	/**
//	 * Create FieldIndexMap with 3 fields 
//	 */
//	public static FieldIndexMap create(String field1, String field2, String field3){
//		String[] fields = new String[3];
//		fields[0] = field1;
//		fields[1] = field2;
//		fields[2] = field3;
//		return new FieldIndexArrayMap(fields);
//	}
//	
//	/**
//	 * Create FieldIndexMap with 4 fields 
//	 */
//	public static FieldIndexMap create(String field1, String field2, String field3, String field4){
//		String[] fields = new String[4];
//		fields[0] = field1;
//		fields[1] = field2;
//		fields[2] = field3;
//		fields[3] = field4;
//		return new FieldIndexArrayMap(fields);
//	}
//}
//	
//
//	

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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FieldIndexHashMap implements FieldIndexMap {
	private Map<String, Integer> map = new HashMap<String, Integer>();
	
	/** 
	 * get the array-index we should use for the given fieldName. 
	 * Returns -1 if the fieldName is not stored in the array we are describing. 
	 * @param fieldName
	 * @return
	 */
	public int getIndex(String fieldName){
		assert(map.get(fieldName)!=null) : "I expected the name "+fieldName+", but it's not there. Did you typecheck? If so, this is a bug.";
		Integer intObj = map.get(fieldName);
		return (intObj!=null ? intObj.intValue() : -1);
	}
	
	/**
	 * add a fieldName  
	 * @param fieldName
	 * @return The new index on which it is stored. 
	 */
	public int addFieldName(String fieldName){
		int newIndex = size();
		map.put(fieldName, new Integer(newIndex));
		return newIndex;
	}
	
	public Set<String> getFieldNameSet(){
		return map.keySet();
	}
	
	public int size(){
		return map.size();
	}
	

	/** 
	 * Give a fieldmap that contains the union of the two ranges.
	 * This could return a new FieldMap, or reuse an old one. So the called should not modify the result.  
	 * @param range1
	 * @param range2
	 * @return
	 */
	public static FieldIndexHashMap union(Set<String> range1, Set<String> range2) {
		FieldIndexHashMap fiMap = new FieldIndexHashMap();
		
		for(String name : range1){
			fiMap.addFieldName(name);	
		}
		for(String name : range2){
			if (! range1.contains(name)){
				// not yet added to fieldmap
				fiMap.addFieldName(name);	
			}
		}
		
		return fiMap;
	}
}

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.rgl.exception.NoSuchFieldInRowException;
import nl.tudelft.rdfgears.util.ArrayIterator;

public class FieldIndexArrayMap implements FieldIndexMap {
	private String[] fields; 
	private Set<String> fieldNameSet; 
	
	private void checkNotNull(){
		for (int i=0; i<fields.length; i++){
			if (fields[i]==null)
				throw new IllegalArgumentException("Cannot construct a FieldIndexArrayMap with null-field. ");
		}
	}
	
	/**
	 * instantiate fieldMap with the fields given in the array 
	 * @param fieldList
	 */
	public FieldIndexArrayMap(String[] fieldList){
		assert(fieldList!=null);
		fields = fieldList; 
		checkNotNull();
	}
	
	/**
	 * Create fieldIndexMap from a collection. The collection should not contain duplicates or null. 
	 * @param requiredInputList
	 */
	public FieldIndexArrayMap(Collection<String> fieldList) {
		assert(fieldList!=null);
		fields = new String[fieldList.size()];
		int i=0;
		for (String s : fieldList ){
			fields[i++] = s;
		}
	}
	
	/** 
	 * get the array-index we should use for the given fieldName. 
	 * Returns -1 if the fieldName is not stored in the array we are describing. 
	 * @param fieldName
	 * @return
	 */
	public int getIndex(String fieldName){
		for (int i = 0; i<fields.length; i++){
			if (fields[i].equals(fieldName))
				return i;
		}
		throw new NoSuchFieldInRowException(fieldName); // also fires if fieldName == null
	}
	
	public Set<String> getFieldNameSet(){
		if (fieldNameSet==null){
			fieldNameSet = new ArrayBasedSet(); 
			
				/*
			new HashSet<String>();
			for (int i=0 ; i<fields.length; i++){
				fieldNameSet.add(fields[i]); // fixme: should be able to use an array-based set, faster? 
				Engine.getLogger().debug("creating set in a naive way... ");
			}
			*/
		}
		return fieldNameSet; 
	}
	
	public int size(){
		return fields.length;
	}
	
	
	class ArrayBasedSet implements Set<String>{
		private HashSet<String> hashSet;
		@Override
		public int size() {
			return fields.length;
		}

		@Override
		public boolean isEmpty() {
			return size()==0;
		}

		@Override
		public boolean contains(Object o) {
			return getHashSet().contains(o);
		}

		@Override
	    @SuppressWarnings("unchecked")
		public Iterator<String> iterator() {
			return new ArrayIterator(fields);
		}

		@Override
		public Object[] toArray() {
			return getHashSet().toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return (T[]) getHashSet().toArray(a);
		}

		@Override
		public boolean add(String e) {
			throw new UnsupportedOperationException("you should not modify the fields in a FieldIndexMap");
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException("you should not modify the fields in a FieldIndexMap");
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return getHashSet().containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends String> c) {
			throw new UnsupportedOperationException("you should not modify the fields in a FieldIndexMap");
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException("you should not modify the fields in a FieldIndexMap");
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException("you should not modify the fields in a FieldIndexMap");
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException("you should not modify the fields in a FieldIndexMap");
		}
		
		private HashSet<String> getHashSet(){
			if (hashSet==null){
				hashSet = new HashSet<String>();
				for (String s : fields){
					hashSet.add(s);
				}
				Engine.getLogger().debug("creating set in a naive way... ");
			}
			return hashSet;
		}
	}
}

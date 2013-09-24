package nl.tudelft.rdfgears.engine.diskvalues;

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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import nl.tudelft.rdfgears.engine.bindings.RGLListBinding;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;

/**
 * DiskList is an implementation of List that can store its contents on HDD
 * using BerkeleyDB Base API as a back-end.
 * 
 * @author Tomasz Traczyk
 * 
 * @param <RGLValue>
 */
public class DiskList implements List<RGLValue> {

	/**
	 * DiskListIterator is an implementation of Iterator for DiskList. Each
	 * instance of this class have its own cache of size defined in its DiskList
	 * instance.
	 * 
	 * It can also use its DiskList caches, but won't modify them.
	 * 
	 * DiskListIterator will fetch any requested elements from DB if they can't
	 * be found in any of caches.
	 * 
	 * @author tomek
	 * 
	 */
	private class DiskListIterator implements Iterator<RGLValue> {
		private int nextIndexPointer;
		private List<RGLValue> iteratorCache = new ArrayList<RGLValue>();
		private int iteratorOffset;

		public DiskListIterator() {
			nextIndexPointer = 0;
			iteratorOffset = -cacheSize;
		}

		@Override
		public boolean hasNext() {
			if (nextIndexPointer >= size)
				return false;
			else
				return true;
		}

		@Override
		public synchronized RGLValue next() {
			rangeCheck(nextIndexPointer);
			RGLValue ret;
			if ((ret = tryToGet(nextIndexPointer, iteratorOffset, iteratorCache)) != null)
				;
			else if ((ret = tryToGet(nextIndexPointer, addOffset, addCache)) != null)
				;
			else if ((ret = tryToGet(nextIndexPointer, getOffset, getCache)) != null)
				;
			else {
				iteratorOffset = loadCache(nextIndexPointer, iteratorOffset,
						iteratorCache);
				ret = iteratorCache.get(nextIndexPointer - iteratorOffset);
			}

			nextIndexPointer++;
			return ret;
		}

		@Override
		public void remove() {
			assert (false) : "Not implemented";
		}

	}

	private int size = 0;

	/* the maximum size of each cache */
	private int cacheSize;

	private List<RGLValue> addCache = new ArrayList<RGLValue>();
	private int addOffset = 0;

	private List<RGLValue> getCache = new ArrayList<RGLValue>();
	private int getOffset;

	private Database listDatabase;
	private TupleBinding<List<RGLValue>> dataBinding;

	/**
	 * Create a DiskList with default cache size of 1000 instances of type E.
	 * 
	 * @throws Exception
	 */
	public DiskList() {
		this(1);
	}

	/**
	 * Create a DiskList with specified cache size The constructor establishes a
	 * database for storing lists contensts and opens a connection with it. This
	 * might be postponed until needed, in a lazy manner, i.e. moved to
	 * dumpCache method, but it's not sure which approach is better for usual
	 * workflows.
	 * 
	 * @param cacheSize
	 *            cache size
	 * @throws DatabaseException
	 */
	public DiskList(int cacheSize) throws DatabaseException {
		this.cacheSize = cacheSize;
		getOffset = -cacheSize; // initially there's no getCache;
		listDatabase = DatabaseManager.openListDatabase();
		dataBinding = new RGLListBinding();
	}

	@Override
	public boolean add(RGLValue value) {
		if (addCache.size() < cacheSize)
			addCache.add(value);
		else {
			dumpCache();
			addCache.add(value);
			addOffset += cacheSize;
		}
		size++;
		return true;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean contains(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* dump the current addCache to HDD */
	private void dumpCache() {
		Stoper.diskTime -= System.currentTimeMillis();
		
		DatabaseEntry key = DatabaseManager.int2entry(addOffset);
		DatabaseEntry data = new DatabaseEntry();
		dataBinding.objectToEntry(addCache, data);
		listDatabase.put(null, key, data);
		addCache.clear();
		
		Stoper.diskTime += System.currentTimeMillis();
	}

	@Override
	public RGLValue get(int index) {
		// rangeCheck(index);
		RGLValue ret;
		if ((ret = tryToGet(index, getOffset, getCache)) != null) {
			return ret;
		} else if ((ret = tryToGet(index, addOffset, addCache)) != null) {
			return ret;
		} else {
			getOffset = loadCache(index, getOffset, getCache);
			return getCache.get(index - getOffset);
		}
	}

	public int getCacheSize() {
		return cacheSize;
	}

	/* probably shouldn't be implemented - it would be very costly */
	@Override
	public int indexOf(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return (size == 0);
	}

	@Override
	public Iterator<RGLValue> iterator() {
		return new DiskListIterator();
	}

	@Override
	public int lastIndexOf(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<RGLValue> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<RGLValue> listIterator(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	private int loadCache(int index, int offset, List<RGLValue> cache) {
		Stoper.diskTime -= System.currentTimeMillis();
		offset = index - index % cacheSize;

		DatabaseEntry key = DatabaseManager.int2entry(offset);
		DatabaseEntry data = new DatabaseEntry();

		listDatabase.get(null, key, data, LockMode.DEFAULT);
		cache.clear();
		cache.addAll((ArrayList<RGLValue>) dataBinding.entryToObject(data));
		Stoper.diskTime += System.currentTimeMillis();
		return offset;
	}

	private void rangeCheck(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException(String.format(
					"Index: %d, Size: %d", index, size));
		}
	}

	@Override
	public RGLValue remove(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RGLValue set(int arg0, RGLValue arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return size;
	}

	@Override
	public List<RGLValue> subList(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* check if desired index is in a cache */
	private RGLValue tryToGet(int index, int offset, List<RGLValue> cache) {
		if (index >= offset && index < offset + cacheSize) {
			return cache.get(index - offset);
		} else
			return null;
	}

	@Override
	public void add(int arg0, RGLValue arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean addAll(Collection<? extends RGLValue> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends RGLValue> arg1) {
		// TODO Auto-generated method stub
		return false;
	}

}

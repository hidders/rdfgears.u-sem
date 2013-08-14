package nl.tudelft.rdfgears.rgl.datamodel.value.impl.bags;

import java.util.Iterator;
import java.util.List;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;

/**
 * A MemoryBag value supports adding elements, for during the construction. 
 * 
 * @author Eric Feliksik
 *
 */
public class ListBackedBagValue extends BagValue {
	protected List<RGLValue> backingList;
	
	public ListBackedBagValue(long id, List<RGLValue> list) {
		this(list);
		myId = id;
	}
	
	public ListBackedBagValue(List<RGLValue> list){
		backingList = list;
	}
	
	public ListBackedBagValue(){
		backingList = ValueFactory.createBagBackingList();
	}
	
	/**
	 * Return the List<RGLValue> Object that is backing this bag implementation. 
	 * Note that it must NOT be changed after an iterator() has been instantiated
	 * 
	 * Making private for now (not used) as it is not yet needed. 
	 * @return The backing list.
	 */
	private List<RGLValue> getBackingList(){
		return backingList;
	}
	
	/**
	 * Get an iterator over the bag; that is, an iterator over the backingList, as it is 
	 * assumed to be completely filled.  
	 */
	@Override
	public Iterator<RGLValue> iterator() {
		return backingList.iterator();
	}
	
	/**
	 * Get the size of this bag. That is, the size of the backingList, as it is 
	 * assumed to be completely filled.
	 */
	@Override
	public int size() {
		return backingList.size();
	}

	@Override
	public void prepareForMultipleReadings() {
		// nothing to do
	}
}

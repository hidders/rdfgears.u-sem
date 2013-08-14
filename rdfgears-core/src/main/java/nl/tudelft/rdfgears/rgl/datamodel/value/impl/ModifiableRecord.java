package nl.tudelft.rdfgears.rgl.datamodel.value.impl;

import java.util.Set;

import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.util.row.FieldIndexMap;
import nl.tudelft.rdfgears.util.row.FieldMappedValueRow;
import nl.tudelft.rdfgears.util.row.ValueRow;
import nl.tudelft.rdfgears.util.row.ValueRowWithPut;


/**
 * Field values are modifiable! 
 * @author Eric Feliksik
 *
 */
public class ModifiableRecord extends RecordValue implements ValueRowWithPut {
	private ValueRowWithPut row;

	/**
 	 * Create a Record with given fields. It must still be filled with the put(key,value) operation
	 * @param map
	 */
	public ModifiableRecord(FieldIndexMap map) {
		row = new FieldMappedValueRow(map);
	}

	protected ValueRow getRow(){
		return this.row;
	}
	
	@Override
	public RGLValue get(String s){
		RGLValue v = row.get(s);
		assert(v!=null) : "You fetched field '"+s+"' from a record value where the field is not set. If you're working with SPARQL OPTIONALS: sorry, this isn't supported";
		return v;
	}
	
	public Set<String> getRange(){
		return row.getRange();
	}

	public void put(String fieldName, RGLValue element) {
		row.put(fieldName, element);
	}

}

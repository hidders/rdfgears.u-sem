package nl.tudelft.rdfgears.util.row;

import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;




public class TypeRow extends HashRow<RGLType> {
	
//	/**
//	 * this TypeRow is a supertype of otherTypeRow if every field in this TypeRow exists 
//	 * in otherTypeRow, and contains a supertype of the equivalent field in the otherTypeRow. 
//	 * 
//	 * If the otherTypeRow contains more fields than required, we don't mind.  
//	 */
//	public boolean isSupertypeOf(TypeRow otherTypeRow) {
//		for (String thisField: getRange()){
//			if (! get(thisField).isSupertypeOf(otherTypeRow.get(thisField))){
//				return false;
//			}
//		}
//		/* all rows are supertype of the equivalent row in otherType. */
//		return true;
//	}
	

	/**
	 * This valueRow is a subtype of the otherRow iff all values in the other row are also present
	 * in this row, and all those rows contain subtypes  
	 * @param otherRow
	 * @return
	 */
	public boolean isSubtypeOf(TypeRow otherRow) {
		if (otherRow==null)
			throw new IllegalArgumentException("otherType cannot be null");
		
		
		for (String otherField : otherRow.getRange()){
			RGLType rglType = get(otherField);
			if (rglType==null)
				return false; // we don't have all fields of otherRow, so we are not a subtype 
			
			
			if (! rglType.isSubtypeOf(otherRow.get(otherField))){
				return false;
			}
		}
		
		/* all rows are supertype of the equivalent row in otherType. */
		return true;
		
	}

}

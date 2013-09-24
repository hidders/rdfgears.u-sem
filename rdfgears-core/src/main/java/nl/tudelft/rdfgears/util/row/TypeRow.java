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

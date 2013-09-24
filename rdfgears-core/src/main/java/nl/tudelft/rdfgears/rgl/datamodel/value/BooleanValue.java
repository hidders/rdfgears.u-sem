package nl.tudelft.rdfgears.rgl.datamodel.value;

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

import nl.tudelft.rdfgears.rgl.datamodel.value.visitors.RGLValueVisitor;
import nl.tudelft.rdfgears.rgl.exception.ComparisonNotDefinedException;

public abstract class BooleanValue extends DeterminedRGLValue {
	// extends BagValue  { // no this may be theoretically interesting in NRC but not in implementation

	@Override
	public String toString() {
		return isTrue() ? "<RGLBoolean:True>" : "<RGLBoolean:False>" ;
	}
	
	public BooleanValue asBoolean(){
		return this;
	}
	
	public boolean isBoolean(){
		return true;
	}
	
	public abstract boolean isTrue();

	public void accept(RGLValueVisitor visitor){
		visitor.visit(this);
	}
	

	public int compareTo(RGLValue v2) {
		if (v2.isBoolean()){
			int  myValue = isTrue() ? 1 : 0;
			int  hisValue = v2.asBoolean().isTrue() ? 1 : 0;
			return myValue - hisValue; 
		} else if (v2.isNull()){
			return 1; // boolean is bigger than null
		}
		
		throw new ComparisonNotDefinedException(this, v2);
	}
}

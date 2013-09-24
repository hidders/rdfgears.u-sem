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

import java.util.Iterator;
import java.util.Set;


public abstract class AbstractRow<E> implements Row<E>{

	@Override 
	public boolean equals(Object object){
		if (this==object){
			return true;
		}
		if (object instanceof Row){
			Row<?> row = (Row<?>) object;
			Set<String> thisRange = this.getRange();
			Set<String> rowRange = row.getRange();
			
			if (! thisRange.equals(rowRange)){
				return false;
			}
			
			/* check whether all elements are equal */
			Iterator<String> thisIter = thisRange.iterator();
			while (thisIter.hasNext()){
				String name = thisIter.next();
				if (! this.get(name).equals(row.get(name))){
					return false;
				}
			}
			return true; /* all elements are equal */
		}
		return false;
	}
}

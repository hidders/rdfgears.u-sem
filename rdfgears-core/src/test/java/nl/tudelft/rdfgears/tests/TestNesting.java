package nl.tudelft.rdfgears.tests;

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
import java.util.Iterator;
import java.util.List;

import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.MemoryLiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.bags.ListBackedBagValue;

import org.junit.Test;

import com.hp.hpl.jena.datatypes.xsd.impl.XSDDouble;

public class TestNesting {
	@Test
	public void nestedBags() {
		List<RGLValue> outerList = new ArrayList<RGLValue>();
		
		int outerSize, nestedSize;
		
		outerSize = 100;
		nestedSize = 100;

		for (int i = 0; i <= outerSize; ++i) {

			List<RGLValue> nestedList = new ArrayList<RGLValue>();

			for (double d = 0.0; d <= nestedSize; ++d) {
				nestedList.add(MemoryLiteralValue.createLiteralTyped(d,
						new XSDDouble("double")));
			}

			outerList.add(new ListBackedBagValue(nestedList));
		}
		BagValue outerBag = new ListBackedBagValue(outerList);
		
		Iterator<RGLValue> outerIt = outerBag.iterator();
		while (outerIt.hasNext()) {
			Iterator<RGLValue> nestedIt = outerIt.next().asBag().iterator();
			while (nestedIt.hasNext()) {
				System.out.println("\t" + nestedIt.next());
			}
		}
		
		// (new ValueSerializer()).serialize(outerBag);
		System.out.println(outerBag);
		
	}
}
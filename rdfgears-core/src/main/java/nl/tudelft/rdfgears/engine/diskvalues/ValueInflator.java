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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.RGLFunction;

/**
 * @author Tomasz Traczyk
 *
 */
/**
 * @author Tomasz Traczyk
 *
 */
public class ValueInflator {
	private static Map<Long, RGLValue> valuesMap = new HashMap<Long, RGLValue>();
	private static List<RGLFunction> functionsList = new ArrayList<RGLFunction>();
	private static Map<RGLFunction, Integer> functionsMap = new HashMap<RGLFunction, Integer>();
	private static Map<Long, Long> referencesMap = new HashMap<Long, Long>();
	private static Set<Long> complexSet = new HashSet<Long>();

	public static void registerValue(RGLValue value) {
		long id = value.getId();
		valuesMap.put(id, value);
		referencesMap.put(id, referencesMap.get(id) == null ? 1
				: (referencesMap.get(id) + 1));
	}

	public static int registerFunction(RGLFunction function) {
		Integer id = functionsMap.get(function);
		if (id == null) {
			functionsList.add(function);
			id = functionsList.size() - 1;
		}
		return id;
	}

	public static RGLFunction getFunction(int id) {
		return functionsList.get(id);
	}

	public static RGLValue getValue(long id) {
		/*
		 * Long references = referencesMap.get(id); if (references != null &&
		 * references > 1) { referencesMap.put(id, references - 1); return
		 * valuesMap.get(id); } else { referencesMap.remove(id); return
		 * valuesMap.remove(id); }
		 */
		return valuesMap.get(id);
	}

	/**
	 * @param id
	 *            the id of the complex object to be registered
	 * @return true if the object have not been registered earlier (thus it must
	 *         be serialized), false - otherwise
	 */
	public static boolean registerComplex(Long id) {
		if (complexSet.contains(id))
			return false;
		else {
			complexSet.add(id);
			return true;
		}

	}
	
	
	/**
	 * @return the number of coplex elements serialized
	 */
	public static int getCount() {
		return complexSet.size();
	}
}

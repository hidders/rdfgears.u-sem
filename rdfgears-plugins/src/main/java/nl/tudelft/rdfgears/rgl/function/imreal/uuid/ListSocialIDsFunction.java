package nl.tudelft.rdfgears.rgl.function.imreal.uuid;

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

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.GraphType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.rgl.function.imreal.UserProfileGenerator;
import nl.tudelft.rdfgears.util.row.ValueRow;

/**
 * A function that provides a list of web IDs for a given UUID
 * 
 */
public class ListSocialIDsFunction extends SimplyTypedRGLFunction {

	/**
	 * The name of the input field providing the uuid
	 */
	public static final String INPUT_UUID = "uuid";

	public ListSocialIDsFunction() {
		this.requireInputType(INPUT_UUID, RDFType.getInstance());
	}

	@Override
	public RGLType getOutputType() {
		return GraphType.getInstance();
	}

	@Override
	public RGLValue simpleExecute(ValueRow inputRow) {
		// typechecking the input
		RGLValue rdfValue = inputRow.get(INPUT_UUID);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		// extracting the twitter username from the input
		String uuid = rdfValue.asLiteral().getValueString();

		try {
			return UserProfileGenerator.constructSocialIDListProfile(uuid,
					UUIDDBUtils.retrieve(uuid));
		} catch (Exception e) {
			e.printStackTrace();
			return ValueFactory.createNull("Error in "
					+ this.getClass().getCanonicalName() + ": "
					+ e.getMessage());
		}

	}
}

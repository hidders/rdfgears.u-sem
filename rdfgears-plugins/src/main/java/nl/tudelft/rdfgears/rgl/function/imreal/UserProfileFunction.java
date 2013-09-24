package nl.tudelft.rdfgears.rgl.function.imreal;

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
import nl.tudelft.rdfgears.util.row.ValueRow;

/**
 * This is a wrapper for UserProfileGenerator, to enable the workflow-based
 * output be wrapped into the U-Sem format.
 * 
 * (has nothing to do with userprofile/UserProfile.java)
 * 
 * TODO: change terminology, refactor code
 * 
 * @author Claudia
 */
public class UserProfileFunction extends SimplyTypedRGLFunction {

	public static final String INPUT_WEIGHT = "wo:weight";
	public static final String INPUT_TOPIC = "wi:topic";
	public static final String INPUT_USERID = "rdf:about";//social ID or UUID
	
	public UserProfileFunction() {
		this.requireInputType(INPUT_USERID, RDFType.getInstance());
		this.requireInputType(INPUT_TOPIC, RDFType.getInstance());
		this.requireInputType(INPUT_WEIGHT, RDFType.getInstance());
	}

	public RGLType getOutputType() {
		return GraphType.getInstance();
	}

	@Override
	public RGLValue simpleExecute(ValueRow inputRow) {
		/*
		 * - typechecking guarantees it is an RDFType - simpleExecute guarantees
		 * it is non-null SanityCheck: we must still check whether it is URI or
		 * String, because typechecking doesn't distinguish this!
		 */
		RGLValue rdfValue = inputRow.get(INPUT_USERID);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		String userid = rdfValue.asLiteral().getValueString();

		RGLValue rdfValue2 = inputRow.get(INPUT_TOPIC);
		if (!rdfValue2.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());
		String topic = rdfValue2.asLiteral().getValueString();
		
		RGLValue rdfValue3 = inputRow.get(INPUT_WEIGHT);
		if (!rdfValue3.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());
		String weight = rdfValue3.asLiteral().getValueString();

		RGLValue userProfile = null;
		try 
		{
			userProfile = UserProfileGenerator.generateProfile(userid, topic, weight);
		} 
		catch (Exception e) 
		{
			return ValueFactory.createNull("Error in "
					+ this.getClass().getCanonicalName() + ": "
					+ e.getMessage());
		}
		return userProfile;
	}
}
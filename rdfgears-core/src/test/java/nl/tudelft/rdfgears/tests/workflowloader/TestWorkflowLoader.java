package nl.tudelft.rdfgears.tests.workflowloader;

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

	

import java.text.ParseException;

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.rgl.exception.WorkflowLoadingException;
import nl.tudelft.rdfgears.util.ValueParser;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;


public class TestWorkflowLoader {
	
	@Before 
	public void init(){
		Engine.init("./rdfgears.config");
		Engine.getConfig().configurePath("./src/test/workflows/");
	}
	
	/**
	 * Test the literal parsing mechanism 
	 * @throws WorkflowLoadingException 
	 */
	@Test 
	public void testCreateSimpleValueByParsing() throws WorkflowLoadingException{
		
		Literal plainLit;
		try {
			plainLit = ValueParser.parseSimpleRGLValue("\"apple\"").getRDFNode().asLiteral();
			assertTrue(plainLit.getLanguage().equals(""));
			assertTrue(plainLit.getDatatypeURI()==null);
	
			Literal langLit = ValueParser.parseSimpleRGLValue("\"apple\"@en").getRDFNode().asLiteral();
			assertTrue("language is '"+langLit.getLanguage()+"' but should have been 'en'", langLit.getLanguage().equals("en"));
			assertTrue(plainLit.getDatatypeURI()==null);

			Literal typedLit = ValueParser.parseSimpleRGLValue("\"1.2\"^^<"+XSDDatatype.XSDdouble.getURI()+">").getRDFNode().asLiteral();
			assertTrue(typedLit.getLanguage().equals(""));
			assertTrue(typedLit.getDatatypeURI().equals(XSDDatatype.XSDdouble.getURI()));

			assertTrue ( ValueParser.parseSimpleRGLValue("true").asBoolean().isTrue());
			assertTrue ( ! ValueParser.parseSimpleRGLValue("false").asBoolean().isTrue());
			
			assertTrue(typedLit.getLanguage().equals(""));
			assertTrue(typedLit.getDatatypeURI().equals(XSDDatatype.XSDdouble.getURI()));
		} catch (ParseException e) {
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
		
		
	}
}

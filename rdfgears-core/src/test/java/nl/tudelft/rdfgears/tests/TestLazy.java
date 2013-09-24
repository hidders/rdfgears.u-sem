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

import static org.junit.Assert.assertTrue;
import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.engine.WorkflowLoader;
import nl.tudelft.rdfgears.rgl.datamodel.type.GraphType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.exception.WorkflowCheckingException;
import nl.tudelft.rdfgears.rgl.exception.WorkflowLoadingException;
import nl.tudelft.rdfgears.rgl.workflow.Workflow;
import nl.tudelft.rdfgears.util.row.SingleElementValueRow;
import nl.tudelft.rdfgears.util.row.TypeRow;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * Test created when a lazy evaluation bug was detected. Somewhere, laziness affected records.
 * @author Eric Feliksik
 *
 */
public class TestLazy {
	
	@Before 
	public void init(){
		Engine.init("./rdfgears.config");
		Engine.getConfig().configurePath("./src/test/workflows/");
	}
	
	@Test
	public void testFoo() throws WorkflowLoadingException {
		
		Workflow workflow = WorkflowLoader.loadWorkflow("tests/various/tests10dir");
		try {
			TypeRow tr = new TypeRow();
			tr.put("graph", GraphType.getInstance());
			Engine.typeCheck(workflow, tr);
		} catch (WorkflowCheckingException e) {
			// TODO Auto-generated catch block
			assertTrue(false);
		}
		RGLValue res = workflow.execute(
			new SingleElementValueRow("graph", Data.getGraphFromFile("./data/lmdb-10directors.xml"))
		);
		assertTrue("should be a graph", res.isGraph());

		Resource s = ResourceFactory.createResource("http://data.linkedmdb.org/resource/director/9");
		Property p = ResourceFactory.createProperty("http://data.linkedmdb.org/resource/movie/director_name");
		RDFNode o = ResourceFactory.createPlainLiteral("Max Reinhardt");
		
		assertTrue("did not correctly preserve records", res.asGraph().getModel().contains(s,p,o));
		
		
	}

}
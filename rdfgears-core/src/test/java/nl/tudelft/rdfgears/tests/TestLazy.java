package nl.tudelft.rdfgears.tests;

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
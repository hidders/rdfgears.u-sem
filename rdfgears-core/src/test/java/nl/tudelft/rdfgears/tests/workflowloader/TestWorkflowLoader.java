package nl.tudelft.rdfgears.tests.workflowloader;

	

import java.text.ParseException;

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.engine.WorkflowLoader;
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

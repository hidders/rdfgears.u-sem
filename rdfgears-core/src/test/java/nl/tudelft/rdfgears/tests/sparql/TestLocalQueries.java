package nl.tudelft.rdfgears.tests.sparql;

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

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.engine.WorkflowLoader;
import nl.tudelft.rdfgears.rgl.exception.WorkflowLoadingException;
import nl.tudelft.rdfgears.rgl.workflow.ConstantProcessor;
import nl.tudelft.rdfgears.rgl.workflow.FunctionProcessor;
import nl.tudelft.rdfgears.rgl.workflow.Workflow;
import nl.tudelft.rdfgears.tests.Data;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestLocalQueries {

	@Before 
	public void init(){
		Engine.init("./rdfgears.config");
		Engine.getConfig().configurePath("./src/test/workflows/");
	}
	
	@Test 
    public void testLocalConstruct() throws WorkflowLoadingException {
		

		
    	ConstantProcessor graphProc = new ConstantProcessor(Data.getGraphFromFile("./data/dbpedia_incomplete.xml"));
    	Workflow workflow = WorkflowLoader.loadWorkflow("tests/localConstruct");    	
    	FunctionProcessor workflowProc = new FunctionProcessor(workflow);
    	workflowProc.getPort("graph").setInputProcessor(graphProc);
    	assertTrue("should have given number of values", workflowProc.getResultValue().asGraph().getModel().size()==965);
	}

	@Test 
    public void testLocalSelect() throws WorkflowLoadingException {
		
		
    	ConstantProcessor graphProc = new ConstantProcessor(Data.getGraphFromFile("./data/dbpedia_incomplete.xml"));
    	ConstantProcessor dirProc = new ConstantProcessor(ValueFactory.createURI("http://dbpedia.org/resource/David_Lynch"));
    	
    	Workflow workflow = WorkflowLoader.loadWorkflow("tests/localSelect");    	
    	FunctionProcessor workflowProc = new FunctionProcessor(workflow);
    	workflowProc.getPort("graph").setInputProcessor(graphProc);
    	workflowProc.getPort("diruri").setInputProcessor(dirProc);
    	assertTrue("Should be director of 5 movies ", workflowProc.getResultValue().asBag().size()==5);
	}
}

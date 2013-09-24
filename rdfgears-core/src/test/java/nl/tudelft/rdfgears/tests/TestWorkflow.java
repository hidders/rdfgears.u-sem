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

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.engine.WorkflowLoader;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.visitors.ValueSerializerInformal;
import nl.tudelft.rdfgears.rgl.exception.WorkflowCheckingException;
import nl.tudelft.rdfgears.rgl.exception.WorkflowLoadingException;
import nl.tudelft.rdfgears.rgl.workflow.FunctionProcessor;
import nl.tudelft.rdfgears.rgl.workflow.Workflow;
import nl.tudelft.rdfgears.util.row.TypeRow;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestWorkflow {
	
	@Before 
	public void init(){
		Engine.init("./rdfgears.config");
		Engine.getConfig().configurePath("./src/test/workflows/");
	}

	@Test 
    public void testWorkflowLoader() throws WorkflowLoadingException {
//		nl.tudelft.rdfgears.Test.enableTestConfig();
    	System.out.println(System.getProperty("user.dir"));
//    	System.exit(-1);
	if (true) return; // jarosimilarity function is removed, so this test is broken
    	Workflow workflow = WorkflowLoader.loadWorkflow("tests/silkExOk/queries");
		//Workflow workflow = (new WorkflowLoader("silkEx/queries2")).getWorkflow();
    	/**
    	 * Check type directly on workflow
    	 */
    	try {
			RGLType outputType = workflow.getOutputType(new TypeRow());
			assertTrue("output should be a bag", outputType instanceof BagType);
		} catch (WorkflowCheckingException e) {
			
			assertTrue("Exception thrown: "+e.getMessage()+" ", false);
		}
		
		/**
		 * wrap workflow in outputprocessor and check type
		 */
		ValueSerializerInformal serializer = new ValueSerializerInformal();
		
		
		RGLValue workflowResultValue = workflow.getOutputProcessor().getResultValue();
		System.out.println("");
		System.out.println("================ workflow result silkExOk/queries: ");
		System.out.flush();
		serializer.serialize(workflowResultValue);
		
		int size = workflowResultValue.asBag().size();
		assertTrue("should have size 4, but size is "+size, size==4);
		
		FunctionProcessor fproc = new FunctionProcessor(workflow, "root");
		
		try {
			fproc.getOutputType();
		} catch (WorkflowCheckingException e) {
			assertTrue("ERROR: The workflow is not executable, as it is not well-typed: "+e.getMessage(), false);
		}
		
		RGLValue procResultValue = fproc.getResultValue();
		
		//assertTrue("values should be the same, because we are not iterating", procResultValue==workflowResultValue);

		
		System.out.println("");
		System.out.println("================ proc result: ");
		System.out.flush();
		serializer.serialize(procResultValue);
		
		size = procResultValue.asBag().size();
		assertTrue("should have size 4, but is "+size, size==4);
		assertTrue("Should be (Record[lmdb:Record[dir_name:String]])", procResultValue.asBag().iterator().next().asRecord().get("lmdb").asRecord().get("dir_name").isLiteral());		
	} 
	
	@Test 
    public void testMistypedIteration() throws WorkflowLoadingException {
		Workflow workflow = WorkflowLoader.loadWorkflow("tests/silkTypingError/queries");
		
    	/**
    	 * Check type directly on workflow
    	 */
    	try {
			@SuppressWarnings("unused")
			RGLType outputType = workflow.getOutputType(new TypeRow());
			assertTrue("should have thrown typing error", false);
		} catch (WorkflowCheckingException e) {
			/* ok */
		}
	}
    
}

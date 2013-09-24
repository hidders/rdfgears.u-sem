package nl.tudelft.rdfgears.cli;

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

import nl.tudelft.rdfgears.engine.Config;
import uk.co.flamingpenguin.jewel.cli.Option;


public interface CmdOptions {
	
	  @Option(shortName="w", longName="workflow", description="Path to the workflow to be executed")
	  String getWorkflowName();
	  boolean isWorkflowName();

	  @Option(shortName="t", longName="typecheck-only", description="Do not execute, only typecheck")
	  boolean getTypecheckOnly();


	  @Option(helpRequest = true, description="show this help message")
	  boolean getHelp();
	  
	  @Option(longName="disable-optimizer", description="Disable the workflow optimizer")
	  boolean getDisableOptimizer();
	  
	  @Option(longName="workflow-path", description="List of ':' separated paths where the \n\t\t(nested) workflows can be found. Default: "+Config.DEFAULT_WORKFLOW_PATH)
	  String getWorkflowPathList();
	  boolean isWorkflowPathList();

	  @Option(longName="outputformat", description="The format for the RGL output data [xml|informal|none]. Default: "+Config.DEFAULT_RGL_SERIALIZATION_FORMAT, defaultValue=Config.DEFAULT_RGL_SERIALIZATION_FORMAT)
	  String getOutputFormat();
	  boolean isOutputFormat();
	  
	  @Option(shortName="d", longName="debug-level", description="The log4j debug level \n\t\t(DEBUG/INFO/WARN/ERROR/OFF etc)")
  	  String getDebugLevel();
	  boolean isDebugLevel();
	  
	  @Option(longName="diskBased", description="Use disk based backend. Only recomended for really large data.")
	  boolean isDiskBased();
}

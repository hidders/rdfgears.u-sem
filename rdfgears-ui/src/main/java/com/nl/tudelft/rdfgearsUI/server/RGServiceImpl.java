package com.nl.tudelft.rdfgearsUI.server;

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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nl.tudelft.rdfgearsUI.client.RGService;

public class RGServiceImpl extends RemoteServiceServlet implements RGService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6305136928250756266L;
	private static final String RDFGEARS_CONF_FILE = "/WEB-INF/rdf-gears-ui.config";
	// private DataDriver dd = new
	// DataDriver(getServletContext().getRealPath("/"));
	// private DataDriver dd = new DataDriver(".");
	private ConfigurationDataDriver cdd = null;
	private WorkflowsDataDriver wdd;
	private ProcessorsDataDriver pdd;
	private FunctionsDataDriver fdd;

	public RGServiceImpl() {
		// /**
		// * isProductionMode = true, if the application is to be deployed
		// * isProductionMode = false, if the application is on development mode
		// */
		// boolean isProductionMode = true;
		// if(isProductionMode){
		// // try{
		// // System.out.println("get servlet context");
		// // ServletContext sc = getServletContext();
		// //// dd = new DataDriver(sc.getResourcePaths("/").toString() +
		// "/..");
		// // dd = new DataDriver(sc.getRealPath("") + "/..");
		// // }catch (Exception e){
		// // System.out.println("Servlet error: cannot get ServletContext");
		// // e.printStackTrace();
		// // }
		// }else{ //development mode
		// dd = new DataDriver(".");
		// }
	}

	public void init(ServletConfig cfg) throws ServletException {
		super.init(cfg);
		try {
			// System.out.println("get servlet context");
			ServletContext sc = getServletContext();
			System.out.println("current directory "
					+ new java.io.File(".").getCanonicalPath());
			// dd = new DataDriver(sc.getResourcePaths("/").toString() + "/..");
			cdd = new ConfigurationDataDriver(sc, RDFGEARS_CONF_FILE);
			wdd = new WorkflowsDataDriver(cdd);
			fdd = new FunctionsDataDriver(cdd);
			pdd = new ProcessorsDataDriver(cdd);

		} catch (Exception e) {
			System.out.println("Servlet error: cannot get ServletContext");
			e.printStackTrace();
		}
	}

	public String getNode(String nType) {
		if (cdd == null) {
			return "servlet constructor failed";
		}

		if (nType.startsWith("function:")) {
			return fdd.getFunctionFile(nType.substring(9));
		} else if (nType.startsWith("workflow:")) {
			return wdd.getWorkflowFileAsNode(nType.substring(9));
		}

		return pdd.getProcessorFromFile(nType);
	}

	public String getListItems(String source) {

		return "";
	}

	public String getConfig(String confKey) {
		return cdd.getConfig(confKey);
	}

	public String getOperatorList() {
		return pdd.getOperatorDirContent();
	}

	public String getWorkflowList() {
		// String dummyWorkflowList = "<workflows> " +
		// "<category name=\"category1\">" +
		// "<item id=\"wf1\" name=\"workflow 1\">" +
		// "<description>some description of workflow 1</description>" +
		// "</item>" +
		// "<item id=\"wf11\" name=\"Workflow 1.1\"/>" +
		// "</category>" +
		// "<item id=\"wf2\" name=\"workflow 2\">" +
		// "<description>some description of workflow 2 and this is supposed to be a long text and very very long text indeed</description>"
		// +
		// "</item>" +
		// "</workflows>";
		// return dummyWorkflowList;
		return wdd.getWorkflowDirContent();
	}

	public String getWorkflowById(String wfId) {
		// String wf =
		// "<rdfgears><metadata/><workflow><workflowInputList x=\"300\" y=\"100\"><workflowInputPort name=\"input0\"/><workflowInputPort name=\"input1qwewr\"/><workflowInputPort name=\"input2\"/></workflowInputList><network output=\"node-6\" x=\"816\" y=\"397\"><processor id=\"node-2\" x=\"475\" y=\"118\"><function type=\"bagUnion\"><config param=\"test\">bababag</config></function><inputPort name=\"bag1\" iterate=\"false\"><source workflowInputPort=\"input0\"/></inputPort><inputPort name=\"bag2\" iterate=\"false\"><source workflowInputPort=\"input1qwewr\"/></inputPort></processor><processor id=\"node-3\" x=\"428\" y=\"221\"><function type=\"Categorizer\"><config param=\"categorizer\">abc</config><config param=\"categories\">cat1;cat2;</config></function><inputPort name=\"bag\" iterate=\"false\"><source workflowInputPort=\"input2\"/></inputPort></processor><processor id=\"node-4\" x=\"639\" y=\"132\"><function type=\"function\"><config param=\"implementation\">def</config></function><inputPort name=\"opt2-data\" iterate=\"false\"><source processor=\"node-2\"/></inputPort></processor><processor id=\"node-5\" x=\"604\" y=\"253\"><function type=\"sparqlQuery\"><config param=\"bindVariables\">input0;input1;</config><config param=\"query\">some sparql query</config></function><inputPort name=\"input0\" iterate=\"false\"><source processor=\"node-3\"/></inputPort><inputPort name=\"input1\" iterate=\"false\"><source processor=\"node-4\"/></inputPort></processor><processor id=\"node-6\" x=\"597\" y=\"382\"><function type=\"RecordProject\"><config param=\"projectField\"/><config param=\"projectField2\"/></function><inputPort name=\"record\" iterate=\"false\"><source processor=\"node-5\"/></inputPort></processor></network></workflow></rdfgears>";
		// return wf;
		return wdd.getWorkflowFile(wfId);
	}

	public String doCopyWorkflowFile(String wfId, String newId, String newName,
			String newDesc, String newCat) {
		return wdd.doCopyWorkflowFile(wfId, newId, newName, newDesc, newCat);
	}

	public String saveAsNewWorkflow(String filename, String id, String content) {
		if (wdd.isWorkflowIdExist(id)) {
			return "<error>Workflow with the same ID already exist.</error>";
		}

		return wdd.saveWofkflowFile(filename, id, content);
	}

	public String saveWorkflow(String filename, String id, String content) {
		return wdd.saveWofkflowFile(filename, id, content);
	}

	public String deleteWorkflow(String wfId) {
		return wdd.deleteWorkflowFile(wfId);
	}

	public String getFunctionList() {
		return fdd.getFunctionsDirContent();
	}

	public String getFunctionNode(String fId) {
		return fdd.getFunctionFile(fId);
	}

	public String formatXml(String rawXml) {
		return DataDriverUtils.formatXml(rawXml);
	}
}

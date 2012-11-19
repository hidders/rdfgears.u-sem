package com.nl.tudelft.rdfgearsUI.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.rpc.client.RpcService;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nl.tudelft.rdfgearsUI.client.RGService;

public class RGServiceImpl extends RemoteServiceServlet implements RGService{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6305136928250756266L;
	//private DataDriver dd = new DataDriver(getServletContext().getRealPath("/"));
	//private DataDriver dd = new DataDriver(".");
	private DataDriver dd = null;
	
	public RGServiceImpl(){
//		/**
//		 * isProductionMode = true, if the application is to be deployed
//		 * isProductionMode = false, if the application is on development mode
//		 */
//		boolean isProductionMode = true;
//		if(isProductionMode){
////			try{
////				System.out.println("get servlet context");
////				ServletContext sc = getServletContext();
//////				dd = new DataDriver(sc.getResourcePaths("/").toString() + "/..");
////				dd = new DataDriver(sc.getRealPath("") + "/..");
////			}catch (Exception e){
////				System.out.println("Servlet error: cannot get ServletContext");
////				e.printStackTrace();
////			}
//		}else{ //development mode
//			dd = new DataDriver(".");
//		}
	}
	
	public void init(ServletConfig cfg) throws ServletException {
		super.init(cfg);
		try{
			System.out.println("get servlet context");
			ServletContext sc = getServletContext();
//			dd = new DataDriver(sc.getResourcePaths("/").toString() + "/..");
			dd = new DataDriver(sc.getRealPath("") + "/WEB-INF/rdf-gears-ui-config.xml");
		}catch (Exception e){
			System.out.println("Servlet error: cannot get ServletContext");
			e.printStackTrace();
		}
	}
	
	public String getNode(String nType) {
		if(dd == null){
			return "servlet constructor failed";
		}
		
		if(nType.startsWith("function:")){
			return dd.getFunctionFile(nType.substring(9));
		}else if (nType.startsWith("workflow:")){
			return dd.getWorkflowFileAsNode(nType.substring(9));
		}
		
		return dd.getProcessorFromFile(nType);
	}
	
	public String getListItems(String source){
		
		return "";
	}
	
	public String getConfig(String confKey){
		return dd.getConfig(confKey);
	}
	
	public String getOperatorList(){
		return dd.getOperatorDirContent();
	}
	public String getWorkflowList(){
//		String dummyWorkflowList = "<workflows> " +
//								   "<category name=\"category1\">" +
//								   		"<item id=\"wf1\" name=\"workflow 1\">" +
//								   			"<description>some description of workflow 1</description>" +
//								   		"</item>" +
//								   		"<item id=\"wf11\" name=\"Workflow 1.1\"/>" +
//								   "</category>" +
//								   "<item id=\"wf2\" name=\"workflow 2\">" +
//								   "<description>some description of workflow 2 and this is supposed to be a long text and very very long text indeed</description>" +
//								   "</item>" +
//								   "</workflows>";
//		return dummyWorkflowList;
		return dd.getWorkflowDirContent();
	}
	
	public String getWorkflowById(String wfId){
		//String wf = "<rdfgears><metadata/><workflow><workflowInputList x=\"300\" y=\"100\"><workflowInputPort name=\"input0\"/><workflowInputPort name=\"input1qwewr\"/><workflowInputPort name=\"input2\"/></workflowInputList><network output=\"node-6\" x=\"816\" y=\"397\"><processor id=\"node-2\" x=\"475\" y=\"118\"><function type=\"bagUnion\"><config param=\"test\">bababag</config></function><inputPort name=\"bag1\" iterate=\"false\"><source workflowInputPort=\"input0\"/></inputPort><inputPort name=\"bag2\" iterate=\"false\"><source workflowInputPort=\"input1qwewr\"/></inputPort></processor><processor id=\"node-3\" x=\"428\" y=\"221\"><function type=\"Categorizer\"><config param=\"categorizer\">abc</config><config param=\"categories\">cat1;cat2;</config></function><inputPort name=\"bag\" iterate=\"false\"><source workflowInputPort=\"input2\"/></inputPort></processor><processor id=\"node-4\" x=\"639\" y=\"132\"><function type=\"function\"><config param=\"implementation\">def</config></function><inputPort name=\"opt2-data\" iterate=\"false\"><source processor=\"node-2\"/></inputPort></processor><processor id=\"node-5\" x=\"604\" y=\"253\"><function type=\"sparqlQuery\"><config param=\"bindVariables\">input0;input1;</config><config param=\"query\">some sparql query</config></function><inputPort name=\"input0\" iterate=\"false\"><source processor=\"node-3\"/></inputPort><inputPort name=\"input1\" iterate=\"false\"><source processor=\"node-4\"/></inputPort></processor><processor id=\"node-6\" x=\"597\" y=\"382\"><function type=\"RecordProject\"><config param=\"projectField\"/><config param=\"projectField2\"/></function><inputPort name=\"record\" iterate=\"false\"><source processor=\"node-5\"/></inputPort></processor></network></workflow></rdfgears>";
		//return wf;
		return dd.getWorkflowFile(wfId);
	}
	
	public String doCopyWorkflowFile(String wfId, String newId, String newName, String newDesc, String newCat){
		return dd.doCopyWorkflowFile(wfId, newId, newName, newDesc, newCat);
	}
	public String saveAsNewWorkflow(String filename, String id, String content){
		if(dd.isWorkflowIdExist(id)){
			return "<error>Workflow with the same ID already exist.</error>";
		}
		
		return dd.saveWofkflowFile(filename, id,content);
	}
	
	public String saveWorkflow(String filename, String id, String content){
		return dd.saveWofkflowFile(filename, id,content);
	}
	public String deleteWorkflow(String wfId){
		return dd.deleteWorkflowFile(wfId);
	}

	public String getFunctionList() {
		return dd.getFunctionsDirContent();
	}
	
	public String getFunctionNode(String fId){
		return dd.getFunctionFile(fId);
	}
	
	public String formatXml(String rawXml){
		return dd.formatXml(rawXml);
	}
}

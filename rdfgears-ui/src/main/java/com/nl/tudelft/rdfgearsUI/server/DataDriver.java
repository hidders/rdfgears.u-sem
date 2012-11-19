package com.nl.tudelft.rdfgearsUI.server;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataDriver {
	private String basePath = ".";
	private File dataDir;
	private Map <String, String> configs = new HashMap <String, String>();
	
	public DataDriver(String configFilePath){
		//this.basePath = bp;
		//dataDir = new File(basePath + "/data");
		System.out.println("configFilePath basepath:" + configFilePath);
		if(!readConfigFile(configFilePath)){
			System.out.println("Cannot read config file or it contain error.. fix it dude..!!");
		}
	}
	public boolean readConfigFile(String path){
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		boolean r = true;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document d = dBuilder.parse(new File(path));
			
			Element confRoot = (Element) d.getElementsByTagName("RDFGearsUIConfig").item(0);
			NodeList confEls = confRoot.getChildNodes();
			for(int i = 0; i < confEls.getLength(); i++){
				if(confEls.item(i).getNodeType() == 1){
					Element c = (Element) confEls.item(i);
					configs.put(c.getTagName(), c.getTextContent());
				}
				
			}
			
		
			//basePath = d.getElementsByTagName("BasePath").item(0).getTextContent();
			
			basePath = configs.get("BasePath");
			//test the content of the data dir on base path
			File workflowDir = new File(basePath + "/data/workflows");
			if(!workflowDir.exists()){
				System.out.println("Workflow directory do not exist, " + basePath + "/data/workflows");
				r = false;
			}
			
			File processorDir = new File(basePath + "/data/processors");
			if(!processorDir.exists()){
				System.out.println("Processor directory do not exist, " + basePath + "/data/processors");
				r =  false;
			}
			
			File functionsDir = new File(basePath + "/data/functions");
			if(!functionsDir.exists()){
				System.out.println("Functions directory do not exist, " + basePath + "/data/functions");
				r =  false;
			}
			
		} catch (Exception e) {
			System.out.println("Error while parsing config file, " + path);
			e.printStackTrace();
			return false;
		}
		
		return r;
	}
	
	public String getConfig(String configKey){
		return configs.get(configKey);
	}
	public String getProcessorFromFile(String filePath){
		File p = new File(basePath + "/data/processors/" + filePath + ".xml");
		if(p.exists()){
			try {
				p = null;
				return readFileToString(basePath + "/data/processors/" + filePath + ".xml");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return "<error> processor not found !!: "+ dataDir.getAbsolutePath() + "/processors/" + filePath + ".xml" +"</error>";
	}
	
	private String readFileToString(String path) throws IOException{
		StringBuffer fileContent = new StringBuffer();
		  try {
			  BufferedReader br = new BufferedReader( new FileReader(new File(path)));
			  String s = "";
		       while((s = br.readLine()) != null){// end of the file
		    	  fileContent.append(s).append(System.getProperty("line.separator"));
		       }
		       br.close();
		  }catch (Exception e){e.printStackTrace();}
		  
		  return fileContent.toString();
	}
	
	public String getWorkflowDirContent(){
		File operatorDir = new File(basePath + "/data/workflows");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;
		Element root = null; 
		StreamResult result = new StreamResult(new StringWriter());
		ArrayList <String> categoryNames = new ArrayList <String>();
		Map <String, Element> catName2Element = new HashMap <String, Element>();
		
		try {
			if(operatorDir.isDirectory()){
				File operators[] = operatorDir.listFiles();
				
				dBuilder = dbFactory.newDocumentBuilder();
				doc = dBuilder.newDocument();
				root = doc.createElement("workflows");
				doc.appendChild(root);
				for(File op: operators){
					if(op.isDirectory()){
						//root.appendChild(readAllOperatorXmlFiles(op, doc));
					}else{
						Document d = dBuilder.parse(op);
						d.getDocumentElement().normalize();
						Element wf = (Element) d.getElementsByTagName("rdfgears").item(0);
						
						Element meta = (Element) wf.getElementsByTagName("metadata").item(0);
//						System.out.println("id size:" + meta.getElementsByTagName("id").getLength());
//						Element ide = (Element) meta.getElementsByTagName("id").item(0);
//						System.out.println("id:" + ide.getTextContent());
						String id = meta.getElementsByTagName("id").item(0).getTextContent();
						String name = meta.getElementsByTagName("name").item(0).getTextContent();
						String desc = meta.getElementsByTagName("description").item(0).getTextContent();
						
						Element newWf = doc.createElement("item");
						//newNode.setAttribute("id", FilenameUtils.removeExtension(op.getName()));
						newWf.setAttribute("id", id);
						newWf.setAttribute("name", name);
						if(desc != null){
							if(desc.length() > 0){
								Element descEl = doc.createElement("description");
								descEl.appendChild(doc.createTextNode(desc));
								newWf.appendChild(descEl);
							}
						}
						if(meta.getElementsByTagName("category").getLength() > 0){
							String cat = meta.getElementsByTagName("category").item(0).getTextContent();
							if(cat.trim().length() > 0){
								if(categoryNames.contains(cat)){
									catName2Element.get(cat).appendChild(newWf);
								}else{
									categoryNames.add(cat);
									Element catElement = doc.createElement("category");
									catElement.setAttribute("name", cat);
									catElement.appendChild(newWf);
									catName2Element.put(cat, catElement);
								}
							}else{
								root.appendChild(newWf);
							}
						}else{
							root.appendChild(newWf);
						}
					}
				}
				
				for(String cname: categoryNames){
					if(catName2Element.containsKey(cname)){
						root.appendChild(catName2Element.get(cname));
					}
				}
				
				Transformer t = TransformerFactory.newInstance().newTransformer();
				DOMSource source = new DOMSource(doc);
				t.transform(source, result);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//System.out.println(result.getWriter().toString());
		return result.getWriter().toString();
	}
	public String getOperatorDirContent(){
		File operatorDir = new File(basePath + "/data/processors");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;
		Element root = null; 
		StreamResult result = new StreamResult(new StringWriter());
		ArrayList <String> categoryNames = new ArrayList <String>();
		Map <String, Element> catName2Element = new HashMap <String, Element>();
		
		try {
			if(operatorDir.isDirectory()){
				File operators[] = operatorDir.listFiles();
				
				dBuilder = dbFactory.newDocumentBuilder();
				doc = dBuilder.newDocument();
				root = doc.createElement("operators");
				doc.appendChild(root);
				for(File op: operators){
					if(op.isDirectory()){
						//root.appendChild(readAllOperatorXmlFiles(op, doc));
					}else{
						Document d = dBuilder.parse(op);
						d.getDocumentElement().normalize();
						Element proc = (Element) d.getElementsByTagName("processor").item(0);
						Element newNode = doc.createElement("item");
						//newNode.setAttribute("id", FilenameUtils.removeExtension(op.getName()));
						String fileName = op.getName();
						newNode.setAttribute("id", fileName.substring(0, fileName.lastIndexOf(".")));
						newNode.setAttribute("name", proc.getAttribute("label"));
						if(proc.hasAttribute("category")){
							String cat = proc.getAttribute("category");
							if(cat.trim().length() > 0){
								if(categoryNames.contains(cat)){
									catName2Element.get(cat).appendChild(newNode);
								}else{
									categoryNames.add(cat);
									Element catElement = doc.createElement("category");
									catElement.setAttribute("name", cat);
									catElement.appendChild(newNode);
									catName2Element.put(cat, catElement);
								}
							}else{
								root.appendChild(newNode);
							}
						}else{
							root.appendChild(newNode);
						}
					}
				}
				
				for(String cname: categoryNames){
					if(catName2Element.containsKey(cname)){
						root.appendChild(catName2Element.get(cname));
					}
				}
				
				Transformer t = TransformerFactory.newInstance().newTransformer();
				DOMSource source = new DOMSource(doc);
				t.transform(source, result);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		return result.getWriter().toString();
	}
	
	public String getFunctionsDirContent(){
		File functionsDir = new File(basePath + "/data/functions");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc = null;
		Element root = null; 
		StreamResult result = new StreamResult(new StringWriter());
		ArrayList <String> categoryNames = new ArrayList <String>();
		Map <String, Element> catName2Element = new HashMap <String, Element>();
		
		try {
			if(functionsDir.isDirectory()){
				File operators[] = functionsDir.listFiles();
				
				dBuilder = dbFactory.newDocumentBuilder();
				doc = dBuilder.newDocument();
				root = doc.createElement("operators");
				doc.appendChild(root);
				for(File op: operators){
					if(op.isDirectory()){
						//root.appendChild(readAllOperatorXmlFiles(op, doc));
					}else{
						Document d = dBuilder.parse(op);
						d.getDocumentElement().normalize();
						Element proc = (Element) d.getElementsByTagName("processor").item(0);
						Element newNode = doc.createElement("item");
						String fId = "";
						NodeList params = proc.getElementsByTagName("param");
						for(int i = 0; i < params.getLength(); i++){
							Element prm = (Element) params.item(i);
							if(prm.hasAttribute("name")){
								if(prm.getAttribute("name").equals("implementation")){
									if(prm.hasAttribute("value")){
										fId = prm.getAttribute("value").trim();
									}
								}
							}
						}
						newNode.setAttribute("id", "function:"+fId);
						newNode.setAttribute("name", proc.getAttribute("label"));
						if(proc.hasAttribute("category")){
							String cat = proc.getAttribute("category");
							if(cat.trim().length() > 0){
								if(categoryNames.contains(cat)){
									catName2Element.get(cat).appendChild(newNode);
								}else{
									categoryNames.add(cat);
									Element catElement = doc.createElement("category");
									catElement.setAttribute("name", cat);
									catElement.appendChild(newNode);
									catName2Element.put(cat, catElement);
								}
							}else{
								root.appendChild(newNode);
							}
						}else{
							root.appendChild(newNode);
						}
					}
				}
				
				for(String cname: categoryNames){
					if(catName2Element.containsKey(cname)){
						root.appendChild(catName2Element.get(cname));
					}
				}
				
				Transformer t = TransformerFactory.newInstance().newTransformer();
				DOMSource source = new DOMSource(doc);
				t.transform(source, result);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return result.getWriter().toString();
	}
	
	public String getFunctionFile(String fId){
		fId = fId.trim();
		
		File functionsDir = new File(basePath + "/data/functions");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		
		try {
			if(functionsDir.isDirectory()){
				File operators[] = functionsDir.listFiles();
				dBuilder = dbFactory.newDocumentBuilder();
				for(File op: operators){
					if(op.isDirectory()){
						//root.appendChild(readAllOperatorXmlFiles(op, doc));
					}else{
						Document d = dBuilder.parse(op);
						d.getDocumentElement().normalize();
						
						NodeList params = d.getElementsByTagName("param");
						for(int i = 0; i < params.getLength(); i++){
							Element prm = (Element) params.item(i);
							if(prm.hasAttribute("name")){
								if(prm.getAttribute("name").equals("implementation")){
									if(prm.hasAttribute("value")){
										String fId1 = prm.getAttribute("value").trim();
										if(fId1.equals(fId)){
											return readFileToString(op.getAbsolutePath());
										}
									}
								}
							}
						}
						
					}
				}
				
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return "<error> Function's definition file cannot be found !!</error>";
	}
	
	public String getWorkflowFileAsNode(String wfId){
		String fContent = getWorkflowFile(wfId);
		if(fContent.startsWith("<error>")){
			return "<error>Workflow's file cannot be found</error>";
		}
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document wfDoc;
		Element proc, desc, inputs, func, param, output;
		StreamResult result = new StreamResult(new StringWriter());
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document wfXml = dBuilder.parse(new ByteArrayInputStream(fContent.getBytes()));
			//transform workflow to node
			Element metadata = (Element) wfXml.getElementsByTagName("metadata").item(0);
			String id = metadata.getElementsByTagName("id").item(0).getTextContent();
			String name = metadata.getElementsByTagName("name").item(0).getTextContent();
			String des = metadata.getElementsByTagName("description").item(0).getTextContent();
			wfDoc = dBuilder.newDocument();
			
			des = des.replace("&lt;", "<");
			des = des.replace("&gt;", ">");
			des = des.replace("&amp;", "&");
			
			proc = wfDoc.createElement("processor");
			proc.setAttribute("label", name);
			desc = wfDoc.createElement("description");
			if(des != null)
			desc.appendChild(wfDoc.createTextNode(des));
			
			proc.appendChild(desc);
			
			inputs = wfDoc.createElement("inputs");
			func = wfDoc.createElement("function");
			func.setAttribute("type", "custom-java");
			param = wfDoc.createElement("param");
			param.setAttribute("name", "implementation");
			param.setAttribute("value", "workflow:"+ id);
			func.appendChild(param);
			inputs.appendChild(func);
			
			//parse the input port
			Element wfInputList = (Element) wfXml.getElementsByTagName("workflowInputList").item(0);
			NodeList inputPorts = wfInputList.getElementsByTagName("workflowInputPort");
			for(int i = 0; i < inputPorts.getLength(); i++){
				Element inputP = (Element) inputPorts.item(i);
				String inName = inputP.getAttribute("name");
				
				Element data = wfDoc.createElement("data");
				data.setAttribute("iterate", "false");
				data.setAttribute("name", inName);
				data.setAttribute("label", inName);
				if(inputP.getElementsByTagName("type").getLength() > 0){
					Element t = (Element) wfDoc.importNode(inputP.getElementsByTagName("type").item(0), true);
					data.appendChild(t);
				}
				inputs.appendChild(data);
			}
			proc.appendChild(inputs);
			
			Element network = (Element) wfXml.getElementsByTagName("network").item(0);
		
			if(network.hasAttribute("output")){
				output = wfDoc.createElement("output");
				if(wfXml.getElementsByTagName("output-type").getLength() > 0){
					Element wOutput = (Element) wfDoc.importNode(wfXml.getElementsByTagName("output-type").item(0), true);
					wfDoc.renameNode(wOutput, null, "type");
					output.appendChild(wOutput);
				}
				
				proc.appendChild(output);
			}
			
			wfDoc.appendChild(proc);
			Transformer t = TransformerFactory.newInstance().newTransformer();
			DOMSource source = new DOMSource(wfDoc);
			t.transform(source, result);
			
		} catch (Exception e) {
			e.printStackTrace();
			return "<error>Workflow's file has an invalid format</error>";
		}
//		System.out.println(result.getWriter().toString());
		return result.getWriter().toString();
	}
	
	public String getWorkflowFile(String wfId){
		wfId = wfId.trim();
		
		File functionsDir = new File(basePath + "/data/workflows");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
//		System.out.println("Looking for wf with id:" + wfId);
		try {
			if(functionsDir.isDirectory()){
				File operators[] = functionsDir.listFiles();
				
					dBuilder = dbFactory.newDocumentBuilder();
				
				for(File op: operators){
					if(op.isDirectory()){
						//root.appendChild(readAllOperatorXmlFiles(op, doc));
					}else{
						Document d = dBuilder.parse(op);
						d.getDocumentElement().normalize();
						
						Element meta = (Element) d.getElementsByTagName("metadata").item(0);
						if(meta != null){
							if(meta.hasChildNodes()){
								if(meta.getElementsByTagName("id").getLength() > 0){
									Element id = (Element) meta.getElementsByTagName("id").item(0);
									if(id.hasChildNodes()){
										String ids = id.getTextContent().trim();
										if(wfId.equals(ids)){
											return readFileToString(op.getAbsolutePath());
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return "<error>Workflow's file cannot be found</error>";
	}
	
	public String doCopyWorkflowFile(String wfId, String newId, String newName, String newDesc, String newCat){
		if(isWorkflowIdExist(newId)){
			return "<error> Workflow with the same Id already exist </error>";
		}else{
			String currentWfContent = getWorkflowFile(wfId);
			if(currentWfContent.startsWith("<error>")){
				return "<error>Workflow cannot be found </error>";
			}else{
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder;
				
				StreamResult result = new StreamResult(new StringWriter());
				try {
					dBuilder = dbFactory.newDocumentBuilder();
					Document wfXml = dBuilder.parse(new ByteArrayInputStream(currentWfContent.getBytes()));
					
					Element newIdEl = wfXml.createElement("id");
					newIdEl.appendChild(wfXml.createTextNode(newId));
					Element newNameEl = wfXml.createElement("name");
					newNameEl.appendChild(wfXml.createTextNode(newName));
					Element newDescEl = wfXml.createElement("description");
					if(!newDesc.isEmpty()) 
						newDescEl.appendChild(wfXml.createTextNode(newDesc));
					
					Element newCatEl = wfXml.createElement("category");
					if(!newCat.isEmpty())
						newCatEl.appendChild(wfXml.createTextNode(newCat));
					
					//transform workflow to node
					Element metadata = (Element) wfXml.getElementsByTagName("metadata").item(0);
					metadata.replaceChild(newIdEl, metadata.getElementsByTagName("id").item(0));
					metadata.replaceChild(newNameEl, metadata.getElementsByTagName("name").item(0));
					metadata.replaceChild(newDescEl, metadata.getElementsByTagName("description").item(0));
					metadata.replaceChild(newCatEl, metadata.getElementsByTagName("category").item(0));
					
					Transformer t = TransformerFactory.newInstance().newTransformer();
					DOMSource source = new DOMSource(wfXml);
					t.transform(source, result);
					saveWofkflowFile(newId, newId, result.getWriter().toString());
				}catch (Exception e){
					e.printStackTrace();
					return "<error>Workflow cannot be found </error>";
				}
			}
		}
		return "<success>Workflow successfully copied</success>";
	}
	public boolean isWorkflowIdExist(String wfId){
		wfId = wfId.trim();
		
		File functionsDir = new File(basePath + "/data/workflows");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			if(functionsDir.isDirectory()){
				File operators[] = functionsDir.listFiles();
				
					dBuilder = dbFactory.newDocumentBuilder();
				
				for(File op: operators){
					if(op.isDirectory()){
						//root.appendChild(readAllOperatorXmlFiles(op, doc));
					}else{
						Document d = dBuilder.parse(op);
						d.getDocumentElement().normalize();
						
						Element meta = (Element) d.getElementsByTagName("metadata").item(0);
						if(meta != null){
							if(meta.hasChildNodes()){
								if(meta.getElementsByTagName("id").getLength() > 0){
									Element id = (Element) meta.getElementsByTagName("id").item(0);
									if(id.hasChildNodes()){
										String ids = id.getTextContent().trim();
//										System.out.println("WFID:" + wfId + " ID:" + ids);
										if(wfId.equals(ids)){
											return true;
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return false;
	}
	
	public String saveWofkflowFile(String filename, String wfId, String fileContent){
		try {
			FileWriter fw = new FileWriter(basePath + "/data/workflows/" + filename + ".xml");
			BufferedWriter out = new BufferedWriter(fw);
			out.write(formatXml(fileContent));
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "<error>File with name \""+filename+"\" cannot be created</error>";
		}		
		return "<success>Workflow file successfully saved</success>";
	}
	public String deleteWorkflowFile(String wfId){
		File f = new File(basePath + "/data/workflows/" + wfId + ".xml");
		try{
			if(f.exists()){
				f.delete();
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return "<success> file deleted </success>";
	}
	public String formatXml(String rawXml){
		TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer serializer;
        StreamResult xmlOutput = new StreamResult(new StringWriter());
        //System.out.println("rawXml: " + rawXml);
        try {
            serializer = tfactory.newTransformer();
            //Setup indenting to "pretty print"
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            Source XMLSource = new StreamSource(new StringReader(rawXml));
            serializer.transform(XMLSource, xmlOutput);
        } catch (TransformerException e) {
            // this is fatal, just dump the stack and throw a runtime exception
            e.printStackTrace();
            
            throw new RuntimeException(e);
        }
        
        return xmlOutput.getWriter().toString();
	}
}

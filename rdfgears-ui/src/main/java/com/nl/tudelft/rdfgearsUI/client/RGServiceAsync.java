package com.nl.tudelft.rdfgearsUI.client;

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


import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RGServiceAsync {
	void getNode(String nType, AsyncCallback<String> async);
	void getListItems(String source, AsyncCallback<String> async);
	void getOperatorList(AsyncCallback<String> async);
	void getFunctionList(AsyncCallback<String> async);
	void getWorkflowList(AsyncCallback<String> async);
	void getWorkflowById(String wfId, AsyncCallback<String> async);
	void saveWorkflow(String filename, String name, String content, AsyncCallback<String> async);
	void saveAsNewWorkflow(String filename, String name, String content, AsyncCallback<String> async);
	void formatXml(String rawXml, AsyncCallback<String> async);
	void doCopyWorkflowFile(String wfId, String newId, String newName, String newDesc, String newCat, AsyncCallback<String> async);
	void deleteWorkflow(String wfId, AsyncCallback<String> async);
	void getConfig(String confKey, AsyncCallback<String> async);
}

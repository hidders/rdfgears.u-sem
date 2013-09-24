package nl.rdfgears.tudelft.webservice;

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

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MyRequestDispatcher
 */
public class UserRequestDispatcher extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserRequestDispatcher() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
//    @WebServlet(name="HelloServlet1", urlPatterns={"/HelloServlet1"})
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if (pathInfo==null){
			response.getWriter().print("Not a correct request: "+request.getRequestURI());
			return;
		}
		
		String[] splitPathInfo = request.getPathInfo().split("/"); /* splitPathInfo[0]=="", as path starts with '/' */
		
		if (splitPathInfo.length==0){ // pathInfo was "/", last hit was omitted
			response.getWriter().print("You must specify an action");
			
			return;	
		}
		/* ok. */
		
//		/* get username */
//		request.setAttribute("rdfgears.username", splitPathInfo[0]);
//		
		
		
		/* get action */ 
		String action = splitPathInfo[1]; 
		request.setAttribute("rdfgears.action", action);
		
		String workflowId = "";
		for (int i=2; i<splitPathInfo.length; i++){
			workflowId += "/"+splitPathInfo[i];
		}
		request.setAttribute("rdfgears.workflowId", workflowId); 
		
		// forget the whole path, all given data is now assumed to be set as attributes. 
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/"+action); 
		if (dispatcher!=null){
			dispatcher.forward(request, response);	
		} else {
			response.getWriter().print("Action not supported: "+action);
		}
		
		
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	

}

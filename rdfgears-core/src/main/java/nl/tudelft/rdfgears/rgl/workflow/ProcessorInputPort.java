package nl.tudelft.rdfgears.rgl.workflow;

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


public class ProcessorInputPort {
	
	private FunctionProcessor proc;
	private String portName;
	public ProcessorInputPort(FunctionProcessor proc, String portName){
		this.proc = proc;
		this.portName = portName;
		
	}
	public FunctionProcessor getProcessor(){
		return proc;
	}
	public String getPortName(){
		return portName;
	}
	public boolean equals(Object object){
		if (this==object){
			return true;
		}
		if (object instanceof ProcessorInputPort){
			ProcessorInputPort port = (ProcessorInputPort ) object;
			return this.proc==port.proc && this.portName == port.portName; 
		}
		return false;
	}
}

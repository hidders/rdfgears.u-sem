package tools;

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

public class Timer {
	String msg = null;
	long startTime = 0;
	
	public Timer(){
	}
	public void start(String message){
		msg = message;
		System.out.println("Start task: "+msg);
		startTime = System.currentTimeMillis();
	}
	public void end(){
		assert(startTime!=0) : "Error: you must first call start()";
		long endTime = System.currentTimeMillis();
		
		long millis = endTime-startTime;
		
		long seconds = millis/1000;
		long rest = millis%1000;
		
		System.out.println("Finished task: "+msg+": "+seconds+"."+rest+" seconds");
		
	}
	
	public void reset(String message){
		if (message!=null){
			msg = message;
			startTime = 0;
		}
	}
}

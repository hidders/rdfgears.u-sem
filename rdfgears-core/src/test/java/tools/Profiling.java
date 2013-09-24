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

public class Profiling {

	public static double getUsedMemoryBytes(){
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}
	
	public static void printUsedMemoryString(){
		System.out.println( String.format("Memory used: %.2f GB",getUsedMemoryBytes()/(1024*1024d*1024)));		
	}
	
	public static void collectGarbage(){
		System.out.print("Will collect garbage on request, ");
		printUsedMemoryString();
		System.out.print("collecting...");
		System.gc();
		System.out.print("ok, ");
		printUsedMemoryString();
		
		
	}
	
	public static void sleep(int seconds){
		System.out.println("Sleeping for "+seconds+" seconds.");
		try {
			Thread.sleep((long) seconds*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("Sleep interrupted. ");
			e.printStackTrace();
		}
	}
	
}

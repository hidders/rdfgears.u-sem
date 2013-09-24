package nl.tudelft.rdfgears.rgl.datamodel.type;

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

public class GraphType extends RGLType {
	private static GraphType instance = new GraphType();
	private GraphType() {}

	/**
	 * singleton constructor
	 * @return
	 */
	public static synchronized GraphType getInstance(){
		if (instance == null){
			instance = new GraphType();
			assert(instance!=null);
		}

		assert(instance!=null);
		return instance;
	}

	@Override
	public boolean equals(Object that) {
		return this==that; // we can only have a singleton instance
	}

	@Override
	public boolean isType(RGLType type) {
		return this.equals(type);
	}
//
//	@Override
//	public boolean isSupertypeOf(RGLType otherType) {
//		return otherType.isGraphType(); 
//	}
	
	@Override
	public boolean acceptsAsSubtype(RGLType otherType){
		return otherType.isSubtypeOf(this);
	}
	
	public boolean isSubtypeOf(GraphType type){
		return true;
	}

	
	@Override
	public boolean isGraphType(){
		return true;
	}

	@Override
	public GraphType asGraphType(){
		return this;
	}
	

	public String toString(){
		// types are not SO deeply nested, so no need to write to pass a strinbuilder. 
		return "Graph";
	}
	
	
}

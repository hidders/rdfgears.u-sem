package com.nl.tudelft.rdfgearsUI.client.Dia;

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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.nl.tudelft.rdfgearsUI.client.RGType;

public class RGFunctionParamListItem {
	int idx;
	String value;
	String label;
	int inputDataNum;
	
	private ArrayList <String> inputIds = new ArrayList <String>();
	private Map <String, RGFunctionParamListInputData> inputIdMap = new HashMap<String, RGFunctionParamListInputData>();
	
	public RGFunctionParamListItem(int _idx, String v, String l){
		idx = _idx;
		value = v;
		label = l;
	}
	
	public void addInputData(String _name, String _label, RGType _type, boolean _iterate){
		inputIds.add(_name);
		inputIdMap.put(_name, new RGFunctionParamListInputData(_name, _label, _type, _iterate));
	}
	
	public int getInputNum(){
		return inputIds.size();
	}
	
	public RGFunctionParamListInputData getInputDataByIdx(int idx){
		return (inputIdMap.get(inputIds.get(idx)));
	}
}

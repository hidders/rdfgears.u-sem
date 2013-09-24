package nl.tudelft.rdfgears.rgl.function.obsolete;

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


import java.util.Map;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.LiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.util.row.ValueRow;

public class JaroSimilarityFunction extends SimplyTypedRGLFunction {
	public static final String s1 = "s1";
	public static final String s2 = "s2";
	
	
	public JaroSimilarityFunction(){
		this.requireInputType(s1, RDFType.getInstance());
		this.requireInputType(s2, RDFType.getInstance());
	}


	@Override
	public void initialize(Map<String, String> config) {
	}

	@Override
	public RGLType getOutputType() {
		return RDFType.getInstance();
	}
	
	@Override
	public RGLValue simpleExecute(ValueRow inputRow) {
//		if(true)
//			return new Fixed0Literal();
		LiteralValue v1 = inputRow.get(s1).asLiteral();
		LiteralValue v2 = inputRow.get(s2).asLiteral();
		
		String str1 = v1.asLiteral().getValueString();
		String str2 = v2.asLiteral().getValueString();
		double d = jaro(str1, str2);
		
		//System.out.println("Calculated jarosim, result is "+d);
		return ValueFactory.createLiteralDouble(d); 
	}


	
	/* 
	 * Copied from Silk 2.0 (Robert Isele, Anja Jentzsch, Chris Bizer), and ported to Java.  
	 */
	private static double jaro(String string1, String string2) { 
        //get half the length of the string rounded up - (this is the distance used for acceptable transpositions)
        int halflen = ((Math.min(string1.length(), string2.length())) / 2) + ((Math.min(string1.length(), string2.length())) % 2);

        //get common characters
        StringBuilder common1 = getCommonCharacters(string1, string2, halflen);
        StringBuilder common2 = getCommonCharacters(string2, string1, halflen);

        //check for zero in common
        if (common1.length() == 0 || common2.length() == 0) {
            return 0;
        }
        /*
        //check for same length common strings returning 0.0 is not the same
        if (common1.length != common2.length) {
            return 0.0
        }
        */

        //get the number of transpositions
        int transpositions = 0;

        for (int i=0; i<=Math.min(common1.length()-1, common2.length()-1); i++){
            if (common1.charAt(i) != common2.charAt(i))
            {
                transpositions++;
            }
        }

        transpositions = transpositions / 2;

        //calculate jaro metric
        return ( common1.length() / ((double) string1.length()) + 
        		 common2.length() / ((double) string2.length()) + 
        		(common1.length() - transpositions) / ((double)common1.length()) 
               ) / 3.0;
    }


    /**
     * returns a string buffer of characters from string1 within string2 if they are of a given
     * distance separation from the position in string1.
     *
     * @param string1
     * @param string2
     * @param distanceSep
     * @return a string buffer of characters from string1 within string2 if they are of a given
     *         distance separation from the position in string1
     */
    private static StringBuilder getCommonCharacters(String string1, String string2, int distanceSep){
    	StringBuilder returnCommons = new StringBuilder();
    	StringBuilder copy = new StringBuilder(string2);

        for (int i=0; i<string1.length(); i++){
        	char string1Char = string1.charAt(i);
            boolean foundIt = false; 
            int j = Math.max(0, i - distanceSep); 
            while (!foundIt && j < Math.min(i + distanceSep + 1, string2.length()))
            {
                if (copy.charAt(j) == string1Char)
                {
                    foundIt = true;
                    returnCommons.append(string1Char);
                    copy.setCharAt(j, '0');
                }
                j++;
            }
        }
        
        return returnCommons;
    }


    
    
	
}

package com.cybozu.labs.langdetect.util;

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


import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * {@link LangProfile} is a Language Profile Class.
 * Users don't use this class directly.
 * 
 * @author Nakatani Shuyo
 */
public class LangProfile {
    private static final int MINIMUM_FREQ = 2;
    private static final int LESS_FREQ_RATIO = 100000;
    public String name = null;
    public HashMap<String, Integer> freq = new HashMap<String, Integer>();
    public int[] n_words = new int[NGram.N_GRAM];

    /**
     * Constructor for JSONIC 
     */
    public LangProfile() {}

    /**
     * Normal Constructor
     * @param name language name
     */
    public LangProfile(String name) {
        this.name = name;
    }
    
    /**
     * Add n-gram to profile
     * @param gram
     */
    public void add(String gram) {
        if (name == null || gram == null) return;   // Illegal
        int len = gram.length();
        if (len < 1 || len > NGram.N_GRAM) return;  // Illegal
        ++n_words[len - 1];
        if (freq.containsKey(gram)) {
            freq.put(gram, freq.get(gram) + 1);
        } else {
            freq.put(gram, 1);
        }
    }

    /**
     * Eliminate below less frequency n-grams and noise Latin alphabets
     */
    public void omitLessFreq() {
        if (name == null) return;   // Illegal
        int threshold = n_words[0] / LESS_FREQ_RATIO;
        if (threshold < MINIMUM_FREQ) threshold = MINIMUM_FREQ;
        
        Set<String> keys = freq.keySet();
        int roman = 0;
        for(Iterator<String> i = keys.iterator(); i.hasNext(); ){
            String key = i.next();
            int count = freq.get(key);
            if (count <= threshold) {
                n_words[key.length()-1] -= count; 
                i.remove();
            } else {
                if (key.matches("^[A-Za-z]$")) {
                    roman += count;
                }
            }
        }

        // roman check
        if (roman < n_words[0] / 3) {
            Set<String> keys2 = freq.keySet();
            for(Iterator<String> i = keys2.iterator(); i.hasNext(); ){
                String key = i.next();
                if (key.matches(".*[A-Za-z].*")) {
                    n_words[key.length()-1] -= freq.get(key); 
                    i.remove();
                }
            }
            
        }
    }
}

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


/**
 * {@link TagExtractor} is a class which extracts inner texts of specified tag.
 * Users don't use this class directly.
 * @author Nakatani Shuyo
 */
public class TagExtractor {
    /* package scope */ String target_;
    /* package scope */ int threshold_;
    /* package scope */ StringBuffer buf_;
    /* package scope */ String tag_;
    private int count_;

    public TagExtractor(String tag, int threshold) {
        target_ = tag;
        threshold_ = threshold;
        count_ = 0;
        clear();
    }
    public int count() {
        return count_;
    }
    public void clear() {
        buf_ = new StringBuffer();
        tag_ = null;
    }
    public void setTag(String tag){
        tag_ = tag;
    }
    public void add(String line) {
        if (tag_ == target_ && line != null) {
            buf_.append(line);
        }
    }
    public void closeTag(LangProfile profile) {
        if (profile != null && tag_ == target_ && buf_.length() > threshold_) {
            NGram gram = new NGram();
            for(int i=0; i<buf_.length(); ++i) {
                gram.addChar(buf_.charAt(i));
                for(int n=1; n<=NGram.N_GRAM; ++n) {
                    profile.add(gram.get(n));
                }
            }
            ++count_;
        }
        clear();
    }

}

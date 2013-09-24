
package com.hp.hpl.jena.sparql.serializer;

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

import org.openjena.atlas.io.IndentedLineBuffer;
import org.openjena.atlas.io.IndentedWriter;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.util.NodeToLabelMapBNode;

/**
 * 
 * 
 * In ARQ, queries cannot easily be modified/cloned (e.g. see Query.cloneQuery(), they serializer/parse it). 
 * 
 * So this is also the way we modify the query. 
 * 
 * 
 * @author Eric Feliksik
 *
 */
public class QueryLimitRewriter extends QuerySerializer {

	/**
	 * Breaking news: these days there is documentation available for ARQ query manipulation.
	 * 
	 * See http://openjena.org/wiki/ARQ/Manipulating_SPARQL_using_ARQ on how these visitors work. 
	 */

	private long limit = Query.NOLIMIT;
	private long offset = Query.NOLIMIT;
	
	QueryLimitRewriter(IndentedWriter iwriter,
			FormatterElement formatterElement, FmtExpr formatterExpr,
			FormatterTemplate formatterTemplate) {
		super(iwriter, formatterElement, formatterExpr, formatterTemplate);	
	}
	
	public long getLimit(){ return limit; }
	public long getOffset (){ return offset; }
	
    public void visitLimit(Query query)
    {
       out.print("LIMIT   "+getLimit()) ;
       out.newline() ;
    }
    
    public void visitOffset(Query query)
    {
        out.print("OFFSET  "+getOffset()) ;
        out.newline() ;
    }
    
    /**
     * rewrite the given query with the given limit/offset 
     * @param limit
     * @param offset
     * @return
     */
    public static Query rewrite(Query query, long limit, long offset){
    	/* taken from com.hp.hpl.jena.sparql.serializer.Serializer */
		IndentedLineBuffer iwriter = new IndentedLineBuffer() ;
		
		// For the query pattern
        SerializationContext cxt1 = new SerializationContext(query, new NodeToLabelMapBNode("b", false) ) ;
        // For the construct pattern
        SerializationContext cxt2 = new SerializationContext(query, new NodeToLabelMapBNode("c", false)  ) ;
        
        QueryLimitRewriter rewriter =  new QueryLimitRewriter(iwriter, 
				new FormatterElement(iwriter, cxt1), 
				new FmtExpr(iwriter, cxt1), 
				new FmtTemplate(iwriter, cxt2));
        rewriter.setLimit(limit);
        rewriter.setOffset(offset); 
        
    	query.visit(rewriter);
    	iwriter.flush() ;
    	String queryString = iwriter.getBuffer().toString();
    	
    	return QueryFactory.create(queryString, query.getSyntax());
    }

	private void setOffset(long offset) {
		this.offset = offset;
	}

	private void setLimit(long limit) {
		this.limit = limit;
	}
    
    
    
	
	
}

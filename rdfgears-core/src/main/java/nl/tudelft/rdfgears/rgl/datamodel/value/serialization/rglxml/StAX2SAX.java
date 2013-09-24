package nl.tudelft.rdfgears.rgl.datamodel.value.serialization.rglxml;

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

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

/**
 * "Re: parsing RDF/XML sub-part with stax, using Jena?"
 * http://mail-archives.apache.org/mod_mbox/incubator-jena-users/201110.mbox/%3CE6BCE639-48EA-4D9E-A055-B9102E21D0B8@bris.ac.uk%3E
 * 
 * @author Damian Steer <d.steer@bris.ac.uk>
 */
public class StAX2SAX {
    private final ContentHandler handler;
    private final LexicalHandler lhandler;
    private final XMLInputFactory xef;
    
    public StAX2SAX(ContentHandler handler) {
        this.handler = handler;
        this.lhandler = (handler instanceof LexicalHandler) ?
                (LexicalHandler) handler :
                NO_LEXICAL_HANDLER ;
        this.xef = XMLInputFactory.newInstance(); 
        // method is deprecated, but java6 includes XMLInputFactory but does not provide the .newFactory() method 
    }
    
    // I'd be happier using XMLStreamReader, but we can only convert this way
    public void parse(XMLStreamReader xmlReader) throws XMLStreamException, SAXException {
        handler.setDocumentLocator(new LocatorConv(xmlReader));
        parse(xef.createXMLEventReader(xmlReader));
    }
    
    public void parse(XMLEventReader xmlReader) throws XMLStreamException, SAXException {
        // We permit nesting, so keep at track of where we are
        int level = 0;
        while (xmlReader.hasNext()) {
            XMLEvent e = xmlReader.nextEvent();
            if (e.isStartDocument()) handler.startDocument();
            else if (e.isEndDocument()) handler.endDocument();
            else if (e.isStartElement()) { emitSE(e.asStartElement()); level++; }
            else if (e.isEndElement()) { 
                emitEE(e.asEndElement()); 
                level--;
                if (level == 0) break;
            }
            else if (e.isProcessingInstruction()) emitPi((ProcessingInstruction) e);
            else if (e.isCharacters()) emitChars(e.asCharacters());
            else if (e.isAttribute()) emitAttr((Attribute) e);
            else if (e.isEntityReference()) emitEnt((EntityDeclaration) e);
            else if (e.isNamespace()) emitNS((Namespace) e);
            else if (e instanceof Comment) emitComment((Comment) e);
            else {
                //System.err.println("Unknown / unhandled event type " + e);
                //throw new SAXException("Unknown / unhandled event type " + e);
            }            
        }
    }

    private void emitSE(StartElement se) throws SAXException {
        handler.startElement(se.getName().getNamespaceURI(), 
                se.getName().getLocalPart(), qnameToS(se.getName()), convertAttrs(se.getAttributes()));
        Iterator<Namespace> it = se.getNamespaces();
        while (it.hasNext()) emitNS(it.next());
    }

    private void emitEE(EndElement ee) throws SAXException {
        handler.endElement(ee.getName().getNamespaceURI(), 
                ee.getName().getLocalPart(), qnameToS(ee.getName()));
        Iterator<Namespace> it = ee.getNamespaces();
        while (it.hasNext()) emitNSGone(it.next());
    }

    private void emitPi(ProcessingInstruction pi) throws SAXException {
        handler.processingInstruction(pi.getTarget(), pi.getData());
    }

    private void emitChars(Characters chars) throws SAXException {
        if (chars.isIgnorableWhiteSpace()) 
            handler.ignorableWhitespace(chars.getData().toCharArray(), 
                    0, chars.getData().length());
        else
            handler.characters(chars.getData().toCharArray(), 
                    0, chars.getData().length());
    }

    private void emitAttr(Attribute attribute) {
        // nowt to do
    }

    private void emitEnt(EntityDeclaration entityDeclaration) {
        // nowt to do
    }

    private void emitNS(Namespace namespace) throws SAXException {
        handler.startPrefixMapping(namespace.getPrefix(), namespace.getNamespaceURI());
    }
    
    private void emitNSGone(Namespace namespace) throws SAXException {
        handler.endPrefixMapping(namespace.getPrefix());
    }

    private void emitComment(Comment comment) throws SAXException {
        lhandler.comment(comment.getText().toCharArray(), 0, comment.getText().length());
    }
    
    private Attributes convertAttrs(Iterator<Attribute> attributes) {
        AttributesImpl toReturn = new AttributesImpl();
        while (attributes.hasNext()) {
            Attribute a = attributes.next();
            toReturn.addAttribute(a.getName().getNamespaceURI(), a.getName().getLocalPart(),
                    qnameToS(a.getName()), a.getDTDType(), a.getValue());
        }
        return toReturn;
    }

    private String qnameToS(QName name) {
        if (name.getPrefix().length() == 0) return name.getLocalPart();
        else return name.getPrefix() + ":" + name.getLocalPart();
    }
    
    static class LocatorConv implements Locator {
        private final XMLStreamReader reader;
        
        public LocatorConv(XMLStreamReader reader) { this.reader = reader; }

        public final String getPublicId() { return reader.getLocation().getPublicId(); }
        public final String getSystemId() { return reader.getLocation().getSystemId(); }
        public final int getLineNumber() { return reader.getLocation().getLineNumber(); }
        public final int getColumnNumber() { return reader.getLocation().getColumnNumber(); }
    }
    
    final static LexicalHandler NO_LEXICAL_HANDLER = new LexicalHandler() {
        public void startDTD(String string, String string1, String string2) throws SAXException {}
        public void endDTD() throws SAXException {}
        public void startEntity(String string) throws SAXException {}
        public void endEntity(String string) throws SAXException {}
        public void startCDATA() throws SAXException {}
        public void endCDATA() throws SAXException {}
        public void comment(char[] chars, int i, int i1) throws SAXException {}
    };
}

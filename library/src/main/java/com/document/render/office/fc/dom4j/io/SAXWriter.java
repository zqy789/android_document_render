

package com.document.render.office.fc.dom4j.io;

import com.document.render.office.fc.dom4j.Attribute;
import com.document.render.office.fc.dom4j.Branch;
import com.document.render.office.fc.dom4j.CDATA;
import com.document.render.office.fc.dom4j.CharacterData;
import com.document.render.office.fc.dom4j.Comment;
import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.DocumentType;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Entity;
import com.document.render.office.fc.dom4j.Namespace;
import com.document.render.office.fc.dom4j.Node;
import com.document.render.office.fc.dom4j.ProcessingInstruction;
import com.document.render.office.fc.dom4j.Text;
import com.document.render.office.fc.dom4j.tree.NamespaceStack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class SAXWriter implements XMLReader {
    protected static final String[] LEXICAL_HANDLER_NAMES = {
            "http://xml.org/sax/properties/lexical-handler",
            "http://xml.org/sax/handlers/LexicalHandler"};

    protected static final String FEATURE_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";

    protected static final String FEATURE_NAMESPACES = "http://xml.org/sax/features/namespaces";


    private ContentHandler contentHandler;


    private DTDHandler dtdHandler;


    private EntityResolver entityResolver;

    private ErrorHandler errorHandler;


    private LexicalHandler lexicalHandler;


    private AttributesImpl attributes = new AttributesImpl();


    private Map features = new HashMap();


    private Map properties = new HashMap();


    private boolean declareNamespaceAttributes;

    public SAXWriter() {
        properties.put(FEATURE_NAMESPACE_PREFIXES, Boolean.FALSE);
        properties.put(FEATURE_NAMESPACE_PREFIXES, Boolean.TRUE);
    }

    public SAXWriter(ContentHandler contentHandler) {
        this();
        this.contentHandler = contentHandler;
    }

    public SAXWriter(ContentHandler contentHandler, LexicalHandler lexicalHandler) {
        this();
        this.contentHandler = contentHandler;
        this.lexicalHandler = lexicalHandler;
    }

    public SAXWriter(ContentHandler contentHandler, LexicalHandler lexicalHandler,
                     EntityResolver entityResolver) {
        this();
        this.contentHandler = contentHandler;
        this.lexicalHandler = lexicalHandler;
        this.entityResolver = entityResolver;
    }


    public void write(Node node) throws SAXException {
        int nodeType = node.getNodeType();

        switch (nodeType) {
            case Node.ELEMENT_NODE:
                write((Element) node);

                break;

            case Node.ATTRIBUTE_NODE:
                write((Attribute) node);

                break;

            case Node.TEXT_NODE:
                write(node.getText());

                break;

            case Node.CDATA_SECTION_NODE:
                write((CDATA) node);

                break;

            case Node.ENTITY_REFERENCE_NODE:
                write((Entity) node);

                break;

            case Node.PROCESSING_INSTRUCTION_NODE:
                write((ProcessingInstruction) node);

                break;

            case Node.COMMENT_NODE:
                write((Comment) node);

                break;

            case Node.DOCUMENT_NODE:
                write((Document) node);

                break;

            case Node.DOCUMENT_TYPE_NODE:
                write((DocumentType) node);

                break;

            case Node.NAMESPACE_NODE:



                break;

            default:
                throw new SAXException("Invalid node type: " + node);
        }
    }


    public void write(Document document) throws SAXException {
        if (document != null) {
            checkForNullHandlers();

            documentLocator(document);
            startDocument();
            entityResolver(document);
            dtdHandler(document);

            writeContent(document, new NamespaceStack());
            endDocument();
        }
    }


    public void write(Element element) throws SAXException {
        write(element, new NamespaceStack());
    }


    public void writeOpen(Element element) throws SAXException {
        startElement(element, null);
    }


    public void writeClose(Element element) throws SAXException {
        endElement(element);
    }


    public void write(String text) throws SAXException {
        if (text != null) {
            char[] chars = text.toCharArray();
            contentHandler.characters(chars, 0, chars.length);
        }
    }


    public void write(CDATA cdata) throws SAXException {
        String text = cdata.getText();

        if (lexicalHandler != null) {
            lexicalHandler.startCDATA();
            write(text);
            lexicalHandler.endCDATA();
        } else {
            write(text);
        }
    }


    public void write(Comment comment) throws SAXException {
        if (lexicalHandler != null) {
            String text = comment.getText();
            char[] chars = text.toCharArray();
            lexicalHandler.comment(chars, 0, chars.length);
        }
    }


    public void write(Entity entity) throws SAXException {
        String text = entity.getText();

        if (lexicalHandler != null) {
            String name = entity.getName();
            lexicalHandler.startEntity(name);
            write(text);
            lexicalHandler.endEntity(name);
        } else {
            write(text);
        }
    }


    public void write(ProcessingInstruction pi) throws SAXException {
        String target = pi.getTarget();
        String text = pi.getText();
        contentHandler.processingInstruction(target, text);
    }


    public boolean isDeclareNamespaceAttributes() {
        return declareNamespaceAttributes;
    }


    public void setDeclareNamespaceAttributes(boolean declareNamespaceAttrs) {
        this.declareNamespaceAttributes = declareNamespaceAttrs;
    }





    public ContentHandler getContentHandler() {
        return contentHandler;
    }


    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }


    public DTDHandler getDTDHandler() {
        return dtdHandler;
    }


    public void setDTDHandler(DTDHandler handler) {
        this.dtdHandler = handler;
    }


    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }


    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }


    public EntityResolver getEntityResolver() {
        return entityResolver;
    }


    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }


    public LexicalHandler getLexicalHandler() {
        return lexicalHandler;
    }


    public void setLexicalHandler(LexicalHandler lexicalHandler) {
        this.lexicalHandler = lexicalHandler;
    }


    public void setXMLReader(XMLReader xmlReader) {
        setContentHandler(xmlReader.getContentHandler());
        setDTDHandler(xmlReader.getDTDHandler());
        setEntityResolver(xmlReader.getEntityResolver());
        setErrorHandler(xmlReader.getErrorHandler());
    }


    public boolean getFeature(String name) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        Boolean answer = (Boolean) features.get(name);

        return (answer != null) && answer.booleanValue();
    }


    public void setFeature(String name, boolean value) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        if (FEATURE_NAMESPACE_PREFIXES.equals(name)) {
            setDeclareNamespaceAttributes(value);
        } else if (FEATURE_NAMESPACE_PREFIXES.equals(name)) {
            if (!value) {
                String msg = "Namespace feature is always supported in dom4j";
                throw new SAXNotSupportedException(msg);
            }
        }

        features.put(name, (value) ? Boolean.TRUE : Boolean.FALSE);
    }


    public void setProperty(String name, Object value) {
        for (int i = 0; i < LEXICAL_HANDLER_NAMES.length; i++) {
            if (LEXICAL_HANDLER_NAMES[i].equals(name)) {
                setLexicalHandler((LexicalHandler) value);

                return;
            }
        }

        properties.put(name, value);
    }


    public Object getProperty(String name) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        for (int i = 0; i < LEXICAL_HANDLER_NAMES.length; i++) {
            if (LEXICAL_HANDLER_NAMES[i].equals(name)) {
                return getLexicalHandler();
            }
        }

        return properties.get(name);
    }


    public void parse(String systemId) throws SAXNotSupportedException {
        throw new SAXNotSupportedException("This XMLReader can only accept"
                + " <dom4j> InputSource objects");
    }


    public void parse(InputSource input) throws SAXException {
        if (input instanceof DocumentInputSource) {
            DocumentInputSource documentInput = (DocumentInputSource) input;
            Document document = documentInput.getDocument();
            write(document);
        } else {
            throw new SAXNotSupportedException("This XMLReader can only accept "
                    + "<dom4j> InputSource objects");
        }
    }



    protected void writeContent(Branch branch, NamespaceStack namespaceStack) throws SAXException {
        for (Iterator iter = branch.nodeIterator(); iter.hasNext(); ) {
            Object object = iter.next();

            if (object instanceof Element) {
                write((Element) object, namespaceStack);
            } else if (object instanceof CharacterData) {
                if (object instanceof Text) {
                    Text text = (Text) object;
                    write(text.getText());
                } else if (object instanceof CDATA) {
                    write((CDATA) object);
                } else if (object instanceof Comment) {
                    write((Comment) object);
                } else {
                    throw new SAXException("Invalid Node in DOM4J content: " + object
                            + " of type: " + object.getClass());
                }
            } else if (object instanceof String) {
                write((String) object);
            } else if (object instanceof Entity) {
                write((Entity) object);
            } else if (object instanceof ProcessingInstruction) {
                write((ProcessingInstruction) object);
            } else if (object instanceof Namespace) {
                write((Namespace) object);
            } else {
                throw new SAXException("Invalid Node in DOM4J content: " + object);
            }
        }
    }


    protected void documentLocator(Document document) throws SAXException {
        LocatorImpl locator = new LocatorImpl();

        String publicID = null;
        String systemID = null;
        DocumentType docType = document.getDocType();

        if (docType != null) {
            publicID = docType.getPublicID();
            systemID = docType.getSystemID();
        }

        if (publicID != null) {
            locator.setPublicId(publicID);
        }

        if (systemID != null) {
            locator.setSystemId(systemID);
        }

        locator.setLineNumber(-1);
        locator.setColumnNumber(-1);

        contentHandler.setDocumentLocator(locator);
    }

    protected void entityResolver(Document document) throws SAXException {
        if (entityResolver != null) {
            DocumentType docType = document.getDocType();

            if (docType != null) {
                String publicID = docType.getPublicID();
                String systemID = docType.getSystemID();

                if ((publicID != null) || (systemID != null)) {
                    try {
                        entityResolver.resolveEntity(publicID, systemID);
                    } catch (IOException e) {
                        throw new SAXException("Could not resolve publicID: " + publicID
                                + " systemID: " + systemID, e);
                    }
                }
            }
        }
    }


    protected void dtdHandler(Document document) throws SAXException {
    }

    protected void startDocument() throws SAXException {
        contentHandler.startDocument();
    }

    protected void endDocument() throws SAXException {
        contentHandler.endDocument();
    }

    protected void write(Element element, NamespaceStack namespaceStack) throws SAXException {
        int stackSize = namespaceStack.size();
        AttributesImpl namespaceAttributes = startPrefixMapping(element, namespaceStack);
        startElement(element, namespaceAttributes);
        writeContent(element, namespaceStack);
        endElement(element);
        endPrefixMapping(namespaceStack, stackSize);
    }


    protected AttributesImpl startPrefixMapping(Element element, NamespaceStack namespaceStack)
            throws SAXException {
        AttributesImpl namespaceAttributes = null;


        Namespace elementNamespace = element.getNamespace();

        if ((elementNamespace != null) && !isIgnoreableNamespace(elementNamespace, namespaceStack)) {
            namespaceStack.push(elementNamespace);
            contentHandler.startPrefixMapping(elementNamespace.getPrefix(),
                    elementNamespace.getURI());
            namespaceAttributes = addNamespaceAttribute(namespaceAttributes, elementNamespace);
        }

        List declaredNamespaces = element.declaredNamespaces();

        for (int i = 0, size = declaredNamespaces.size(); i < size; i++) {
            Namespace namespace = (Namespace) declaredNamespaces.get(i);

            if (!isIgnoreableNamespace(namespace, namespaceStack)) {
                namespaceStack.push(namespace);
                contentHandler.startPrefixMapping(namespace.getPrefix(), namespace.getURI());
                namespaceAttributes = addNamespaceAttribute(namespaceAttributes, namespace);
            }
        }

        return namespaceAttributes;
    }


    protected void endPrefixMapping(NamespaceStack stack, int stackSize) throws SAXException {
        while (stack.size() > stackSize) {
            Namespace namespace = stack.pop();

            if (namespace != null) {
                contentHandler.endPrefixMapping(namespace.getPrefix());
            }
        }
    }

    protected void startElement(Element element, AttributesImpl namespaceAttributes)
            throws SAXException {
        contentHandler.startElement(element.getNamespaceURI(), element.getName(),
                element.getQualifiedName(), createAttributes(element, namespaceAttributes));
    }

    protected void endElement(Element element) throws SAXException {
        contentHandler.endElement(element.getNamespaceURI(), element.getName(),
                element.getQualifiedName());
    }

    protected Attributes createAttributes(Element element, Attributes namespaceAttributes)
            throws SAXException {
        attributes.clear();

        if (namespaceAttributes != null) {
            attributes.setAttributes(namespaceAttributes);
        }

        for (Iterator iter = element.attributeIterator(); iter.hasNext(); ) {
            Attribute attribute = (Attribute) iter.next();
            attributes.addAttribute(attribute.getNamespaceURI(), attribute.getName(),
                    attribute.getQualifiedName(), "CDATA", attribute.getValue());
        }

        return attributes;
    }


    protected AttributesImpl addNamespaceAttribute(AttributesImpl attrs, Namespace namespace) {
        if (declareNamespaceAttributes) {
            if (attrs == null) {
                attrs = new AttributesImpl();
            }

            String prefix = namespace.getPrefix();
            String qualifiedName = "xmlns";

            if ((prefix != null) && (prefix.length() > 0)) {
                qualifiedName = "xmlns:" + prefix;
            }

            String uri = "";
            String localName = prefix;
            String type = "CDATA";
            String value = namespace.getURI();

            attrs.addAttribute(uri, localName, qualifiedName, type, value);
        }

        return attrs;
    }


    protected boolean isIgnoreableNamespace(Namespace namespace, NamespaceStack namespaceStack) {
        if (namespace.equals(Namespace.NO_NAMESPACE) || namespace.equals(Namespace.XML_NAMESPACE)) {
            return true;
        }

        String uri = namespace.getURI();

        if ((uri == null) || (uri.length() <= 0)) {
            return true;
        }

        return namespaceStack.contains(namespace);
    }


    protected void checkForNullHandlers() {
    }
}



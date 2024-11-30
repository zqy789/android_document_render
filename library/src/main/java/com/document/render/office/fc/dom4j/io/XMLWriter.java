

package com.document.render.office.fc.dom4j.io;

import com.document.render.office.fc.dom4j.Attribute;
import com.document.render.office.fc.dom4j.CDATA;
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
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;



public class XMLWriter extends XMLFilterImpl implements LexicalHandler {
    protected static final String[] LEXICAL_HANDLER_NAMES = {
            "http://xml.org/sax/properties/lexical-handler",
            "http://xml.org/sax/handlers/LexicalHandler"};
    protected static final OutputFormat DEFAULT_FORMAT = new OutputFormat();
    private static final String PAD_TEXT = " ";

    protected int lastOutputNodeType;

    protected boolean preserve = false;

    protected Writer writer;

    private boolean resolveEntityRefs = true;

    private boolean lastElementClosed = false;

    private NamespaceStack namespaceStack = new NamespaceStack();


    private OutputFormat format;


    private boolean escapeText = true;


    private int indentLevel = 0;


    private StringBuffer buffer = new StringBuffer();


    private boolean charsAdded = false;

    private char lastChar;


    private boolean autoFlush;


    private LexicalHandler lexicalHandler;


    private boolean showCommentsInDTDs;


    private boolean inDTD;


    private Map namespacesMap;


    private int maximumAllowedCharacter;

    public XMLWriter(Writer writer) {
        this(writer, DEFAULT_FORMAT);
    }

    public XMLWriter(Writer writer, OutputFormat format) {
        this.writer = writer;
        this.format = format;
        namespaceStack.push(Namespace.NO_NAMESPACE);
    }

    public XMLWriter() {
        this.format = DEFAULT_FORMAT;
        this.writer = new BufferedWriter(new OutputStreamWriter(System.out));
        this.autoFlush = true;
        namespaceStack.push(Namespace.NO_NAMESPACE);
    }

    public XMLWriter(OutputStream out) throws UnsupportedEncodingException {
        this.format = DEFAULT_FORMAT;
        this.writer = createWriter(out, format.getEncoding());
        this.autoFlush = true;
        namespaceStack.push(Namespace.NO_NAMESPACE);
    }

    public XMLWriter(OutputStream out, OutputFormat format) throws UnsupportedEncodingException {
        this.format = format;
        this.writer = createWriter(out, format.getEncoding());
        this.autoFlush = true;
        namespaceStack.push(Namespace.NO_NAMESPACE);
    }

    public XMLWriter(OutputFormat format) throws UnsupportedEncodingException {
        this.format = format;
        this.writer = createWriter(System.out, format.getEncoding());
        this.autoFlush = true;
        namespaceStack.push(Namespace.NO_NAMESPACE);
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
        this.autoFlush = false;
    }

    public void setOutputStream(OutputStream out) throws UnsupportedEncodingException {
        this.writer = createWriter(out, format.getEncoding());
        this.autoFlush = true;
    }


    public boolean isEscapeText() {
        return escapeText;
    }


    public void setEscapeText(boolean escapeText) {
        this.escapeText = escapeText;
    }


    public void setIndentLevel(int indentLevel) {
        this.indentLevel = indentLevel;
    }


    public int getMaximumAllowedCharacter() {
        if (maximumAllowedCharacter == 0) {
            maximumAllowedCharacter = defaultMaximumAllowedCharacter();
        }

        return maximumAllowedCharacter;
    }


    public void setMaximumAllowedCharacter(int maximumAllowedCharacter) {
        this.maximumAllowedCharacter = maximumAllowedCharacter;
    }


    public void flush() throws IOException {
        writer.flush();
    }


    public void close() throws IOException {
        writer.close();
    }


    public void println() throws IOException {
        writer.write(format.getLineSeparator());
    }


    public void write(Attribute attribute) throws IOException {
        writeAttribute(attribute);

        if (autoFlush) {
            flush();
        }
    }


    public void write(Document doc) throws IOException {
        writeDeclaration();

        if (doc.getDocType() != null) {
            indent();
            writeDocType(doc.getDocType());
        }

        for (int i = 0, size = doc.nodeCount(); i < size; i++) {
            Node node = doc.node(i);
            writeNode(node);
        }

        writePrintln();

        if (autoFlush) {
            flush();
        }
    }


    public void write(Element element) throws IOException {
        writeElement(element);

        if (autoFlush) {
            flush();
        }
    }


    public void write(CDATA cdata) throws IOException {
        writeCDATA(cdata.getText());

        if (autoFlush) {
            flush();
        }
    }


    public void write(Comment comment) throws IOException {
        writeComment(comment.getText());

        if (autoFlush) {
            flush();
        }
    }


    public void write(DocumentType docType) throws IOException {
        writeDocType(docType);

        if (autoFlush) {
            flush();
        }
    }


    public void write(Entity entity) throws IOException {
        writeEntity(entity);

        if (autoFlush) {
            flush();
        }
    }


    public void write(Namespace namespace) throws IOException {
        writeNamespace(namespace);

        if (autoFlush) {
            flush();
        }
    }


    public void write(ProcessingInstruction processingInstruction) throws IOException {
        writeProcessingInstruction(processingInstruction);

        if (autoFlush) {
            flush();
        }
    }


    public void write(String text) throws IOException {
        writeString(text);

        if (autoFlush) {
            flush();
        }
    }


    public void write(Text text) throws IOException {
        writeString(text.getText());

        if (autoFlush) {
            flush();
        }
    }


    public void write(Node node) throws IOException {
        writeNode(node);

        if (autoFlush) {
            flush();
        }
    }


    public void write(Object object) throws IOException {
        if (object instanceof Node) {
            write((Node) object);
        } else if (object instanceof String) {
            write((String) object);
        } else if (object instanceof List) {
            List list = (List) object;

            for (int i = 0, size = list.size(); i < size; i++) {
                write(list.get(i));
            }
        } else if (object != null) {
            throw new IOException("Invalid object: " + object);
        }
    }


    public void writeOpen(Element element) throws IOException {
        writer.write("<");
        writer.write(element.getQualifiedName());
        writeAttributes(element);
        writer.write(">");
    }


    public void writeClose(Element element) throws IOException {
        writeClose(element.getQualifiedName());
    }



    public void parse(InputSource source) throws IOException, SAXException {
        installLexicalHandler();
        super.parse(source);
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        for (int i = 0; i < LEXICAL_HANDLER_NAMES.length; i++) {
            if (LEXICAL_HANDLER_NAMES[i].equals(name)) {
                setLexicalHandler((LexicalHandler) value);

                return;
            }
        }

        super.setProperty(name, value);
    }

    public Object getProperty(String name) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        for (int i = 0; i < LEXICAL_HANDLER_NAMES.length; i++) {
            if (LEXICAL_HANDLER_NAMES[i].equals(name)) {
                return getLexicalHandler();
            }
        }

        return super.getProperty(name);
    }

    public LexicalHandler getLexicalHandler() {
        return lexicalHandler;
    }

    public void setLexicalHandler(LexicalHandler handler) {
        if (handler == null) {
            throw new NullPointerException("Null lexical handler");
        } else {
            this.lexicalHandler = handler;
        }
    }



    public void setDocumentLocator(Locator locator) {
        super.setDocumentLocator(locator);
    }

    public void startDocument() throws SAXException {
        try {
            writeDeclaration();
            super.startDocument();
        } catch (IOException e) {
            handleException(e);
        }
    }

    public void endDocument() throws SAXException {
        super.endDocument();

        if (autoFlush) {
            try {
                flush();
            } catch (IOException e) {
            }
        }
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (namespacesMap == null) {
            namespacesMap = new HashMap();
        }

        namespacesMap.put(prefix, uri);
        super.startPrefixMapping(prefix, uri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        super.endPrefixMapping(prefix);
    }

    public void startElement(String namespaceURI, String localName, String qName,
                             Attributes attributes) throws SAXException {
        try {
            charsAdded = false;

            writePrintln();
            indent();
            writer.write("<");
            writer.write(qName);
            writeNamespaces();
            writeAttributes(attributes);
            writer.write(">");
            ++indentLevel;
            lastOutputNodeType = Node.ELEMENT_NODE;
            lastElementClosed = false;

            super.startElement(namespaceURI, localName, qName, attributes);
        } catch (IOException e) {
            handleException(e);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        try {
            charsAdded = false;
            --indentLevel;

            if (lastElementClosed) {
                writePrintln();
                indent();
            }



            boolean hadContent = true;

            if (hadContent) {
                writeClose(qName);
            } else {
                writeEmptyElementClose(qName);
            }

            lastOutputNodeType = Node.ELEMENT_NODE;
            lastElementClosed = true;

            super.endElement(namespaceURI, localName, qName);
        } catch (IOException e) {
            handleException(e);
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if ((ch == null) || (ch.length == 0) || (length <= 0)) {
            return;
        }

        try {

            String string = String.valueOf(ch, start, length);

            if (escapeText) {
                string = escapeElementEntities(string);
            }

            if (format.isTrimText()) {
                if ((lastOutputNodeType == Node.TEXT_NODE) && !charsAdded) {
                    writer.write(' ');
                } else if (charsAdded && Character.isWhitespace(lastChar)) {
                    writer.write(' ');
                } else if (lastOutputNodeType == Node.ELEMENT_NODE && format.isPadText()
                        && lastElementClosed && Character.isWhitespace(ch[0])) {
                    writer.write(PAD_TEXT);
                }

                String delim = "";
                StringTokenizer tokens = new StringTokenizer(string);

                while (tokens.hasMoreTokens()) {
                    writer.write(delim);
                    writer.write(tokens.nextToken());
                    delim = " ";
                }
            } else {
                writer.write(string);
            }

            charsAdded = true;
            lastChar = ch[(start + length) - 1];
            lastOutputNodeType = Node.TEXT_NODE;

            super.characters(ch, start, length);
        } catch (IOException e) {
            handleException(e);
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        super.ignorableWhitespace(ch, start, length);
    }

    public void processingInstruction(String target, String data) throws SAXException {
        try {
            indent();
            writer.write("<?");
            writer.write(target);
            writer.write(" ");
            writer.write(data);
            writer.write("?>");
            writePrintln();
            lastOutputNodeType = Node.PROCESSING_INSTRUCTION_NODE;

            super.processingInstruction(target, data);
        } catch (IOException e) {
            handleException(e);
        }
    }



    public void notationDecl(String name, String publicID, String systemID) throws SAXException {
        super.notationDecl(name, publicID, systemID);
    }

    public void unparsedEntityDecl(String name, String publicID, String systemID,
                                   String notationName) throws SAXException {
        super.unparsedEntityDecl(name, publicID, systemID, notationName);
    }



    public void startDTD(String name, String publicID, String systemID) throws SAXException {
        inDTD = true;

        try {
            writeDocType(name, publicID, systemID);
        } catch (IOException e) {
            handleException(e);
        }

        if (lexicalHandler != null) {
            lexicalHandler.startDTD(name, publicID, systemID);
        }
    }

    public void endDTD() throws SAXException {
        inDTD = false;

        if (lexicalHandler != null) {
            lexicalHandler.endDTD();
        }
    }

    public void startCDATA() throws SAXException {
        try {
            writer.write("<![CDATA[");
        } catch (IOException e) {
            handleException(e);
        }

        if (lexicalHandler != null) {
            lexicalHandler.startCDATA();
        }
    }

    public void endCDATA() throws SAXException {
        try {
            writer.write("]]>");
        } catch (IOException e) {
            handleException(e);
        }

        if (lexicalHandler != null) {
            lexicalHandler.endCDATA();
        }
    }

    public void startEntity(String name) throws SAXException {
        try {
            writeEntityRef(name);
        } catch (IOException e) {
            handleException(e);
        }

        if (lexicalHandler != null) {
            lexicalHandler.startEntity(name);
        }
    }

    public void endEntity(String name) throws SAXException {
        if (lexicalHandler != null) {
            lexicalHandler.endEntity(name);
        }
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        if (showCommentsInDTDs || !inDTD) {
            try {
                charsAdded = false;
                writeComment(new String(ch, start, length));
            } catch (IOException e) {
                handleException(e);
            }
        }

        if (lexicalHandler != null) {
            lexicalHandler.comment(ch, start, length);
        }
    }



    protected void writeElement(Element element) throws IOException {
        int size = element.nodeCount();
        String qualifiedName = element.getQualifiedName();

        writePrintln();
        indent();

        writer.write("<");
        writer.write(qualifiedName);

        int previouslyDeclaredNamespaces = namespaceStack.size();
        Namespace ns = element.getNamespace();

        if (isNamespaceDeclaration(ns)) {
            namespaceStack.push(ns);
            writeNamespace(ns);
        }


        boolean textOnly = true;

        for (int i = 0; i < size; i++) {
            Node node = element.node(i);

            if (node instanceof Namespace) {
                Namespace additional = (Namespace) node;

                if (isNamespaceDeclaration(additional)) {
                    namespaceStack.push(additional);
                    writeNamespace(additional);
                }
            } else if (node instanceof Element) {
                textOnly = false;
            } else if (node instanceof Comment) {
                textOnly = false;
            }
        }

        writeAttributes(element);

        lastOutputNodeType = Node.ELEMENT_NODE;

        if (size <= 0) {
            writeEmptyElementClose(qualifiedName);
        } else {
            writer.write(">");

            if (textOnly) {


                writeElementContent(element);
            } else {

                ++indentLevel;

                writeElementContent(element);

                --indentLevel;

                writePrintln();
                indent();
            }

            writer.write("</");
            writer.write(qualifiedName);
            writer.write(">");
        }


        while (namespaceStack.size() > previouslyDeclaredNamespaces) {
            namespaceStack.pop();
        }

        lastOutputNodeType = Node.ELEMENT_NODE;
    }


    protected final boolean isElementSpacePreserved(Element element) {
        final Attribute attr = (Attribute) element.attribute("space");
        boolean preserveFound = preserve;

        if (attr != null) {
            if ("xml".equals(attr.getNamespacePrefix()) && "preserve".equals(attr.getText())) {
                preserveFound = true;
            } else {
                preserveFound = false;
            }
        }

        return preserveFound;
    }


    protected void writeElementContent(Element element) throws IOException {
        boolean trim = format.isTrimText();
        boolean oldPreserve = preserve;

        if (trim) {
            preserve = isElementSpacePreserved(element);
            trim = !preserve;
        }

        if (trim) {


            Text lastTextNode = null;
            StringBuffer buff = null;
            boolean textOnly = true;

            for (int i = 0, size = element.nodeCount(); i < size; i++) {
                Node node = element.node(i);

                if (node instanceof Text) {
                    if (lastTextNode == null) {
                        lastTextNode = (Text) node;
                    } else {
                        if (buff == null) {
                            buff = new StringBuffer(lastTextNode.getText());
                        }

                        buff.append(((Text) node).getText());
                    }
                } else {
                    if (!textOnly && format.isPadText()) {


                        char firstChar = 'a';
                        if (buff != null) {
                            firstChar = buff.charAt(0);
                        } else if (lastTextNode != null) {
                            firstChar = lastTextNode.getText().charAt(0);
                        }

                        if (Character.isWhitespace(firstChar)) {
                            writer.write(PAD_TEXT);
                        }
                    }

                    if (lastTextNode != null) {
                        if (buff != null) {
                            writeString(buff.toString());
                            buff = null;
                        } else {
                            writeString(lastTextNode.getText());
                        }

                        if (format.isPadText()) {


                            char lastTextChar = 'a';
                            if (buff != null) {
                                lastTextChar = buff.charAt(buff.length() - 1);
                            } else if (lastTextNode != null) {
                                String txt = lastTextNode.getText();
                                lastTextChar = txt.charAt(txt.length() - 1);
                            }

                            if (Character.isWhitespace(lastTextChar)) {
                                writer.write(PAD_TEXT);
                            }
                        }

                        lastTextNode = null;
                    }

                    textOnly = false;
                    writeNode(node);
                }
            }

            if (lastTextNode != null) {
                if (!textOnly && format.isPadText()) {


                    char firstChar = 'a';
                    if (buff != null) {
                        firstChar = buff.charAt(0);
                    } else {
                        firstChar = lastTextNode.getText().charAt(0);
                    }

                    if (Character.isWhitespace(firstChar)) {
                        writer.write(PAD_TEXT);
                    }
                }

                if (buff != null) {
                    writeString(buff.toString());
                    buff = null;
                } else {
                    writeString(lastTextNode.getText());
                }

                lastTextNode = null;
            }
        } else {
            Node lastTextNode = null;

            for (int i = 0, size = element.nodeCount(); i < size; i++) {
                Node node = element.node(i);

                if (node instanceof Text) {
                    writeNode(node);
                    lastTextNode = node;
                } else {
                    if ((lastTextNode != null) && format.isPadText()) {


                        String txt = lastTextNode.getText();
                        char lastTextChar = txt.charAt(txt.length() - 1);

                        if (Character.isWhitespace(lastTextChar)) {
                            writer.write(PAD_TEXT);
                        }
                    }

                    writeNode(node);





                    lastTextNode = null;
                }
            }
        }

        preserve = oldPreserve;
    }

    protected void writeCDATA(String text) throws IOException {
        writer.write("<![CDATA[");

        if (text != null) {
            writer.write(text);
        }

        writer.write("]]>");

        lastOutputNodeType = Node.CDATA_SECTION_NODE;
    }

    protected void writeDocType(DocumentType docType) throws IOException {
        if (docType != null) {
            docType.write(writer);
            writePrintln();
        }
    }

    protected void writeNamespace(Namespace namespace) throws IOException {
        if (namespace != null) {
            writeNamespace(namespace.getPrefix(), namespace.getURI());
        }
    }


    protected void writeNamespaces() throws IOException {
        if (namespacesMap != null) {
            for (Iterator iter = namespacesMap.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();
                String prefix = (String) entry.getKey();
                String uri = (String) entry.getValue();
                writeNamespace(prefix, uri);
            }

            namespacesMap = null;
        }
    }


    protected void writeNamespace(String prefix, String uri) throws IOException {
        if ((prefix != null) && (prefix.length() > 0)) {
            writer.write(" xmlns:");
            writer.write(prefix);
            writer.write("=\"");
        } else {
            writer.write(" xmlns=\"");
        }

        writer.write(uri);
        writer.write("\"");
    }

    protected void writeProcessingInstruction(ProcessingInstruction pi) throws IOException {

        writer.write("<?");
        writer.write(pi.getName());
        writer.write(" ");
        writer.write(pi.getText());
        writer.write("?>");
        writePrintln();

        lastOutputNodeType = Node.PROCESSING_INSTRUCTION_NODE;
    }

    protected void writeString(String text) throws IOException {
        if ((text != null) && (text.length() > 0)) {
            if (escapeText) {
                text = escapeElementEntities(text);
            }






            if (format.isTrimText()) {
                boolean first = true;
                StringTokenizer tokenizer = new StringTokenizer(text);

                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();

                    if (first) {
                        first = false;

                        if (lastOutputNodeType == Node.TEXT_NODE) {
                            writer.write(" ");
                        }
                    } else {
                        writer.write(" ");
                    }

                    writer.write(token);
                    lastOutputNodeType = Node.TEXT_NODE;
                    lastChar = token.charAt(token.length() - 1);
                }
            } else {
                lastOutputNodeType = Node.TEXT_NODE;
                writer.write(text);
                lastChar = text.charAt(text.length() - 1);
            }
        }
    }


    protected void writeNodeText(Node node) throws IOException {
        String text = node.getText();

        if ((text != null) && (text.length() > 0)) {
            if (escapeText) {
                text = escapeElementEntities(text);
            }

            lastOutputNodeType = Node.TEXT_NODE;
            writer.write(text);
            lastChar = text.charAt(text.length() - 1);
        }
    }

    protected void writeNode(Node node) throws IOException {
        int nodeType = node.getNodeType();

        switch (nodeType) {
            case Node.ELEMENT_NODE:
                writeElement((Element) node);

                break;

            case Node.ATTRIBUTE_NODE:
                writeAttribute((Attribute) node);

                break;

            case Node.TEXT_NODE:
                writeNodeText(node);


                break;

            case Node.CDATA_SECTION_NODE:
                writeCDATA(node.getText());

                break;

            case Node.ENTITY_REFERENCE_NODE:
                writeEntity((Entity) node);

                break;

            case Node.PROCESSING_INSTRUCTION_NODE:
                writeProcessingInstruction((ProcessingInstruction) node);

                break;

            case Node.COMMENT_NODE:
                writeComment(node.getText());

                break;

            case Node.DOCUMENT_NODE:
                write((Document) node);

                break;

            case Node.DOCUMENT_TYPE_NODE:
                writeDocType((DocumentType) node);

                break;

            case Node.NAMESPACE_NODE:



                break;

            default:
                throw new IOException("Invalid node type: " + node);
        }
    }

    protected void installLexicalHandler() {
        XMLReader parent = getParent();

        if (parent == null) {
            throw new NullPointerException("No parent for filter");
        }


        for (int i = 0; i < LEXICAL_HANDLER_NAMES.length; i++) {
            try {
                parent.setProperty(LEXICAL_HANDLER_NAMES[i], this);

                break;
            } catch (SAXNotRecognizedException ex) {

            } catch (SAXNotSupportedException ex) {

            }
        }
    }

    protected void writeDocType(String name, String publicID, String systemID) throws IOException {
        boolean hasPublic = false;

        writer.write("<!DOCTYPE ");
        writer.write(name);

        if ((publicID != null) && (!publicID.equals(""))) {
            writer.write(" PUBLIC \"");
            writer.write(publicID);
            writer.write("\"");
            hasPublic = true;
        }

        if ((systemID != null) && (!systemID.equals(""))) {
            if (!hasPublic) {
                writer.write(" SYSTEM");
            }

            writer.write(" \"");
            writer.write(systemID);
            writer.write("\"");
        }

        writer.write(">");
        writePrintln();
    }

    protected void writeEntity(Entity entity) throws IOException {
        if (!resolveEntityRefs()) {
            writeEntityRef(entity.getName());
        } else {
            writer.write(entity.getText());
        }
    }

    protected void writeEntityRef(String name) throws IOException {
        writer.write("&");
        writer.write(name);
        writer.write(";");

        lastOutputNodeType = Node.ENTITY_REFERENCE_NODE;
    }

    protected void writeComment(String text) throws IOException {
        if (format.isNewlines()) {
            println();
            indent();
        }

        writer.write("<!--");
        writer.write(text);
        writer.write("-->");

        lastOutputNodeType = Node.COMMENT_NODE;
    }


    protected void writeAttributes(Element element) throws IOException {




        for (int i = 0, size = element.attributeCount(); i < size; i++) {
            Attribute attribute = element.attribute(i);
            Namespace ns = attribute.getNamespace();

            if ((ns != null) && (ns != Namespace.NO_NAMESPACE) && (ns != Namespace.XML_NAMESPACE)) {
                String prefix = ns.getPrefix();
                String uri = namespaceStack.getURI(prefix);

                if (!ns.getURI().equals(uri)) {
                    writeNamespace(ns);
                    namespaceStack.push(ns);
                }
            }




            String attName = attribute.getName();

            if (attName.startsWith("xmlns:")) {
                String prefix = attName.substring(6);

                if (namespaceStack.getNamespaceForPrefix(prefix) == null) {
                    String uri = attribute.getValue();
                    namespaceStack.push(prefix, uri);
                    writeNamespace(prefix, uri);
                }
            } else if (attName.equals("xmlns")) {
                if (namespaceStack.getDefaultNamespace() == null) {
                    String uri = attribute.getValue();
                    namespaceStack.push(null, uri);
                    writeNamespace(null, uri);
                }
            } else {
                char quote = format.getAttributeQuoteCharacter();
                writer.write(" ");
                writer.write(attribute.getQualifiedName());
                writer.write("=");
                writer.write(quote);
                writeEscapeAttributeEntities(attribute.getValue());
                writer.write(quote);
            }
        }
    }

    protected void writeAttribute(Attribute attribute) throws IOException {
        writer.write(" ");
        writer.write(attribute.getQualifiedName());
        writer.write("=");

        char quote = format.getAttributeQuoteCharacter();
        writer.write(quote);

        writeEscapeAttributeEntities(attribute.getValue());

        writer.write(quote);
        lastOutputNodeType = Node.ATTRIBUTE_NODE;
    }

    protected void writeAttributes(Attributes attributes) throws IOException {
        for (int i = 0, size = attributes.getLength(); i < size; i++) {
            writeAttribute(attributes, i);
        }
    }

    protected void writeAttribute(Attributes attributes, int index) throws IOException {
        char quote = format.getAttributeQuoteCharacter();
        writer.write(" ");
        writer.write(attributes.getQName(index));
        writer.write("=");
        writer.write(quote);
        writeEscapeAttributeEntities(attributes.getValue(index));
        writer.write(quote);
    }

    protected void indent() throws IOException {
        String indent = format.getIndent();

        if ((indent != null) && (indent.length() > 0)) {
            for (int i = 0; i < indentLevel; i++) {
                writer.write(indent);
            }
        }
    }


    protected void writePrintln() throws IOException {
        if (format.isNewlines()) {
            String seperator = format.getLineSeparator();
            if (lastChar != seperator.charAt(seperator.length() - 1)) {
                writer.write(format.getLineSeparator());
            }
        }
    }


    protected Writer createWriter(OutputStream outStream, String encoding)
            throws UnsupportedEncodingException {
        return new BufferedWriter(new OutputStreamWriter(outStream, encoding));
    }


    protected void writeDeclaration() throws IOException {
        String encoding = format.getEncoding();


        if (!format.isSuppressDeclaration()) {

            if (encoding.equals("UTF8")) {
                writer.write("<?xml version=\"1.0\"");

                if (!format.isOmitEncoding()) {
                    writer.write(" encoding=\"UTF-8\"");
                }

                writer.write("?>");
            } else {
                writer.write("<?xml version=\"1.0\"");

                if (!format.isOmitEncoding()) {
                    writer.write(" encoding=\"" + encoding + "\"");
                }

                writer.write("?>");
            }

            if (format.isNewLineAfterDeclaration()) {
                println();
            }
        }
    }

    protected void writeClose(String qualifiedName) throws IOException {
        writer.write("</");
        writer.write(qualifiedName);
        writer.write(">");
    }

    protected void writeEmptyElementClose(String qualifiedName) throws IOException {

        if (!format.isExpandEmptyElements()) {
            writer.write("/>");
        } else {
            writer.write("></");
            writer.write(qualifiedName);
            writer.write(">");
        }
    }

    protected boolean isExpandEmptyElements() {
        return format.isExpandEmptyElements();
    }


    protected String escapeElementEntities(String text) {
        char[] block = null;
        int i;
        int last = 0;
        int size = text.length();

        for (i = 0; i < size; i++) {
            String entity = null;
            char c = text.charAt(i);

            switch (c) {
                case '<':
                    entity = "&lt;";

                    break;

                case '>':
                    entity = "&gt;";

                    break;

                case '&':
                    entity = "&amp;";

                    break;

                case '\t':
                case '\n':
                case '\r':


                    if (preserve) {
                        entity = String.valueOf(c);
                    }

                    break;

                default:

                    if ((c < 32) || shouldEncodeChar(c)) {
                        entity = "&#" + (int) c + ";";
                    }

                    break;
            }

            if (entity != null) {
                if (block == null) {
                    block = text.toCharArray();
                }

                buffer.append(block, last, i - last);
                buffer.append(entity);
                last = i + 1;
            }
        }

        if (last == 0) {
            return text;
        }

        if (last < size) {
            if (block == null) {
                block = text.toCharArray();
            }

            buffer.append(block, last, i - last);
        }

        String answer = buffer.toString();
        buffer.setLength(0);

        return answer;
    }

    protected void writeEscapeAttributeEntities(String txt) throws IOException {
        if (txt != null) {
            String escapedText = escapeAttributeEntities(txt);
            writer.write(escapedText);
        }
    }


    protected String escapeAttributeEntities(String text) {
        char quote = format.getAttributeQuoteCharacter();

        char[] block = null;
        int i;
        int last = 0;
        int size = text.length();

        for (i = 0; i < size; i++) {
            String entity = null;
            char c = text.charAt(i);

            switch (c) {
                case '<':
                    entity = "&lt;";

                    break;

                case '>':
                    entity = "&gt;";

                    break;

                case '\'':

                    if (quote == '\'') {
                        entity = "&apos;";
                    }

                    break;

                case '\"':

                    if (quote == '\"') {
                        entity = "&quot;";
                    }

                    break;

                case '&':
                    entity = "&amp;";

                    break;

                case '\t':
                case '\n':
                case '\r':


                    break;

                default:

                    if ((c < 32) || shouldEncodeChar(c)) {
                        entity = "&#" + (int) c + ";";
                    }

                    break;
            }

            if (entity != null) {
                if (block == null) {
                    block = text.toCharArray();
                }

                buffer.append(block, last, i - last);
                buffer.append(entity);
                last = i + 1;
            }
        }

        if (last == 0) {
            return text;
        }

        if (last < size) {
            if (block == null) {
                block = text.toCharArray();
            }

            buffer.append(block, last, i - last);
        }

        String answer = buffer.toString();
        buffer.setLength(0);

        return answer;
    }


    protected boolean shouldEncodeChar(char c) {
        int max = getMaximumAllowedCharacter();

        return (max > 0) && (c > max);
    }


    protected int defaultMaximumAllowedCharacter() {
        String encoding = format.getEncoding();

        if (encoding != null) {
            if (encoding.equals("US-ASCII")) {
                return 127;
            }
        }


        return -1;
    }

    protected boolean isNamespaceDeclaration(Namespace ns) {
        if ((ns != null) && (ns != Namespace.XML_NAMESPACE)) {
            String uri = ns.getURI();

            if (uri != null) {
                if (!namespaceStack.contains(ns)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected void handleException(IOException e) throws SAXException {
        throw new SAXException(e);
    }




    protected OutputFormat getOutputFormat() {
        return format;
    }

    public boolean resolveEntityRefs() {
        return resolveEntityRefs;
    }

    public void setResolveEntityRefs(boolean resolve) {
        this.resolveEntityRefs = resolve;
    }
}



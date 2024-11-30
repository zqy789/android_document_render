

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.Comment;
import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.DocumentType;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.IllegalAddException;
import com.document.render.office.fc.dom4j.Node;
import com.document.render.office.fc.dom4j.ProcessingInstruction;
import com.document.render.office.fc.dom4j.QName;
import com.document.render.office.fc.dom4j.Text;
import com.document.render.office.fc.dom4j.Visitor;
import com.document.render.office.fc.dom4j.io.OutputFormat;
import com.document.render.office.fc.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public abstract class AbstractDocument extends AbstractBranch implements Document {

    
    protected String encoding;

    public AbstractDocument() {
    }

    public short getNodeType() {
        return DOCUMENT_NODE;
    }

    public String getPath(Element context) {
        return "/";
    }

    public String getUniquePath(Element context) {
        return "/";
    }

    public Document getDocument() {
        return this;
    }

    public String getXMLEncoding() {
        return null;
    }

    public void setXMLEncoding(String enc) {
        this.encoding = enc;
    }

    public String getStringValue() {
        Element root = getRootElement();

        return (root != null) ? root.getStringValue() : "";
    }

    public String asXML() {
        OutputFormat format = new OutputFormat();
        format.setEncoding(encoding);

        try {
            StringWriter out = new StringWriter();
            XMLWriter writer = new XMLWriter(out, format);
            writer.write(this);
            writer.flush();

            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException("IOException while generating textual " + "representation: "
                    + e.getMessage());
        }
    }

    public void write(Writer out) throws IOException {
        OutputFormat format = new OutputFormat();
        format.setEncoding(encoding);

        XMLWriter writer = new XMLWriter(out, format);
        writer.write(this);
    }

    
    public void accept(Visitor visitor) {
        visitor.visit(this);

        DocumentType docType = getDocType();

        if (docType != null) {
            visitor.visit(docType);
        }

        
        List content = content();

        if (content != null) {
            for (Iterator iter = content.iterator(); iter.hasNext(); ) {
                Object object = iter.next();

                if (object instanceof String) {
                    Text text = getDocumentFactory().createText((String) object);
                    visitor.visit(text);
                } else {
                    Node node = (Node) object;
                    node.accept(visitor);
                }
            }
        }
    }

    public String toString() {
        return super.toString() + " [Document: name " + getName() + "]";
    }

    public void normalize() {
        Element element = getRootElement();

        if (element != null) {
            element.normalize();
        }
    }

    public Document addComment(String comment) {
        Comment node = getDocumentFactory().createComment(comment);
        add(node);

        return this;
    }

    public Document addProcessingInstruction(String target, String data) {
        ProcessingInstruction node = getDocumentFactory().createProcessingInstruction(target, data);
        add(node);

        return this;
    }

    public Document addProcessingInstruction(String target, Map data) {
        ProcessingInstruction node = getDocumentFactory().createProcessingInstruction(target, data);
        add(node);

        return this;
    }

    public Element addElement(String name) {
        Element element = getDocumentFactory().createElement(name);
        add(element);

        return element;
    }

    public Element addElement(String qualifiedName, String namespaceURI) {
        Element element = getDocumentFactory().createElement(qualifiedName, namespaceURI);
        add(element);

        return element;
    }

    public Element addElement(QName qName) {
        Element element = getDocumentFactory().createElement(qName);
        add(element);

        return element;
    }

    public void setRootElement(Element rootElement) {
        clearContent();

        if (rootElement != null) {
            super.add(rootElement);
            rootElementAdded(rootElement);
        }
    }

    public void add(Element element) {
        checkAddElementAllowed(element);
        super.add(element);
        rootElementAdded(element);
    }

    public boolean remove(Element element) {
        boolean answer = super.remove(element);
        Element root = getRootElement();

        if ((root != null) && answer) {
            setRootElement(null);
        }

        element.setDocument(null);

        return answer;
    }

    public Node asXPathResult(Element parent) {
        return this;
    }

    protected void childAdded(Node node) {
        if (node != null) {
            node.setDocument(this);
        }
    }

    protected void childRemoved(Node node) {
        if (node != null) {
            node.setDocument(null);
        }
    }

    protected void checkAddElementAllowed(Element element) {
        Element root = getRootElement();

        if (root != null) {
            throw new IllegalAddException(this, element, "Cannot add another element to this "
                    + "Document as it already has a root " + "element of: " + root.getQualifiedName());
        }
    }

    
    protected abstract void rootElementAdded(Element rootElement);
}





package com.document.render.office.fc.dom4j.io;

import com.document.render.office.fc.dom4j.Attribute;
import com.document.render.office.fc.dom4j.CDATA;
import com.document.render.office.fc.dom4j.Comment;
import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.DocumentException;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Entity;
import com.document.render.office.fc.dom4j.Namespace;
import com.document.render.office.fc.dom4j.ProcessingInstruction;
import com.document.render.office.fc.dom4j.Text;
import com.document.render.office.fc.dom4j.tree.NamespaceStack;

import org.w3c.dom.DOMImplementation;

import java.util.List;



public class DOMWriter {
    private static final String[] DEFAULT_DOM_DOCUMENT_CLASSES = {
            "org.apache.xerces.dom.DocumentImpl",
            "gnu.xml.dom.DomDocument",
            "org.apache.crimson.tree.XmlDocument",
            "com.sun.xml.tree.XmlDocument",
            "oracle.xml.parser.v2.XMLDocument",
            "oracle.xml.parser.XMLDocument",
            "org.dom4j.dom.DOMDocument"
    };
    private static boolean loggedWarning = false;

    private Class domDocumentClass;


    private NamespaceStack namespaceStack = new NamespaceStack();

    public DOMWriter() {
    }

    public DOMWriter(Class domDocumentClass) {
        this.domDocumentClass = domDocumentClass;
    }

    public Class getDomDocumentClass() throws DocumentException {
        Class result = domDocumentClass;

        if (result == null) {

            int size = DEFAULT_DOM_DOCUMENT_CLASSES.length;

            for (int i = 0; i < size; i++) {
                try {
                    String name = DEFAULT_DOM_DOCUMENT_CLASSES[i];
                    result = Class.forName(name, true, DOMWriter.class.getClassLoader());

                    if (result != null) {
                        break;
                    }
                } catch (Exception e) {


                }
            }
        }

        return result;
    }


    public void setDomDocumentClass(Class domDocumentClass) {
        this.domDocumentClass = domDocumentClass;
    }


    public void setDomDocumentClassName(String name) throws DocumentException {
        try {
            this.domDocumentClass = Class.forName(name, true, DOMWriter.class.getClassLoader());
        } catch (Exception e) {
            throw new DocumentException("Could not load the DOM Document " + "class: " + name, e);
        }
    }

    public org.w3c.dom.Document write(Document document) throws DocumentException {
        if (document instanceof org.w3c.dom.Document) {
            return (org.w3c.dom.Document) document;
        }

        resetNamespaceStack();

        org.w3c.dom.Document domDocument = createDomDocument(document);
        appendDOMTree(domDocument, domDocument, document.content());
        namespaceStack.clear();

        return domDocument;
    }

    public org.w3c.dom.Document write(Document document, org.w3c.dom.DOMImplementation domImpl)
            throws DocumentException {
        if (document instanceof org.w3c.dom.Document) {
            return (org.w3c.dom.Document) document;
        }

        resetNamespaceStack();

        org.w3c.dom.Document domDocument = createDomDocument(document, domImpl);
        appendDOMTree(domDocument, domDocument, document.content());
        namespaceStack.clear();

        return domDocument;
    }

    protected void appendDOMTree(org.w3c.dom.Document domDocument, org.w3c.dom.Node domCurrent,
                                 List content) {
        int size = content.size();

        for (int i = 0; i < size; i++) {
            Object object = content.get(i);

            if (object instanceof Element) {
                appendDOMTree(domDocument, domCurrent, (Element) object);
            } else if (object instanceof String) {
                appendDOMTree(domDocument, domCurrent, (String) object);
            } else if (object instanceof Text) {
                Text text = (Text) object;
                appendDOMTree(domDocument, domCurrent, text.getText());
            } else if (object instanceof CDATA) {
                appendDOMTree(domDocument, domCurrent, (CDATA) object);
            } else if (object instanceof Comment) {
                appendDOMTree(domDocument, domCurrent, (Comment) object);
            } else if (object instanceof Entity) {
                appendDOMTree(domDocument, domCurrent, (Entity) object);
            } else if (object instanceof ProcessingInstruction) {
                appendDOMTree(domDocument, domCurrent, (ProcessingInstruction) object);
            }
        }
    }

    protected void appendDOMTree(org.w3c.dom.Document domDocument, org.w3c.dom.Node domCurrent,
                                 Element element) {
        String elUri = element.getNamespaceURI();
        String elName = element.getQualifiedName();
        org.w3c.dom.Element domElement = domDocument.createElementNS(elUri, elName);

        int stackSize = namespaceStack.size();


        Namespace elementNamespace = element.getNamespace();

        if (isNamespaceDeclaration(elementNamespace)) {
            namespaceStack.push(elementNamespace);
            writeNamespace(domElement, elementNamespace);
        }


        List declaredNamespaces = element.declaredNamespaces();

        for (int i = 0, size = declaredNamespaces.size(); i < size; i++) {
            Namespace namespace = (Namespace) declaredNamespaces.get(i);

            if (isNamespaceDeclaration(namespace)) {
                namespaceStack.push(namespace);
                writeNamespace(domElement, namespace);
            }
        }


        for (int i = 0, size = element.attributeCount(); i < size; i++) {
            Attribute attribute = (Attribute) element.attribute(i);
            String attUri = attribute.getNamespaceURI();
            String attName = attribute.getQualifiedName();
            String value = attribute.getValue();
            domElement.setAttributeNS(attUri, attName, value);
        }


        appendDOMTree(domDocument, domElement, element.content());

        domCurrent.appendChild(domElement);

        while (namespaceStack.size() > stackSize) {
            namespaceStack.pop();
        }
    }

    protected void appendDOMTree(org.w3c.dom.Document domDocument, org.w3c.dom.Node domCurrent,
                                 CDATA cdata) {
        org.w3c.dom.CDATASection domCDATA = domDocument.createCDATASection(cdata.getText());
        domCurrent.appendChild(domCDATA);
    }

    protected void appendDOMTree(org.w3c.dom.Document domDocument, org.w3c.dom.Node domCurrent,
                                 Comment comment) {
        org.w3c.dom.Comment domComment = domDocument.createComment(comment.getText());
        domCurrent.appendChild(domComment);
    }

    protected void appendDOMTree(org.w3c.dom.Document domDocument, org.w3c.dom.Node domCurrent,
                                 String text) {
        org.w3c.dom.Text domText = domDocument.createTextNode(text);
        domCurrent.appendChild(domText);
    }

    protected void appendDOMTree(org.w3c.dom.Document domDocument, org.w3c.dom.Node domCurrent,
                                 Entity entity) {
        org.w3c.dom.EntityReference domEntity = domDocument.createEntityReference(entity.getName());
        domCurrent.appendChild(domEntity);
    }

    protected void appendDOMTree(org.w3c.dom.Document domDoc, org.w3c.dom.Node domCurrent,
                                 ProcessingInstruction pi) {
        org.w3c.dom.ProcessingInstruction domPI = domDoc.createProcessingInstruction(
                pi.getTarget(), pi.getText());
        domCurrent.appendChild(domPI);
    }

    protected void writeNamespace(org.w3c.dom.Element domElement, Namespace namespace) {
        String attributeName = attributeNameForNamespace(namespace);


        domElement.setAttribute(attributeName, namespace.getURI());
    }

    protected String attributeNameForNamespace(Namespace namespace) {
        String xmlns = "xmlns";
        String prefix = namespace.getPrefix();

        if (prefix.length() > 0) {
            return xmlns + ":" + prefix;
        }

        return xmlns;
    }

    protected org.w3c.dom.Document createDomDocument(Document document) throws DocumentException {
        org.w3c.dom.Document result = null;


        if (domDocumentClass != null) {
            try {
                result = (org.w3c.dom.Document) domDocumentClass.newInstance();
            } catch (Exception e) {
                throw new DocumentException("Could not instantiate an instance "
                        + "of DOM Document with class: " + domDocumentClass.getName(), e);
            }
        } else {

            result = createDomDocumentViaJAXP();

            if (result == null) {
                Class theClass = getDomDocumentClass();

                try {
                    result = (org.w3c.dom.Document) theClass.newInstance();
                } catch (Exception e) {
                    throw new DocumentException("Could not instantiate an "
                            + "instance of DOM Document " + "with class: " + theClass.getName(), e);
                }
            }
        }

        return result;
    }

    protected org.w3c.dom.Document createDomDocumentViaJAXP() throws DocumentException {
        try {
            return JAXPHelper.createDocument(false, true);
        } catch (Throwable e) {
            if (!loggedWarning) {
                loggedWarning = true;

                if (SAXHelper.isVerboseErrorReporting()) {

                    e.printStackTrace();
                } else {
                }
            }
        }

        return null;
    }

    protected org.w3c.dom.Document createDomDocument(Document document, DOMImplementation domImpl)
            throws DocumentException {
        String namespaceURI = null;
        String qualifiedName = null;
        org.w3c.dom.DocumentType docType = null;

        return domImpl.createDocument(namespaceURI, qualifiedName, docType);
    }

    protected boolean isNamespaceDeclaration(Namespace ns) {
        if ((ns != null) && (ns != Namespace.NO_NAMESPACE) && (ns != Namespace.XML_NAMESPACE)) {
            String uri = ns.getURI();

            if ((uri != null) && (uri.length() > 0)) {
                if (!namespaceStack.contains(ns)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected void resetNamespaceStack() {
        namespaceStack.clear();
        namespaceStack.push(Namespace.XML_NAMESPACE);
    }
}



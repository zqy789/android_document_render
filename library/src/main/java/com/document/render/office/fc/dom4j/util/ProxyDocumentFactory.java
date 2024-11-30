

package com.document.render.office.fc.dom4j.util;

import com.document.render.office.fc.dom4j.Attribute;
import com.document.render.office.fc.dom4j.CDATA;
import com.document.render.office.fc.dom4j.Comment;
import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.DocumentFactory;
import com.document.render.office.fc.dom4j.DocumentType;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Entity;
import com.document.render.office.fc.dom4j.Namespace;
import com.document.render.office.fc.dom4j.NodeFilter;
import com.document.render.office.fc.dom4j.ProcessingInstruction;
import com.document.render.office.fc.dom4j.QName;
import com.document.render.office.fc.dom4j.Text;
import com.document.render.office.fc.dom4j.XPath;
import com.document.render.office.fc.dom4j.rule.Pattern;

import java.util.Map;



public abstract class ProxyDocumentFactory {
    private DocumentFactory proxy;

    public ProxyDocumentFactory() {

        this.proxy = DocumentFactory.getInstance();
    }

    public ProxyDocumentFactory(DocumentFactory proxy) {
        this.proxy = proxy;
    }



    public Document createDocument() {
        return proxy.createDocument();
    }

    public Document createDocument(Element rootElement) {
        return proxy.createDocument(rootElement);
    }

    public DocumentType createDocType(String name, String publicId, String systemId) {
        return proxy.createDocType(name, publicId, systemId);
    }

    public Element createElement(QName qname) {
        return proxy.createElement(qname);
    }

    public Element createElement(String name) {
        return proxy.createElement(name);
    }

    public Attribute createAttribute(Element owner, QName qname, String value) {
        return proxy.createAttribute(owner, qname, value);
    }

    public Attribute createAttribute(Element owner, String name, String value) {
        return proxy.createAttribute(owner, name, value);
    }

    public CDATA createCDATA(String text) {
        return proxy.createCDATA(text);
    }

    public Comment createComment(String text) {
        return proxy.createComment(text);
    }

    public Text createText(String text) {
        return proxy.createText(text);
    }

    public Entity createEntity(String name, String text) {
        return proxy.createEntity(name, text);
    }

    public Namespace createNamespace(String prefix, String uri) {
        return proxy.createNamespace(prefix, uri);
    }

    public ProcessingInstruction createProcessingInstruction(String target, String data) {
        return proxy.createProcessingInstruction(target, data);
    }

    public ProcessingInstruction createProcessingInstruction(String target, Map data) {
        return proxy.createProcessingInstruction(target, data);
    }

    public QName createQName(String localName, Namespace namespace) {
        return proxy.createQName(localName, namespace);
    }

    public QName createQName(String localName) {
        return proxy.createQName(localName);
    }

    public QName createQName(String name, String prefix, String uri) {
        return proxy.createQName(name, prefix, uri);
    }

    public QName createQName(String qualifiedName, String uri) {
        return proxy.createQName(qualifiedName, uri);
    }

    public XPath createXPath(String xpathExpression) {
        return proxy.createXPath(xpathExpression);
    }



    public NodeFilter createXPathFilter(String xpathFilterExpression) {
        return proxy.createXPathFilter(xpathFilterExpression);
    }

    public Pattern createPattern(String xpathPattern) {
        return proxy.createPattern(xpathPattern);
    }



    protected DocumentFactory getProxy() {
        return proxy;
    }

    protected void setProxy(DocumentFactory proxy) {
        if (proxy == null) {

            proxy = DocumentFactory.getInstance();
        }

        this.proxy = proxy;
    }
}



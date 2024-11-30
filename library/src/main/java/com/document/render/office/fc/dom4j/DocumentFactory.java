

package com.document.render.office.fc.dom4j;

import com.document.render.office.fc.dom4j.rule.Pattern;
import com.document.render.office.fc.dom4j.tree.AbstractDocument;
import com.document.render.office.fc.dom4j.tree.DefaultAttribute;
import com.document.render.office.fc.dom4j.tree.DefaultCDATA;
import com.document.render.office.fc.dom4j.tree.DefaultComment;
import com.document.render.office.fc.dom4j.tree.DefaultDocument;
import com.document.render.office.fc.dom4j.tree.DefaultDocumentType;
import com.document.render.office.fc.dom4j.tree.DefaultElement;
import com.document.render.office.fc.dom4j.tree.DefaultEntity;
import com.document.render.office.fc.dom4j.tree.DefaultProcessingInstruction;
import com.document.render.office.fc.dom4j.tree.DefaultText;
import com.document.render.office.fc.dom4j.tree.QNameCache;
import com.document.render.office.fc.dom4j.util.SimpleSingleton;
import com.document.render.office.fc.dom4j.util.SingletonStrategy;
import com.document.render.office.fc.dom4j.xpath.DefaultXPath;
import com.document.render.office.fc.dom4j.xpath.XPathPattern;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;



public class DocumentFactory implements Serializable {
    private static SingletonStrategy singleton = null;

    protected transient QNameCache cache;


    private Map xpathNamespaceURIs;

    public DocumentFactory() {
        init();
    }

    private static SingletonStrategy createSingleton() {
        SingletonStrategy result = null;

        String documentFactoryClassName;
        try {
            documentFactoryClassName = System.getProperty("com.document.render.office.fc.dom4j.factory",
                    "com.document.render.office.fc.dom4j.DocumentFactory");
        } catch (Exception e) {
            documentFactoryClassName = "com.document.render.office.fc.dom4j.DocumentFactory";
        }

        try {
            String singletonClass = System.getProperty(
                    "com.document.render.office.fc.dom4j.DocumentFactory.singleton.strategy", "com.document.render.office.fc.dom4j.util.SimpleSingleton");
            Class clazz = Class.forName(singletonClass);
            result = (SingletonStrategy) clazz.newInstance();
        } catch (Exception e) {
            result = new SimpleSingleton();
        }

        result.setSingletonClassName(documentFactoryClassName);

        return result;
    }


    public static synchronized DocumentFactory getInstance() {
        if (singleton == null) {
            singleton = createSingleton();
        }
        return (DocumentFactory) singleton.instance();
    }


    protected static DocumentFactory createSingleton(String className) {

        try {


            Class theClass = Class.forName(className, true, DocumentFactory.class.getClassLoader());

            return (DocumentFactory) theClass.newInstance();
        } catch (Throwable e) {
            System.out.println("WARNING: Cannot load DocumentFactory: " + className);

            return new DocumentFactory();
        }
    }


    public Document createDocument() {
        DefaultDocument answer = new DefaultDocument();
        answer.setDocumentFactory(this);

        return answer;
    }


    public Document createDocument(String encoding) {



        Document answer = createDocument();

        if (answer instanceof AbstractDocument) {
            ((AbstractDocument) answer).setXMLEncoding(encoding);
        }

        return answer;
    }

    public Document createDocument(Element rootElement) {
        Document answer = createDocument();
        answer.setRootElement(rootElement);

        return answer;
    }

    public DocumentType createDocType(String name, String publicId, String systemId) {
        return new DefaultDocumentType(name, publicId, systemId);
    }

    public Element createElement(QName qname) {
        return new DefaultElement(qname);
    }

    public Element createElement(String name) {
        return createElement(createQName(name));
    }

    public Element createElement(String qualifiedName, String namespaceURI) {
        return createElement(createQName(qualifiedName, namespaceURI));
    }

    public Attribute createAttribute(Element owner, QName qname, String value) {
        return new DefaultAttribute(qname, value);
    }

    public Attribute createAttribute(Element owner, String name, String value) {
        return createAttribute(owner, createQName(name), value);
    }

    public CDATA createCDATA(String text) {
        return new DefaultCDATA(text);
    }

    public Comment createComment(String text) {
        return new DefaultComment(text);
    }

    public Text createText(String text) {
        if (text == null) {
            String msg = "Adding text to an XML document must not be null";
            throw new IllegalArgumentException(msg);
        }

        return new DefaultText(text);
    }

    public Entity createEntity(String name, String text) {
        return new DefaultEntity(name, text);
    }

    public Namespace createNamespace(String prefix, String uri) {
        return Namespace.get(prefix, uri);
    }

    public ProcessingInstruction createProcessingInstruction(String target, String data) {
        return new DefaultProcessingInstruction(target, data);
    }

    public ProcessingInstruction createProcessingInstruction(String target, Map data) {
        return new DefaultProcessingInstruction(target, data);
    }

    public QName createQName(String localName, Namespace namespace) {
        return cache.get(localName, namespace);
    }

    public QName createQName(String localName) {
        return cache.get(localName);
    }

    public QName createQName(String name, String prefix, String uri) {
        return cache.get(name, Namespace.get(prefix, uri));
    }

    public QName createQName(String qualifiedName, String uri) {
        return cache.get(qualifiedName, uri);
    }


    public XPath createXPath(String xpathExpression) throws InvalidXPathException {
        DefaultXPath xpath = new DefaultXPath(xpathExpression);

        if (xpathNamespaceURIs != null) {
            xpath.setNamespaceURIs(xpathNamespaceURIs);
        }

        return xpath;
    }


    public NodeFilter createXPathFilter(String xpathFilterExpression) {
        return createXPath(xpathFilterExpression);


    }





    public Pattern createPattern(String xpathPattern) {
        return new XPathPattern(xpathPattern);
    }


    public List getQNames() {
        return cache.getQNames();
    }


    public Map getXPathNamespaceURIs() {
        return xpathNamespaceURIs;
    }





    public void setXPathNamespaceURIs(Map namespaceURIs) {
        this.xpathNamespaceURIs = namespaceURIs;
    }


    protected QName intern(QName qname) {
        return cache.intern(qname);
    }


    protected QNameCache createQNameCache() {
        return new QNameCache(this);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        init();
    }

    protected void init() {
        cache = createQNameCache();
    }
}



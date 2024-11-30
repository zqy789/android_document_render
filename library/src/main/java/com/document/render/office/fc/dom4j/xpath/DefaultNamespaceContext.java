

package com.document.render.office.fc.dom4j.xpath;

import androidx.annotation.Keep;

import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Namespace;
import com.document.render.office.fc.dom4j.Node;

import java.io.Serializable;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;



public class DefaultNamespaceContext implements NamespaceContext, Serializable {
    private final Element element;

    public DefaultNamespaceContext(Element element) {
        this.element = element;
    }

    @Keep
    public static DefaultNamespaceContext create(Object node) {
        Element element = null;

        if (node instanceof Element) {
            element = (Element) node;
        } else if (node instanceof Document) {
            Document doc = (Document) node;
            element = doc.getRootElement();
        } else if (node instanceof Node) {
            element = ((Node) node).getParent();
        }

        if (element != null) {
            return new DefaultNamespaceContext(element);
        }

        return null;
    }

    public String translateNamespacePrefixToUri(String prefix) {
        if ((prefix != null) && (prefix.length() > 0)) {
            Namespace ns = element.getNamespaceForPrefix(prefix);

            if (ns != null) {
                return ns.getURI();
            }
        }

        return null;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        
        return null;
    }

    @Override
    public String getPrefix(String namespaceURI) {
        
        return null;
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        
        return null;
    }
}



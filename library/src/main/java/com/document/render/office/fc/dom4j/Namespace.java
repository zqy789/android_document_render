

package com.document.render.office.fc.dom4j;

import com.document.render.office.fc.dom4j.tree.AbstractNode;
import com.document.render.office.fc.dom4j.tree.DefaultNamespace;
import com.document.render.office.fc.dom4j.tree.NamespaceCache;


public class Namespace extends AbstractNode {

    protected static final NamespaceCache CACHE = new NamespaceCache();


    public static final Namespace XML_NAMESPACE = CACHE.get("xml",
            "http://www.w3.org/XML/1998/namespace");


    public static final Namespace NO_NAMESPACE = CACHE.get("", "");


    private String prefix;


    private String uri;


    private int hashCode;


    public Namespace(String prefix, String uri) {
        this.prefix = (prefix != null) ? prefix : "";
        this.uri = (uri != null) ? uri : "";
    }


    public static Namespace get(String prefix, String uri) {
        return CACHE.get(prefix, uri);
    }


    public static Namespace get(String uri) {
        return CACHE.get(uri);
    }

    public short getNodeType() {
        return NAMESPACE_NODE;
    }


    public int hashCode() {
        if (hashCode == 0) {
            hashCode = createHashCode();
        }

        return hashCode;
    }


    protected int createHashCode() {
        int result = uri.hashCode() ^ prefix.hashCode();

        if (result == 0) {
            result = 0xbabe;
        }

        return result;
    }


    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object instanceof Namespace) {
            Namespace that = (Namespace) object;


            if (hashCode() == that.hashCode()) {
                return uri.equals(that.getURI()) && prefix.equals(that.getPrefix());
            }
        }

        return false;
    }

    public String getText() {
        return uri;
    }

    public String getStringValue() {
        return uri;
    }


    public String getPrefix() {
        return prefix;
    }


    public String getURI() {
        return uri;
    }

    public String getXPathNameStep() {
        if ((prefix != null) && !"".equals(prefix)) {
            return "namespace::" + prefix;
        }

        return "namespace::*[name()='']";
    }

    public String getPath(Element context) {
        StringBuffer path = new StringBuffer(10);
        Element parent = getParent();

        if ((parent != null) && (parent != context)) {
            path.append(parent.getPath(context));
            path.append('/');
        }

        path.append(getXPathNameStep());

        return path.toString();
    }

    public String getUniquePath(Element context) {
        StringBuffer path = new StringBuffer(10);
        Element parent = getParent();

        if ((parent != null) && (parent != context)) {
            path.append(parent.getUniquePath(context));
            path.append('/');
        }

        path.append(getXPathNameStep());

        return path.toString();
    }

    public String toString() {
        return super.toString() + " [Namespace: prefix " + getPrefix() + " mapped to URI \""
                + getURI() + "\"]";
    }

    public String asXML() {
        StringBuffer asxml = new StringBuffer(10);
        String pref = getPrefix();

        if ((pref != null) && (pref.length() > 0)) {
            asxml.append("xmlns:");
            asxml.append(pref);
            asxml.append("=\"");
        } else {
            asxml.append("xmlns=\"");
        }

        asxml.append(getURI());
        asxml.append("\"");

        return asxml.toString();
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    protected Node createXPathResult(Element parent) {
        return new DefaultNamespace(parent, getPrefix(), getURI());
    }
}



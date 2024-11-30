

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Namespace;
import com.document.render.office.fc.dom4j.QName;


public class DefaultAttribute extends FlyweightAttribute {

    private Element parent;

    public DefaultAttribute(QName qname) {
        super(qname);
    }

    public DefaultAttribute(QName qname, String value) {
        super(qname, value);
    }

    public DefaultAttribute(Element parent, QName qname, String value) {
        super(qname, value);
        this.parent = parent;
    }


    public DefaultAttribute(String name, String value) {
        super(name, value);
    }


    public DefaultAttribute(String name, String value, Namespace namespace) {
        super(name, value, namespace);
    }


    public DefaultAttribute(Element parent, String name, String value, Namespace namespace) {
        super(name, value, namespace);
        this.parent = parent;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Element getParent() {
        return parent;
    }

    public void setParent(Element parent) {
        this.parent = parent;
    }

    public boolean supportsParent() {
        return true;
    }

    public boolean isReadOnly() {
        return false;
    }
}



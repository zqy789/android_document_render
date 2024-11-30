

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Namespace;


public class DefaultNamespace extends Namespace {

    private Element parent;


    public DefaultNamespace(String prefix, String uri) {
        super(prefix, uri);
    }


    public DefaultNamespace(Element parent, String prefix, String uri) {
        super(prefix, uri);
        this.parent = parent;
    }


    protected int createHashCode() {
        int hashCode = super.createHashCode();

        if (parent != null) {
            hashCode ^= parent.hashCode();
        }

        return hashCode;
    }


    public boolean equals(Object object) {
        if (object instanceof DefaultNamespace) {
            DefaultNamespace that = (DefaultNamespace) object;

            if (that.parent == parent) {
                return super.equals(object);
            }
        }

        return false;
    }

    public int hashCode() {
        return super.hashCode();
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



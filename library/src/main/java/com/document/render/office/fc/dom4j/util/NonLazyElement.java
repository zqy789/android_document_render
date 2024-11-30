

package com.document.render.office.fc.dom4j.util;

import com.document.render.office.fc.dom4j.Namespace;
import com.document.render.office.fc.dom4j.QName;
import com.document.render.office.fc.dom4j.tree.BaseElement;



public class NonLazyElement extends BaseElement {
    public NonLazyElement(String name) {
        super(name);
        this.attributes = createAttributeList();
        this.content = createContentList();
    }

    public NonLazyElement(QName qname) {
        super(qname);
        this.attributes = createAttributeList();
        this.content = createContentList();
    }

    public NonLazyElement(String name, Namespace namespace) {
        super(name, namespace);
        this.attributes = createAttributeList();
        this.content = createContentList();
    }

    public NonLazyElement(QName qname, int attributeCount) {
        super(qname);
        this.attributes = createAttributeList(attributeCount);
        this.content = createContentList();
    }
}



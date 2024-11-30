

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.CDATA;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Node;


public class FlyweightCDATA extends AbstractCDATA implements CDATA {

    protected String text;


    public FlyweightCDATA(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    protected Node createXPathResult(Element parent) {
        return new DefaultCDATA(parent, getText());
    }
}



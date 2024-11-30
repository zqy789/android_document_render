

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.Element;


public class DefaultText extends FlyweightText {

    private Element parent;


    public DefaultText(String text) {
        super(text);
    }


    public DefaultText(Element parent, String text) {
        super(text);
        this.parent = parent;
    }

    public void setText(String text) {
        this.text = text;
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



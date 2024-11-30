

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.Comment;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Node;


public class FlyweightComment extends AbstractComment implements Comment {

    protected String text;


    public FlyweightComment(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    protected Node createXPathResult(Element parent) {
        return new DefaultComment(parent, getText());
    }
}



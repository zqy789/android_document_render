

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.CharacterData;
import com.document.render.office.fc.dom4j.Element;


public abstract class AbstractCharacterData extends AbstractNode implements CharacterData {
    public AbstractCharacterData() {
    }

    public String getPath(Element context) {
        Element parent = getParent();

        return ((parent != null) && (parent != context)) ? (parent.getPath(context) + "/text()")
                : "text()";
    }

    public String getUniquePath(Element context) {
        Element parent = getParent();

        return ((parent != null) && (parent != context))
                ? (parent.getUniquePath(context) + "/text()") : "text()";
    }

    public void appendText(String text) {
        setText(getText() + text);
    }
}



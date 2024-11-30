

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.Element;

import java.util.Map;



public class DefaultProcessingInstruction extends FlyweightProcessingInstruction {

    private Element parent;


    public DefaultProcessingInstruction(String target, Map values) {
        super(target, values);
    }


    public DefaultProcessingInstruction(String target, String values) {
        super(target, values);
    }


    public DefaultProcessingInstruction(Element parent, String target, String values) {
        super(target, values);
        this.parent = parent;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setText(String text) {
        this.text = text;
        this.values = parseValues(text);
    }

    public void setValues(Map values) {
        this.values = values;
        this.text = toString(values);
    }

    public void setValue(String name, String value) {
        values.put(name, value);
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



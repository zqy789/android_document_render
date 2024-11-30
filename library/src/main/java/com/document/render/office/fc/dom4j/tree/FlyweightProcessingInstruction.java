

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Node;

import java.util.Collections;
import java.util.Map;



public class FlyweightProcessingInstruction extends AbstractProcessingInstruction {

    protected String target;


    protected String text;


    protected Map values;


    public FlyweightProcessingInstruction() {
    }


    public FlyweightProcessingInstruction(String target, Map values) {
        this.target = target;
        this.values = values;
        this.text = toString(values);
    }


    public FlyweightProcessingInstruction(String target, String text) {
        this.target = target;
        this.text = text;
        this.values = parseValues(text);
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        throw new UnsupportedOperationException("This PI is read-only and " + "cannot be modified");
    }

    public String getText() {
        return text;
    }

    public String getValue(String name) {
        String answer = (String) values.get(name);

        if (answer == null) {
            return "";
        }

        return answer;
    }

    public Map getValues() {
        return Collections.unmodifiableMap(values);
    }

    protected Node createXPathResult(Element parent) {
        return new DefaultProcessingInstruction(parent, getTarget(), getText());
    }
}



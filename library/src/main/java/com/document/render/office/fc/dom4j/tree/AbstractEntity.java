

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Entity;
import com.document.render.office.fc.dom4j.Visitor;

import java.io.IOException;
import java.io.Writer;



public abstract class AbstractEntity extends AbstractNode implements Entity {
    public AbstractEntity() {
    }

    public short getNodeType() {
        return ENTITY_REFERENCE_NODE;
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

    public String toString() {
        return super.toString() + " [Entity: &" + getName() + ";]";
    }

    public String getStringValue() {
        return "&" + getName() + ";";
    }

    public String asXML() {
        return "&" + getName() + ";";
    }

    public void write(Writer writer) throws IOException {
        writer.write("&");
        writer.write(getName());
        writer.write(";");
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}



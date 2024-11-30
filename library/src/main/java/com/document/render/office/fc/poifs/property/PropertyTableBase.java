

package com.document.render.office.fc.poifs.property;

import com.document.render.office.fc.poifs.filesystem.BATManaged;
import com.document.render.office.fc.poifs.storage.HeaderBlock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;



public abstract class PropertyTableBase implements BATManaged {
    protected final List<Property> _properties;
    private final HeaderBlock _header_block;

    public PropertyTableBase(final HeaderBlock header_block) {
        _header_block = header_block;
        _properties = new ArrayList<Property>();
        addProperty(new RootProperty());
    }


    public PropertyTableBase(final HeaderBlock header_block, final List<Property> properties)
            throws IOException {
        _header_block = header_block;
        _properties = properties;
        populatePropertyTree((DirectoryProperty) _properties.get(0));
    }


    public void addProperty(Property property) {
        _properties.add(property);
    }


    public void removeProperty(final Property property) {
        _properties.remove(property);
    }


    public RootProperty getRoot() {

        return (RootProperty) _properties.get(0);
    }

    private void populatePropertyTree(DirectoryProperty root) throws IOException {
        int index = root.getChildIndex();

        if (!Property.isValidIndex(index)) {


            return;
        }
        Stack<Property> children = new Stack<Property>();

        children.push(_properties.get(index));
        while (!children.empty()) {
            Property property = children.pop();

            root.addChild(property);
            if (property.isDirectory()) {
                populatePropertyTree((DirectoryProperty) property);
            }
            index = property.getPreviousChildIndex();
            if (Property.isValidIndex(index)) {
                children.push(_properties.get(index));
            }
            index = property.getNextChildIndex();
            if (Property.isValidIndex(index)) {
                children.push(_properties.get(index));
            }
        }
    }


    public int getStartBlock() {
        return _header_block.getPropertyStart();
    }


    public void setStartBlock(final int index) {
        _header_block.setPropertyStart(index);
    }
}

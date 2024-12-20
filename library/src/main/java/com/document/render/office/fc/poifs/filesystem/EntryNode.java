


package com.document.render.office.fc.poifs.filesystem;

import com.document.render.office.fc.poifs.property.Property;



public abstract class EntryNode
        implements Entry {


    private Property _property;


    private DirectoryNode _parent;



    protected EntryNode(final Property property, final DirectoryNode parent) {
        _property = property;
        _parent = parent;
    }



    protected Property getProperty() {
        return _property;
    }



    protected boolean isRoot() {


        return (_parent == null);
    }



    protected abstract boolean isDeleteOK();





    public String getName() {
        return _property.getName();
    }



    public boolean isDirectoryEntry() {
        return false;
    }



    public boolean isDocumentEntry() {
        return false;
    }



    public DirectoryEntry getParent() {
        return _parent;
    }



    public boolean delete() {
        boolean rval = false;

        if ((!isRoot()) && isDeleteOK()) {
            rval = _parent.deleteEntry(this);
        }
        return rval;
    }



    public boolean renameTo(final String newName) {
        boolean rval = false;

        if (!isRoot()) {
            rval = _parent.changeName(getName(), newName);
        }
        return rval;
    }


}


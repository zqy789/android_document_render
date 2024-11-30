


package com.document.render.office.fc.poifs.filesystem;

import com.document.render.office.fc.poifs.property.DocumentProperty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;




public class DocumentNode
        extends EntryNode
        implements DocumentEntry {


    private POIFSDocument _document;



    DocumentNode(final DocumentProperty property, final DirectoryNode parent) {
        super(property, parent);
        _document = property.getDocument();
    }



    POIFSDocument getDocument() {
        return _document;
    }





    public int getSize() {
        return getProperty().getSize();
    }






    public boolean isDocumentEntry() {
        return true;
    }






    protected boolean isDeleteOK() {
        return true;
    }






    public Object[] getViewableArray() {
        return new Object[0];
    }



    public Iterator getViewableIterator() {
        List components = new ArrayList();

        components.add(getProperty());
        components.add(_document);
        return components.iterator();
    }



    public boolean preferArray() {
        return false;
    }



    public String getShortDescription() {
        return getName();
    }


}


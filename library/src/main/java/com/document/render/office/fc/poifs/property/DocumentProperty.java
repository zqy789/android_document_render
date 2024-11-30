


package com.document.render.office.fc.poifs.property;

import com.document.render.office.fc.poifs.filesystem.POIFSDocument;



public class DocumentProperty
        extends Property {


    private POIFSDocument _document;



    public DocumentProperty(final String name, final int size) {
        super();
        _document = null;
        setName(name);
        setSize(size);
        setNodeColor(_NODE_BLACK);
        setPropertyType(PropertyConstants.DOCUMENT_TYPE);
    }



    protected DocumentProperty(final int index, final byte[] array,
                               final int offset) {
        super(index, array, offset);
        _document = null;
    }



    public POIFSDocument getDocument() {
        return _document;
    }



    public void setDocument(POIFSDocument doc) {
        _document = doc;
    }





    public boolean shouldUseSmallBlocks() {
        return super.shouldUseSmallBlocks();
    }



    public boolean isDirectory() {
        return false;
    }



    protected void preWrite() {


    }


}


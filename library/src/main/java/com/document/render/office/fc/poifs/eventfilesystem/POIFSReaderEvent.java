


package com.document.render.office.fc.poifs.eventfilesystem;

import com.document.render.office.fc.poifs.filesystem.DocumentInputStream;
import com.document.render.office.fc.poifs.filesystem.POIFSDocumentPath;



public class POIFSReaderEvent {
    private DocumentInputStream stream;
    private POIFSDocumentPath path;
    private String documentName;



    POIFSReaderEvent(final DocumentInputStream stream,
                     final POIFSDocumentPath path, final String documentName) {
        this.stream = stream;
        this.path = path;
        this.documentName = documentName;
    }



    public DocumentInputStream getStream() {
        return stream;
    }



    public POIFSDocumentPath getPath() {
        return path;
    }



    public String getName() {
        return documentName;
    }
}


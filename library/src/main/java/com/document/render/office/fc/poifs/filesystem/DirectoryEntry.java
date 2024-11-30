


package com.document.render.office.fc.poifs.filesystem;

import com.document.render.office.fc.hpsf.ClassID;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;




public interface DirectoryEntry
        extends Entry, Iterable<Entry> {



    public Iterator<Entry> getEntries();



    public boolean isEmpty();



    public int getEntryCount();



    public boolean hasEntry(final String name);



    public Entry getEntry(final String name)
            throws FileNotFoundException;



    public DocumentEntry createDocument(final String name,
                                        final InputStream stream)
            throws IOException;



    public DocumentEntry createDocument(final String name, final int size,
                                        final POIFSWriterListener writer)
            throws IOException;



    public DirectoryEntry createDirectory(final String name)
            throws IOException;


    public ClassID getStorageClsid();


    public void setStorageClsid(ClassID clsidStorage);

}





package com.document.render.office.fc.poifs.filesystem;

import com.document.render.office.fc.hpsf.ClassID;
import com.document.render.office.fc.poifs.property.DirectoryProperty;
import com.document.render.office.fc.poifs.property.DocumentProperty;
import com.document.render.office.fc.poifs.property.Property;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class DirectoryNode
        extends EntryNode
        implements DirectoryEntry, Iterable<Entry> {


    private Map<String, Entry> _byname;

    private ArrayList<Entry> _entries;



    private POIFSFileSystem _ofilesystem;

    private NPOIFSFileSystem _nfilesystem;


    private POIFSDocumentPath _path;


    DirectoryNode(final DirectoryProperty property,
                  final POIFSFileSystem filesystem,
                  final DirectoryNode parent) {
        this(property, parent, filesystem, (NPOIFSFileSystem) null);
    }


    DirectoryNode(final DirectoryProperty property,
                  final NPOIFSFileSystem nfilesystem,
                  final DirectoryNode parent) {
        this(property, parent, (POIFSFileSystem) null, nfilesystem);
    }

    private DirectoryNode(final DirectoryProperty property,
                          final DirectoryNode parent,
                          final POIFSFileSystem ofilesystem,
                          final NPOIFSFileSystem nfilesystem) {
        super(property, parent);
        this._ofilesystem = ofilesystem;
        this._nfilesystem = nfilesystem;

        if (parent == null) {
            _path = new POIFSDocumentPath();
        } else {
            _path = new POIFSDocumentPath(parent._path, new String[]
                    {
                            property.getName()
                    });
        }
        _byname = new HashMap<String, Entry>();
        _entries = new ArrayList<Entry>();
        Iterator<Property> iter = property.getChildren();

        while (iter.hasNext()) {
            Property child = iter.next();
            Entry childNode = null;

            if (child.isDirectory()) {
                DirectoryProperty childDir = (DirectoryProperty) child;
                if (_ofilesystem != null) {
                    childNode = new DirectoryNode(childDir, _ofilesystem, this);
                } else {
                    childNode = new DirectoryNode(childDir, _nfilesystem, this);
                }
            } else {
                childNode = new DocumentNode((DocumentProperty) child, this);
            }
            _entries.add(childNode);
            _byname.put(childNode.getName(), childNode);
        }
    }



    public POIFSDocumentPath getPath() {
        return _path;
    }


    public POIFSFileSystem getFileSystem() {
        return _ofilesystem;
    }


    public NPOIFSFileSystem getNFileSystem() {
        return _nfilesystem;
    }


    public DocumentInputStream createDocumentInputStream(
            final String documentName)
            throws IOException {
        return createDocumentInputStream(getEntry(documentName));
    }


    public DocumentInputStream createDocumentInputStream(
            final Entry document)
            throws IOException {
        if (!document.isDocumentEntry()) {
            throw new IOException("Entry '" + document.getName()
                    + "' is not a DocumentEntry");
        }

        DocumentEntry entry = (DocumentEntry) document;
        return new DocumentInputStream(entry);
    }


    DocumentEntry createDocument(final POIFSDocument document)
            throws IOException {
        DocumentProperty property = document.getDocumentProperty();
        DocumentNode rval = new DocumentNode(property, this);

        ((DirectoryProperty) getProperty()).addChild(property);
        _ofilesystem.addDocument(document);

        _entries.add(rval);
        _byname.put(property.getName(), rval);
        return rval;
    }


    DocumentEntry createDocument(final NPOIFSDocument document)
            throws IOException {
        DocumentProperty property = document.getDocumentProperty();
        DocumentNode rval = new DocumentNode(property, this);

        ((DirectoryProperty) getProperty()).addChild(property);
        _nfilesystem.addDocument(document);

        _entries.add(rval);
        _byname.put(property.getName(), rval);
        return rval;
    }


    boolean changeName(final String oldName, final String newName) {
        boolean rval = false;
        EntryNode child = (EntryNode) _byname.get(oldName);

        if (child != null) {
            rval = ((DirectoryProperty) getProperty())
                    .changeName(child.getProperty(), newName);
            if (rval) {
                _byname.remove(oldName);
                _byname.put(child.getProperty().getName(), child);
            }
        }
        return rval;
    }



    boolean deleteEntry(final EntryNode entry) {
        boolean rval =
                ((DirectoryProperty) getProperty())
                        .deleteChild(entry.getProperty());

        if (rval) {
            _entries.remove(entry);
            _byname.remove(entry.getName());

            if (_ofilesystem != null) {
                _ofilesystem.remove(entry);
            } else {
                _nfilesystem.remove(entry);
            }
        }
        return rval;
    }





    public Iterator<Entry> getEntries() {
        return _entries.iterator();
    }



    public boolean isEmpty() {
        return _entries.isEmpty();
    }



    public int getEntryCount() {
        return _entries.size();
    }

    public boolean hasEntry(String name) {
        return name != null && _byname.containsKey(name);
    }



    public Entry getEntry(final String name)
            throws FileNotFoundException {
        Entry rval = null;

        if (name != null) {
            rval = _byname.get(name);
        }
        if (rval == null) {


            throw new FileNotFoundException("no such entry: \"" + name
                    + "\"");
        }
        return rval;
    }



    public DocumentEntry createDocument(final String name,
                                        final InputStream stream)
            throws IOException {
        if (_nfilesystem != null) {
            return createDocument(new NPOIFSDocument(name, _nfilesystem, stream));
        } else {
            return createDocument(new POIFSDocument(name, stream));
        }
    }



    public DocumentEntry createDocument(final String name, final int size,
                                        final POIFSWriterListener writer)
            throws IOException {
        return createDocument(new POIFSDocument(name, size, _path, writer));
    }



    public DirectoryEntry createDirectory(final String name)
            throws IOException {
        DirectoryNode rval;
        DirectoryProperty property = new DirectoryProperty(name);

        if (_ofilesystem != null) {
            rval = new DirectoryNode(property, _ofilesystem, this);
            _ofilesystem.addDirectory(property);
        } else {
            rval = new DirectoryNode(property, _nfilesystem, this);
            _nfilesystem.addDirectory(property);
        }

        ((DirectoryProperty) getProperty()).addChild(property);
        _entries.add(rval);
        _byname.put(name, rval);
        return rval;
    }


    public ClassID getStorageClsid() {
        return getProperty().getStorageClsid();
    }


    public void setStorageClsid(ClassID clsidStorage) {
        getProperty().setStorageClsid(clsidStorage);
    }






    public boolean isDirectoryEntry() {
        return true;
    }






    protected boolean isDeleteOK() {


        return isEmpty();
    }






    public Object[] getViewableArray() {
        return new Object[0];
    }


    @SuppressWarnings("unchecked")
    public Iterator getViewableIterator() {
        List components = new ArrayList();

        components.add(getProperty());
        Iterator<Entry> iter = _entries.iterator();
        while (iter.hasNext()) {
            components.add(iter.next());
        }
        return components.iterator();
    }



    public boolean preferArray() {
        return false;
    }



    public String getShortDescription() {
        return getName();
    }


    public Iterator<Entry> iterator() {
        return getEntries();
    }


}


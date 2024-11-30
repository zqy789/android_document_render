

package com.document.render.office.fc.poifs.filesystem;

import com.document.render.office.fc.util.LittleEndianInput;

import java.io.IOException;
import java.io.InputStream;



public class DocumentInputStream extends InputStream implements LittleEndianInput {

    protected static final int EOF = -1;

    protected static final int SIZE_SHORT = 2;
    protected static final int SIZE_INT = 4;
    protected static final int SIZE_LONG = 8;

    private DocumentInputStream delegate;


    protected DocumentInputStream() {
    }


    public DocumentInputStream(DocumentEntry document) throws IOException {
        if (!(document instanceof DocumentNode)) {
            throw new IOException("Cannot open internal document storage");
        }
        DocumentNode documentNode = (DocumentNode) document;
        DirectoryNode parentNode = (DirectoryNode) document.getParent();

        if (documentNode.getDocument() != null) {
            delegate = new ODocumentInputStream(document);
        } else if (parentNode.getFileSystem() != null) {
            delegate = new ODocumentInputStream(document);
        } else if (parentNode.getNFileSystem() != null) {
            delegate = new NDocumentInputStream(document);
        } else {
            throw new IOException("No FileSystem bound on the parent, can't read contents");
        }
    }


    public DocumentInputStream(POIFSDocument document) {
        delegate = new ODocumentInputStream(document);
    }


    public DocumentInputStream(NPOIFSDocument document) {
        delegate = new NDocumentInputStream(document);
    }

    public int available() {
        return delegate.available();
    }

    public void close() {
        delegate.close();
    }

    public void mark(int ignoredReadlimit) {
        delegate.mark(ignoredReadlimit);
    }


    public boolean markSupported() {
        return true;
    }

    public int read() throws IOException {
        return delegate.read();
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return delegate.read(b, off, len);
    }


    public void reset() {
        delegate.reset();
    }

    public long skip(long n) throws IOException {
        return delegate.skip(n);
    }

    public byte readByte() {
        return delegate.readByte();
    }

    public double readDouble() {
        return delegate.readDouble();
    }

    public short readShort() {
        return (short) readUShort();
    }

    public void readFully(byte[] buf) {
        readFully(buf, 0, buf.length);
    }

    public void readFully(byte[] buf, int off, int len) {
        delegate.readFully(buf, off, len);
    }

    public long readLong() {
        return delegate.readLong();
    }

    public int readInt() {
        return delegate.readInt();
    }

    public int readUShort() {
        return delegate.readUShort();
    }

    public int readUByte() {
        return delegate.readUByte();
    }
}

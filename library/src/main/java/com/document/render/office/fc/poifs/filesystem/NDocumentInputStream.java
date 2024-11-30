

package com.document.render.office.fc.poifs.filesystem;

import com.document.render.office.fc.poifs.property.DocumentProperty;
import com.document.render.office.fc.util.LittleEndian;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;



public final class NDocumentInputStream extends DocumentInputStream {

    private int _current_offset;

    private int _current_block_count;


    private int _marked_offset;

    private int _marked_offset_count;


    private int _document_size;


    private boolean _closed;


    private NPOIFSDocument _document;

    private Iterator<ByteBuffer> _data;
    private ByteBuffer _buffer;


    public NDocumentInputStream(DocumentEntry document) throws IOException {
        if (!(document instanceof DocumentNode)) {
            throw new IOException("Cannot open internal document storage, " + document + " not a Document Node");
        }
        _current_offset = 0;
        _current_block_count = 0;
        _marked_offset = 0;
        _marked_offset_count = 0;
        _document_size = document.getSize();
        _closed = false;

        DocumentNode doc = (DocumentNode) document;
        DocumentProperty property = (DocumentProperty) doc.getProperty();
        _document = new NPOIFSDocument(
                property,
                ((DirectoryNode) doc.getParent()).getNFileSystem()
        );
        _data = _document.getBlockIterator();
    }


    public NDocumentInputStream(NPOIFSDocument document) {
        _current_offset = 0;
        _current_block_count = 0;
        _marked_offset = 0;
        _marked_offset_count = 0;
        _document_size = document.getSize();
        _closed = false;
        _document = document;
        _data = _document.getBlockIterator();
    }

    @Override
    public int available() {
        if (_closed) {
            throw new IllegalStateException("cannot perform requested operation on a closed stream");
        }
        return _document_size - _current_offset;
    }

    @Override
    public void close() {
        _closed = true;
    }

    @Override
    public void mark(int ignoredReadlimit) {
        _marked_offset = _current_offset;
        _marked_offset_count = Math.max(0, _current_block_count - 1);
    }

    @Override
    public int read() throws IOException {
        dieIfClosed();
        if (atEOD()) {
            return EOF;
        }
        byte[] b = new byte[1];
        int result = read(b, 0, 1);
        if (result >= 0) {
            if (b[0] < 0) {
                return b[0] + 256;
            }
            return b[0];
        }
        return result;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        dieIfClosed();
        if (b == null) {
            throw new IllegalArgumentException("buffer must not be null");
        }
        if (off < 0 || len < 0 || b.length < off + len) {
            throw new IndexOutOfBoundsException("can't read past buffer boundaries");
        }
        if (len == 0) {
            return 0;
        }
        if (atEOD()) {
            return EOF;
        }
        int limit = Math.min(available(), len);
        readFully(b, off, limit);
        return limit;
    }


    @Override
    public void reset() {

        if (_marked_offset == 0 && _marked_offset_count == 0) {
            _current_block_count = _marked_offset_count;
            _current_offset = _marked_offset;
            _data = _document.getBlockIterator();
            _buffer = null;
            return;
        }


        _data = _document.getBlockIterator();
        _current_offset = 0;
        for (int i = 0; i < _marked_offset_count; i++) {
            _buffer = _data.next();
            _current_offset += _buffer.remaining();
        }

        _current_block_count = _marked_offset_count;


        if (_current_offset != _marked_offset) {

            _buffer = _data.next();
            _current_block_count++;




            int skipBy = _marked_offset - _current_offset;
            _buffer.position(_buffer.position() + skipBy);
        }


        _current_offset = _marked_offset;
    }

    @Override
    public long skip(long n) throws IOException {
        dieIfClosed();
        if (n < 0) {
            return 0;
        }
        int new_offset = _current_offset + (int) n;

        if (new_offset < _current_offset) {

            new_offset = _document_size;
        } else if (new_offset > _document_size) {
            new_offset = _document_size;
        }

        long rval = new_offset - _current_offset;


        byte[] skip = new byte[(int) rval];
        readFully(skip);
        return rval;
    }

    private void dieIfClosed() throws IOException {
        if (_closed) {
            throw new IOException("cannot perform requested operation on a closed stream");
        }
    }

    private boolean atEOD() {
        return _current_offset == _document_size;
    }

    private void checkAvaliable(int requestedSize) {
        if (_closed) {
            throw new IllegalStateException("cannot perform requested operation on a closed stream");
        }
        if (requestedSize > _document_size - _current_offset) {
            throw new RuntimeException("Buffer underrun - requested " + requestedSize
                    + " bytes but " + (_document_size - _current_offset) + " was available");
        }
    }

    @Override
    public void readFully(byte[] buf, int off, int len) {
        checkAvaliable(len);

        int read = 0;
        while (read < len) {
            if (_buffer == null || _buffer.remaining() == 0) {
                _current_block_count++;
                _buffer = _data.next();
            }

            int limit = Math.min(len - read, _buffer.remaining());
            _buffer.get(buf, off + read, limit);
            _current_offset += limit;
            read += limit;
        }
    }

    @Override
    public byte readByte() {
        return (byte) readUByte();
    }

    @Override
    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public long readLong() {
        checkAvaliable(SIZE_LONG);
        byte[] data = new byte[SIZE_LONG];
        readFully(data, 0, SIZE_LONG);
        return LittleEndian.getLong(data, 0);
    }

    @Override
    public short readShort() {
        checkAvaliable(SIZE_SHORT);
        byte[] data = new byte[SIZE_SHORT];
        readFully(data, 0, SIZE_SHORT);
        return LittleEndian.getShort(data);
    }

    @Override
    public int readInt() {
        checkAvaliable(SIZE_INT);
        byte[] data = new byte[SIZE_INT];
        readFully(data, 0, SIZE_INT);
        return LittleEndian.getInt(data);
    }

    @Override
    public int readUShort() {
        checkAvaliable(SIZE_SHORT);
        byte[] data = new byte[SIZE_SHORT];
        readFully(data, 0, SIZE_SHORT);
        return LittleEndian.getUShort(data);
    }

    @Override
    public int readUByte() {
        checkAvaliable(1);
        byte[] data = new byte[1];
        readFully(data, 0, 1);
        if (data[0] >= 0)
            return data[0];
        return data[0] + 256;
    }
}

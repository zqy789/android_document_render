

package com.document.render.office.fc.poifs.filesystem;

import com.document.render.office.fc.poifs.storage.DataInputBlock;

import java.io.IOException;



public final class ODocumentInputStream extends DocumentInputStream {

    private int _current_offset;


    private int _marked_offset;


    private int _document_size;


    private boolean _closed;


    private POIFSDocument _document;


    private DataInputBlock _currentBlock;


    public ODocumentInputStream(DocumentEntry document) throws IOException {
        if (!(document instanceof DocumentNode)) {
            throw new IOException("Cannot open internal document storage");
        }
        DocumentNode documentNode = (DocumentNode) document;
        if (documentNode.getDocument() == null) {
            throw new IOException("Cannot open internal document storage");
        }

        _current_offset = 0;
        _marked_offset = 0;
        _document_size = document.getSize();
        _closed = false;
        _document = documentNode.getDocument();
        _currentBlock = getDataInputBlock(0);
    }


    public ODocumentInputStream(POIFSDocument document) {
        _current_offset = 0;
        _marked_offset = 0;
        _document_size = document.getSize();
        _closed = false;
        _document = document;
        _currentBlock = getDataInputBlock(0);
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
    }

    private DataInputBlock getDataInputBlock(int offset) {
        return _document.getDataInputBlock(offset);
    }

    @Override
    public int read() throws IOException {
        dieIfClosed();
        if (atEOD()) {
            return EOF;
        }
        int result = _currentBlock.readUByte();
        _current_offset++;
        if (_currentBlock.available() < 1) {
            _currentBlock = getDataInputBlock(_current_offset);
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
        _current_offset = _marked_offset;
        _currentBlock = getDataInputBlock(_current_offset);
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

        _current_offset = new_offset;
        _currentBlock = getDataInputBlock(_current_offset);
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
    public byte readByte() {
        return (byte) readUByte();
    }

    @Override
    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public short readShort() {
        return (short) readUShort();
    }

    @Override
    public void readFully(byte[] buf, int off, int len) {
        checkAvaliable(len);
        int blockAvailable = _currentBlock.available();
        if (blockAvailable > len) {
            _currentBlock.readFully(buf, off, len);
            _current_offset += len;
            return;
        }

        int remaining = len;
        int writePos = off;
        while (remaining > 0) {
            boolean blockIsExpiring = remaining >= blockAvailable;
            int reqSize;
            if (blockIsExpiring) {
                reqSize = blockAvailable;
            } else {
                reqSize = remaining;
            }
            _currentBlock.readFully(buf, writePos, reqSize);
            remaining -= reqSize;
            writePos += reqSize;
            _current_offset += reqSize;
            if (blockIsExpiring) {
                if (_current_offset == _document_size) {
                    if (remaining > 0) {
                        throw new IllegalStateException(
                                "reached end of document stream unexpectedly");
                    }
                    _currentBlock = null;
                    break;
                }
                _currentBlock = getDataInputBlock(_current_offset);
                blockAvailable = _currentBlock.available();
            }
        }
    }

    @Override
    public long readLong() {
        checkAvaliable(SIZE_LONG);
        int blockAvailable = _currentBlock.available();
        long result;
        if (blockAvailable > SIZE_LONG) {
            result = _currentBlock.readLongLE();
        } else {
            DataInputBlock nextBlock = getDataInputBlock(_current_offset + blockAvailable);
            if (blockAvailable == SIZE_LONG) {
                result = _currentBlock.readLongLE();
            } else {
                result = nextBlock.readLongLE(_currentBlock, blockAvailable);
            }
            _currentBlock = nextBlock;
        }
        _current_offset += SIZE_LONG;
        return result;
    }

    @Override
    public int readInt() {
        checkAvaliable(SIZE_INT);
        int blockAvailable = _currentBlock.available();
        int result;
        if (blockAvailable > SIZE_INT) {
            result = _currentBlock.readIntLE();
        } else {
            DataInputBlock nextBlock = getDataInputBlock(_current_offset + blockAvailable);
            if (blockAvailable == SIZE_INT) {
                result = _currentBlock.readIntLE();
            } else {
                result = nextBlock.readIntLE(_currentBlock, blockAvailable);
            }
            _currentBlock = nextBlock;
        }
        _current_offset += SIZE_INT;
        return result;
    }

    @Override
    public int readUShort() {
        checkAvaliable(SIZE_SHORT);
        int blockAvailable = _currentBlock.available();
        int result;
        if (blockAvailable > SIZE_SHORT) {
            result = _currentBlock.readUShortLE();
        } else {
            DataInputBlock nextBlock = getDataInputBlock(_current_offset + blockAvailable);
            if (blockAvailable == SIZE_SHORT) {
                result = _currentBlock.readUShortLE();
            } else {
                result = nextBlock.readUShortLE(_currentBlock);
            }
            _currentBlock = nextBlock;
        }
        _current_offset += SIZE_SHORT;
        return result;
    }

    @Override
    public int readUByte() {
        checkAvaliable(1);
        int result = _currentBlock.readUByte();
        _current_offset++;
        if (_currentBlock.available() < 1) {
            _currentBlock = getDataInputBlock(_current_offset);
        }
        return result;
    }
}

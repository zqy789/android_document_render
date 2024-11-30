

package com.document.render.office.fc.poifs.filesystem;

import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.poifs.property.DocumentProperty;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;



public final class NPOIFSDocument {
    private DocumentProperty _property;

    private NPOIFSFileSystem _filesystem;
    private NPOIFSStream _stream;
    private int _block_size;


    public NPOIFSDocument(DocumentProperty property, NPOIFSFileSystem filesystem)
            throws IOException {
        this._property = property;
        this._filesystem = filesystem;

        if (property.getSize() < POIFSConstants.BIG_BLOCK_MINIMUM_DOCUMENT_SIZE) {
            _stream = new NPOIFSStream(_filesystem.getMiniStore(), property.getStartBlock());
            _block_size = _filesystem.getMiniStore().getBlockStoreBlockSize();
        } else {
            _stream = new NPOIFSStream(_filesystem, property.getStartBlock());
            _block_size = _filesystem.getBlockStoreBlockSize();
        }
    }


    public NPOIFSDocument(String name, NPOIFSFileSystem filesystem, InputStream stream)
            throws IOException {
        this._filesystem = filesystem;



        byte[] contents;
        if (stream instanceof ByteArrayInputStream) {
            ByteArrayInputStream bais = (ByteArrayInputStream) stream;
            contents = new byte[bais.available()];
            bais.read(contents);
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(stream, baos);
            contents = baos.toByteArray();
        }


        if (contents.length <= POIFSConstants.BIG_BLOCK_MINIMUM_DOCUMENT_SIZE) {
            _stream = new NPOIFSStream(filesystem.getMiniStore());
            _block_size = _filesystem.getMiniStore().getBlockStoreBlockSize();
        } else {
            _stream = new NPOIFSStream(filesystem);
            _block_size = _filesystem.getBlockStoreBlockSize();
        }


        _stream.updateContents(contents);


        this._property = new DocumentProperty(name, contents.length);
        _property.setStartBlock(_stream.getStartBlock());
    }

    int getDocumentBlockSize() {
        return _block_size;
    }

    Iterator<ByteBuffer> getBlockIterator() {
        if (getSize() > 0) {
            return _stream.getBlockIterator();
        } else {
            List<ByteBuffer> empty = Collections.emptyList();
            return empty.iterator();
        }
    }


    public int getSize() {
        return _property.getSize();
    }


    DocumentProperty getDocumentProperty() {
        return _property;
    }


    public Object[] getViewableArray() {
        Object[] results = new Object[1];
        String result;

        try {
            if (getSize() > 0) {

                byte[] data = new byte[getSize()];
                int offset = 0;
                for (ByteBuffer buffer : _stream) {
                    int length = Math.min(_block_size, data.length - offset);
                    buffer.get(data, offset, length);
                    offset += length;
                }

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                HexDump.dump(data, 0, output, 0);
                result = output.toString();
            } else {
                result = "<NO DATA>";
            }
        } catch (IOException e) {
            result = e.getMessage();
        }
        results[0] = result;
        return results;
    }


    public Iterator getViewableIterator() {
        return Collections.EMPTY_LIST.iterator();
    }


    public boolean preferArray() {
        return true;
    }


    public String getShortDescription() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("Document: \"").append(_property.getName()).append("\"");
        buffer.append(" size = ").append(getSize());
        return buffer.toString();
    }
}

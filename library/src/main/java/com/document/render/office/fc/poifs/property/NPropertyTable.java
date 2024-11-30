

package com.document.render.office.fc.poifs.property;

import com.document.render.office.fc.poifs.common.POIFSBigBlockSize;
import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.poifs.filesystem.NPOIFSFileSystem;
import com.document.render.office.fc.poifs.filesystem.NPOIFSStream;
import com.document.render.office.fc.poifs.storage.HeaderBlock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public final class NPropertyTable extends PropertyTableBase {
    private POIFSBigBlockSize _bigBigBlockSize;

    public NPropertyTable(HeaderBlock headerBlock) {
        super(headerBlock);
        _bigBigBlockSize = headerBlock.getBigBlockSize();
    }


    public NPropertyTable(final HeaderBlock headerBlock,
                          final NPOIFSFileSystem filesystem)
            throws IOException {
        super(
                headerBlock,
                buildProperties(
                        (new NPOIFSStream(filesystem, headerBlock.getPropertyStart())).iterator(),
                        headerBlock.getBigBlockSize()
                )
        );
        _bigBigBlockSize = headerBlock.getBigBlockSize();
    }


    private static List<Property> buildProperties(final Iterator<ByteBuffer> dataSource,
                                                  final POIFSBigBlockSize bigBlockSize) throws IOException {
        List<Property> properties = new ArrayList<Property>();
        while (dataSource.hasNext()) {
            ByteBuffer bb = dataSource.next();


            byte[] data;
            if (bb.hasArray() && bb.arrayOffset() == 0 &&
                    bb.array().length == bigBlockSize.getBigBlockSize()) {
                data = bb.array();
            } else {
                data = new byte[bigBlockSize.getBigBlockSize()];
                bb.get(data, 0, data.length);
            }

            PropertyFactory.convertToProperties(data, properties);
        }
        return properties;
    }


    public int countBlocks() {
        int size = _properties.size() * POIFSConstants.PROPERTY_SIZE;
        return (int) Math.ceil(size / _bigBigBlockSize.getBigBlockSize());
    }


    public void write(NPOIFSStream stream) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (Property property : _properties) {
            if (property != null) {
                property.writeData(baos);
            }
        }
        stream.updateContents(baos.toByteArray());


        if (getStartBlock() != stream.getStartBlock()) {
            setStartBlock(stream.getStartBlock());
        }
    }
}

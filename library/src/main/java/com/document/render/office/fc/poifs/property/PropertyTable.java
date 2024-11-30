

package com.document.render.office.fc.poifs.property;

import com.document.render.office.fc.poifs.common.POIFSBigBlockSize;
import com.document.render.office.fc.poifs.filesystem.POIFSFileSystem;
import com.document.render.office.fc.poifs.storage.BlockWritable;
import com.document.render.office.fc.poifs.storage.HeaderBlock;
import com.document.render.office.fc.poifs.storage.PropertyBlock;
import com.document.render.office.fc.poifs.storage.RawDataBlockList;

import java.io.IOException;
import java.io.OutputStream;



public final class PropertyTable extends PropertyTableBase implements BlockWritable {
    private POIFSBigBlockSize _bigBigBlockSize;
    private BlockWritable[] _blocks;

    public PropertyTable(HeaderBlock headerBlock) {
        super(headerBlock);
        _bigBigBlockSize = headerBlock.getBigBlockSize();
        _blocks = null;
    }


    public PropertyTable(final HeaderBlock headerBlock,
                         final RawDataBlockList blockList)
            throws IOException {
        super(
                headerBlock,
                PropertyFactory.convertToProperties(
                        blockList.fetchBlocks(headerBlock.getPropertyStart(), -1)
                )
        );
        _bigBigBlockSize = headerBlock.getBigBlockSize();
        _blocks = null;
    }


    public void preWrite() {
        Property[] properties = _properties.toArray(new Property[_properties.size()]);


        for (int k = 0; k < properties.length; k++) {
            properties[k].setIndex(k);
        }


        _blocks = PropertyBlock.createPropertyBlockArray(_bigBigBlockSize, _properties);


        for (int k = 0; k < properties.length; k++) {
            properties[k].preWrite();
        }
    }


    public int countBlocks() {
        return (_blocks == null) ? 0
                : _blocks.length;
    }


    public void writeBlocks(final OutputStream stream)
            throws IOException {
        if (_blocks != null) {
            for (int j = 0; j < _blocks.length; j++) {
                _blocks[j].writeBlocks(stream);
            }
        }
    }
}

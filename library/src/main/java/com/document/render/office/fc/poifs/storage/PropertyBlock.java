

package com.document.render.office.fc.poifs.storage;

import com.document.render.office.fc.poifs.common.POIFSBigBlockSize;
import com.document.render.office.fc.poifs.property.Property;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;



public final class PropertyBlock extends BigBlock {
    private Property[] _properties;



    private PropertyBlock(final POIFSBigBlockSize bigBlockSize, final Property[] properties, final int offset) {
        super(bigBlockSize);

        _properties = new Property[bigBlockSize.getPropertiesPerBlock()];
        for (int j = 0; j < _properties.length; j++) {
            _properties[j] = properties[j + offset];
        }
    }



    public static BlockWritable[] createPropertyBlockArray(
            final POIFSBigBlockSize bigBlockSize, final List<Property> properties) {
        int _properties_per_block = bigBlockSize.getPropertiesPerBlock();
        int block_count =
                (properties.size() + _properties_per_block - 1)
                        / _properties_per_block;
        Property[] to_be_written =
                new Property[block_count * _properties_per_block];

        System.arraycopy(properties.toArray(new Property[0]), 0,
                to_be_written, 0, properties.size());
        for (int j = properties.size(); j < to_be_written.length; j++) {



            to_be_written[j] = new Property() {
                protected void preWrite() {
                }

                public boolean isDirectory() {
                    return false;
                }
            };
        }
        BlockWritable[] rvalue = new BlockWritable[block_count];

        for (int j = 0; j < block_count; j++) {
            rvalue[j] = new PropertyBlock(bigBlockSize, to_be_written,
                    j * _properties_per_block);
        }
        return rvalue;
    }





    void writeData(final OutputStream stream)
            throws IOException {
        int _properties_per_block = bigBlockSize.getPropertiesPerBlock();
        for (int j = 0; j < _properties_per_block; j++) {
            _properties[j].writeData(stream);
        }
    }


}


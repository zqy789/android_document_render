


package com.document.render.office.fc.poifs.property;

import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.poifs.storage.ListManagedBlock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;




class PropertyFactory {

    private PropertyFactory() {
    }


    static List<Property> convertToProperties(ListManagedBlock[] blocks)
            throws IOException {
        List<Property> properties = new ArrayList<Property>();

        for (int j = 0; j < blocks.length; j++) {
            byte[] data = blocks[j].getData();
            convertToProperties(data, properties);
        }
        return properties;
    }

    static void convertToProperties(byte[] data, List<Property> properties)
            throws IOException {
        int property_count = data.length / POIFSConstants.PROPERTY_SIZE;
        int offset = 0;

        for (int k = 0; k < property_count; k++) {
            switch (data[offset + PropertyConstants.PROPERTY_TYPE_OFFSET]) {
                case PropertyConstants.DIRECTORY_TYPE:
                    properties.add(
                            new DirectoryProperty(properties.size(), data, offset)
                    );
                    break;

                case PropertyConstants.DOCUMENT_TYPE:
                    properties.add(
                            new DocumentProperty(properties.size(), data, offset)
                    );
                    break;

                case PropertyConstants.ROOT_TYPE:
                    properties.add(
                            new RootProperty(properties.size(), data, offset)
                    );
                    break;

                default:
                    properties.add(null);
                    break;
            }

            offset += POIFSConstants.PROPERTY_SIZE;
        }
    }

}


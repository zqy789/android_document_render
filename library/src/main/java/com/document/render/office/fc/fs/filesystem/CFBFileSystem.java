package com.document.render.office.fc.fs.filesystem;

import com.document.render.office.fc.fs.storage.BlockAllocationTableReader;
import com.document.render.office.fc.fs.storage.BlockList;
import com.document.render.office.fc.fs.storage.HeaderBlock;
import com.document.render.office.fc.fs.storage.RawDataBlock;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;



public class CFBFileSystem {


    private BlockSize bigBlockSize = CFBConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS;

    private boolean isGetThumbnail;


    private Property root;

    private HeaderBlock headerBlock;



    public CFBFileSystem(InputStream stream) throws IOException {
        this(stream, false);
    }


    public CFBFileSystem(InputStream stream, boolean isGetThumbnail) throws IOException {
        this.isGetThumbnail = isGetThumbnail;
        BlockList rawDataBlockList;
        try {

            headerBlock = new HeaderBlock(stream);
            bigBlockSize = headerBlock.getBigBlockSize();

            rawDataBlockList = new BlockList(stream, bigBlockSize);
        } finally {
            stream.close();
        }



        new BlockAllocationTableReader(headerBlock.getBigBlockSize(),
                headerBlock.getBATCount(), headerBlock.getBATArray(),
                headerBlock.getXBATCount(), headerBlock.getXBATIndex(), rawDataBlockList);

        List<Property> properties = new ArrayList<Property>();

        readProperties(rawDataBlockList.fetchBlocks(headerBlock.getPropertyStart(), -1), rawDataBlockList, properties);

        createPropertyTree(root, properties);

        BlockList smallBlockList = readSmallRawDataBlock(rawDataBlockList);

        readPrepertiesRawData(smallBlockList, rawDataBlockList, root);
    }


    private void createPropertyTree(Property directory, List<Property> properties) {
        int index = directory.getChildPropertyIndex();
        if (index < 0) {
            return;
        }
        Property property;
        Stack<Property> children = new Stack<Property>();
        children.push(properties.get(index));
        while (!children.isEmpty()) {
            property = children.pop();
            directory.addChildProperty(property);
            if (property.isDirectory()) {
                createPropertyTree(property, properties);
            }

            index = property.getPreviousPropertyIndex();
            if (index >= 0) {
                children.push(properties.get(index));
            }

            index = property.getNextPropertyIndex();
            if (index >= 0) {
                children.push(properties.get(index));
            }
        }
    }


    private void readProperties(RawDataBlock[] propertyBlocks, BlockList rawBlockList, List<Property> properties) throws IOException {
        for (int j = 0; j < propertyBlocks.length; j++) {
            byte[] data = propertyBlocks[j].getData();
            int property_count = data.length / CFBConstants.PROPERTY_SIZE;
            int offset = 0;

            for (int k = 0; k < property_count; k++) {
                switch (data[offset + Property.PROPERTY_TYPE_OFFSET]) {
                    case Property.DIRECTORY_TYPE:
                        properties.add(new Property(properties.size(), data, offset));
                        break;

                    case Property.DOCUMENT_TYPE:
                        properties.add(new Property(properties.size(), data, offset));
                        break;

                    case Property.ROOT_TYPE:
                        root = new Property(properties.size(), data, offset);
                        properties.add(root);
                        break;
                }
                offset += CFBConstants.PROPERTY_SIZE;
            }
        }
    }


    private BlockList readSmallRawDataBlock(BlockList rawDataBlockList) throws IOException {
        int block_size = 64;
        RawDataBlock[] smallRawDataBlocks = rawDataBlockList.fetchBlocks(root.getStartBlock(), -1);

        int _blocks_per_big_block = headerBlock.getBigBlockSize().getBigBlockSize() / block_size;
        List<RawDataBlock> sdbs = new ArrayList<RawDataBlock>();
        for (int j = 0; j < smallRawDataBlocks.length; j++) {
            byte[] data = smallRawDataBlocks[j].getData();

            for (int k = 0; k < _blocks_per_big_block; k++) {
                byte[] smallData = new byte[block_size];
                System.arraycopy(data, k * block_size, smallData, 0, block_size);
                sdbs.add(new RawDataBlock(smallData));
            }
        }
        BlockList smallBlockList = new BlockList((RawDataBlock[]) sdbs.toArray(new RawDataBlock[sdbs.size()]));
        new BlockAllocationTableReader(bigBlockSize, rawDataBlockList.fetchBlocks(headerBlock.getSBATStart(), -1), smallBlockList);
        return smallBlockList;
    }


    private void readPrepertiesRawData(BlockList smallBlockList, BlockList rawBlockList, Property directory) throws IOException {
        Iterator<Property> ite = directory.properties.values().iterator();
        while (ite.hasNext()) {
            Property property = ite.next();
            if (property.isDocument()) {
                getPropertyRawData(property, smallBlockList, rawBlockList);
            } else if (property.isDirectory()) {
                readPrepertiesRawData(smallBlockList, rawBlockList, property);
            }
        }
    }


    private void getPropertyRawData(Property property, BlockList smallBlockList, BlockList rawBlockList) throws IOException {
        String name = property.getName();
        int startBlock = property.getStartBlock();
        RawDataBlock[] blocks;
        if (property.shouldUseSmallBlocks()) {
            blocks = smallBlockList.fetchBlocks(startBlock, headerBlock.getPropertyStart());
        } else {
            blocks = rawBlockList.fetchBlocks(startBlock, headerBlock.getPropertyStart());
        }
        if (blocks == null || blocks.length == 0) {
            return;
        }

        if (name.equals("Pictures")
                || name.endsWith("WorkBook")
                || name.equals("PowerPoint Document")
                || name.endsWith("Ole")
                || name.endsWith("ObjInfo")
                || name.endsWith("ComObj")
                || name.endsWith("EPRINT")) {
            property.setBlocks(blocks);
            return;
        }
        int bSize = blocks[0].getData().length;
        byte[] b = new byte[blocks.length * bSize];
        int offset = 0;
        for (int i = 0; i < blocks.length; i++) {
            System.arraycopy(blocks[i].getData(), 0, b, offset, bSize);
            offset += bSize;
        }
        property.setDocumentRawData(b);
    }


    public byte[] getPropertyRawData(String propertyName) {
        Property p = getProperty(propertyName);
        if (p != null) {
            return p.getDocumentRawData();
        }
        return null;
    }


    public Property getProperty(String propertyName) {
        return root.getChlidProperty(propertyName);
    }


    public void dispose() {

        if (headerBlock != null) {
            headerBlock.dispose();
            headerBlock = null;
        }
        if (root != null) {
            root.dispose();
        }
    }
}



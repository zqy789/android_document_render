

package com.document.render.office.fc.poifs.filesystem;

import com.document.render.office.fc.poifs.common.POIFSBigBlockSize;
import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.poifs.property.DirectoryProperty;
import com.document.render.office.fc.poifs.property.Property;
import com.document.render.office.fc.poifs.property.PropertyTable;
import com.document.render.office.fc.poifs.storage.BATBlock;
import com.document.render.office.fc.poifs.storage.BlockAllocationTableReader;
import com.document.render.office.fc.poifs.storage.BlockAllocationTableWriter;
import com.document.render.office.fc.poifs.storage.BlockList;
import com.document.render.office.fc.poifs.storage.BlockWritable;
import com.document.render.office.fc.poifs.storage.HeaderBlock;
import com.document.render.office.fc.poifs.storage.HeaderBlockConstants;
import com.document.render.office.fc.poifs.storage.HeaderBlockWriter;
import com.document.render.office.fc.poifs.storage.RawDataBlockList;
import com.document.render.office.fc.poifs.storage.SmallBlockTableReader;
import com.document.render.office.fc.poifs.storage.SmallBlockTableWriter;
import com.document.render.office.fc.util.CloseIgnoringInputStream;
import com.document.render.office.fc.util.IOUtils;
import com.document.render.office.fc.util.LongField;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;




public class POIFSFileSystem {
    private static final POILogger _logger = POILogFactory.getLogger(POIFSFileSystem.class);
    private PropertyTable _property_table;
    private List _documents;
    private DirectoryNode _root;

    private POIFSBigBlockSize bigBlockSize = POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS;


    public POIFSFileSystem() {
        HeaderBlock header_block = new HeaderBlock(bigBlockSize);
        _property_table = new PropertyTable(header_block);
        _documents = new ArrayList();
        _root = null;
    }



    public POIFSFileSystem(InputStream stream) throws IOException {
        this();
        boolean success = false;

        HeaderBlock header_block;
        RawDataBlockList data_blocks;
        try {

            header_block = new HeaderBlock(stream);
            bigBlockSize = header_block.getBigBlockSize();


            data_blocks = new RawDataBlockList(stream, bigBlockSize);
            success = true;
        } finally {
            closeInputStream(stream, success);
        }



        new BlockAllocationTableReader(header_block.getBigBlockSize(), header_block.getBATCount(),
                header_block.getBATArray(), header_block.getXBATCount(), header_block.getXBATIndex(),
                data_blocks);


        PropertyTable properties = new PropertyTable(header_block, data_blocks);


        processProperties(SmallBlockTableReader.getSmallDocumentBlocks(bigBlockSize, data_blocks,
                properties.getRoot(), header_block.getSBATStart()), data_blocks, properties.getRoot()
                .getChildren(), null, header_block.getPropertyStart());


        getRoot().setStorageClsid(properties.getRoot().getStorageClsid());
    }


    public static InputStream createNonClosingInputStream(InputStream is) {
        return new CloseIgnoringInputStream(is);
    }


    public static boolean hasPOIFSHeader(InputStream inp) throws IOException {

        inp.mark(8);

        byte[] header = new byte[8];
        IOUtils.readFully(inp, header);
        LongField signature = new LongField(HeaderBlockConstants._signature_offset, header);


        if (inp instanceof PushbackInputStream) {
            PushbackInputStream pin = (PushbackInputStream) inp;
            pin.unread(header);
        } else {
            inp.reset();
        }


        return (signature.get() == HeaderBlockConstants._signature);
    }



    public static void main(String args[]) throws IOException {
        if (args.length != 2) {
            System.err.println("two arguments required: input filename and output filename");
            System.exit(1);
        }
        FileInputStream istream = new FileInputStream(args[0]);
        FileOutputStream ostream = new FileOutputStream(args[1]);

        new POIFSFileSystem(istream).writeFilesystem(ostream);
        istream.close();
        ostream.close();
    }


    private void closeInputStream(InputStream stream, boolean success) {

        if (stream.markSupported() && !(stream instanceof ByteArrayInputStream)) {
            String msg = "POIFS is closing the supplied input stream of type ("
                    + stream.getClass().getName()
                    + ") which supports mark/reset.  "
                    + "This will be a problem for the caller if the stream will still be used.  "
                    + "If that is the case the caller should wrap the input stream to avoid this close logic.  "
                    + "This warning is only temporary and will not be present in future versions of POI.";
            _logger.log(POILogger.WARN, msg);
        }
        try {
            stream.close();
        } catch (IOException e) {
            if (success) {
                throw new RuntimeException(e);
            }


            e.printStackTrace();
        }
    }



    public DocumentEntry createDocument(final InputStream stream, final String name)
            throws IOException {
        return getRoot().createDocument(name, stream);
    }



    public DocumentEntry createDocument(final String name, final int size,
                                        final POIFSWriterListener writer) throws IOException {
        return getRoot().createDocument(name, size, writer);
    }



    public DirectoryEntry createDirectory(final String name) throws IOException {
        return getRoot().createDirectory(name);
    }



    public void writeFilesystem(final OutputStream stream) throws IOException {


        _property_table.preWrite();


        SmallBlockTableWriter sbtw = new SmallBlockTableWriter(bigBlockSize, _documents,
                _property_table.getRoot());


        BlockAllocationTableWriter bat = new BlockAllocationTableWriter(bigBlockSize);



        List bm_objects = new ArrayList();

        bm_objects.addAll(_documents);
        bm_objects.add(_property_table);
        bm_objects.add(sbtw);
        bm_objects.add(sbtw.getSBAT());



        Iterator iter = bm_objects.iterator();

        while (iter.hasNext()) {
            BATManaged bmo = (BATManaged) iter.next();
            int block_count = bmo.countBlocks();

            if (block_count != 0) {
                bmo.setStartBlock(bat.allocateSpace(block_count));
            } else {




            }
        }



        int batStartBlock = bat.createBlocks();


        HeaderBlockWriter header_block_writer = new HeaderBlockWriter(bigBlockSize);
        BATBlock[] xbat_blocks = header_block_writer.setBATBlocks(bat.countBlocks(), batStartBlock);


        header_block_writer.setPropertyStart(_property_table.getStartBlock());


        header_block_writer.setSBATStart(sbtw.getSBAT().getStartBlock());


        header_block_writer.setSBATBlockCount(sbtw.getSBATBlockCount());






        List writers = new ArrayList();

        writers.add(header_block_writer);
        writers.addAll(_documents);
        writers.add(_property_table);
        writers.add(sbtw);
        writers.add(sbtw.getSBAT());
        writers.add(bat);
        for (int j = 0; j < xbat_blocks.length; j++) {
            writers.add(xbat_blocks[j]);
        }


        iter = writers.iterator();
        while (iter.hasNext()) {
            BlockWritable writer = (BlockWritable) iter.next();

            writer.writeBlocks(stream);
        }
    }



    public DirectoryNode getRoot() {
        if (_root == null) {
            _root = new DirectoryNode(_property_table.getRoot(), this, null);
        }
        return _root;
    }



    public DocumentInputStream createDocumentInputStream(final String documentName)
            throws IOException {
        return getRoot().createDocumentInputStream(documentName);
    }



    void addDocument(final POIFSDocument document) {
        _documents.add(document);
        _property_table.addProperty(document.getDocumentProperty());
    }



    void addDirectory(final DirectoryProperty directory) {
        _property_table.addProperty(directory);
    }



    void remove(EntryNode entry) {
        _property_table.removeProperty(entry.getProperty());
        if (entry.isDocumentEntry()) {
            _documents.remove(((DocumentNode) entry).getDocument());
        }
    }

    private void processProperties(final BlockList small_blocks, final BlockList big_blocks,
                                   final Iterator properties, final DirectoryNode dir, final int headerPropertiesStartAt)
            throws IOException {
        while (properties.hasNext()) {
            Property property = (Property) properties.next();
            String name = property.getName();
            DirectoryNode parent = (dir == null) ? ((DirectoryNode) getRoot()) : dir;

            if (property.isDirectory()) {
                DirectoryNode new_dir = (DirectoryNode) parent.createDirectory(name);

                new_dir.setStorageClsid(property.getStorageClsid());

                processProperties(small_blocks, big_blocks,
                        ((DirectoryProperty) property).getChildren(), new_dir, headerPropertiesStartAt);
            } else {
                int startBlock = property.getStartBlock();
                int size = property.getSize();
                POIFSDocument document = null;

                if (property.shouldUseSmallBlocks()) {
                    document = new POIFSDocument(name, small_blocks.fetchBlocks(startBlock,
                            headerPropertiesStartAt), size);
                } else {
                    document = new POIFSDocument(name, big_blocks.fetchBlocks(startBlock,
                            headerPropertiesStartAt), size);
                }
                parent.createDocument(document);
            }
        }
    }









    public String getShortDescription() {
        return "POIFS FileSystem";
    }


    public int getBigBlockSize() {
        return bigBlockSize.getBigBlockSize();
    }


    public POIFSBigBlockSize getBigBlockSizeDetails() {
        return bigBlockSize;
    }


}


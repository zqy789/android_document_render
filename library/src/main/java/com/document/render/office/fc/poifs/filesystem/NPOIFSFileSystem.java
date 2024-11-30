


package com.document.render.office.fc.poifs.filesystem;

import com.document.render.office.fc.poifs.common.POIFSBigBlockSize;
import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.poifs.nio.ByteArrayBackedDataSource;
import com.document.render.office.fc.poifs.nio.DataSource;
import com.document.render.office.fc.poifs.nio.FileBackedDataSource;
import com.document.render.office.fc.poifs.property.DirectoryProperty;
import com.document.render.office.fc.poifs.property.NPropertyTable;
import com.document.render.office.fc.poifs.storage.BATBlock;
import com.document.render.office.fc.poifs.storage.BATBlock.BATBlockAndIndex;
import com.document.render.office.fc.poifs.storage.BlockAllocationTableReader;
import com.document.render.office.fc.poifs.storage.BlockAllocationTableWriter;
import com.document.render.office.fc.poifs.storage.HeaderBlock;
import com.document.render.office.fc.poifs.storage.HeaderBlockConstants;
import com.document.render.office.fc.poifs.storage.HeaderBlockWriter;
import com.document.render.office.fc.util.CloseIgnoringInputStream;
import com.document.render.office.fc.util.IOUtils;
import com.document.render.office.fc.util.LongField;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;




public class NPOIFSFileSystem extends BlockStore
        implements Closeable {
    private static final POILogger _logger =
            POILogFactory.getLogger(NPOIFSFileSystem.class);
    private NPOIFSMiniStore _mini_store;
    private NPropertyTable _property_table;
    private List<BATBlock> _xbat_blocks;
    private List<BATBlock> _bat_blocks;
    private HeaderBlock _header;
    private DirectoryNode _root;
    private DataSource _data;

    private POIFSBigBlockSize bigBlockSize =
            POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS;

    private NPOIFSFileSystem(boolean newFS) {
        _header = new HeaderBlock(bigBlockSize);
        _property_table = new NPropertyTable(_header);
        _mini_store = new NPOIFSMiniStore(this, _property_table.getRoot(), new ArrayList<BATBlock>(), _header);
        _xbat_blocks = new ArrayList<BATBlock>();
        _bat_blocks = new ArrayList<BATBlock>();
        _root = null;

        if (newFS) {


            _data = new ByteArrayBackedDataSource(new byte[bigBlockSize.getBigBlockSize() * 3]);
        }
    }


    public NPOIFSFileSystem() {
        this(true);


        _header.setBATCount(1);
        _header.setBATArray(new int[]{0});
        _bat_blocks.add(BATBlock.createEmptyBATBlock(bigBlockSize, false));
        setNextBlock(0, POIFSConstants.FAT_SECTOR_BLOCK);


        _property_table.setStartBlock(1);
        setNextBlock(1, POIFSConstants.END_OF_CHAIN);
    }


    public NPOIFSFileSystem(File file)
            throws IOException {
        this(file, true);
    }


    public NPOIFSFileSystem(File file, boolean readOnly)
            throws IOException {
        this(
                (new RandomAccessFile(file, readOnly ? "r" : "rw")).getChannel(),
                true
        );
    }


    public NPOIFSFileSystem(FileChannel channel)
            throws IOException {
        this(channel, false);
    }

    private NPOIFSFileSystem(FileChannel channel, boolean closeChannelOnError)
            throws IOException {
        this(false);

        try {

            ByteBuffer headerBuffer = ByteBuffer.allocate(POIFSConstants.SMALLER_BIG_BLOCK_SIZE);
            IOUtils.readFully(channel, headerBuffer);


            _header = new HeaderBlock(headerBuffer);


            _data = new FileBackedDataSource(channel);
            readCoreContents();
        } catch (IOException e) {
            if (closeChannelOnError) {
                channel.close();
            }
            throw e;
        } catch (RuntimeException e) {



            if (closeChannelOnError) {
                channel.close();
            }
            throw e;
        }
    }



    public NPOIFSFileSystem(InputStream stream)
            throws IOException {
        this(false);

        ReadableByteChannel channel = null;
        boolean success = false;

        try {

            channel = Channels.newChannel(stream);


            ByteBuffer headerBuffer = ByteBuffer.allocate(POIFSConstants.SMALLER_BIG_BLOCK_SIZE);
            IOUtils.readFully(channel, headerBuffer);


            _header = new HeaderBlock(headerBuffer);


            BlockAllocationTableReader.sanityCheckBlockCount(_header.getBATCount());




            int maxSize = BATBlock.calculateMaximumSize(_header);
            ByteBuffer data = ByteBuffer.allocate(maxSize);

            headerBuffer.position(0);
            data.put(headerBuffer);
            data.position(headerBuffer.capacity());

            IOUtils.readFully(channel, data);
            success = true;


            _data = new ByteArrayBackedDataSource(data.array(), data.position());
        } finally {

            if (channel != null)
                channel.close();
            closeInputStream(stream, success);
        }


        readCoreContents();
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



    public static void main(String args[])
            throws IOException {
        if (args.length != 2) {
            System.err.println(
                    "two arguments required: input filename and output filename");
            System.exit(1);
        }
        FileInputStream istream = new FileInputStream(args[0]);
        FileOutputStream ostream = new FileOutputStream(args[1]);

        new NPOIFSFileSystem(istream).writeFilesystem(ostream);
        istream.close();
        ostream.close();
    }


    private void closeInputStream(InputStream stream, boolean success) {
        try {
            stream.close();
        } catch (IOException e) {
            if (success) {
                throw new RuntimeException(e);
            }


            e.printStackTrace();
        }
    }


    private void readCoreContents() throws IOException {

        bigBlockSize = _header.getBigBlockSize();



        ChainLoopDetector loopDetector = getChainLoopDetector();


        for (int fatAt : _header.getBATArray()) {
            readBAT(fatAt, loopDetector);
        }


        BATBlock xfat;
        int nextAt = _header.getXBATIndex();
        for (int i = 0; i < _header.getXBATCount(); i++) {
            loopDetector.claim(nextAt);
            ByteBuffer fatData = getBlockAt(nextAt);
            xfat = BATBlock.createBATBlock(bigBlockSize, fatData);
            xfat.setOurBlockIndex(nextAt);
            nextAt = xfat.getValueAt(bigBlockSize.getXBATEntriesPerBlock());
            _xbat_blocks.add(xfat);

            for (int j = 0; j < bigBlockSize.getXBATEntriesPerBlock(); j++) {
                int fatAt = xfat.getValueAt(j);
                if (fatAt == POIFSConstants.UNUSED_BLOCK) break;
                readBAT(fatAt, loopDetector);
            }
        }



        _property_table = new NPropertyTable(_header, this);


        BATBlock sfat;
        List<BATBlock> sbats = new ArrayList<BATBlock>();
        _mini_store = new NPOIFSMiniStore(this, _property_table.getRoot(), sbats, _header);
        nextAt = _header.getSBATStart();
        for (int i = 0; i < _header.getSBATCount(); i++) {
            loopDetector.claim(nextAt);
            ByteBuffer fatData = getBlockAt(nextAt);
            sfat = BATBlock.createBATBlock(bigBlockSize, fatData);
            sfat.setOurBlockIndex(nextAt);
            sbats.add(sfat);
            nextAt = getNextBlock(nextAt);
        }
    }

    private void readBAT(int batAt, ChainLoopDetector loopDetector) throws IOException {
        loopDetector.claim(batAt);
        ByteBuffer fatData = getBlockAt(batAt);
        BATBlock bat = BATBlock.createBATBlock(bigBlockSize, fatData);
        bat.setOurBlockIndex(batAt);
        _bat_blocks.add(bat);
    }

    private BATBlock createBAT(int offset, boolean isBAT) throws IOException {

        BATBlock newBAT = BATBlock.createEmptyBATBlock(bigBlockSize, !isBAT);
        newBAT.setOurBlockIndex(offset);

        ByteBuffer buffer = ByteBuffer.allocate(bigBlockSize.getBigBlockSize());
        int writeTo = (1 + offset) * bigBlockSize.getBigBlockSize();
        _data.write(buffer, writeTo);

        return newBAT;
    }


    protected ByteBuffer getBlockAt(final int offset) throws IOException {

        long startAt = (offset + 1) * bigBlockSize.getBigBlockSize();
        return _data.read(bigBlockSize.getBigBlockSize(), startAt);
    }


    protected ByteBuffer createBlockIfNeeded(final int offset) throws IOException {
        try {
            return getBlockAt(offset);
        } catch (IndexOutOfBoundsException e) {

            long startAt = (offset + 1) * bigBlockSize.getBigBlockSize();

            ByteBuffer buffer = ByteBuffer.allocate(getBigBlockSize());
            _data.write(buffer, startAt);

            return getBlockAt(offset);
        }
    }


    protected BATBlockAndIndex getBATBlockAndIndex(final int offset) {
        return BATBlock.getBATBlockAndIndex(
                offset, _header, _bat_blocks
        );
    }


    protected int getNextBlock(final int offset) {
        BATBlockAndIndex bai = getBATBlockAndIndex(offset);
        return bai.getBlock().getValueAt(bai.getIndex());
    }


    protected void setNextBlock(final int offset, final int nextBlock) {
        BATBlockAndIndex bai = getBATBlockAndIndex(offset);
        bai.getBlock().setValueAt(
                bai.getIndex(), nextBlock
        );
    }


    protected int getFreeBlock() throws IOException {

        int offset = 0;
        for (int i = 0; i < _bat_blocks.size(); i++) {
            int numSectors = bigBlockSize.getBATEntriesPerBlock();


            BATBlock bat = _bat_blocks.get(i);
            if (bat.hasFreeSectors()) {

                for (int j = 0; j < numSectors; j++) {
                    int batValue = bat.getValueAt(j);
                    if (batValue == POIFSConstants.UNUSED_BLOCK) {

                        return offset + j;
                    }
                }
            }


            offset += numSectors;
        }



        BATBlock bat = createBAT(offset, true);
        bat.setValueAt(0, POIFSConstants.FAT_SECTOR_BLOCK);
        _bat_blocks.add(bat);


        if (_header.getBATCount() >= 109) {

            BATBlock xbat = null;
            for (BATBlock x : _xbat_blocks) {
                if (x.hasFreeSectors()) {
                    xbat = x;
                    break;
                }
            }
            if (xbat == null) {

                xbat = createBAT(offset + 1, false);
                xbat.setValueAt(0, offset);
                bat.setValueAt(1, POIFSConstants.DIFAT_SECTOR_BLOCK);


                offset++;


                if (_xbat_blocks.size() == 0) {
                    _header.setXBATStart(offset);
                } else {
                    _xbat_blocks.get(_xbat_blocks.size() - 1).setValueAt(
                            bigBlockSize.getXBATEntriesPerBlock(), offset
                    );
                }
                _xbat_blocks.add(xbat);
                _header.setXBATCount(_xbat_blocks.size());
            }

            for (int i = 0; i < bigBlockSize.getXBATEntriesPerBlock(); i++) {
                if (xbat.getValueAt(i) == POIFSConstants.UNUSED_BLOCK) {
                    xbat.setValueAt(i, offset);
                }
            }
        } else {

            int[] newBATs = new int[_header.getBATCount() + 1];
            System.arraycopy(_header.getBATArray(), 0, newBATs, 0, newBATs.length - 1);
            newBATs[newBATs.length - 1] = offset;
            _header.setBATArray(newBATs);
        }
        _header.setBATCount(_bat_blocks.size());


        return offset + 1;
    }

    @Override
    protected ChainLoopDetector getChainLoopDetector() throws IOException {
        return new ChainLoopDetector(_data.size());
    }


    NPropertyTable _get_property_table() {
        return _property_table;
    }


    public NPOIFSMiniStore getMiniStore() {
        return _mini_store;
    }


    void addDocument(final NPOIFSDocument document) {
        _property_table.addProperty(document.getDocumentProperty());
    }


    void addDirectory(final DirectoryProperty directory) {
        _property_table.addProperty(directory);
    }



    public DocumentEntry createDocument(final InputStream stream,
                                        final String name)
            throws IOException {
        return getRoot().createDocument(name, stream);
    }



    public DocumentEntry createDocument(final String name, final int size,
                                        final POIFSWriterListener writer)
            throws IOException {
        return getRoot().createDocument(name, size, writer);
    }



    public DirectoryEntry createDirectory(final String name)
            throws IOException {
        return getRoot().createDirectory(name);
    }


    public void writeFilesystem() throws IOException {
        if (_data instanceof FileBackedDataSource) {

        } else {
            throw new IllegalArgumentException(
                    "POIFS opened from an inputstream, so writeFilesystem() may " +
                            "not be called. Use writeFilesystem(OutputStream) instead"
            );
        }
        syncWithDataSource();
    }



    public void writeFilesystem(final OutputStream stream)
            throws IOException {

        syncWithDataSource();


        _data.copyTo(stream);
    }


    private void syncWithDataSource() throws IOException {

        HeaderBlockWriter hbw = new HeaderBlockWriter(_header);
        hbw.writeBlock(getBlockAt(-1));


        for (BATBlock bat : _bat_blocks) {
            ByteBuffer block = getBlockAt(bat.getOurBlockIndex());
            BlockAllocationTableWriter.writeBlock(bat, block);
        }


        _mini_store.syncWithDataSource();


        _property_table.write(
                new NPOIFSStream(this, _header.getPropertyStart())
        );
    }


    public void close() throws IOException {
        _data.close();
    }


    public DirectoryNode getRoot() {
        if (_root == null) {
            _root = new DirectoryNode(_property_table.getRoot(), this, null);
        }
        return _root;
    }



    public DocumentInputStream createDocumentInputStream(
            final String documentName)
            throws IOException {
        return getRoot().createDocumentInputStream(documentName);
    }



    void remove(EntryNode entry) {
        _property_table.removeProperty(entry.getProperty());
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

    protected int getBlockStoreBlockSize() {
        return getBigBlockSize();
    }
}


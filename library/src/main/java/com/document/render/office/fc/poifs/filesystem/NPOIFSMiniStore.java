


package com.document.render.office.fc.poifs.filesystem;

import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.poifs.property.RootProperty;
import com.document.render.office.fc.poifs.storage.BATBlock;
import com.document.render.office.fc.poifs.storage.BATBlock.BATBlockAndIndex;
import com.document.render.office.fc.poifs.storage.BlockAllocationTableWriter;
import com.document.render.office.fc.poifs.storage.HeaderBlock;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;



public class NPOIFSMiniStore extends BlockStore {
    private NPOIFSFileSystem _filesystem;
    private NPOIFSStream _mini_stream;
    private List<BATBlock> _sbat_blocks;
    private HeaderBlock _header;
    private RootProperty _root;

    protected NPOIFSMiniStore(NPOIFSFileSystem filesystem, RootProperty root,
                              List<BATBlock> sbats, HeaderBlock header) {
        this._filesystem = filesystem;
        this._sbat_blocks = sbats;
        this._header = header;
        this._root = root;

        this._mini_stream = new NPOIFSStream(filesystem, root.getStartBlock());
    }


    protected ByteBuffer getBlockAt(final int offset) throws IOException {

        int byteOffset = offset * POIFSConstants.SMALL_BLOCK_SIZE;
        int bigBlockNumber = byteOffset / _filesystem.getBigBlockSize();
        int bigBlockOffset = byteOffset % _filesystem.getBigBlockSize();


        Iterator<ByteBuffer> it = _mini_stream.getBlockIterator();
        for (int i = 0; i < bigBlockNumber; i++) {
            it.next();
        }
        ByteBuffer dataBlock = it.next();
        if (dataBlock == null) {
            throw new IndexOutOfBoundsException("Big block " + bigBlockNumber + " outside stream");
        }


        dataBlock.position(
                dataBlock.position() + bigBlockOffset
        );
        ByteBuffer miniBuffer = dataBlock.slice();
        miniBuffer.limit(POIFSConstants.SMALL_BLOCK_SIZE);
        return miniBuffer;
    }


    protected ByteBuffer createBlockIfNeeded(final int offset) throws IOException {

        try {
            return getBlockAt(offset);
        } catch (IndexOutOfBoundsException e) {





            int newBigBlock = _filesystem.getFreeBlock();
            _filesystem.createBlockIfNeeded(newBigBlock);


            ChainLoopDetector loopDetector = _filesystem.getChainLoopDetector();
            int block = _mini_stream.getStartBlock();
            while (true) {
                loopDetector.claim(block);
                int next = _filesystem.getNextBlock(block);
                if (next == POIFSConstants.END_OF_CHAIN) {
                    break;
                }
                block = next;
            }
            _filesystem.setNextBlock(block, newBigBlock);
            _filesystem.setNextBlock(newBigBlock, POIFSConstants.END_OF_CHAIN);


            return createBlockIfNeeded(offset);
        }
    }


    protected BATBlockAndIndex getBATBlockAndIndex(final int offset) {
        return BATBlock.getSBATBlockAndIndex(
                offset, _header, _sbat_blocks
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
        int sectorsPerSBAT = _filesystem.getBigBlockSizeDetails().getBATEntriesPerBlock();


        int offset = 0;
        for (int i = 0; i < _sbat_blocks.size(); i++) {

            BATBlock sbat = _sbat_blocks.get(i);
            if (sbat.hasFreeSectors()) {

                for (int j = 0; j < sectorsPerSBAT; j++) {
                    int sbatValue = sbat.getValueAt(j);
                    if (sbatValue == POIFSConstants.UNUSED_BLOCK) {

                        return offset + j;
                    }
                }
            }


            offset += sectorsPerSBAT;
        }






        BATBlock newSBAT = BATBlock.createEmptyBATBlock(_filesystem.getBigBlockSizeDetails(), false);
        int batForSBAT = _filesystem.getFreeBlock();
        newSBAT.setOurBlockIndex(batForSBAT);


        if (_header.getSBATCount() == 0) {
            _header.setSBATStart(batForSBAT);
            _header.setSBATBlockCount(1);
        } else {

            ChainLoopDetector loopDetector = _filesystem.getChainLoopDetector();
            int batOffset = _header.getSBATStart();
            while (true) {
                loopDetector.claim(batOffset);
                int nextBat = _filesystem.getNextBlock(batOffset);
                if (nextBat == POIFSConstants.END_OF_CHAIN) {
                    break;
                }
                batOffset = nextBat;
            }


            _filesystem.setNextBlock(batOffset, batForSBAT);


            _header.setSBATBlockCount(
                    _header.getSBATCount() + 1
            );
        }


        _filesystem.setNextBlock(batForSBAT, POIFSConstants.END_OF_CHAIN);
        _sbat_blocks.add(newSBAT);


        return offset;
    }

    @Override
    protected ChainLoopDetector getChainLoopDetector() throws IOException {
        return new ChainLoopDetector(_root.getSize());
    }

    protected int getBlockStoreBlockSize() {
        return POIFSConstants.SMALL_BLOCK_SIZE;
    }


    protected void syncWithDataSource() throws IOException {
        for (BATBlock sbat : _sbat_blocks) {
            ByteBuffer block = _filesystem.getBlockAt(sbat.getOurBlockIndex());
            BlockAllocationTableWriter.writeBlock(sbat, block);
        }
    }
}

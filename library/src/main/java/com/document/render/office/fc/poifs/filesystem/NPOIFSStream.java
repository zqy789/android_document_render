


package com.document.render.office.fc.poifs.filesystem;

import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.poifs.filesystem.BlockStore.ChainLoopDetector;
import com.document.render.office.fc.poifs.property.Property;
import com.document.render.office.fc.poifs.storage.HeaderBlock;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;




public class NPOIFSStream implements Iterable<ByteBuffer> {
    private BlockStore blockStore;
    private int startBlock;


    public NPOIFSStream(BlockStore blockStore, int startBlock) {
        this.blockStore = blockStore;
        this.startBlock = startBlock;
    }


    public NPOIFSStream(BlockStore blockStore) {
        this.blockStore = blockStore;
        this.startBlock = POIFSConstants.END_OF_CHAIN;
    }


    public int getStartBlock() {
        return startBlock;
    }


    public Iterator<ByteBuffer> iterator() {
        return getBlockIterator();
    }

    public Iterator<ByteBuffer> getBlockIterator() {
        if (startBlock == POIFSConstants.END_OF_CHAIN) {
            throw new IllegalStateException(
                    "Can't read from a new stream before it has been written to"
            );
        }
        return new StreamBlockByteBufferIterator(startBlock);
    }


    public void updateContents(byte[] contents) throws IOException {

        int blockSize = blockStore.getBlockStoreBlockSize();
        int blocks = (int) Math.ceil(((double) contents.length) / blockSize);



        ChainLoopDetector loopDetector = blockStore.getChainLoopDetector();


        int prevBlock = POIFSConstants.END_OF_CHAIN;
        int nextBlock = startBlock;
        for (int i = 0; i < blocks; i++) {
            int thisBlock = nextBlock;



            if (thisBlock == POIFSConstants.END_OF_CHAIN) {
                thisBlock = blockStore.getFreeBlock();
                loopDetector.claim(thisBlock);


                nextBlock = POIFSConstants.END_OF_CHAIN;


                if (prevBlock != POIFSConstants.END_OF_CHAIN) {
                    blockStore.setNextBlock(prevBlock, thisBlock);
                }
                blockStore.setNextBlock(thisBlock, POIFSConstants.END_OF_CHAIN);



                if (this.startBlock == POIFSConstants.END_OF_CHAIN) {
                    this.startBlock = thisBlock;
                }
            } else {
                loopDetector.claim(thisBlock);
                nextBlock = blockStore.getNextBlock(thisBlock);
            }


            ByteBuffer buffer = blockStore.createBlockIfNeeded(thisBlock);
            int startAt = i * blockSize;
            int endAt = Math.min(contents.length - startAt, blockSize);
            buffer.put(contents, startAt, endAt);


            prevBlock = thisBlock;
        }
        int lastBlock = prevBlock;


        NPOIFSStream toFree = new NPOIFSStream(blockStore, nextBlock);
        toFree.free(loopDetector);


        blockStore.setNextBlock(lastBlock, POIFSConstants.END_OF_CHAIN);
    }






    public void free() throws IOException {
        ChainLoopDetector loopDetector = blockStore.getChainLoopDetector();
        free(loopDetector);
    }

    private void free(ChainLoopDetector loopDetector) {
        int nextBlock = startBlock;
        while (nextBlock != POIFSConstants.END_OF_CHAIN) {
            int thisBlock = nextBlock;
            loopDetector.claim(thisBlock);
            nextBlock = blockStore.getNextBlock(thisBlock);
            blockStore.setNextBlock(thisBlock, POIFSConstants.UNUSED_BLOCK);
        }
        this.startBlock = POIFSConstants.END_OF_CHAIN;
    }


    protected class StreamBlockByteBufferIterator implements Iterator<ByteBuffer> {
        private ChainLoopDetector loopDetector;
        private int nextBlock;

        protected StreamBlockByteBufferIterator(int firstBlock) {
            this.nextBlock = firstBlock;
            try {
                this.loopDetector = blockStore.getChainLoopDetector();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean hasNext() {
            if (nextBlock == POIFSConstants.END_OF_CHAIN) {
                return false;
            }
            return true;
        }

        public ByteBuffer next() {
            if (nextBlock == POIFSConstants.END_OF_CHAIN) {
                throw new IndexOutOfBoundsException("Can't read past the end of the stream");
            }

            try {
                loopDetector.claim(nextBlock);
                ByteBuffer data = blockStore.getBlockAt(nextBlock);
                nextBlock = blockStore.getNextBlock(nextBlock);
                return data;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}


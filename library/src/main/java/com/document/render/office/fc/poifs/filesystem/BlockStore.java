

package com.document.render.office.fc.poifs.filesystem;

import com.document.render.office.fc.poifs.storage.BATBlock.BATBlockAndIndex;

import java.io.IOException;
import java.nio.ByteBuffer;



public abstract class BlockStore {

    protected abstract int getBlockStoreBlockSize();


    protected abstract ByteBuffer getBlockAt(final int offset) throws IOException;


    protected abstract ByteBuffer createBlockIfNeeded(final int offset) throws IOException;


    protected abstract BATBlockAndIndex getBATBlockAndIndex(final int offset);


    protected abstract int getNextBlock(final int offset);


    protected abstract void setNextBlock(final int offset, final int nextBlock);


    protected abstract int getFreeBlock() throws IOException;


    protected abstract ChainLoopDetector getChainLoopDetector() throws IOException;


    protected class ChainLoopDetector {
        private boolean[] used_blocks;

        protected ChainLoopDetector(long rawSize) {
            int numBlocks = (int) Math.ceil(rawSize / getBlockStoreBlockSize());
            used_blocks = new boolean[numBlocks];
        }

        protected void claim(int offset) {
            if (offset >= used_blocks.length) {



                return;
            }


            if (used_blocks[offset]) {
                throw new IllegalStateException(
                        "Potential loop detected - Block " + offset +
                                " was already claimed but was just requested again"
                );
            }
            used_blocks[offset] = true;
        }
    }
}


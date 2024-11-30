


package com.document.render.office.fc.poifs.storage;

import java.io.IOException;



public interface BlockList {



    public void zap(final int index);



    public ListManagedBlock remove(final int index)
            throws IOException;



    public ListManagedBlock[] fetchBlocks(final int startBlock, final int headerPropertiesStartBlock)
            throws IOException;



    public void setBAT(final BlockAllocationTableReader bat)
            throws IOException;

    public int blockCount();
}


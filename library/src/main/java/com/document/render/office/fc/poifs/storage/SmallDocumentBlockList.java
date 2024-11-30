


package com.document.render.office.fc.poifs.storage;

import java.util.List;



public class SmallDocumentBlockList
        extends BlockListImpl {



    public SmallDocumentBlockList(final List blocks) {
        setBlocks((com.document.render.office.fc.poifs.storage.SmallDocumentBlock[]) blocks
                .toArray(new SmallDocumentBlock[blocks.size()]));
    }
}


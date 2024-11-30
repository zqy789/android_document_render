

package com.document.render.office.fc.hssf.util;


public final class AreaReference extends com.document.render.office.fc.ss.util.AreaReference {
    
    public AreaReference(String reference) {
        super(reference);
    }

    
    public AreaReference(CellReference topLeft, CellReference botRight) {
        super(topLeft, botRight);
    }
}



package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class CreateBrushIndirect extends EMFTag {

    private int index;

    private LogBrush32 brush;

    public CreateBrushIndirect() {
        super(39, 1);
    }

    public CreateBrushIndirect(int index, LogBrush32 brush) {
        this();
        this.index = index;
        this.brush = brush;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        return new CreateBrushIndirect(emf.readDWORD(), new LogBrush32(emf));
    }

    public String toString() {
        return super.toString() + "\n  index: 0x" + Integer.toHexString(index) + "\n"
                + brush.toString();
    }


    public void render(EMFRenderer renderer) {








        renderer.storeGDIObject(index, brush);
    }
}

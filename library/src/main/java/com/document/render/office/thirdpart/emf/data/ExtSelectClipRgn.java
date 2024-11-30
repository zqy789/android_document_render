

package com.document.render.office.thirdpart.emf.data;

import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.IOException;


public class ExtSelectClipRgn extends AbstractClipPath {

    private Region rgn;

    public ExtSelectClipRgn() {
        super(75, 1, EMFConstants.RGN_COPY);
    }

    public ExtSelectClipRgn(int mode, Region rgn) {
        super(75, 1, mode);
        this.rgn = rgn;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        int length = emf.readDWORD();
        int mode = emf.readDWORD();
        return new ExtSelectClipRgn(mode, length > 8 ? new Region(emf) : null);
    }


    public void render(EMFRenderer renderer) {
        if (rgn == null || rgn.getBounds() == null) {
            return;
        }

        render(renderer, rgn.getBounds());
    }
}

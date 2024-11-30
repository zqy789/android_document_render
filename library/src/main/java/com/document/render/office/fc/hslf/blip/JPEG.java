

package com.document.render.office.fc.hslf.blip;

import com.document.render.office.fc.hslf.model.Picture;


public final class JPEG extends Bitmap {


    public int getType() {
        return Picture.JPEG;
    }


    public int getSignature() {
        return 0x46A0;
    }
}

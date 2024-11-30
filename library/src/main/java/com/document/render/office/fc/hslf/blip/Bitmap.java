

package com.document.render.office.fc.hslf.blip;


import com.document.render.office.fc.hslf.usermodel.PictureData;

import java.io.IOException;


public abstract class Bitmap extends PictureData {

    public byte[] getData() {
        byte[] rawdata = getRawData();
        byte[] imgdata = new byte[rawdata.length - 17];
        System.arraycopy(rawdata, 17, imgdata, 0, imgdata.length);
        return imgdata;
    }

    public void setData(byte[] data) throws IOException {

    }
}

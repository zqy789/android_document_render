

package com.document.render.office.fc.hslf.blip;


import com.document.render.office.fc.hslf.model.Picture;
import com.document.render.office.fc.util.LittleEndian;

import java.io.IOException;


public final class DIB extends Bitmap {

    public static final int HEADER_SIZE = 14;

    public static byte[] addBMPHeader(byte[] data) {



        byte[] header = new byte[HEADER_SIZE];

        LittleEndian.putInt(header, 0, 0x4D42);



        int imageSize = LittleEndian.getInt(data, 0x22 - HEADER_SIZE);
        int fileSize = data.length + HEADER_SIZE;
        int offset = fileSize - imageSize;


        LittleEndian.putInt(header, 2, fileSize);

        LittleEndian.putInt(header, 6, 0);

        LittleEndian.putInt(header, 10, offset);


        byte[] dib = new byte[header.length + data.length];
        System.arraycopy(header, 0, dib, 0, header.length);
        System.arraycopy(data, 0, dib, header.length, data.length);

        return dib;
    }


    public int getType() {
        return Picture.DIB;
    }


    public int getSignature() {
        return 0x7A80;
    }

    public byte[] getData() {
        return addBMPHeader(super.getData());
    }

    public void setData(byte[] data) throws IOException {

        byte[] dib = new byte[data.length - HEADER_SIZE];
        System.arraycopy(data, HEADER_SIZE, dib, 0, dib.length);
        super.setData(dib);
    }
}

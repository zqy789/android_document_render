

package com.document.render.office.fc.hslf.blip;

import com.document.render.office.fc.ShapeKit;
import com.document.render.office.fc.hslf.exceptions.HSLFException;
import com.document.render.office.fc.hslf.model.Picture;
import com.document.render.office.java.awt.Dimension;
import com.document.render.office.java.awt.Rectangle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;



public final class PICT extends Metafile {

    public PICT() {
        super();
    }


    public byte[] getData() {
        byte[] rawdata = getRawData();
        try {
            byte[] macheader = new byte[512];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(macheader);
            int pos = CHECKSUM_SIZE;
            byte[] pict;
            try {
                pict = read(rawdata, pos);
            } catch (IOException e) {


                pict = read(rawdata, pos + 16);
            }
            out.write(pict);
            return out.toByteArray();
        } catch (IOException e) {
            throw new HSLFException(e);
        }
    }

    public void setData(byte[] data) throws IOException {
        int pos = 512;
        byte[] compressed = compress(data, pos, data.length - pos);

        Header header = new Header();
        header.wmfsize = data.length - 512;

        header.bounds = new Rectangle(0, 0, 200, 200);
        header.size = new Dimension(header.bounds.width * ShapeKit.EMU_PER_POINT, header.bounds.height
                * ShapeKit.EMU_PER_POINT);
        header.zipsize = compressed.length;

        byte[] checksum = getChecksum(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(checksum);

        out.write(new byte[16]);
        header.write(out);
        out.write(compressed);

        setRawData(out.toByteArray());
    }

    private byte[] read(byte[] data, int pos) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        Header header = new Header();
        header.read(data, pos);
        bis.skip(pos + header.getSize());
        InflaterInputStream inflater = new InflaterInputStream(bis);
        byte[] chunk = new byte[4096];
        int count;
        while ((count = inflater.read(chunk)) >= 0) {
            out.write(chunk, 0, count);
        }
        inflater.close();
        return out.toByteArray();
    }


    public int getType() {
        return Picture.PICT;
    }


    public int getSignature() {
        return 0x5430;
    }

}

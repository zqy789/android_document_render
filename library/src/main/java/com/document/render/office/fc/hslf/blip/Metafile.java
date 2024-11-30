

package com.document.render.office.fc.hslf.blip;

import com.document.render.office.fc.hslf.exceptions.HSLFException;
import com.document.render.office.fc.hslf.usermodel.PictureData;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.java.awt.Dimension;
import com.document.render.office.java.awt.Rectangle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;



public abstract class Metafile extends PictureData {

    protected byte[] compress(byte[] bytes, int offset, int length) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DeflaterOutputStream deflater = new DeflaterOutputStream(out);
        deflater.write(bytes, offset, length);
        deflater.close();
        return out.toByteArray();
    }

    public void writeByte_WMFAndEMF(FileOutputStream out) {
        try {
            byte[] rawdata = getRawData();

            InputStream is = new ByteArrayInputStream(rawdata);
            is.skip(8);

            Header header = new Header();
            header.read(rawdata, CHECKSUM_SIZE);
            is.skip(header.getSize() + CHECKSUM_SIZE);

            InflaterInputStream inflater = new InflaterInputStream(is);
            byte[] chunk = new byte[4096];
            int count;
            while ((count = inflater.read(chunk)) >= 0) {
                out.write(chunk, 0, count);
            }
            inflater.close();
        } catch (IOException e) {
            throw new HSLFException(e);
        }
    }


    public static class Header {


        public int wmfsize;


        public Rectangle bounds;


        public Dimension size;


        public int zipsize;


        public int compression;


        public int filter = 254;

        public void read(byte[] data, int offset) {
            int pos = offset;
            wmfsize = LittleEndian.getInt(data, pos);
            pos += LittleEndian.INT_SIZE;

            int left = LittleEndian.getInt(data, pos);
            pos += LittleEndian.INT_SIZE;
            int top = LittleEndian.getInt(data, pos);
            pos += LittleEndian.INT_SIZE;
            int right = LittleEndian.getInt(data, pos);
            pos += LittleEndian.INT_SIZE;
            int bottom = LittleEndian.getInt(data, pos);
            pos += LittleEndian.INT_SIZE;

            bounds = new Rectangle(left, top, right - left, bottom - top);
            int width = LittleEndian.getInt(data, pos);
            pos += LittleEndian.INT_SIZE;
            int height = LittleEndian.getInt(data, pos);
            pos += LittleEndian.INT_SIZE;

            size = new Dimension(width, height);

            zipsize = LittleEndian.getInt(data, pos);
            pos += LittleEndian.INT_SIZE;

            compression = LittleEndian.getUnsignedByte(data, pos);
            pos++;
            filter = LittleEndian.getUnsignedByte(data, pos);
            pos++;
        }

        public void write(OutputStream out) throws IOException {
            byte[] header = new byte[34];
            int pos = 0;
            LittleEndian.putInt(header, pos, wmfsize);
            pos += LittleEndian.INT_SIZE;

            LittleEndian.putInt(header, pos, bounds.x);
            pos += LittleEndian.INT_SIZE;
            LittleEndian.putInt(header, pos, bounds.y);
            pos += LittleEndian.INT_SIZE;
            LittleEndian.putInt(header, pos, bounds.x + bounds.width);
            pos += LittleEndian.INT_SIZE;
            LittleEndian.putInt(header, pos, bounds.y + bounds.height);
            pos += LittleEndian.INT_SIZE;
            LittleEndian.putInt(header, pos, size.width);
            pos += LittleEndian.INT_SIZE;
            LittleEndian.putInt(header, pos, size.height);
            pos += LittleEndian.INT_SIZE;
            LittleEndian.putInt(header, pos, zipsize);
            pos += LittleEndian.INT_SIZE;

            header[pos] = 0;
            pos++;
            header[pos] = (byte) filter;
            pos++;

            out.write(header);
        }

        public int getSize() {
            return 34;
        }
    }
}

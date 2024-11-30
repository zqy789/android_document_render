

package com.document.render.office.fc.hslf.blip;

import com.document.render.office.fc.ShapeKit;
import com.document.render.office.fc.hslf.exceptions.HSLFException;
import com.document.render.office.fc.hslf.model.Picture;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.java.awt.Dimension;
import com.document.render.office.java.awt.Rectangle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.InflaterInputStream;



public final class WMF extends Metafile {


    public byte[] getData() {
        try {
            byte[] rawdata = getRawData();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
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
            return out.toByteArray();
        } catch (IOException e) {
            throw new HSLFException(e);
        }
    }

    public void setData(byte[] data) throws IOException {
        int pos = 0;
        AldusHeader aldus = new AldusHeader();
        aldus.read(data, pos);
        pos += aldus.getSize();

        byte[] compressed = compress(data, pos, data.length - pos);

        Header header = new Header();
        header.wmfsize = data.length - aldus.getSize();
        header.bounds = new Rectangle((short) aldus.left, (short) aldus.top,
                (short) aldus.right - (short) aldus.left, (short) aldus.bottom - (short) aldus.top);

        int coeff = 96 * ShapeKit.EMU_PER_POINT / aldus.inch;
        header.size = new Dimension(header.bounds.width * coeff, header.bounds.height
                * coeff);
        header.zipsize = compressed.length;

        byte[] checksum = getChecksum(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(checksum);
        header.write(out);
        out.write(compressed);

        setRawData(out.toByteArray());
    }


    public int getType() {
        return Picture.WMF;
    }


    public int getSignature() {
        return 0x2160;
    }


    public class AldusHeader {
        public static final int APMHEADER_KEY = 0x9AC6CDD7;

        public int handle;
        public int left, top, right, bottom;
        public int inch = 72;
        public int reserved;
        public int checksum;

        public void read(byte[] data, int offset) {
            int pos = offset;
            int key = LittleEndian.getInt(data, pos);
            pos += LittleEndian.INT_SIZE;
            if (key != APMHEADER_KEY)
                throw new HSLFException("Not a valid WMF file");

            handle = LittleEndian.getUShort(data, pos);
            pos += LittleEndian.SHORT_SIZE;
            left = LittleEndian.getUShort(data, pos);
            pos += LittleEndian.SHORT_SIZE;
            top = LittleEndian.getUShort(data, pos);
            pos += LittleEndian.SHORT_SIZE;
            right = LittleEndian.getUShort(data, pos);
            pos += LittleEndian.SHORT_SIZE;
            bottom = LittleEndian.getUShort(data, pos);
            pos += LittleEndian.SHORT_SIZE;

            inch = LittleEndian.getUShort(data, pos);
            pos += LittleEndian.SHORT_SIZE;
            reserved = LittleEndian.getInt(data, pos);
            pos += LittleEndian.INT_SIZE;

            checksum = LittleEndian.getShort(data, pos);
            pos += LittleEndian.SHORT_SIZE;
            if (checksum != getChecksum()) {

            }
        }


        public int getChecksum() {
            int checksum = 0;
            checksum ^= (APMHEADER_KEY & 0x0000FFFF);
            checksum ^= ((APMHEADER_KEY & 0xFFFF0000) >> 16);
            checksum ^= left;
            checksum ^= top;
            checksum ^= right;
            checksum ^= bottom;
            checksum ^= inch;
            return checksum;
        }

        public void write(OutputStream out) throws IOException {
            byte[] header = new byte[22];
            int pos = 0;
            LittleEndian.putInt(header, pos, APMHEADER_KEY);
            pos += LittleEndian.INT_SIZE;
            LittleEndian.putUShort(header, pos, 0);
            pos += LittleEndian.SHORT_SIZE;
            LittleEndian.putUShort(header, pos, left);
            pos += LittleEndian.SHORT_SIZE;
            LittleEndian.putUShort(header, pos, top);
            pos += LittleEndian.SHORT_SIZE;
            LittleEndian.putUShort(header, pos, right);
            pos += LittleEndian.SHORT_SIZE;
            LittleEndian.putUShort(header, pos, bottom);
            pos += LittleEndian.SHORT_SIZE;
            LittleEndian.putUShort(header, pos, inch);
            pos += LittleEndian.SHORT_SIZE;
            LittleEndian.putInt(header, pos, 0);
            pos += LittleEndian.INT_SIZE;

            checksum = getChecksum();
            LittleEndian.putUShort(header, pos, checksum);

            out.write(header);
        }

        public int getSize() {
            return 22;
        }
    }

}

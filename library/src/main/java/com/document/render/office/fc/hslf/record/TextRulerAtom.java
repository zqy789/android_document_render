

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;

import java.io.IOException;
import java.io.OutputStream;



public final class TextRulerAtom extends RecordAtom {


    private byte[] _header;


    private byte[] _data;


    private int defaultTabSize;
    private int numLevels;
    private int[] tabStops;
    private int[] bulletOffsets = new int[]{-1, -1, -1, -1, -1};
    private int[] textOffsets = new int[]{-1, -1, -1, -1, -1};


    public TextRulerAtom() {
        _header = new byte[8];
        _data = new byte[0];

        LittleEndian.putShort(_header, 2, (short) getRecordType());
        LittleEndian.putInt(_header, 4, _data.length);
    }


    protected TextRulerAtom(byte[] source, int start, int len) {

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        _data = new byte[len - 8];
        System.arraycopy(source, start + 8, _data, 0, len - 8);

        try {
            read();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public static TextRulerAtom getParagraphInstance() {
        byte[] data = new byte[]{0x00, 0x00, (byte) 0xA6, 0x0F, 0x0A, 0x00, 0x00, 0x00, 0x10, 0x03,
                0x00, 0x00, (byte) 0xF9, 0x00, 0x41, 0x01, 0x41, 0x01};
        TextRulerAtom ruler = new TextRulerAtom(data, 0, data.length);
        return ruler;
    }


    public long getRecordType() {
        return RecordTypes.TextRulerAtom.typeID;
    }


    public void writeOut(OutputStream out) throws IOException {
        out.write(_header);
        out.write(_data);
    }


    private void read() {
        int pos = 0;
        short mask = LittleEndian.getShort(_data);
        pos += 4;
        short val;
        int[] bits = {1, 0, 2, 3, 8, 4, 9, 5, 10, 6, 11, 7, 12};
        for (int i = 0; i < bits.length; i++) {
            if ((mask & 1 << bits[i]) != 0) {
                switch (bits[i]) {
                    case 0:

                        defaultTabSize = LittleEndian.getShort(_data, pos);
                        pos += 2;
                        break;
                    case 1:

                        numLevels = LittleEndian.getShort(_data, pos);
                        pos += 2;
                        break;
                    case 2:

                        val = LittleEndian.getShort(_data, pos);
                        pos += 2;
                        tabStops = new int[val * 2];
                        for (int j = 0; j < tabStops.length; j++) {
                            tabStops[j] = LittleEndian.getUShort(_data, pos);
                            pos += 2;
                        }
                        break;
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:

                        val = LittleEndian.getShort(_data, pos);
                        pos += 2;
                        bulletOffsets[bits[i] - 3] = val;
                        break;
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:

                        val = LittleEndian.getShort(_data, pos);
                        pos += 2;
                        textOffsets[bits[i] - 8] = val;
                        break;
                }
            }
        }
    }


    public int getDefaultTabSize() {
        return defaultTabSize;
    }


    public int getNumberOfLevels() {
        return numLevels;
    }


    public int[] getTabStops() {
        return tabStops;
    }


    public int[] getTextOffsets() {
        return textOffsets;
    }


    public int[] getBulletOffsets() {
        return bulletOffsets;
    }

    public void setParagraphIndent(short tetxOffset, short bulletOffset) {
        LittleEndian.putShort(_data, 4, tetxOffset);
        LittleEndian.putShort(_data, 6, bulletOffset);
        LittleEndian.putShort(_data, 8, bulletOffset);
    }


    public void dispose() {
        _header = null;
        _data = null;
        tabStops = null;
        textOffsets = null;
        bulletOffsets = null;
    }
}

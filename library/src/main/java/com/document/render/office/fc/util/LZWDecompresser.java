
package com.document.render.office.fc.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public abstract class LZWDecompresser {

    private final boolean maskMeansCompressed;

    private final int codeLengthIncrease;

    private final boolean positionIsBigEndian;

    protected LZWDecompresser(boolean maskMeansCompressed,
                              int codeLengthIncrease, boolean positionIsBigEndian) {
        this.maskMeansCompressed = maskMeansCompressed;
        this.codeLengthIncrease = codeLengthIncrease;
        this.positionIsBigEndian = positionIsBigEndian;
    }


    public static byte fromInt(int b) {
        if (b < 128) return (byte) b;
        return (byte) (b - 256);
    }


    public static int fromByte(byte b) {
        if (b >= 0) {
            return b;
        }
        return b + 256;
    }


    protected abstract int populateDictionary(byte[] dict);


    protected abstract int adjustDictionaryOffset(int offset);


    public byte[] decompress(InputStream src) throws IOException {
        ByteArrayOutputStream res = new ByteArrayOutputStream();
        decompress(src, res);
        return res.toByteArray();
    }


    public void decompress(InputStream src, OutputStream res) throws IOException {



        int pos;



        int flag;


        int mask;






        byte[] buffer = new byte[4096];
        pos = populateDictionary(buffer);




        byte[] dataB = new byte[16 + codeLengthIncrease];


        int dataI;

        int dataIPt1, dataIPt2;


        int len, pntr;

        while ((flag = src.read()) != -1) {

            for (mask = 1; mask < 256; mask <<= 1) {


                boolean isMaskSet = (flag & mask) > 0;
                if (isMaskSet ^ maskMeansCompressed) {

                    if ((dataI = src.read()) != -1) {

                        buffer[(pos & 4095)] = fromInt(dataI);
                        pos++;

                        res.write(new byte[]{fromInt(dataI)});
                    }
                } else {


                    dataIPt1 = src.read();
                    dataIPt2 = src.read();
                    if (dataIPt1 == -1 || dataIPt2 == -1) break;





                    len = (dataIPt2 & 15) + codeLengthIncrease;
                    if (positionIsBigEndian) {
                        pntr = (dataIPt1 << 4) + (dataIPt2 >> 4);
                    } else {
                        pntr = dataIPt1 + ((dataIPt2 & 0xF0) << 4);
                    }


                    pntr = adjustDictionaryOffset(pntr);


                    for (int i = 0; i < len; i++) {
                        dataB[i] = buffer[(pntr + i) & 4095];
                        buffer[(pos + i) & 4095] = dataB[i];
                    }
                    res.write(dataB, 0, len);


                    pos = pos + len;
                }
            }
        }
    }
}

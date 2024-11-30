
package com.document.render.office.thirdpart.emf.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;


public class BitInputStream extends DecompressableInputStream {

    final protected static int MASK_SIZE = 8;

    final protected static int ZERO = 0;

    final protected static int ONES = ~0;

    final protected static int[] BIT_MASK = new int[MASK_SIZE];

    final protected static int[] FIELD_MASK = new int[MASK_SIZE];



    static {
        int tempBit = 1;
        int tempField = 1;
        for (int i = 0; i < MASK_SIZE; i++) {


            BIT_MASK[i] = tempBit;
            FIELD_MASK[i] = tempField;


            tempBit <<= 1;
            tempField <<= 1;
            tempField++;
        }
    }


    private int bits;


    private int validBits;


    public BitInputStream(InputStream in) {
        super(in);

        bits = 0;
        validBits = 0;
    }


    protected void fetchByte() throws IOException {

        bits = read();
        if (bits < 0) {
            throw new EOFException();
        }
        validBits = MASK_SIZE;
    }


    public void byteAlign() {
        validBits = 0;
    }


    public boolean readBitFlag() throws IOException {

        if (validBits == 0) {
            fetchByte();
        }
        return ((bits & BIT_MASK[--validBits]) != 0);
    }


    public long readSBits(int n) throws IOException {

        if (n == 0) {
            return 0;
        }
        int value = (readBitFlag()) ? ONES : ZERO;
        value <<= (--n);
        return (value | readUBits(n));
    }


    public float readFBits(int n) throws IOException {

        if (n == 0) {
            return 0.0f;
        }
        return ((float) readSBits(n)) / 0x1000;
    }


    public long readUBits(int n) throws IOException {

        long value = ZERO;
        while (n > 0) {



            if (validBits == 0) {
                fetchByte();
            }
            int nbits = (n > validBits) ? validBits : n;


            int temp = ((bits >> (validBits - nbits)) & FIELD_MASK[nbits - 1]);
            validBits -= nbits;
            n -= nbits;


            value <<= nbits;
            value |= temp;
        }
        return value;
    }
}

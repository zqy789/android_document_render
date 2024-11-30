
package com.document.render.office.thirdpart.emf.io;



import java.io.IOException;
import java.io.OutputStream;


public class EEXECEncryption extends OutputStream implements EEXECConstants {

    private int n, c1, c2, r;

    private OutputStream out;

    private boolean first = true;


    public EEXECEncryption(OutputStream out) {
        this(out, EEXEC_R, N);
    }


    public EEXECEncryption(OutputStream out, int r) {
        this(out, r, N);
    }


    public EEXECEncryption(OutputStream out, int r, int n) {
        this.out = out;
        this.c1 = C1;
        this.c2 = C2;
        this.r = r;
        this.n = n;

    }


    public static int[] encryptString(int[] chars, int r, int n)
            throws IOException {
        IntOutputStream resultStr = new IntOutputStream(chars.length + 4);
        EEXECEncryption eout = new EEXECEncryption(resultStr, r, n);
        for (int i = 0; i < chars.length; i++) {
            eout.write(chars[i]);
        }
        return resultStr.getInts();
    }

    private int encrypt(int plainByte) {
        int cipher = (plainByte ^ (r >>> 8)) % 256;
        r = ((cipher + r) * c1 + c2) % 65536;
        return cipher;
    }

    public void write(int b) throws IOException {
        if (first) {
            for (int i = 0; i < n; i++) {
                out.write(encrypt(0));
            }
            first = false;
        }

        out.write(encrypt(b));
    }

    public void flush() throws IOException {
        super.flush();
        out.flush();
    }

    public void close() throws IOException {
        flush();
        super.close();
        out.close();
    }

    private static class IntOutputStream extends OutputStream {
        int[] chars;

        int i;

        private IntOutputStream(int size) {
            chars = new int[size];
            i = 0;
        }

        public void write(int b) {
            chars[i++] = b;
        }


        private int[] getInts() {
            return chars;
        }
    }
}

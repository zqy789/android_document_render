
package com.document.render.office.thirdpart.emf.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConditionalInputStream extends DecodingInputStream {

    private int[] buffer = new int[4096];

    private int index;

    private int len;

    private InputStream in;

    private Properties defines;

    private int nesting;

    private boolean[] ok = new boolean[50];

    private boolean escape;


    public ConditionalInputStream(InputStream input, Properties defines) {
        super();
        in = input;
        this.defines = defines;
        nesting = 0;
        escape = false;
        index = 0;
        len = 0;
    }

    public int read() throws IOException {

        int b;
        int n;


        if (index < len) {
            b = buffer[index];
            index++;
        } else {
            b = in.read();
        }


        if (b < 0) {
            return -1;
        }


        if (b == '\\') {
            n = in.read();
            if (n == '@') {
                b = ' ';
                escape = true;
            }
            buffer[0] = n;
            index = 0;
            len = 1;
        }


        if (b == '@') {
            if (escape) {
                escape = false;
            } else {

                index = 0;
                StringBuffer s = new StringBuffer();
                n = in.read();
                while ((n >= 0) && !Character.isWhitespace((char) n)) {
                    s.append((char) n);
                    buffer[index] = n;
                    n = in.read();
                    index++;
                }
                buffer[index] = n;
                index++;
                b = ' ';


                String keyword = s.toString();
                if (keyword.equals("ifdef") || keyword.equals("ifndef")) {


                    s = new StringBuffer();
                    n = in.read();
                    while ((n >= 0) && Character.isWhitespace((char) n)) {
                        buffer[index] = n;
                        n = in.read();
                        index++;
                    }
                    while ((n >= 0) && !Character.isWhitespace((char) n)) {
                        s.append((char) n);
                        buffer[index] = n;
                        n = in.read();
                        index++;
                    }
                    buffer[index] = n;
                    index++;


                    String property = s.toString();
                    if (defines.getProperty(property) != null) {
                        ok[nesting] = (nesting > 0 ? ok[nesting - 1] : true)
                                && keyword.equals("ifdef");
                    } else {
                        ok[nesting] = (nesting > 0 ? ok[nesting - 1] : true)
                                && keyword.equals("ifndef");
                    }
                    nesting++;
                    replaceBufferWithWhitespace(index);
                } else if (keyword.equals("else")) {


                    if (nesting <= 0) {
                        throw new RuntimeException(
                                "@else without corresponding @ifdef");
                    }
                    ok[nesting - 1] = (nesting > 1 ? ok[nesting - 2] : true)
                            && !ok[nesting - 1];
                    replaceBufferWithWhitespace(index);
                } else if (keyword.equals("endif")) {

                    if (nesting <= 0) {
                        throw new RuntimeException(
                                "@endif without corresponding @ifdef");
                    }
                    nesting--;
                    replaceBufferWithWhitespace(index);
                } else {

                    b = '@';
                }
                len = index;
                index = 0;
            }
        }

        if ((nesting > 0) && !ok[nesting - 1]) {
            if (!Character.isWhitespace((char) b)) {
                b = ' ';
            }
        }
        return b & 0x00FF;
    }

    private void replaceBufferWithWhitespace(int size) {
        for (int i = 0; i < size; i++) {
            if (!Character.isWhitespace((char) buffer[i])) {
                buffer[i] = ' ';
            }
        }
    }
}

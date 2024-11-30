
package com.document.render.office.thirdpart.emf.font;

import java.io.IOException;


public class TTFNameTable extends TTFTable {

    private int format;

    private int numberOfNameRecords;

    private int stringStorage;

    private String[][] name = new String[4][19];


    public String getTag() {
        return "name";
    }






    public void readTable() throws IOException {

        format = ttf.readUShort();
        numberOfNameRecords = ttf.readUShort();
        stringStorage = ttf.readUShort();

        for (int i = 0; i < numberOfNameRecords; i++) {
            int pid = ttf.readUShort();
            int eid = ttf.readUShort();
            int lid = ttf.readUShort();
            int nid = ttf.readUShort();
            int stringLen = ttf.readUShort();
            int stringOffset = ttf.readUShort();

            ttf.pushPos();
            ttf.seek(stringStorage + stringOffset);
            byte[] b = new byte[stringLen];
            ttf.readFully(b);
            if (pid == 0) {

                name[pid][nid] = new String(b, "UnicodeBig");
            } else if ((pid == 1) && (eid == 0)) {
                if (lid == 0) {

                    name[pid][nid] = new String(b, "ISO8859-1");
                }

            } else if ((pid == 3) && (eid == 1)) {

                if (lid == 0x0409) {

                    name[pid][nid] = new String(b, "UnicodeBig");
                }

            } else {
                System.out.println("Unimplemented PID, EID, LID scheme: " + pid
                        + ", " + eid + ", " + lid);
                System.out.println("NID = " + nid);
                name[pid][nid] = new String(b, "Default");
            }
            ttf.popPos();

        }
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append(super.toString() + "\n");
        s.append("  format: " + format);
        for (int i = 0; i < name.length; i++) {
            for (int j = 0; j < name[i].length; j++) {
                if (name[i][j] != null) {
                    s.append("\n  name[" + i + "][" + j + "]: " + name[i][j]);
                }
            }
        }
        return s.toString();
    }
}

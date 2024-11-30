

package com.document.render.office.thirdpart.emf.font;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class TTFFile extends TTFFont {

    private static final String mode = "r";

    private String fileName;

    private RandomAccessFile ttf;

    private int sfntMajorVersion;

    private int sfntMinorVersion;

    private int numberOfTables;

    private int searchRange;

    private int entrySelector;

    private int rangeShift;

    public TTFFile(String name) throws FileNotFoundException, IOException {
        fileName = name;
        ttf = new RandomAccessFile(name, mode);


        ttf.seek(0);
        sfntMajorVersion = ttf.readUnsignedShort();
        sfntMinorVersion = ttf.readUnsignedShort();
        numberOfTables = ttf.readUnsignedShort();
        searchRange = ttf.readUnsignedShort();
        entrySelector = ttf.readUnsignedShort();
        rangeShift = ttf.readUnsignedShort();


        for (int i = 0; i < numberOfTables; i++) {
            ttf.seek(12 + i * 16);
            byte b[] = new byte[4];
            ttf.readFully(b);
            String tag = new String(b);
            int checksum = ttf.readInt();
            int offset = ttf.readInt();
            int len = ttf.readInt();
            TTFInput input = new TTFFileInput(ttf, offset, len, checksum);
            newTable(tag, input);
        }
    }

    public int getFontVersion() {
        return sfntMajorVersion;
    }

    public void close() throws IOException {
        super.close();
        ttf.close();
    }

    public void show() {
        super.show();

        System.out.println("Font: " + fileName);
        System.out.println("  sfnt: " + sfntMajorVersion + "." + sfntMinorVersion);
        System.out.println("  numTables: " + numberOfTables);
        System.out.println("  searchRange: " + searchRange);
        System.out.println("  entrySelector: " + entrySelector);
        System.out.println("  rangeShift: " + rangeShift);
    }

}

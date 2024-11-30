
package com.document.render.office.thirdpart.emf.font;

import java.io.IOException;


public abstract class TTFTable {

    public static final String[] TT_TAGS = new String[]{"cmap", "glyf",
            "head", "hhea", "hmtx", "loca", "maxp", "name", "OS/2", "post"};

    public static final Class[] TABLE_CLASSES = new Class[]{
            TTFCMapTable.class, TTFGlyfTable.class, TTFHeadTable.class,
            TTFHHeaTable.class, TTFHMtxTable.class, TTFLocaTable.class,
            TTFMaxPTable.class, TTFNameTable.class, TTFOS_2Table.class,
            TTFPostTable.class};
    TTFInput ttf;
    private TTFFont ttfFont;
    private boolean isRead = false;

    public void init(TTFFont font, TTFInput ttf) throws IOException {
        this.ttfFont = font;
        this.ttf = ttf;
    }

    public void read() throws IOException {
        ttf.pushPos();
        System.out.print("[" + getTag());
        ttf.seek(0);
        readTable();
        isRead = true;
        System.out.print("]");
        ttf.popPos();
    }

    public abstract void readTable() throws IOException;

    public abstract String getTag();

    public boolean isRead() {
        return isRead;
    }

    public TTFTable getTable(String tag) throws IOException {
        return ttfFont.getTable(tag);
    }



    public String toString() {
        return ttf + ": [" + getTag() + "/" + getClass().getName() + "]";
    }

}

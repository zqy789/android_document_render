package com.document.render.office.thirdpart.emf.font;

import java.io.IOException;


public class TTFHHeaTable extends TTFVersionTable {

    public short ascender, descender, lineGap;

    public int advanceWidthMax;

    public short minLeftSideBearing, minRightSideBearing;

    public short xMaxExtent;

    public short caretSlopeRise, caretSlopeRun;

    public short metricDataFormat;

    public int numberOfHMetrics;

    public String getTag() {
        return "hhea";
    }

    public void readTable() throws IOException {
        readVersion();

        ascender = ttf.readFWord();
        descender = ttf.readFWord();
        lineGap = ttf.readFWord();

        advanceWidthMax = ttf.readUFWord();
        minLeftSideBearing = ttf.readFWord();
        minRightSideBearing = ttf.readFWord();

        xMaxExtent = ttf.readFWord();

        caretSlopeRise = ttf.readShort();
        caretSlopeRun = ttf.readShort();

        for (int i = 0; i < 5; i++)
            ttf.checkShortZero();

        metricDataFormat = ttf.readShort();
        numberOfHMetrics = ttf.readUShort();
    }

    public String toString() {
        String str = super.toString();
        str += "\n  asc:" + ascender + " desc:" + descender + " lineGap:"
                + lineGap + " maxAdvance:" + advanceWidthMax;
        str += "\n  metricDataFormat:" + metricDataFormat + " #HMetrics:"
                + numberOfHMetrics;
        return str;
    }
}

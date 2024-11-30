

package com.document.render.office.fc.hssf.record.cf;

import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class PatternFormatting implements Cloneable {

    public final static short NO_FILL = 0;

    public final static short SOLID_FOREGROUND = 1;

    public final static short FINE_DOTS = 2;

    public final static short ALT_BARS = 3;

    public final static short SPARSE_DOTS = 4;

    public final static short THICK_HORZ_BANDS = 5;

    public final static short THICK_VERT_BANDS = 6;

    public final static short THICK_BACKWARD_DIAG = 7;

    public final static short THICK_FORWARD_DIAG = 8;

    public final static short BIG_SPOTS = 9;

    public final static short BRICKS = 10;

    public final static short THIN_HORZ_BANDS = 11;

    public final static short THIN_VERT_BANDS = 12;

    public final static short THIN_BACKWARD_DIAG = 13;

    public final static short THIN_FORWARD_DIAG = 14;

    public final static short SQUARES = 15;

    public final static short DIAMONDS = 16;

    public final static short LESS_DOTS = 17;

    public final static short LEAST_DOTS = 18;
    private static final BitField fillPatternStyle = BitFieldFactory.getInstance(0xFC00);
    private static final BitField patternColorIndex = BitFieldFactory.getInstance(0x007F);
    private static final BitField patternBackgroundColorIndex = BitFieldFactory.getInstance(0x3F80);


    private int field_15_pattern_style;
    private int field_16_pattern_color_indexes;


    public PatternFormatting() {
        field_15_pattern_style = 0;
        field_16_pattern_color_indexes = 0;
    }


    public PatternFormatting(LittleEndianInput in) {
        field_15_pattern_style = in.readUShort();
        field_16_pattern_color_indexes = in.readUShort();
    }


    public int getFillPattern() {
        return fillPatternStyle.getValue(field_15_pattern_style);
    }


    public void setFillPattern(int fp) {
        field_15_pattern_style = fillPatternStyle.setValue(field_15_pattern_style, fp);
    }


    public int getFillBackgroundColor() {
        return patternBackgroundColorIndex.getValue(field_16_pattern_color_indexes);
    }


    public void setFillBackgroundColor(int bg) {
        field_16_pattern_color_indexes = patternBackgroundColorIndex.setValue(field_16_pattern_color_indexes, bg);
    }


    public int getFillForegroundColor() {
        return patternColorIndex.getValue(field_16_pattern_color_indexes);
    }


    public void setFillForegroundColor(int fg) {
        field_16_pattern_color_indexes = patternColorIndex.setValue(field_16_pattern_color_indexes, fg);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("    [Pattern Formatting]\n");
        buffer.append("          .fillpattern= ").append(Integer.toHexString(getFillPattern())).append("\n");
        buffer.append("          .fgcoloridx= ").append(Integer.toHexString(getFillForegroundColor())).append("\n");
        buffer.append("          .bgcoloridx= ").append(Integer.toHexString(getFillBackgroundColor())).append("\n");
        buffer.append("    [/Pattern Formatting]\n");
        return buffer.toString();
    }

    public Object clone() {
        PatternFormatting rec = new PatternFormatting();
        rec.field_15_pattern_style = field_15_pattern_style;
        rec.field_16_pattern_color_indexes = field_16_pattern_color_indexes;
        return rec;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(field_15_pattern_style);
        out.writeShort(field_16_pattern_color_indexes);
    }
}

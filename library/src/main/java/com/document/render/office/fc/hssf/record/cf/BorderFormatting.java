

package com.document.render.office.fc.hssf.record.cf;

import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class BorderFormatting {


    public final static short BORDER_NONE = 0x0;

    public final static short BORDER_THIN = 0x1;

    public final static short BORDER_MEDIUM = 0x2;

    public final static short BORDER_DASHED = 0x3;

    public final static short BORDER_HAIR = 0x4;

    public final static short BORDER_THICK = 0x5;

    public final static short BORDER_DOUBLE = 0x6;

    public final static short BORDER_DOTTED = 0x7;

    public final static short BORDER_MEDIUM_DASHED = 0x8;

    public final static short BORDER_DASH_DOT = 0x9;

    public final static short BORDER_MEDIUM_DASH_DOT = 0xA;

    public final static short BORDER_DASH_DOT_DOT = 0xB;

    public final static short BORDER_MEDIUM_DASH_DOT_DOT = 0xC;

    public final static short BORDER_SLANTED_DASH_DOT = 0xD;
    private static final BitField bordLeftLineStyle = BitFieldFactory.getInstance(0x0000000F);
    private static final BitField bordRightLineStyle = BitFieldFactory.getInstance(0x000000F0);
    private static final BitField bordTopLineStyle = BitFieldFactory.getInstance(0x00000F00);
    private static final BitField bordBottomLineStyle = BitFieldFactory.getInstance(0x0000F000);
    private static final BitField bordLeftLineColor = BitFieldFactory.getInstance(0x007F0000);
    private static final BitField bordRightLineColor = BitFieldFactory.getInstance(0x3F800000);
    private static final BitField bordTlBrLineOnOff = BitFieldFactory.getInstance(0x40000000);
    private static final BitField bordBlTrtLineOnOff = BitFieldFactory.getInstance(0x80000000);
    private static final BitField bordTopLineColor = BitFieldFactory.getInstance(0x0000007F);
    private static final BitField bordBottomLineColor = BitFieldFactory.getInstance(0x00003f80);
    private static final BitField bordDiagLineColor = BitFieldFactory.getInstance(0x001FC000);
    private static final BitField bordDiagLineStyle = BitFieldFactory.getInstance(0x01E00000);


    private int field_13_border_styles1;
    private int field_14_border_styles2;


    public BorderFormatting() {
        field_13_border_styles1 = 0;
        field_14_border_styles2 = 0;
    }


    public BorderFormatting(LittleEndianInput in) {
        field_13_border_styles1 = in.readInt();
        field_14_border_styles2 = in.readInt();
    }


    public int getBorderLeft() {
        return bordLeftLineStyle.getValue(field_13_border_styles1);
    }


    public void setBorderLeft(int border) {
        field_13_border_styles1 = bordLeftLineStyle.setValue(field_13_border_styles1, border);
    }


    public int getBorderRight() {
        return bordRightLineStyle.getValue(field_13_border_styles1);
    }


    public void setBorderRight(int border) {
        field_13_border_styles1 = bordRightLineStyle.setValue(field_13_border_styles1, border);
    }


    public int getBorderTop() {
        return bordTopLineStyle.getValue(field_13_border_styles1);
    }


    public void setBorderTop(int border) {
        field_13_border_styles1 = bordTopLineStyle.setValue(field_13_border_styles1, border);
    }


    public int getBorderBottom() {
        return bordBottomLineStyle.getValue(field_13_border_styles1);
    }


    public void setBorderBottom(int border) {
        field_13_border_styles1 = bordBottomLineStyle.setValue(field_13_border_styles1, border);
    }


    public int getBorderDiagonal() {
        return bordDiagLineStyle.getValue(field_14_border_styles2);
    }


    public void setBorderDiagonal(int border) {
        field_14_border_styles2 = bordDiagLineStyle.setValue(field_14_border_styles2, border);
    }


    public int getLeftBorderColor() {
        return bordLeftLineColor.getValue(field_13_border_styles1);
    }


    public void setLeftBorderColor(int color) {
        field_13_border_styles1 = bordLeftLineColor.setValue(field_13_border_styles1, color);
    }


    public int getRightBorderColor() {
        return bordRightLineColor.getValue(field_13_border_styles1);
    }


    public void setRightBorderColor(int color) {
        field_13_border_styles1 = bordRightLineColor.setValue(field_13_border_styles1, color);
    }


    public int getTopBorderColor() {
        return bordTopLineColor.getValue(field_14_border_styles2);
    }


    public void setTopBorderColor(int color) {
        field_14_border_styles2 = bordTopLineColor.setValue(field_14_border_styles2, color);
    }


    public int getBottomBorderColor() {
        return bordBottomLineColor.getValue(field_14_border_styles2);
    }


    public void setBottomBorderColor(int color) {
        field_14_border_styles2 = bordBottomLineColor.setValue(field_14_border_styles2, color);
    }


    public int getDiagonalBorderColor() {
        return bordDiagLineColor.getValue(field_14_border_styles2);
    }


    public void setDiagonalBorderColor(int color) {
        field_14_border_styles2 = bordDiagLineColor.setValue(field_14_border_styles2, color);
    }


    public boolean isForwardDiagonalOn() {
        return bordBlTrtLineOnOff.isSet(field_13_border_styles1);
    }


    public void setForwardDiagonalOn(boolean on) {
        field_13_border_styles1 = bordBlTrtLineOnOff.setBoolean(field_13_border_styles1, on);
    }


    public boolean isBackwardDiagonalOn() {
        return bordTlBrLineOnOff.isSet(field_13_border_styles1);
    }


    public void setBackwardDiagonalOn(boolean on) {
        field_13_border_styles1 = bordTlBrLineOnOff.setBoolean(field_13_border_styles1, on);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("    [Border Formatting]\n");
        buffer.append("          .lftln     = ").append(Integer.toHexString(getBorderLeft())).append("\n");
        buffer.append("          .rgtln     = ").append(Integer.toHexString(getBorderRight())).append("\n");
        buffer.append("          .topln     = ").append(Integer.toHexString(getBorderTop())).append("\n");
        buffer.append("          .btmln     = ").append(Integer.toHexString(getBorderBottom())).append("\n");
        buffer.append("          .leftborder= ").append(Integer.toHexString(getLeftBorderColor())).append("\n");
        buffer.append("          .rghtborder= ").append(Integer.toHexString(getRightBorderColor())).append("\n");
        buffer.append("          .topborder= ").append(Integer.toHexString(getTopBorderColor())).append("\n");
        buffer.append("          .bottomborder= ").append(Integer.toHexString(getBottomBorderColor())).append("\n");
        buffer.append("          .fwdiag= ").append(isForwardDiagonalOn()).append("\n");
        buffer.append("          .bwdiag= ").append(isBackwardDiagonalOn()).append("\n");
        buffer.append("    [/Border Formatting]\n");
        return buffer.toString();
    }

    public Object clone() {
        BorderFormatting rec = new BorderFormatting();
        rec.field_13_border_styles1 = field_13_border_styles1;
        rec.field_14_border_styles2 = field_14_border_styles2;
        return rec;
    }

    public int serialize(int offset, byte[] data) {
        LittleEndian.putInt(data, offset + 0, field_13_border_styles1);
        LittleEndian.putInt(data, offset + 4, field_14_border_styles2);
        return 8;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeInt(field_13_border_styles1);
        out.writeInt(field_14_border_styles2);
    }
}

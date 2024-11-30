

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class WindowTwoRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x023E;


    private static final BitField displayFormulas = BitFieldFactory.getInstance(0x01);
    private static final BitField displayGridlines = BitFieldFactory.getInstance(0x02);
    private static final BitField displayRowColHeadings = BitFieldFactory.getInstance(0x04);
    private static final BitField freezePanes = BitFieldFactory.getInstance(0x08);
    private static final BitField displayZeros = BitFieldFactory.getInstance(0x10);

    private static final BitField defaultHeader = BitFieldFactory.getInstance(0x20);
    private static final BitField arabic = BitFieldFactory.getInstance(0x040);
    private static final BitField displayGuts = BitFieldFactory.getInstance(0x080);
    private static final BitField freezePanesNoSplit = BitFieldFactory.getInstance(0x100);
    private static final BitField selected = BitFieldFactory.getInstance(0x200);
    private static final BitField active = BitFieldFactory.getInstance(0x400);
    private static final BitField savedInPageBreakPreview = BitFieldFactory.getInstance(0x800);



    private short field_1_options;
    private short field_2_top_row;
    private short field_3_left_col;
    private int field_4_header_color;
    private short field_5_page_break_zoom;
    private short field_6_normal_zoom;
    private int field_7_reserved;

    public WindowTwoRecord() {
    }

    public WindowTwoRecord(RecordInputStream in) {
        int size = in.remaining();
        field_1_options = in.readShort();
        field_2_top_row = in.readShort();
        field_3_left_col = in.readShort();
        field_4_header_color = in.readInt();
        if (size > 10) {
            field_5_page_break_zoom = in.readShort();
            field_6_normal_zoom = in.readShort();
        }
        if (size > 14) {
            field_7_reserved = in.readInt();
        }
    }



    public short getOptions() {
        return field_1_options;
    }





    public void setOptions(short options) {
        field_1_options = options;
    }



    public boolean getDisplayFormulas() {
        return displayFormulas.isSet(field_1_options);
    }



    public void setDisplayFormulas(boolean formulas) {
        field_1_options = displayFormulas.setShortBoolean(field_1_options, formulas);
    }



    public boolean getDisplayGridlines() {
        return displayGridlines.isSet(field_1_options);
    }



    public void setDisplayGridlines(boolean gridlines) {
        field_1_options = displayGridlines.setShortBoolean(field_1_options, gridlines);
    }



    public boolean getDisplayRowColHeadings() {
        return displayRowColHeadings.isSet(field_1_options);
    }



    public void setDisplayRowColHeadings(boolean headings) {
        field_1_options = displayRowColHeadings.setShortBoolean(field_1_options, headings);
    }



    public boolean getFreezePanes() {
        return freezePanes.isSet(field_1_options);
    }



    public void setFreezePanes(boolean freezepanes) {
        field_1_options = freezePanes.setShortBoolean(field_1_options, freezepanes);
    }



    public boolean getDisplayZeros() {
        return displayZeros.isSet(field_1_options);
    }



    public void setDisplayZeros(boolean zeros) {
        field_1_options = displayZeros.setShortBoolean(field_1_options, zeros);
    }



    public boolean getDefaultHeader() {
        return defaultHeader.isSet(field_1_options);
    }



    public void setDefaultHeader(boolean header) {
        field_1_options = defaultHeader.setShortBoolean(field_1_options, header);
    }





    public boolean getArabic() {
        return arabic.isSet(field_1_options);
    }



    public void setArabic(boolean isarabic) {
        field_1_options = arabic.setShortBoolean(field_1_options, isarabic);
    }



    public boolean getDisplayGuts() {
        return displayGuts.isSet(field_1_options);
    }



    public void setDisplayGuts(boolean guts) {
        field_1_options = displayGuts.setShortBoolean(field_1_options, guts);
    }



    public boolean getFreezePanesNoSplit() {
        return freezePanesNoSplit.isSet(field_1_options);
    }



    public void setFreezePanesNoSplit(boolean freeze) {
        field_1_options = freezePanesNoSplit.setShortBoolean(field_1_options, freeze);
    }



    public boolean getSelected() {
        return selected.isSet(field_1_options);
    }





    public void setSelected(boolean sel) {
        field_1_options = selected.setShortBoolean(field_1_options, sel);
    }



    public boolean isActive() {
        return active.isSet(field_1_options);
    }


    public void setActive(boolean p) {
        field_1_options = active.setShortBoolean(field_1_options, p);
    }


    public boolean getPaged() {
        return isActive();
    }


    public void setPaged(boolean p) {
        setActive(p);
    }



    public boolean getSavedInPageBreakPreview() {
        return savedInPageBreakPreview.isSet(field_1_options);
    }



    public void setSavedInPageBreakPreview(boolean p) {
        field_1_options = savedInPageBreakPreview.setShortBoolean(field_1_options, p);
    }



    public short getTopRow() {
        return field_2_top_row;
    }



    public void setTopRow(short topRow) {
        field_2_top_row = topRow;
    }



    public short getLeftCol() {
        return field_3_left_col;
    }



    public void setLeftCol(short leftCol) {
        field_3_left_col = leftCol;
    }



    public int getHeaderColor() {
        return field_4_header_color;
    }



    public void setHeaderColor(int color) {
        field_4_header_color = color;
    }





    public short getPageBreakZoom() {
        return field_5_page_break_zoom;
    }



    public void setPageBreakZoom(short zoom) {
        field_5_page_break_zoom = zoom;
    }



    public short getNormalZoom() {
        return field_6_normal_zoom;
    }



    public void setNormalZoom(short zoom) {
        field_6_normal_zoom = zoom;
    }



    public int getReserved() {
        return field_7_reserved;
    }



    public void setReserved(int reserved) {
        field_7_reserved = reserved;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[WINDOW2]\n");
        buffer.append("    .options        = ")
                .append(Integer.toHexString(getOptions())).append("\n");
        buffer.append("       .dispformulas= ").append(getDisplayFormulas())
                .append("\n");
        buffer.append("       .dispgridlins= ").append(getDisplayGridlines())
                .append("\n");
        buffer.append("       .disprcheadin= ")
                .append(getDisplayRowColHeadings()).append("\n");
        buffer.append("       .freezepanes = ").append(getFreezePanes())
                .append("\n");
        buffer.append("       .displayzeros= ").append(getDisplayZeros())
                .append("\n");
        buffer.append("       .defaultheadr= ").append(getDefaultHeader())
                .append("\n");
        buffer.append("       .arabic      = ").append(getArabic())
                .append("\n");
        buffer.append("       .displayguts = ").append(getDisplayGuts())
                .append("\n");
        buffer.append("       .frzpnsnosplt= ")
                .append(getFreezePanesNoSplit()).append("\n");
        buffer.append("       .selected    = ").append(getSelected())
                .append("\n");
        buffer.append("       .active       = ").append(isActive())
                .append("\n");
        buffer.append("       .svdinpgbrkpv= ")
                .append(getSavedInPageBreakPreview()).append("\n");
        buffer.append("    .toprow         = ")
                .append(Integer.toHexString(getTopRow())).append("\n");
        buffer.append("    .leftcol        = ")
                .append(Integer.toHexString(getLeftCol())).append("\n");
        buffer.append("    .headercolor    = ")
                .append(Integer.toHexString(getHeaderColor())).append("\n");
        buffer.append("    .pagebreakzoom  = ")
                .append(Integer.toHexString(getPageBreakZoom())).append("\n");
        buffer.append("    .normalzoom     = ")
                .append(Integer.toHexString(getNormalZoom())).append("\n");
        buffer.append("    .reserved       = ")
                .append(Integer.toHexString(getReserved())).append("\n");
        buffer.append("[/WINDOW2]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getOptions());
        out.writeShort(getTopRow());
        out.writeShort(getLeftCol());
        out.writeInt(getHeaderColor());
        out.writeShort(getPageBreakZoom());
        out.writeShort(getNormalZoom());
        out.writeInt(getReserved());
    }

    protected int getDataSize() {
        return 18;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        WindowTwoRecord rec = new WindowTwoRecord();
        rec.field_1_options = field_1_options;
        rec.field_2_top_row = field_2_top_row;
        rec.field_3_left_col = field_3_left_col;
        rec.field_4_header_color = field_4_header_color;
        rec.field_5_page_break_zoom = field_5_page_break_zoom;
        rec.field_6_normal_zoom = field_6_normal_zoom;
        rec.field_7_reserved = field_7_reserved;
        return rec;
    }
}

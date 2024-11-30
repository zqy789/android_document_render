

package com.document.render.office.fc.hssf.record;


import com.document.render.office.fc.ss.usermodel.PrintSetup;
import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class PrintSetupRecord extends StandardRecord {
    public final static short sid = 0x00A1;
    static final private BitField lefttoright =
            BitFieldFactory.getInstance(0x01);
    static final private BitField landscape =
            BitFieldFactory.getInstance(0x02);
    static final private BitField validsettings = BitFieldFactory.getInstance(
            0x04);


    static final private BitField nocolor =
            BitFieldFactory.getInstance(0x08);
    static final private BitField draft =
            BitFieldFactory.getInstance(0x10);
    static final private BitField notes =
            BitFieldFactory.getInstance(0x20);
    static final private BitField noOrientation =
            BitFieldFactory.getInstance(0x40);
    static final private BitField usepage =
            BitFieldFactory.getInstance(0x80);

    private short field_1_paper_size;
    private short field_2_scale;
    private short field_3_page_start;
    private short field_4_fit_width;
    private short field_5_fit_height;
    private short field_6_options;
    private short field_7_hresolution;
    private short field_8_vresolution;
    private double field_9_headermargin;
    private double field_10_footermargin;
    private short field_11_copies;

    public PrintSetupRecord() {
    }

    public PrintSetupRecord(RecordInputStream in) {
        field_1_paper_size = in.readShort();
        field_2_scale = in.readShort();
        field_3_page_start = in.readShort();
        field_4_fit_width = in.readShort();
        field_5_fit_height = in.readShort();
        field_6_options = in.readShort();
        field_7_hresolution = in.readShort();
        field_8_vresolution = in.readShort();
        field_9_headermargin = in.readDouble();
        field_10_footermargin = in.readDouble();
        field_11_copies = in.readShort();
    }

    public short getPaperSize() {
        return field_1_paper_size;
    }

    public void setPaperSize(short size) {
        field_1_paper_size = size;
    }

    public short getScale() {
        return field_2_scale;
    }

    public void setScale(short scale) {
        field_2_scale = scale;
    }

    public short getPageStart() {
        return field_3_page_start;
    }

    public void setPageStart(short start) {
        field_3_page_start = start;
    }

    public short getFitWidth() {
        return field_4_fit_width;
    }

    public void setFitWidth(short width) {
        field_4_fit_width = width;
    }

    public short getFitHeight() {
        return field_5_fit_height;
    }

    public void setFitHeight(short height) {
        field_5_fit_height = height;
    }

    public short getOptions() {
        return field_6_options;
    }

    public void setOptions(short options) {
        field_6_options = options;
    }


    public boolean getLeftToRight() {
        return lefttoright.isSet(field_6_options);
    }


    public void setLeftToRight(boolean ltor) {
        field_6_options = lefttoright.setShortBoolean(field_6_options, ltor);
    }

    public boolean getLandscape() {
        return landscape.isSet(field_6_options);
    }

    public void setLandscape(boolean ls) {
        field_6_options = landscape.setShortBoolean(field_6_options, ls);
    }

    public boolean getValidSettings() {
        return validsettings.isSet(field_6_options);
    }

    public void setValidSettings(boolean valid) {
        field_6_options = validsettings.setShortBoolean(field_6_options, valid);
    }

    public boolean getNoColor() {
        return nocolor.isSet(field_6_options);
    }

    public void setNoColor(boolean mono) {
        field_6_options = nocolor.setShortBoolean(field_6_options, mono);
    }

    public boolean getDraft() {
        return draft.isSet(field_6_options);
    }

    public void setDraft(boolean d) {
        field_6_options = draft.setShortBoolean(field_6_options, d);
    }

    public boolean getNotes() {
        return notes.isSet(field_6_options);
    }

    public void setNotes(boolean printnotes) {
        field_6_options = notes.setShortBoolean(field_6_options, printnotes);
    }

    public boolean getNoOrientation() {
        return noOrientation.isSet(field_6_options);
    }

    public void setNoOrientation(boolean orientation) {
        field_6_options = noOrientation.setShortBoolean(field_6_options, orientation);
    }

    public boolean getUsePage() {
        return usepage.isSet(field_6_options);
    }

    public void setUsePage(boolean page) {
        field_6_options = usepage.setShortBoolean(field_6_options, page);
    }


    public short getHResolution() {
        return field_7_hresolution;
    }


    public void setHResolution(short resolution) {
        field_7_hresolution = resolution;
    }

    public short getVResolution() {
        return field_8_vresolution;
    }

    public void setVResolution(short resolution) {
        field_8_vresolution = resolution;
    }

    public double getHeaderMargin() {
        return field_9_headermargin;
    }

    public void setHeaderMargin(double headermargin) {
        field_9_headermargin = headermargin;
    }

    public double getFooterMargin() {
        return field_10_footermargin;
    }

    public void setFooterMargin(double footermargin) {
        field_10_footermargin = footermargin;
    }

    public short getCopies() {
        return field_11_copies;
    }

    public void setCopies(short copies) {
        field_11_copies = copies;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[PRINTSETUP]\n");
        buffer.append("    .papersize      = ").append(getPaperSize())
                .append("\n");
        buffer.append("    .scale          = ").append(getScale())
                .append("\n");
        buffer.append("    .pagestart      = ").append(getPageStart())
                .append("\n");
        buffer.append("    .fitwidth       = ").append(getFitWidth())
                .append("\n");
        buffer.append("    .fitheight      = ").append(getFitHeight())
                .append("\n");
        buffer.append("    .options        = ").append(getOptions())
                .append("\n");
        buffer.append("        .ltor       = ").append(getLeftToRight())
                .append("\n");
        buffer.append("        .landscape  = ").append(getLandscape())
                .append("\n");
        buffer.append("        .valid      = ").append(getValidSettings())
                .append("\n");
        buffer.append("        .mono       = ").append(getNoColor())
                .append("\n");
        buffer.append("        .draft      = ").append(getDraft())
                .append("\n");
        buffer.append("        .notes      = ").append(getNotes())
                .append("\n");
        buffer.append("        .noOrientat = ").append(getNoOrientation())
                .append("\n");
        buffer.append("        .usepage    = ").append(getUsePage())
                .append("\n");
        buffer.append("    .hresolution    = ").append(getHResolution())
                .append("\n");
        buffer.append("    .vresolution    = ").append(getVResolution())
                .append("\n");
        buffer.append("    .headermargin   = ").append(getHeaderMargin())
                .append("\n");
        buffer.append("    .footermargin   = ").append(getFooterMargin())
                .append("\n");
        buffer.append("    .copies         = ").append(getCopies())
                .append("\n");
        buffer.append("[/PRINTSETUP]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getPaperSize());
        out.writeShort(getScale());
        out.writeShort(getPageStart());
        out.writeShort(getFitWidth());
        out.writeShort(getFitHeight());
        out.writeShort(getOptions());
        out.writeShort(getHResolution());
        out.writeShort(getVResolution());
        out.writeDouble(getHeaderMargin());
        out.writeDouble(getFooterMargin());
        out.writeShort(getCopies());
    }

    protected int getDataSize() {
        return 34;
    }

    public short getSid() {
        return sid;
    }

    public Object clone() {
        PrintSetupRecord rec = new PrintSetupRecord();
        rec.field_1_paper_size = field_1_paper_size;
        rec.field_2_scale = field_2_scale;
        rec.field_3_page_start = field_3_page_start;
        rec.field_4_fit_width = field_4_fit_width;
        rec.field_5_fit_height = field_5_fit_height;
        rec.field_6_options = field_6_options;
        rec.field_7_hresolution = field_7_hresolution;
        rec.field_8_vresolution = field_8_vresolution;
        rec.field_9_headermargin = field_9_headermargin;
        rec.field_10_footermargin = field_10_footermargin;
        rec.field_11_copies = field_11_copies;
        return rec;
    }
}

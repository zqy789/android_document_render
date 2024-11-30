

package com.document.render.office.fc.hwpf.model.types;


import com.document.render.office.fc.hwpf.model.HDFType;
import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.Internal;
import com.document.render.office.fc.util.LittleEndian;



@Internal
public abstract class FIBAbstractType implements HDFType {

    private static BitField fDot = BitFieldFactory.getInstance(0x0001);
    private static BitField fGlsy = BitFieldFactory.getInstance(0x0002);
    private static BitField fComplex = BitFieldFactory.getInstance(0x0004);
    private static BitField fHasPic = BitFieldFactory.getInstance(0x0008);
    private static BitField cQuickSaves = BitFieldFactory.getInstance(0x00F0);
    private static BitField fEncrypted = BitFieldFactory.getInstance(0x0100);
    private static BitField fWhichTblStm = BitFieldFactory.getInstance(0x0200);
    private static BitField fReadOnlyRecommended = BitFieldFactory.getInstance(0x0400);
    private static BitField fWriteReservation = BitFieldFactory.getInstance(0x0800);
    private static BitField fExtChar = BitFieldFactory.getInstance(0x1000);
    private static BitField fLoadOverride = BitFieldFactory.getInstance(0x2000);
    private static BitField fFarEast = BitFieldFactory.getInstance(0x4000);
    private static BitField fCrypto = BitFieldFactory.getInstance(0x8000);
    private static BitField fMac = BitFieldFactory.getInstance(0x0001);
    private static BitField fEmptySpecial = BitFieldFactory.getInstance(0x0002);
    private static BitField fLoadOverridePage = BitFieldFactory.getInstance(0x0004);
    private static BitField fFutureSavedUndo = BitFieldFactory.getInstance(0x0008);
    private static BitField fWord97Saved = BitFieldFactory.getInstance(0x0010);
    private static BitField fSpare0 = BitFieldFactory.getInstance(0x00FE);
    protected int field_1_wIdent;
    protected int field_2_nFib;
    protected int field_3_nProduct;
    protected int field_4_lid;
    protected int field_5_pnNext;
    protected short field_6_options;
    protected int field_7_nFibBack;
    protected int field_8_lKey;
    protected int field_9_envr;
    protected short field_10_history;
    protected int field_11_chs;

    protected int field_12_chsTables;

    protected int field_13_fcMin;

    protected int field_14_fcMac;




    public FIBAbstractType() {

    }

    protected void fillFields(byte[] data, int offset) {
        field_1_wIdent = LittleEndian.getShort(data, 0x0 + offset);
        field_2_nFib = LittleEndian.getShort(data, 0x2 + offset);
        field_3_nProduct = LittleEndian.getShort(data, 0x4 + offset);
        field_4_lid = LittleEndian.getShort(data, 0x6 + offset);
        field_5_pnNext = LittleEndian.getShort(data, 0x8 + offset);
        field_6_options = LittleEndian.getShort(data, 0xa + offset);
        field_7_nFibBack = LittleEndian.getShort(data, 0xc + offset);
        field_8_lKey = LittleEndian.getShort(data, 0xe + offset);
        field_9_envr = LittleEndian.getShort(data, 0x10 + offset);
        field_10_history = LittleEndian.getShort(data, 0x12 + offset);
        field_11_chs = LittleEndian.getShort(data, 0x14 + offset);
        field_12_chsTables = LittleEndian.getShort(data, 0x16 + offset);
        field_13_fcMin = LittleEndian.getInt(data, 0x18 + offset);
        field_14_fcMac = LittleEndian.getInt(data, 0x1c + offset);
    }

    public void serialize(byte[] data, int offset) {
        LittleEndian.putShort(data, 0x0 + offset, (short) field_1_wIdent);
        LittleEndian.putShort(data, 0x2 + offset, (short) field_2_nFib);
        LittleEndian.putShort(data, 0x4 + offset, (short) field_3_nProduct);
        LittleEndian.putShort(data, 0x6 + offset, (short) field_4_lid);
        LittleEndian.putShort(data, 0x8 + offset, (short) field_5_pnNext);
        LittleEndian.putShort(data, 0xa + offset, field_6_options);
        LittleEndian.putShort(data, 0xc + offset, (short) field_7_nFibBack);
        LittleEndian.putShort(data, 0xe + offset, (short) field_8_lKey);
        LittleEndian.putShort(data, 0x10 + offset, (short) field_9_envr);
        LittleEndian.putShort(data, 0x12 + offset, field_10_history);
        LittleEndian.putShort(data, 0x14 + offset, (short) field_11_chs);
        LittleEndian.putShort(data, 0x16 + offset, (short) field_12_chsTables);
        LittleEndian.putInt(data, 0x18 + offset, field_13_fcMin);
        LittleEndian.putInt(data, 0x1c + offset, field_14_fcMac);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[FIB]\n");

        buffer.append("    .wIdent               = ");
        buffer.append(" (").append(getWIdent()).append(" )\n");

        buffer.append("    .nFib                 = ");
        buffer.append(" (").append(getNFib()).append(" )\n");

        buffer.append("    .nProduct             = ");
        buffer.append(" (").append(getNProduct()).append(" )\n");

        buffer.append("    .lid                  = ");
        buffer.append(" (").append(getLid()).append(" )\n");

        buffer.append("    .pnNext               = ");
        buffer.append(" (").append(getPnNext()).append(" )\n");

        buffer.append("    .options              = ");
        buffer.append(" (").append(getOptions()).append(" )\n");
        buffer.append("         .fDot                     = ").append(isFDot()).append('\n');
        buffer.append("         .fGlsy                    = ").append(isFGlsy()).append('\n');
        buffer.append("         .fComplex                 = ").append(isFComplex()).append('\n');
        buffer.append("         .fHasPic                  = ").append(isFHasPic()).append('\n');
        buffer.append("         .cQuickSaves              = ").append(getCQuickSaves()).append('\n');
        buffer.append("         .fEncrypted               = ").append(isFEncrypted()).append('\n');
        buffer.append("         .fWhichTblStm             = ").append(isFWhichTblStm()).append('\n');
        buffer.append("         .fReadOnlyRecommended     = ").append(isFReadOnlyRecommended()).append('\n');
        buffer.append("         .fWriteReservation        = ").append(isFWriteReservation()).append('\n');
        buffer.append("         .fExtChar                 = ").append(isFExtChar()).append('\n');
        buffer.append("         .fLoadOverride            = ").append(isFLoadOverride()).append('\n');
        buffer.append("         .fFarEast                 = ").append(isFFarEast()).append('\n');
        buffer.append("         .fCrypto                  = ").append(isFCrypto()).append('\n');

        buffer.append("    .nFibBack             = ");
        buffer.append(" (").append(getNFibBack()).append(" )\n");

        buffer.append("    .lKey                 = ");
        buffer.append(" (").append(getLKey()).append(" )\n");

        buffer.append("    .envr                 = ");
        buffer.append(" (").append(getEnvr()).append(" )\n");

        buffer.append("    .history              = ");
        buffer.append(" (").append(getHistory()).append(" )\n");
        buffer.append("         .fMac                     = ").append(isFMac()).append('\n');
        buffer.append("         .fEmptySpecial            = ").append(isFEmptySpecial()).append('\n');
        buffer.append("         .fLoadOverridePage        = ").append(isFLoadOverridePage()).append('\n');
        buffer.append("         .fFutureSavedUndo         = ").append(isFFutureSavedUndo()).append('\n');
        buffer.append("         .fWord97Saved             = ").append(isFWord97Saved()).append('\n');
        buffer.append("         .fSpare0                  = ").append(getFSpare0()).append('\n');

        buffer.append("    .chs                  = ");
        buffer.append(" (").append(getChs()).append(" )\n");

        buffer.append("    .chsTables            = ");
        buffer.append(" (").append(getChsTables()).append(" )\n");

        buffer.append("    .fcMin                = ");
        buffer.append(" (").append(getFcMin()).append(" )\n");

        buffer.append("    .fcMac                = ");
        buffer.append(" (").append(getFcMac()).append(" )\n");

        buffer.append("[/FIB]\n");
        return buffer.toString();
    }


    public int getSize() {
        return 2 + 2 + 2 + 2 + 2 + 2 + 2 + 2 + 2 + 2 + 2 + 2 + 4 + 4;
    }



    public int getWIdent() {
        return field_1_wIdent;
    }


    public void setWIdent(int field_1_wIdent) {
        this.field_1_wIdent = field_1_wIdent;
    }


    public int getNFib() {
        return field_2_nFib;
    }


    public void setNFib(int field_2_nFib) {
        this.field_2_nFib = field_2_nFib;
    }


    public int getNProduct() {
        return field_3_nProduct;
    }


    public void setNProduct(int field_3_nProduct) {
        this.field_3_nProduct = field_3_nProduct;
    }


    public int getLid() {
        return field_4_lid;
    }


    public void setLid(int field_4_lid) {
        this.field_4_lid = field_4_lid;
    }


    public int getPnNext() {
        return field_5_pnNext;
    }


    public void setPnNext(int field_5_pnNext) {
        this.field_5_pnNext = field_5_pnNext;
    }


    public short getOptions() {
        return field_6_options;
    }


    public void setOptions(short field_6_options) {
        this.field_6_options = field_6_options;
    }


    public int getNFibBack() {
        return field_7_nFibBack;
    }


    public void setNFibBack(int field_7_nFibBack) {
        this.field_7_nFibBack = field_7_nFibBack;
    }


    public int getLKey() {
        return field_8_lKey;
    }


    public void setLKey(int field_8_lKey) {
        this.field_8_lKey = field_8_lKey;
    }


    public int getEnvr() {
        return field_9_envr;
    }


    public void setEnvr(int field_9_envr) {
        this.field_9_envr = field_9_envr;
    }


    public short getHistory() {
        return field_10_history;
    }


    public void setHistory(short field_10_history) {
        this.field_10_history = field_10_history;
    }


    public int getChs() {
        return field_11_chs;
    }


    public void setChs(int field_11_chs) {
        this.field_11_chs = field_11_chs;
    }


    public int getChsTables() {
        return field_12_chsTables;
    }


    public void setChsTables(int field_12_chsTables) {
        this.field_12_chsTables = field_12_chsTables;
    }


    public int getFcMin() {
        return field_13_fcMin;
    }


    public void setFcMin(int field_13_fcMin) {
        this.field_13_fcMin = field_13_fcMin;
    }


    public int getFcMac() {
        return field_14_fcMac;
    }


    public void setFcMac(int field_14_fcMac) {
        this.field_14_fcMac = field_14_fcMac;
    }


    public boolean isFDot() {
        return fDot.isSet(field_6_options);
    }


    public void setFDot(boolean value) {
        field_6_options = (short) fDot.setBoolean(field_6_options, value);
    }


    public boolean isFGlsy() {
        return fGlsy.isSet(field_6_options);
    }


    public void setFGlsy(boolean value) {
        field_6_options = (short) fGlsy.setBoolean(field_6_options, value);
    }


    public boolean isFComplex() {
        return fComplex.isSet(field_6_options);
    }


    public void setFComplex(boolean value) {
        field_6_options = (short) fComplex.setBoolean(field_6_options, value);
    }


    public boolean isFHasPic() {
        return fHasPic.isSet(field_6_options);
    }


    public void setFHasPic(boolean value) {
        field_6_options = (short) fHasPic.setBoolean(field_6_options, value);
    }


    public byte getCQuickSaves() {
        return (byte) cQuickSaves.getValue(field_6_options);
    }


    public void setCQuickSaves(byte value) {
        field_6_options = (short) cQuickSaves.setValue(field_6_options, value);
    }


    public boolean isFEncrypted() {
        return fEncrypted.isSet(field_6_options);
    }


    public void setFEncrypted(boolean value) {
        field_6_options = (short) fEncrypted.setBoolean(field_6_options, value);
    }


    public boolean isFWhichTblStm() {
        return fWhichTblStm.isSet(field_6_options);
    }


    public void setFWhichTblStm(boolean value) {
        field_6_options = (short) fWhichTblStm.setBoolean(field_6_options, value);
    }


    public boolean isFReadOnlyRecommended() {
        return fReadOnlyRecommended.isSet(field_6_options);
    }


    public void setFReadOnlyRecommended(boolean value) {
        field_6_options = (short) fReadOnlyRecommended.setBoolean(field_6_options, value);
    }


    public boolean isFWriteReservation() {
        return fWriteReservation.isSet(field_6_options);
    }


    public void setFWriteReservation(boolean value) {
        field_6_options = (short) fWriteReservation.setBoolean(field_6_options, value);
    }


    public boolean isFExtChar() {
        return fExtChar.isSet(field_6_options);
    }


    public void setFExtChar(boolean value) {
        field_6_options = (short) fExtChar.setBoolean(field_6_options, value);
    }


    public boolean isFLoadOverride() {
        return fLoadOverride.isSet(field_6_options);
    }


    public void setFLoadOverride(boolean value) {
        field_6_options = (short) fLoadOverride.setBoolean(field_6_options, value);
    }


    public boolean isFFarEast() {
        return fFarEast.isSet(field_6_options);
    }


    public void setFFarEast(boolean value) {
        field_6_options = (short) fFarEast.setBoolean(field_6_options, value);
    }


    public boolean isFCrypto() {
        return fCrypto.isSet(field_6_options);
    }


    public void setFCrypto(boolean value) {
        field_6_options = (short) fCrypto.setBoolean(field_6_options, value);
    }


    public boolean isFMac() {
        return fMac.isSet(field_10_history);
    }


    public void setFMac(boolean value) {
        field_10_history = (short) fMac.setBoolean(field_10_history, value);
    }


    public boolean isFEmptySpecial() {
        return fEmptySpecial.isSet(field_10_history);
    }


    public void setFEmptySpecial(boolean value) {
        field_10_history = (short) fEmptySpecial.setBoolean(field_10_history, value);
    }


    public boolean isFLoadOverridePage() {
        return fLoadOverridePage.isSet(field_10_history);
    }


    public void setFLoadOverridePage(boolean value) {
        field_10_history = (short) fLoadOverridePage.setBoolean(field_10_history, value);
    }


    public boolean isFFutureSavedUndo() {
        return fFutureSavedUndo.isSet(field_10_history);
    }


    public void setFFutureSavedUndo(boolean value) {
        field_10_history = (short) fFutureSavedUndo.setBoolean(field_10_history, value);
    }


    public boolean isFWord97Saved() {
        return fWord97Saved.isSet(field_10_history);
    }


    public void setFWord97Saved(boolean value) {
        field_10_history = (short) fWord97Saved.setBoolean(field_10_history, value);
    }


    public byte getFSpare0() {
        return (byte) fSpare0.getValue(field_10_history);
    }


    public void setFSpare0(byte value) {
        field_10_history = (short) fSpare0.setValue(field_10_history, value);
    }
}

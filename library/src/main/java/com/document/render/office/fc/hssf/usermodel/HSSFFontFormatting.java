

package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.hssf.record.CFRuleRecord;
import com.document.render.office.fc.hssf.record.cf.FontFormatting;


public final class HSSFFontFormatting implements com.document.render.office.fc.ss.usermodel.FontFormatting {


    public final static byte U_NONE = FontFormatting.U_NONE;

    public final static byte U_SINGLE = FontFormatting.U_SINGLE;

    public final static byte U_DOUBLE = FontFormatting.U_DOUBLE;

    public final static byte U_SINGLE_ACCOUNTING = FontFormatting.U_SINGLE_ACCOUNTING;

    public final static byte U_DOUBLE_ACCOUNTING = FontFormatting.U_DOUBLE_ACCOUNTING;

    private final FontFormatting fontFormatting;

    protected HSSFFontFormatting(CFRuleRecord cfRuleRecord) {
        this.fontFormatting = cfRuleRecord.getFontFormatting();
    }

    protected FontFormatting getFontFormattingBlock() {
        return fontFormatting;
    }


    public short getEscapementType() {
        return fontFormatting.getEscapementType();
    }


    public void setEscapementType(short escapementType) {
        switch (escapementType) {
            case HSSFFontFormatting.SS_SUB:
            case HSSFFontFormatting.SS_SUPER:
                fontFormatting.setEscapementType(escapementType);
                fontFormatting.setEscapementTypeModified(true);
                break;
            case HSSFFontFormatting.SS_NONE:
                fontFormatting.setEscapementType(escapementType);
                fontFormatting.setEscapementTypeModified(false);
                break;
            default:
        }
    }


    public short getFontColorIndex() {
        return fontFormatting.getFontColorIndex();
    }


    public void setFontColorIndex(short fci) {
        fontFormatting.setFontColorIndex(fci);
    }


    public int getFontHeight() {
        return fontFormatting.getFontHeight();
    }


    public void setFontHeight(int height) {
        fontFormatting.setFontHeight(height);
    }



    public short getFontWeight() {
        return fontFormatting.getFontWeight();
    }


    protected byte[] getRawRecord() {
        return fontFormatting.getRawRecord();
    }


    public short getUnderlineType() {
        return fontFormatting.getUnderlineType();
    }


    public void setUnderlineType(short underlineType) {
        switch (underlineType) {
            case HSSFFontFormatting.U_SINGLE:
            case HSSFFontFormatting.U_DOUBLE:
            case HSSFFontFormatting.U_SINGLE_ACCOUNTING:
            case HSSFFontFormatting.U_DOUBLE_ACCOUNTING:
                fontFormatting.setUnderlineType(underlineType);
                setUnderlineTypeModified(true);
                break;

            case HSSFFontFormatting.U_NONE:
                fontFormatting.setUnderlineType(underlineType);
                setUnderlineTypeModified(false);
                break;
            default:
        }
    }


    public boolean isBold() {
        return fontFormatting.isFontWeightModified() && fontFormatting.isBold();
    }


    public boolean isEscapementTypeModified() {
        return fontFormatting.isEscapementTypeModified();
    }


    public void setEscapementTypeModified(boolean modified) {
        fontFormatting.setEscapementTypeModified(modified);
    }


    public boolean isFontCancellationModified() {
        return fontFormatting.isFontCancellationModified();
    }


    public void setFontCancellationModified(boolean modified) {
        fontFormatting.setFontCancellationModified(modified);
    }


    public boolean isFontOutlineModified() {
        return fontFormatting.isFontOutlineModified();
    }


    public void setFontOutlineModified(boolean modified) {
        fontFormatting.setFontOutlineModified(modified);
    }


    public boolean isFontShadowModified() {
        return fontFormatting.isFontShadowModified();
    }


    public void setFontShadowModified(boolean modified) {
        fontFormatting.setFontShadowModified(modified);
    }


    public boolean isFontStyleModified() {
        return fontFormatting.isFontStyleModified();
    }


    public void setFontStyleModified(boolean modified) {
        fontFormatting.setFontStyleModified(modified);
    }


    public boolean isItalic() {
        return fontFormatting.isFontStyleModified() && fontFormatting.isItalic();
    }


    public boolean isOutlineOn() {
        return fontFormatting.isFontOutlineModified() && fontFormatting.isOutlineOn();
    }


    public boolean isShadowOn() {
        return fontFormatting.isFontOutlineModified() && fontFormatting.isShadowOn();
    }


    public boolean isStruckout() {
        return fontFormatting.isFontCancellationModified() && fontFormatting.isStruckout();
    }


    public boolean isUnderlineTypeModified() {
        return fontFormatting.isUnderlineTypeModified();
    }


    public void setUnderlineTypeModified(boolean modified) {
        fontFormatting.setUnderlineTypeModified(modified);
    }


    public boolean isFontWeightModified() {
        return fontFormatting.isFontWeightModified();
    }



    public void setFontStyle(boolean italic, boolean bold) {
        boolean modified = italic || bold;
        fontFormatting.setItalic(italic);
        fontFormatting.setBold(bold);
        fontFormatting.setFontStyleModified(modified);
        fontFormatting.setFontWieghtModified(modified);
    }


    public void resetFontStyle() {
        setFontStyle(false, false);
    }


    public void setOutline(boolean on) {
        fontFormatting.setOutline(on);
        fontFormatting.setFontOutlineModified(on);
    }


    public void setShadow(boolean on) {
        fontFormatting.setShadow(on);
        fontFormatting.setFontShadowModified(on);
    }


    public void setStrikeout(boolean strike) {
        fontFormatting.setStrikeout(strike);
        fontFormatting.setFontCancellationModified(strike);
    }
}

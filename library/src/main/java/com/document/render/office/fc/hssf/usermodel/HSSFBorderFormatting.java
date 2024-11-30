

package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.hssf.record.CFRuleRecord;
import com.document.render.office.fc.hssf.record.cf.BorderFormatting;



public final class HSSFBorderFormatting implements com.document.render.office.fc.ss.usermodel.BorderFormatting {
    private final CFRuleRecord cfRuleRecord;
    private final BorderFormatting borderFormatting;

    protected HSSFBorderFormatting(CFRuleRecord cfRuleRecord) {
        this.cfRuleRecord = cfRuleRecord;
        this.borderFormatting = cfRuleRecord.getBorderFormatting();
    }

    protected BorderFormatting getBorderFormattingBlock() {
        return borderFormatting;
    }

    public short getBorderBottom() {
        return (short) borderFormatting.getBorderBottom();
    }

    public void setBorderBottom(short border) {
        borderFormatting.setBorderBottom(border);
        if (border != 0) {
            cfRuleRecord.setBottomBorderModified(true);
        }
    }

    public short getBorderDiagonal() {
        return (short) borderFormatting.getBorderDiagonal();
    }

    public void setBorderDiagonal(short border) {
        borderFormatting.setBorderDiagonal(border);
        if (border != 0) {
            cfRuleRecord.setBottomLeftTopRightBorderModified(true);
            cfRuleRecord.setTopLeftBottomRightBorderModified(true);
        }
    }

    public short getBorderLeft() {
        return (short) borderFormatting.getBorderLeft();
    }

    public void setBorderLeft(short border) {
        borderFormatting.setBorderLeft(border);
        if (border != 0) {
            cfRuleRecord.setLeftBorderModified(true);
        }
    }

    public short getBorderRight() {
        return (short) borderFormatting.getBorderRight();
    }

    public void setBorderRight(short border) {
        borderFormatting.setBorderRight(border);
        if (border != 0) {
            cfRuleRecord.setRightBorderModified(true);
        }
    }

    public short getBorderTop() {
        return (short) borderFormatting.getBorderTop();
    }

    public void setBorderTop(short border) {
        borderFormatting.setBorderTop(border);
        if (border != 0) {
            cfRuleRecord.setTopBorderModified(true);
        }
    }

    public short getBottomBorderColor() {
        return (short) borderFormatting.getBottomBorderColor();
    }

    public void setBottomBorderColor(short color) {
        borderFormatting.setBottomBorderColor(color);
        if (color != 0) {
            cfRuleRecord.setBottomBorderModified(true);
        }
    }

    public short getDiagonalBorderColor() {
        return (short) borderFormatting.getDiagonalBorderColor();
    }

    public void setDiagonalBorderColor(short color) {
        borderFormatting.setDiagonalBorderColor(color);
        if (color != 0) {
            cfRuleRecord.setBottomLeftTopRightBorderModified(true);
            cfRuleRecord.setTopLeftBottomRightBorderModified(true);
        }
    }

    public short getLeftBorderColor() {
        return (short) borderFormatting.getLeftBorderColor();
    }

    public void setLeftBorderColor(short color) {
        borderFormatting.setLeftBorderColor(color);
        if (color != 0) {
            cfRuleRecord.setLeftBorderModified(true);
        }
    }

    public short getRightBorderColor() {
        return (short) borderFormatting.getRightBorderColor();
    }

    public void setRightBorderColor(short color) {
        borderFormatting.setRightBorderColor(color);
        if (color != 0) {
            cfRuleRecord.setRightBorderModified(true);
        }
    }

    public short getTopBorderColor() {
        return (short) borderFormatting.getTopBorderColor();
    }

    public void setTopBorderColor(short color) {
        borderFormatting.setTopBorderColor(color);
        if (color != 0) {
            cfRuleRecord.setTopBorderModified(true);
        }
    }

    public boolean isBackwardDiagonalOn() {
        return borderFormatting.isBackwardDiagonalOn();
    }

    public void setBackwardDiagonalOn(boolean on) {
        borderFormatting.setBackwardDiagonalOn(on);
        if (on) {
            cfRuleRecord.setTopLeftBottomRightBorderModified(on);
        }
    }

    public boolean isForwardDiagonalOn() {
        return borderFormatting.isForwardDiagonalOn();
    }

    public void setForwardDiagonalOn(boolean on) {
        borderFormatting.setForwardDiagonalOn(on);
        if (on) {
            cfRuleRecord.setBottomLeftTopRightBorderModified(on);
        }
    }
}

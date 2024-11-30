

package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.hssf.record.CFRuleRecord;
import com.document.render.office.fc.hssf.record.cf.PatternFormatting;



public class HSSFPatternFormatting implements com.document.render.office.fc.ss.usermodel.PatternFormatting {
    private final CFRuleRecord cfRuleRecord;
    private final PatternFormatting patternFormatting;

    protected HSSFPatternFormatting(CFRuleRecord cfRuleRecord) {
        this.cfRuleRecord = cfRuleRecord;
        this.patternFormatting = cfRuleRecord.getPatternFormatting();
    }

    protected PatternFormatting getPatternFormattingBlock() {
        return patternFormatting;
    }


    public short getFillBackgroundColor() {
        return (short) patternFormatting.getFillBackgroundColor();
    }


    public void setFillBackgroundColor(short bg) {
        patternFormatting.setFillBackgroundColor(bg);
        if (bg != 0) {
            cfRuleRecord.setPatternBackgroundColorModified(true);
        }
    }


    public short getFillForegroundColor() {
        return (short) patternFormatting.getFillForegroundColor();
    }


    public void setFillForegroundColor(short fg) {
        patternFormatting.setFillForegroundColor(fg);
        if (fg != 0) {
            cfRuleRecord.setPatternColorModified(true);
        }
    }


    public short getFillPattern() {
        return (short) patternFormatting.getFillPattern();
    }


    public void setFillPattern(short fp) {
        patternFormatting.setFillPattern(fp);
        if (fp != 0) {
            cfRuleRecord.setPatternStyleModified(true);
        }
    }
}

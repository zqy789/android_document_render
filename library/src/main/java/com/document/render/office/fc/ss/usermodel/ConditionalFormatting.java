

package com.document.render.office.fc.ss.usermodel;


import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;



public interface ConditionalFormatting {


    HSSFCellRangeAddress[] getFormattingRanges();


    void setRule(int idx, ConditionalFormattingRule cfRule);


    void addRule(ConditionalFormattingRule cfRule);


    ConditionalFormattingRule getRule(int idx);


    int getNumberOfRules();


}

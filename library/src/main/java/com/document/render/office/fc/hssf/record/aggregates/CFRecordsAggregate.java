

package com.document.render.office.fc.hssf.record.aggregates;

import com.document.render.office.fc.hssf.formula.FormulaShifter;
import com.document.render.office.fc.hssf.formula.ptg.AreaErrPtg;
import com.document.render.office.fc.hssf.formula.ptg.AreaPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.model.RecordStream;
import com.document.render.office.fc.hssf.record.CFHeaderRecord;
import com.document.render.office.fc.hssf.record.CFRuleRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;

import java.util.ArrayList;
import java.util.List;



public final class CFRecordsAggregate extends RecordAggregate {

    private static final int MAX_CONDTIONAL_FORMAT_RULES = 3;

    private final CFHeaderRecord header;


    private final List rules;

    private CFRecordsAggregate(CFHeaderRecord pHeader, CFRuleRecord[] pRules) {
        if (pHeader == null) {
            throw new IllegalArgumentException("header must not be null");
        }
        if (pRules == null) {
            throw new IllegalArgumentException("rules must not be null");
        }
        if (pRules.length > MAX_CONDTIONAL_FORMAT_RULES) {
            throw new IllegalArgumentException("No more than "
                    + MAX_CONDTIONAL_FORMAT_RULES + " rules may be specified");
        }
        if (pRules.length != pHeader.getNumberOfConditionalFormats()) {
            throw new RuntimeException("Mismatch number of rules");
        }
        header = pHeader;
        rules = new ArrayList(3);
        for (int i = 0; i < pRules.length; i++) {
            rules.add(pRules[i]);
        }
    }

    public CFRecordsAggregate(HSSFCellRangeAddress[] regions, CFRuleRecord[] rules) {
        this(new CFHeaderRecord(regions, rules.length), rules);
    }


    public static CFRecordsAggregate createCFAggregate(RecordStream rs) {
        Record rec = rs.getNext();
        if (rec.getSid() != CFHeaderRecord.sid) {
            throw new IllegalStateException("next record sid was " + rec.getSid()
                    + " instead of " + CFHeaderRecord.sid + " as expected");
        }

        CFHeaderRecord header = (CFHeaderRecord) rec;
        int nRules = header.getNumberOfConditionalFormats();

        CFRuleRecord[] rules = new CFRuleRecord[nRules];
        for (int i = 0; i < rules.length; i++) {
            rules[i] = (CFRuleRecord) rs.getNext();
        }

        return new CFRecordsAggregate(header, rules);
    }

    private static HSSFCellRangeAddress shiftRange(FormulaShifter shifter, HSSFCellRangeAddress cra, int currentExternSheetIx) {

        AreaPtg aptg = new AreaPtg(cra.getFirstRow(), cra.getLastRow(), cra.getFirstColumn(), cra.getLastColumn(), false, false, false, false);
        Ptg[] ptgs = {aptg,};

        if (!shifter.adjustFormula(ptgs, currentExternSheetIx)) {
            return cra;
        }
        Ptg ptg0 = ptgs[0];
        if (ptg0 instanceof AreaPtg) {
            AreaPtg bptg = (AreaPtg) ptg0;
            return new HSSFCellRangeAddress(bptg.getFirstRow(), bptg.getLastRow(), bptg.getFirstColumn(), bptg.getLastColumn());
        }
        if (ptg0 instanceof AreaErrPtg) {
            return null;
        }
        throw new IllegalStateException("Unexpected shifted ptg class (" + ptg0.getClass().getName() + ")");
    }


    public CFRecordsAggregate cloneCFAggregate() {

        CFRuleRecord[] newRecs = new CFRuleRecord[rules.size()];
        for (int i = 0; i < newRecs.length; i++) {
            newRecs[i] = (CFRuleRecord) getRule(i).clone();
        }
        return new CFRecordsAggregate((CFHeaderRecord) header.clone(), newRecs);
    }


    public CFHeaderRecord getHeader() {
        return header;
    }

    private void checkRuleIndex(int idx) {
        if (idx < 0 || idx >= rules.size()) {
            throw new IllegalArgumentException("Bad rule record index (" + idx
                    + ") nRules=" + rules.size());
        }
    }

    public CFRuleRecord getRule(int idx) {
        checkRuleIndex(idx);
        return (CFRuleRecord) rules.get(idx);
    }

    public void setRule(int idx, CFRuleRecord r) {
        if (r == null) {
            throw new IllegalArgumentException("r must not be null");
        }
        checkRuleIndex(idx);
        rules.set(idx, r);
    }

    public void addRule(CFRuleRecord r) {
        if (r == null) {
            throw new IllegalArgumentException("r must not be null");
        }
        if (rules.size() >= MAX_CONDTIONAL_FORMAT_RULES) {
            throw new IllegalStateException("Cannot have more than "
                    + MAX_CONDTIONAL_FORMAT_RULES + " conditional format rules");
        }
        rules.add(r);
        header.setNumberOfConditionalFormats(rules.size());
    }

    public int getNumberOfRules() {
        return rules.size();
    }


    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[CF]\n");
        if (header != null) {
            buffer.append(header.toString());
        }
        for (int i = 0; i < rules.size(); i++) {
            CFRuleRecord cfRule = (CFRuleRecord) rules.get(i);
            buffer.append(cfRule.toString());
        }
        buffer.append("[/CF]\n");
        return buffer.toString();
    }

    public void visitContainedRecords(RecordVisitor rv) {
        rv.visitRecord(header);
        for (int i = 0; i < rules.size(); i++) {
            CFRuleRecord rule = (CFRuleRecord) rules.get(i);
            rv.visitRecord(rule);
        }
    }


    public boolean updateFormulasAfterCellShift(FormulaShifter shifter, int currentExternSheetIx) {
        HSSFCellRangeAddress[] cellRanges = header.getCellRanges();
        boolean changed = false;
        List temp = new ArrayList();
        for (int i = 0; i < cellRanges.length; i++) {
            HSSFCellRangeAddress craOld = cellRanges[i];
            HSSFCellRangeAddress craNew = shiftRange(shifter, craOld, currentExternSheetIx);
            if (craNew == null) {
                changed = true;
                continue;
            }
            temp.add(craNew);
            if (craNew != craOld) {
                changed = true;
            }
        }

        if (changed) {
            int nRanges = temp.size();
            if (nRanges == 0) {
                return false;
            }
            HSSFCellRangeAddress[] newRanges = new HSSFCellRangeAddress[nRanges];
            temp.toArray(newRanges);
            header.setCellRanges(newRanges);
        }

        for (int i = 0; i < rules.size(); i++) {
            CFRuleRecord rule = (CFRuleRecord) rules.get(i);
            Ptg[] ptgs;
            ptgs = rule.getParsedExpression1();
            if (ptgs != null && shifter.adjustFormula(ptgs, currentExternSheetIx)) {
                rule.setParsedExpression1(ptgs);
            }
            ptgs = rule.getParsedExpression2();
            if (ptgs != null && shifter.adjustFormula(ptgs, currentExternSheetIx)) {
                rule.setParsedExpression2(ptgs);
            }
        }
        return true;
    }
}

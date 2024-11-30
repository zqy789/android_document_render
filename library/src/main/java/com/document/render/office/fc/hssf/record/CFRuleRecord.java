

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.formula.Formula;
import com.document.render.office.fc.hssf.formula.FormulaType;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.model.HSSFFormulaParser;
import com.document.render.office.fc.hssf.record.cf.BorderFormatting;
import com.document.render.office.fc.hssf.record.cf.FontFormatting;
import com.document.render.office.fc.hssf.record.cf.PatternFormatting;
import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.ss.model.XLSModel.ASheet;
import com.document.render.office.ss.model.XLSModel.AWorkbook;



public final class CFRuleRecord extends StandardRecord {
    @Keep
    public static final short sid = 0x01B1;
    public static final byte CONDITION_TYPE_CELL_VALUE_IS = 1;
    public static final byte CONDITION_TYPE_FORMULA = 2;
    private static final BitField modificationBits = bf(0x003FFFFF);
    private static final BitField alignHor = bf(0x00000001);
    private static final BitField alignVer = bf(0x00000002);
    private static final BitField alignWrap = bf(0x00000004);
    private static final BitField alignRot = bf(0x00000008);
    private static final BitField alignJustLast = bf(0x00000010);
    private static final BitField alignIndent = bf(0x00000020);
    private static final BitField alignShrin = bf(0x00000040);
    private static final BitField notUsed1 = bf(0x00000080);
    private static final BitField protLocked = bf(0x00000100);
    private static final BitField protHidden = bf(0x00000200);
    private static final BitField bordLeft = bf(0x00000400);
    private static final BitField bordRight = bf(0x00000800);
    private static final BitField bordTop = bf(0x00001000);
    private static final BitField bordBot = bf(0x00002000);
    private static final BitField bordTlBr = bf(0x00004000);
    private static final BitField bordBlTr = bf(0x00008000);
    private static final BitField pattStyle = bf(0x00010000);
    private static final BitField pattCol = bf(0x00020000);
    private static final BitField pattBgCol = bf(0x00040000);
    private static final BitField notUsed2 = bf(0x00380000);
    private static final BitField undocumented = bf(0x03C00000);
    private static final BitField fmtBlockBits = bf(0x7C000000);
    private static final BitField font = bf(0x04000000);
    private static final BitField align = bf(0x08000000);
    private static final BitField bord = bf(0x10000000);
    private static final BitField patt = bf(0x20000000);
    private static final BitField prot = bf(0x40000000);
    private static final BitField alignTextDir = bf(0x80000000);
    private byte field_1_condition_type;
    private byte field_2_comparison_operator;
    private int field_5_options;
    private short field_6_not_used;
    private FontFormatting _fontFormatting;
    private BorderFormatting _borderFormatting;
    private PatternFormatting _patternFormatting;
    private Formula field_17_formula1;
    private Formula field_18_formula2;


    private CFRuleRecord(byte conditionType, byte comparisonOperation) {
        field_1_condition_type = conditionType;
        field_2_comparison_operator = comparisonOperation;


        field_5_options = modificationBits.setValue(field_5_options, -1);

        field_5_options = fmtBlockBits.setValue(field_5_options, 0);
        field_5_options = undocumented.clear(field_5_options);

        field_6_not_used = (short) 0x8002;
        _fontFormatting = null;
        _borderFormatting = null;
        _patternFormatting = null;
        field_17_formula1 = Formula.create(Ptg.EMPTY_PTG_ARRAY);
        field_18_formula2 = Formula.create(Ptg.EMPTY_PTG_ARRAY);
    }
    private CFRuleRecord(byte conditionType, byte comparisonOperation, Ptg[] formula1, Ptg[] formula2) {
        this(conditionType, comparisonOperation);
        field_17_formula1 = Formula.create(formula1);
        field_18_formula2 = Formula.create(formula2);
    }

    public CFRuleRecord(RecordInputStream in) {
        field_1_condition_type = in.readByte();
        field_2_comparison_operator = in.readByte();
        int field_3_formula1_len = in.readUShort();
        int field_4_formula2_len = in.readUShort();
        field_5_options = in.readInt();
        field_6_not_used = in.readShort();

        if (containsFontFormattingBlock()) {
            _fontFormatting = new FontFormatting(in);
        }

        if (containsBorderFormattingBlock()) {
            _borderFormatting = new BorderFormatting(in);
        }

        if (containsPatternFormattingBlock()) {
            _patternFormatting = new PatternFormatting(in);
        }


        field_17_formula1 = Formula.read(field_3_formula1_len, in);
        field_18_formula2 = Formula.read(field_4_formula2_len, in);
    }

    private static BitField bf(int i) {
        return BitFieldFactory.getInstance(i);
    }


    @Keep
    public static CFRuleRecord create(ASheet sheet, String formulaText) {
        Ptg[] formula1 = parseFormula(formulaText, sheet);
        return new CFRuleRecord(CONDITION_TYPE_FORMULA, ComparisonOperator.NO_COMPARISON,
                formula1, null);
    }


    @Keep
    public static CFRuleRecord create(ASheet sheet, byte comparisonOperation,
                                      String formulaText1, String formulaText2) {
        Ptg[] formula1 = parseFormula(formulaText1, sheet);
        Ptg[] formula2 = parseFormula(formulaText2, sheet);
        return new CFRuleRecord(CONDITION_TYPE_CELL_VALUE_IS, comparisonOperation, formula1, formula2);
    }


    private static int getFormulaSize(Formula formula) {
        return formula.getEncodedTokenSize();
    }


    private static Ptg[] parseFormula(String formula, ASheet sheet) {
        if (formula == null) {
            return null;
        }


        int sheetIndex = ((AWorkbook) sheet.getWorkbook()).getSheetIndex(sheet);
        return HSSFFormulaParser.parse(formula, (AWorkbook) sheet.getWorkbook(), FormulaType.CELL, sheetIndex);
    }

    public byte getConditionType() {
        return field_1_condition_type;
    }

    public boolean containsFontFormattingBlock() {
        return getOptionFlag(font);
    }

    public FontFormatting getFontFormatting() {
        if (containsFontFormattingBlock()) {
            return _fontFormatting;
        }
        return null;
    }

    public void setFontFormatting(FontFormatting fontFormatting) {
        _fontFormatting = fontFormatting;
        setOptionFlag(fontFormatting != null, font);
    }

    public boolean containsAlignFormattingBlock() {
        return getOptionFlag(align);
    }

    public void setAlignFormattingUnchanged() {
        setOptionFlag(false, align);
    }

    public boolean containsBorderFormattingBlock() {
        return getOptionFlag(bord);
    }

    public BorderFormatting getBorderFormatting() {
        if (containsBorderFormattingBlock()) {
            return _borderFormatting;
        }
        return null;
    }

    public void setBorderFormatting(BorderFormatting borderFormatting) {
        _borderFormatting = borderFormatting;
        setOptionFlag(borderFormatting != null, bord);
    }

    public boolean containsPatternFormattingBlock() {
        return getOptionFlag(patt);
    }

    public PatternFormatting getPatternFormatting() {
        if (containsPatternFormattingBlock()) {
            return _patternFormatting;
        }
        return null;
    }

    public void setPatternFormatting(PatternFormatting patternFormatting) {
        _patternFormatting = patternFormatting;
        setOptionFlag(patternFormatting != null, patt);
    }

    public boolean containsProtectionFormattingBlock() {
        return getOptionFlag(prot);
    }

    public void setProtectionFormattingUnchanged() {
        setOptionFlag(false, prot);
    }

    public byte getComparisonOperation() {
        return field_2_comparison_operator;
    }

    public void setComparisonOperation(byte operation) {
        field_2_comparison_operator = operation;
    }


    public int getOptions() {
        return field_5_options;
    }

    private boolean isModified(BitField field) {
        return !field.isSet(field_5_options);
    }

    private void setModified(boolean modified, BitField field) {
        field_5_options = field.setBoolean(field_5_options, !modified);
    }

    public boolean isLeftBorderModified() {
        return isModified(bordLeft);
    }

    public void setLeftBorderModified(boolean modified) {
        setModified(modified, bordLeft);
    }

    public boolean isRightBorderModified() {
        return isModified(bordRight);
    }

    public void setRightBorderModified(boolean modified) {
        setModified(modified, bordRight);
    }

    public boolean isTopBorderModified() {
        return isModified(bordTop);
    }

    public void setTopBorderModified(boolean modified) {
        setModified(modified, bordTop);
    }

    public boolean isBottomBorderModified() {
        return isModified(bordBot);
    }

    public void setBottomBorderModified(boolean modified) {
        setModified(modified, bordBot);
    }

    public boolean isTopLeftBottomRightBorderModified() {
        return isModified(bordTlBr);
    }

    public void setTopLeftBottomRightBorderModified(boolean modified) {
        setModified(modified, bordTlBr);
    }

    public boolean isBottomLeftTopRightBorderModified() {
        return isModified(bordBlTr);
    }

    public void setBottomLeftTopRightBorderModified(boolean modified) {
        setModified(modified, bordBlTr);
    }

    public boolean isPatternStyleModified() {
        return isModified(pattStyle);
    }

    public void setPatternStyleModified(boolean modified) {
        setModified(modified, pattStyle);
    }

    public boolean isPatternColorModified() {
        return isModified(pattCol);
    }

    public void setPatternColorModified(boolean modified) {
        setModified(modified, pattCol);
    }

    public boolean isPatternBackgroundColorModified() {
        return isModified(pattBgCol);
    }

    public void setPatternBackgroundColorModified(boolean modified) {
        setModified(modified, pattBgCol);
    }

    private boolean getOptionFlag(BitField field) {
        return field.isSet(field_5_options);
    }

    private void setOptionFlag(boolean flag, BitField field) {
        field_5_options = field.setBoolean(field_5_options, flag);
    }



    public Ptg[] getParsedExpression1() {
        return field_17_formula1.getTokens();
    }

    public void setParsedExpression1(Ptg[] ptgs) {
        field_17_formula1 = Formula.create(ptgs);
    }


    public Ptg[] getParsedExpression2() {
        return Formula.getTokens(field_18_formula2);
    }

    public void setParsedExpression2(Ptg[] ptgs) {
        field_18_formula2 = Formula.create(ptgs);
    }

    public short getSid() {
        return sid;
    }


    public void serialize(LittleEndianOutput out) {

        int formula1Len = getFormulaSize(field_17_formula1);
        int formula2Len = getFormulaSize(field_18_formula2);

        out.writeByte(field_1_condition_type);
        out.writeByte(field_2_comparison_operator);
        out.writeShort(formula1Len);
        out.writeShort(formula2Len);
        out.writeInt(field_5_options);
        out.writeShort(field_6_not_used);

        if (containsFontFormattingBlock()) {
            byte[] fontFormattingRawRecord = _fontFormatting.getRawRecord();
            out.write(fontFormattingRawRecord);
        }

        if (containsBorderFormattingBlock()) {
            _borderFormatting.serialize(out);
        }

        if (containsPatternFormattingBlock()) {
            _patternFormatting.serialize(out);
        }

        field_17_formula1.serializeTokens(out);
        field_18_formula2.serializeTokens(out);
    }

    protected int getDataSize() {
        return 12 +
                (containsFontFormattingBlock() ? _fontFormatting.getRawRecord().length : 0) +
                (containsBorderFormattingBlock() ? 8 : 0) +
                (containsPatternFormattingBlock() ? 4 : 0) +
                getFormulaSize(field_17_formula1) +
                getFormulaSize(field_18_formula2)
                ;
    }


    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[CFRULE]\n");
        buffer.append("    .condition_type   =" + field_1_condition_type);
        buffer.append("    OPTION FLAGS=0x" + Integer.toHexString(getOptions()));
        if (false) {
            if (containsFontFormattingBlock()) {
                buffer.append(_fontFormatting.toString());
            }
            if (containsBorderFormattingBlock()) {
                buffer.append(_borderFormatting.toString());
            }
            if (containsPatternFormattingBlock()) {
                buffer.append(_patternFormatting.toString());
            }
            buffer.append("[/CFRULE]\n");
        }
        return buffer.toString();
    }

    public Object clone() {
        CFRuleRecord rec = new CFRuleRecord(field_1_condition_type, field_2_comparison_operator);
        rec.field_5_options = field_5_options;
        rec.field_6_not_used = field_6_not_used;
        if (containsFontFormattingBlock()) {
            rec._fontFormatting = (FontFormatting) _fontFormatting.clone();
        }
        if (containsBorderFormattingBlock()) {
            rec._borderFormatting = (BorderFormatting) _borderFormatting.clone();
        }
        if (containsPatternFormattingBlock()) {
            rec._patternFormatting = (PatternFormatting) _patternFormatting.clone();
        }
        rec.field_17_formula1 = field_17_formula1.copy();
        rec.field_18_formula2 = field_17_formula1.copy();

        return rec;
    }

    public static final class ComparisonOperator {
        public static final byte NO_COMPARISON = 0;
        public static final byte BETWEEN = 1;
        public static final byte NOT_BETWEEN = 2;
        public static final byte EQUAL = 3;
        public static final byte NOT_EQUAL = 4;
        public static final byte GT = 5;
        public static final byte LT = 6;
        public static final byte GE = 7;
        public static final byte LE = 8;
    }

}

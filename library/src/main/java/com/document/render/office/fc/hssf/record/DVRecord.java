

package com.document.render.office.fc.hssf.record;


import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.formula.Formula;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.record.common.UnicodeString;
import com.document.render.office.fc.hssf.usermodel.HSSFDataValidation;
import com.document.render.office.fc.ss.util.CellRangeAddressList;
import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;
import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;



public final class DVRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x01BE;


    private static final UnicodeString NULL_TEXT_STRING = new UnicodeString("\0");

    private static final BitField opt_data_type = new BitField(0x0000000F);
    private static final BitField opt_error_style = new BitField(0x00000070);
    private static final BitField opt_string_list_formula = new BitField(0x00000080);
    private static final BitField opt_empty_cell_allowed = new BitField(0x00000100);
    private static final BitField opt_suppress_dropdown_arrow = new BitField(0x00000200);
    private static final BitField opt_show_prompt_on_cell_selected = new BitField(0x00040000);
    private static final BitField opt_show_error_on_invalid_value = new BitField(0x00080000);
    private static final BitField opt_condition_operator = new BitField(0x00700000);

    private int _option_flags;

    private UnicodeString _promptTitle;

    private UnicodeString _errorTitle;

    private UnicodeString _promptText;

    private UnicodeString _errorText;

    private short _not_used_1 = 0x3FE0;

    private Formula _formula1;

    private short _not_used_2 = 0x0000;

    private Formula _formula2;

    private CellRangeAddressList _regions;

    public DVRecord(int validationType, int operator, int errorStyle, boolean emptyCellAllowed,
                    boolean suppressDropDownArrow, boolean isExplicitList,
                    boolean showPromptBox, String promptTitle, String promptText,
                    boolean showErrorBox, String errorTitle, String errorText,
                    Ptg[] formula1, Ptg[] formula2,
                    CellRangeAddressList regions) {

        int flags = 0;
        flags = opt_data_type.setValue(flags, validationType);
        flags = opt_condition_operator.setValue(flags, operator);
        flags = opt_error_style.setValue(flags, errorStyle);
        flags = opt_empty_cell_allowed.setBoolean(flags, emptyCellAllowed);
        flags = opt_suppress_dropdown_arrow.setBoolean(flags, suppressDropDownArrow);
        flags = opt_string_list_formula.setBoolean(flags, isExplicitList);
        flags = opt_show_prompt_on_cell_selected.setBoolean(flags, showPromptBox);
        flags = opt_show_error_on_invalid_value.setBoolean(flags, showErrorBox);
        _option_flags = flags;
        _promptTitle = resolveTitleText(promptTitle);
        _promptText = resolveTitleText(promptText);
        _errorTitle = resolveTitleText(errorTitle);
        _errorText = resolveTitleText(errorText);
        _formula1 = Formula.create(formula1);
        _formula2 = Formula.create(formula2);
        _regions = regions;
    }

    public DVRecord(RecordInputStream in) {

        _option_flags = in.readInt();

        _promptTitle = readUnicodeString(in);
        _errorTitle = readUnicodeString(in);
        _promptText = readUnicodeString(in);
        _errorText = readUnicodeString(in);

        int field_size_first_formula = in.readUShort();
        _not_used_1 = in.readShort();




        _formula1 = Formula.read(field_size_first_formula, in);

        int field_size_sec_formula = in.readUShort();
        _not_used_2 = in.readShort();


        _formula2 = Formula.read(field_size_sec_formula, in);


        _regions = new CellRangeAddressList(in);
    }



    private static String formatTextTitle(UnicodeString us) {
        String str = us.getString();
        if (str.length() == 1 && str.charAt(0) == '\0') {
            return "'\\0'";
        }
        return str;
    }

    private static void appendFormula(StringBuffer sb, String label, Formula f) {
        sb.append(label);

        if (f == null) {
            sb.append("<empty>\n");
            return;
        }
        Ptg[] ptgs = f.getTokens();
        sb.append('\n');
        for (int i = 0; i < ptgs.length; i++) {
            sb.append('\t').append(ptgs[i].toString()).append('\n');
        }
    }


    private static UnicodeString resolveTitleText(String str) {
        if (str == null || str.length() < 1) {
            return NULL_TEXT_STRING;
        }
        return new UnicodeString(str);
    }

    private static UnicodeString readUnicodeString(RecordInputStream in) {
        return new UnicodeString(in);
    }

    private static void serializeUnicodeString(UnicodeString us, LittleEndianOutput out) {
        StringUtil.writeUnicodeString(out, us.getString());
    }

    private static int getUnicodeStringSize(UnicodeString us) {
        String str = us.getString();
        return 3 + str.length() * (StringUtil.hasMultibyte(str) ? 2 : 1);
    }


    public int getDataType() {
        return opt_data_type.getValue(_option_flags);
    }


    public int getErrorStyle() {
        return opt_error_style.getValue(_option_flags);
    }



    public boolean getListExplicitFormula() {
        return (opt_string_list_formula.isSet(_option_flags));
    }


    public boolean getEmptyCellAllowed() {
        return (opt_empty_cell_allowed.isSet(_option_flags));
    }


    public boolean getSuppressDropdownArrow() {
        return (opt_suppress_dropdown_arrow.isSet(_option_flags));
    }


    public boolean getShowPromptOnCellSelected() {
        return (opt_show_prompt_on_cell_selected.isSet(_option_flags));
    }


    public boolean getShowErrorOnInvalidValue() {
        return (opt_show_error_on_invalid_value.isSet(_option_flags));
    }


    public int getConditionOperator() {
        return opt_condition_operator.getValue(_option_flags);
    }

    public CellRangeAddressList getCellRangeAddress() {
        return this._regions;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[DV]\n");
        sb.append(" options=").append(Integer.toHexString(_option_flags));
        sb.append(" title-prompt=").append(formatTextTitle(_promptTitle));
        sb.append(" title-error=").append(formatTextTitle(_errorTitle));
        sb.append(" text-prompt=").append(formatTextTitle(_promptText));
        sb.append(" text-error=").append(formatTextTitle(_errorText));
        sb.append("\n");
        appendFormula(sb, "Formula 1:", _formula1);
        appendFormula(sb, "Formula 2:", _formula2);
        sb.append("Regions: ");
        int nRegions = _regions.countRanges();
        for (int i = 0; i < nRegions; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            HSSFCellRangeAddress addr = _regions.getCellRangeAddress(i);
            sb.append('(').append(addr.getFirstRow()).append(',').append(addr.getLastRow());
            sb.append(',').append(addr.getFirstColumn()).append(',').append(addr.getLastColumn()).append(')');
        }
        sb.append("\n");
        sb.append("[/DV]");

        return sb.toString();
    }

    public void serialize(LittleEndianOutput out) {

        out.writeInt(_option_flags);

        serializeUnicodeString(_promptTitle, out);
        serializeUnicodeString(_errorTitle, out);
        serializeUnicodeString(_promptText, out);
        serializeUnicodeString(_errorText, out);
        out.writeShort(_formula1.getEncodedTokenSize());
        out.writeShort(_not_used_1);
        _formula1.serializeTokens(out);

        out.writeShort(_formula2.getEncodedTokenSize());
        out.writeShort(_not_used_2);
        _formula2.serializeTokens(out);

        _regions.serialize(out);
    }

    protected int getDataSize() {
        int size = 4 + 2 + 2 + 2 + 2;
        size += getUnicodeStringSize(_promptTitle);
        size += getUnicodeStringSize(_errorTitle);
        size += getUnicodeStringSize(_promptText);
        size += getUnicodeStringSize(_errorText);
        size += _formula1.getEncodedTokenSize();
        size += _formula2.getEncodedTokenSize();
        size += _regions.getSize();
        return size;
    }

    public short getSid() {
        return sid;
    }


    public Object clone() {
        return cloneViaReserialise();
    }
}

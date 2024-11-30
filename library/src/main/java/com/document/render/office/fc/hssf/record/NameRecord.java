

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.formula.Formula;
import com.document.render.office.fc.hssf.formula.ptg.Area3DPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.formula.ptg.Ref3DPtg;
import com.document.render.office.fc.hssf.record.cont.ContinuableRecord;
import com.document.render.office.fc.hssf.record.cont.ContinuableRecordOutput;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianByteArrayInputStream;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.StringUtil;



public final class NameRecord extends ContinuableRecord {
    @Keep
    public final static short sid = 0x0018;

    public final static byte BUILTIN_CONSOLIDATE_AREA = 1;

    public final static byte BUILTIN_AUTO_OPEN = 2;

    public final static byte BUILTIN_AUTO_CLOSE = 3;

    public final static byte BUILTIN_DATABASE = 4;

    public final static byte BUILTIN_CRITERIA = 5;

    public final static byte BUILTIN_PRINT_AREA = 6;
    public final static byte BUILTIN_PRINT_TITLE = 7;


    public final static byte BUILTIN_RECORDER = 8;

    public final static byte BUILTIN_DATA_FORM = 9;

    public final static byte BUILTIN_AUTO_ACTIVATE = 10;

    public final static byte BUILTIN_AUTO_DEACTIVATE = 11;

    public final static byte BUILTIN_SHEET_TITLE = 12;

    public final static byte BUILTIN_FILTER_DB = 13;
    private short field_1_option_flag;
    private byte field_2_keyboard_shortcut;

    private short field_5_externSheetIndex_plus1;

    private int field_6_sheetNumber;
    private boolean field_11_nameIsMultibyte;
    private byte field_12_built_in_code;
    private String field_12_name_text;
    private Formula field_13_name_definition;
    private String field_14_custom_menu_text;
    private String field_15_description_text;
    private String field_16_help_topic_text;
    private String field_17_status_bar_text;

    public NameRecord() {
        field_13_name_definition = Formula.create(Ptg.EMPTY_PTG_ARRAY);

        field_12_name_text = "";
        field_14_custom_menu_text = "";
        field_15_description_text = "";
        field_16_help_topic_text = "";
        field_17_status_bar_text = "";
    }



    public NameRecord(byte builtin, int sheetNumber) {
        this();
        field_12_built_in_code = builtin;
        setOptionFlag((short) (field_1_option_flag | Option.OPT_BUILTIN));
        field_6_sheetNumber = sheetNumber;
    }


    public NameRecord(RecordInputStream ris) {




        byte[] remainder = ris.readAllContinuedRemainder();
        LittleEndianInput in = new LittleEndianByteArrayInputStream(remainder);

        field_1_option_flag = in.readShort();
        field_2_keyboard_shortcut = in.readByte();
        int field_3_length_name_text = in.readUByte();
        int field_4_length_name_definition = in.readShort();
        field_5_externSheetIndex_plus1 = in.readShort();
        field_6_sheetNumber = in.readUShort();
        int f7_customMenuLen = in.readUByte();
        int f8_descriptionTextLen = in.readUByte();
        int f9_helpTopicTextLen = in.readUByte();
        int f10_statusBarTextLen = in.readUByte();


        field_11_nameIsMultibyte = (in.readByte() != 0);
        if (isBuiltInName()) {
            field_12_built_in_code = in.readByte();
        } else {
            if (field_11_nameIsMultibyte) {
                field_12_name_text = StringUtil.readUnicodeLE(in, field_3_length_name_text);
            } else {
                field_12_name_text = StringUtil.readCompressedUnicode(in, field_3_length_name_text);
            }
        }

        int nBytesAvailable = in.available() - (f7_customMenuLen
                + f8_descriptionTextLen + f9_helpTopicTextLen + f10_statusBarTextLen);
        field_13_name_definition = Formula.read(field_4_length_name_definition, in, nBytesAvailable);


        field_14_custom_menu_text = StringUtil.readCompressedUnicode(in, f7_customMenuLen);
        field_15_description_text = StringUtil.readCompressedUnicode(in, f8_descriptionTextLen);
        field_16_help_topic_text = StringUtil.readCompressedUnicode(in, f9_helpTopicTextLen);
        field_17_status_bar_text = StringUtil.readCompressedUnicode(in, f10_statusBarTextLen);
    }


    private static String translateBuiltInName(byte name) {
        switch (name) {
            case NameRecord.BUILTIN_AUTO_ACTIVATE:
                return "Auto_Activate";
            case NameRecord.BUILTIN_AUTO_CLOSE:
                return "Auto_Close";
            case NameRecord.BUILTIN_AUTO_DEACTIVATE:
                return "Auto_Deactivate";
            case NameRecord.BUILTIN_AUTO_OPEN:
                return "Auto_Open";
            case NameRecord.BUILTIN_CONSOLIDATE_AREA:
                return "Consolidate_Area";
            case NameRecord.BUILTIN_CRITERIA:
                return "Criteria";
            case NameRecord.BUILTIN_DATABASE:
                return "Database";
            case NameRecord.BUILTIN_DATA_FORM:
                return "Data_Form";
            case NameRecord.BUILTIN_PRINT_AREA:
                return "Print_Area";
            case NameRecord.BUILTIN_PRINT_TITLE:
                return "Print_Titles";
            case NameRecord.BUILTIN_RECORDER:
                return "Recorder";
            case NameRecord.BUILTIN_SHEET_TITLE:
                return "Sheet_Title";
            case NameRecord.BUILTIN_FILTER_DB:
                return "_FilterDatabase";

        }

        return "Unknown";
    }


    public int getSheetNumber() {
        return field_6_sheetNumber;
    }

    public void setSheetNumber(int value) {
        field_6_sheetNumber = value;
    }


    public byte getFnGroup() {
        int masked = field_1_option_flag & 0x0fc0;
        return (byte) (masked >> 4);
    }


    public short getOptionFlag() {
        return field_1_option_flag;
    }


    public void setOptionFlag(short flag) {
        field_1_option_flag = flag;
    }


    public byte getKeyboardShortcut() {
        return field_2_keyboard_shortcut;
    }


    public void setKeyboardShortcut(byte shortcut) {
        field_2_keyboard_shortcut = shortcut;
    }


    private int getNameTextLength() {
        if (isBuiltInName()) {
            return 1;
        }
        return field_12_name_text.length();
    }


    public boolean isHiddenName() {
        return (field_1_option_flag & Option.OPT_HIDDEN_NAME) != 0;
    }

    public void setHidden(boolean b) {
        if (b) {
            field_1_option_flag |= Option.OPT_HIDDEN_NAME;
        } else {
            field_1_option_flag &= (~Option.OPT_HIDDEN_NAME);
        }
    }


    public boolean isFunctionName() {
        return (field_1_option_flag & Option.OPT_FUNCTION_NAME) != 0;
    }


    public void setFunction(boolean function) {
        if (function) {
            field_1_option_flag |= Option.OPT_FUNCTION_NAME;
        } else {
            field_1_option_flag &= (~Option.OPT_FUNCTION_NAME);
        }
    }


    public boolean hasFormula() {
        return Option.isFormula(field_1_option_flag) && field_13_name_definition.getEncodedTokenSize() > 0;
    }


    public boolean isCommandName() {
        return (field_1_option_flag & Option.OPT_COMMAND_NAME) != 0;
    }


    public boolean isMacro() {
        return (field_1_option_flag & Option.OPT_MACRO) != 0;
    }


    public boolean isComplexFunction() {
        return (field_1_option_flag & Option.OPT_COMPLEX) != 0;
    }


    public boolean isBuiltInName() {
        return ((field_1_option_flag & Option.OPT_BUILTIN) != 0);
    }


    public String getNameText() {

        return isBuiltInName() ? translateBuiltInName(getBuiltInName()) : field_12_name_text;
    }


    public void setNameText(String name) {
        field_12_name_text = name;
        field_11_nameIsMultibyte = StringUtil.hasMultibyte(name);
    }


    public byte getBuiltInName() {
        return field_12_built_in_code;
    }


    public Ptg[] getNameDefinition() {
        return field_13_name_definition.getTokens();
    }

    public void setNameDefinition(Ptg[] ptgs) {
        field_13_name_definition = Formula.create(ptgs);
    }


    public String getCustomMenuText() {
        return field_14_custom_menu_text;
    }


    public void setCustomMenuText(String text) {
        field_14_custom_menu_text = text;
    }


    public String getDescriptionText() {
        return field_15_description_text;
    }


    public void setDescriptionText(String text) {
        field_15_description_text = text;
    }


    public String getHelpTopicText() {
        return field_16_help_topic_text;
    }


    public void setHelpTopicText(String text) {
        field_16_help_topic_text = text;
    }


    public String getStatusBarText() {
        return field_17_status_bar_text;
    }


    public void setStatusBarText(String text) {
        field_17_status_bar_text = text;
    }


    public void serialize(ContinuableRecordOutput out) {

        int field_7_length_custom_menu = field_14_custom_menu_text.length();
        int field_8_length_description_text = field_15_description_text.length();
        int field_9_length_help_topic_text = field_16_help_topic_text.length();
        int field_10_length_status_bar_text = field_17_status_bar_text.length();


        out.writeShort(getOptionFlag());
        out.writeByte(getKeyboardShortcut());
        out.writeByte(getNameTextLength());

        out.writeShort(field_13_name_definition.getEncodedTokenSize());
        out.writeShort(field_5_externSheetIndex_plus1);
        out.writeShort(field_6_sheetNumber);
        out.writeByte(field_7_length_custom_menu);
        out.writeByte(field_8_length_description_text);
        out.writeByte(field_9_length_help_topic_text);
        out.writeByte(field_10_length_status_bar_text);
        out.writeByte(field_11_nameIsMultibyte ? 1 : 0);

        if (isBuiltInName()) {

            out.writeByte(field_12_built_in_code);
        } else {
            String nameText = field_12_name_text;
            if (field_11_nameIsMultibyte) {
                StringUtil.putUnicodeLE(nameText, out);
            } else {
                StringUtil.putCompressedUnicode(nameText, out);
            }
        }
        field_13_name_definition.serializeTokens(out);
        field_13_name_definition.serializeArrayConstantData(out);

        StringUtil.putCompressedUnicode(getCustomMenuText(), out);
        StringUtil.putCompressedUnicode(getDescriptionText(), out);
        StringUtil.putCompressedUnicode(getHelpTopicText(), out);
        StringUtil.putCompressedUnicode(getStatusBarText(), out);
    }

    private int getNameRawSize() {
        if (isBuiltInName()) {
            return 1;
        }
        int nChars = field_12_name_text.length();
        if (field_11_nameIsMultibyte) {
            return 2 * nChars;
        }
        return nChars;
    }

    protected int getDataSize() {
        return 13
                + getNameRawSize()
                + field_14_custom_menu_text.length()
                + field_15_description_text.length()
                + field_16_help_topic_text.length()
                + field_17_status_bar_text.length()
                + field_13_name_definition.getEncodedSize();
    }


    public int getExternSheetNumber() {
        if (field_13_name_definition.getEncodedSize() < 1) {
            return 0;
        }
        Ptg ptg = field_13_name_definition.getTokens()[0];

        if (ptg.getClass() == Area3DPtg.class) {
            return ((Area3DPtg) ptg).getExternSheetIndex();

        }
        if (ptg.getClass() == Ref3DPtg.class) {
            return ((Ref3DPtg) ptg).getExternSheetIndex();
        }
        return 0;
    }


    public short getSid() {
        return sid;
    }



    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("[NAME]\n");
        sb.append("    .option flags           = ").append(HexDump.shortToHex(field_1_option_flag)).append("\n");
        sb.append("    .keyboard shortcut      = ").append(HexDump.byteToHex(field_2_keyboard_shortcut)).append("\n");
        sb.append("    .length of the name     = ").append(getNameTextLength()).append("\n");
        sb.append("    .extSheetIx(1-based, 0=Global)= ").append(field_5_externSheetIndex_plus1).append("\n");
        sb.append("    .sheetTabIx             = ").append(field_6_sheetNumber).append("\n");
        sb.append("    .Menu text length       = ").append(field_14_custom_menu_text.length()).append("\n");
        sb.append("    .Description text length= ").append(field_15_description_text.length()).append("\n");
        sb.append("    .Help topic text length = ").append(field_16_help_topic_text.length()).append("\n");
        sb.append("    .Status bar text length = ").append(field_17_status_bar_text.length()).append("\n");
        sb.append("    .NameIsMultibyte        = ").append(field_11_nameIsMultibyte).append("\n");
        sb.append("    .Name (Unicode text)    = ").append(getNameText()).append("\n");
        Ptg[] ptgs = field_13_name_definition.getTokens();
        sb.append("    .Formula (nTokens=").append(ptgs.length).append("):").append("\n");
        for (int i = 0; i < ptgs.length; i++) {
            Ptg ptg = ptgs[i];
            sb.append("       " + ptg.toString()).append(ptg.getRVAType()).append("\n");
        }

        sb.append("    .Menu text       = ").append(field_14_custom_menu_text).append("\n");
        sb.append("    .Description text= ").append(field_15_description_text).append("\n");
        sb.append("    .Help topic text = ").append(field_16_help_topic_text).append("\n");
        sb.append("    .Status bar text = ").append(field_17_status_bar_text).append("\n");
        sb.append("[/NAME]\n");

        return sb.toString();
    }

    private static final class Option {
        public static final int OPT_HIDDEN_NAME = 0x0001;
        public static final int OPT_FUNCTION_NAME = 0x0002;
        public static final int OPT_COMMAND_NAME = 0x0004;
        public static final int OPT_MACRO = 0x0008;
        public static final int OPT_COMPLEX = 0x0010;
        public static final int OPT_BUILTIN = 0x0020;
        public static final int OPT_BINDATA = 0x1000;

        public static final boolean isFormula(int optValue) {
            return (optValue & 0x0F) == 0;
        }
    }
}

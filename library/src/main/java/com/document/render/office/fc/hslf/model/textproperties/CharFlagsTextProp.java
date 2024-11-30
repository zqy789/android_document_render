

package com.document.render.office.fc.hslf.model.textproperties;


public class CharFlagsTextProp extends BitMaskTextProp {
    public static final int BOLD_IDX = 0;
    public static final int ITALIC_IDX = 1;
    public static final int UNDERLINE_IDX = 2;
    public static final int SHADOW_IDX = 4;
    public static final int STRIKETHROUGH_IDX = 8;
    public static final int RELIEF_IDX = 9;
    public static final int RESET_NUMBERING_IDX = 10;
    public static final int ENABLE_NUMBERING_1_IDX = 11;
    public static final int ENABLE_NUMBERING_2_IDX = 12;

    public static String NAME = "char_flags";

    public CharFlagsTextProp() {
        super(2, 0xffff, NAME, new String[]{"bold",
                "italic",
                "underline",
                "char_unknown_1",
                "shadow",
                "fehint",
                "char_unknown_2",
                "kumi",
                "strikethrough",
                "emboss",
                "char_unknown_3",
                "char_unknown_4",
                "char_unknown_5",
        });
    }
}

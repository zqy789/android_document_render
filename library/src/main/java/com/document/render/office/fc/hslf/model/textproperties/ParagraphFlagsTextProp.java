

package com.document.render.office.fc.hslf.model.textproperties;


public final class ParagraphFlagsTextProp extends BitMaskTextProp {
    public static final int BULLET_IDX = 0;
    public static final int BULLET_HARDFONT_IDX = 1;
    public static final int BULLET_HARDCOLOR_IDX = 2;
    public static final int BULLET_HARDSIZE_IDX = 4;

    public static String NAME = "paragraph_flags";

    public ParagraphFlagsTextProp() {
        super(2, 0xF, NAME, new String[]{"bullet", "bullet.hardfont", "bullet.hardcolor",
                "bullet.hardsize"});
    }
}

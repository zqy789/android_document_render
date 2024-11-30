
package com.document.render.office.constant.wp;


public class AttrIDConstant {


    public static final short FONT_STYLE_ID = 0x0000;

    public static final short FONT_SIZE_ID = FONT_STYLE_ID + 1;

    public static final short FONT_NAME_ID = FONT_SIZE_ID + 1;

    public static final short FONT_COLOR_ID = FONT_NAME_ID + 1;

    public static final short FONT_BOLD_ID = FONT_COLOR_ID + 1;

    public static final short FONT_ITALIC_ID = FONT_BOLD_ID + 1;

    public static final short FONT_STRIKE_ID = FONT_ITALIC_ID + 1;

    public static final short FONT_DOUBLESTRIKE_ID = FONT_STRIKE_ID + 1;

    public static final short FONT_UNDERLINE_ID = FONT_DOUBLESTRIKE_ID + 1;

    public static final short FONT_UNDERLINE_COLOR_ID = FONT_UNDERLINE_ID + 1;

    public static final short FONT_SCRIPT_ID = FONT_UNDERLINE_COLOR_ID + 1;

    public static final short FONT_HIGHLIGHT_ID = FONT_SCRIPT_ID + 1;

    public static final short FONT_HYPERLINK_ID = FONT_HIGHLIGHT_ID + 1;

    public static final short FONT_SHAPE_ID = FONT_HYPERLINK_ID + 1;

    public static final short FONT_SCALE_ID = FONT_SHAPE_ID + 1;

    public static final short FONT_PAGE_NUMBER_TYPE_ID = FONT_SCALE_ID + 1;

    public static final short FONT_ENCLOSE_CHARACTER_TYPE_ID = FONT_PAGE_NUMBER_TYPE_ID + 1;



    public static final short PARA_STYLE_ID = 0x1000;

    public static final short PARA_INDENT_LEFT_ID = PARA_STYLE_ID + 1;
    public static final short PARA_INDENT_INITLEFT_ID = PARA_INDENT_LEFT_ID + 1;

    public static final short PARA_INDENT_RIGHT_ID = PARA_INDENT_INITLEFT_ID + 1;

    public static final short PARA_BEFORE_ID = PARA_INDENT_RIGHT_ID + 1;

    public static final short PARA_AFTER_ID = PARA_BEFORE_ID + 1;

    public static final short PARA_HORIZONTAL_ID = PARA_AFTER_ID + 1;

    public static final short PARA_VERTICAL_ID = PARA_HORIZONTAL_ID + 1;

    public static final short PARA_SPECIALINDENT_ID = PARA_VERTICAL_ID + 1;

    public static final short PARA_LINESPACE_ID = PARA_SPECIALINDENT_ID + 1;

    public static final short PARA_LINESPACE_TYPE_ID = PARA_LINESPACE_ID + 1;

    public static final short PARA_LEVEL_ID = PARA_LINESPACE_TYPE_ID + 1;

    public static final short PARA_LIST_LEVEL_ID = +PARA_LEVEL_ID + 1;

    public static final short PARA_LIST_ID = PARA_LIST_LEVEL_ID + 1;

    public static final short PARA_PG_BULLET_ID = PARA_LIST_ID + 1;

    public static final short PARA_TABS_CLEAR_POSITION_ID = PARA_PG_BULLET_ID + 1;



    public static final short PAGE_WIDTH_ID = 0x2000;

    public static final short PAGE_HEIGHT_ID = PAGE_WIDTH_ID + 1;

    public static final short PAGE_LEFT_ID = PAGE_HEIGHT_ID + 1;

    public static final short PAGE_RIGHT_ID = PAGE_LEFT_ID + 1;

    public static final short PAGE_TOP_ID = PAGE_RIGHT_ID + 1;

    public static final short PAGE_BOTTOM_ID = PAGE_TOP_ID + 1;

    public static final short PAGE_VERTICAL_ID = PAGE_BOTTOM_ID + 1;

    public static final short PAGE_HEADER_ID = PAGE_VERTICAL_ID + 1;

    public static final short PAGE_FOOTER_ID = PAGE_HEADER_ID + 1;

    public static final short PAGE_HORIZONTAL_ID = PAGE_FOOTER_ID + 1;

    public static final short PAGE_BACKGROUND_COLOR_ID = PAGE_HORIZONTAL_ID + 1;

    public static final short PAGE_BORDER_ID = PAGE_BACKGROUND_COLOR_ID + 1;

    public static final short PAGE_LINEPITCH_ID = PAGE_BORDER_ID + 1;



    public static final short TABLE_TOP_BORDER_ID = 0x3000;

    public static final short TABLE_TOP_BORDER_COLOR_ID = TABLE_TOP_BORDER_ID + 1;

    public static final short TABLE_BOTTOM_BORDER_ID = TABLE_TOP_BORDER_COLOR_ID + 1;

    public static final short TABLE_BOTTOM_BORDER_COLOR_ID = TABLE_BOTTOM_BORDER_ID + 1;

    public static final short TABLE_LEFT_BORDER_ID = TABLE_BOTTOM_BORDER_COLOR_ID + 1;

    public static final short TABLE_LEFT_BORDER_COLOR_ID = TABLE_LEFT_BORDER_ID + 1;

    public static final short TABLE_RIGHT_BORDER_ID = TABLE_LEFT_BORDER_COLOR_ID + 1;

    public static final short TABLE_RIGHT_BORDER_COLOR_ID = TABLE_RIGHT_BORDER_ID + 1;

    public static final short TABLE_ROW_HEIGHT_ID = TABLE_RIGHT_BORDER_COLOR_ID + 1;

    public static final short TABLE_CELL_WIDTH_ID = TABLE_ROW_HEIGHT_ID + 1;

    public static final short TABLE_ROW_HEADER_ID = TABLE_CELL_WIDTH_ID + 1;

    public static final short TABLE_ROW_SPLIT_ID = TABLE_ROW_HEADER_ID + 1;

    public static final short TABLE_CELL_HOR_FIRST_MERGED_ID = TABLE_ROW_SPLIT_ID + 1;

    public static final short TABLE_CELL_HORIZONTAL_MERGED_ID = TABLE_CELL_HOR_FIRST_MERGED_ID + 1;

    public static final short TABLE_CELL_VER_FIRST_MERGED_ID = TABLE_CELL_HORIZONTAL_MERGED_ID + 1;

    public static final short TABLE_CELL_VERTICAL_MERGED_ID = TABLE_CELL_VER_FIRST_MERGED_ID + 1;

    public static final short TABLE_CELL_VERTICAL_ALIGN_ID = TABLE_CELL_VERTICAL_MERGED_ID + 1;

    public static final short TABLE_TOP_MARGIN_ID = TABLE_CELL_VERTICAL_ALIGN_ID + 1;

    public static final short TABLE_BOTTOM_MARGIN_ID = TABLE_TOP_MARGIN_ID + 1;

    public static final short TABLE_LEFT_MARGIN_ID = TABLE_BOTTOM_MARGIN_ID + 1;

    public static final short TABLE_RIGHT_MARGIN_ID = TABLE_LEFT_MARGIN_ID + 1;
}

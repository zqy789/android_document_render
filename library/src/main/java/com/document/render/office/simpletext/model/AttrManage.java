
package com.document.render.office.simpletext.model;

import android.graphics.Color;

import com.document.render.office.common.bulletnumber.ListData;
import com.document.render.office.common.bulletnumber.ListLevel;
import com.document.render.office.constant.MainConstant;
import com.document.render.office.constant.wp.AttrIDConstant;
import com.document.render.office.constant.wp.WPAttrConstant;
import com.document.render.office.simpletext.view.CharAttr;
import com.document.render.office.simpletext.view.PageAttr;
import com.document.render.office.simpletext.view.ParaAttr;
import com.document.render.office.simpletext.view.TableAttr;
import com.document.render.office.system.IControl;


public class AttrManage {
    public static AttrManage am = new AttrManage();


    public static AttrManage instance() {
        return am;
    }


    public boolean hasAttribute(IAttributeSet attr, short attrID) {
        return attr.getAttribute(attrID) != Integer.MIN_VALUE;
    }





    public int getFontStyleID(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.FONT_STYLE_ID);
        if (a == Integer.MIN_VALUE) {
            return -1;
        }
        return a;
    }


    public void setFontStyleID(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.FONT_STYLE_ID, value);
    }



    public int getFontSize(IAttributeSet paraAttr, IAttributeSet leafAttr) {
        int a = leafAttr.getAttribute(AttrIDConstant.FONT_SIZE_ID);
        if (a == Integer.MIN_VALUE) {
            a = paraAttr.getAttribute(AttrIDConstant.FONT_SIZE_ID);
            if (a == Integer.MIN_VALUE) {
                return 12;
            }
        }
        return a;
    }


    public void setFontSize(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.FONT_SIZE_ID, value);
    }


    public int getFontName(IAttributeSet paraAttr, IAttributeSet leafAttr) {
        int a = leafAttr.getAttribute(AttrIDConstant.FONT_NAME_ID);
        if (a == Integer.MIN_VALUE) {
            a = paraAttr.getAttribute(AttrIDConstant.FONT_NAME_ID);
            if (a == Integer.MIN_VALUE) {
                return -1;
            }
        }
        return a;
    }


    public void setFontName(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.FONT_NAME_ID, value);
    }


    public int getFontScale(IAttributeSet paraAttr, IAttributeSet leafAttr) {
        int a = leafAttr.getAttribute(AttrIDConstant.FONT_SCALE_ID);
        if (a == Integer.MIN_VALUE) {
            a = paraAttr.getAttribute(AttrIDConstant.FONT_SCALE_ID);
            if (a == Integer.MIN_VALUE) {
                return 100;
            }
        }
        return a;
    }


    public void setFontScale(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.FONT_SCALE_ID, value);
    }


    public int getFontColor(IAttributeSet paraAttr, IAttributeSet leafAttr) {
        int a = leafAttr.getAttribute(AttrIDConstant.FONT_COLOR_ID);
        if (a == Integer.MIN_VALUE) {
            a = paraAttr.getAttribute(AttrIDConstant.FONT_COLOR_ID);
            if (a == Integer.MIN_VALUE) {
                return Color.BLACK;
            }
        }
        return a;
    }


    public void setFontColor(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.FONT_COLOR_ID, value);
    }


    public boolean getFontBold(IAttributeSet paraAttr, IAttributeSet leafAttr) {
        int a = leafAttr.getAttribute(AttrIDConstant.FONT_BOLD_ID);
        if (a == Integer.MIN_VALUE) {
            a = paraAttr.getAttribute(AttrIDConstant.FONT_BOLD_ID);
            if (a == Integer.MIN_VALUE) {
                return false;
            }
        }
        return a == 1;
    }


    public void setFontBold(IAttributeSet attr, boolean b) {
        attr.setAttribute(AttrIDConstant.FONT_BOLD_ID, b ? 1 : 0);
    }


    public boolean getFontItalic(IAttributeSet paraAttr, IAttributeSet leafAttr) {
        int a = leafAttr.getAttribute(AttrIDConstant.FONT_ITALIC_ID);
        if (a == Integer.MIN_VALUE) {
            a = paraAttr.getAttribute(AttrIDConstant.FONT_ITALIC_ID);
            if (a == Integer.MIN_VALUE) {
                return false;
            }
        }
        return a == 1;
    }


    public void setFontItalic(IAttributeSet attr, boolean b) {
        attr.setAttribute(AttrIDConstant.FONT_ITALIC_ID, b ? 1 : 0);
    }


    public boolean getFontStrike(IAttributeSet paraAttr, IAttributeSet leafAttr) {
        int a = leafAttr.getAttribute(AttrIDConstant.FONT_STRIKE_ID);
        if (a == Integer.MIN_VALUE) {
            a = paraAttr.getAttribute(AttrIDConstant.FONT_STRIKE_ID);
            if (a == Integer.MIN_VALUE) {
                return false;
            }
        }
        return a == 1;
    }


    public void setFontStrike(IAttributeSet attr, boolean b) {
        attr.setAttribute(AttrIDConstant.FONT_STRIKE_ID, b ? 1 : 0);
    }


    public boolean getFontDoubleStrike(IAttributeSet paraAttr, IAttributeSet leafAttr) {
        int a = leafAttr.getAttribute(AttrIDConstant.FONT_DOUBLESTRIKE_ID);
        if (a == Integer.MIN_VALUE) {
            a = paraAttr.getAttribute(AttrIDConstant.FONT_DOUBLESTRIKE_ID);
            if (a == Integer.MIN_VALUE) {
                return false;
            }
        }
        return a == 1;
    }


    public void setFontDoubleStrike(IAttributeSet attr, boolean b) {
        attr.setAttribute(AttrIDConstant.FONT_DOUBLESTRIKE_ID, b ? 1 : 0);
    }


    public int getFontUnderline(IAttributeSet paraAttr, IAttributeSet leafAttr) {
        int a = leafAttr.getAttribute(AttrIDConstant.FONT_UNDERLINE_ID);
        if (a == Integer.MIN_VALUE) {
            a = paraAttr.getAttribute(AttrIDConstant.FONT_UNDERLINE_ID);
            if (a == Integer.MIN_VALUE) {
                return 0;
            }
        }
        return a;
    }


    public void setFontUnderline(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.FONT_UNDERLINE_ID, value);
    }


    public int getFontUnderlineColor(IAttributeSet paraAttr, IAttributeSet leafAttr) {
        int a = leafAttr.getAttribute(AttrIDConstant.FONT_UNDERLINE_COLOR_ID);
        if (a == Integer.MIN_VALUE) {
            a = paraAttr.getAttribute(AttrIDConstant.FONT_UNDERLINE_COLOR_ID);
            if (a == Integer.MIN_VALUE) {
                return getFontColor(paraAttr, leafAttr);
            }
        }
        return a;
    }


    public void setFontUnderlineColr(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.FONT_UNDERLINE_COLOR_ID, value);
    }


    public int getFontScript(IAttributeSet paraAttr, IAttributeSet leafAttr) {
        int a = leafAttr.getAttribute(AttrIDConstant.FONT_SCRIPT_ID);
        if (a == Integer.MIN_VALUE) {
            a = paraAttr.getAttribute(AttrIDConstant.FONT_SCRIPT_ID);
            if (a == Integer.MIN_VALUE) {
                return 0;
            }
        }
        return a;
    }


    public void setFontScript(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.FONT_SCRIPT_ID, value);
    }


    public int getFontHighLight(IAttributeSet paraAttr, IAttributeSet leafAttr) {
        int a = leafAttr.getAttribute(AttrIDConstant.FONT_HIGHLIGHT_ID);
        if (a == Integer.MIN_VALUE) {
            a = paraAttr.getAttribute(AttrIDConstant.FONT_HIGHLIGHT_ID);
            if (a == Integer.MIN_VALUE) {
                return -1;
            }
        }
        return a;
    }


    public void setFontHighLight(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.FONT_HIGHLIGHT_ID, value);
    }


    public int getHperlinkID(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.FONT_HYPERLINK_ID);
        if (a == Integer.MIN_VALUE) {
            return -1;
        }
        return a;
    }


    public void setHyperlinkID(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.FONT_HYPERLINK_ID, value);
    }



    public int getShapeID(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.FONT_SHAPE_ID);
        if (a == Integer.MIN_VALUE) {
            return -1;
        }
        return a;
    }


    public void setShapeID(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.FONT_SHAPE_ID, value);
    }



    public int getFontPageNumberType(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.FONT_PAGE_NUMBER_TYPE_ID);
        if (a == Integer.MIN_VALUE) {
            return -1;
        }
        return a;
    }


    public void setFontPageNumberType(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.FONT_PAGE_NUMBER_TYPE_ID, value);
    }



    public int getFontEncloseChanacterType(IAttributeSet paraAttr, IAttributeSet leafAttr) {
        int a = leafAttr.getAttribute(AttrIDConstant.FONT_ENCLOSE_CHARACTER_TYPE_ID);
        if (a == Integer.MIN_VALUE) {
            return -1;
        }
        return a;
    }


    public void setEncloseChanacterType(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.FONT_ENCLOSE_CHARACTER_TYPE_ID, value);
    }




    public int getParaStyleID(IAttributeSet attr) {

        int a = attr.getAttribute(AttrIDConstant.PARA_STYLE_ID);
        if (a == Integer.MIN_VALUE) {
            return -1;
        }
        return a;
    }


    public void setParaStyleID(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PARA_STYLE_ID, value);
    }


    public int getParaIndentLeft(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PARA_INDENT_LEFT_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setParaIndentLeft(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PARA_INDENT_LEFT_ID, value);
    }

    public int getParaIndentInitLeft(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PARA_INDENT_INITLEFT_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setParaIndentInitLeft(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PARA_INDENT_INITLEFT_ID, value);
    }


    public int getParaIndentRight(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PARA_INDENT_RIGHT_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setParaIndentRight(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PARA_INDENT_RIGHT_ID, value);
    }


    public int getParaBefore(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PARA_BEFORE_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setParaBefore(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PARA_BEFORE_ID, value);
    }


    public int getParaAfter(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PARA_AFTER_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setParaAfter(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PARA_AFTER_ID, value);
    }


    public int getParaSpecialIndent(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PARA_SPECIALINDENT_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setParaSpecialIndent(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PARA_SPECIALINDENT_ID, value);
    }


    public float getParaLineSpace(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PARA_LINESPACE_ID);
        if (a == Integer.MIN_VALUE) {
            return 1;
        }
        return a / 100.f;
    }


    public void setParaLineSpace(IAttributeSet attr, float value) {
        attr.setAttribute(AttrIDConstant.PARA_LINESPACE_ID, (int) (value * 100));
    }


    public int getParaLineSpaceType(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PARA_LINESPACE_TYPE_ID);
        if (a == Integer.MIN_VALUE) {
            return 1;
        }
        return a;
    }


    public void setParaLineSpaceType(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PARA_LINESPACE_TYPE_ID, value);
    }


    public int getParaHorizontalAlign(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PARA_HORIZONTAL_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setParaHorizontalAlign(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PARA_HORIZONTAL_ID, value);
    }


    public int getParaVerticalAlign(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PARA_VERTICAL_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setParaVerticalAlign(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PARA_VERTICAL_ID, value);
    }


    public int getParaLevel(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PARA_LEVEL_ID);
        if (a == Integer.MIN_VALUE) {
            return -1;
        }
        return a;
    }


    public void setParaLevel(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PARA_LEVEL_ID, value);
    }


    public int getParaListLevel(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PARA_LIST_LEVEL_ID);
        if (a == Integer.MIN_VALUE) {
            return -1;
        }
        return a;
    }


    public void setParaListLevel(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PARA_LIST_LEVEL_ID, value);
    }



    public int getParaListID(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PARA_LIST_ID);
        if (a == Integer.MIN_VALUE) {
            return -1;
        }
        return a;
    }


    public void setParaListID(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PARA_LIST_ID, value);
    }


    public int getPGParaBulletID(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PARA_PG_BULLET_ID);
        if (a == Integer.MIN_VALUE) {
            return -1;
        }
        return a;
    }


    public void setPGParaBulletID(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PARA_PG_BULLET_ID, value);
    }


    public int getParaTabsClearPostion(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PARA_TABS_CLEAR_POSITION_ID);
        if (a == Integer.MIN_VALUE) {
            return -1;
        }
        return a;
    }


    public void setParaTabsClearPostion(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PARA_TABS_CLEAR_POSITION_ID, value);
    }




    public int getPageWidth(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PAGE_WIDTH_ID);
        if (a == Integer.MIN_VALUE) {
            return 1000;
        }
        return a;
    }


    public void setPageWidth(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PAGE_WIDTH_ID, value);
    }


    public int getPageHeight(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PAGE_HEIGHT_ID);
        if (a == Integer.MIN_VALUE) {
            return 1200;
        }
        return a;
    }


    public void setPageHeight(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PAGE_HEIGHT_ID, value);
    }


    public void setPageVerticalAlign(IAttributeSet attr, byte verAlign) {
        attr.setAttribute(AttrIDConstant.PAGE_VERTICAL_ID, verAlign);
    }


    public byte getPageVerticalAlign(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PAGE_VERTICAL_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return (byte) a;
    }


    public void setPageHorizontalAlign(IAttributeSet attr, byte verAlign) {
        attr.setAttribute(AttrIDConstant.PAGE_HORIZONTAL_ID, verAlign);
    }


    public byte getPageHorizontalAlign(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PAGE_HORIZONTAL_ID);
        if (a == Integer.MIN_VALUE) {
            return WPAttrConstant.PAGE_H_LEFT;
        }
        return (byte) a;
    }


    public int getPageMarginLeft(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PAGE_LEFT_ID);
        if (a == Integer.MIN_VALUE) {
            return 1800;
        }
        return a;
    }


    public void setPageMarginLeft(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PAGE_LEFT_ID, value);
    }


    public int getPageMarginRight(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PAGE_RIGHT_ID);
        if (a == Integer.MIN_VALUE) {
            return 1800;
        }
        return a;
    }


    public void setPageMarginRight(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PAGE_RIGHT_ID, value);
    }


    public int getPageMarginTop(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PAGE_TOP_ID);
        if (a == Integer.MIN_VALUE) {
            return 1440;
        }
        return a;
    }


    public void setPageMarginTop(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PAGE_TOP_ID, value);
    }


    public int getPageMarginBottom(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PAGE_BOTTOM_ID);
        if (a == Integer.MIN_VALUE) {
            return 1440;
        }
        return a;
    }


    public void setPageMarginBottom(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PAGE_BOTTOM_ID, value);
    }



    public int getPageHeaderMargin(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PAGE_HEADER_ID);
        if (a == Integer.MIN_VALUE) {
            return 850;
        }
        return a;
    }


    public void setPageHeaderMargin(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PAGE_HEADER_ID, value);
    }



    public int getPageFooterMargin(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PAGE_FOOTER_ID);
        if (a == Integer.MIN_VALUE) {
            return 850;
        }
        return a;
    }


    public void setPageFooterMargin(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PAGE_FOOTER_ID, value);
    }



    public int getPageBackgroundColor(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PAGE_BACKGROUND_COLOR_ID);
        if (a == Integer.MIN_VALUE) {
            return Color.WHITE;
        }
        return a;
    }


    public void setPageBackgroundColor(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PAGE_BACKGROUND_COLOR_ID, value);
    }



    public int getPageBorder(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PAGE_BORDER_ID);
        if (a == Integer.MIN_VALUE) {
            return -1;
        }
        return a;
    }


    public void setPageBorder(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PAGE_BORDER_ID, value);
    }


    public int getPageLinePitch(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PAGE_LINEPITCH_ID);
        if (a == Integer.MIN_VALUE) {
            return -1;
        }
        return a;
    }


    public void setPageLinePitch(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PAGE_LINEPITCH_ID, value);
    }




    public int getTableTopBorder(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_TOP_BORDER_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setTableTopBorder(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.TABLE_TOP_BORDER_ID, value);
    }


    public int getTableTopBorderColor(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_TOP_BORDER_COLOR_ID);
        if (a == Integer.MIN_VALUE) {
            return Color.BLACK;
        }
        return a;
    }


    public void setTableTopBorderColor(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.TABLE_TOP_BORDER_COLOR_ID, value);
    }


    public int getTableLeftBorder(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_LEFT_BORDER_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setTableLeftBorder(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.TABLE_LEFT_BORDER_ID, value);
    }


    public int getTableLeftBorderColor(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_LEFT_BORDER_COLOR_ID);
        if (a == Integer.MIN_VALUE) {
            return Color.BLACK;
        }
        return a;
    }


    public void setTableLeftBorderColor(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.TABLE_LEFT_BORDER_COLOR_ID, value);
    }


    public int getTableBottomBorder(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_BOTTOM_BORDER_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setTableBottomBorder(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.TABLE_BOTTOM_BORDER_ID, value);
    }


    public int getTableBottomBorderColor(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_BOTTOM_BORDER_COLOR_ID);
        if (a == Integer.MIN_VALUE) {
            return Color.BLACK;
        }
        return a;
    }


    public void setTableBottomBorderColor(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.TABLE_BOTTOM_BORDER_COLOR_ID, value);
    }


    public int getTableRightBorder(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_RIGHT_BORDER_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setTableRightBorder(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.TABLE_RIGHT_BORDER_ID, value);
    }


    public int getTableRightBorderColor(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_RIGHT_BORDER_COLOR_ID);
        if (a == Integer.MIN_VALUE) {
            return Color.BLACK;
        }
        return a;
    }


    public void setTableRightBorderColor(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.TABLE_RIGHT_BORDER_COLOR_ID, value);
    }


    public int getTableRowHeight(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_ROW_HEIGHT_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setTableRowHeight(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.TABLE_ROW_HEIGHT_ID, value);
    }


    public int getTableCellWidth(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_CELL_WIDTH_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setTableCellWidth(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.TABLE_CELL_WIDTH_ID, value);
    }


    public boolean isTableRowSplit(IAttributeSet attr, int value) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_ROW_SPLIT_ID);
        if (a == Integer.MIN_VALUE) {
            return true;
        }
        return a == 1;
    }


    public void setTableRowSplit(IAttributeSet attr, boolean b) {
        attr.setAttribute(AttrIDConstant.TABLE_ROW_SPLIT_ID, b ? 1 : 0);
    }


    public boolean isTableHeaderRow(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_ROW_HEADER_ID);
        if (a == Integer.MIN_VALUE) {
            return true;
        }
        return a == 1;
    }


    public void setTableHeaderRow(IAttributeSet attr, boolean b) {
        attr.setAttribute(AttrIDConstant.TABLE_ROW_HEADER_ID, b ? 1 : 0);
    }


    public boolean isTableHorFirstMerged(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_CELL_HOR_FIRST_MERGED_ID);
        if (a == Integer.MIN_VALUE) {
            return false;
        }
        return a == 1;
    }


    public void setTableHorFirstMerged(IAttributeSet attr, boolean b) {
        attr.setAttribute(AttrIDConstant.TABLE_CELL_HOR_FIRST_MERGED_ID, b ? 1 : 0);
    }


    public boolean isTableHorMerged(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_CELL_HORIZONTAL_MERGED_ID);
        if (a == Integer.MIN_VALUE) {
            return false;
        }
        return a == 1;
    }


    public void setTableHorMerged(IAttributeSet attr, boolean b) {
        attr.setAttribute(AttrIDConstant.TABLE_CELL_HORIZONTAL_MERGED_ID, b ? 1 : 0);
    }


    public boolean isTableVerFirstMerged(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_CELL_VER_FIRST_MERGED_ID);
        if (a == Integer.MIN_VALUE) {
            return false;
        }
        return a == 1;
    }


    public void setTableVerFirstMerged(IAttributeSet attr, boolean b) {
        attr.setAttribute(AttrIDConstant.TABLE_CELL_VER_FIRST_MERGED_ID, b ? 1 : 0);
    }


    public boolean isTableVerMerged(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_CELL_VERTICAL_MERGED_ID);
        if (a == Integer.MIN_VALUE) {
            return false;
        }
        return a == 1;
    }


    public void setTableVerMerged(IAttributeSet attr, boolean b) {
        attr.setAttribute(AttrIDConstant.TABLE_CELL_VERTICAL_MERGED_ID, b ? 1 : 0);
    }


    public int getTableCellVerAlign(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_CELL_VERTICAL_ALIGN_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setTableCellVerAlign(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.TABLE_CELL_VERTICAL_ALIGN_ID, value);
    }


    public int getTableTopMargin(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_TOP_MARGIN_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setTableTopMargin(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.TABLE_TOP_MARGIN_ID, value);
    }


    public int getTableBottomMargin(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_BOTTOM_MARGIN_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setTableBottomMargin(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.TABLE_BOTTOM_MARGIN_ID, value);
    }


    public int getTableLeftMargin(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_LEFT_MARGIN_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setTableLeftMargin(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.TABLE_LEFT_MARGIN_ID, value);
    }



    public int getTableRightMargin(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.TABLE_RIGHT_MARGIN_ID);
        if (a == Integer.MIN_VALUE) {
            return 0;
        }
        return a;
    }


    public void setTableRightMargin(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.TABLE_RIGHT_MARGIN_ID, value);
    }



    public int getTableCellTableBackground(IAttributeSet attr) {
        int a = attr.getAttribute(AttrIDConstant.PAGE_BACKGROUND_COLOR_ID);
        if (a == Integer.MIN_VALUE) {
            return -1;
        }
        return a;
    }


    public void setTableCellBackground(IAttributeSet attr, int value) {
        attr.setAttribute(AttrIDConstant.PAGE_BACKGROUND_COLOR_ID, value);
    }





    public void fillPageAttr(PageAttr pageAttr, IAttributeSet attr) {
        pageAttr.reset();

        pageAttr.verticalAlign = getPageVerticalAlign(attr);
        pageAttr.horizontalAlign = getPageHorizontalAlign(attr);

        pageAttr.pageWidth = (int) (getPageWidth(attr) * MainConstant.TWIPS_TO_PIXEL);
        pageAttr.pageHeight = (int) (getPageHeight(attr) * MainConstant.TWIPS_TO_PIXEL);
        pageAttr.topMargin = (int) (getPageMarginTop(attr) * MainConstant.TWIPS_TO_PIXEL);
        pageAttr.bottomMargin = (int) (getPageMarginBottom(attr) * MainConstant.TWIPS_TO_PIXEL);
        pageAttr.rightMargin = (int) (getPageMarginRight(attr) * MainConstant.TWIPS_TO_PIXEL);
        pageAttr.leftMargin = (int) (getPageMarginLeft(attr) * MainConstant.TWIPS_TO_PIXEL);
        pageAttr.headerMargin = (int) (getPageHeaderMargin(attr) * MainConstant.TWIPS_TO_PIXEL);
        pageAttr.footerMargin = (int) (getPageFooterMargin(attr) * MainConstant.TWIPS_TO_PIXEL);

        pageAttr.pageBRColor = getPageBackgroundColor(attr);
        pageAttr.pageBorder = getPageBorder(attr);
        pageAttr.pageLinePitch = (getPageLinePitch(attr) * MainConstant.TWIPS_TO_PIXEL);
    }



    public void fillParaAttr(IControl control, ParaAttr paraAttr, IAttributeSet attr) {
        paraAttr.reset();
        paraAttr.tabClearPosition = (int) (getParaTabsClearPostion(attr) * MainConstant.TWIPS_TO_PIXEL);
        paraAttr.leftIndent = (int) (getParaIndentLeft(attr) * MainConstant.TWIPS_TO_PIXEL);
        paraAttr.rightIndent = (int) (getParaIndentRight(attr) * MainConstant.TWIPS_TO_PIXEL);
        paraAttr.beforeSpace = (int) (getParaBefore(attr) * MainConstant.TWIPS_TO_PIXEL);
        paraAttr.afterSpace = (int) (getParaAfter(attr) * MainConstant.TWIPS_TO_PIXEL);

        paraAttr.lineSpaceType = (byte) getParaLineSpaceType(attr);

        paraAttr.lineSpaceValue = getParaLineSpace(attr);

        if (paraAttr.lineSpaceType == WPAttrConstant.LINE_SAPCE_LEAST
                || paraAttr.lineSpaceType == WPAttrConstant.LINE_SPACE_EXACTLY) {
            paraAttr.lineSpaceValue *= MainConstant.TWIPS_TO_PIXEL;
        }

        paraAttr.horizontalAlignment = (byte) getParaHorizontalAlign(attr);

        paraAttr.specialIndentValue = (int) (getParaSpecialIndent(attr) * MainConstant.TWIPS_TO_PIXEL);

        paraAttr.listID = getParaListID(attr);

        paraAttr.listLevel = (byte) getParaListLevel(attr);

        paraAttr.pgBulletID = getPGParaBulletID(attr);


        if (paraAttr.listID >= 0 && paraAttr.listLevel >= 0 && control != null) {
            ListData listData = control.getSysKit().getListManage().getListData(paraAttr.listID);
            if (listData != null) {
                ListLevel listLevel = listData.getLevel(paraAttr.listLevel);
                if (listLevel != null) {

                    paraAttr.listTextIndent = (int) (listLevel.getTextIndent() * MainConstant.TWIPS_TO_PIXEL);

                    paraAttr.listAlignIndent = paraAttr.listTextIndent + (int) (listLevel.getSpecialIndent() * MainConstant.TWIPS_TO_PIXEL);

                    if (paraAttr.leftIndent - paraAttr.listTextIndent == 0
                            || paraAttr.leftIndent == 0) {

                        if (paraAttr.specialIndentValue == 0) {
                            paraAttr.specialIndentValue = -(paraAttr.listTextIndent - paraAttr.listAlignIndent);
                        }

                        if (paraAttr.specialIndentValue < 0) {
                            paraAttr.leftIndent = paraAttr.listAlignIndent;
                            paraAttr.listAlignIndent = 0;
                        }

                        else if (paraAttr.listAlignIndent > paraAttr.specialIndentValue) {
                            paraAttr.specialIndentValue += paraAttr.listAlignIndent;

                        }
                    }

                    else {

                        if (paraAttr.leftIndent + paraAttr.listAlignIndent == paraAttr.listTextIndent) {
                            paraAttr.leftIndent = paraAttr.listAlignIndent;
                        }

                        if (paraAttr.specialIndentValue >= 0) {
                            paraAttr.listAlignIndent = paraAttr.specialIndentValue;
                        }

                        else {
                            paraAttr.listAlignIndent = 0;
                        }

                        if (paraAttr.specialIndentValue == 0 && paraAttr.listTextIndent - paraAttr.leftIndent > 0) {
                            paraAttr.specialIndentValue -= paraAttr.listTextIndent - paraAttr.leftIndent;
                        }
                    }


                }
            }
        }
    }


    public void fillCharAttr(CharAttr charAttr, IAttributeSet paraAttr, IAttributeSet leafAttr) {
        charAttr.reset();
        charAttr.fontIndex = getFontName(paraAttr, leafAttr);
        charAttr.fontSize = getFontSize(paraAttr, leafAttr);
        charAttr.fontScale = getFontScale(paraAttr, leafAttr);
        charAttr.fontColor = getFontColor(paraAttr, leafAttr);
        charAttr.isBold = getFontBold(paraAttr, leafAttr);
        charAttr.isItalic = getFontItalic(paraAttr, leafAttr);
        charAttr.isStrikeThrough = getFontStrike(paraAttr, leafAttr);
        charAttr.isDoubleStrikeThrough = getFontDoubleStrike(paraAttr, leafAttr);
        charAttr.underlineType = getFontUnderline(paraAttr, leafAttr);
        charAttr.underlineColor = getFontUnderlineColor(paraAttr, leafAttr);
        charAttr.subSuperScriptType = (short) getFontScript(paraAttr, leafAttr);
        charAttr.highlightedColor = getFontHighLight(paraAttr, leafAttr);
        charAttr.encloseType = (byte) getFontEncloseChanacterType(paraAttr, leafAttr);
        charAttr.pageNumberType = (byte) getFontPageNumberType(leafAttr);
    }



    public void fillTableAttr(TableAttr tableAttr, IAttributeSet attr) {

        tableAttr.topMargin = 0;
        tableAttr.leftMargin = 7;
        tableAttr.rightMargin = 7;
        tableAttr.bottomMargin = 0;
        tableAttr.cellWidth = (int) (getTableCellWidth(attr) * MainConstant.TWIPS_TO_PIXEL);
        tableAttr.cellVerticalAlign = (byte) getTableCellVerAlign(attr);
        tableAttr.cellBackground = getTableCellTableBackground(attr);
    }


    public void dispose() {
    }
}

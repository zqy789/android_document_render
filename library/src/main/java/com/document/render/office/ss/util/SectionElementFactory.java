
package com.document.render.office.ss.util;

import com.document.render.office.constant.MainConstant;
import com.document.render.office.constant.SSConstant;
import com.document.render.office.constant.wp.WPAttrConstant;
import com.document.render.office.constant.wp.WPModelConstant;
import com.document.render.office.fc.hssf.record.common.UnicodeString;
import com.document.render.office.fc.hssf.record.common.UnicodeString.FormatRun;
import com.document.render.office.fc.hssf.usermodel.HSSFRichTextString;
import com.document.render.office.fc.hssf.usermodel.HSSFTextbox;
import com.document.render.office.fc.ppt.attribute.ParaAttr;
import com.document.render.office.fc.ppt.attribute.RunAttr;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.simpletext.model.AttrManage;
import com.document.render.office.simpletext.model.AttributeSetImpl;
import com.document.render.office.simpletext.model.IAttributeSet;
import com.document.render.office.simpletext.model.LeafElement;
import com.document.render.office.simpletext.model.ParagraphElement;
import com.document.render.office.simpletext.model.SectionElement;
import com.document.render.office.ss.model.baseModel.Cell;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.model.style.CellStyle;

import java.util.Iterator;


public class SectionElementFactory {
    private static Workbook book;
    private static int offset;
    private static ParagraphElement paraElem;
    private static AttributeSetImpl attrLayout;
    private static LeafElement leaf;

    public static SectionElement getSectionElement(Workbook workbook, UnicodeString unicodeString, Cell cell) {
        book = workbook;

        CellStyle cellStyle = cell.getCellStyle();

        SectionElement secElem = new SectionElement();

        secElem.setStartOffset(0);

        IAttributeSet attr = secElem.getAttribute();


        AttrManage.instance().setPageMarginLeft(attr, Math.round(SSConstant.SHEET_SPACETOBORDER * MainConstant.PIXEL_TO_TWIPS));

        AttrManage.instance().setPageMarginRight(attr, Math.round(SSConstant.SHEET_SPACETOBORDER * MainConstant.PIXEL_TO_TWIPS));

        AttrManage.instance().setPageMarginTop(attr, 0);

        AttrManage.instance().setPageMarginBottom(attr, 0);

        byte verAlign;
        switch (cellStyle.getVerticalAlign()) {
            case CellStyle.VERTICAL_TOP:
                verAlign = WPAttrConstant.PAGE_V_TOP;
                break;
            case CellStyle.VERTICAL_CENTER:
                verAlign = WPAttrConstant.PAGE_V_CENTER;
                break;
            case CellStyle.VERTICAL_JUSTIFY:
                verAlign = WPAttrConstant.PAGE_V_JUSTIFIED;
                break;
            case CellStyle.VERTICAL_BOTTOM:
                verAlign = WPAttrConstant.PAGE_V_BOTTOM;
                break;
            default:
                verAlign = WPAttrConstant.PAGE_V_TOP;
                break;
        }
        AttrManage.instance().setPageVerticalAlign(attr, verAlign);

        int font = cellStyle.getFontIndex();

        offset = 0;
        int pos = processParagraph(secElem, unicodeString, cellStyle, cell);
        if (pos != 0) {
            secElem.setEndOffset(pos);
        } else {
            secElem.dispose();
            secElem = null;
        }

        dispose();

        return secElem;
    }

    public static SectionElement getSectionElement(Workbook workbook, HSSFTextbox textbox, Rectangle rect) {
        book = workbook;



        SectionElement secElem = new SectionElement();

        secElem.setStartOffset(0);

        IAttributeSet attr = secElem.getAttribute();

        AttrManage.instance().setPageWidth(attr, Math.round(rect.width * MainConstant.PIXEL_TO_TWIPS));

        AttrManage.instance().setPageHeight(attr, Math.round(rect.height * MainConstant.PIXEL_TO_TWIPS));

        AttrManage.instance().setPageMarginLeft(attr, Math.round(textbox.getMarginLeft() * MainConstant.PIXEL_TO_TWIPS));

        AttrManage.instance().setPageMarginTop(attr, Math.round(textbox.getMarginTop() * MainConstant.PIXEL_TO_TWIPS));
        AttrManage.instance().setPageMarginRight(attr, Math.round(textbox.getMarginRight() * MainConstant.PIXEL_TO_TWIPS));
        AttrManage.instance().setPageMarginBottom(attr, Math.round(textbox.getMarginBottom() * MainConstant.PIXEL_TO_TWIPS));

        byte valign = 0;
        switch (textbox.getVerticalAlignment()) {
            case HSSFTextbox.VERTICAL_ALIGNMENT_TOP:
                valign = WPAttrConstant.PAGE_V_TOP;
                break;
            case HSSFTextbox.VERTICAL_ALIGNMENT_CENTER:
            case HSSFTextbox.VERTICAL_ALIGNMENT_JUSTIFY:
            case HSSFTextbox.VERTICAL_ALIGNMENT_DISTRIBUTED:
                valign = WPAttrConstant.PAGE_V_CENTER;
                break;
            case HSSFTextbox.VERTICAL_ALIGNMENT_BOTTOM:
                valign = WPAttrConstant.PAGE_V_BOTTOM;
                break;
        }
        AttrManage.instance().setPageVerticalAlign(attr, valign);

        int pos = processParagraph(secElem, textbox);
        secElem.setEndOffset(pos);

        dispose();

        return secElem;
    }

    private static int processParagraph(SectionElement secElem, UnicodeString unicodeString, CellStyle cellStyle, Cell cell) {
        offset = 0;
        String text = unicodeString.getString();

        byte halign = 0;
        switch (cellStyle.getHorizontalAlign()) {
            case CellStyle.ALIGN_LEFT:
                halign = WPAttrConstant.PARA_HOR_ALIGN_LEFT;
                break;
            case CellStyle.ALIGN_CENTER:
            case CellStyle.ALIGN_JUSTIFY:
            case CellStyle.ALIGN_CENTER_SELECTION:
                halign = WPAttrConstant.PARA_HOR_ALIGN_CENTER;
                break;
            case CellStyle.ALIGN_RIGHT:
                halign = WPAttrConstant.PARA_HOR_ALIGN_RIGHT;
                break;
        }

        paraElem = new ParagraphElement();
        paraElem.setStartOffset(offset);
        attrLayout = new AttributeSetImpl();
        ParaAttr.instance().setParaAttribute(cellStyle, paraElem.getAttribute(), attrLayout);
        AttrManage.instance().setParaHorizontalAlign(paraElem.getAttribute(), halign);

        if (unicodeString.getFormatRunCount() == 0) {
            processParagraph_SubString(secElem,
                    cellStyle,
                    text,
                    cellStyle.getFontIndex(),
                    halign);
        } else {
            Iterator<FormatRun> iter = unicodeString.formatIterator();
            FormatRun begin = null;
            FormatRun end = iter.next();

            String subString = text.substring(0, end.getCharacterPos());
            if (!cellStyle.isWrapText()) {
                subString = subString.replace("\n", "");
            }

            processParagraph_SubString(secElem,
                    cellStyle,
                    subString,
                    cellStyle.getFontIndex(),
                    halign);

            begin = end;


            while (iter.hasNext()) {
                end = iter.next();
                if (end.getCharacterPos() > text.length()) {
                    break;
                }

                subString = text.substring(begin.getCharacterPos(), end.getCharacterPos());
                if (!cellStyle.isWrapText()) {
                    subString = subString.replace("\n", "");
                }

                processParagraph_SubString(secElem,
                        cellStyle,
                        subString,
                        begin.getFontIndex(),
                        halign);

                begin = end;
            }


            subString = text.substring(begin.getCharacterPos());
            if (!cellStyle.isWrapText()) {
                subString = subString.replace("\n", "");
            }

            processParagraph_SubString(secElem,
                    cellStyle,
                    subString,
                    begin.getFontIndex(),
                    halign);

            if (leaf != null) {
                leaf.setText(leaf.getText(null) + "\n");
                offset++;
            }
        }


        if (leaf != null && paraElem.getLeaf(leaf.getStartOffset()) == null) {
            leaf.setEndOffset(offset);
            paraElem.appendLeaf(leaf);
        }

        if (paraElem != null && secElem.getElement(paraElem.getStartOffset()) == null) {
            paraElem.setEndOffset(offset);
            secElem.appendParagraph(paraElem, WPModelConstant.MAIN);
        }

        return offset;
    }

    private static void processParagraph_SubString(SectionElement secElem, CellStyle cellStyle, String subString, int fontIndex, byte hAlign) {
        if (!subString.contains("\n")) {
            leaf = new LeafElement(subString);

            RunAttr.instance().setRunAttribute(book, fontIndex, null, leaf.getAttribute(), attrLayout);

            leaf.setStartOffset(offset);
            offset += subString.length();

            leaf.setEndOffset(offset);
            paraElem.appendLeaf(leaf);
        } else {
            int index = subString.indexOf('\n');
            while (index >= 0) {
                offset = processBreakLine(secElem, cellStyle, fontIndex, hAlign, subString.substring(0, index), true);
                if (index + 1 < subString.length()) {
                    subString = subString.substring(index + 1, subString.length());
                    index = subString.indexOf('\n');
                } else {
                    subString = null;
                    break;
                }
            }

            if (subString != null) {
                offset = processBreakLine(secElem, cellStyle, fontIndex, hAlign, subString, true);
            }
        }
    }

    private static int processParagraph(SectionElement secElem, HSSFTextbox textbox) {
        offset = 0;
        HSSFRichTextString richText = textbox.getString();
        String text = richText.getString();

        byte halign = 0;
        switch (textbox.getHorizontalAlignment()) {
            case HSSFTextbox.HORIZONTAL_ALIGNMENT_LEFT:
                halign = WPAttrConstant.PARA_HOR_ALIGN_LEFT;
                break;
            case HSSFTextbox.HORIZONTAL_ALIGNMENT_CENTERED:
            case HSSFTextbox.HORIZONTAL_ALIGNMENT_JUSTIFIED:
            case HSSFTextbox.HORIZONTAL_ALIGNMENT_DISTRIBUTED:
                halign = WPAttrConstant.PARA_HOR_ALIGN_CENTER;
                break;
            case HSSFTextbox.HORIZONTAL_ALIGNMENT_RIGHT:
                halign = WPAttrConstant.PARA_HOR_ALIGN_RIGHT;
                break;
        }

        paraElem = new ParagraphElement();
        paraElem.setStartOffset(offset);
        attrLayout = new AttributeSetImpl();
        AttrManage.instance().setParaHorizontalAlign(paraElem.getAttribute(), halign);

        Iterator<FormatRun> iter = richText.getUnicodeString().formatIterator();
        FormatRun begin = iter.next();

        FormatRun end = null;
        while (iter.hasNext()) {
            end = iter.next();
            if (end.getCharacterPos() > text.length()) {
                break;
            }

            processParagraph_SubString(secElem,
                    null,
                    text.substring(begin.getCharacterPos(), end.getCharacterPos()),
                    begin.getFontIndex(),
                    halign);

            begin = end;
        }

        processParagraph_SubString(secElem, null, text.substring(begin.getCharacterPos()), begin.getFontIndex(), halign);

        if (leaf != null && paraElem.getLeaf(leaf.getStartOffset()) == null) {
            leaf.setText(leaf.getText(null) + "\n");
            offset++;
            leaf.setEndOffset(offset);
            paraElem.appendLeaf(leaf);
        }

        if (paraElem != null && secElem.getElement(paraElem.getStartOffset()) == null) {
            paraElem.setEndOffset(offset);
            secElem.appendParagraph(paraElem, WPModelConstant.MAIN);
        }

        return offset;
    }


    private static int processBreakLine(SectionElement secElem, CellStyle cellStyle, int fontID, byte hAlign, String text, boolean paraEnd) {
        if (text == null || text.length() == 0) {
            if (leaf != null) {

                leaf.setText(leaf.getText(null) + "\n");
                offset++;
                leaf.setEndOffset(offset);
            } else {

                leaf = new LeafElement("\n");

                RunAttr.instance().setRunAttribute(book, fontID, null, leaf.getAttribute(), attrLayout);

                leaf.setStartOffset(offset);
                offset++;
                leaf.setEndOffset(offset);
                paraElem.appendLeaf(leaf);
            }
            paraElem.setEndOffset(offset);
            secElem.appendParagraph(paraElem, WPModelConstant.MAIN);


            paraElem = new ParagraphElement();
            paraElem.setStartOffset(offset);
            attrLayout = new AttributeSetImpl();
            ParaAttr.instance().setParaAttribute(cellStyle, paraElem.getAttribute(), attrLayout);
            AttrManage.instance().setParaHorizontalAlign(paraElem.getAttribute(), hAlign);
            leaf = null;
        } else {
            leaf = new LeafElement(text);

            RunAttr.instance().setRunAttribute(book, fontID, null, leaf.getAttribute(), attrLayout);

            leaf.setStartOffset(offset);
            offset += text.length();

            leaf.setEndOffset(offset);
            paraElem.appendLeaf(leaf);

            if (paraEnd) {
                leaf.setText(leaf.getText(null) + "\n");
                offset++;
                leaf.setEndOffset(offset);
                paraElem.setEndOffset(offset);
                secElem.appendParagraph(paraElem, WPModelConstant.MAIN);


                paraElem = new ParagraphElement();
                paraElem.setStartOffset(offset);
                attrLayout = new AttributeSetImpl();
                ParaAttr.instance().setParaAttribute(cellStyle, paraElem.getAttribute(), attrLayout);
                AttrManage.instance().setParaHorizontalAlign(paraElem.getAttribute(), hAlign);
                leaf = null;
            }

        }

        return offset;
    }

    private static void dispose() {
        leaf = null;
        paraElem = null;
        book = null;
        offset = 0;
        attrLayout = null;
    }
}

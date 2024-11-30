
package com.document.render.office.fc.xls.Reader;

import com.document.render.office.constant.MainConstant;
import com.document.render.office.constant.SSConstant;
import com.document.render.office.constant.wp.WPAttrConstant;
import com.document.render.office.constant.wp.WPModelConstant;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.ppt.attribute.ParaAttr;
import com.document.render.office.fc.ppt.attribute.RunAttr;
import com.document.render.office.simpletext.model.AttrManage;
import com.document.render.office.simpletext.model.AttributeSetImpl;
import com.document.render.office.simpletext.model.IAttributeSet;
import com.document.render.office.simpletext.model.LeafElement;
import com.document.render.office.simpletext.model.ParagraphElement;
import com.document.render.office.simpletext.model.SectionElement;
import com.document.render.office.ss.model.baseModel.Cell;
import com.document.render.office.ss.model.baseModel.Row;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.model.style.CellStyle;
import com.document.render.office.ss.util.ReferenceUtil;

import java.util.List;



public class CellReader {
    private final static short CELLTYPE_BOOLEAN = 0;
    private final static short CELLTYPE_NUMBER = 1;
    private final static short CELLTYPE_ERROR = 2;
    private final static short CELLTYPE_SHAREDSTRING = 3;
    private final static short CELLTYPE_STRING = 4;
    private final static short CELLTYPE_INLINESTRING = 5;

    private static CellReader reader = new CellReader();
    private int offset;
    private ParagraphElement paraElem;
    private AttributeSetImpl attrLayout;
    private LeafElement leaf;


    public static CellReader instance() {
        return reader;
    }

    private boolean isValidateCell(Sheet sheet, Element cellElement) {
        if (cellElement.attributeValue("t") != null) {
            return true;
        }


        Element v = cellElement.element("v");
        if (v != null) {
            return true;
        }


        Workbook book = sheet.getWorkbook();

        if (cellElement.attributeValue("s") != null) {
            int styleIndex = Integer.parseInt(cellElement.attributeValue("s"));
            CellStyle cellStyle = book.getCellStyle(styleIndex);
            if (Workbook.isValidateStyle(cellStyle)) {

                return true;
            }
        } else {

            String ref = cellElement.attributeValue("r");
            int col = ReferenceUtil.instance().getColumnIndex(ref);
            Row row = sheet.getRow(ReferenceUtil.instance().getRowIndex(ref));

            if ((row != null && Workbook.isValidateStyle(book.getCellStyle(row.getRowStyle())))
                    || Workbook.isValidateStyle(book.getCellStyle(col))) {
                return true;
            }
        }

        return false;
    }


    public Cell getCell(Sheet sheet, Element cellElement) {
        Cell cell;

        if (!isValidateCell(sheet, cellElement)) {
            return null;
        }


        short cellType = getCellType(cellElement.attributeValue("t"));
        switch (cellType) {
            case CELLTYPE_BOOLEAN:
                cell = new Cell(Cell.CELL_TYPE_BOOLEAN);
                break;
            case CELLTYPE_NUMBER:
                cell = new Cell(Cell.CELL_TYPE_NUMERIC);
                break;
            case CELLTYPE_INLINESTRING:
            case CELLTYPE_SHAREDSTRING:
            case CELLTYPE_STRING:
            case CELLTYPE_ERROR:
                cell = new Cell(Cell.CELL_TYPE_STRING);
                break;
            default:
                cell = new Cell(Cell.CELL_TYPE_BLANK);
                break;
        }


        String ref = cellElement.attributeValue("r");
        cell.setColNumber(ReferenceUtil.instance().getColumnIndex(ref));
        cell.setRowNumber(ReferenceUtil.instance().getRowIndex(ref));

        Workbook book = sheet.getWorkbook();

        int styleIndex = 0;
        if (cellElement.attributeValue("s") != null) {
            styleIndex = Integer.parseInt(cellElement.attributeValue("s"));
        } else {
            styleIndex = sheet.getColumnStyle(cell.getColNumber());
        }
        cell.setCellStyle(styleIndex);


        Element v = cellElement.element("v");
        if (v != null) {
            String value = v.getText();

            if (cellType == CELLTYPE_SHAREDSTRING) {
                int sstIndex = Integer.parseInt(value);

                Object sst = book.getSharedItem(sstIndex);
                if (sst instanceof Element) {

                    cell.setSheet(sheet);
                    SectionElement secElement = processComplexSST(cell, (Element) sst);
                    sstIndex = book.addSharedString(secElement);
                }
                cell.setCellValue(sstIndex);
            } else if (cellType == CELLTYPE_STRING || cellType == CELLTYPE_ERROR) {
                cell.setCellValue(book.addSharedString(value));
            } else if (cellType == CELLTYPE_NUMBER) {
                cell.setCellValue(Double.parseDouble(value));
            } else if (cellType == CELLTYPE_BOOLEAN) {
                cell.setCellValue(Integer.parseInt(value) != 0);
            } else {
                cell.setCellValue(value);
            }
        }


        return cell;
    }


    private short getCellType(String type) {
        if (type == null || type.equalsIgnoreCase("n")) {
            return CELLTYPE_NUMBER;
        } else if (type.equalsIgnoreCase("b")) {
            return CELLTYPE_BOOLEAN;
        } else if (type.equalsIgnoreCase("s")) {
            return CELLTYPE_SHAREDSTRING;
        } else if (type.equalsIgnoreCase("str")) {
            return CELLTYPE_STRING;
        } else if (type.equalsIgnoreCase("inlineStr")) {
            return CELLTYPE_INLINESTRING;
        } else {
            return CELLTYPE_ERROR;
        }
    }

    private SectionElement processComplexSST(Cell cell, Element si) {
        SectionElement secElem = new SectionElement();

        secElem.setStartOffset(0);

        IAttributeSet attr = secElem.getAttribute();


        AttrManage.instance().setPageMarginLeft(attr, Math.round(SSConstant.SHEET_SPACETOBORDER * MainConstant.PIXEL_TO_TWIPS));

        AttrManage.instance().setPageMarginRight(attr, Math.round(SSConstant.SHEET_SPACETOBORDER * MainConstant.PIXEL_TO_TWIPS));

        AttrManage.instance().setPageMarginTop(attr, 0);

        AttrManage.instance().setPageMarginBottom(attr, 0);

        byte verAlign;
        switch (cell.getCellStyle().getVerticalAlign()) {
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

        int font = cell.getCellStyle().getFontIndex();

        offset = 0;
        paraElem = new ParagraphElement();
        paraElem.setStartOffset(offset);
        attrLayout = new AttributeSetImpl();

        ParaAttr.instance().setParaAttribute(cell.getCellStyle(), paraElem.getAttribute(), attrLayout);

        paraElem = processRun(cell, secElem, si, font);

        paraElem.setEndOffset(offset);
        secElem.appendParagraph(paraElem, WPModelConstant.MAIN);
        secElem.setEndOffset(offset);

        offset = 0;
        paraElem = null;
        attrLayout = null;
        leaf = null;

        return secElem;
    }

    private ParagraphElement processRun(Cell cell, SectionElement secElem, Element si, int fontID) {
        Workbook book = cell.getSheet().getWorkbook();
        List<Element> rs = si.elements();
        CellStyle cellStyle = cell.getCellStyle();


        boolean ignoreNewline = false;
        if (!cellStyle.isWrapText()) {
            ignoreNewline = true;
        }


        if (rs.size() == 0) {
            leaf = new LeafElement("\n");
            RunAttr.instance().setRunAttribute(book, fontID, null, leaf.getAttribute(), attrLayout);

            leaf.setStartOffset(offset);
            offset++;
            leaf.setEndOffset(offset);
            paraElem.appendLeaf(leaf);
            return paraElem;
        }

        for (Element r : rs) {
            if (r.getName().equalsIgnoreCase("r")) {
                Element t = r.element("t");
                if (t != null) {
                    String text = t.getText();
                    int len = text.length();
                    if (len > 0) {
                        if (ignoreNewline) {
                            text = text.replace("\n", "");
                            leaf = new LeafElement(text);

                            RunAttr.instance().setRunAttribute(book, fontID, r.element("rPr"), leaf.getAttribute(), attrLayout);

                            leaf.setStartOffset(offset);
                            offset += text.length();

                            leaf.setEndOffset(offset);
                            paraElem.appendLeaf(leaf);
                        } else {
                            if (!text.contains("\n")) {
                                leaf = new LeafElement(text);

                                RunAttr.instance().setRunAttribute(book, fontID, r.element("rPr"), leaf.getAttribute(), attrLayout);

                                leaf.setStartOffset(offset);
                                offset += text.length();

                                leaf.setEndOffset(offset);
                                paraElem.appendLeaf(leaf);
                            } else {
                                processBreakLine(cell, secElem, fontID, r, text);
                            }
                        }

                    }
                }
            }

        }
        if (leaf != null) {
            leaf.setText(leaf.getText(null) + "\n");
            offset++;
        }

        return paraElem;
    }


    private void processBreakLine(Cell cell, SectionElement secElem, int fontID, Element r, String text) {
        Workbook book = cell.getSheet().getWorkbook();
        if (text == null || text.length() == 0) {
            return;
        }

        int len = text.length();
        if (text.charAt(0) == '\n') {

            if (leaf != null) {
                leaf.setText(leaf.getText(null) + "\n");
                offset++;
            } else {
                leaf = new LeafElement("\n");

                RunAttr.instance().setRunAttribute(book, fontID, r.element("rPr"), leaf.getAttribute(), attrLayout);

                leaf.setStartOffset(offset);
                offset++;
                leaf.setEndOffset(offset);
                paraElem.appendLeaf(leaf);
            }

            paraElem.setEndOffset(offset);
            secElem.appendParagraph(paraElem, WPModelConstant.MAIN);
            leaf = null;

            text = text.substring(1);

            paraElem = new ParagraphElement();
            paraElem.setStartOffset(offset);
            attrLayout = new AttributeSetImpl();
            ParaAttr.instance().setParaAttribute(cell.getCellStyle(), paraElem.getAttribute(), attrLayout);

            processBreakLine(cell, secElem, fontID, r, text);
        } else if (text.charAt(len - 1) == '\n') {

            leaf = new LeafElement(text.substring(0, text.indexOf("\n") + 1));

            RunAttr.instance().setRunAttribute(book, fontID, r.element("rPr"), leaf.getAttribute(), attrLayout);

            leaf.setStartOffset(offset);
            offset += leaf.getText(null).length();

            leaf.setEndOffset(offset);
            paraElem.appendLeaf(leaf);

            paraElem.setEndOffset(offset);
            secElem.appendParagraph(paraElem, WPModelConstant.MAIN);

            text = text.substring(text.indexOf("\n") + 1);
            processBreakLine(cell, secElem, fontID, r, text);
        } else {
            String[] items = text.split("\n");
            int cnt = items.length;


            text = items[0] + "\n";
            leaf = new LeafElement(text);

            RunAttr.instance().setRunAttribute(book, fontID, r.element("rPr"), leaf.getAttribute(), attrLayout);

            leaf.setStartOffset(offset);
            offset += text.length();

            leaf.setEndOffset(offset);
            paraElem.appendLeaf(leaf);
            paraElem.setEndOffset(offset);
            secElem.appendParagraph(paraElem, WPModelConstant.MAIN);


            int index = 1;
            while (index < cnt - 1) {
                paraElem = new ParagraphElement();
                paraElem.setStartOffset(offset);
                attrLayout = new AttributeSetImpl();
                ParaAttr.instance().setParaAttribute(cell.getCellStyle(), paraElem.getAttribute(), attrLayout);
                text = items[index] + "\n";
                leaf = new LeafElement(text);

                RunAttr.instance().setRunAttribute(book, fontID, r.element("rPr"), leaf.getAttribute(), attrLayout);

                leaf.setStartOffset(offset);
                offset += text.length();

                leaf.setEndOffset(offset);
                paraElem.appendLeaf(leaf);
                paraElem.setEndOffset(offset);
                secElem.appendParagraph(paraElem, WPModelConstant.MAIN);

                index++;
            }


            paraElem = new ParagraphElement();
            paraElem.setStartOffset(offset);
            attrLayout = new AttributeSetImpl();
            ParaAttr.instance().setParaAttribute(cell.getCellStyle(), paraElem.getAttribute(), attrLayout);
            text = items[cnt - 1];
            leaf = new LeafElement(text);

            RunAttr.instance().setRunAttribute(book, fontID, r.element("rPr"), leaf.getAttribute(), attrLayout);

            leaf.setStartOffset(offset);
            offset += text.length();

            leaf.setEndOffset(offset);
            paraElem.appendLeaf(leaf);
        }
    }


    public boolean searchContent(Element cellElement, String key) {
        Element v = cellElement.element("v");
        if (v != null && getCellType(cellElement.attributeValue("t")) != CELLTYPE_SHAREDSTRING && v.getText().toLowerCase().contains(key)) {
            return true;
        }

        return false;
    }
}

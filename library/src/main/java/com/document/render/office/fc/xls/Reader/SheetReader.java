
package com.document.render.office.fc.xls.Reader;

import com.document.render.office.common.hyperlink.Hyperlink;
import com.document.render.office.constant.MainConstant;
import com.document.render.office.constant.SSConstant;
import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.ElementHandler;
import com.document.render.office.fc.dom4j.ElementPath;
import com.document.render.office.fc.dom4j.io.SAXReader;
import com.document.render.office.fc.openxml4j.opc.PackagePart;
import com.document.render.office.fc.openxml4j.opc.PackageRelationship;
import com.document.render.office.fc.openxml4j.opc.PackageRelationshipCollection;
import com.document.render.office.fc.openxml4j.opc.PackageRelationshipTypes;
import com.document.render.office.fc.openxml4j.opc.ZipPackage;
import com.document.render.office.fc.ppt.reader.PictureReader;
import com.document.render.office.fc.xls.Reader.drawing.DrawingReader;
import com.document.render.office.fc.xls.Reader.table.TableReader;
import com.document.render.office.ss.model.CellRangeAddress;
import com.document.render.office.ss.model.baseModel.Cell;
import com.document.render.office.ss.model.baseModel.Row;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.model.sheetProperty.ColumnInfo;
import com.document.render.office.ss.model.sheetProperty.PaneInformation;
import com.document.render.office.ss.model.table.SSTable;
import com.document.render.office.ss.util.ReferenceUtil;
import com.document.render.office.system.AbortReaderError;
import com.document.render.office.system.IControl;
import com.document.render.office.system.IReader;
import com.document.render.office.system.StopReaderError;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



public class SheetReader {


    private static SheetReader reader = new SheetReader();
    private Sheet sheet;
    private IReader iReader;
    private int defaultRowHeight;
    private int defaultColWidth;

    private String key;
    private boolean searched;


    public static SheetReader instance() {
        return reader;
    }


    public void getSheet(IControl control, ZipPackage zipPackage, Sheet sheet, PackagePart sheetPart, IReader iReader) throws Exception {
        this.sheet = sheet;
        this.iReader = iReader;

        SAXReader saxreader = new SAXReader();
        Element root;
        try {
            XLSXSaxHandler xLSXSaxHandler = new XLSXSaxHandler();

            saxreader.addHandler("/worksheet/sheetFormatPr", xLSXSaxHandler);
            saxreader.addHandler("/worksheet/cols/col", xLSXSaxHandler);
            saxreader.addHandler("/worksheet/sheetData/row", xLSXSaxHandler);
            saxreader.addHandler("/worksheet/sheetData/row/c", xLSXSaxHandler);
            saxreader.addHandler("/worksheet/mergeCells/mergeCell", xLSXSaxHandler);
            InputStream in = sheetPart.getInputStream();
            Document poiXls = saxreader.read(in);
            in.close();

            root = poiXls.getRootElement();
        } finally {
            saxreader.resetHandlers();
        }



        Element ele = root.element("sheetViews").element("sheetView");
        if (ele.element("pane") != null) {
            PaneInformation paneInfo = new PaneInformation();

            Element pane = ele.element("pane");
            if (pane.attributeValue("xSplit") != null) {
                paneInfo.setVerticalSplitLeftColumn((short) Integer.parseInt(pane.attributeValue("xSplit")));
            }

            if (pane.attributeValue("ySplit") != null) {
                paneInfo.setHorizontalSplitTopRow((short) Integer.parseInt(pane.attributeValue("ySplit")));
            }

            sheet.setPaneInformation(paneInfo);
        }


        Map<String, String> hyperlinkTarget = getSheetHyperlinkByRelation(sheetPart);


        PackageRelationshipCollection tableRelCollection =
                sheetPart.getRelationshipsByType(PackageRelationshipTypes.TABLE_PART);
        if (tableRelCollection.size() > 0) {
            Iterator<PackageRelationship> iter = tableRelCollection.iterator();
            while (iter.hasNext()) {
                PackageRelationship tableRel = iter.next();
                TableReader.instance().read(control, zipPackage.getPart(tableRel.getTargetURI()), sheet);
            }
        }


        PackageRelationshipCollection drawingRelCollection =
                sheetPart.getRelationshipsByType(PackageRelationshipTypes.DRAWING_PART);

        if (drawingRelCollection.size() > 0) {
            PackagePart drawingPart = zipPackage.getPart(drawingRelCollection.getRelationship(0).getTargetURI());
            DrawingReader.instance().read(control, zipPackage, drawingPart, sheet);
        }

        DrawingReader.instance().processOLEPicture(control, zipPackage, sheetPart, sheet, root.element("oleObjects"));
        PictureReader.instance().dispose();


        getSheetHyperlink(sheet, hyperlinkTarget, root.element("hyperlinks"));


        checkTableCell(sheet);

        sheet.setState(Sheet.State_Accomplished);

        dispose();

        return;
    }


    private Map<String, String> getSheetHyperlinkByRelation(PackagePart sheetPart) throws Exception {

        PackageRelationshipCollection HyperlinkRelCollection =
                sheetPart.getRelationshipsByType(PackageRelationshipTypes.HYPERLINK_PART);

        Map<String, String> hyperlink =
                new HashMap<String, String>(HyperlinkRelCollection.size());
        for (PackageRelationship HyperlinkRel : HyperlinkRelCollection) {
            hyperlink.put(HyperlinkRel.getId(), HyperlinkRel.getTargetURI().toString());
        }

        return hyperlink;
    }


    private void getSheetHyperlink(Sheet sheet, Map<String, String> hyperlinkTarget, Element hyperlinksEle) {
        if (hyperlinksEle == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        Iterator<Element> iter = hyperlinksEle.elementIterator();
        Element hyperlinkEle;
        String id;
        String ref;
        Row row;
        Cell cell;
        Hyperlink hyperlink;
        while (iter.hasNext()) {
            hyperlinkEle = iter.next();

            id = hyperlinkEle.attributeValue("id");
            ref = hyperlinkEle.attributeValue("ref");

            row = sheet.getRow(ReferenceUtil.instance().getRowIndex(ref));
            if (row != null && (cell = row.getCell(ReferenceUtil.instance().getColumnIndex(ref))) != null) {

                hyperlink = new Hyperlink();
                String target = hyperlinkTarget.get(id);
                if (target == null) {
                    hyperlink.setLinkType(Hyperlink.LINK_DOCUMENT);
                    target = hyperlinkEle.attributeValue("location");
                } else {
                    if (target.contains("mailto")) {
                        hyperlink.setLinkType(Hyperlink.LINK_EMAIL);
                    } else if (target.contains("http")) {
                        hyperlink.setLinkType(Hyperlink.LINK_URL);
                    } else {
                        hyperlink.setLinkType(Hyperlink.LINK_FILE);
                    }
                }
                hyperlink.setAddress(target);

                cell.setHyperLink(hyperlink);
            }

        }
    }


    private void setColumnProperty(Element col) {
        int min = Integer.parseInt(col.attributeValue("min")) - 1;
        int max = Integer.parseInt(col.attributeValue("max")) - 1;
        int styleIndex = 0;
        double width;
        boolean hidden;


        if (col.attributeValue("width") != null) {
            width = Double.parseDouble(col.attributeValue("width")) * SSConstant.COLUMN_CHAR_WIDTH * MainConstant.POINT_TO_PIXEL;
        } else {
            width = 0;
        }


        if (col.attributeValue("hidden") != null) {
            hidden = (Integer.parseInt(col.attributeValue("hidden")) != 0);
        } else {
            hidden = false;
        }


        if (col.attributeValue("style") != null) {
            styleIndex = Integer.parseInt(col.attributeValue("style"));
        }

        sheet.addColumnInfo(new ColumnInfo(min, max, (int) width, styleIndex, hidden));
    }


    private void getSheetMergerdCells(Element mergedCell) {
        CellRangeAddress cellRange = getCellRangeAddress(mergedCell.attributeValue("ref"));
        if (cellRange.getLastRow() - cellRange.getFirstRow() == Workbook.MAXROW_07 - 1
                || cellRange.getLastColumn() - cellRange.getFirstColumn() == Workbook.MAXCOLUMN_07 - 1) {
            return;
        }
        int index = sheet.addMergeRange(cellRange) - 1;
        Row row;
        Cell cell;
        for (int i = cellRange.getFirstRow(); i <= cellRange.getLastRow(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                row = new Row(cellRange.getLastColumn() - cellRange.getFirstColumn());
                row.setSheet(sheet);
                row.setRowNumber(i);
                sheet.addRow(row);
            }
            for (int j = cellRange.getFirstColumn(); j <= cellRange.getLastColumn(); j++) {
                cell = row.getCell(j);
                if (cell == null) {
                    cell = new Cell(Cell.CELL_TYPE_BLANK);
                    cell.setRowNumber(i);
                    cell.setColNumber(j);
                    cell.setSheet(sheet);
                    cell.setCellStyle(row.getRowStyle());

                    row.addCell(cell);
                }
                cell.setRangeAddressIndex(index);
            }
        }
    }


    private CellRangeAddress getCellRangeAddress(String region) {
        String[] subRegion = region.split(":");
        return new CellRangeAddress(
                ReferenceUtil.instance().getRowIndex(subRegion[0]),
                ReferenceUtil.instance().getColumnIndex(subRegion[0]),
                ReferenceUtil.instance().getRowIndex(subRegion[1]),
                ReferenceUtil.instance().getColumnIndex(subRegion[1]));
    }

    private void checkTableCell(Sheet sheet) {
        SSTable[] tables = sheet.getTables();
        if (tables == null) {
            return;
        }

        Workbook book = sheet.getWorkbook();
        CellRangeAddress rangeAddr = null;
        for (SSTable item : tables) {
            rangeAddr = item.getTableReference();
            for (int i = rangeAddr.getFirstRow(); i <= rangeAddr.getLastRow(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    row = new Row(rangeAddr.getLastColumn() - rangeAddr.getFirstColumn() + 1);
                    row.setSheet(sheet);
                    row.setRowNumber(i);
                    row.setFirstCol(rangeAddr.getFirstColumn());
                    row.setLastCol(rangeAddr.getLastColumn());
                    row.setInitExpandedRangeAddress(true);

                    sheet.addRow(row);
                }
                for (int j = rangeAddr.getFirstColumn(); j <= rangeAddr.getLastColumn(); j++) {
                    Cell cell = row.getCell(j);
                    if (cell == null) {
                        cell = new Cell(Cell.CELL_TYPE_BLANK);
                        cell.setColNumber(j);
                        cell.setRowNumber(row.getRowNumber());
                        cell.setSheet(sheet);
                        cell.setCellStyle(row.getRowStyle());

                        row.addCell(cell);
                    }

                    cell.setTableInfo(item);
                }
            }
        }
    }


    public boolean searchContent(ZipPackage zipPackage, IReader iReader, PackagePart sheetPart, String key) throws Exception {
        this.key = key;
        searched = false;
        this.iReader = iReader;
        SAXReader saxreader = new SAXReader();

        try {
            saxreader.addHandler("/worksheet/sheetData/row/c", new XLSXSearchSaxHandler());
            InputStream in = sheetPart.getInputStream();
            saxreader.read(in);
            in.close();
        } catch (StopReaderError e) {
            return true;
        } finally {
            saxreader.resetHandlers();
        }
        return searched;

    }

    private boolean isValidateRow(Element rowElement) {
        if (rowElement.attributeValue("ht") != null) {
            return true;
        }

        if (rowElement.attributeValue("s") != null) {
            int style = Integer.parseInt(rowElement.attributeValue("s"));
            if (Workbook.isValidateStyle(sheet.getWorkbook().getCellStyle(style))) {
                return true;
            }
        }

        return false;
    }


    private Row createRow(Element rowElement, int defaultRowHeight) {
        if (!isValidateRow(rowElement)) {
            return null;
        }

        int rowIndex = Integer.parseInt(rowElement.attributeValue("r")) - 1;

        String spans = rowElement.attributeValue("spans");

        float height = defaultRowHeight;
        boolean hidden = false;
        int style = 0;

        if (rowElement.attributeValue("ht") != null) {
            height = (Float.parseFloat(rowElement.attributeValue("ht")) * MainConstant.POINT_TO_PIXEL);
        }

        if (rowElement.attributeValue("hidden") != null) {
            hidden = (Integer.parseInt(rowElement.attributeValue("hidden")) != 0);
        }

        if (rowElement.attributeValue("s") != null) {
            style = Integer.parseInt(rowElement.attributeValue("s"));
        }

        Row row = new Row(getEndBySpans(spans));

        row.setRowNumber(rowIndex);
        row.setRowPixelHeight(height);
        row.setZeroHeight(hidden);
        row.setSheet(sheet);
        row.setRowStyle(style);
        row.completed();
        return row;
    }

    private void modifyRow(Row row, Element rowElement, int defaultRowHeight) {
        int height = defaultRowHeight;
        boolean hidden = false;
        int style = 0;

        if (rowElement.attributeValue("ht") != null) {
            height = (int) (Double.parseDouble(rowElement.attributeValue("ht")) * MainConstant.POINT_TO_PIXEL);
        }

        if (rowElement.attributeValue("hidden") != null) {
            hidden = (Integer.parseInt(rowElement.attributeValue("hidden")) != 0);
        }

        if (rowElement.attributeValue("s") != null) {
            style = Integer.parseInt(rowElement.attributeValue("s"));
        }

        row.setRowPixelHeight(height);
        row.setZeroHeight(hidden);
        row.setRowStyle(style);

        row.completed();
    }


    private int getEndBySpans(String spans) {
        if (spans != null) {

            String[] subSpans = spans.split(" ");
            subSpans = subSpans[subSpans.length - 1].split(":");
            return Integer.parseInt(subSpans[1], 16) - 1;
        }
        return 0;
    }

    private void dispose() {
        sheet = null;
        iReader = null;
        key = null;
    }


    class XLSXSaxHandler implements ElementHandler {


        public void onStart(ElementPath elementPath) {

        }


        public void onEnd(ElementPath elementPath) {
            if (iReader.isAborted()) {
                throw new AbortReaderError("abort Reader");
            }

            Element elem = elementPath.getCurrent();
            String name = elem.getName();
            if (name.equals("sheetFormatPr")) {
                if (elem.attributeValue("defaultRowHeight") != null) {
                    defaultRowHeight = (int) (Double.parseDouble(elem.attributeValue("defaultRowHeight")) * MainConstant.POINT_TO_PIXEL);

                    sheet.setDefaultRowHeight(defaultRowHeight);
                }

                if (elem.attributeValue("defaultColWidth") != null) {
                    defaultColWidth = (int) (Double.parseDouble(elem.attributeValue("defaultColWidth")) * SSConstant.COLUMN_CHAR_WIDTH * MainConstant.POINT_TO_PIXEL);

                    sheet.setDefaultColWidth(defaultColWidth);
                }

            } else if (name.equals("col")) {
                setColumnProperty(elem);

            } else if (name.equals("row")) {
                int rowIndex = Integer.parseInt(elem.attributeValue("r")) - 1;
                if (sheet.getRow(rowIndex) == null) {

                    sheet.addRow(createRow(elem, defaultRowHeight));
                } else {
                    modifyRow(sheet.getRow(rowIndex), elem, defaultRowHeight);
                }
            } else if (name.equals("c")) {

                String ref = elem.attributeValue("r");
                int rowIndex = ReferenceUtil.instance().getRowIndex(ref);
                int colIndex = ReferenceUtil.instance().getColumnIndex(ref);

                Row row = sheet.getRow(rowIndex);
                Cell cell = null;
                if (row != null) {

                    cell = row.getCell(colIndex, false);
                } else {

                    row = new Row(colIndex);
                    row.setRowNumber(rowIndex);
                    row.setSheet(sheet);
                    sheet.addRow(row);
                }

                if (cell == null) {
                    cell = CellReader.instance().getCell(sheet, elem);
                }

                if (cell != null) {
                    cell.setSheet(sheet);
                    row.addCell(cell);
                }

            } else if (name.equals("mergeCell")) {
                getSheetMergerdCells(elem);
            }


            elem.detach();
        }

    }

    class XLSXSearchSaxHandler implements ElementHandler {


        public void onStart(ElementPath elementPath) {

        }


        public void onEnd(ElementPath elementPath) {
            if (iReader.isAborted()) {
                throw new AbortReaderError("abort Reader");
            }

            Element elem = elementPath.getCurrent();
            String name = elem.getName();
            if (name.equals("c") && CellReader.instance().searchContent(elem, key)) {
                searched = true;
            }
            elem.detach();

            if (searched) {
                throw new StopReaderError("stop");
            }
        }

    }
}

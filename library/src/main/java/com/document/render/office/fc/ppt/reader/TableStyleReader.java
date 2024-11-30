
package com.document.render.office.fc.ppt.reader;

import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.ElementHandler;
import com.document.render.office.fc.dom4j.ElementPath;
import com.document.render.office.fc.dom4j.io.SAXReader;
import com.document.render.office.fc.openxml4j.opc.PackagePart;
import com.document.render.office.pg.model.PGModel;
import com.document.render.office.pg.model.tableStyle.TableCellBorders;
import com.document.render.office.pg.model.tableStyle.TableCellStyle;
import com.document.render.office.pg.model.tableStyle.TableStyle;
import com.document.render.office.simpletext.model.AttrManage;
import com.document.render.office.simpletext.model.AttributeSetImpl;
import com.document.render.office.simpletext.model.IAttributeSet;

import java.io.InputStream;


public class TableStyleReader {
    private static TableStyleReader tableStyleReader = new TableStyleReader();
    private PGModel pgModel = null;
    private int defaultFontSize = 12;


    public static TableStyleReader instance() {
        return tableStyleReader;
    }

    public void read(PGModel pgModel, PackagePart tableStyle, int defaultFontSize) throws Exception {
        this.pgModel = pgModel;
        this.defaultFontSize = defaultFontSize;


        SAXReader saxreader = new SAXReader();
        try {
            InputStream in = tableStyle.getInputStream();

            TableStyleSaxHandler preSaxHandler = new TableStyleSaxHandler();

            saxreader.addHandler("/tblStyleLst/tblStyle", preSaxHandler);

            saxreader.read(in);
            in.close();
            pgModel = null;
        } catch (Exception e) {
            throw e;
        } finally {
            saxreader.resetHandlers();
        }
    }

    private void processTableStyle(Element tablestyleElement) {
        TableStyle tableStyle = new TableStyle();

        String styleId = tablestyleElement.attributeValue("styleId");

        Element element = tablestyleElement.element("wholeTbl");
        if (element != null) {
            tableStyle.setWholeTable(processTableCellStyle(element));
        }


        element = tablestyleElement.element("band1H");
        if (element != null) {
            tableStyle.setBand1H(processTableCellStyle(element));
        }


        element = tablestyleElement.element("band2H");
        if (element != null) {
            tableStyle.setBand2H(processTableCellStyle(element));
        }


        element = tablestyleElement.element("band1V");
        if (element != null) {
            tableStyle.setBand1V(processTableCellStyle(element));
        }


        element = tablestyleElement.element("band2V");
        if (element != null) {
            tableStyle.setBand2V(processTableCellStyle(element));
        }


        element = tablestyleElement.element("lastCol");
        if (element != null) {
            tableStyle.setLastCol(processTableCellStyle(element));
        }


        element = tablestyleElement.element("firstCol");
        if (element != null) {
            tableStyle.setFirstCol(processTableCellStyle(element));
        }


        element = tablestyleElement.element("lastRow");
        if (element != null) {
            tableStyle.setLastRow(processTableCellStyle(element));
        }


        element = tablestyleElement.element("firstRow");
        if (element != null) {
            tableStyle.setFirstRow(processTableCellStyle(element));
        }

        pgModel.putTableStyle(styleId, tableStyle);
    }

    private TableCellStyle processTableCellStyle(Element tableStyleElement) {
        TableCellStyle tableCellStyle = new TableCellStyle();

        Element cellTextStyleElement = tableStyleElement.element("tcTxStyle");
        if (cellTextStyleElement != null) {
            IAttributeSet attr = new AttributeSetImpl();

            String str = cellTextStyleElement.attributeValue("b");
            if ("on".equals(str)) {
                AttrManage.instance().setFontBold(attr, true);
            }


            str = cellTextStyleElement.attributeValue("i");
            if ("on".equals(str)) {
                AttrManage.instance().setFontItalic(attr, true);
            }




            AttrManage.instance().setFontSize(attr, defaultFontSize);

            tableCellStyle.setFontAttributeSet(attr);
        }

        Element cellStyleElement = tableStyleElement.element("tcStyle");

        Element ele = cellStyleElement.element("tcBdr");
        if (ele != null) {
            tableCellStyle.setTableCellBorders(getTableCellBorders(ele));
        }


        tableCellStyle.setTableCellBgFill(cellStyleElement.element("fill"));


        return tableCellStyle;
    }

    private TableCellBorders getTableCellBorders(Element tcBrdElement) {
        TableCellBorders tableCellBorders = new TableCellBorders();

        Element ele = tcBrdElement.element("left");
        if (ele != null) {
            tableCellBorders.setLeftBorder(ele.element("ln"));
        }


        ele = tcBrdElement.element("right");
        if (ele != null) {
            tableCellBorders.setRightBorder(ele.element("ln"));
        }


        ele = tcBrdElement.element("top");
        if (ele != null) {
            tableCellBorders.setTopBorder(ele.element("ln"));
        }


        ele = tcBrdElement.element("bottom");
        if (ele != null) {
            tableCellBorders.setBottomBorder(ele.element("ln"));
        }

        return tableCellBorders;
    }


    class TableStyleSaxHandler implements ElementHandler {

        public void onStart(ElementPath elementPath) {

        }


        public void onEnd(ElementPath elementPath) {

            Element elem = elementPath.getCurrent();
            String name = elem.getName();
            try {
                if (name.equals("tblStyle")) {
                    processTableStyle(elem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            elem.detach();
        }
    }
}

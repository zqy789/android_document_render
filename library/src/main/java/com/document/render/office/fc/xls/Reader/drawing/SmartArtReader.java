
package com.document.render.office.fc.xls.Reader.drawing;

import com.document.render.office.common.autoshape.AutoShapeDataKit;
import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.common.borders.Line;
import com.document.render.office.common.shape.AbstractShape;
import com.document.render.office.common.shape.SmartArt;
import com.document.render.office.common.shape.TextBox;
import com.document.render.office.constant.MainConstant;
import com.document.render.office.constant.SSConstant;
import com.document.render.office.constant.wp.WPModelConstant;
import com.document.render.office.fc.LineKit;
import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.io.SAXReader;
import com.document.render.office.fc.openxml4j.opc.PackagePart;
import com.document.render.office.fc.openxml4j.opc.PackageRelationship;
import com.document.render.office.fc.openxml4j.opc.ZipPackage;
import com.document.render.office.fc.ppt.attribute.ParaAttr;
import com.document.render.office.fc.ppt.attribute.RunAttr;
import com.document.render.office.fc.ppt.attribute.SectionAttr;
import com.document.render.office.fc.ppt.reader.ReaderKit;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.simpletext.model.AttrManage;
import com.document.render.office.simpletext.model.IAttributeSet;
import com.document.render.office.simpletext.model.LeafElement;
import com.document.render.office.simpletext.model.ParagraphElement;
import com.document.render.office.simpletext.model.SectionElement;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.system.IControl;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class SmartArtReader {
    private static SmartArtReader reader = new SmartArtReader();
    private Sheet sheet;
    private int offset;


    public static SmartArtReader instance() {
        return reader;
    }


    public SmartArt read(IControl control, ZipPackage zipPackage, PackagePart slidePart, PackagePart dataPart,
                         Map<String, Integer> schemeColor, Sheet sheet) throws Exception {
        this.sheet = sheet;

        SAXReader saxreader = new SAXReader();
        InputStream in = dataPart.getInputStream();
        Document dataDoc = saxreader.read(in);
        in.close();
        Element root = dataDoc.getRootElement();

        BackgroundAndFill fill = AutoShapeDataKit.processBackground(control, zipPackage, dataPart, root.element("bg"), schemeColor);
        ;
        Line line = LineKit.createLine(control, zipPackage, dataPart, root.element("whole").element("ln"), schemeColor);
        PackagePart drawingPart = null;
        Element e = null;
        if ((e = root.element("extLst")) != null && (e = e.element("ext")) != null && (e = e.element("dataModelExt")) != null) {
            String relId = e.attributeValue("relId");
            if (relId != null) {
                PackageRelationship smartArDrawingRel = slidePart.getRelationship(relId);
                drawingPart = zipPackage.getPart(smartArDrawingRel.getTargetURI());
            }

        }
        if (drawingPart == null) {
            return null;
        }

        in = drawingPart.getInputStream();
        Document smartArtDoc = saxreader.read(in);
        in.close();

        SmartArt smartArt = new SmartArt();
        smartArt.setBackgroundAndFill(fill);
        smartArt.setLine(line);

        root = smartArtDoc.getRootElement();
        Element spTree = root.element("spTree");

        for (Iterator<?> it = spTree.elementIterator("sp"); it.hasNext(); ) {
            e = (Element) it.next();
            AbstractShape shape = null;
            Element temp = e.element("spPr");
            Rectangle rect = null;
            if (temp != null) {
                rect = ReaderKit.instance().getShapeAnchor(temp.element("xfrm"), 1.f, 1.f);
            }

            shape = AutoShapeDataKit.getAutoShape(control, zipPackage, dataPart, e,
                    rect, schemeColor, MainConstant.APPLICATION_TYPE_SS);
            if (shape != null) {
                smartArt.appendShapes(shape);
            }

            shape = getTextBoxData(control, e);
            if (shape != null) {
                smartArt.appendShapes(shape);
            }
        }

        sheet = null;

        return smartArt;
    }


    private TextBox getTextBoxData(IControl control, Element sp) {
        Element temp = sp.element("txXfrm");
        Rectangle rect = null;
        if (temp != null) {
            rect = ReaderKit.instance().getShapeAnchor(temp, 1.f, 1.f);
        }

        Element txBody = sp.element("txBody");
        if (txBody != null) {
            TextBox tb = new TextBox();




            SectionElement secElem = new SectionElement();

            secElem.setStartOffset(0);
            tb.setElement(secElem);

            IAttributeSet attr = secElem.getAttribute();

            AttrManage.instance().setPageWidth(attr, Math.round(rect.width * MainConstant.PIXEL_TO_TWIPS));

            AttrManage.instance().setPageHeight(attr, Math.round(rect.height * MainConstant.PIXEL_TO_TWIPS));


            AttrManage.instance().setPageMarginLeft(attr, Math.round(SSConstant.SHEET_SPACETOBORDER * MainConstant.PIXEL_TO_TWIPS));

            AttrManage.instance().setPageMarginRight(attr, Math.round(SSConstant.SHEET_SPACETOBORDER * MainConstant.PIXEL_TO_TWIPS));

            AttrManage.instance().setPageMarginTop(attr, 0);

            AttrManage.instance().setPageMarginBottom(attr, 0);

            Element bodyPr = temp.element("bodyPr");
            SectionAttr.instance().setSectionAttribute(bodyPr, attr, null, null, false);
            if (bodyPr != null) {

                String value = bodyPr.attributeValue("wrap");
                tb.setWrapLine(value == null || "square".equalsIgnoreCase(value));
            }
            int offset = processParagraph(control, secElem, txBody);
            secElem.setEndOffset(offset);

            tb.setBounds(rect);

            if (tb.getElement() != null && tb.getElement().getText(null) != null
                    && tb.getElement().getText(null).length() > 0
                    && !"\n".equals(tb.getElement().getText(null))) {
                ReaderKit.instance().processRotation(tb, sp.element("txXfrm"));
            }
            return tb;
        }

        return null;
    }

    private int processParagraph(IControl control, SectionElement secElem, Element txBody) {
        offset = 0;
        List<Element> ps = txBody.elements("p");
        for (Element p : ps) {
            Element pPr = p.element("pPr");

            ParagraphElement paraElem = new ParagraphElement();
            paraElem.setStartOffset(offset);
            IAttributeSet attrLayout = null;

            ParaAttr.instance().setParaAttribute(control, pPr, paraElem.getAttribute(), attrLayout, -1, -1, 0, false, false);

            paraElem = processRun(control, secElem, paraElem, p, attrLayout);

            paraElem.setEndOffset(offset);
            secElem.appendParagraph(paraElem, WPModelConstant.MAIN);
        }
        return offset;
    }

    private ParagraphElement processRun(IControl control, SectionElement secElem, ParagraphElement paraElem, Element p,
                                        IAttributeSet attrLayout) {

        List<Element> rs = p.elements("r");
        LeafElement leaf = null;

        if (rs.size() == 0) {
            leaf = new LeafElement("\n");
            Element ele = p.element("pPr");
            if (ele != null) {
                ele = ele.element("rPr");
                if (ele != null) {

                    RunAttr.instance().setRunAttribute(sheet, ele, leaf.getAttribute(), attrLayout);
                }
            }
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
                    if (len >= 0) {
                        leaf = new LeafElement(text);

                        RunAttr.instance().setRunAttribute(sheet, r.element("rPr"), leaf.getAttribute(), attrLayout);

                        leaf.setStartOffset(offset);
                        offset += len;

                        leaf.setEndOffset(offset);
                        paraElem.appendLeaf(leaf);
                    }
                }
            } else if (r.getName().equalsIgnoreCase("br")) {

                if (leaf != null) {
                    leaf.setText(leaf.getText(null) + "\n");
                    offset++;
                }
                paraElem.setEndOffset(offset);
                secElem.appendParagraph(paraElem, WPModelConstant.MAIN);

                paraElem = new ParagraphElement();
                paraElem.setStartOffset(offset);
                attrLayout = null;
                Element pPr = p.element("pPr");
                ParaAttr.instance().setParaAttribute(control, pPr, paraElem.getAttribute(), attrLayout, -1, -1, 0, false, false);
            }

        }
        if (leaf != null) {
            leaf.setText(leaf.getText(null) + "\n");
            offset++;
        }
        return paraElem;
    }
}

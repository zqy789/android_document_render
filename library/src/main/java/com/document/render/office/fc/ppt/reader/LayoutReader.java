
package com.document.render.office.fc.ppt.reader;

import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.io.SAXReader;
import com.document.render.office.fc.openxml4j.opc.PackagePart;
import com.document.render.office.fc.openxml4j.opc.ZipPackage;
import com.document.render.office.fc.ppt.ShapeManage;
import com.document.render.office.pg.model.PGLayout;
import com.document.render.office.pg.model.PGMaster;
import com.document.render.office.pg.model.PGModel;
import com.document.render.office.pg.model.PGPlaceholderUtil;
import com.document.render.office.pg.model.PGSlide;
import com.document.render.office.pg.model.PGStyle;
import com.document.render.office.system.IControl;

import java.io.InputStream;
import java.util.Iterator;


public class LayoutReader {
    private static LayoutReader layoutReader = new LayoutReader();

    private int style = 1001;


    public static LayoutReader instance() {
        return layoutReader;
    }


    public PGLayout getLayouts(IControl control, ZipPackage zipPackage, PackagePart layoutPart, PGModel pgModel,
                               PGMaster pgMaster, PGStyle defaultStyle) throws Exception {

        SAXReader saxreader = new SAXReader();
        InputStream in = layoutPart.getInputStream();
        Document poiLayout = saxreader.read(in);
        Element layout = poiLayout.getRootElement();
        PGLayout pgLayout = null;
        if (layout != null) {
            pgLayout = new PGLayout();
            if (layout.attribute("showMasterSp") != null) {
                String val = layout.attributeValue("showMasterSp");
                if (val != null && val.length() > 0 && Integer.valueOf(val) == 0) {
                    pgLayout.setAddShapes(false);
                }
            }
            Element cSld = layout.element("cSld");
            if (cSld != null) {
                Element spTree = cSld.element("spTree");
                if (spTree != null) {

                    processBackgroundAndFill(control, zipPackage, layoutPart, pgMaster, pgLayout, cSld);

                    processTextStyle(control, layoutPart, pgMaster, pgLayout, spTree);


                    PGSlide pgSlide = new PGSlide();
                    pgSlide.setSlideType(PGSlide.Slide_Layout);
                    for (Iterator<?> it = spTree.elementIterator(); it.hasNext(); ) {
                        ShapeManage.instance().processShape(control, zipPackage, layoutPart, null,
                                pgMaster, pgLayout, defaultStyle, pgSlide, PGSlide.Slide_Layout, (Element) it.next(), null, 1.0f, 1.0f);
                    }
                    if (pgSlide.getShapeCount() > 0) {
                        pgLayout.setSlideMasterIndex(pgModel.appendSlideMaster(pgSlide));
                    }
                }
            }
        }
        in.close();
        return pgLayout;
    }


    private void processTextStyle(IControl control, PackagePart layoutPart, PGMaster pgMaster, PGLayout pgLayout, Element spTree) {
        for (Iterator<?> it = spTree.elementIterator(); it.hasNext(); ) {
            Element sp = (Element) it.next();
            String type = ReaderKit.instance().getPlaceholderType(sp);
            int idx = ReaderKit.instance().getPlaceholderIdx(sp);
            Element txBody = sp.element("txBody");
            if (txBody != null) {
                Element lstStyle = txBody.element("lstStyle");
                StyleReader.instance().setStyleIndex(style);
                if (!PGPlaceholderUtil.instance().isBody(type)) {
                    pgLayout.setStyleByType(type, StyleReader.instance().getStyles(control, pgMaster, sp, lstStyle));
                } else if (idx > 0) {
                    pgLayout.setStyleByIdx(idx, StyleReader.instance().getStyles(control, pgMaster, sp, lstStyle));
                }

                style = StyleReader.instance().getStyleIndex();
            }
        }
    }


    private void processBackgroundAndFill(IControl control, ZipPackage zipPackage, PackagePart layoutPart,
                                          PGMaster pgMaster, PGLayout pgLayout, Element cSld) throws Exception {
        Element bg = cSld.element("bg");
        if (bg != null) {
            pgLayout.setBackgroundAndFill(BackgroundReader.instance().getBackground(control,
                    zipPackage, layoutPart, pgMaster, bg));
        }
    }


    public void dispose() {
        style = 1001;
    }
}

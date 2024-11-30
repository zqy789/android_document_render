
package com.document.render.office.fc.ppt.reader;

import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.io.SAXReader;
import com.document.render.office.fc.openxml4j.opc.PackagePart;
import com.document.render.office.fc.openxml4j.opc.PackageRelationship;
import com.document.render.office.fc.openxml4j.opc.PackageRelationshipTypes;
import com.document.render.office.fc.openxml4j.opc.ZipPackage;
import com.document.render.office.fc.ppt.ShapeManage;
import com.document.render.office.pg.model.PGMaster;
import com.document.render.office.pg.model.PGModel;
import com.document.render.office.pg.model.PGPlaceholderUtil;
import com.document.render.office.pg.model.PGSlide;
import com.document.render.office.system.IControl;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;


public class MasterReader {
    private static MasterReader masterReader = new MasterReader();

    private int styleIndex = 10;


    public static MasterReader instance() {
        return masterReader;
    }


    public PGMaster getMasterData(IControl control, ZipPackage zipPackage, PackagePart masterPart,
                                  PGModel pgModel) throws Exception {
        SAXReader saxreader = new SAXReader();
        InputStream in = masterPart.getInputStream();
        Document poiMaster = saxreader.read(in);
        Element master = poiMaster.getRootElement();
        PGMaster pgMaster = null;
        if (master != null) {
            pgMaster = new PGMaster();

            processClrMap(pgMaster, zipPackage, masterPart, master);

            processStyle(control, pgMaster, master);

            Element cSld = master.element("cSld");
            if (cSld != null) {

                processBackgroundAndFill(control, pgMaster, zipPackage, masterPart, cSld);

                Element spTree = cSld.element("spTree");
                if (spTree != null) {
                    processTextStyle(control, pgMaster, spTree);


                    PGSlide pgSlide = new PGSlide();
                    pgSlide.setSlideType(PGSlide.Slide_Master);
                    for (Iterator<?> it = spTree.elementIterator(); it.hasNext(); ) {
                        ShapeManage.instance().processShape(control, zipPackage, masterPart, null,
                                pgMaster, null, null, pgSlide, PGSlide.Slide_Master, (Element) it.next(), null, 1.0f, 1.0f);
                    }
                    if (pgSlide.getShapeCount() > 0) {
                        pgMaster.setSlideMasterIndex(pgModel.appendSlideMaster(pgSlide));
                    }
                }
            }
        }
        in.close();
        return pgMaster;
    }


    private void processClrMap(PGMaster pgMaster, ZipPackage zipPackage, PackagePart masterPart,
                               Element master) throws Exception {

        PackageRelationship themeShip = masterPart.getRelationshipsByType(
                PackageRelationshipTypes.THEME_PART).getRelationship(0);
        if (themeShip != null) {
            PackagePart themePart = zipPackage.getPart(themeShip.getTargetURI());
            if (themePart != null) {
                Map<String, Integer> themeColor = ThemeReader.instance().getThemeColorMap(themePart);

                Element clrMap = master.element("clrMap");
                if (clrMap != null) {
                    for (int i = 0; i < clrMap.attributeCount(); i++) {
                        String name = clrMap.attribute(i).getName();
                        String value = clrMap.attributeValue(name);
                        if (!name.equals(value)) {
                            pgMaster.addColor(value, themeColor.get(value));
                        }
                        pgMaster.addColor(name, themeColor.get(value));
                    }
                }
            }
        }
    }


    private void processBackgroundAndFill(IControl control, PGMaster pgMaster, ZipPackage zipPackage,
                                          PackagePart masterPart, Element cSld) throws Exception {
        Element bg = cSld.element("bg");
        if (bg != null) {
            pgMaster.setBackgroundAndFill(BackgroundReader.instance().getBackground(control,
                    zipPackage, masterPart, pgMaster, bg));
        }
    }


    private void processTextStyle(IControl control, PGMaster pgMaster, Element spTree) throws Exception {
        for (Iterator<?> it = spTree.elementIterator(); it.hasNext(); ) {
            Element sp = (Element) it.next();
            String type = ReaderKit.instance().getPlaceholderType(sp);
            type = PGPlaceholderUtil.instance().checkTypeName(type);
            int idx = ReaderKit.instance().getPlaceholderIdx(sp);
            Element txBody = sp.element("txBody");
            if (txBody != null) {
                Element lstStyle = txBody.element("lstStyle");
                StyleReader.instance().setStyleIndex(styleIndex);
                if (!PGPlaceholderUtil.instance().isBody(type)) {
                    pgMaster.addStyleByType(type, StyleReader.instance().getStyles(control, pgMaster, sp, lstStyle));
                } else if (idx > 0) {
                    pgMaster.addStyleByIdx(idx, StyleReader.instance().getStyles(control, pgMaster, sp, lstStyle));
                }

                styleIndex = StyleReader.instance().getStyleIndex();
            }
        }
    }


    private void processStyle(IControl control, PGMaster pgMaster, Element master) {
        Element txStyles = master.element("txStyles");
        if (txStyles != null) {
            StyleReader.instance().setStyleIndex(styleIndex);

            Element style = txStyles.element("titleStyle");
            pgMaster.setTitleStyle(StyleReader.instance().getStyles(control, pgMaster, null, style));

            style = txStyles.element("bodyStyle");
            pgMaster.setBodyStyle(StyleReader.instance().getStyles(control, pgMaster, null, style));

            style = txStyles.element("otherStyle");
            pgMaster.setDefaultStyle(StyleReader.instance().getStyles(control, pgMaster, null, style));

            styleIndex = StyleReader.instance().getStyleIndex();
        }
    }


    public void dispose() {
        styleIndex = 10;
    }
}

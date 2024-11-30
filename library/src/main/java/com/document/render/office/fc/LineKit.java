package com.document.render.office.fc;

import com.document.render.office.common.autoshape.AutoShapeDataKit;
import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.common.borders.Line;
import com.document.render.office.constant.MainConstant;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.openxml4j.opc.PackagePart;
import com.document.render.office.fc.openxml4j.opc.ZipPackage;
import com.document.render.office.fc.ppt.reader.BackgroundReader;
import com.document.render.office.fc.ppt.reader.ReaderKit;
import com.document.render.office.pg.model.PGMaster;
import com.document.render.office.system.IControl;

import java.util.Map;

public class LineKit {

    public static Line createLine(IControl control, ZipPackage zipPackage, PackagePart packagePart, PGMaster pgMaster, Element ln) throws Exception {
        int lineWidth = 1;
        boolean dash = false;
        BackgroundAndFill lineFill = null;
        if (ln != null) {

            if (ln.element("noFill") == null) {

                if (ln.attributeValue("w") != null) {
                    lineWidth = Math.round(Integer.parseInt(ln.attributeValue("w")) * MainConstant.PIXEL_DPI / MainConstant.EMU_PER_INCH);
                }

                Element prstDash = ln.element("prstDash");
                if (prstDash != null && !"solid".equalsIgnoreCase(prstDash.attributeValue("val"))) {
                    dash = true;
                }


                lineFill = BackgroundReader.instance().processBackground(control, zipPackage, packagePart, pgMaster, ln);
                if (lineFill != null) {
                    Line line = new Line();
                    line.setBackgroundAndFill(lineFill);
                    line.setLineWidth(lineWidth);
                    line.setDash(dash);
                    return line;
                }
            }
        }

        return null;
    }


    public static Line createShapeLine(IControl control, ZipPackage zipPackage, PackagePart packagePart, PGMaster pgMaster, Element sp) throws Exception {
        int lineWidth = 1;
        boolean dash = false;
        BackgroundAndFill lineFill = null;

        Element ln = sp.element("spPr").element("ln");
        Element style = sp.element("style");
        if (ln != null) {

            if (ln.element("noFill") == null) {

                if (ln.attributeValue("w") != null) {
                    lineWidth = Math.round(Integer.parseInt(ln.attributeValue("w")) * MainConstant.PIXEL_DPI / MainConstant.EMU_PER_INCH);
                }

                Element prstDash = ln.element("prstDash");
                if (prstDash != null && !"solid".equalsIgnoreCase(prstDash.attributeValue("val"))) {
                    dash = true;
                }


                lineFill = BackgroundReader.instance().processBackground(control, zipPackage, packagePart, pgMaster, ln);
                if (lineFill == null && style != null && style.element("lnRef") != null) {
                    lineFill = new BackgroundAndFill();
                    lineFill.setFillType(BackgroundAndFill.FILL_SOLID);
                    lineFill.setForegroundColor(ReaderKit.instance().getColor(pgMaster, style.element("lnRef")));
                }
            }
        } else {
            if (style != null && style.element("lnRef") != null) {
                int color = ReaderKit.instance().getColor(pgMaster, style.element("lnRef"));
                if ((color & 0xFFFFFF) != 0) {
                    lineFill = new BackgroundAndFill();
                    lineFill.setFillType(BackgroundAndFill.FILL_SOLID);
                    lineFill.setForegroundColor(color);
                }
            }
        }

        if (lineFill != null) {
            Line line = new Line();
            line.setBackgroundAndFill(lineFill);
            line.setLineWidth(lineWidth);
            line.setDash(dash);
            return line;
        }

        return null;
    }

    public static Line createLine(IControl control, ZipPackage zipPackage, PackagePart drawingPart,
                                  Element ln, Map<String, Integer> schemeColor) {
        int lineWidth = 1;
        boolean dash = false;
        BackgroundAndFill lineFill = null;

        if (ln != null) {

            if (ln.attributeValue("w") != null) {
                lineWidth = Math.round(Integer.parseInt(ln.attributeValue("w")) * MainConstant.PIXEL_DPI / MainConstant.EMU_PER_INCH);
            }

            Element prstDash = ln.element("prstDash");
            if (prstDash != null && !"solid".equalsIgnoreCase(prstDash.attributeValue("val"))) {
                dash = true;
            }

            if (ln.element("noFill") == null) {
                lineFill = AutoShapeDataKit.processBackground(control, zipPackage, drawingPart, ln, schemeColor);
            }
        }

        if (lineFill != null) {
            Line line = new Line();
            line.setBackgroundAndFill(lineFill);
            line.setLineWidth(lineWidth);
            line.setDash(dash);
            return line;
        }

        return null;
    }

    public static Line createShapeLine(IControl control, ZipPackage zipPackage, PackagePart drawingPart,
                                       Element sp, Map<String, Integer> schemeColor) {
        int lineWidth = 1;
        boolean dash = false;
        BackgroundAndFill lineFill = null;

        Element ln = sp.element("spPr").element("ln");
        Element style = sp.element("style");
        if (ln != null) {

            if (ln.attributeValue("w") != null) {
                lineWidth = Math.round(Integer.parseInt(ln.attributeValue("w")) * MainConstant.PIXEL_DPI / MainConstant.EMU_PER_INCH);
            }

            Element prstDash = ln.element("prstDash");
            if (prstDash != null && !"solid".equalsIgnoreCase(prstDash.attributeValue("val"))) {
                dash = true;
            }

            if (ln.element("noFill") == null) {
                lineFill = AutoShapeDataKit.processBackground(control, zipPackage, drawingPart, ln, schemeColor);
                if (lineFill == null && style != null && style.element("lnRef") != null) {
                    lineFill = new BackgroundAndFill();
                    lineFill.setFillType(BackgroundAndFill.FILL_SOLID);
                    lineFill.setForegroundColor(AutoShapeDataKit.getColor(schemeColor, style.element("lnRef")));
                }
            }
        } else {
            if (style != null && style.element("lnRef") != null) {

                lineFill = new BackgroundAndFill();
                lineFill.setFillType(BackgroundAndFill.FILL_SOLID);
                lineFill.setForegroundColor(AutoShapeDataKit.getColor(schemeColor, style.element("lnRef")));
            }
        }

        if (lineFill != null) {
            Line line = new Line();
            line.setBackgroundAndFill(lineFill);
            line.setLineWidth(lineWidth);
            line.setDash(dash);
            return line;
        }

        return null;
    }

    public static Line createChartLine(IControl control, ZipPackage zipPackage, PackagePart drawingPart,
                                       Element ln, Map<String, Integer> schemeColor) {

        if (ln != null) {
            int lineWidth = 1;
            boolean dash = false;
            BackgroundAndFill lineFill = null;

            if (ln.attributeValue("w") != null) {
                lineWidth = Math.round(Integer.parseInt(ln.attributeValue("w")) * MainConstant.PIXEL_DPI / MainConstant.EMU_PER_INCH);
            }

            Element prstDash = ln.element("prstDash");
            if (prstDash != null && !"solid".equalsIgnoreCase(prstDash.attributeValue("val"))) {
                dash = true;
            }

            if (ln.element("noFill") == null) {
                lineFill = AutoShapeDataKit.processBackground(control, zipPackage, drawingPart, ln, schemeColor);
                if (lineFill != null) {
                    Line line = new Line();
                    line.setBackgroundAndFill(lineFill);
                    line.setLineWidth(lineWidth);
                    line.setDash(dash);
                    return line;
                }
            }
        } else {

            Line line = new Line();
            BackgroundAndFill lineFill = new BackgroundAndFill();
            lineFill.setFillType(BackgroundAndFill.FILL_SOLID);
            lineFill.setForegroundColor(0xFF747474);
            line.setBackgroundAndFill(lineFill);
            line.setLineWidth(1);

            return line;
        }
        return null;
    }
}

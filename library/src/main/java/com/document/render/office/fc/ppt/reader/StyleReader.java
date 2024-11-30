
package com.document.render.office.fc.ppt.reader;

import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.ppt.attribute.ParaAttr;
import com.document.render.office.fc.ppt.attribute.RunAttr;
import com.document.render.office.fc.ppt.attribute.SectionAttr;
import com.document.render.office.fc.ppt.bulletnumber.BulletNumberManage;
import com.document.render.office.pg.model.PGMaster;
import com.document.render.office.pg.model.PGStyle;
import com.document.render.office.simpletext.model.AttrManage;
import com.document.render.office.simpletext.model.AttributeSetImpl;
import com.document.render.office.simpletext.model.IAttributeSet;
import com.document.render.office.simpletext.model.Style;
import com.document.render.office.simpletext.model.StyleManage;
import com.document.render.office.system.IControl;


public class StyleReader {
    private static StyleReader style = new StyleReader();

    private int index;


    public static StyleReader instance() {
        return style;
    }


    public PGStyle getStyles(IControl control, PGMaster pgMaster, Element sp, Element style) {
        PGStyle pgStyle = new PGStyle();
        processSp(pgStyle, sp);
        processStyle(control, pgStyle, pgMaster, style);
        return pgStyle;
    }


    private void processSp(PGStyle pgStyle, Element sp) {
        if (sp != null) {

            Element spPr = sp.element("spPr");
            if (spPr != null) {
                pgStyle.setAnchor(ReaderKit.instance().getShapeAnchor(spPr.element("xfrm"), 1.0f, 1.0f));
            }

            Element txBody = sp.element("txBody");
            if (txBody != null) {
                Element bodyPr = txBody.element("bodyPr");
                if (bodyPr != null) {
                    IAttributeSet attr = new AttributeSetImpl();
                    SectionAttr.instance().setSectionAttribute(bodyPr, attr, null, null, false);
                    pgStyle.setSectionAttr(attr);
                }
            }
        }
    }


    private void processStyle(IControl control, PGStyle pgStyle, PGMaster pgMaster, Element style) {
        if (style != null) {
            Element lvl1pPr = style.element("lvl1pPr");
            if (lvl1pPr != null) {
                processStyleAttribute(control, pgStyle, pgMaster, lvl1pPr, 1);
            }
            Element lvl2pPr = style.element("lvl2pPr");
            if (lvl2pPr != null) {
                processStyleAttribute(control, pgStyle, pgMaster, lvl2pPr, 2);
            }
            Element lvl3pPr = style.element("lvl3pPr");
            if (lvl3pPr != null) {
                processStyleAttribute(control, pgStyle, pgMaster, lvl3pPr, 3);
            }
            Element lvl4pPr = style.element("lvl4pPr");
            if (lvl4pPr != null) {
                processStyleAttribute(control, pgStyle, pgMaster, lvl4pPr, 4);
            }
            Element lvl5pPr = style.element("lvl5pPr");
            if (lvl5pPr != null) {
                processStyleAttribute(control, pgStyle, pgMaster, lvl5pPr, 5);
            }
            Element lvl6pPr = style.element("lvl6pPr");
            if (lvl6pPr != null) {
                processStyleAttribute(control, pgStyle, pgMaster, lvl6pPr, 6);
            }
            Element lvl7pPr = style.element("lvl7pPr");
            if (lvl7pPr != null) {
                processStyleAttribute(control, pgStyle, pgMaster, lvl7pPr, 7);
            }
            Element lvl8pPr = style.element("lvl8pPr");
            if (lvl8pPr != null) {
                processStyleAttribute(control, pgStyle, pgMaster, lvl8pPr, 8);
            }
            Element lvl9pPr = style.element("lvl9pPr");
            if (lvl9pPr != null) {
                processStyleAttribute(control, pgStyle, pgMaster, lvl9pPr, 9);
            }
        }
    }


    private void processStyleAttribute(IControl control, PGStyle pgStyle, PGMaster pgMaster, Element paraStyle, int lvl) {
        Style style = new Style();
        style.setId(index);
        style.setType((byte) 0);

        ParaAttr.instance().setParaAttribute(control, paraStyle, style.getAttrbuteSet(), null, -1, -1, 0, false, false);
        Element defRPr = paraStyle.element("defRPr");

        RunAttr.instance().setRunAttribute(pgMaster, defRPr, style.getAttrbuteSet(), null, 100, -1, false);
        RunAttr.instance().setMaxFontSize(AttrManage.instance().getFontSize(style.getAttrbuteSet(),
                style.getAttrbuteSet()));

        ParaAttr.instance().processParaWithPct(paraStyle, style.getAttrbuteSet());
        RunAttr.instance().resetMaxFontSize();
        StyleManage.instance().addStyle(style);

        pgStyle.addStyle(lvl, index);
        BulletNumberManage.instance().addBulletNumber(control, index, paraStyle);
        index++;
    }


    public int getStyleIndex() {
        return index;
    }


    public void setStyleIndex(int index) {
        this.index = index;
    }
}

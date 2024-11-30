
package com.document.render.office.fc.ppt.attribute;

import com.document.render.office.constant.MainConstant;
import com.document.render.office.constant.SSConstant;
import com.document.render.office.constant.wp.AttrIDConstant;
import com.document.render.office.constant.wp.WPAttrConstant;
import com.document.render.office.constant.wp.WPModelConstant;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.ppt.bulletnumber.BulletNumberManage;
import com.document.render.office.fc.ppt.reader.ReaderKit;
import com.document.render.office.pg.model.PGLayout;
import com.document.render.office.pg.model.PGMaster;
import com.document.render.office.pg.model.PGPlaceholderUtil;
import com.document.render.office.pg.model.PGStyle;
import com.document.render.office.simpletext.model.AttrManage;
import com.document.render.office.simpletext.model.AttributeSetImpl;
import com.document.render.office.simpletext.model.IAttributeSet;
import com.document.render.office.simpletext.model.ParagraphElement;
import com.document.render.office.simpletext.model.SectionElement;
import com.document.render.office.simpletext.model.Style;
import com.document.render.office.simpletext.model.StyleManage;
import com.document.render.office.ss.model.style.CellStyle;
import com.document.render.office.system.IControl;

import java.util.List;


public class ParaAttr {

    public static final float POINT_PER_LINE_PER_FONTSIZE = 1.2f;

    private static ParaAttr kit = new ParaAttr();


    public static ParaAttr instance() {
        return kit;
    }


    public int processParagraph(IControl control, PGMaster master, PGLayout pgLayout, PGStyle defaultStyle,
                                SectionElement secElem, Element styleElement, Element txBody, String type, int idx) {
        String val;
        int fontScale = 100;
        int lnSpcReduction = 0;
        Boolean defaultFontColor = false;
        Element bodyPr = txBody.element("bodyPr");
        if (bodyPr != null) {
            Element normAutofit = bodyPr.element("normAutofit");
            if (normAutofit != null) {
                if (normAutofit.attribute("fontScale") != null) {
                    val = normAutofit.attributeValue("fontScale");
                    if (val != null && val.length() > 0) {
                        fontScale = Integer.parseInt(val) / 1000;
                    }
                }
                if (normAutofit.attribute("lnSpcReduction") != null) {
                    val = normAutofit.attributeValue("lnSpcReduction");
                    if (val != null && val.length() > 0) {
                        lnSpcReduction = Integer.parseInt(val);
                    }
                }
            }
        }
        int offset = 0;
        boolean subTitle = PGPlaceholderUtil.SUBTITLE.equals(type);
        Element lstStyle = txBody.element("lstStyle");
        List<Element> ps = txBody.elements("p");
        for (Element p : ps) {
            int lvl = 1;
            Element pPr = p.element("pPr");
            if (pPr != null && pPr.attribute("lvl") != null) {
                val = pPr.attributeValue("lvl");
                if (val != null && val.length() > 0) {
                    lvl += Integer.parseInt(val);
                }
            }
            int layoutStyle = -1;
            if (pgLayout != null) {
                layoutStyle = pgLayout.getStyleID(type, idx, lvl);
            }
            int masterStyle = -1;
            if (master != null) {
                masterStyle = master.getTextStyle(type, idx, lvl);
            }
            if (masterStyle < 0 && defaultStyle != null) {
                defaultFontColor = true;
                masterStyle = defaultStyle.getStyle(lvl);
            }

            ParagraphElement paraElem = new ParagraphElement();
            paraElem.setStartOffset(offset);
            IAttributeSet attrLayout = null;

            if (lstStyle != null) {
                int ind = lvl;
                if (lvl > 0 || lstStyle.element("defPPr") == null) {
                    ind += 1;
                }
                Element txStyle = null;
                switch (ind) {
                    case 1:
                        txStyle = lstStyle.element("defPPr");
                        break;

                    case 2:
                        txStyle = lstStyle.element("lvl1pPr");
                        break;

                    case 3:
                        txStyle = lstStyle.element("lvl2pPr");
                        break;

                    case 4:
                        txStyle = lstStyle.element("lvl3pPr");
                        break;

                    case 5:
                        txStyle = lstStyle.element("lvl4pPr");
                        break;

                    case 6:
                        txStyle = lstStyle.element("lvl5pPr");
                        break;

                    case 7:
                        txStyle = lstStyle.element("lvl6pPr");
                        break;

                    case 8:
                        txStyle = lstStyle.element("lvl7pPr");
                        break;

                    case 9:
                        txStyle = lstStyle.element("lvl8pPr");
                        break;

                    case 10:
                        txStyle = lstStyle.element("lvl9pPr");
                        break;

                    default:
                        break;
                }
                if (txStyle != null) {
                    attrLayout = new AttributeSetImpl();

                    setParaAttribute(control, txStyle, attrLayout, null, -1, -1, 0, true, subTitle);
                    Element defRPr = txStyle.element("defRPr");

                    RunAttr.instance().setRunAttribute(master, defRPr, attrLayout, null, 100, -1, false);
                    processParaWithPct(txStyle, attrLayout);
                }
            }
            if (attrLayout == null && layoutStyle > 0) {
                Style style = StyleManage.instance().getStyle(layoutStyle);
                if (style != null) {
                    attrLayout = style.getAttrbuteSet();
                }
            } else if (attrLayout == null && styleElement != null) {
                Element fontRef = styleElement.element("fontRef");
                if (fontRef.elements().size() > 0) {
                    int fontColor = ReaderKit.instance().getColor(master, fontRef);
                    attrLayout = new AttributeSetImpl();
                    AttrManage.instance().setFontColor(attrLayout, fontColor);

                }
            } else if (defaultFontColor && attrLayout == null && defaultStyle != null) {
                String fontColor = defaultStyle.getDefaultFontColor(lvl);
                if (fontColor != null) {
                    attrLayout = new AttributeSetImpl();
                    AttrManage.instance().setFontColor(attrLayout, master.getColor(fontColor));
                }
            }

            offset = RunAttr.instance().processRun(master, paraElem, p, attrLayout, offset, fontScale, masterStyle);


            if (p.elements("r").size() == 0 && p.elements("fld").size() == 0) {
                setParaAttribute(control, pPr, paraElem.getAttribute(), attrLayout, layoutStyle, masterStyle,
                        lnSpcReduction, false, subTitle);
            } else {
                setParaAttribute(control, pPr, paraElem.getAttribute(), attrLayout, layoutStyle, masterStyle,
                        lnSpcReduction, true, subTitle);
            }

            processParaWithPct(pPr, paraElem.getAttribute());

            paraElem.setEndOffset(offset);
            secElem.appendParagraph(paraElem, WPModelConstant.MAIN);
        }
        BulletNumberManage.instance().clearData();
        RunAttr.instance().setMaxFontSize(0);
        return offset;
    }


    public void setParaAlign(IAttributeSet attr, String align) {

        if (align.equals("l")) {
            AttrManage.instance().setParaHorizontalAlign(attr, WPAttrConstant.PARA_HOR_ALIGN_LEFT);
        }

        else if (align.equals("ctr")) {
            AttrManage.instance().setParaHorizontalAlign(attr, WPAttrConstant.PARA_HOR_ALIGN_CENTER);
        }

        else if (align.equals("r")) {
            AttrManage.instance().setParaHorizontalAlign(attr, WPAttrConstant.PARA_HOR_ALIGN_RIGHT);
        }
    }


    public void setParaHorizontalAlign(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.PARA_HORIZONTAL_ID)) {
                AttrManage.instance().setParaHorizontalAlign(attrTo,
                        AttrManage.instance().getParaHorizontalAlign(attrFrom));
            }
        }
    }


    public void setParaBefore(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.PARA_BEFORE_ID)) {
                AttrManage.instance().setParaBefore(attrTo,
                        AttrManage.instance().getParaBefore(attrFrom));
            }
        }
    }


    public void setParaAfter(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.PARA_AFTER_ID)) {
                AttrManage.instance().setParaAfter(attrTo,
                        AttrManage.instance().getParaAfter(attrFrom));
            }
        }
    }


    public void setParaLineSpace(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.PARA_LINESPACE_TYPE_ID)) {
                AttrManage.instance().setParaLineSpaceType(attrTo,
                        AttrManage.instance().getParaLineSpaceType(attrFrom));
            }
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.PARA_LINESPACE_ID)) {
                AttrManage.instance().setParaLineSpace(attrTo,
                        AttrManage.instance().getParaLineSpace(attrFrom));
            }
        }
    }


    public void setParaIndentLeft(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.PARA_INDENT_LEFT_ID)) {
                AttrManage.instance().setParaIndentLeft(attrTo,
                        AttrManage.instance().getParaIndentLeft(attrFrom));
            }
        }
    }


    public void setParaIndentRight(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.PARA_INDENT_RIGHT_ID)) {
                AttrManage.instance().setParaIndentRight(attrTo,
                        AttrManage.instance().getParaIndentRight(attrFrom));
            }
        }
    }


    public void setParaSpecialIndent(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.PARA_SPECIALINDENT_ID)) {
                AttrManage.instance().setParaSpecialIndent(attrTo,
                        AttrManage.instance().getParaSpecialIndent(attrFrom));
            }
        }
    }


    public void setParaAttribute(IControl control, Element pPr, IAttributeSet attr, IAttributeSet attrLayout,
                                 int layoutStyle, int masterStyle, int lnSpcReduction, boolean addBullet, boolean subTitle) {
        String val;
        if (pPr != null) {

            Element temp;
            if (pPr.attribute("algn") != null) {
                val = pPr.attributeValue("algn");
                setParaAlign(attr, val);
            } else {
                setParaHorizontalAlign(attrLayout, attr);
            }


            Element spcBef = pPr.element("spcBef");
            if (spcBef != null) {

                temp = spcBef.element("spcPts");
                if (temp != null && temp.attribute("val") != null) {
                    val = temp.attributeValue("val");
                    if (val != null && val.length() > 0) {
                        AttrManage.instance().setParaBefore(attr,
                                (int) (Integer.parseInt(val) / 100 * MainConstant.POINT_TO_TWIPS));
                    }
                }
            } else {
                setParaBefore(attrLayout, attr);
            }


            Element spcAft = pPr.element("spcAft");
            if (spcAft != null) {

                temp = spcAft.element("spcPts");
                if (temp != null && temp.attribute("val") != null) {
                    val = temp.attributeValue("val");
                    if (val != null && val.length() > 0) {
                        AttrManage.instance().setParaAfter(attr,
                                (int) (Integer.parseInt(val) / 100 * MainConstant.POINT_TO_TWIPS));
                    }
                }
            } else {
                setParaAfter(attrLayout, attr);
            }


            Element lnSpc = pPr.element("lnSpc");
            if (lnSpc != null) {

                temp = lnSpc.element("spcPts");
                if (temp != null && temp.attribute("val") != null) {
                    val = temp.attributeValue("val");
                    if (val != null && val.length() > 0) {

                        AttrManage.instance().setParaLineSpaceType(attr, WPAttrConstant.LINE_SPACE_EXACTLY);

                        AttrManage.instance().setParaLineSpace(attr,
                                (int) (Integer.parseInt(val) / 100 * MainConstant.POINT_TO_TWIPS));
                    }
                }


                temp = lnSpc.element("spcPct");
                if (temp != null && temp.attribute("val") != null) {
                    val = temp.attributeValue("val");
                    if (val != null && val.length() > 0) {

                        AttrManage.instance().setParaLineSpaceType(attr, WPAttrConstant.LINE_SAPCE_MULTIPLE);

                        AttrManage.instance().setParaLineSpace(attr, (float) (Integer.parseInt(val) - lnSpcReduction) / 100000);
                    }
                }
            } else {
                if (lnSpcReduction > 0) {

                    AttrManage.instance().setParaLineSpaceType(attr, WPAttrConstant.LINE_SAPCE_MULTIPLE);

                    AttrManage.instance().setParaLineSpace(attr, (float) (100000 - lnSpcReduction) / 100000);
                } else {
                    setParaLineSpace(attrLayout, attr);
                }
            }


            if (pPr.attribute("marR") != null) {
                val = pPr.attributeValue("marR");
                if (val != null && val.length() > 0) {
                    AttrManage.instance().setParaIndentRight(attr,
                            (int) (Integer.parseInt(val) * MainConstant.POINT_DPI / MainConstant.EMU_PER_INCH
                                    * MainConstant.POINT_TO_TWIPS));
                }
            } else {
                setParaIndentRight(attrLayout, attr);
            }
        } else {
            setParaHorizontalAlign(attrLayout, attr);
            setParaBefore(attrLayout, attr);
            setParaAfter(attrLayout, attr);

            if (lnSpcReduction > 0) {

                AttrManage.instance().setParaLineSpaceType(attr, WPAttrConstant.LINE_SAPCE_MULTIPLE);

                AttrManage.instance().setParaLineSpace(attr, (float) (100000 - lnSpcReduction) / 100000);
            } else {
                setParaLineSpace(attrLayout, attr);
            }
            setParaIndentLeft(attrLayout, attr);
            setParaIndentRight(attrLayout, attr);
        }

        Style style = StyleManage.instance().getStyle(masterStyle);



        int leftFrom = 0;
        int left = 0;
        if (pPr != null && pPr.attribute("marL") != null) {
            val = pPr.attributeValue("marL");
            if (val != null && val.length() > 0) {
                left = (int) (Integer.parseInt(val) * MainConstant.POINT_DPI / MainConstant.EMU_PER_INCH
                        * MainConstant.POINT_TO_TWIPS);
                AttrManage.instance().setParaIndentInitLeft(attr, left);
                AttrManage.instance().setParaIndentLeft(attr, left);
            }
        } else if (attrLayout != null) {
            if (AttrManage.instance().hasAttribute(attrLayout, AttrIDConstant.PARA_INDENT_LEFT_ID)) {
                leftFrom = 1;
                left = AttrManage.instance().getParaIndentInitLeft(attrLayout);
                AttrManage.instance().setParaIndentLeft(attr, left);
            }
        } else {
            if (style != null && style.getAttrbuteSet() != null
                    && AttrManage.instance().hasAttribute(style.getAttrbuteSet(), AttrIDConstant.PARA_INDENT_LEFT_ID)) {
                leftFrom = 2;
                left = AttrManage.instance().getParaIndentInitLeft(style.getAttrbuteSet());
                AttrManage.instance().setParaIndentLeft(attr, left);
            }
        }


        int indent = 0;
        if (pPr != null && pPr.attribute("indent") != null) {
            val = pPr.attributeValue("indent");
            if (val != null && val.length() > 0) {
                indent = (int) (Integer.parseInt(val) * MainConstant.POINT_DPI / MainConstant.EMU_PER_INCH
                        * MainConstant.POINT_TO_TWIPS);
                setSpecialIndent(attr, left, indent, true);
            }
        } else if (attrLayout != null) {
            if (AttrManage.instance().hasAttribute(attrLayout, AttrIDConstant.PARA_SPECIALINDENT_ID)) {
                indent = AttrManage.instance().getParaSpecialIndent(attrLayout);





                {
                    setSpecialIndent(attr, left, indent, true);
                }
            }
        } else {
            if (style != null && style.getAttrbuteSet() != null
                    && AttrManage.instance().hasAttribute(style.getAttrbuteSet(), AttrIDConstant.PARA_SPECIALINDENT_ID)) {
                indent = AttrManage.instance().getParaSpecialIndent(style.getAttrbuteSet());





                {
                    setSpecialIndent(attr, left, indent, true);
                }
            }
        }


        if (addBullet && (pPr == null || (pPr != null && pPr.element("buNone") == null))) {
            int id = BulletNumberManage.instance().addBulletNumber(control, -1, pPr);
            if (id == -1 && attrLayout != null) {
                id = AttrManage.instance().getPGParaBulletID(attrLayout);
            }
            if (id == -1 && layoutStyle >= 0) {
                id = BulletNumberManage.instance().getBulletID(layoutStyle);
            }
            if (id == -1 && masterStyle > 0 && !subTitle) {
                id = BulletNumberManage.instance().getBulletID(masterStyle);
            }
            if (id >= 0) {
                AttrManage.instance().setPGParaBulletID(attr, id);
            }
        }


        if (masterStyle > 0) {
            AttrManage.instance().setParaStyleID(attr, masterStyle);
        }
    }


    public void setSpecialIndent(IAttributeSet attr, int left, int indent, boolean bSet) {

        if (indent < 0 && Math.abs(indent) > left) {
            indent = -left;
        }
        AttrManage.instance().setParaSpecialIndent(attr, indent);

        if (bSet && indent < 0) {
            AttrManage.instance().setParaIndentLeft(attr, left + indent);
        }
    }


    public void setParaAttribute(CellStyle style, IAttributeSet attr, IAttributeSet attrLayout) {
        if (style != null && attrLayout != null) {
            int indent = (int) (style.getIndent() * SSConstant.INDENT_TO_PIXEL);

            switch (style.getHorizontalAlign()) {
                case CellStyle.ALIGN_LEFT:
                case CellStyle.ALIGN_GENERAL:
                    attrLayout.setAttribute(AttrIDConstant.PARA_INDENT_LEFT_ID, Math.round(indent * MainConstant.PIXEL_TO_TWIPS));
                    attrLayout.setAttribute(AttrIDConstant.PARA_INDENT_RIGHT_ID, 0);
                    AttrManage.instance().setParaHorizontalAlign(attr, WPAttrConstant.PARA_HOR_ALIGN_LEFT);
                    break;

                case CellStyle.ALIGN_RIGHT:

                    attrLayout.setAttribute(AttrIDConstant.PARA_INDENT_LEFT_ID, 0);
                    attrLayout.setAttribute(AttrIDConstant.PARA_INDENT_RIGHT_ID, Math.round(indent * MainConstant.PIXEL_TO_TWIPS));
                    AttrManage.instance().setParaHorizontalAlign(attr, WPAttrConstant.PARA_HOR_ALIGN_RIGHT);
                    break;

                case CellStyle.ALIGN_CENTER:
                case CellStyle.ALIGN_FILL:
                case CellStyle.ALIGN_JUSTIFY:
                case CellStyle.ALIGN_CENTER_SELECTION:
                    AttrManage.instance().setParaHorizontalAlign(attr, WPAttrConstant.PARA_HOR_ALIGN_CENTER);
                    break;
            }


            setParaBefore(attrLayout, attr);

            setParaAfter(attrLayout, attr);

            setParaLineSpace(attrLayout, attr);



            setParaIndentLeft(attrLayout, attr);


            setParaIndentRight(attrLayout, attr);


            setParaSpecialIndent(attrLayout, attr);
        } else if (attrLayout != null) {
            setParaHorizontalAlign(attrLayout, attr);
            setParaBefore(attrLayout, attr);
            setParaAfter(attrLayout, attr);
            setParaLineSpace(attrLayout, attr);
        }
    }


    public void processParaWithPct(Element pPr, IAttributeSet attr) {
        int fontSize = RunAttr.instance().getMaxFontSize();
        if (pPr != null) {
            Element temp;
            String val;

            Element spcBef = pPr.element("spcBef");
            if (spcBef != null) {

                temp = spcBef.element("spcPct");
                if (temp != null && temp.attribute("val") != null) {
                    val = temp.attributeValue("val");
                    if (val != null && val.length() > 0) {
                        AttrManage.instance().setParaBefore(attr,
                                (int) (Integer.parseInt(val) / 100000.f * fontSize * POINT_PER_LINE_PER_FONTSIZE * MainConstant.POINT_TO_TWIPS));
                    }
                }
            }


            Element spcAft = pPr.element("spcAft");
            if (spcAft != null) {

                temp = spcAft.element("spcPct");
                if (temp != null && temp.attribute("val") != null) {
                    val = temp.attributeValue("val");
                    if (val != null && val.length() > 0) {
                        AttrManage.instance().setParaAfter(attr,
                                (int) (Integer.parseInt(val) / 100000.f * fontSize * POINT_PER_LINE_PER_FONTSIZE * MainConstant.POINT_TO_TWIPS));
                    }
                }
            }
        }
    }
}


package com.document.render.office.fc.ppt.attribute;

import android.graphics.Color;

import com.document.render.office.constant.wp.AttrIDConstant;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.ppt.reader.HyperlinkReader;
import com.document.render.office.fc.ppt.reader.ReaderKit;
import com.document.render.office.fc.xls.Reader.SchemeColorUtil;
import com.document.render.office.pg.model.PGMaster;
import com.document.render.office.simpletext.font.Font;
import com.document.render.office.simpletext.font.FontTypefaceManage;
import com.document.render.office.simpletext.model.AttrManage;
import com.document.render.office.simpletext.model.IAttributeSet;
import com.document.render.office.simpletext.model.LeafElement;
import com.document.render.office.simpletext.model.ParagraphElement;
import com.document.render.office.simpletext.model.Style;
import com.document.render.office.simpletext.model.StyleManage;
import com.document.render.office.ss.model.baseModel.Cell;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.model.style.CellStyle;
import com.document.render.office.ss.util.ColorUtil;
import com.document.render.office.ss.util.format.NumericFormatter;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;


public class RunAttr {
    private static RunAttr kit = new RunAttr();

    private int maxFontSize = 0;

    private boolean table;

    private boolean slide;


    public static RunAttr instance() {
        return kit;
    }


    public int processRun(PGMaster pgMaster, ParagraphElement paraElem,
                          Element p, IAttributeSet attrLayout, int offset, int fontScale, int styleID) {
        maxFontSize = 0;
        LeafElement leaf = null;
        Element pPr = p.element("pPr");

        if (p.elements("r").size() == 0 && p.elements("fld").size() == 0 && p.elements("br").size() == 0) {
            leaf = new LeafElement("\n");

            if (pPr != null) {
                pPr = pPr.element("rPr");
            }
            if (pPr == null) {
                pPr = p.element("endParaRPr");
            }
            setRunAttribute(pgMaster, pPr, leaf.getAttribute(), attrLayout, fontScale, styleID, true);

            setMaxFontSize(AttrManage.instance().getFontSize(paraElem.getAttribute(), leaf.getAttribute()));
            leaf.setStartOffset(offset);
            offset++;
            leaf.setEndOffset(offset);
            paraElem.appendLeaf(leaf);
            return offset;
        }
        for (Iterator<?> it = p.elementIterator(); it.hasNext(); ) {
            Element r = (Element) it.next();
            String name = r.getName();
            if (name.equals("r") || name.equals("fld") || name.equals("br")) {
                String text = null;
                if (name.equals("fld")
                        && r.attributeValue("type") != null
                        && r.attributeValue("type").contains("datetime")) {

                    text = NumericFormatter.instance().getFormatContents("yyyy/m/d", new Date(System.currentTimeMillis()));
                } else {
                    Element t = r.element("t");
                    if (name.equals("br")) {
                        text = String.valueOf('\u000b');
                    } else if (t != null) {
                        text = t.getText();
                    }
                }

                if (text != null) {
                    text = text.replace((char) 160, ' ');
                    int len = text.length();
                    if (len > 0) {
                        leaf = new LeafElement(text);

                        setRunAttribute(pgMaster, r.element("rPr"), leaf.getAttribute(), attrLayout,
                                fontScale, styleID, "\n".equals(text));

                        setMaxFontSize(AttrManage.instance().getFontSize(paraElem.getAttribute(), leaf.getAttribute()));

                        leaf.setStartOffset(offset);
                        offset += len;

                        leaf.setEndOffset(offset);
                        paraElem.appendLeaf(leaf);
                    }
                }
            }
        }
        if (leaf != null) {
            leaf.setText(leaf.getText(null) + "\n");
            offset++;
        }
        return offset;
    }


    private void setFontSize(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.FONT_SIZE_ID)) {
                AttrManage.instance().setFontSize(attrTo, AttrManage.instance().getFontSize(null, attrFrom));
            }
        }
    }


    private void setFontTypeface(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.FONT_NAME_ID)) {
                AttrManage.instance().setFontName(attrTo, AttrManage.instance().getFontName(null, attrFrom));
            }
        }
    }


    private void setFontColor(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.FONT_COLOR_ID)) {
                AttrManage.instance().setFontColor(attrTo, AttrManage.instance().getFontColor(null, attrFrom));
            }
        }
    }


    private void setFontBold(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.FONT_BOLD_ID)) {
                AttrManage.instance().setFontBold(attrTo, AttrManage.instance().getFontBold(null, attrFrom));
            }
        }
    }


    private void setFontItalic(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.FONT_ITALIC_ID)) {
                AttrManage.instance().setFontItalic(attrTo, AttrManage.instance().getFontItalic(null, attrFrom));
            }
        }
    }


    private void setFontStrike(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.FONT_STRIKE_ID)) {
                AttrManage.instance().setFontStrike(attrTo, AttrManage.instance().getFontStrike(null, attrFrom));
            }
        }
    }


    private void setFontDoubleStrike(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.FONT_DOUBLESTRIKE_ID)) {
                AttrManage.instance().setFontDoubleStrike(attrTo, AttrManage.instance().getFontDoubleStrike(null, attrFrom));
            }
        }
    }


    private void setFontUnderline(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.FONT_UNDERLINE_ID)) {
                AttrManage.instance().setFontUnderline(attrTo, AttrManage.instance().getFontUnderline(null, attrFrom));
                if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.FONT_UNDERLINE_COLOR_ID)) {
                    AttrManage.instance().setFontUnderlineColr(attrTo, AttrManage.instance().getFontUnderlineColor(null, attrFrom));
                } else {
                    if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.FONT_COLOR_ID)) {
                        AttrManage.instance().setFontUnderlineColr(attrTo, AttrManage.instance().getFontColor(null, attrFrom));
                    }
                }
            }
        }
    }


    private void setFontScript(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.FONT_SCRIPT_ID)) {
                AttrManage.instance().setFontScript(attrTo, AttrManage.instance().getFontScript(null, attrFrom));
            }
        }
    }


    private void setHyperlinkID(IAttributeSet attrFrom, IAttributeSet attrTo) {
        if (attrFrom != null) {
            if (AttrManage.instance().hasAttribute(attrFrom, AttrIDConstant.FONT_HYPERLINK_ID)) {
                AttrManage.instance().setHyperlinkID(attrTo, AttrManage.instance().getHperlinkID(attrFrom));
            }
        }
    }


    public void setRunAttribute(PGMaster master, Element rPr, IAttributeSet attr, IAttributeSet attrLayout,
                                int fontScale, int styleID, boolean newLine) {
        if (rPr != null) {
            String val;

            if (rPr.attribute("sz") != null) {
                val = rPr.attributeValue("sz");
                if (val != null && val.length() > 0) {
                    AttrManage.instance().setFontSize(attr, (int) (Float.parseFloat(val) / 100));
                }
            } else {
                setFontSize(attrLayout, attr);
            }

            if (!newLine) {

                Element temp = rPr.element("latin");
                if (temp != null || rPr.element("ea") != null) {
                    if (temp == null) {
                        temp = rPr.element("ea");
                    }
                    val = temp.attributeValue("typeface");
                    if (val != null) {
                        int index = FontTypefaceManage.instance().addFontName(val);
                        if (index >= 0) {
                            AttrManage.instance().setFontName(attr, index);
                        }
                    }
                } else {
                    setFontTypeface(attrLayout, attr);
                }


                temp = rPr.element("solidFill");
                Integer fontColor = null;
                if (temp != null) {
                    fontColor = ReaderKit.instance().getColor(master, temp);
                    AttrManage.instance().setFontColor(attr, fontColor);
                } else if ((temp = rPr.element("gradFill")) != null) {
                    Element gsLst = temp.element("gsLst");
                    if (gsLst != null) {
                        fontColor = ReaderKit.instance().getColor(master, gsLst.element("gs"));
                        AttrManage.instance().setFontColor(attr, fontColor);
                    }
                } else {
                    setFontColor(attrLayout, attr);
                }


                if (rPr.attribute("b") != null) {
                    val = rPr.attributeValue("b");
                    if (val != null && val.length() > 0 && Integer.parseInt(val) > 0) {
                        AttrManage.instance().setFontBold(attr, true);
                    }
                } else {
                    setFontBold(attrLayout, attr);
                }


                if (rPr.attribute("i") != null) {
                    val = rPr.attributeValue("i");
                    if (val != null && val.length() > 0) {
                        AttrManage.instance().setFontItalic(attr, Integer.parseInt(val) > 0);
                    }
                } else {
                    setFontItalic(attrLayout, attr);
                }


                if (rPr.attribute("u") != null) {
                    val = rPr.attributeValue("u");
                    if (val != null && val.length() > 0) {
                        if (!val.equalsIgnoreCase("none")) {
                            AttrManage.instance().setFontUnderline(attr, 1);

                            Element uFill = rPr.element("uFill");
                            if (uFill != null && (temp = uFill.element("solidFill")) != null) {
                                AttrManage.instance().setFontUnderlineColr(attr, ReaderKit.instance().getColor(master, temp));
                            } else {
                                if (fontColor != null) {
                                    AttrManage.instance().setFontUnderlineColr(attr, fontColor);
                                }
                            }
                        }
                    }
                } else {
                    setFontUnderline(attrLayout, attr);
                }


                if (rPr.attribute("strike") != null) {
                    val = rPr.attributeValue("strike");
                    if (val.equals("dblStrike")) {

                        AttrManage.instance().setFontDoubleStrike(attr, true);
                    } else if (val.equals("sngStrike")) {
                        AttrManage.instance().setFontStrike(attr, true);
                    }
                } else {
                    setFontStrike(attrLayout, attr);
                    setFontDoubleStrike(attrLayout, attr);
                }


                if (rPr.attribute("baseline") != null) {
                    val = rPr.attributeValue("baseline");
                    if (val != null && val.length() > 0) {
                        int value = Integer.parseInt(val);
                        if (value != 0) {
                            AttrManage.instance().setFontScript(attr, value > 0 ? 1 : 2);
                        }
                    }
                } else {
                    setFontScript(attrLayout, attr);
                }


                temp = rPr.element("hlinkClick");
                if (temp != null) {
                    int color = Color.BLUE;
                    if (master != null) {
                        color = master.getSchemeColor().get("hlink");
                    }
                    AttrManage.instance().setFontColor(attr, color);
                    AttrManage.instance().setFontUnderline(attr, 1);
                    AttrManage.instance().setFontUnderlineColr(attr, color);

                    val = temp.attributeValue("id");
                    if (val != null && val.length() > 0) {
                        AttrManage.instance().setHyperlinkID(attr, HyperlinkReader.instance().getLinkIndex(val));
                    }
                } else {
                    setHyperlinkID(attrLayout, attr);
                }
            }
        } else if (attrLayout != null) {
            setFontSize(attrLayout, attr);
            if (!newLine) {
                setFontTypeface(attrLayout, attr);
                setFontColor(attrLayout, attr);
                setFontBold(attrLayout, attr);
                setFontItalic(attrLayout, attr);
                setFontUnderline(attrLayout, attr);
                setFontStrike(attrLayout, attr);
                setFontDoubleStrike(attrLayout, attr);
                setFontScript(attrLayout, attr);
                setHyperlinkID(attrLayout, attr);
            }
        }
        AttrManage.instance().setFontScale(attr, fontScale);


        if (!AttrManage.instance().hasAttribute(attr, AttrIDConstant.FONT_SIZE_ID)) {
            Style style = StyleManage.instance().getStyle(styleID);
            if (style != null && style.getAttrbuteSet() != null
                    && AttrManage.instance().hasAttribute(style.getAttrbuteSet(), AttrIDConstant.FONT_SIZE_ID)) {
                return;
            } else {
                if (!table && slide) {
                    AttrManage.instance().setFontSize(attr, 18);
                }
            }
        }
    }


    private int getRunPropColor(Workbook book, Element clr) {
        int color = -1;
        String val;
        if (clr.attributeValue("indexed") != null) {
            val = clr.attributeValue("indexed");
            color = book.getColor(Integer.parseInt(val));
        } else if (clr.attributeValue("theme") != null) {
            val = clr.attributeValue("theme");

            color = SchemeColorUtil.getThemeColor(book, Integer.parseInt(val));
        } else if (clr.attributeValue("rgb") != null) {
            val = clr.attributeValue("rgb");

            color = (int) Long.parseLong(val, 16);
        }

        if (clr.attributeValue("tint") != null) {
            double tint = Double.parseDouble(clr.attributeValue("tint"));
            color = ColorUtil.instance().getColorWithTint(color, tint);
        }

        return color;
    }


    public int getColor(Workbook book, Element solidFillElement) {
        String val;
        Element clr;
        int color = -1;
        if (solidFillElement.element("srgbClr") != null) {
            clr = solidFillElement.element("srgbClr");
            color = (int) Long.parseLong(clr.attributeValue("val"), 16);
            color = (0xFF << 24) | color;
        } else if (solidFillElement.element("schemeClr") != null) {
            clr = solidFillElement.element("schemeClr");

            Map<String, Integer> schemeColor = SchemeColorUtil.getSchemeColor(book);
            color = schemeColor.get(clr.attributeValue("val"));

            if (clr.element("tint") != null) {
                color = ColorUtil.instance().getColorWithTint(color,
                        Integer.parseInt(clr.element("tint").attributeValue("val")) / 100000.0);
            } else if (clr.element("lumOff") != null) {
                color = ColorUtil.instance().getColorWithTint(color,
                        Integer.parseInt(clr.element("lumOff").attributeValue("val")) / 100000.0);
            } else if (clr.element("lumMod") != null) {
                color = ColorUtil.instance().getColorWithTint(color,
                        Integer.parseInt(clr.element("lumMod").attributeValue("val")) / 100000.0 - 1);
            } else if (clr.element("shade") != null) {
                color = ColorUtil.instance().getColorWithTint(color,
                        -Integer.parseInt(clr.element("shade").attributeValue("val")) / 200000.0);
            }

            if (clr.element("alpha") != null) {
                val = clr.element("alpha").attributeValue("val");
                if (val != null) {
                    int alpha = (int) (Integer.parseInt(val) / 100000f * 255);
                    color = (0xFFFFFF & color) | (alpha << 24);
                }
            }
        } else if (solidFillElement.element("sysClr") != null) {
            clr = solidFillElement.element("sysClr");

            color = Integer.parseInt(clr.attributeValue("lastClr"), 16);
            color = (0xFF << 24) | color;
        }
        return color;
    }


    public void setRunAttribute(Sheet sheet, Element rPr, IAttributeSet attr, IAttributeSet attrLayout) {
        if (rPr != null) {
            String val;

            if (rPr.attribute("sz") != null) {
                val = rPr.attributeValue("sz");
                if (val != null && val.length() > 0) {
                    AttrManage.instance().setFontSize(attr, (int) (Float.parseFloat(val) / 100));
                }
            } else {
                setFontSize(attrLayout, attr);
            }


            Element temp = rPr.element("solidFill");
            if (temp != null) {
                AttrManage.instance().setFontColor(attr, getColor(sheet.getWorkbook(), temp));
            } else {
                setFontColor(attrLayout, attr);
            }


            if (rPr.attribute("b") != null) {
                AttrManage.instance().setFontBold(attr, Integer.parseInt(rPr.attributeValue("b")) == 1 ? true : false);
            } else {
                setFontBold(attrLayout, attr);
            }


            if (rPr.attribute("i") != null) {
                AttrManage.instance().setFontItalic(attr, Integer.parseInt(rPr.attributeValue("i")) == 1 ? true : false);
            } else {
                setFontItalic(attrLayout, attr);
            }


            if (rPr.attributeValue("u") != null && !rPr.attributeValue("u").equalsIgnoreCase("none")) {
                AttrManage.instance().setFontUnderline(attr, 1);
                Element uFill = rPr.element("uFill");
                if (uFill != null) {
                    temp = uFill.element("solidFill");
                    if (temp != null) {
                        AttrManage.instance().setFontUnderlineColr(attr, getColor(sheet.getWorkbook(), temp));
                    }
                }
            } else {
                setFontUnderline(attrLayout, attr);
            }


            if (rPr.attribute("strike") != null) {
                val = rPr.attributeValue("strike");
                if (val.equals("dblStrike")) {

                    AttrManage.instance().setFontDoubleStrike(attr, true);
                } else if (val.equals("sngStrike")) {
                    AttrManage.instance().setFontStrike(attr, true);
                }
            } else {
                setFontStrike(attrLayout, attr);
                setFontDoubleStrike(attrLayout, attr);
            }


            if (rPr.attribute("baseline") != null) {
                val = rPr.attributeValue("baseline");
                if (val != null && !val.equalsIgnoreCase("0")) {
                    AttrManage.instance().setFontScript(attr, Integer.parseInt(val) > 0 ? 1 : 2);
                }
            } else {
                setFontScript(attrLayout, attr);
            }


            temp = rPr.element("hlinkClick");
            if (temp != null && temp.attribute("id") != null) {
                val = temp.attributeValue("id");
                if (val != null && val.length() > 0) {
                    AttrManage.instance().setFontColor(attr, Color.BLUE);
                    AttrManage.instance().setFontUnderline(attr, 1);
                    AttrManage.instance().setFontUnderlineColr(attr, Color.BLUE);
                    AttrManage.instance().setHyperlinkID(attr, HyperlinkReader.instance().getLinkIndex(val));
                }
            } else {
                setHyperlinkID(attrLayout, attr);
            }
        } else if (attrLayout != null) {
            setFontSize(attrLayout, attr);
            setFontColor(attrLayout, attr);
            setFontBold(attrLayout, attr);
            setFontItalic(attrLayout, attr);
            setFontUnderline(attrLayout, attr);
            setFontStrike(attrLayout, attr);
            setFontDoubleStrike(attrLayout, attr);
            setFontScript(attrLayout, attr);
            setHyperlinkID(attrLayout, attr);
        }
    }


    public void setRunAttribute(Workbook book, int fontID, Element rPr, IAttributeSet attr, IAttributeSet attrLayout) {
        if (rPr != null) {
            String val;

            Element temp = rPr.element("sz");

            if (temp != null) {
                val = temp.attributeValue("val");
                if (val != null && val.length() > 0) {
                    AttrManage.instance().setFontSize(attr, (int) Float.parseFloat(val));
                }
            } else {
                setFontSize(attrLayout, attr);
            }


            temp = rPr.element("color");
            if (temp != null) {
                AttrManage.instance().setFontColor(attr, getRunPropColor(book, temp));
            } else {
                setFontColor(attrLayout, attr);
            }


            temp = rPr.element("b");
            if (temp != null) {
                AttrManage.instance().setFontBold(attr, true);
            } else {
                setFontBold(attrLayout, attr);
            }


            temp = rPr.element("i");
            if (temp != null) {
                AttrManage.instance().setFontItalic(attr, true);
            } else {
                setFontItalic(attrLayout, attr);
            }


            temp = rPr.element("u");
            if (temp != null) {
                AttrManage.instance().setFontUnderline(attr, 1);
            } else {
                setFontUnderline(attrLayout, attr);
            }


            temp = rPr.element("strike");
            if (temp != null) {
                AttrManage.instance().setFontStrike(attr, true);
                setFontDoubleStrike(attrLayout, attr);
            } else {
                setFontStrike(attrLayout, attr);
                setFontDoubleStrike(attrLayout, attr);
            }


            temp = rPr.element("vertAlign");
            if (temp != null) {
                val = temp.attributeValue("val");
                if (val.equalsIgnoreCase("superscript")) {
                    AttrManage.instance().setFontScript(attr, Font.SS_SUPER);
                } else if (val.equalsIgnoreCase("subscript")) {
                    AttrManage.instance().setFontScript(attr, Font.SS_SUB);
                } else {
                    AttrManage.instance().setFontScript(attr, Font.SS_NONE);
                }
            } else {
                setFontScript(attrLayout, attr);
            }


            setHyperlinkID(attrLayout, attr);
        } else if (attrLayout != null) {
            Font font = book.getFont(fontID);
            if (font != null) {
                AttrManage.instance().setFontSize(attr, (int) font.getFontSize());
                AttrManage.instance().setFontColor(attr, book.getColor(font.getColorIndex()));
                AttrManage.instance().setFontBold(attr, font.isBold());
                AttrManage.instance().setFontItalic(attr, font.isItalic());
                AttrManage.instance().setFontUnderline(attr, font.getUnderline());
                AttrManage.instance().setFontStrike(attr, font.isStrikeline());
                setFontDoubleStrike(attrLayout, attr);
                AttrManage.instance().setFontScript(attr, font.getSuperSubScript());
                setHyperlinkID(attrLayout, attr);
            } else {
                setFontSize(attrLayout, attr);
                setFontColor(attrLayout, attr);
                setFontBold(attrLayout, attr);
                setFontItalic(attrLayout, attr);
                setFontUnderline(attrLayout, attr);
                setFontStrike(attrLayout, attr);
                setFontDoubleStrike(attrLayout, attr);
                setFontScript(attrLayout, attr);
                setHyperlinkID(attrLayout, attr);
            }
        }
    }


    public void setRunAttribute(Sheet sheet, Cell cell, IAttributeSet attr, IAttributeSet attrLayout) {
        if (cell != null) {
            CellStyle style = cell.getCellStyle();
            Workbook book = sheet.getWorkbook();
            Font font = book.getFont(style.getFontIndex());


            AttrManage.instance().setFontSize(attr, (int) (font.getFontSize() + 0.5f));


            AttrManage.instance().setFontColor(attr, book.getColor(font.getColorIndex()));


            AttrManage.instance().setFontBold(attr, font.isBold());


            AttrManage.instance().setFontItalic(attr, font.isItalic());


            AttrManage.instance().setFontUnderline(attr, font.getUnderline());



            AttrManage.instance().setFontStrike(attr, font.isStrikeline());
        } else if (attrLayout != null) {
            setFontSize(attrLayout, attr);
            setFontColor(attrLayout, attr);
            setFontBold(attrLayout, attr);
            setFontItalic(attrLayout, attr);
            setFontUnderline(attrLayout, attr);
            setFontStrike(attrLayout, attr);
            setFontDoubleStrike(attrLayout, attr);
            setFontScript(attrLayout, attr);
            setHyperlinkID(attrLayout, attr);
        }
    }


    public void setRunAttribute(Sheet sheet, Font font, IAttributeSet attr, IAttributeSet attrLayout) {
        if (font != null) {
            Workbook book = sheet.getWorkbook();


            AttrManage.instance().setFontSize(attr, (int) (font.getFontSize() + 0.5f));


            AttrManage.instance().setFontColor(attr, book.getColor(font.getColorIndex()));


            AttrManage.instance().setFontBold(attr, font.isBold());


            AttrManage.instance().setFontItalic(attr, font.isItalic());


            AttrManage.instance().setFontUnderline(attr, font.getUnderline());



            AttrManage.instance().setFontStrike(attr, font.isStrikeline());
        } else if (attrLayout != null) {
            setFontSize(attrLayout, attr);
            setFontColor(attrLayout, attr);
            setFontBold(attrLayout, attr);
            setFontItalic(attrLayout, attr);
            setFontUnderline(attrLayout, attr);
            setFontStrike(attrLayout, attr);
            setFontDoubleStrike(attrLayout, attr);
            setFontScript(attrLayout, attr);
            setHyperlinkID(attrLayout, attr);
        }
    }


    public int getMaxFontSize() {
        return maxFontSize;
    }


    public void setMaxFontSize(int size) {
        if (size > maxFontSize) {
            maxFontSize = size;
        }
    }


    public void resetMaxFontSize() {
        maxFontSize = 0;
    }


    public boolean isTable() {
        return table;
    }


    public void setTable(boolean table) {
        this.table = table;
    }


    public boolean isSlide() {
        return slide;
    }


    public void setSlide(boolean slide) {
        this.slide = slide;
    }


    public void dispose() {
        maxFontSize = 0;
    }
}


package com.document.render.office.wp.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.document.render.office.common.bulletnumber.ListData;
import com.document.render.office.common.bulletnumber.ListKit;
import com.document.render.office.common.bulletnumber.ListLevel;
import com.document.render.office.constant.MainConstant;
import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.simpletext.model.AttrManage;
import com.document.render.office.simpletext.model.IDocument;
import com.document.render.office.simpletext.model.IElement;
import com.document.render.office.simpletext.model.Style;
import com.document.render.office.simpletext.model.StyleManage;
import com.document.render.office.simpletext.view.AbstractView;
import com.document.render.office.simpletext.view.CharAttr;
import com.document.render.office.simpletext.view.DocAttr;
import com.document.render.office.simpletext.view.PageAttr;
import com.document.render.office.simpletext.view.ParaAttr;


public class BNView extends AbstractView {


    private Object content;

    private Paint paint;

    private CharAttr charAttr;

    private ListLevel currLevel;


    public BNView() {
        charAttr = new CharAttr();
        paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }


    public short getType() {
        return WPViewConstant.BN_VIEW;
    }


    public synchronized int doLayout(IDocument doc, DocAttr docAttr, PageAttr pageAttr, ParaAttr paraAttr,
                                     ParagraphView para, int x, int y, int w, int h, int flag) {
        setLocation(paraAttr.listAlignIndent + x, y);
        int breakType = WPViewConstant.BREAK_NO;
        IElement paraElem = para.getElement();
        IElement leafElem = null;

        String text = "";
        if (paraAttr.listID >= 0) {
            ListData listData = para.getControl().getSysKit().getListManage().getListData(paraAttr.listID);
            if (listData == null) {
                return breakType;
            }
            if (listData.getLinkStyleID() >= 0) {
                Style style = StyleManage.instance().getStyle(listData.getLinkStyleID());
                if (style != null) {
                    int listID = AttrManage.instance().getParaListID(style.getAttrbuteSet());
                    listData = para.getControl().getSysKit().getListManage().getListData(listID);
                    if (listData == null || listData.getLevels().length == 0) {
                        return breakType;
                    }
                }
            }
            leafElem = doc.getLeaf(paraElem.getEndOffset() - 1);
            ListLevel listLevel = listData.getLevel(paraAttr.listLevel);
            text = ListKit.instance().getBulletText(listData, listLevel, docAttr, paraAttr.listLevel);
            int preParaLevel = docAttr.rootType == WPViewConstant.NORMAL_ROOT ?
                    listData.getNormalPreParaLevel() : listData.getPreParaLevel();

            if (paraAttr.listLevel < preParaLevel) {

                for (int i = paraAttr.listLevel + 1; i < 9; i++) {
                    if (docAttr.rootType == WPViewConstant.NORMAL_ROOT) {
                        listData.getLevel(i).setNormalParaCount(0);
                    } else {
                        listData.getLevel(i).setParaCount(0);
                    }
                }
            } else if (paraAttr.listLevel > preParaLevel) {

                for (int i = preParaLevel + 1; i < paraAttr.listLevel; i++) {
                    ListLevel temp = listData.getLevel(i);
                    if (docAttr.rootType == WPViewConstant.NORMAL_ROOT) {
                        temp.setNormalParaCount(temp.getNormalParaCount() + 1);
                    } else {
                        temp.setParaCount(temp.getParaCount() + 1);
                    }
                }
            }



            if (docAttr.rootType == WPViewConstant.NORMAL_ROOT) {
                listLevel.setNormalParaCount(listLevel.getNormalParaCount() + 1);
                listData.setNormalPreParaLevel(paraAttr.listLevel);
            } else {
                listLevel.setParaCount(listLevel.getParaCount() + 1);
                listData.setPreParaLevel(paraAttr.listLevel);
            }
            currLevel = listLevel;
        }

        else if (paraAttr.pgBulletID >= 0) {
            leafElem = doc.getLeaf(paraElem.getStartOffset());
            text = para.getControl().getSysKit().getPGBulletText().getBulletText(paraAttr.pgBulletID);
            if (text == null) {
                text = "";
            }
        }

        AttrManage.instance().fillCharAttr(charAttr, paraElem.getAttribute(), leafElem.getAttribute());

        if (charAttr.isBold && charAttr.isItalic) {
            paint.setTextSkewX(-0.2f);
            paint.setFakeBoldText(true);
        }

        else if (charAttr.isBold) {
            paint.setFakeBoldText(true);
        }

        else if (charAttr.isItalic) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));

        paint.setTextSize(charAttr.fontSize * (charAttr.fontScale / 100.f) * MainConstant.POINT_TO_PIXEL);

        paint.setColor(charAttr.fontColor);


        float[] widths = new float[text.length()];
        paint.getTextWidths(text, widths);
        float tW = 0;
        for (int i = 0; i < widths.length; i++) {
            tW += widths[i];
        }
        int ex = (int) ((tW + getX()) % MainConstant.DEFAULT_TAB_WIDTH_PIXEL);
        if (ex > 0) {
            tW += (MainConstant.DEFAULT_TAB_WIDTH_PIXEL - ex);
        }

        setSize((int) tW, (int) Math.ceil((paint.descent() - paint.ascent())));

        content = text;
        return breakType;
    }


    public void draw(Canvas canvas, int originX, int originY, float zoom) {
        int dX = (int) (x * zoom) + originX;
        int dY = (int) (y * zoom) + originY;

        if (content != null && content instanceof String) {
            String text = (String) content;
            float oldFontSize = paint.getTextSize();

            paint.setTextSize((charAttr.subSuperScriptType > 0 ? oldFontSize / 2 : oldFontSize) * zoom);
            canvas.drawText(text, 0, text.length(), dX, dY - paint.ascent(), paint);

            paint.setTextSize(oldFontSize);
        }
    }


    public int getBaseline() {
        return (int) -paint.ascent();
    }


    public void free() {

    }


    public synchronized void dispose() {
        content = null;
        paint = null;
        charAttr = null;
        if (currLevel != null) {
            currLevel.setParaCount(currLevel.getParaCount() - 1);
        }
    }
}

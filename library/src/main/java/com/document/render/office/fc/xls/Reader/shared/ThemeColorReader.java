
package com.document.render.office.fc.xls.Reader.shared;

import android.graphics.Color;

import com.document.render.office.constant.SchemeClrConstant;
import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.io.SAXReader;
import com.document.render.office.fc.openxml4j.opc.PackagePart;
import com.document.render.office.ss.model.baseModel.Workbook;

import java.io.InputStream;


public class ThemeColorReader {
    private static ThemeColorReader reader = new ThemeColorReader();


    public static ThemeColorReader instance() {
        return reader;
    }


    public void getThemeColor(PackagePart themeParts, Workbook book) throws Exception {
        SAXReader saxreader = new SAXReader();
        InputStream in = themeParts.getInputStream();
        Document poiTheme = saxreader.read(in);
        in.close();

        Element root = poiTheme.getRootElement();


        Element themeElements = root.element("themeElements");


        Element themeColorElement = themeElements.element("clrScheme");

        Element ele = themeColorElement.element(SchemeClrConstant.SCHEME_LT1);
        int color = getColorIndex(ele, book);
        book.addSchemeColorIndex(SchemeClrConstant.SCHEME_LT1, color);
        book.addSchemeColorIndex(SchemeClrConstant.SCHEME_BG1, color);

        book.addThemeColorIndex(0, color);

        ele = themeColorElement.element(SchemeClrConstant.SCHEME_DK1);
        color = getColorIndex(ele, book);
        book.addSchemeColorIndex(SchemeClrConstant.SCHEME_DK1, color);
        book.addSchemeColorIndex(SchemeClrConstant.SCHEME_TX1, color);
        book.addThemeColorIndex(1, color);

        ele = themeColorElement.element(SchemeClrConstant.SCHEME_LT2);
        color = getColorIndex(ele, book);
        book.addSchemeColorIndex(SchemeClrConstant.SCHEME_LT2, color);
        book.addSchemeColorIndex(SchemeClrConstant.SCHEME_BG2, color);
        book.addThemeColorIndex(2, color);

        ele = themeColorElement.element(SchemeClrConstant.SCHEME_DK2);
        color = getColorIndex(ele, book);
        book.addSchemeColorIndex(SchemeClrConstant.SCHEME_DK2, color);
        book.addSchemeColorIndex(SchemeClrConstant.SCHEME_TX2, color);
        book.addThemeColorIndex(3, color);

        ele = themeColorElement.element(SchemeClrConstant.SCHEME_ACCENT1);
        color = getColorIndex(ele, book);
        book.addSchemeColorIndex(SchemeClrConstant.SCHEME_ACCENT1, color);
        book.addThemeColorIndex(4, color);

        ele = themeColorElement.element(SchemeClrConstant.SCHEME_ACCENT2);
        color = getColorIndex(ele, book);
        book.addSchemeColorIndex(SchemeClrConstant.SCHEME_ACCENT2, color);
        book.addThemeColorIndex(5, color);

        ele = themeColorElement.element(SchemeClrConstant.SCHEME_ACCENT3);
        color = getColorIndex(ele, book);
        book.addSchemeColorIndex(SchemeClrConstant.SCHEME_ACCENT3, color);
        book.addThemeColorIndex(6, color);

        ele = themeColorElement.element(SchemeClrConstant.SCHEME_ACCENT4);
        color = getColorIndex(ele, book);
        book.addSchemeColorIndex(SchemeClrConstant.SCHEME_ACCENT4, color);
        book.addThemeColorIndex(7, color);

        ele = themeColorElement.element(SchemeClrConstant.SCHEME_ACCENT5);
        color = getColorIndex(ele, book);
        book.addSchemeColorIndex(SchemeClrConstant.SCHEME_ACCENT5, color);
        book.addThemeColorIndex(8, color);

        ele = themeColorElement.element(SchemeClrConstant.SCHEME_ACCENT6);
        color = getColorIndex(ele, book);
        book.addSchemeColorIndex(SchemeClrConstant.SCHEME_ACCENT6, color);
        book.addThemeColorIndex(9, color);

        ele = themeColorElement.element(SchemeClrConstant.SCHEME_HLINK);
        color = getColorIndex(ele, book);
        book.addSchemeColorIndex(SchemeClrConstant.SCHEME_HLINK, color);
        book.addThemeColorIndex(10, color);

        ele = themeColorElement.element(SchemeClrConstant.SCHEME_FOLHLINK);
        color = getColorIndex(ele, book);
        book.addSchemeColorIndex(SchemeClrConstant.SCHEME_FOLHLINK, color);
        book.addThemeColorIndex(11, color);
    }


    private int getColorIndex(Element colorEle, Workbook book) {

        int color = Color.BLACK;

        if (colorEle.element("srgbClr") != null) {
            color = Integer.parseInt(colorEle.element("srgbClr").attributeValue("val"), 16);

        } else if (colorEle.element("sysClr") != null) {
            color = Integer.parseInt(colorEle.element("sysClr").attributeValue("lastClr"), 16);
        }


        color = color | (0xFF << 24);

        color = book.addColor(color);
        return color;
    }
}

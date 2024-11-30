

package com.document.render.office.fc.hssf.usermodel;

import androidx.annotation.Keep;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;


public class FontDetails {
    private final Map<Character, Integer> charWidths = new HashMap<Character, Integer>();
    private String _fontName;
    private int _height;


    public FontDetails(String fontName, int height) {
        _fontName = fontName;
        _height = height;
    }

    protected static String buildFontHeightProperty(String fontName) {
        return "font." + fontName + ".height";
    }

    protected static String buildFontWidthsProperty(String fontName) {
        return "font." + fontName + ".widths";
    }

    protected static String buildFontCharactersProperty(String fontName) {
        return "font." + fontName + ".characters";
    }


    @Keep
    public static FontDetails create(String fontName, Properties fontMetricsProps) {
        String heightStr = fontMetricsProps.getProperty(buildFontHeightProperty(fontName));
        String widthsStr = fontMetricsProps.getProperty(buildFontWidthsProperty(fontName));
        String charactersStr = fontMetricsProps.getProperty(buildFontCharactersProperty(fontName));


        if (heightStr == null || widthsStr == null || charactersStr == null) {


            throw new IllegalArgumentException("The supplied FontMetrics doesn't know about the font '" + fontName + "', so we can't use it. Please add it to your font metrics file (see StaticFontMetrics.getFontDetails");
        }

        int height = Integer.parseInt(heightStr);
        FontDetails d = new FontDetails(fontName, height);
        String[] charactersStrArray = split(charactersStr, ",", -1);
        String[] widthsStrArray = split(widthsStr, ",", -1);
        if (charactersStrArray.length != widthsStrArray.length)
            throw new RuntimeException("Number of characters does not number of widths for font " + fontName);
        for (int i = 0; i < widthsStrArray.length; i++) {
            if (charactersStrArray[i].length() != 0)
                d.addChar(charactersStrArray[i].charAt(0), Integer.parseInt(widthsStrArray[i]));
        }
        return d;
    }


    private static String[] split(String text, String separator, int max) {
        StringTokenizer tok = new StringTokenizer(text, separator);
        int listSize = tok.countTokens();
        if (max != -1 && listSize > max)
            listSize = max;
        String list[] = new String[listSize];
        for (int i = 0; tok.hasMoreTokens(); i++) {
            if (max != -1 && i == listSize - 1) {
                StringBuffer buf = new StringBuffer((text.length() * (listSize - i)) / listSize);
                while (tok.hasMoreTokens()) {
                    buf.append(tok.nextToken());
                    if (tok.hasMoreTokens())
                        buf.append(separator);
                }
                list[i] = buf.toString().trim();
                break;
            }
            list[i] = tok.nextToken().trim();
        }

        return list;
    }

    public String getFontName() {
        return _fontName;
    }

    public int getHeight() {
        return _height;
    }

    public void addChar(char c, int width) {
        charWidths.put(Character.valueOf(c), Integer.valueOf(width));
    }


    public int getCharWidth(char c) {
        Integer widthInteger = charWidths.get(Character.valueOf(c));
        if (widthInteger == null && c != 'W') {
            return getCharWidth('W');
        }
        return widthInteger.intValue();
    }

    public void addChars(char[] characters, int[] widths) {
        for (int i = 0; i < characters.length; i++) {
            charWidths.put(Character.valueOf(characters[i]), Integer.valueOf(widths[i]));
        }
    }


    public int getStringWidth(String str) {
        int width = 0;
        for (int i = 0; i < str.length(); i++) {
            width += getCharWidth(str.charAt(i));
        }
        return width;
    }
}

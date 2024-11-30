

package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.hssf.record.FontRecord;
import com.document.render.office.fc.hssf.record.PaletteRecord;
import com.document.render.office.fc.ss.usermodel.IFont;



public final class HSSFFont implements IFont {



    public final static String FONT_ARIAL = "Arial";


    private FontRecord font;
    private short index;



    protected HSSFFont(short index, FontRecord rec) {
        font = rec;
        this.index = index;
    }



    public String getFontName() {
        return font.getFontName();
    }



    public void setFontName(String name) {
        font.setFontName(name);
    }



    public short getIndex() {
        return index;
    }



    public short getFontHeight() {
        return font.getFontHeight();
    }



    public void setFontHeight(short height) {
        font.setFontHeight(height);
    }



    public short getFontHeightInPoints() {
        return (short) (font.getFontHeight() / 20);
    }



    public void setFontHeightInPoints(short height) {
        font.setFontHeight((short) (height * 20));
    }



    public boolean getItalic() {
        return font.isItalic();
    }



    public void setItalic(boolean italic) {
        font.setItalic(italic);
    }



    public boolean getStrikeout() {
        return font.isStruckout();
    }



    public void setStrikeout(boolean strikeout) {
        font.setStrikeout(strikeout);
    }


    public short getColor() {
        short index = font.getColorPaletteIndex();
        return (index == 32767 ? PaletteRecord.FIRST_COLOR_INDEX : index);
    }



    public void setColor(short color) {
        font.setColorPaletteIndex(color);
    }



    public short getBoldweight() {
        return font.getBoldWeight();
    }



    public void setBoldweight(short boldweight) {
        font.setBoldWeight(boldweight);
    }



    public short getTypeOffset() {
        return font.getSuperSubScript();
    }



    public void setTypeOffset(short offset) {
        font.setSuperSubScript(offset);
    }



    public byte getUnderline() {
        return font.getUnderline();
    }



    public void setUnderline(byte underline) {
        font.setUnderline(underline);
    }


    public int getCharSet() {
        byte charset = font.getCharset();
        if (charset >= 0) {
            return (int) charset;
        } else {
            return charset + 256;
        }
    }


    public void setCharSet(int charset) {
        byte cs = (byte) charset;
        if (charset > 127) {
            cs = (byte) (charset - 256);
        }
        setCharSet(cs);
    }


    public void setCharSet(byte charset) {
        font.setCharset(charset);
    }

    public String toString() {
        return "org.apache.poi.hssf.usermodel.HSSFFont{" +
                font +
                "}";
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((font == null) ? 0 : font.hashCode());
        result = prime * result + index;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof HSSFFont) {
            final HSSFFont other = (HSSFFont) obj;
            if (font == null) {
                if (other.font != null)
                    return false;
            } else if (!font.equals(other.font))
                return false;
            if (index != other.index)
                return false;
            return true;
        }
        return false;
    }
}

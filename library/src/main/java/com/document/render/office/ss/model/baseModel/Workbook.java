
package com.document.render.office.ss.model.baseModel;

import android.graphics.Color;
import android.os.Message;

import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.common.picture.Picture;
import com.document.render.office.constant.MainConstant;
import com.document.render.office.simpletext.font.Font;
import com.document.render.office.simpletext.model.SectionElement;
import com.document.render.office.ss.model.style.CellStyle;
import com.document.render.office.ss.model.table.TableFormatManager;
import com.document.render.office.system.ReaderHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Workbook {
    public final static int MAXROW_03 = 65536;
    public final static int MAXCOLUMN_03 = 256;

    public final static int MAXROW_07 = 1048576;
    public final static int MAXCOLUMN_07 = 16384;
    protected ReaderHandler readerHandler;

    protected boolean isUsing1904DateWindowing;

    protected Map<Integer, Sheet> sheets;

    protected Map<Integer, Font> fonts;

    protected Map<Integer, Integer> colors;

    protected Map<Integer, CellStyle> cellStyles;

    protected Map<Integer, Object> sharedString;

    private Map<Integer, Picture> pictures;

    private Map<Integer, Integer> themeColor;

    private Map<String, Integer> schemeColor;

    private TableFormatManager tableFormatManager;

    private boolean before07;


    public Workbook(boolean before07) {
        sheets = new HashMap<Integer, Sheet>(5);
        fonts = new HashMap<Integer, Font>(20);
        colors = new HashMap<Integer, Integer>(20);
        cellStyles = new HashMap<Integer, CellStyle>(80);
        sharedString = new HashMap<Integer, Object>(20);
        schemeColor = new HashMap<String, Integer>(20);
        themeColor = new HashMap<Integer, Integer>(20);
        pictures = new HashMap<Integer, Picture>();

        this.before07 = before07;
    }

    public static boolean isValidateStyle(CellStyle cellStyle) {
        if (cellStyle == null) {
            return false;
        }

        if (cellStyle.getBorderLeft() > 0
                || cellStyle.getBorderTop() > 0
                || cellStyle.getBorderRight() > 0
                || cellStyle.getBorderBottom() > 0) {

            return true;
        }


        if (cellStyle.getFillPatternType() != BackgroundAndFill.FILL_NO) {

            return true;
        }

        return false;
    }


    public void addSheet(int index, Sheet sheet) {
        sheets.put(index, sheet);
    }


    public ReaderHandler getReaderHandler() {
        return readerHandler;
    }


    public void setReaderHandler(ReaderHandler readerHandler) {
        this.readerHandler = readerHandler;
    }


    public Sheet getSheet(String sheetName) {
        Collection<Sheet> sheetCol = sheets.values();
        for (Sheet sheet : sheetCol) {
            if (sheet.getSheetName().equals(sheetName)) {
                return sheet;
            }
        }
        return null;
    }


    public int getSheetIndex(Sheet sheet) {
        Iterator<Integer> iter = sheets.keySet().iterator();
        while (iter.hasNext()) {
            int index = iter.next();
            if (sheets.get(index).equals(sheet)) {
                return index;
            }
        }
        return -1;
    }


    public Sheet getSheet(int index) {
        if (index < 0 || index >= sheets.size()) {
            return null;
        }
        return sheets.get(index);
    }


    public int getSheetCount() {
        return sheets.size();
    }


    public void addFont(int index, Font font) {
        fonts.put(index, font);
    }


    public Font getFont(int index) {
        return fonts.get(index);
    }


    public synchronized int addColor(int argb) {
        if (colors.containsValue(argb)) {
            Iterator<Integer> iter = colors.keySet().iterator();
            int index = 0;
            while (iter.hasNext()) {
                index = iter.next();
                if (colors.get(index) == argb) {
                    break;
                }
            }

            return index;
        } else {
            int index = colors.size() - 1;
            while (colors.get(index) != null) {
                index++;
            }
            colors.put(index, argb);
            return index;
        }
    }


    public synchronized void addColor(int index, int rgba) {
        colors.put(index, rgba);
    }


    public int getColor(int index) {
        return getColor(index, false);
    }


    public synchronized int getColor(int index, boolean line) {
        Integer t = colors.get(index);
        if (t == null && (index >= 0 && index <= 7)) {
            t = colors.get(8);
        }

        if (t == null) {
            if (line) {
                return Color.BLACK;
            } else {
                return Color.WHITE;
            }
        }
        return t;
    }


    public void addCellStyle(int index, CellStyle cellStyle) {
        cellStyles.put(index, cellStyle);
    }

    public int getNumStyles() {
        return cellStyles.size();
    }


    public CellStyle getCellStyle(int index) {
        return cellStyles.get(index);
    }


    public int addSharedString(Object item) {
        if (item == null) {
            return -1;
        }















        {

            sharedString.put(sharedString.size(), item);

            return sharedString.size() - 1;
        }
    }


    public void addSharedString(int index, Object item) {
        sharedString.put(index, item);
    }


    public String getSharedString(int index) {
        Object si = sharedString.get(index);
        String value = null;
        if (si instanceof SectionElement) {
            value = ((SectionElement) si).getText(null);
        } else if (si instanceof String) {
            value = (String) si;
        }

        return value;
    }


    public Object getSharedItem(int index) {
        return sharedString.get(index);
    }


    public synchronized void addThemeColorIndex(int index, int colorIndex) {
        themeColor.put(index, colorIndex);
    }


    public synchronized int getThemeColorIndex(int index) {
        Integer t = themeColor.get(index);
        if (t != null) {
            return t;
        } else {
            return -1;
        }
    }


    public synchronized int getThemeColor(int index) {
        Integer t = colors.get(themeColor.get(index));
        if (t != null) {
            return t;
        } else {
            return Color.BLACK;
        }
    }


    public synchronized void addSchemeColorIndex(String key, int colorIndex) {
        schemeColor.put(key, colorIndex);
    }


    public synchronized int getSchemeColorIndex(String key) {
        Integer t = schemeColor.get(key);
        if (t != null) {
            return t;
        } else {
            return -1;
        }
    }


    public synchronized int getSchemeColor(String key) {
        Integer t = colors.get(schemeColor.get(key));
        if (t != null) {
            return t;
        } else {
            return Color.BLACK;
        }
    }


    public boolean isUsing1904DateWindowing() {
        return isUsing1904DateWindowing;
    }


    public void setUsing1904DateWindowing(boolean isUsing1904DateWindowing) {
        this.isUsing1904DateWindowing = isUsing1904DateWindowing;
    }


    public void addPicture(int index, Picture pic) {
        pictures.put(index, pic);
    }


    public int addPicture(Picture pic) {

        Iterator<Integer> iter = pictures.keySet().iterator();
        int index = 0;
        while (iter.hasNext()) {
            index = iter.next();
            if (pictures.get(index).getTempFilePath().equals(pic.getTempFilePath())) {

                return index;
            }
        }

        pictures.put(index + 1, pic);
        return index + 1;
    }


    public Picture getPicture(int index) {
        return pictures.get(index);
    }


    public boolean isBefore07Version() {
        return before07;
    }

    public int getMaxRow() {
        if (before07) {
            return MAXROW_03;
        } else {
            return MAXROW_07;
        }
    }

    public int getMaxColumn() {
        if (before07) {
            return MAXCOLUMN_03;
        } else {
            return MAXCOLUMN_07;
        }
    }

    public TableFormatManager getTableFormatManager() {
        return tableFormatManager;
    }

    public void setTableFormatManager(TableFormatManager tableFormatManager) {
        this.tableFormatManager = tableFormatManager;
    }

    public void destroy() {

        if (readerHandler != null) {
            Message msg = new Message();
            msg.what = MainConstant.HANDLER_MESSAGE_DISPOSE;
            readerHandler.handleMessage(msg);

            readerHandler = null;
        }

        if (sheets != null) {
            Collection<Sheet> sheetCollection = sheets.values();
            for (Sheet sheet : sheetCollection) {
                sheet.dispose();
            }
            sheets.clear();
            sheets = null;
        }

        if (fonts != null) {
            Collection<Font> fontCollection = fonts.values();
            for (Font font : fontCollection) {
                font.dispose();
            }
            fonts.clear();
            fonts = null;
        }

        if (colors != null) {
            colors.clear();
            colors = null;
        }

        if (pictures != null) {
            pictures.clear();
            pictures = null;
        }

        if (cellStyles != null) {
            Collection<CellStyle> styleCollection = cellStyles.values();
            for (CellStyle cellStyle : styleCollection) {
                cellStyle.dispose();
            }
            cellStyles.clear();
            cellStyles = null;
        }

        if (sharedString != null) {
            sharedString.clear();
            sharedString = null;
        }

        if (themeColor != null) {
            themeColor.clear();
            themeColor = null;
        }

        if (schemeColor != null) {
            schemeColor.clear();
            schemeColor = null;
        }
    }


    public void dispose() {

        synchronized (this) {
            destroy();
        }
    }
}

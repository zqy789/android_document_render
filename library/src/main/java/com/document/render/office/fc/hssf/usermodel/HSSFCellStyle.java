


package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.hssf.model.InternalWorkbook;
import com.document.render.office.fc.hssf.record.ExtendedFormatRecord;
import com.document.render.office.fc.hssf.record.FontRecord;
import com.document.render.office.fc.hssf.record.StyleRecord;
import com.document.render.office.fc.hssf.util.HSSFColor;
import com.document.render.office.fc.ss.usermodel.ICellStyle;
import com.document.render.office.fc.ss.usermodel.IFont;



public final class HSSFCellStyle implements ICellStyle {
    private ExtendedFormatRecord _format = null;
    private short _index = 0;
    private InternalWorkbook _workbook = null;



    protected HSSFCellStyle(short index, ExtendedFormatRecord rec, HSSFWorkbook workbook) {
        this(index, rec, workbook.getWorkbook());
    }

    protected HSSFCellStyle(short index, ExtendedFormatRecord rec, InternalWorkbook workbook) {
        _workbook = workbook;
        _index = index;
        _format = rec;
    }


    public short getIndex() {
        return _index;
    }


    public HSSFCellStyle getParentStyle() {
        short parentIndex = _format.getParentIndex();

        if (parentIndex == 0 || parentIndex == 0xFFF) {
            return null;
        }
        return new HSSFCellStyle(
                parentIndex,
                _workbook.getExFormatAt(parentIndex),
                _workbook
        );
    }



    public short getDataFormat() {
        return _format.getFormatIndex();
    }


    public void setDataFormat(short fmt) {
        _format.setFormatIndex(fmt);
    }


    public String getDataFormatString() {
        return getDataFormatString(_workbook);
    }


    public String getDataFormatString(com.document.render.office.fc.ss.usermodel.Workbook workbook) {
        HSSFDataFormat format = new HSSFDataFormat(((HSSFWorkbook) workbook).getWorkbook());

        int idx = getDataFormat();
        return idx == -1 ? "General" : format.getFormat(getDataFormat());
    }


    public String getDataFormatString(com.document.render.office.fc.hssf.model.InternalWorkbook workbook) {



        return HSSFDataFormat.getFormatCode(workbook, _format.getFormatIndex());
    }


    public void setFont(IFont font) {
        setFont((HSSFFont) font);
    }

    public void setFont(HSSFFont font) {
        _format.setIndentNotParentFont(true);
        short fontindex = font.getIndex();
        _format.setFontIndex(fontindex);
    }


    public short getFontIndex() {
        return _format.getFontIndex();
    }


    public HSSFFont getFont(com.document.render.office.fc.ss.usermodel.Workbook parentWorkbook) {
        return ((HSSFWorkbook) parentWorkbook).getFontAt(getFontIndex());
    }


    public boolean getHidden() {
        return _format.isHidden();
    }


    public void setHidden(boolean hidden) {
        _format.setIndentNotParentCellOptions(true);
        _format.setHidden(hidden);
    }


    public boolean getLocked() {
        return _format.isLocked();
    }


    public void setLocked(boolean locked) {
        _format.setIndentNotParentCellOptions(true);
        _format.setLocked(locked);
    }


    public short getAlignment() {
        return _format.getAlignment();
    }


    public void setAlignment(short align) {
        _format.setIndentNotParentAlignment(true);
        _format.setAlignment(align);
    }


    public boolean getWrapText() {
        return _format.getWrapText();
    }


    public void setWrapText(boolean wrapped) {
        _format.setIndentNotParentAlignment(true);
        _format.setWrapText(wrapped);
    }


    public short getVerticalAlignment() {
        return _format.getVerticalAlignment();
    }


    public void setVerticalAlignment(short align) {
        _format.setVerticalAlignment(align);
    }


    public short getRotation() {
        short rotation = _format.getRotation();
        if (rotation == 0xff) {

            return rotation;
        }
        if (rotation > 90) {

            rotation = (short) (90 - rotation);
        }
        return rotation;
    }


    public void setRotation(short rotation) {
        if (rotation == 0xff) {

        } else if ((rotation < 0) && (rotation >= -90)) {


            rotation = (short) (90 - rotation);
        } else if ((rotation < -90) || (rotation > 90)) {

            throw new IllegalArgumentException("The rotation must be between -90 and 90 degrees, or 0xff");
        }
        _format.setRotation(rotation);
    }


    public short getIndention() {
        return _format.getIndent();
    }


    public void setIndention(short indent) {
        _format.setIndent(indent);
    }


    public short getBorderLeft() {
        return _format.getBorderLeft();
    }


    public void setBorderLeft(short border) {
        _format.setIndentNotParentBorder(true);
        _format.setBorderLeft(border);
    }


    public short getBorderRight() {
        return _format.getBorderRight();
    }


    public void setBorderRight(short border) {
        _format.setIndentNotParentBorder(true);
        _format.setBorderRight(border);
    }


    public short getBorderTop() {
        return _format.getBorderTop();
    }


    public void setBorderTop(short border) {
        _format.setIndentNotParentBorder(true);
        _format.setBorderTop(border);
    }


    public short getBorderBottom() {
        return _format.getBorderBottom();
    }


    public void setBorderBottom(short border) {
        _format.setIndentNotParentBorder(true);
        _format.setBorderBottom(border);
    }


    public short getLeftBorderColor() {
        return _format.getLeftBorderPaletteIdx();
    }


    public void setLeftBorderColor(short color) {
        _format.setLeftBorderPaletteIdx(color);
    }


    public short getRightBorderColor() {
        return _format.getRightBorderPaletteIdx();
    }


    public void setRightBorderColor(short color) {
        _format.setRightBorderPaletteIdx(color);
    }


    public short getTopBorderColor() {
        return _format.getTopBorderPaletteIdx();
    }


    public void setTopBorderColor(short color) {
        _format.setTopBorderPaletteIdx(color);
    }


    public short getBottomBorderColor() {
        return _format.getBottomBorderPaletteIdx();
    }


    public void setBottomBorderColor(short color) {
        _format.setBottomBorderPaletteIdx(color);
    }


    public short getFillPattern() {
        return _format.getAdtlFillPattern();
    }


    public void setFillPattern(short fp) {
        _format.setAdtlFillPattern(fp);
    }


    private void checkDefaultBackgroundFills() {
        if (_format.getFillForeground() == com.document.render.office.fc.hssf.util.HSSFColor.AUTOMATIC.index) {



            if (_format.getFillBackground() != (com.document.render.office.fc.hssf.util.HSSFColor.AUTOMATIC.index + 1))
                setFillBackgroundColor((short) (com.document.render.office.fc.hssf.util.HSSFColor.AUTOMATIC.index + 1));
        } else if (_format.getFillBackground() == com.document.render.office.fc.hssf.util.HSSFColor.AUTOMATIC.index + 1)

            if (_format.getFillForeground() != com.document.render.office.fc.hssf.util.HSSFColor.AUTOMATIC.index)
                setFillBackgroundColor(com.document.render.office.fc.hssf.util.HSSFColor.AUTOMATIC.index);
    }


    public short getFillBackgroundColor() {
        short result = _format.getFillBackground();


        if (result == (HSSFColor.AUTOMATIC.index + 1)) {
            return HSSFColor.AUTOMATIC.index;
        }
        return result;
    }


    public void setFillBackgroundColor(short bg) {
        _format.setFillBackground(bg);
        checkDefaultBackgroundFills();
    }

    public HSSFColor getFillBackgroundColorColor() {
        HSSFPalette pallette = new HSSFPalette(
                _workbook.getCustomPalette()
        );
        return pallette.getColor(
                getFillBackgroundColor()
        );
    }


    public short getFillForegroundColor() {
        return _format.getFillForeground();
    }


    public void setFillForegroundColor(short bg) {
        _format.setFillForeground(bg);
        checkDefaultBackgroundFills();
    }

    public HSSFColor getFillForegroundColorColor() {
        HSSFPalette pallette = new HSSFPalette(
                _workbook.getCustomPalette()
        );
        return pallette.getColor(
                getFillForegroundColor()
        );
    }


    public String getUserStyleName() {
        StyleRecord sr = _workbook.getStyleRecord(_index);
        if (sr == null) {
            return null;
        }
        if (sr.isBuiltin()) {
            return null;
        }
        return sr.getName();
    }


    public void setUserStyleName(String styleName) {
        StyleRecord sr = _workbook.getStyleRecord(_index);
        if (sr == null) {
            sr = _workbook.createStyleRecord(_index);
        }


        if (sr.isBuiltin() && _index <= 20) {
            throw new IllegalArgumentException("Unable to set user specified style names for built in styles!");
        }
        sr.setName(styleName);
    }


    public void verifyBelongsToWorkbook(HSSFWorkbook wb) {
        if (wb.getWorkbook() != _workbook) {
            throw new IllegalArgumentException("This Style does not belong to the supplied Workbook. Are you trying to assign a style from one workbook to the cell of a differnt workbook?");
        }
    }


    public void cloneStyleFrom(ICellStyle source) {
        if (source instanceof HSSFCellStyle) {
            this.cloneStyleFrom((HSSFCellStyle) source);
        } else {
            throw new IllegalArgumentException("Can only clone from one HSSFCellStyle to another, not between HSSFCellStyle and XSSFCellStyle");
        }
    }

    public void cloneStyleFrom(HSSFCellStyle source) {


        _format.cloneStyleFrom(source._format);


        if (_workbook != source._workbook) {


            short fmt = (short) _workbook.createFormat(source.getDataFormatString());
            setDataFormat(fmt);



            FontRecord fr = _workbook.createNewFont();
            fr.cloneStyleFrom(
                    source._workbook.getFontRecordAt(
                            source.getFontIndex()
                    )
            );

            HSSFFont font = new HSSFFont(
                    (short) _workbook.getFontIndex(fr), fr
            );
            setFont(font);
        }
    }


    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_format == null) ? 0 : _format.hashCode());
        result = prime * result + _index;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof HSSFCellStyle) {
            final HSSFCellStyle other = (HSSFCellStyle) obj;
            if (_format == null) {
                if (other._format != null)
                    return false;
            } else if (!_format.equals(other._format))
                return false;
            if (_index != other._index)
                return false;
            return true;
        }
        return false;
    }
}

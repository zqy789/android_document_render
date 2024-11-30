

package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.ddf.EscherBSERecord;
import com.document.render.office.fc.ddf.EscherBlipRecord;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.fc.ddf.EscherOptRecord;
import com.document.render.office.fc.hssf.model.InternalWorkbook;
import com.document.render.office.fc.ss.usermodel.Picture;
import com.document.render.office.fc.ss.util.ImageUtils;
import com.document.render.office.java.awt.Dimension;
import com.document.render.office.ss.model.XLSModel.AWorkbook;
import com.document.render.office.ss.model.baseModel.Row;

import java.io.ByteArrayInputStream;



public final class HSSFPicture extends HSSFSimpleShape implements Picture {
    public static final int PICTURE_TYPE_EMF = HSSFWorkbook.PICTURE_TYPE_EMF;
    public static final int PICTURE_TYPE_WMF = HSSFWorkbook.PICTURE_TYPE_WMF;
    public static final int PICTURE_TYPE_PICT = HSSFWorkbook.PICTURE_TYPE_PICT;
    public static final int PICTURE_TYPE_JPEG = HSSFWorkbook.PICTURE_TYPE_JPEG;
    public static final int PICTURE_TYPE_PNG = HSSFWorkbook.PICTURE_TYPE_PNG;
    public static final int PICTURE_TYPE_DIB = HSSFWorkbook.PICTURE_TYPE_DIB;


    private static final float PX_DEFAULT = 32.00f;

    private static final float PX_MODIFIED = 36.56f;


    private static final int PX_ROW = 15;

    private int _pictureIndex;

    private EscherOptRecord opt;


    public HSSFPicture(EscherContainerRecord escherContainer, HSSFShape parent, HSSFAnchor anchor) {
        super(escherContainer, parent, anchor);
        setShapeType(OBJECT_TYPE_PICTURE);
    }


    public HSSFPicture(AWorkbook workbook, EscherContainerRecord escherContainer, HSSFShape parent, HSSFAnchor anchor, EscherOptRecord opt) {
        super(escherContainer, parent, anchor);
        setShapeType(OBJECT_TYPE_PICTURE);
        this.opt = opt;

        processLineWidth();
        processLine(escherContainer, workbook);
        processSimpleBackground(escherContainer, workbook);

        processRotationAndFlip(escherContainer);
    }

    public int getPictureIndex() {
        return _pictureIndex;
    }

    public void setPictureIndex(int pictureIndex) {
        this._pictureIndex = pictureIndex;
    }


    public EscherOptRecord getEscherOptRecord() {
        return opt;
    }


    public void resize(double scale) {
        HSSFClientAnchor anchor = (HSSFClientAnchor) getAnchor();
        anchor.setAnchorType(2);

        HSSFClientAnchor pref = getPreferredSize(scale);

        int row2 = anchor.getRow1() + (pref.getRow2() - pref.getRow1());
        int col2 = anchor.getCol1() + (pref.getCol2() - pref.getCol1());

        anchor.setCol2((short) col2);
        anchor.setDx1(0);
        anchor.setDx2(pref.getDx2());

        anchor.setRow2(row2);
        anchor.setDy1(0);
        anchor.setDy2(pref.getDy2());
    }


    public void resize() {
        resize(1.0);
    }


    public HSSFClientAnchor getPreferredSize() {
        return getPreferredSize(1.0);
    }


    public HSSFClientAnchor getPreferredSize(double scale) {
        HSSFClientAnchor anchor = (HSSFClientAnchor) getAnchor();

        Dimension size = getImageDimension();
        double scaledWidth = size.getWidth() * scale;
        double scaledHeight = size.getHeight() * scale;

        float w = 0;


        w += getColumnWidthInPixels(anchor.col1) * (1 - (float) anchor.dx1 / 1024);
        short col2 = (short) (anchor.col1 + 1);
        int dx2 = 0;

        while (w < scaledWidth) {
            w += getColumnWidthInPixels(col2++);
        }

        if (w > scaledWidth) {

            col2--;
            double cw = getColumnWidthInPixels(col2);
            double delta = w - scaledWidth;
            dx2 = (int) ((cw - delta) / cw * 1024);
        }
        anchor.col2 = col2;
        anchor.dx2 = dx2;

        float h = 0;
        h += (1 - (float) anchor.dy1 / 256) * getRowHeightInPixels(anchor.row1);
        int row2 = anchor.row1 + 1;
        int dy2 = 0;

        while (h < scaledHeight) {
            h += getRowHeightInPixels(row2++);
        }
        if (h > scaledHeight) {
            row2--;
            double ch = getRowHeightInPixels(row2);
            double delta = h - scaledHeight;
            dy2 = (int) ((ch - delta) / ch * 256);
        }
        anchor.row2 = row2;
        anchor.dy2 = dy2;

        return anchor;
    }

    private float getColumnWidthInPixels(int column) {





        if (checkPatriarch()) {
            return _patriarch._sheet.getColumnPixelWidth(column);
        }
        return 0f;
    }

    private float getRowHeightInPixels(int i) {










        if (checkPatriarch()) {
            Row row = _patriarch._sheet.getRow(i);
            if (row != null) {
                return row.getRowPixelHeight();
            }
        }

        {
            return 18;
        }
    }

    private float getPixelWidth(int column) {





        return PX_DEFAULT;
    }


    public Dimension getImageDimension() {
        if (checkPatriarch()) {
            EscherBSERecord bse = _patriarch._sheet.getAWorkbook().getInternalWorkbook().getBSERecord(_pictureIndex);
            byte[] data = bse.getBlipRecord().getPicturedata();
            int type = bse.getBlipTypeWin32();
            return ImageUtils.getImageDimension(new ByteArrayInputStream(data), type);
        }
        return null;
    }


    public HSSFPictureData getPictureData() {
        if (checkPatriarch() && _pictureIndex > 0) {
            InternalWorkbook iwb = _patriarch._sheet.getAWorkbook().getInternalWorkbook();
            EscherBlipRecord blipRecord = iwb.getBSERecord(_pictureIndex).getBlipRecord();
            return new HSSFPictureData(blipRecord);
        }

        return null;
    }
}

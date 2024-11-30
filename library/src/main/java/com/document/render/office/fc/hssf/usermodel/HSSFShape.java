

package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.common.bg.Gradient;
import com.document.render.office.common.bg.LinearGradientShader;
import com.document.render.office.common.bg.RadialGradientShader;
import com.document.render.office.common.bg.TileShader;
import com.document.render.office.common.borders.Line;
import com.document.render.office.common.picture.Picture;
import com.document.render.office.common.shape.Arrow;
import com.document.render.office.common.shape.ShapeTypes;
import com.document.render.office.constant.AutoShapeConstant;
import com.document.render.office.constant.MainConstant;
import com.document.render.office.fc.ShapeKit;
import com.document.render.office.fc.ddf.EscherBSERecord;
import com.document.render.office.fc.ddf.EscherBlipRecord;
import com.document.render.office.fc.ddf.EscherChildAnchorRecord;
import com.document.render.office.fc.ddf.EscherClientAnchorRecord;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.fc.ddf.EscherOptRecord;
import com.document.render.office.fc.ddf.EscherProperties;
import com.document.render.office.fc.ddf.EscherSimpleProperty;
import com.document.render.office.fc.hssf.model.InternalWorkbook;
import com.document.render.office.java.awt.Color;
import com.document.render.office.ss.model.XLSModel.AWorkbook;
import com.document.render.office.system.IControl;


public abstract class HSSFShape {
    final HSSFShape parent;
    protected EscherContainerRecord escherContainer;
    HSSFPatriarch _patriarch;
    private HSSFAnchor anchor;
    private int shapeType = ShapeTypes.NotPrimitive;
    private boolean _noBorder = false;
    private int _lineStyleColor = 0x08000040;
    private int _lineWidth = AutoShapeConstant.LINEWIDTH_DEFAULT;
    private int _lineStyle = AutoShapeConstant.LINESTYLE_SOLID;
    private boolean _noFill = false;
    private int _fillType;
    private int _fillColor = 0x08000009;
    private byte[] bgPictureData;
    private int angle;
    private boolean flipH;
    private boolean flipV;

    private Arrow startArrow;

    private Arrow endArrow;


    HSSFShape(EscherContainerRecord escherContainer, HSSFShape parent, HSSFAnchor anchor) {
        this.escherContainer = escherContainer;
        this.parent = parent;
        this.anchor = anchor;
    }

    public static HSSFClientAnchor toClientAnchor(EscherClientAnchorRecord anchorRecord) {
        HSSFClientAnchor anchor = new HSSFClientAnchor();
        anchor.setAnchorType(anchorRecord.getFlag());
        anchor.setCol1(anchorRecord.getCol1());
        anchor.setCol2(anchorRecord.getCol2());
        anchor.setDx1(anchorRecord.getDx1());
        anchor.setDx2(anchorRecord.getDx2());
        anchor.setDy1(anchorRecord.getDy1());
        anchor.setDy2(anchorRecord.getDy2());
        anchor.setRow1(anchorRecord.getRow1());
        anchor.setRow2(anchorRecord.getRow2());
        return anchor;
    }

    public static HSSFChildAnchor toChildAnchor(EscherChildAnchorRecord anchorRecord) {
        HSSFChildAnchor anchor = new HSSFChildAnchor(anchorRecord.getDx1(),
                anchorRecord.getDy1(),
                anchorRecord.getDx2(),
                anchorRecord.getDy2());

        return anchor;
    }

    public boolean checkPatriarch() {
        HSSFShape topParent = parent;
        while (_patriarch == null && topParent != null) {
            _patriarch = topParent._patriarch;
            topParent = topParent.getParent();
        }

        return _patriarch != null;
    }

    public void processLineWidth() {
        _lineWidth = ShapeKit.getLineWidth(escherContainer);
    }


    public HSSFShape getParent() {
        return parent;
    }


    public HSSFAnchor getAnchor() {
        return anchor;
    }


    public void setAnchor(HSSFAnchor anchor) {
        if (parent == null) {
            if (anchor instanceof HSSFChildAnchor)
                throw new IllegalArgumentException("Must use client anchors for shapes directly attached to sheet.");
        } else {
            if (anchor instanceof HSSFClientAnchor)
                throw new IllegalArgumentException("Must use child anchors for shapes attached to groups.");
        }

        this.anchor = anchor;
    }


    public boolean isNoBorder() {
        return _noBorder;
    }


    public void setNoBorder(boolean noBorder) {
        this._noBorder = noBorder;
    }


    public int getLineStyleColor() {
        return _lineStyleColor;
    }


    public void setLineStyleColor(int lineStyleColor) {
        _lineStyleColor = lineStyleColor;
        _lineStyleColor = (0xFFFFFF & _lineStyleColor) | (255 << 24);
    }


    public void setLineStyleColor(int red, int green, int blue) {
        this._lineStyleColor = ((255 & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF) << 0);
    }


    public int getFillColor() {
        return _fillColor;
    }


    public void setFillColor(int fillColor, int alpha) {
        _fillColor = fillColor;
        _fillColor = (0xFFFFFF & _fillColor) | (alpha << 24);
    }


    public void setFillColor(int red, int green, int blue, int alpha) {
        this._fillColor = ((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF) << 0);
    }


    public int getLineWidth() {
        return _lineWidth;
    }


    public void setLineWidth(int lineWidth) {
        _lineWidth = lineWidth;
    }


    public int getLineStyle() {
        return _lineStyle;
    }


    public void setLineStyle(int lineStyle) {
        _lineStyle = lineStyle;
    }


    public boolean isNoFill() {
        return _noFill;
    }


    public void setNoFill(boolean noFill) {
        _noFill = noFill;
    }


    public int getFillType() {
        return _fillType;
    }


    public void setFillType(int fillType) {
        this._fillType = fillType;
    }

    public byte[] getBGPictureData() {
        return bgPictureData;
    }

    public void setBGPictureData(byte[] bgPictureData) {
        this.bgPictureData = bgPictureData;
    }

    public int getShapeType() {
        return shapeType;
    }

    public void setShapeType(int shapeType) {
        this.shapeType = shapeType;
    }

    public int getRotation() {
        return angle;
    }

    public void setRotation(int angle) {
        this.angle = angle;
    }

    public boolean getFlipH() {
        return flipH;
    }

    public void setFilpH(boolean flipH) {
        this.flipH = flipH;
    }

    public boolean getFlipV() {
        return flipV;
    }

    public void setFlipV(boolean flipV) {
        this.flipV = flipV;
    }

    public void setStartArrow(byte type, int width, int length) {
        startArrow = new Arrow(type, width, length);
    }

    public void setEndArrow(byte type, int width, int length) {
        endArrow = new Arrow(type, width, length);
    }

    public int getStartArrowType() {
        return startArrow.getType();
    }

    public int getStartArrowWidth() {
        return startArrow.getWidth();
    }

    public int getStartArrowLength() {
        return startArrow.getLength();
    }

    public int getEndArrowType() {
        return endArrow.getType();
    }

    public int getEndArrowWidth() {
        return endArrow.getWidth();
    }

    public int getEndArrowLength() {
        return endArrow.getLength();
    }


    public int countOfAllChildren() {
        return 1;
    }

    public void processSimpleBackground(EscherContainerRecord escherContainer, AWorkbook workbook) {
        EscherOptRecord opt = (EscherOptRecord) ShapeKit.getEscherChild(escherContainer,
                EscherOptRecord.RECORD_ID);

        int type = ShapeKit.getFillType(escherContainer);
        if (type == BackgroundAndFill.FILL_PICTURE) {
            EscherSimpleProperty p4 = (EscherSimpleProperty) ShapeKit.getEscherProperty(opt,
                    EscherProperties.FILL__PATTERNTEXTURE);
            if (p4 != null) {
                InternalWorkbook iwb = workbook.getInternalWorkbook();
                EscherBSERecord bseRecord = iwb.getBSERecord(p4.getPropertyValue());
                if (bseRecord != null) {
                    EscherBlipRecord blipRecord = bseRecord.getBlipRecord();
                    if (blipRecord != null) {
                        setFillType(BackgroundAndFill.FILL_PICTURE);
                        setBGPictureData(blipRecord.getPicturedata());
                        return;
                    }
                }
            }
        } else if (type == BackgroundAndFill.FILL_PATTERN) {
            Color color = ShapeKit.getFillbackColor(escherContainer, workbook, MainConstant.APPLICATION_TYPE_SS);
            if (color != null) {
                setFillType(BackgroundAndFill.FILL_SOLID);
                setFillColor(color.getRGB(), 255);
                return;
            }
        } else if (isGradientTile()) {
            setFillType(type);
            return;
        } else {
            Color color = ShapeKit.getForegroundColor(escherContainer, workbook, MainConstant.APPLICATION_TYPE_SS);
            if (color != null) {
                setFillType(BackgroundAndFill.FILL_SOLID);
                setFillColor(color.getRGB(), 255);
                return;
            }
        }
        setNoFill(true);
    }

    public boolean isGradientTile() {
        int type = ShapeKit.getFillType(escherContainer);
        return type == BackgroundAndFill.FILL_SHADE_LINEAR
                || type == BackgroundAndFill.FILL_SHADE_RADIAL
                || type == BackgroundAndFill.FILL_SHADE_RECT
                || type == BackgroundAndFill.FILL_SHADE_SHAPE
                || type == BackgroundAndFill.FILL_SHADE_TILE;
    }

    public BackgroundAndFill getGradientTileBackground(AWorkbook workbook, IControl control) {
        BackgroundAndFill bgFill = null;

        int type = getFillType();
        if (type == BackgroundAndFill.FILL_SHADE_LINEAR
                || type == BackgroundAndFill.FILL_SHADE_RADIAL
                || type == BackgroundAndFill.FILL_SHADE_RECT
                || type == BackgroundAndFill.FILL_SHADE_SHAPE) {
            bgFill = new BackgroundAndFill();

            int angle = ShapeKit.getFillAngle(escherContainer);
            switch (angle) {
                case -90:
                case 0:
                    angle += 90;
                    break;
                case -45:
                    angle = 135;
                    break;
                case -135:
                    angle = 45;
                    break;
            }

            int focus = ShapeKit.getFillFocus(escherContainer);
            com.document.render.office.java.awt.Color fillColor =
                    ShapeKit.getForegroundColor(escherContainer, workbook, MainConstant.APPLICATION_TYPE_SS);
            com.document.render.office.java.awt.Color fillbackColor =
                    ShapeKit.getFillbackColor(escherContainer, workbook, MainConstant.APPLICATION_TYPE_SS);

            int[] colors = null;
            float[] positions = null;
            if (ShapeKit.isShaderPreset(escherContainer)) {
                colors = ShapeKit.getShaderColors(escherContainer);
                positions = ShapeKit.getShaderPositions(escherContainer);
            }

            if (colors == null) {
                colors = new int[]{fillColor == null ? 0xFFFFFFFF : fillColor.getRGB(),
                        fillbackColor == null ? 0xFFFFFFFF : fillbackColor.getRGB()};
            }
            if (positions == null) {
                positions = new float[]{0f, 1f};
            }

            Gradient gradient = null;
            if (type == BackgroundAndFill.FILL_SHADE_LINEAR) {
                gradient = new LinearGradientShader(angle, colors, positions);
            } else if (type == BackgroundAndFill.FILL_SHADE_RADIAL
                    || type == BackgroundAndFill.FILL_SHADE_RECT
                    || type == BackgroundAndFill.FILL_SHADE_SHAPE) {
                gradient =
                        new RadialGradientShader(ShapeKit.getRadialGradientPositionType(escherContainer), colors, positions);
            }

            if (gradient != null) {
                gradient.setFocus(focus);
            }

            bgFill.setFillType((byte) type);
            bgFill.setShader(gradient);
        } else if (type == BackgroundAndFill.FILL_SHADE_TILE) {
            bgFill = new BackgroundAndFill();

            EscherOptRecord opt = (EscherOptRecord) ShapeKit.getEscherChild(escherContainer,
                    EscherOptRecord.RECORD_ID);

            EscherSimpleProperty p4 = (EscherSimpleProperty) ShapeKit.getEscherProperty(opt,
                    EscherProperties.FILL__PATTERNTEXTURE);
            if (p4 != null) {
                InternalWorkbook iwb = workbook.getInternalWorkbook();
                EscherBSERecord bseRecord = iwb.getBSERecord(p4.getPropertyValue());
                if (bseRecord != null) {
                    EscherBlipRecord blipRecord = bseRecord.getBlipRecord();
                    if (blipRecord != null) {
                        bgFill.setFillType(BackgroundAndFill.FILL_SHADE_TILE);
                        Picture pic = new Picture();
                        pic.setData(blipRecord.getPicturedata());

                        control.getSysKit().getPictureManage().addPicture(pic);

                        bgFill.setShader(
                                new TileShader(pic, TileShader.Flip_None, 1f, 1.0f));
                    }
                }
            }
        }

        return bgFill;
    }


    public Line getLine() {
        BackgroundAndFill lineFill = new BackgroundAndFill();
        lineFill.setForegroundColor(_lineStyleColor);

        Line line = new Line();
        line.setBackgroundAndFill(lineFill);
        line.setLineWidth(_lineWidth);
        line.setDash(_lineStyle > AutoShapeConstant.LINESTYLE_SOLID);
        return line;
    }
}

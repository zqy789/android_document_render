
package com.document.render.office.ss.model.XLSModel;

import android.graphics.Path;
import android.graphics.PointF;

import com.document.render.office.common.autoshape.ExtendPath;
import com.document.render.office.common.autoshape.pathbuilder.ArrowPathAndTail;
import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.common.borders.Line;
import com.document.render.office.common.hyperlink.Hyperlink;
import com.document.render.office.common.picture.Picture;
import com.document.render.office.common.pictureefftect.PictureEffectInfoFactory;
import com.document.render.office.common.shape.AChart;
import com.document.render.office.common.shape.ArbitraryPolygonShape;
import com.document.render.office.common.shape.Arrow;
import com.document.render.office.common.shape.AutoShape;
import com.document.render.office.common.shape.GroupShape;
import com.document.render.office.common.shape.IShape;
import com.document.render.office.common.shape.LineShape;
import com.document.render.office.common.shape.PictureShape;
import com.document.render.office.common.shape.ShapeTypes;
import com.document.render.office.common.shape.TextBox;
import com.document.render.office.constant.MainConstant;
import com.document.render.office.constant.SSConstant;
import com.document.render.office.fc.hssf.model.InternalSheet;
import com.document.render.office.fc.hssf.model.InternalWorkbook;
import com.document.render.office.fc.hssf.record.BlankRecord;
import com.document.render.office.fc.hssf.record.CellValueRecordInterface;
import com.document.render.office.fc.hssf.record.DefaultRowHeightRecord;
import com.document.render.office.fc.hssf.record.EscherAggregate;
import com.document.render.office.fc.hssf.record.HyperlinkRecord;
import com.document.render.office.fc.hssf.record.RecordBase;
import com.document.render.office.fc.hssf.record.RowRecord;
import com.document.render.office.fc.hssf.usermodel.HSSFAutoShape;
import com.document.render.office.fc.hssf.usermodel.HSSFChart;
import com.document.render.office.fc.hssf.usermodel.HSSFChildAnchor;
import com.document.render.office.fc.hssf.usermodel.HSSFClientAnchor;
import com.document.render.office.fc.hssf.usermodel.HSSFFreeform;
import com.document.render.office.fc.hssf.usermodel.HSSFLine;
import com.document.render.office.fc.hssf.usermodel.HSSFPatriarch;
import com.document.render.office.fc.hssf.usermodel.HSSFPicture;
import com.document.render.office.fc.hssf.usermodel.HSSFPictureData;
import com.document.render.office.fc.hssf.usermodel.HSSFRichTextString;
import com.document.render.office.fc.hssf.usermodel.HSSFShape;
import com.document.render.office.fc.hssf.usermodel.HSSFShapeGroup;
import com.document.render.office.fc.hssf.usermodel.HSSFSheet;
import com.document.render.office.fc.hssf.usermodel.HSSFTextbox;
import com.document.render.office.fc.hssf.usermodel.HSSFWorkbook;
import com.document.render.office.fc.hssf.util.HSSFPaneInformation;
import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;
import com.document.render.office.fc.xls.ChartConverter;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.ss.model.CellRangeAddress;
import com.document.render.office.ss.model.baseModel.Cell;
import com.document.render.office.ss.model.baseModel.Row;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.model.drawing.AnchorPoint;
import com.document.render.office.ss.model.drawing.CellAnchor;
import com.document.render.office.ss.model.sheetProperty.PaneInformation;
import com.document.render.office.ss.util.ModelUtil;
import com.document.render.office.ss.util.SectionElementFactory;
import com.document.render.office.system.AbortReaderError;
import com.document.render.office.system.AbstractReader;
import com.document.render.office.system.IControl;
import com.document.render.office.thirdpart.achartengine.chart.AbstractChart;
import com.document.render.office.thirdpart.achartengine.chart.RoundChart;
import com.document.render.office.thirdpart.achartengine.chart.XYChart;
import com.document.render.office.thirdpart.achartengine.renderers.DefaultRenderer;

import java.util.Iterator;
import java.util.List;


public class ASheet extends Sheet implements com.document.render.office.fc.ss.usermodel.Sheet {

    private InternalSheet sheet;
    private boolean initRowFinished;


    protected ASheet(AWorkbook workbook, InternalSheet sheet) {
        super();
        this.sheet = sheet;
        book = workbook;


        int count = sheet.getNumMergedRegions();
        for (int i = 0; i < count; i++) {
            HSSFCellRangeAddress range = sheet.getMergedRegionAt(i);
            addMergeRange(new CellRangeAddress(range.getFirstRow(),
                    range.getFirstColumn(), range.getLastRow(), range.getLastColumn()));
        }


        HSSFPaneInformation pane = sheet.getPaneInformation();
        if (pane != null) {
            setPaneInformation(new PaneInformation(pane.getHorizontalSplitTopRow(),
                    pane.getVerticalSplitLeftColumn(), pane.isFreezePane()));
        }



        List<com.document.render.office.fc.hssf.util.ColumnInfo> hssfColumnInfoList = sheet.getColumnInfo();
        if (hssfColumnInfoList != null) {
            Iterator<com.document.render.office.fc.hssf.util.ColumnInfo> iter = hssfColumnInfoList.iterator();
            while (iter.hasNext()) {
                com.document.render.office.fc.hssf.util.ColumnInfo hssfColumnInfo = iter.next();
                com.document.render.office.ss.model.sheetProperty.ColumnInfo columnInfo = new com.document.render.office.ss.model.sheetProperty.ColumnInfo(
                        hssfColumnInfo.getFirstCol(),
                        hssfColumnInfo.getLastCol(),
                        (int) (hssfColumnInfo.getColWidth() / 256.0 * SSConstant.COLUMN_CHAR_WIDTH * MainConstant.POINT_TO_PIXEL),
                        hssfColumnInfo.getStyle(),
                        hssfColumnInfo.isHidden());

                addColumnInfo(columnInfo);
            }
        }
    }

    public void processSheet(AbstractReader iAbortListener) {
        if (getSheetType() != Sheet.TYPE_CHARTSHEET && !initRowFinished) {
            processRowsAndCells(sheet, iAbortListener);


            processMergedCells();


            processHyperlinkfromSheet(sheet);
            initRowFinished = true;
        }

    }


    private void processHyperlinkfromSheet(InternalSheet sheet) {

        try {
            for (Iterator<RecordBase> it = sheet.getRecords().iterator(); it.hasNext(); ) {
                RecordBase rec = it.next();
                if (rec instanceof HyperlinkRecord) {
                    HyperlinkRecord linkRec = (HyperlinkRecord) rec;

                    Hyperlink link = new Hyperlink();

                    if (linkRec.isFileLink()) {
                        link.setLinkType(Hyperlink.LINK_FILE);
                        ;
                    } else if (linkRec.isDocumentLink()) {
                        link.setLinkType(Hyperlink.LINK_DOCUMENT);
                    } else {
                        if (linkRec.getAddress() != null &&
                                linkRec.getAddress().startsWith("mailto:")) {
                            link.setLinkType(Hyperlink.LINK_EMAIL);
                        } else {
                            link.setLinkType(Hyperlink.LINK_URL);
                        }
                    }


                    link.setAddress(linkRec.getAddress());
                    link.setTitle(linkRec.getLabel());
                    Row row = getRow(linkRec.getFirstRow());
                    if (row == null) {
                        RowRecord rowRec = new RowRecord(linkRec.getFirstRow());
                        row = new ARow(book, this, rowRec);
                        row.setRowPixelHeight(18);

                        rows.put(linkRec.getFirstRow(), row);
                    }

                    Cell cell = row.getCell(linkRec.getFirstColumn());
                    if (cell == null) {
                        BlankRecord brec = new BlankRecord();
                        brec.setRow(linkRec.getFirstRow());
                        brec.setColumn((short) linkRec.getFirstColumn());
                        brec.setXFIndex((short) row.getRowStyle());
                        cell = new ACell(this, brec);

                        row.addCell(cell);
                    }
                    cell.setHyperLink(link);
                }
            }
        } catch (Exception e) {
        }
    }


    private void processRowsAndCells(InternalSheet sheet, AbstractReader iAbortListener) {
        RowRecord row = sheet.getNextRow();
        boolean rowRecordsAlreadyPresent = row != null;


        while (row != null) {
            if (iAbortListener.isAborted()) {
                throw new AbortReaderError("abort Reader");
            }

            createValidateRowFromRecord(row);

            row = sheet.getNextRow();
        }


        Iterator<CellValueRecordInterface> iter = sheet.getCellValueIterator();
        ARow lastrow = null;

        while (iter.hasNext()) {
            if (iAbortListener.isAborted()) {
                throw new AbortReaderError("abort Reader");
            }

            CellValueRecordInterface cval = iter.next();
            iter.remove();

            ARow hrow = lastrow;
            if (hrow == null || hrow.getRowNumber() != cval.getRow()) {
                if (lastrow != null) {
                    lastrow.completed();
                }

                hrow = (ARow) getRow(cval.getRow());
                lastrow = hrow;
                if (hrow == null) {









                    RowRecord rowRec = new RowRecord(cval.getRow());

                    hrow = createRowFromRecord(rowRec);
                }
            }

            hrow.createCellFromRecord(cval);
        }

        if (lastrow != null) {
            lastrow.completed();
        }
    }

    private boolean isValidateRow(RowRecord row) {
        if (row.getFirstCol() != row.getLastCol() || row.getHeight() != DefaultRowHeightRecord.DEFAULT_ROW_HEIGHT) {
            return true;
        } else {
            int styleIndex = row.getXFIndex();
            if (styleIndex > book.getNumStyles()) {
                styleIndex &= 0xFF;
            }

            if (Workbook.isValidateStyle(book.getCellStyle(styleIndex))) {
                return true;
            }
        }

        return false;
    }


    private ARow createValidateRowFromRecord(RowRecord rowRec) {
        Row row = getRow(rowRec.getRowNumber());
        if (row != null) {
            return (ARow) row;
        }

        if (isValidateRow(rowRec)) {
            ARow hrow = new ARow(book, this, rowRec);

            addRow(hrow);
            return hrow;
        } else {
            return null;
        }

    }


    private ARow createRowFromRecord(RowRecord rowRec) {
        Row row = getRow(rowRec.getRowNumber());
        if (row != null) {
            return (ARow) row;
        }

        ARow hrow = new ARow(book, this, rowRec);
        addRow(hrow);

        return hrow;

    }


    private void processMergedCells() {
        Row row;
        Cell cell;
        int count = getMergeRangeCount();
        for (int i = 0; i < count; i++) {
            CellRangeAddress cr = getMergeRange(i);

            if (cr.getLastRow() - cr.getFirstRow() == Workbook.MAXROW_03 - 1
                    || cr.getLastColumn() - cr.getFirstColumn() == Workbook.MAXCOLUMN_03 - 1) {
                continue;
            }

            for (int j = cr.getFirstRow(); j <= cr.getLastRow(); j++) {
                row = getRow(j);
                if (row == null) {
                    RowRecord rowRec = new RowRecord(j);
                    row = new ARow(book, this, rowRec);
                    row.setRowPixelHeight(18);

                    addRow(row);
                }

                for (int k = cr.getFirstColumn(); k <= cr.getLastColumn(); k++) {
                    cell = row.getCell(k);
                    if (cell == null) {
                        BlankRecord brec = new BlankRecord();
                        brec.setRow(j);
                        brec.setColumn((short) k);
                        brec.setXFIndex((short) row.getRowStyle());
                        cell = new ACell(this, brec);
                        row.addCell(cell);
                    }

                    cell.setRangeAddressIndex(i);
                }
            }

        }
    }


    public EscherAggregate getDrawingEscherAggregate(InternalSheet sheet) {
        InternalWorkbook internalWorkbook = ((AWorkbook) book).getInternalWorkbook();
        internalWorkbook.findDrawingGroup();



        if (internalWorkbook.getDrawingManager() == null) {
            return null;
        }

        int found = sheet.aggregateDrawingRecords(internalWorkbook.getDrawingManager(), false);
        if (found == -1) {

            return null;
        }


        EscherAggregate agg = (EscherAggregate) sheet.findFirstRecordBySid(EscherAggregate.sid);
        return agg;
    }


    public HSSFPatriarch getDrawingPatriarch(InternalSheet sheet) {
        EscherAggregate agg = getDrawingEscherAggregate(sheet);
        if (agg == null)
            return null;

        HSSFPatriarch patriarch = new HSSFPatriarch(this, agg);
        agg.setPatriarch(patriarch);




        agg.convertRecordsToUserModel(getAWorkbook());


        return patriarch;
    }


    public Iterator<Row> rowIterator() {
        @SuppressWarnings("unchecked")

        Iterator<Row> result = (Iterator<Row>) (Iterator<? extends Row>) rows.values().iterator();
        return result;
    }


    private BackgroundAndFill converFill(HSSFShape shape, IControl control) {
        BackgroundAndFill bgFill = null;
        if (shape != null) {
            if (shape.isGradientTile()) {
                return shape.getGradientTileBackground((AWorkbook) book, control);
            }

            int type = shape.getFillType();
            if (type == BackgroundAndFill.FILL_PICTURE) {
                byte[] picData = shape.getBGPictureData();
                if (picData != null) {
                    Picture pic = new Picture();
                    pic.setData(picData);

                    int picIndex = control.getSysKit().getPictureManage().addPicture(pic);
                    bgFill = new BackgroundAndFill();
                    bgFill.setFillType(BackgroundAndFill.FILL_PICTURE);
                    bgFill.setPictureIndex(picIndex);
                }
            } else {
                bgFill = new BackgroundAndFill();
                bgFill.setFillType(BackgroundAndFill.FILL_SOLID);
                bgFill.setForegroundColor(shape.getFillColor());
            }
        }
        return bgFill;
    }

    public void processRotationAndFlip(HSSFShape shape, IShape autoShape) {
        float angle = shape.getRotation();
        if (shape.getFlipH()) {
            autoShape.setFlipHorizontal(true);
            angle = -angle;
        }
        if (shape.getFlipV()) {
            autoShape.setFlipVertical(true);
            angle = -angle;
        }

        if (autoShape instanceof LineShape) {
            if ((angle == 45 || angle == 135 || angle == 225)
                    && !autoShape.getFlipHorizontal()
                    && !autoShape.getFlipVertical()) {
                angle -= 90;
            }
        }
        autoShape.setRotation(angle);
    }


    public void processSheetShapes(IControl control) {
        int type = getSheetType();
        if (type == Sheet.TYPE_WORKSHEET) {
            HSSFPatriarch patriarch = getDrawingPatriarch(sheet);
            if (patriarch != null) {
                List<HSSFShape> shapeList = patriarch.getChildren();

                for (HSSFShape shape : shapeList) {
                    if (((AWorkbook) book).getAbstractReader().isAborted()) {
                        throw new AbortReaderError("abort Reader");
                    }

                    processShape(control, null, null, shape, null);
                }
                patriarch.dispose();
                patriarch = null;
            }
            sheet = null;
        } else if (type == Sheet.TYPE_CHARTSHEET) {
            if (((AWorkbook) book).getAbstractReader().isAborted()) {
                throw new AbortReaderError("abort Reader");
            }

            HSSFChart chart = sheet.getChart();
            AChart achart = new AChart();
            AbstractChart abstractChart = ChartConverter.instance().converter(this, chart);
            if (abstractChart != null) {
                DefaultRenderer renderer = null;
                if (abstractChart instanceof XYChart) {
                    renderer = ((XYChart) abstractChart).getRenderer();
                } else if (abstractChart instanceof RoundChart) {
                    renderer = ((RoundChart) abstractChart).getRenderer();
                }

                if (renderer != null) {
                    if (!chart.isNoBorder()) {
                        renderer.setChartFrame(chart.getLine());
                    }




                }

                achart.setAChart(abstractChart);
                shapesList.add(achart);
            }
        }
    }


    private CellAnchor ClientAnchorToTwoCellAnchor(HSSFClientAnchor anchor) {
        AnchorPoint from = new AnchorPoint();
        AnchorPoint end = new AnchorPoint();

        from.setColumn(anchor.getCol1());
        from.setRow(anchor.getRow1());

        end.setRow(anchor.getRow2());
        end.setColumn(anchor.getCol2());

        float colWidth = getColumnPixelWidth(anchor.getCol1());
        from.setDX(Math.round(anchor.getDx1() / 1024f * colWidth));

        colWidth = getColumnPixelWidth(anchor.getCol2());
        end.setDX(Math.round(anchor.getDx2() / 1024f * colWidth));


        Row row = getRow(anchor.getRow1());
        float rowHeight = row == null ? getDefaultRowHeight() : row.getRowPixelHeight();
        from.setDY(Math.round(anchor.getDy1() / 256f * rowHeight));

        row = getRow(anchor.getRow2());
        rowHeight = row == null ? getDefaultRowHeight() : row.getRowPixelHeight();
        end.setDY(Math.round(anchor.getDy2() / 256f * rowHeight));


        CellAnchor cellAnchor = new CellAnchor(CellAnchor.TWOCELLANCHOR);

        cellAnchor.setStart(from);
        cellAnchor.setEnd(end);

        return cellAnchor;
    }

    private void processShape(IControl control, GroupShape parent, HSSFShapeGroup hssfParent, HSSFShape shape, Rectangle parentRect) {
        Rectangle rect = null;
        if (getSheetType() == Sheet.TYPE_WORKSHEET) {
            if (parent == null) {
                HSSFClientAnchor anchor = (HSSFClientAnchor) shape.getAnchor();
                if (anchor == null) {
                    return;
                }
                rect = ModelUtil.instance().getCellAnchor(this, ClientAnchorToTwoCellAnchor(anchor));
                if (rect != null) {
                    rect = ModelUtil.processRect(rect, shape.getRotation());
                }
            } else {

                HSSFChildAnchor anchor = (HSSFChildAnchor) shape.getAnchor();
                if (anchor == null) {
                    return;
                }
                rect = new Rectangle();
                rect.x = parentRect.x + Math.round((anchor.getDx1() - hssfParent.getX1()) / (float) (hssfParent.getX2() - hssfParent.getX1()) * parentRect.width);
                rect.y = parentRect.y + Math.round((anchor.getDy1() - hssfParent.getY1()) / (float) (hssfParent.getY2() - hssfParent.getY1()) * parentRect.height);
                rect.width = Math.round((anchor.getDx2() - anchor.getDx1()) / (float) (hssfParent.getX2() - hssfParent.getX1()) * parentRect.width);
                rect.height = Math.round((anchor.getDy2() - anchor.getDy1()) / (float) (hssfParent.getY2() - hssfParent.getY1()) * parentRect.height);

                rect = ModelUtil.processRect(rect, shape.getRotation());
            }

            int type = shape.getShapeType();
            if (type != ShapeTypes.Line && type != ShapeTypes.StraightConnector1 && (rect.width == 0 || rect.height == 0)) {
                return;
            }
        }

        if (shape instanceof HSSFShapeGroup) {
            GroupShape groupShape = new GroupShape();
            groupShape.setBounds(rect);
            List<HSSFShape> shapes = ((HSSFShapeGroup) shape).getChildren();
            for (HSSFShape item : shapes) {
                processShape(control, groupShape, (HSSFShapeGroup) shape, item, rect);
            }

            if (parent == null) {
                shapesList.add(groupShape);
            } else {
                parent.appendShapes(groupShape);
            }
        } else {
            processSingleShape(control, parent, shape, rect);
        }
    }

    private void processSingleShape(IControl control, GroupShape parent, HSSFShape shape, Rectangle rect) {
        if (shape instanceof HSSFPicture) {
            HSSFPicture picture = (HSSFPicture) shape;

            HSSFPictureData picData = picture.getPictureData();
            if (picData != null) {
                byte[] data = picData.getData();
                if (data != null) {
                    Picture pic = new Picture();
                    pic.setData(data);
                    byte type = Picture.PNG;
                    switch (picData.getFormat()) {
                        case HSSFWorkbook.PICTURE_TYPE_EMF:
                            type = Picture.EMF;
                            break;

                        case HSSFWorkbook.PICTURE_TYPE_WMF:
                            type = Picture.WMF;
                            break;
                    }
                    pic.setPictureType(type);

                    int picIndex = control.getSysKit().getPictureManage().addPicture(pic);
                    PictureShape picShape = new PictureShape();
                    picShape.setPictureIndex(picIndex);
                    picShape.setBounds(rect);
                    picShape.setPictureEffectInfor(PictureEffectInfoFactory.getPictureEffectInfor(picture.getEscherOptRecord()));
                    processRotationAndFlip(shape, picShape);


                    if (!shape.isNoBorder()) {
                        picShape.setLine(shape.getLine());
                    }
                    if (!shape.isNoFill()) {
                        picShape.setBackgroundAndFill(converFill(shape, control));
                    }

                    if (parent == null) {
                        shapesList.add(picShape);
                    } else {
                        parent.appendShapes(picShape);
                    }
                }
            } else if (!shape.isNoBorder() || !shape.isNoFill()) {
                AutoShape autoShape = new AutoShape(ShapeTypes.Rectangle);
                autoShape.setAuotShape07(false);
                autoShape.setBounds(rect);

                if (!shape.isNoBorder()) {
                    autoShape.setLine(shape.getLine());
                }
                if (!shape.isNoFill()) {
                    autoShape.setBackgroundAndFill(converFill(shape, control));
                }

                processRotationAndFlip(shape, autoShape);
                if (parent == null) {
                    shapesList.add(autoShape);
                } else {
                    parent.appendShapes(autoShape);
                }
            }
        } else if (shape instanceof HSSFChart) {
            HSSFChart chart = (HSSFChart) shape;

            {
                AChart achart = new AChart();
                achart.setBounds(rect);
                AbstractChart abstractChart = ChartConverter.instance().converter(this, chart);
                if (abstractChart != null) {
                    DefaultRenderer renderer = null;
                    if (abstractChart instanceof XYChart) {
                        renderer = ((XYChart) abstractChart).getRenderer();
                    } else if (abstractChart instanceof RoundChart) {
                        renderer = ((RoundChart) abstractChart).getRenderer();
                    }

                    if (renderer != null) {
                        if (!chart.isNoBorder()) {
                            renderer.setChartFrame(chart.getLine());
                        }
                        if (!chart.isNoFill()) {
                            renderer.setBackgroundAndFill(converFill(chart, control));
                        }
                    }

                    achart.setAChart(abstractChart);
                    if (parent == null) {
                        shapesList.add(achart);
                    } else {
                        parent.appendShapes(achart);
                    }
                }
            }
        } else if (shape instanceof HSSFLine) {
            if (!shape.isNoBorder()) {
                LineShape lineShape = new LineShape();
                lineShape.setAuotShape07(false);
                lineShape.setShapeType(shape.getShapeType());
                lineShape.setBounds(rect);
                lineShape.setLine(shape.getLine());

                Float[] adj = ((HSSFLine) shape).getAdjustmentValue();
                if (lineShape.getShapeType() == ShapeTypes.BentConnector2 && adj == null) {
                    lineShape.setAdjustData(new Float[]{1.0f});
                } else {
                    lineShape.setAdjustData(adj);
                }

                if (((HSSFLine) shape).getStartArrowType() > 0) {
                    lineShape.createStartArrow((byte) shape.getStartArrowType(),
                            shape.getStartArrowWidth(),
                            shape.getStartArrowLength());
                }
                if (((HSSFLine) shape).getEndArrowType() > 0) {
                    lineShape.createEndArrow((byte) shape.getEndArrowType(),
                            shape.getEndArrowWidth(),
                            shape.getEndArrowLength());
                }
                processRotationAndFlip(shape, lineShape);
                if (parent == null) {
                    shapesList.add(lineShape);
                } else {
                    parent.appendShapes(lineShape);
                }
            }
        } else if (shape instanceof HSSFFreeform) {
            if (!shape.isNoBorder() || !shape.isNoFill()) {
                ArbitraryPolygonShape arbitraryPolygonShape = new ArbitraryPolygonShape();
                arbitraryPolygonShape.setShapeType(ShapeTypes.ArbitraryPolygon);
                arbitraryPolygonShape.setBounds(rect);
                Line line = shape.getLine();


                PointF startArrowTailCenter = null;
                PointF endArrowTailCenter = null;

                int startArrowType = ((HSSFFreeform) shape).getStartArrowType();
                if (startArrowType > 0) {
                    ArrowPathAndTail arrowPathAndTail = ((HSSFFreeform) shape).getStartArrowPath(rect);
                    if (arrowPathAndTail != null && arrowPathAndTail.getArrowPath() != null) {
                        startArrowTailCenter = arrowPathAndTail.getArrowTailCenter();
                        ExtendPath pathExtend = new ExtendPath();
                        pathExtend.setPath(arrowPathAndTail.getArrowPath());
                        pathExtend.setArrowFlag(true);
                        if (startArrowType != Arrow.Arrow_Arrow) {
                            BackgroundAndFill fill = null;
                            if (shape.isNoFill()) {
                                fill = new BackgroundAndFill();
                                fill.setFillType(BackgroundAndFill.FILL_SOLID);
                                fill.setForegroundColor(shape.getLineStyleColor());
                            } else if (line != null) {
                                fill = line.getBackgroundAndFill();
                            }
                            pathExtend.setBackgroundAndFill(fill);
                        } else {
                            pathExtend.setLine(line);
                        }

                        arbitraryPolygonShape.appendPath(pathExtend);
                    }

                }

                int endArrowType = ((HSSFFreeform) shape).getEndArrowType();
                if (endArrowType > 0) {
                    ArrowPathAndTail arrowPathAndTail = ((HSSFFreeform) shape).getEndArrowPath(rect);
                    if (arrowPathAndTail != null && arrowPathAndTail.getArrowPath() != null) {
                        endArrowTailCenter = arrowPathAndTail.getArrowTailCenter();
                        ExtendPath pathExtend = new ExtendPath();
                        pathExtend.setPath(arrowPathAndTail.getArrowPath());
                        pathExtend.setArrowFlag(true);
                        if (endArrowType != Arrow.Arrow_Arrow) {
                            BackgroundAndFill fill = null;
                            if (shape.isNoFill()) {
                                fill = new BackgroundAndFill();
                                fill.setFillType(BackgroundAndFill.FILL_SOLID);
                                fill.setForegroundColor(shape.getLineStyleColor());
                            } else if (line != null) {
                                fill = line.getBackgroundAndFill();
                            }
                            pathExtend.setBackgroundAndFill(fill);
                        } else {
                            pathExtend.setLine(line);
                        }

                        arbitraryPolygonShape.appendPath(pathExtend);
                    }
                }

                Path[] paths = ((HSSFFreeform) shape).getFreeformPath(rect, startArrowTailCenter, (byte) startArrowType, endArrowTailCenter, (byte) endArrowType);
                for (int i = 0; i < paths.length; i++) {
                    ExtendPath pathExtend = new ExtendPath();
                    pathExtend.setPath(paths[i]);
                    if (!shape.isNoBorder()) {
                        pathExtend.setLine(line);
                    }
                    if (!shape.isNoFill()) {
                        pathExtend.setBackgroundAndFill(converFill(shape, control));
                    }
                    arbitraryPolygonShape.appendPath(pathExtend);
                }

                processRotationAndFlip(shape, arbitraryPolygonShape);
                if (parent == null) {
                    shapesList.add(arbitraryPolygonShape);
                } else {
                    parent.appendShapes(arbitraryPolygonShape);
                }
            }
        } else if (shape instanceof HSSFAutoShape) {
            AutoShape autoShape = null;
            if (!shape.isNoBorder() || !shape.isNoFill()) {
                autoShape = new AutoShape(shape.getShapeType());
                autoShape.setAuotShape07(false);
                autoShape.setBounds(rect);

                if (!shape.isNoBorder()) {
                    autoShape.setLine(shape.getLine());
                }
                if (!shape.isNoFill()) {
                    autoShape.setBackgroundAndFill(converFill(shape, control));
                }

                if (shape.getShapeType() != ShapeTypes.TextBox) {
                    autoShape.setAdjustData(((HSSFAutoShape) shape).getAdjustmentValue());
                }
                processRotationAndFlip(shape, autoShape);
                if (parent == null) {
                    shapesList.add(autoShape);
                } else {
                    parent.appendShapes(autoShape);
                }
            }


            HSSFTextbox textbox = (HSSFTextbox) shape;
            HSSFRichTextString richTextString = textbox.getString();
            if (richTextString != null) {
                String str = richTextString.getString();
                if (str != null && str.length() > 0) {
                    TextBox tb = new TextBox();
                    tb.setElement(SectionElementFactory.getSectionElement(book, textbox, rect));
                    tb.setWrapLine(textbox.isTextboxWrapLine());
                    tb.setBounds(rect);

                    processRotationAndFlip(shape, tb);
                    if (parent == null) {
                        shapesList.add(tb);
                    } else {
                        parent.appendShapes(tb);
                    }
                }
            }
        }
    }

    public AWorkbook getAWorkbook() {
        return (AWorkbook) book;
    }

    public InternalSheet getInternalSheet() {
        return sheet;
    }


    public void dispose() {
        super.dispose();

        sheet.dispose();
        sheet = null;
    }
}

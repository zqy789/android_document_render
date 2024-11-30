
package com.document.render.office.pg.model;

import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.common.shape.AbstractShape;
import com.document.render.office.common.shape.IShape;
import com.document.render.office.common.shape.SmartArt;
import com.document.render.office.common.shape.TableCell;
import com.document.render.office.common.shape.TableShape;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.java.awt.Rectanglef;
import com.document.render.office.pg.animate.ShapeAnimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PGSlide {

    public static final byte Slide_Master = 0;

    public static final byte Slide_Layout = 1;

    public static final byte Slide_Normal = 2;

    private boolean hasTable;

    private int slideNo;

    private int slideType;

    private int shapeCountForFind = -1;

    private List<IShape> shapes;

    private List<IShape> shapesForFind;

    private PGNotes notes;

    private BackgroundAndFill bgFill;

    private int[] masterIndexs = {-1, -1};


    private boolean hasTransition;

    private Map<Integer, List<Integer>> grpShapeLst;

    private List<ShapeAnimation> shapeAnimLst;
    private Map<String, SmartArt> smartArtList;
    private boolean showMasterHeadersFooters;

    private int geometryType;


    public PGSlide() {
        shapes = new ArrayList<IShape>();
        showMasterHeadersFooters = true;
        geometryType = -1;
    }


    public PGSlide(int slideNo, PGNotes notes) {
        this.slideNo = slideNo;
        this.notes = notes;
        shapes = new ArrayList<IShape>();
        showMasterHeadersFooters = true;
        geometryType = -1;
    }


    public int getSlideNo() {
        return slideNo;
    }


    public void setSlideNo(int slideNo) {
        this.slideNo = slideNo;
    }


    public int getSlideType() {
        return slideType;
    }


    public void setSlideType(int slideType) {
        this.slideType = slideType;
    }


    public void appendShapes(IShape shape) {
        if (shape == null) {
            return;
        }
        if (!hasTable) {
            hasTable = shape.getType() == AbstractShape.SHAPE_TABLE;
        }
        this.shapes.add(shape);
    }


    public IShape[] getShapes() {
        return shapes.toArray(new IShape[shapes.size()]);
    }


    public int getShapeCount() {
        return shapes.size();
    }


    public int getShapeCountForFind() {
        if (!hasTable) {
            return getShapeCount();
        }
        if (shapeCountForFind > 0) {
            return shapeCountForFind;
        }
        shapesForFind = new ArrayList<IShape>();
        int count = 0;
        for (IShape shape : shapes) {
            if (shape.getType() == AbstractShape.SHAPE_TABLE) {
                for (int i = 0; i < ((TableShape) shape).getCellCount(); i++) {
                    TableCell cell = ((TableShape) shape).getCell(i);
                    if (cell != null && cell.getText() != null) {
                        shapesForFind.add(cell.getText());
                        count++;
                    }
                }
            } else {
                shapesForFind.add(shape);
                count++;
            }
        }
        return shapeCountForFind = count;
    }


    public IShape getShape(int index) {
        if (index < 0 || index >= shapes.size()) {
            return null;
        }
        return shapes.get(index);
    }


    public IShape getShapeForFind(int index) {
        if (!hasTable) {
            return getShape(index);
        }
        if (index < 0 || index >= shapesForFind.size()) {
            return null;
        }
        return shapesForFind.get(index);
    }


    public IShape getShape(float x, float y) {
        for (IShape shape : shapes) {
            Rectangle rect = shape.getBounds();
            if (shape.getType() == AbstractShape.SHAPE_TABLE) {
                TableShape table = (TableShape) shape;
                int count = table.getCellCount();
                for (int i = 0; i < count; i++) {
                    TableCell cell = table.getCell(i);
                    if (cell != null) {
                        Rectanglef r = cell.getBounds();
                        if (r.contains(x, y)) {
                            return cell.getText();
                        }
                    }
                }
            } else if (rect.contains(x, y)) {
                return shape;
            }
        }
        return null;
    }


    public PGNotes getNotes() {
        return this.notes;
    }


    public void setNotes(PGNotes notes) {
        this.notes = notes;
    }


    public BackgroundAndFill getBackgroundAndFill() {
        return bgFill;
    }


    public void setBackgroundAndFill(BackgroundAndFill bgFill) {
        this.bgFill = bgFill;
    }


    public void setMasterSlideIndex(int index) {
        masterIndexs[0] = index;
    }


    public void setLayoutSlideIndex(int index) {
        masterIndexs[1] = index;
    }


    public int[] getMasterIndexs() {
        return masterIndexs;
    }


    public IShape getShape(int x, int y) {
        for (IShape shape : shapes) {
            Rectangle rect = shape.getBounds();
            if (shape.getType() == AbstractShape.SHAPE_TABLE) {
                TableShape table = (TableShape) shape;
                int count = table.getCellCount();
                for (int i = 0; i < count; i++) {
                    TableCell cell = table.getCell(i);
                    if (cell != null) {
                        Rectanglef r = cell.getBounds();
                        if (r.contains(x, y)) {
                            return cell.getText();
                        }
                    }
                }
            } else if (rect.contains(x, y)) {
                return shape;
            }
        }
        return null;
    }


    public IShape getTextboxShape(int x, int y) {

        int shapeCnt = shapes.size();
        for (int i = shapeCnt - 1; i >= 0; i--) {
            IShape shape = shapes.get(i);
            Rectangle rect = shape.getBounds();
            if (shape.getType() == AbstractShape.SHAPE_TABLE) {
                TableShape table = (TableShape) shape;
                int count = table.getCellCount();
                for (int j = 0; j < count; j++) {
                    TableCell cell = table.getCell(j);
                    if (cell != null) {
                        Rectanglef r = cell.getBounds();
                        if (r.contains(x, y)) {
                            return cell.getText();
                        }
                    }
                }
            } else if (rect.contains(x, y) && shape.getType() == AbstractShape.SHAPE_TEXTBOX) {
                return shape;
            }
        }
        return null;
    }


    public void setTransition(boolean transition) {
        hasTransition = transition;
    }


    public boolean hasTransition() {
        return hasTransition;
    }

    public void addShapeAnimation(ShapeAnimation shapeAnim) {
        if (shapeAnimLst == null) {
            shapeAnimLst = new ArrayList<ShapeAnimation>();
        }

        if (shapeAnim != null) {
            shapeAnimLst.add(shapeAnim);
        }
    }


    public List<ShapeAnimation> getSlideShowAnimation() {
        return shapeAnimLst;
    }

    public void addGroupShape(int grpShapeID, List<Integer> childShapes) {
        if (grpShapeLst == null) {
            grpShapeLst = new HashMap<Integer, List<Integer>>();
        }
        Integer[] arr = new Integer[childShapes.size()];
        childShapes.toArray(arr);

        for (Integer id : arr) {
            if (grpShapeLst.containsKey(id)) {
                List<Integer> subShapes = grpShapeLst.remove(id);
                childShapes.remove(id);
                childShapes.addAll(subShapes);
            }
        }
        grpShapeLst.put(grpShapeID, childShapes);
    }

    public Map<Integer, List<Integer>> getGroupShape() {
        return grpShapeLst;
    }


    public void addSmartArt(String id, SmartArt smartArt) {
        if (smartArtList == null) {
            smartArtList = new HashMap<String, SmartArt>();
        }

        smartArtList.put(id, smartArt);
    }


    public SmartArt getSmartArt(String id) {
        if (id != null && smartArtList != null) {
            return smartArtList.remove(id);
        }

        return null;
    }

    public IShape getTextboxByPlaceHolderID(int placeHolderID) {
        int shapeCnt = shapes.size();
        for (int i = 0; i < shapeCnt; i++) {
            IShape shape = shapes.get(i);
            if (shape.getType() == AbstractShape.SHAPE_TEXTBOX && shape.getPlaceHolderID() == placeHolderID) {
                return shape;
            }
        }
        return null;
    }


    public boolean isShowMasterHeadersFooter() {
        return showMasterHeadersFooters;
    }


    public void setShowMasterHeadersFooters(boolean showMasterHeadersFooters) {
        this.showMasterHeadersFooters = showMasterHeadersFooters;
    }


    public int getGeometryType() {
        return geometryType;
    }


    public void setGeometryType(int geometryType) {
        this.geometryType = geometryType;
    }


    public void dispose() {
        if (notes != null) {
            notes.dispose();
            notes = null;
        }
        if (shapesForFind != null) {
            shapesForFind.clear();
            shapesForFind = null;
        }
        if (shapes != null) {
            for (IShape shape : shapes) {
                shape.dispose();
            }
            shapes.clear();
            shapes = null;
        }
        if (bgFill != null) {
            bgFill.dispose();
            bgFill = null;
        }
        if (shapeAnimLst != null) {
            shapeAnimLst.clear();
            shapeAnimLst = null;
        }
    }
}

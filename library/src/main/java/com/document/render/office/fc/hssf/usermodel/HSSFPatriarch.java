

package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.ddf.EscherBSERecord;
import com.document.render.office.fc.ddf.EscherComplexProperty;
import com.document.render.office.fc.ddf.EscherOptRecord;
import com.document.render.office.fc.ddf.EscherProperty;
import com.document.render.office.fc.hssf.record.EscherAggregate;
import com.document.render.office.fc.ss.usermodel.Chart;
import com.document.render.office.fc.ss.usermodel.Drawing;
import com.document.render.office.fc.util.Internal;
import com.document.render.office.fc.util.StringUtil;
import com.document.render.office.ss.model.XLSModel.ASheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public final class HSSFPatriarch implements HSSFShapeContainer, Drawing {
    protected ASheet _sheet;
    private List<HSSFShape> _shapes = new ArrayList<HSSFShape>();
    private int _x1 = 0;
    private int _y1 = 0;
    private int _x2 = 1023;
    private int _y2 = 255;

    private EscherAggregate _boundAggregate;


    public HSSFPatriarch(ASheet sheet, EscherAggregate boundAggregate) {
        _sheet = sheet;
        _boundAggregate = boundAggregate;
    }


    public HSSFShapeGroup createGroup(HSSFClientAnchor anchor) {
        HSSFShapeGroup group = new HSSFShapeGroup(null, null, anchor);
        group.setAnchor(anchor);
        addShape(group);
        return group;
    }


    public HSSFSimpleShape createSimpleShape(HSSFClientAnchor anchor) {
        HSSFSimpleShape shape = new HSSFSimpleShape(null, null, anchor);
        shape.setAnchor(anchor);
        addShape(shape);
        return shape;
    }


    public HSSFPicture createPicture(HSSFClientAnchor anchor, int pictureIndex) {
        HSSFPicture shape = new HSSFPicture(null, null, anchor);
        shape.setPictureIndex(pictureIndex);
        shape.setAnchor(anchor);
        addShape(shape);

        EscherBSERecord bse = _sheet.getAWorkbook().getInternalWorkbook().getBSERecord(pictureIndex);
        bse.setRef(bse.getRef() + 1);
        return shape;
    }

    public HSSFPicture createPicture(IClientAnchor anchor, int pictureIndex) {
        return createPicture((HSSFClientAnchor) anchor, pictureIndex);
    }


    public HSSFPolygon createPolygon(HSSFClientAnchor anchor) {
        HSSFPolygon shape = new HSSFPolygon(null, null, anchor);
        shape.setAnchor(anchor);
        addShape(shape);
        return shape;
    }


    public HSSFTextbox createTextbox(HSSFClientAnchor anchor) {
        HSSFTextbox shape = new HSSFTextbox(null, null, anchor);
        shape.setAnchor(anchor);
        addShape(shape);
        return shape;
    }


    public HSSFComment createComment(HSSFAnchor anchor) {
        HSSFComment shape = new HSSFComment(null, null, anchor);
        shape.setAnchor(anchor);
        addShape(shape);
        return shape;
    }


    HSSFSimpleShape createComboBox(HSSFAnchor anchor) {
        HSSFSimpleShape shape = new HSSFSimpleShape(null, null, anchor);
        shape.setShapeType(HSSFSimpleShape.OBJECT_TYPE_COMBO_BOX);
        shape.setAnchor(anchor);
        addShape(shape);
        return shape;
    }

    public HSSFComment createCellComment(IClientAnchor anchor) {
        return createComment((HSSFAnchor) anchor);
    }


    public List<HSSFShape> getChildren() {
        return _shapes;
    }


    @Internal
    public void addShape(HSSFShape shape) {
        shape._patriarch = this;
        _shapes.add(shape);
    }


    public int countOfAllChildren() {
        int count = _shapes.size();
        for (Iterator<HSSFShape> iterator = _shapes.iterator(); iterator.hasNext(); ) {
            HSSFShape shape = iterator.next();
            count += shape.countOfAllChildren();
        }
        return count;
    }


    public void setCoordinates(int x1, int y1, int x2, int y2) {
        _x1 = x1;
        _y1 = y1;
        _x2 = x2;
        _y2 = y2;
    }


    public boolean containsChart() {



        EscherOptRecord optRecord = (EscherOptRecord)
                _boundAggregate.findFirstWithId(EscherOptRecord.RECORD_ID);
        if (optRecord == null) {

            return false;
        }

        for (Iterator<EscherProperty> it = optRecord.getEscherProperties().iterator(); it.hasNext(); ) {
            EscherProperty prop = it.next();
            if (prop.getPropertyNumber() == 896 && prop.isComplex()) {
                EscherComplexProperty cp = (EscherComplexProperty) prop;
                String str = StringUtil.getFromUnicodeLE(cp.getComplexData());

                if (str.equals("Chart 1\0")) {
                    return true;
                }
            }
        }

        return false;
    }


    public int getX1() {
        return _x1;
    }


    public int getY1() {
        return _y1;
    }


    public int getX2() {
        return _x2;
    }


    public int getY2() {
        return _y2;
    }


    protected EscherAggregate _getBoundAggregate() {
        return _boundAggregate;
    }


    public HSSFClientAnchor createAnchor(int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2) {
        return new HSSFClientAnchor(dx1, dy1, dx2, dy2, (short) col1, row1, (short) col2, row2);
    }

    public Chart createChart(IClientAnchor anchor) {
        throw new RuntimeException("NotImplemented");
    }

    public void dispose() {
        _shapes.clear();
        _shapes = null;

        _boundAggregate = null;
        _sheet = null;
    }
}


package com.document.render.office.wp.view;

import com.document.render.office.common.shape.AbstractShape;
import com.document.render.office.common.shape.AutoShape;
import com.document.render.office.constant.wp.AttrIDConstant;
import com.document.render.office.constant.wp.WPModelConstant;
import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.simpletext.model.AttrManage;
import com.document.render.office.simpletext.model.IElement;
import com.document.render.office.simpletext.view.IView;
import com.document.render.office.system.IControl;


public class ViewFactory {


    public static IView createView(IControl control, IElement elem, IElement paraElem, int viewType) {
        IView view = null;
        switch (viewType) {
            case WPViewConstant.PAGE_VIEW:
                view = new PageView(elem);
                break;

            case WPViewConstant.PARAGRAPH_VIEW:
                view = new ParagraphView(elem);
                break;

            case WPViewConstant.LINE_VIEW:


                view = new LineView(elem);
                break;

            case WPViewConstant.LEAF_VIEW:

                if (AttrManage.instance().hasAttribute(elem.getAttribute(), AttrIDConstant.FONT_SHAPE_ID)) {
                    AbstractShape shape = control.getSysKit().getWPShapeManage().getShape(AttrManage.instance().getShapeID(elem.getAttribute()));
                    if (shape != null) {
                        if (shape.getType() == AbstractShape.SHAPE_AUTOSHAPE
                                || shape.getType() == AbstractShape.SHAPE_CHART) {
                            view = new ShapeView(paraElem, elem, (AutoShape) shape);
                        } else if (shape.getType() == AbstractShape.SHAPE_PICTURE) {
                            view = new ObjView(paraElem, elem, (com.document.render.office.common.shape.WPAutoShape) shape);
                        }
                    } else {
                        view = new ObjView(paraElem, elem, null);
                    }
                } else if (AttrManage.instance().hasAttribute(elem.getAttribute(), AttrIDConstant.FONT_ENCLOSE_CHARACTER_TYPE_ID)) {
                    view = new EncloseCharacterView(paraElem, elem);
                } else {
                    view = new LeafView(paraElem, elem);
                }
                break;

            case WPViewConstant.TABLE_VIEW:
                if (elem.getType() == WPModelConstant.TABLE_ELEMENT) {
                    view = new TableView(elem);
                } else {
                    view = new ParagraphView(elem);
                }
                break;

            case WPViewConstant.TABLE_ROW_VIEW:
                view = new RowView(elem);
                break;

            case WPViewConstant.TABLE_CELL_VIEW:
                view = new CellView(elem);
                break;

            case WPViewConstant.TITLE_VIEW:
                view = new TitleView(elem);
                break;

            case WPViewConstant.BN_VIEW:

                view = new BNView();
                break;

            default:
                break;
        }
        return view;
    }


    public static void dispose() {








    }










}

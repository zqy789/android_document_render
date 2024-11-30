

package com.document.render.office.fc.hslf.model;

import com.document.render.office.common.shape.ShapeTypes;
import com.document.render.office.fc.ShapeKit;
import com.document.render.office.fc.ddf.EscherProperties;
import com.document.render.office.java.awt.geom.AffineTransform;
import com.document.render.office.java.awt.geom.Arc2D;
import com.document.render.office.java.awt.geom.Ellipse2D;
import com.document.render.office.java.awt.geom.GeneralPath;
import com.document.render.office.java.awt.geom.Line2D;
import com.document.render.office.java.awt.geom.Rectangle2D;
import com.document.render.office.java.awt.geom.RoundRectangle2D;


public final class AutoShapes {
    protected static ShapeOutline[] shapes;

    static {
        shapes = new ShapeOutline[255];

        shapes[ShapeTypes.Rectangle] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {
                Rectangle2D path = new Rectangle2D.Float(0, 0, 21600, 21600);
                return path;
            }
        };

        shapes[ShapeTypes.RoundRectangle] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {
                int adjval = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUSTVALUE, 5400);
                RoundRectangle2D path = new RoundRectangle2D.Float(0, 0, 21600, 21600, adjval,
                        adjval);
                return path;
            }
        };

        shapes[ShapeTypes.Ellipse] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {
                Ellipse2D path = new Ellipse2D.Float(0, 0, 21600, 21600);
                return path;
            }
        };

        shapes[ShapeTypes.Diamond] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {
                GeneralPath path = new GeneralPath();
                path.moveTo(10800, 0);
                path.lineTo(21600, 10800);
                path.lineTo(10800, 21600);
                path.lineTo(0, 10800);
                path.closePath();
                return path;
            }
        };


        shapes[ShapeTypes.IsocelesTriangle] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {
                int adjval = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUSTVALUE, 10800);
                GeneralPath path = new GeneralPath();
                path.moveTo(adjval, 0);
                path.lineTo(0, 21600);
                path.lineTo(21600, 21600);
                path.closePath();
                return path;
            }
        };

        shapes[ShapeTypes.RightTriangle] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {
                GeneralPath path = new GeneralPath();
                path.moveTo(0, 0);
                path.lineTo(21600, 21600);
                path.lineTo(0, 21600);
                path.closePath();
                return path;
            }
        };

        shapes[ShapeTypes.Parallelogram] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {
                int adjval = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUSTVALUE, 5400);

                GeneralPath path = new GeneralPath();
                path.moveTo(adjval, 0);
                path.lineTo(21600, 0);
                path.lineTo(21600 - adjval, 21600);
                path.lineTo(0, 21600);
                path.closePath();
                return path;
            }
        };

        shapes[ShapeTypes.Trapezoid] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {
                int adjval = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUSTVALUE, 5400);

                GeneralPath path = new GeneralPath();
                path.moveTo(0, 0);
                path.lineTo(adjval, 21600);
                path.lineTo(21600 - adjval, 21600);
                path.lineTo(21600, 0);
                path.closePath();
                return path;
            }
        };

        shapes[ShapeTypes.Hexagon] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {
                int adjval = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUSTVALUE, 5400);

                GeneralPath path = new GeneralPath();
                path.moveTo(adjval, 0);
                path.lineTo(21600 - adjval, 0);
                path.lineTo(21600, 10800);
                path.lineTo(21600 - adjval, 21600);
                path.lineTo(adjval, 21600);
                path.lineTo(0, 10800);
                path.closePath();
                return path;
            }
        };

        shapes[ShapeTypes.Octagon] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {
                int adjval = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUSTVALUE, 6326);

                GeneralPath path = new GeneralPath();
                path.moveTo(adjval, 0);
                path.lineTo(21600 - adjval, 0);
                path.lineTo(21600, adjval);
                path.lineTo(21600, 21600 - adjval);
                path.lineTo(21600 - adjval, 21600);
                path.lineTo(adjval, 21600);
                path.lineTo(0, 21600 - adjval);
                path.lineTo(0, adjval);
                path.closePath();
                return path;
            }
        };

        shapes[ShapeTypes.Plus] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {
                int adjval = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUSTVALUE, 5400);

                GeneralPath path = new GeneralPath();
                path.moveTo(adjval, 0);
                path.lineTo(21600 - adjval, 0);
                path.lineTo(21600 - adjval, adjval);
                path.lineTo(21600, adjval);
                path.lineTo(21600, 21600 - adjval);
                path.lineTo(21600 - adjval, 21600 - adjval);
                path.lineTo(21600 - adjval, 21600);
                path.lineTo(adjval, 21600);
                path.lineTo(adjval, 21600 - adjval);
                path.lineTo(0, 21600 - adjval);
                path.lineTo(0, adjval);
                path.lineTo(adjval, adjval);
                path.closePath();
                return path;
            }
        };

        shapes[ShapeTypes.Pentagon] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {

                GeneralPath path = new GeneralPath();
                path.moveTo(10800, 0);
                path.lineTo(21600, 8259);
                path.lineTo(21600 - 4200, 21600);
                path.lineTo(4200, 21600);
                path.lineTo(0, 8259);
                path.closePath();
                return path;
            }
        };

        shapes[ShapeTypes.DownArrow] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {

                int adjval = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUSTVALUE, 16200);
                int adjval2 = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUST2VALUE, 5400);
                GeneralPath path = new GeneralPath();
                path.moveTo(0, adjval);
                path.lineTo(adjval2, adjval);
                path.lineTo(adjval2, 0);
                path.lineTo(21600 - adjval2, 0);
                path.lineTo(21600 - adjval2, adjval);
                path.lineTo(21600, adjval);
                path.lineTo(10800, 21600);
                path.closePath();
                return path;
            }
        };

        shapes[ShapeTypes.UpArrow] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {

                int adjval = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUSTVALUE, 5400);
                int adjval2 = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUST2VALUE, 5400);
                GeneralPath path = new GeneralPath();
                path.moveTo(0, adjval);
                path.lineTo(adjval2, adjval);
                path.lineTo(adjval2, 21600);
                path.lineTo(21600 - adjval2, 21600);
                path.lineTo(21600 - adjval2, adjval);
                path.lineTo(21600, adjval);
                path.lineTo(10800, 0);
                path.closePath();
                return path;
            }
        };

        shapes[ShapeTypes.Arrow] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {

                int adjval = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUSTVALUE, 16200);
                int adjval2 = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUST2VALUE, 5400);
                GeneralPath path = new GeneralPath();
                path.moveTo(adjval, 0);
                path.lineTo(adjval, adjval2);
                path.lineTo(0, adjval2);
                path.lineTo(0, 21600 - adjval2);
                path.lineTo(adjval, 21600 - adjval2);
                path.lineTo(adjval, 21600);
                path.lineTo(21600, 10800);
                path.closePath();
                return path;
            }
        };

        shapes[ShapeTypes.LeftArrow] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {

                int adjval = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUSTVALUE, 5400);
                int adjval2 = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUST2VALUE, 5400);
                GeneralPath path = new GeneralPath();
                path.moveTo(adjval, 0);
                path.lineTo(adjval, adjval2);
                path.lineTo(21600, adjval2);
                path.lineTo(21600, 21600 - adjval2);
                path.lineTo(adjval, 21600 - adjval2);
                path.lineTo(adjval, 21600);
                path.lineTo(0, 10800);
                path.closePath();
                return path;
            }
        };

        shapes[ShapeTypes.Can] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {

                int adjval = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUSTVALUE, 5400);

                GeneralPath path = new GeneralPath();

                path.append(new Arc2D.Float(0, 0, 21600, adjval, 0, 180, Arc2D.OPEN), false);
                path.moveTo(0, adjval / 2);

                path.lineTo(0, 21600 - adjval / 2);
                path.closePath();

                path.append(
                        new Arc2D.Float(0, 21600 - adjval, 21600, adjval, 180, 180, Arc2D.OPEN), false);
                path.moveTo(21600, 21600 - adjval / 2);

                path.lineTo(21600, adjval / 2);
                path.append(new Arc2D.Float(0, 0, 21600, adjval, 180, 180, Arc2D.OPEN), false);
                path.moveTo(0, adjval / 2);
                path.closePath();
                return path;
            }
        };

        shapes[ShapeTypes.LeftBrace] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {

                int adjval = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUSTVALUE, 1800);
                int adjval2 = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUST2VALUE,
                        10800);

                GeneralPath path = new GeneralPath();
                path.moveTo(21600, 0);

                path.append(new Arc2D.Float(10800, 0, 21600, adjval * 2, 90, 90, Arc2D.OPEN), false);
                path.moveTo(10800, adjval);

                path.lineTo(10800, adjval2 - adjval);

                path.append(new Arc2D.Float(-10800, adjval2 - 2 * adjval, 21600, adjval * 2, 270,
                        90, Arc2D.OPEN), false);
                path.moveTo(0, adjval2);

                path.append(new Arc2D.Float(-10800, adjval2, 21600, adjval * 2, 0, 90, Arc2D.OPEN),
                        false);
                path.moveTo(10800, adjval2 + adjval);

                path.lineTo(10800, 21600 - adjval);

                path.append(new Arc2D.Float(10800, 21600 - 2 * adjval, 21600, adjval * 2, 180, 90,
                        Arc2D.OPEN), false);

                return path;
            }
        };

        shapes[ShapeTypes.RightBrace] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {

                int adjval = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUSTVALUE, 1800);
                int adjval2 = ShapeKit.getEscherProperty(shape.getSpContainer(), EscherProperties.GEOMETRY__ADJUST2VALUE,
                        10800);

                GeneralPath path = new GeneralPath();
                path.moveTo(0, 0);

                path.append(new Arc2D.Float(-10800, 0, 21600, adjval * 2, 0, 90, Arc2D.OPEN), false);
                path.moveTo(10800, adjval);

                path.lineTo(10800, adjval2 - adjval);

                path.append(new Arc2D.Float(10800, adjval2 - 2 * adjval, 21600, adjval * 2, 180,
                        90, Arc2D.OPEN), false);
                path.moveTo(21600, adjval2);

                path.append(new Arc2D.Float(10800, adjval2, 21600, adjval * 2, 90, 90, Arc2D.OPEN),
                        false);
                path.moveTo(10800, adjval2 + adjval);

                path.lineTo(10800, 21600 - adjval);

                path.append(new Arc2D.Float(-10800, 21600 - 2 * adjval, 21600, adjval * 2, 270, 90,
                        Arc2D.OPEN), false);

                return path;
            }
        };

        shapes[ShapeTypes.StraightConnector1] = new ShapeOutline() {
            public com.document.render.office.java.awt.Shape getOutline(Shape shape) {
                return new Line2D.Float(0, 0, 21600, 21600);
            }
        };

    }


    public static ShapeOutline getShapeOutline(int type) {
        ShapeOutline outline = shapes[type];
        return outline;
    }


    public static com.document.render.office.java.awt.Shape transform(com.document.render.office.java.awt.Shape outline, Rectangle2D anchor) {
        AffineTransform at = new AffineTransform();
        at.translate(anchor.getX(), anchor.getY());
        at.scale(1.0f / 21600 * anchor.getWidth(), 1.0f / 21600 * anchor.getHeight());
        return at.createTransformedShape(outline);
    }
}

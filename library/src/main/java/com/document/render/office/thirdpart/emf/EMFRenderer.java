package com.document.render.office.thirdpart.emf;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Build;

import com.document.render.office.java.awt.Color;
import com.document.render.office.java.awt.Dimension;
import com.document.render.office.java.awt.Image;
import com.document.render.office.java.awt.Rectangle;
import com.document.render.office.java.awt.Shape;
import com.document.render.office.java.awt.Stroke;
import com.document.render.office.java.awt.geom.AffineTransform;
import com.document.render.office.java.awt.geom.Area;
import com.document.render.office.java.awt.geom.GeneralPath;
import com.document.render.office.java.awt.geom.IllegalPathStateException;
import com.document.render.office.java.awt.geom.PathIterator;
import com.document.render.office.simpletext.font.Font;
import com.document.render.office.thirdpart.emf.data.BasicStroke;
import com.document.render.office.thirdpart.emf.data.GDIObject;
import com.document.render.office.thirdpart.emf.io.Tag;

import java.io.IOException;
import java.util.Stack;
import java.util.Vector;
import java.util.logging.Logger;



public class EMFRenderer {
    private static final Logger logger = Logger.getLogger("com.document.render.office.thirdpart.emf");

    public static double TWIP_SCALE = 1d / 1440 * 254;

    private EMFHeader header;

    private GeneralPath figure = null;



    private Matrix initialMatrix;


    private Point windowOrigin = null;


    private Point viewportOrigin = null;


    private Dimension windowSize = null;


    private Dimension viewportSize = null;


    private boolean mapModeIsotropic = false;


    private AffineTransform mapModeTransform = AffineTransform.getScaleInstance(TWIP_SCALE,
            TWIP_SCALE);



    private Shape initialClip;



    private Canvas mCanvas;


    private GDIObject[] gdiObjects = new GDIObject[256];




    private Stroke penStroke = new BasicStroke();
    private Paint brushPaint = new Paint();
    private Paint penPaint = new Paint();

    private int textAlignMode = 0;


    private Color textColor = Color.BLACK;


    private int windingRule = GeneralPath.WIND_EVEN_ODD;


    private int bkMode = EMFConstants.BKG_OPAQUE;


    private boolean useCreatePen = true;


    private int meterLimit = 10;


    private int rop2 = EMFConstants.R2_COPYPEN;


    private int scaleMode = Image.SCALE_SMOOTH;


    private Point brushOrigin = new Point(0, 0);


    private Vector tags = new Vector(0);


    private GeneralPath path = null;


    private AffineTransform pathTransform = new AffineTransform();


    private Stack dcStack = new Stack();


    private int arcDirection = EMFConstants.AD_COUNTERCLOCKWISE;

    private Area mCurrClip;

    private int escapement = 0;


    public EMFRenderer(EMFInputStream is) throws IOException {

        brushPaint.setColor(new Color(0, 0, 0, 0).getRGB());
        penPaint.setColor(Color.BLACK.getRGB());

        this.header = is.readHeader();


        Tag tag;
        while ((tag = is.readTag()) != null) {
            tags.add(tag);
        }
        is.close();
    }

    public static float[] createMatrix(AffineTransform Tx) {
        double[] at = new double[9];
        Tx.getMatrix(at);
        float[] f = new float[at.length];
        f[0] = (float) at[0];
        f[1] = (float) at[2];
        f[2] = (float) at[4];
        f[3] = (float) at[1];
        f[4] = (float) at[3];
        f[5] = (float) at[5];
        f[6] = 0;
        f[7] = 0;
        f[8] = 1;
        return f;
    }


    public Dimension getSize() {
        return header.getBounds().getSize();



    }



    public void paint(Canvas canvas) {

        this.mCanvas = canvas;



        Rect rect = canvas.getClipBounds();


        Matrix matrix = canvas.getMatrix();


        int cl[] = {-1, rect.top, rect.left, -2, rect.top, rect.right, -2, rect.bottom, rect.right, -2, rect.bottom, rect.left};
        mCurrClip = new Area(createShape(cl));













        penPaint.setAntiAlias(true);
        penPaint.setFilterBitmap(true);
        penPaint.setDither(true);



        initialMatrix = canvas.getMatrix();


        path = null;
        figure = null;
        meterLimit = 10;
        windingRule = GeneralPath.WIND_EVEN_ODD;
        bkMode = EMFConstants.BKG_OPAQUE;
        useCreatePen = true;
        scaleMode = Image.SCALE_SMOOTH;

        windowOrigin = null;
        viewportOrigin = null;
        windowSize = null;
        viewportSize = null;

        mapModeIsotropic = false;
        mapModeTransform = AffineTransform.getScaleInstance(TWIP_SCALE, TWIP_SCALE);



        resetMatrix(canvas);



        initialClip = mCurrClip;


        Tag tag;
        for (int i = 0; i < tags.size(); i++) {
            tag = (Tag) tags.get(i);
            if (tag instanceof EMFTag) {
                ((EMFTag) tags.get(i)).render(this);
            } else {
                logger.warning("unknown tag: " + tag);
            }
        }





        penPaint.setAntiAlias(true);
        penPaint.setFilterBitmap(true);
        penPaint.setDither(true);

        canvas.setMatrix(matrix);

        setClip(initialClip);
    }










































    private void resetMatrix(Canvas canvas) {

        if (initialMatrix != null) {

            canvas.setMatrix(initialMatrix);
        } else {
            canvas.setMatrix(new Matrix());
        }















        if (viewportSize != null && windowSize != null) {
            float scaleX = (float) (viewportSize.getWidth() / windowSize.getWidth());
            float scaleY = (float) (viewportSize.getHeight() / windowSize.getHeight());
            canvas.scale(scaleX, scaleY);
        }
    }


    public void saveDC() {

        DC dc = new DC();

        dc.paint = penPaint;




        dc.matrix = mCanvas.getMatrix();

        dc.clip = mCurrClip;
        dc.path = path;
        dc.meterLimit = meterLimit;
        dc.windingRule = windingRule;
        dc.bkMode = bkMode;
        dc.useCreatePen = useCreatePen;
        dc.scaleMode = scaleMode;

        dcStack.push(dc);
        mCanvas.save();
    }


    public void retoreDC() {

        if (!dcStack.empty()) {

            DC dc = (DC) dcStack.pop();


            meterLimit = dc.meterLimit;
            windingRule = dc.windingRule;
            path = dc.path;
            bkMode = dc.bkMode;
            useCreatePen = dc.useCreatePen;
            scaleMode = dc.scaleMode;
            pathTransform = dc.pathTransform;




            setStroke(penStroke);

            mCanvas.setMatrix(dc.matrix);
            setClip(dc.clip);
        } else {

        }
        mCanvas.restore();
    }


    public void closeFigure() {
        if (figure == null) {
            return;
        }

        try {
            figure.closePath();
            appendToPath(figure);
            figure = null;
        } catch (IllegalPathStateException e) {
            logger.warning("no figure to close");
        }
    }


    public void appendFigure() {
        if (figure == null) {
            return;
        }

        try {
            appendToPath(figure);
            figure = null;
        } catch (IllegalPathStateException e) {
            logger.warning("no figure to append");
        }
    }


    public void fixViewportSize() {
        if (mapModeIsotropic && (windowSize != null && viewportSize != null)) {
            viewportSize.setSize(viewportSize.getWidth(),
                    viewportSize.getWidth() * (windowSize.getHeight() / windowSize.getWidth()));
        }
    }



    private void fillAndDrawOrAppend(Canvas canvas, Shape s) {


        if (!appendToPath(s)) {



            if (useCreatePen) {


                if (bkMode == EMFConstants.BKG_OPAQUE) {

                    fillShape(s);
                } else {




                    fillShape(s);
                }
            } else {


                fillShape(s);
            }

            drawShape(canvas, s);
        }
    }











    private void drawOrAppend(Canvas canvas, Shape s) {


        if (!appendToPath(s)) {
            drawShape(canvas, s);
        }
    }



    public void drawOrAppendText(String text, float x, float y) {






















        Style tmp = penPaint.getStyle();
        penPaint.setColor(textColor.getRGB());
        penPaint.setStrokeWidth(0);

        if (2700 == escapement) {
            for (int i = 0; i < text.length(); i++) {
                mCanvas.drawText(String.valueOf(text.charAt(i)), x, y + i * penPaint.getTextSize(), penPaint);
            }
        } else {
            if (EMFConstants.TA_TOP == textAlignMode) {
                y += penPaint.getTextSize() - 3;
            }
            mCanvas.drawText(text, x, y, penPaint);
        }
        penPaint.setStyle(tmp);

    }


    private boolean appendToPath(Shape s) {


        if (path != null) {

            if (pathTransform != null) {
                s = pathTransform.createTransformedShape(s);
            }

            path.append(s, false);

            return true;
        }

        return false;
    }


    public void closePath() {
        if (path != null) {
            try {
                path.closePath();
            } catch (IllegalPathStateException e) {
                logger.warning("no figure to close");
            }
        }
    }

    private void getCurrentSegment(PathIterator pi, Path path) {
        float[] coordinates = new float[6];
        int type = pi.currentSegment(coordinates);
        switch (type) {
            case PathIterator.SEG_MOVETO:
                path.moveTo(coordinates[0], coordinates[1]);
                break;
            case PathIterator.SEG_LINETO:
                path.lineTo(coordinates[0], coordinates[1]);
                break;
            case PathIterator.SEG_QUADTO:
                path.quadTo(coordinates[0], coordinates[1], coordinates[2],
                        coordinates[3]);
                break;
            case PathIterator.SEG_CUBICTO:
                path.cubicTo(coordinates[0], coordinates[1], coordinates[2],
                        coordinates[3], coordinates[4], coordinates[5]);
                break;
            case PathIterator.SEG_CLOSE:
                path.close();
                break;
            default:
                break;
        }
    }

    private Path getPath(Shape s) {
        Path path = new Path();
        PathIterator pi = s.getPathIterator(null);
        while (pi.isDone() == false) {
            getCurrentSegment(pi, path);
            pi.next();
        }
        return path;
    }












    private void setStroke(Stroke stroke) {



        BasicStroke bs = (BasicStroke) stroke;
        penPaint.setStyle(Paint.Style.STROKE);
        penPaint.setStrokeWidth(bs.getLineWidth());

        int cap = bs.getEndCap();
        if (cap == 0) {
            penPaint.setStrokeCap(Paint.Cap.BUTT);
        } else if (cap == 1) {
            penPaint.setStrokeCap(Paint.Cap.ROUND);
        } else if (cap == 2) {
            penPaint.setStrokeCap(Paint.Cap.SQUARE);
        }

        int join = bs.getLineJoin();
        if (join == 0) {
            penPaint.setStrokeJoin(Paint.Join.MITER);
        } else if (join == 1) {
            penPaint.setStrokeJoin(Paint.Join.ROUND);
        } else if (join == 2) {
            penPaint.setStrokeJoin(Paint.Join.BEVEL);
        }
        penPaint.setStrokeMiter(bs.getMiterLimit());
    }



    private void drawShape(Canvas canvas, Shape s) {

        setStroke(penStroke);


        if (rop2 == EMFConstants.R2_BLACK) {

            penPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));

            penPaint.setColor(Color.black.getRGB());
        }

        else if (rop2 == EMFConstants.R2_COPYPEN) {

            penPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));

        }

        else if (rop2 == EMFConstants.R2_NOP) {

            penPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));

        }

        else if (rop2 == EMFConstants.R2_WHITE) {

            penPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));

            penPaint.setColor(Color.white.getRGB());
        }

        else if (rop2 == EMFConstants.R2_NOTCOPYPEN) {

            penPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));

        }


        else if (rop2 == EMFConstants.R2_XORPEN) {

            penPaint.setXfermode(new PorterDuffXfermode(Mode.XOR));
        } else {
            logger.warning("got unsupported ROP" + rop2);












        }


        canvas.drawPath(getPath(s), penPaint);
    }









    public void setFont(Font font) {

        if (font == null) {
            return;
        }


        Typeface tf = null;
        String nam = font.getName();
        int sty = font.getStyle();
        String aF = "";
        if (nam != null) {
            if (nam.equalsIgnoreCase("Serif")
                    || nam.equalsIgnoreCase("TimesRoman")) {
                aF = "serif";
            } else if (nam.equalsIgnoreCase("SansSerif")
                    || nam.equalsIgnoreCase("Helvetica")) {
                aF = "sans-serif";
            } else if (nam.equalsIgnoreCase("Monospaced")
                    || nam.equalsIgnoreCase("Courier")) {
                aF = "monospace";
            } else {
                aF = "sans-serif";
            }
        }

        switch (sty) {
            case Font.PLAIN:
                tf = Typeface.create(aF, Typeface.NORMAL);
                break;
            case Font.BOLD:
                tf = Typeface.create(aF, Typeface.BOLD);
                break;
            case Font.ITALIC:
                tf = Typeface.create(aF, Typeface.ITALIC);
                break;
            case Font.BOLD | Font.ITALIC:
                tf = Typeface.create(aF, Typeface.BOLD_ITALIC);
                break;
            default:
                tf = Typeface.DEFAULT;
        }

        penPaint.setTextSize((float) font.getFontSize());
        penPaint.setTypeface(tf);
    }

    public void setEscapement(int escapement) {
        this.escapement = escapement;
    }





    public Matrix getMatrix() {
        return mCanvas.getMatrix();
    }





    public void setMatrix(Matrix matrix) {
        mCanvas.setMatrix(matrix);
    }

    public void transform(AffineTransform transform) {




        Matrix matrix = new Matrix();
        matrix.setValues(createMatrix(transform));
        mCanvas.concat(matrix);
    }

    public void resetTransformation() {

        resetMatrix(mCanvas);
    }

    public void clip(Shape shape) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Rectangle bounds = shape.getBounds();
            Path mPathXOR = new Path();
            mPathXOR.moveTo(0, 0);
            mPathXOR.lineTo(bounds.width, 0);
            mPathXOR.lineTo(bounds.width, bounds.height);
            mPathXOR.lineTo(0, bounds.height);
            mPathXOR.close();

            mPathXOR.op(getPath(shape), Path.Op.XOR);
            mCanvas.clipPath(mPathXOR);
        } else {
            mCanvas.clipPath(getPath(shape), Region.Op.REPLACE);
        }

    }

    public Shape getClip() {

        return mCurrClip;
    }

    public void setClip(Shape shape) {

        mCurrClip = new Area(shape);

    }





    private Shape createShape(int[] arr) {
        Shape s = new GeneralPath();
        for (int i = 0; i < arr.length; i++) {
            int type = arr[i];
            switch (type) {
                case -1:

                    ((GeneralPath) s).moveTo(arr[++i], arr[++i]);
                    break;
                case -2:

                    ((GeneralPath) s).lineTo(arr[++i], arr[++i]);
                    break;
                case -3:

                    ((GeneralPath) s).quadTo(arr[++i], arr[++i], arr[++i],
                            arr[++i]);
                    break;
                case -4:

                    ((GeneralPath) s).curveTo(arr[++i], arr[++i], arr[++i],
                            arr[++i], arr[++i], arr[++i]);
                    break;
                case -5:

                    return s;
                default:
                    break;
            }
        }
        return s;
    }

    public void drawImage(Bitmap bitmap, AffineTransform transform) {

        Matrix matrix = new Matrix();
        matrix.setValues(createMatrix(transform));
        mCanvas.drawBitmap(bitmap, matrix, penPaint);
    }





    public void drawImage(Bitmap bitmap, int x, int y, int width, int height) {


        Rect dst = new Rect(x, y, x + width, y + height);
        mCanvas.drawBitmap(bitmap, null, dst, null);
    }

    public void drawShape(Shape shape) {

        drawShape(mCanvas, shape);
    }

    public void fillShape(Shape shape) {

        Paint.Style tmp = brushPaint.getStyle();
        brushPaint.setStyle(Paint.Style.FILL);
        mCanvas.drawPath(getPath(shape), brushPaint);
        brushPaint.setStyle(tmp);
    }

    public void fillAndDrawShape(Shape shape) {
        Paint.Style tmp = brushPaint.getStyle();
        brushPaint.setStyle(Paint.Style.FILL);
        drawShape(mCanvas, shape);
        brushPaint.setStyle(tmp);
    }

    public void fillAndDrawOrAppend(Shape s) {

        fillAndDrawOrAppend(mCanvas, s);
    }

    public void drawOrAppend(Shape s) {

        drawOrAppend(mCanvas, s);
    }

    public int getWindingRule() {
        return windingRule;
    }





    public void setWindingRule(int windingRule) {
        this.windingRule = windingRule;
    }

    public GeneralPath getFigure() {
        return figure;
    }

    public void setFigure(GeneralPath figure) {
        this.figure = figure;
    }

    public GeneralPath getPath() {
        return path;
    }

    public void setPath(GeneralPath path) {
        this.path = path;
    }

    public Shape getInitialClip() {
        return initialClip;
    }

    public AffineTransform getPathTransform() {
        return pathTransform;
    }

    public void setPathTransform(AffineTransform pathTransform) {
        this.pathTransform = pathTransform;
    }

    public void setMapModeIsotropic(boolean mapModeIsotropic) {
        this.mapModeIsotropic = mapModeIsotropic;
    }

    public AffineTransform getMapModeTransform() {
        return mapModeTransform;
    }

    public void setMapModeTransform(AffineTransform mapModeTransform) {
        this.mapModeTransform = mapModeTransform;
    }

    public void setWindowOrigin(Point windowOrigin) {
        this.windowOrigin = windowOrigin;
        if (windowOrigin != null) {
            mCanvas.translate(-windowOrigin.x, -windowOrigin.y);
        }
    }

    public void setViewportOrigin(Point viewportOrigin) {
        this.viewportOrigin = viewportOrigin;
        if (viewportOrigin != null) {
            mCanvas.translate(-viewportOrigin.x, -viewportOrigin.y);
        }
    }

    public void setViewportSize(Dimension viewportSize) {
        this.viewportSize = viewportSize;
        fixViewportSize();
        resetTransformation();
    }

    public void setWindowSize(Dimension windowSize) {
        this.windowSize = windowSize;
        fixViewportSize();
        resetTransformation();
    }

    public GDIObject getGDIObject(int index) {
        return gdiObjects[index];
    }

    public void storeGDIObject(int index, GDIObject tag) {
        gdiObjects[index] = tag;
    }

    public void setUseCreatePen(boolean useCreatePen) {
        this.useCreatePen = useCreatePen;
    }





    public void setPenPaint(Color color) {
        penPaint.setColor(color.getRGB());
    }

    public Stroke getPenStroke() {
        return penStroke;
    }

    public void setPenStroke(Stroke penStroke) {
        this.penStroke = penStroke;
    }





    public void setBrushPaint(Color color) {
        brushPaint.setColor(color.getRGB());
    }

    public void setBrushPaint(Bitmap bitmap) {




        mCanvas.clipRect(0, 0, 16, 16);
        mCanvas.setBitmap(bitmap);
    }

    public float getMeterLimit() {
        return meterLimit;
    }

    public void setMeterLimit(int meterLimit) {
        this.meterLimit = meterLimit;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public void setTextBkColor() {
        setBrushPaint(textColor);
    }

    public void setRop2(int rop2) {
        this.rop2 = rop2;
    }

    public void setBkMode(int bkMode) {
        this.bkMode = bkMode;
    }

    public int getTextAlignMode() {
        return textAlignMode;
    }

    public void setTextAlignMode(int textAlignMode) {
        this.textAlignMode = textAlignMode;
    }

    public void setScaleMode(int scaleMode) {
        this.scaleMode = scaleMode;
    }

    public Point getBrushOrigin() {
        return brushOrigin;
    }

    public void setBrushOrigin(Point brushOrigin) {
        this.brushOrigin = brushOrigin;
    }

    public int getArcDirection() {
        return arcDirection;
    }

    public void setArcDirection(int arcDirection) {
        this.arcDirection = arcDirection;
    }


    private class DC {
        public GeneralPath path;
        public int bkMode;
        public int windingRule;
        public int meterLimit;
        public boolean useCreatePen;
        public int scaleMode;
        public AffineTransform pathTransform;
        private Paint paint;
        private Stroke stroke;
        private AffineTransform transform;
        private Shape clip;


        private Matrix matrix;
    }
}

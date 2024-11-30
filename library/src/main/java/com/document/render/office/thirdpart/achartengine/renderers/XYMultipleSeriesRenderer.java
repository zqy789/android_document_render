
package com.document.render.office.thirdpart.achartengine.renderers;

import android.graphics.Color;
import android.graphics.Paint.Align;

import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.common.borders.Line;
import com.document.render.office.thirdpart.achartengine.util.MathHelper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class XYMultipleSeriesRenderer extends DefaultRenderer {


    private String mXTitle = "";

    private String[] mYTitle;

    private float mXTitleTextSize = 12;
    private float mYTitleTextSize = 12;

    private double[] mMinX;

    private double[] mMaxX;

    private double[] mMinY;

    private double[] mMaxY;

    private int mXLabels = 5;

    private int mYLabels = 7;

    private Orientation mOrientation = Orientation.HORIZONTAL;

    private Map<Double, String> mXTextLabels = new HashMap<Double, String>();

    private Map<Integer, Map<Double, String>> mYTextLabels = new LinkedHashMap<Integer, Map<Double, String>>();

    private boolean mPanXEnabled = true;

    private boolean mPanYEnabled = true;

    private boolean mZoomXEnabled = true;

    private boolean mZoomYEnabled = true;

    private double mBarSpacing = 0;

    private int mMarginsColor = NO_COLOR;

    private double[] mPanLimits;

    private double[] mZoomLimits;

    private float mXLabelsAngle;

    private float mYLabelsAngle;

    private Map<Integer, double[]> initialRange = new LinkedHashMap<Integer, double[]>();

    private float mPointSize = 5;

    private BackgroundAndFill seriesFill = null;

    private Line seriesFrame = null;

    private int mGridColor = Color.BLACK;

    private int scalesCount;

    private Align xLabelsAlign = Align.CENTER;

    private Align[] yLabelsAlign;

    private Align[] yAxisAlign;

    public XYMultipleSeriesRenderer() {
        this(1);
    }

    public XYMultipleSeriesRenderer(int scaleNumber) {
        scalesCount = scaleNumber;
        initAxesRange(scaleNumber);
    }

    public void initAxesRange(int scales) {
        mYTitle = new String[scales];
        yLabelsAlign = new Align[scales];
        yAxisAlign = new Align[scales];
        mMinX = new double[scales];
        mMaxX = new double[scales];
        mMinY = new double[scales];
        mMaxY = new double[scales];
        for (int i = 0; i < scales; i++) {
            initAxesRangeForScale(i);
        }
    }

    public void initAxesRangeForScale(int i) {
        mMinX[i] = MathHelper.NULL_VALUE;
        mMaxX[i] = -MathHelper.NULL_VALUE;
        mMinY[i] = MathHelper.NULL_VALUE;
        mMaxY[i] = -MathHelper.NULL_VALUE;
        double[] range = new double[]{mMinX[i], mMaxX[i], mMinY[i], mMaxY[i]};
        initialRange.put(i, range);
        mYTitle[i] = "";
        mYTextLabels.put(i, new HashMap<Double, String>());
        yLabelsAlign[i] = Align.CENTER;
        yAxisAlign[i] = Align.LEFT;
    }


    public Orientation getOrientation() {
        return mOrientation;
    }


    public void setOrientation(Orientation orientation) {
        mOrientation = orientation;
    }


    public String getXTitle() {
        return mXTitle;
    }


    public void setXTitle(String title) {
        mXTitle = title;
    }


    public String getYTitle() {
        return getYTitle(0);
    }


    public void setYTitle(String title) {
        setYTitle(title, 0);
    }


    public String getYTitle(int scale) {
        return mYTitle[scale];
    }


    public void setYTitle(String title, int scale) {
        mYTitle[scale] = title;
    }


    public float getXTitleTextSize() {
        return mXTitleTextSize;
    }


    public void setXTitleTextSize(float textSize) {
        mXTitleTextSize = textSize;
    }


    public float getYTitleTextSize() {
        return mYTitleTextSize;
    }


    public void setYTitleTextSize(float textSize) {
        mYTitleTextSize = textSize;
    }


    public double getXAxisMin() {
        return getXAxisMin(0);
    }


    public void setXAxisMin(double min) {
        setXAxisMin(min, 0);
    }


    public boolean isMinXSet() {
        return isMinXSet(0);
    }


    public double getXAxisMax() {
        return getXAxisMax(0);
    }


    public void setXAxisMax(double max) {
        setXAxisMax(max, 0);
    }


    public boolean isMaxXSet() {
        return isMaxXSet(0);
    }


    public double getYAxisMin() {
        return getYAxisMin(0);
    }


    public void setYAxisMin(double min) {
        setYAxisMin(min, 0);
    }


    public boolean isMinYSet() {
        return isMinYSet(0);
    }


    public double getYAxisMax() {
        return getYAxisMax(0);
    }


    public void setYAxisMax(double max) {
        setYAxisMax(max, 0);
    }


    public boolean isMaxYSet() {
        return isMaxYSet(0);
    }


    public double getXAxisMin(int scale) {
        return mMinX[scale];
    }


    public void setXAxisMin(double min, int scale) {
        if (!isMinXSet(scale)) {
            initialRange.get(scale)[0] = min;
        }
        mMinX[scale] = min;
    }


    public boolean isMinXSet(int scale) {
        return mMinX[scale] != MathHelper.NULL_VALUE;
    }


    public double getXAxisMax(int scale) {
        return mMaxX[scale];
    }


    public void setXAxisMax(double max, int scale) {
        if (!isMaxXSet(scale)) {
            initialRange.get(scale)[1] = max;
        }
        mMaxX[scale] = max;
    }


    public boolean isMaxXSet(int scale) {
        return mMaxX[scale] != -MathHelper.NULL_VALUE;
    }


    public double getYAxisMin(int scale) {
        return mMinY[scale];
    }


    public void setYAxisMin(double min, int scale) {
        if (!isMinYSet(scale)) {
            initialRange.get(scale)[2] = min;
        }
        mMinY[scale] = min;
    }


    public boolean isMinYSet(int scale) {
        return mMinY[scale] != MathHelper.NULL_VALUE;
    }


    public double getYAxisMax(int scale) {
        return mMaxY[scale];
    }


    public void setYAxisMax(double max, int scale) {
        if (!isMaxYSet(scale)) {
            initialRange.get(scale)[3] = max;
        }
        mMaxY[scale] = max;
    }


    public boolean isMaxYSet(int scale) {
        return mMaxY[scale] != -MathHelper.NULL_VALUE;
    }


    public int getXLabels() {
        return mXLabels;
    }


    public void setXLabels(int xLabels) {
        mXLabels = xLabels;
    }

    public int getXTextLabels() {
        return mXTextLabels.size();
    }


    public void addTextLabel(double x, String text) {
        addXTextLabel(x, text);
    }


    public void addXTextLabel(double x, String text) {
        mXTextLabels.put(x, text);
    }


    public String getXTextLabel(Double x) {
        return mXTextLabels.get(x);
    }


    public Double[] getXTextLabelLocations() {
        return mXTextLabels.keySet().toArray(new Double[0]);
    }


    public void clearTextLabels() {
        clearXTextLabels();
    }


    public void clearXTextLabels() {
        mXTextLabels.clear();
    }


    public void addYTextLabel(double y, String text) {
        addYTextLabel(y, text, 0);
    }


    public void addYTextLabel(double y, String text, int scale) {
        mYTextLabels.get(scale).put(y, text);
    }


    public String getYTextLabel(Double y) {
        return getYTextLabel(y, 0);
    }


    public String getYTextLabel(Double y, int scale) {
        return mYTextLabels.get(scale).get(y);
    }


    public Double[] getYTextLabelLocations() {
        return getYTextLabelLocations(0);
    }


    public Double[] getYTextLabelLocations(int scale) {
        return mYTextLabels.get(scale).keySet().toArray(new Double[0]);
    }


    public void clearYTextLabels() {
        mYTextLabels.clear();
    }


    public int getYLabels() {
        return mYLabels;
    }


    public void setYLabels(int yLabels) {
        mYLabels = yLabels;
    }


    public void setDisplayChartValues(boolean display) {
        SimpleSeriesRenderer[] renderers = getSeriesRenderers();
        for (SimpleSeriesRenderer renderer : renderers) {
            renderer.setDisplayChartValues(display);
        }
    }


    public void setChartValuesTextSize(float textSize) {
        SimpleSeriesRenderer[] renderers = getSeriesRenderers();
        for (SimpleSeriesRenderer renderer : renderers) {
            renderer.setChartValuesTextSize(textSize);
        }
    }


    public boolean isPanEnabled() {
        return isPanXEnabled() && isPanYEnabled();
    }


    public boolean isPanXEnabled() {
        return mPanXEnabled;
    }


    public boolean isPanYEnabled() {
        return mPanYEnabled;
    }


    public void setPanEnabled(boolean enabledX, boolean enabledY) {
        mPanXEnabled = enabledX;
        mPanYEnabled = enabledY;
    }


    public boolean isZoomEnabled() {
        return isZoomXEnabled() || isZoomYEnabled();
    }


    public boolean isZoomXEnabled() {
        return mZoomXEnabled;
    }


    public boolean isZoomYEnabled() {
        return mZoomYEnabled;
    }


    public void setZoomEnabled(boolean enabledX, boolean enabledY) {
        mZoomXEnabled = enabledX;
        mZoomYEnabled = enabledY;
    }


    public double getBarsSpacing() {
        return getBarSpacing();
    }


    public double getBarSpacing() {
        return mBarSpacing;
    }


    public void setBarSpacing(double spacing) {
        mBarSpacing = spacing;
    }


    public int getMarginsColor() {
        return mMarginsColor;
    }


    public void setMarginsColor(int color) {
        mMarginsColor = color;
    }


    public BackgroundAndFill getSeriesBackgroundColor() {
        return seriesFill;
    }


    public void setSeriesBackgroundColor(BackgroundAndFill fill) {
        seriesFill = fill;
    }


    public Line getSeriesFrame() {
        return seriesFrame;
    }


    public void setSeriesFrame(Line seriesFrame) {
        this.seriesFrame = seriesFrame;
    }


    public int getGridColor() {
        return mGridColor;
    }


    public void setGridColor(int color) {
        mGridColor = color;
    }


    public double[] getPanLimits() {
        return mPanLimits;
    }


    public void setPanLimits(double[] panLimits) {
        mPanLimits = panLimits;
    }


    public double[] getZoomLimits() {
        return mZoomLimits;
    }


    public void setZoomLimits(double[] zoomLimits) {
        mZoomLimits = zoomLimits;
    }


    public float getXLabelsAngle() {
        return mXLabelsAngle;
    }


    public void setXLabelsAngle(float angle) {
        mXLabelsAngle = angle;
    }


    public float getYLabelsAngle() {
        return mYLabelsAngle;
    }


    public void setYLabelsAngle(float angle) {
        mYLabelsAngle = angle;
    }


    public float getPointSize() {
        return mPointSize;
    }


    public void setPointSize(float size) {
        mPointSize = size;
    }

    public void setRange(double[] range) {
        setRange(range, 0);
    }


    public void setRange(double[] range, int scale) {
        setXAxisMin(range[0], scale);
        setXAxisMax(range[1], scale);
        setYAxisMin(range[2], scale);
        setYAxisMax(range[3], scale);
    }

    public boolean isInitialRangeSet() {
        return isInitialRangeSet(0);
    }


    public boolean isInitialRangeSet(int scale) {
        return initialRange.get(scale) != null;
    }


    public double[] getInitialRange() {
        return getInitialRange(0);
    }


    public void setInitialRange(double[] range) {
        setInitialRange(range, 0);
    }


    public double[] getInitialRange(int scale) {
        return initialRange.get(scale);
    }


    public void setInitialRange(double[] range, int scale) {
        initialRange.put(scale, range);
    }


    public Align getXLabelsAlign() {
        return xLabelsAlign;
    }


    public void setXLabelsAlign(Align align) {
        xLabelsAlign = align;
    }


    public Align getYLabelsAlign(int scale) {
        return yLabelsAlign[scale];
    }

    public void setYLabelsAlign(Align align) {
        setYLabelsAlign(align, 0);
    }

    public Align getYAxisAlign(int scale) {
        return yAxisAlign[scale];
    }

    public void setYAxisAlign(Align align, int scale) {
        yAxisAlign[scale] = align;
    }


    public void setYLabelsAlign(Align align, int scale) {
        yLabelsAlign[scale] = align;
    }

    public int getScalesCount() {
        return scalesCount;
    }


    public enum Orientation {
        HORIZONTAL(0), VERTICAL(90);

        private int mAngle = 0;

        private Orientation(int angle) {
            mAngle = angle;
        }


        public int getAngle() {
            return mAngle;
        }
    }

}


package com.document.render.office.thirdpart.achartengine.renderers;

import android.graphics.Color;
import android.graphics.Typeface;

import com.document.render.office.common.bg.BackgroundAndFill;
import com.document.render.office.common.borders.Line;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class DefaultRenderer implements Serializable {

    public static final int NO_COLOR = 0;

    public static final int BACKGROUND_COLOR = Color.BLACK;

    public static final int TEXT_COLOR = Color.BLACK;

    private static final Typeface REGULAR_TEXT_FONT = Typeface
            .create(Typeface.SERIF, Typeface.NORMAL);

    private float defaultFontSize = 12;


    private String textTypefaceName = REGULAR_TEXT_FONT.toString();

    private int textTypefaceStyle = Typeface.NORMAL;

    private BackgroundAndFill chartFill = null;

    private Line frame = null;

    private boolean mApplyBackgroundColor = true;

    private boolean mShowChartTitle = true;

    private float mChartTitleTextSize = 15;

    private String mChartTitle = "";

    private boolean mShowAxes = true;

    private int mAxesColor = Color.BLACK;

    private boolean mShowLabels = true;

    private int mLabelsColor = TEXT_COLOR;

    private float mLabelsTextSize = 10;

    private boolean mShowLegend = true;

    private float mLegendTextSize = 12;

    private boolean mFitLegend = false;

    private boolean mXShowGrid = false;
    private boolean mYShowGrid = false;

    private boolean mShowCustomTextGrid = false;

    private List<SimpleSeriesRenderer> mRenderers = new ArrayList<SimpleSeriesRenderer>();

    private boolean antialiasing = true;

    private int mLegendHeight = 0;

    private double[] mMargins = new double[]{0.1, 0.05, 0.1, 0.05};

    private float mScale = 1;

    private boolean mZoomEnabled = true;

    private boolean mZoomButtonsVisible = false;

    private float mZoomRate = 1.0f;

    private float mOriginalScale = mScale;


    public void addSeriesRenderer(SimpleSeriesRenderer renderer) {
        mRenderers.add(renderer);
    }


    public void addSeriesRenderer(int index, SimpleSeriesRenderer renderer) {
        mRenderers.add(index, renderer);
    }


    public void removeSeriesRenderer(SimpleSeriesRenderer renderer) {
        mRenderers.remove(renderer);
    }


    public SimpleSeriesRenderer getSeriesRendererAt(int index) {
        return mRenderers.get(index);
    }


    public int getSeriesRendererCount() {
        return mRenderers.size();
    }


    public SimpleSeriesRenderer[] getSeriesRenderers() {
        return mRenderers.toArray(new SimpleSeriesRenderer[0]);
    }

    public int getBackgroundColor() {
        return Color.BLACK;
    }

    public void setBackgroundColor(int color) {

    }


    public BackgroundAndFill getBackgroundAndFill() {
        return chartFill;
    }


    public void setBackgroundAndFill(BackgroundAndFill fill) {
        chartFill = fill;
    }


    public Line getChartFrame() {
        return frame;
    }


    public void setChartFrame(Line frame) {
        this.frame = frame;
    }

    public float getDefaultFontSize() {
        return defaultFontSize;
    }

    public void setDefaultFontSize(float fontSize) {
        this.defaultFontSize = fontSize;
    }

    public boolean isShowChartTitle() {
        return mShowChartTitle;
    }

    public void setShowChartTitle(boolean showChartTitle) {
        this.mShowChartTitle = showChartTitle;
    }


    public float getChartTitleTextSize() {
        return mChartTitleTextSize;
    }


    public void setChartTitleTextSize(float textSize) {
        mChartTitleTextSize = textSize;
    }


    public String getChartTitle() {
        return mChartTitle;
    }


    public void setChartTitle(String title) {
        mChartTitle = title;
    }


    public boolean isApplyBackgroundColor() {
        return mApplyBackgroundColor;
    }


    public void setApplyBackgroundColor(boolean apply) {
        mApplyBackgroundColor = apply;
    }


    public int getAxesColor() {
        return mAxesColor;
    }


    public void setAxesColor(int color) {
        mAxesColor = color;
    }


    public int getLabelsColor() {
        return mLabelsColor;
    }


    public void setLabelsColor(int color) {
        mLabelsColor = color;
    }


    public float getLabelsTextSize() {
        return mLabelsTextSize;
    }


    public void setLabelsTextSize(float textSize) {
        mLabelsTextSize = textSize;
    }


    public boolean isShowAxes() {
        return mShowAxes;
    }


    public void setShowAxes(boolean showAxes) {
        mShowAxes = showAxes;
    }


    public boolean isShowLabels() {
        return mShowLabels;
    }


    public void setShowLabels(boolean showLabels) {
        mShowLabels = showLabels;
    }


    public boolean isShowGridH() {
        return mXShowGrid;
    }


    public void setShowGridH(boolean showGrid) {
        mXShowGrid = showGrid;
    }


    public boolean isShowGridV() {
        return mYShowGrid;
    }


    public void setShowGridV(boolean showGrid) {
        mYShowGrid = showGrid;
    }


    public boolean isShowCustomTextGrid() {
        return mShowCustomTextGrid;
    }


    public void setShowCustomTextGrid(boolean showGrid) {
        mShowCustomTextGrid = showGrid;
    }


    public boolean isShowLegend() {
        return mShowLegend;
    }


    public void setShowLegend(boolean showLegend) {
        mShowLegend = showLegend;
    }


    public boolean isFitLegend() {
        return mFitLegend;
    }


    public void setFitLegend(boolean fit) {
        mFitLegend = fit;
    }


    public String getTextTypefaceName() {
        return textTypefaceName;
    }


    public int getTextTypefaceStyle() {
        return textTypefaceStyle;
    }


    public float getLegendTextSize() {
        return mLegendTextSize;
    }


    public void setLegendTextSize(float textSize) {
        mLegendTextSize = textSize;
    }


    public void setTextTypeface(String typefaceName, int style) {
        textTypefaceName = typefaceName;
        textTypefaceStyle = style;
    }


    public boolean isAntialiasing() {
        return antialiasing;
    }


    public void setAntialiasing(boolean antialiasing) {
        this.antialiasing = antialiasing;
    }


    public float getScale() {
        return mScale;
    }


    public void setScale(float scale) {
        if (mOriginalScale == 1) {
            mOriginalScale = scale;
        }
        mScale = scale;
    }


    public float getOriginalScale() {
        return mOriginalScale;
    }


    public boolean isZoomEnabled() {
        return mZoomEnabled;
    }


    public void setZoomEnabled(boolean enabled) {
        mZoomEnabled = enabled;
    }


    public boolean isZoomButtonsVisible() {
        return mZoomButtonsVisible;
    }


    public void setZoomButtonsVisible(boolean visible) {
        mZoomButtonsVisible = visible;
    }


    public float getZoomRate() {
        return mZoomRate;
    }


    public void setZoomRate(float rate) {
        mZoomRate = rate;
    }


    public boolean isPanEnabled() {
        return false;
    }


    public int getLegendHeight() {
        return mLegendHeight;
    }


    public void setLegendHeight(int height) {
        mLegendHeight = height;
    }


    public double[] getMargins() {
        return mMargins;
    }


    public void setMargins(double[] margins) {
        mMargins = margins;
    }

}

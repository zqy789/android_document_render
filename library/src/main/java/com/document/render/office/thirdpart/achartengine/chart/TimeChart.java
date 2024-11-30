
package com.document.render.office.thirdpart.achartengine.chart;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.document.render.office.thirdpart.achartengine.model.XYMultipleSeriesDataset;
import com.document.render.office.thirdpart.achartengine.renderers.XYMultipleSeriesRenderer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class TimeChart extends LineChart {

    public static final String TYPE = "Time";

    public static final long DAY = 24 * 60 * 60 * 1000;

    private String mDateFormat;

    TimeChart() {
    }


    public TimeChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer) {
        super(dataset, renderer);
    }


    public String getDateFormat() {
        return mDateFormat;
    }


    public void setDateFormat(String format) {
        mDateFormat = format;
    }


    @Override
    protected void drawXLabels(List<Double> xLabels, Double[] xTextLabelLocations, Canvas canvas,
                               Paint paint, int left, int top, float bottom, double xPixelsPerUnit, double minX) {
        int length = xLabels.size();
        if (length > 0) {
            boolean showLabels = mRenderer.isShowLabels();
            boolean showGrid = mRenderer.isShowGridH();
            DateFormat format = getDateFormat(xLabels.get(0), xLabels.get(length - 1));
            for (int i = 0; i < length; i++) {
                long label = Math.round(xLabels.get(i));
                float xLabel = (float) (left + xPixelsPerUnit * (label - minX));
                if (showLabels) {
                    paint.setColor(mRenderer.getLabelsColor());
                    canvas
                            .drawLine(xLabel, bottom, xLabel, bottom + mRenderer.getLabelsTextSize() / 3, paint);
                    drawText(canvas, format.format(new Date(label)), xLabel, bottom
                            + mRenderer.getLabelsTextSize() * 4 / 3, paint, mRenderer.getXLabelsAngle());
                }
                if (showGrid) {
                    paint.setColor(mRenderer.getGridColor());
                    canvas.drawLine(xLabel, bottom, xLabel, top, paint);
                }
            }
        }
    }


    private DateFormat getDateFormat(double start, double end) {
        if (mDateFormat != null) {
            SimpleDateFormat format = null;
            try {
                format = new SimpleDateFormat(mDateFormat);
                return format;
            } catch (Exception e) {

            }
        }
        DateFormat format = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM);
        double diff = end - start;
        if (diff > DAY && diff < 5 * DAY) {
            format = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
        } else if (diff < DAY) {
            format = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM);
        }
        return format;
    }


    public String getChartType() {
        return TYPE;
    }

}


package com.document.render.office.thirdpart.achartengine.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class MathHelper {

    public static final double NULL_VALUE = Double.MAX_VALUE;

    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance();

    private MathHelper() {

    }


    public static double[] minmax(List<Double> values) {
        if (values.size() == 0) {
            return new double[2];
        }
        double min = values.get(0);
        double max = min;
        int length = values.size();
        for (int i = 1; i < length; i++) {
            double value = values.get(i);
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        return new double[]{min, max};
    }


    public static List<Double> getLabels(final double start, final double end,
                                         final int approxNumLabels) {
        FORMAT.setMaximumFractionDigits(5);
        List<Double> labels = new ArrayList<Double>();
        double[] labelParams = computeLabels(start, end, approxNumLabels);

        int numLabels = 1 + (int) ((labelParams[1] - labelParams[0]) / labelParams[2]);




        for (int i = 0; i < numLabels; i++) {
            double z = labelParams[0] + i * labelParams[2];
            try {


                z = FORMAT.parse(FORMAT.format(z)).doubleValue();
            } catch (ParseException e) {

            }
            labels.add(z);
        }
        return labels;
    }


    private static double[] computeLabels(final double start, final double end,
                                          final int approxNumLabels) {
        double s = start;
        double e = end;
        if (Math.abs(start - end) < 0.0000001f) {
            double xStep = roundUp(s / approxNumLabels);

            double xEnd = xStep * Math.ceil(e / xStep);

            return new double[]{xStep, xEnd, xStep};
        }

        boolean switched = false;
        if (s > e) {
            switched = true;
            double tmp = s;
            s = e;
            e = tmp;
        }
        double xStep = roundUp(Math.abs(s - e) / approxNumLabels);

        double xStart = xStep * Math.floor(s / xStep);
        double xEnd = xStep * Math.ceil(e / xStep);
        if (switched) {
            return new double[]{xEnd, xStart, -1.0 * xStep};
        }
        return new double[]{xStart, xEnd, xStep};
    }


    private static double roundUp(final double val) {
        int exponent = (int) Math.floor(Math.log10(val));
        double rval = val * Math.pow(10, -exponent);
        if (rval > 5.0) {
            rval = 10.0;
        } else if (rval > 2.0) {
            rval = 5.0;
        } else if (rval > 1.0) {
            rval = 2.0;
        }
        rval *= Math.pow(10, exponent);
        return rval;
    }


    public static float[] getFloats(List<Float> values) {
        int length = values.size();
        float[] result = new float[length];
        for (int i = 0; i < length; i++) {
            result[i] = values.get(i).floatValue();
        }
        return result;
    }

}

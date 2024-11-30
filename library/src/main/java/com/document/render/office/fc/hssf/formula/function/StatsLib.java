

package com.document.render.office.fc.hssf.formula.function;

import java.util.Arrays;


final class StatsLib {

    private StatsLib() {

    }



    public static double avedev(double[] v) {
        double r = 0;
        double m = 0;
        double s = 0;
        for (int i = 0, iSize = v.length; i < iSize; i++) {
            s += v[i];
        }
        m = s / v.length;
        s = 0;
        for (int i = 0, iSize = v.length; i < iSize; i++) {
            s += Math.abs(v[i] - m);
        }
        r = s / v.length;
        return r;
    }

    public static double stdev(double[] v) {
        double r = Double.NaN;
        if (v != null && v.length > 1) {
            r = Math.sqrt(devsq(v) / (v.length - 1));
        }
        return r;
    }

    public static double var(double[] v) {
        double r = Double.NaN;
        if (v != null && v.length > 1) {
            r = devsq(v) / (v.length - 1);
        }
        return r;
    }

    public static double varp(double[] v) {
        double r = Double.NaN;
        if (v != null && v.length > 1) {
            r = devsq(v) / v.length;
        }
        return r;
    }

    public static double median(double[] v) {
        double r = Double.NaN;

        if (v != null && v.length >= 1) {
            int n = v.length;
            Arrays.sort(v);
            r = (n % 2 == 0)
                    ? (v[n / 2] + v[n / 2 - 1]) / 2
                    : v[n / 2];
        }

        return r;
    }


    public static double devsq(double[] v) {
        double r = Double.NaN;
        if (v != null && v.length >= 1) {
            double m = 0;
            double s = 0;
            int n = v.length;
            for (int i = 0; i < n; i++) {
                s += v[i];
            }
            m = s / n;
            s = 0;
            for (int i = 0; i < n; i++) {
                s += (v[i] - m) * (v[i] - m);
            }

            r = (n == 1)
                    ? 0
                    : s;
        }
        return r;
    }


    public static double kthLargest(double[] v, int k) {
        double r = Double.NaN;
        int index = k - 1;
        if (v != null && v.length > index && index >= 0) {
            Arrays.sort(v);
            r = v[v.length - index - 1];
        }
        return r;
    }


    public static double kthSmallest(double[] v, int k) {
        double r = Double.NaN;
        int index = k - 1;
        if (v != null && v.length > index && index >= 0) {
            Arrays.sort(v);
            r = v[index];
        }
        return r;
    }
}

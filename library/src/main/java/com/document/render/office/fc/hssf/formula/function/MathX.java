

package com.document.render.office.fc.hssf.formula.function;



final class MathX {

    private MathX() {

    }



    public static double round(double n, int p) {
        double retval;

        if (Double.isNaN(n) || Double.isInfinite(n)) {
            retval = Double.NaN;
        } else {
            retval = java.math.BigDecimal.valueOf(n).setScale(p, java.math.RoundingMode.HALF_UP).doubleValue();
        }

        return retval;
    }


    public static double roundUp(double n, int p) {
        double retval;

        if (Double.isNaN(n) || Double.isInfinite(n)) {
            retval = Double.NaN;
        } else {
            retval = java.math.BigDecimal.valueOf(n).setScale(p, java.math.RoundingMode.UP).doubleValue();
        }

        return retval;
    }


    public static double roundDown(double n, int p) {
        double retval;

        if (Double.isNaN(n) || Double.isInfinite(n)) {
            retval = Double.NaN;
        } else {
            retval = java.math.BigDecimal.valueOf(n).setScale(p, java.math.RoundingMode.DOWN).doubleValue();
        }

        return retval;
    }



    public static short sign(double d) {
        return (short) ((d == 0)
                ? 0
                : (d < 0)
                ? -1
                : 1);
    }


    public static double average(double[] values) {
        double ave = 0;
        double sum = 0;
        for (int i = 0, iSize = values.length; i < iSize; i++) {
            sum += values[i];
        }
        ave = sum / values.length;
        return ave;
    }



    public static double sum(double[] values) {
        double sum = 0;
        for (int i = 0, iSize = values.length; i < iSize; i++) {
            sum += values[i];
        }
        return sum;
    }


    public static double sumsq(double[] values) {
        double sumsq = 0;
        for (int i = 0, iSize = values.length; i < iSize; i++) {
            sumsq += values[i] * values[i];
        }
        return sumsq;
    }



    public static double product(double[] values) {
        double product = 0;
        if (values != null && values.length > 0) {
            product = 1;
            for (int i = 0, iSize = values.length; i < iSize; i++) {
                product *= values[i];
            }
        }
        return product;
    }


    public static double min(double[] values) {
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0, iSize = values.length; i < iSize; i++) {
            min = Math.min(min, values[i]);
        }
        return min;
    }


    public static double max(double[] values) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0, iSize = values.length; i < iSize; i++) {
            max = Math.max(max, values[i]);
        }
        return max;
    }


    public static double floor(double n, double s) {
        double f;

        if ((n < 0 && s > 0) || (n > 0 && s < 0) || (s == 0 && n != 0)) {
            f = Double.NaN;
        } else {
            f = (n == 0 || s == 0) ? 0 : Math.floor(n / s) * s;
        }

        return f;
    }


    public static double ceiling(double n, double s) {
        double c;

        if ((n < 0 && s > 0) || (n > 0 && s < 0)) {
            c = Double.NaN;
        } else {
            c = (n == 0 || s == 0) ? 0 : Math.ceil(n / s) * s;
        }

        return c;
    }


    public static double factorial(int n) {
        double d = 1;

        if (n >= 0) {
            if (n <= 170) {
                for (int i = 1; i <= n; i++) {
                    d *= i;
                }
            } else {
                d = Double.POSITIVE_INFINITY;
            }
        } else {
            d = Double.NaN;
        }
        return d;
    }



    public static double mod(double n, double d) {
        double result = 0;

        if (d == 0) {
            result = Double.NaN;
        } else if (sign(n) == sign(d)) {
            result = n % d;
        } else {
            result = ((n % d) + d) % d;
        }

        return result;
    }


    public static double acosh(double d) {
        return Math.log(Math.sqrt(Math.pow(d, 2) - 1) + d);
    }


    public static double asinh(double d) {
        return Math.log(Math.sqrt(d * d + 1) + d);
    }


    public static double atanh(double d) {
        return Math.log((1 + d) / (1 - d)) / 2;
    }


    public static double cosh(double d) {
        double ePowX = Math.pow(Math.E, d);
        double ePowNegX = Math.pow(Math.E, -d);
        return (ePowX + ePowNegX) / 2;
    }


    public static double sinh(double d) {
        double ePowX = Math.pow(Math.E, d);
        double ePowNegX = Math.pow(Math.E, -d);
        return (ePowX - ePowNegX) / 2;
    }


    public static double tanh(double d) {
        double ePowX = Math.pow(Math.E, d);
        double ePowNegX = Math.pow(Math.E, -d);
        return (ePowX - ePowNegX) / (ePowX + ePowNegX);
    }



    public static double nChooseK(int n, int k) {
        double d = 1;
        if (n < 0 || k < 0 || n < k) {
            d = Double.NaN;
        } else {
            int minnk = Math.min(n - k, k);
            int maxnk = Math.max(n - k, k);
            for (int i = maxnk; i < n; i++) {
                d *= i + 1;
            }
            d /= factorial(minnk);
        }

        return d;
    }

}

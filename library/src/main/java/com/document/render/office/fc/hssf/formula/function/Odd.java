

package com.document.render.office.fc.hssf.formula.function;


public final class Odd extends NumericFunction.OneArg {
    private static final long PARITY_MASK = 0xFFFFFFFFFFFFFFFEL;

    private static long calcOdd(double d) {
        double dpm1 = d + 1;
        long x = ((long) dpm1) & PARITY_MASK;
        if (x == dpm1) {
            return x - 1;
        }
        return x + 1;
    }

    protected double evaluate(double d) {
        if (d == 0) {
            return 1;
        }
        if (d > 0) {
            return calcOdd(d);
        }
        return -calcOdd(-d);
    }
}

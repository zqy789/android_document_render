

package com.document.render.office.fc.hssf.formula.function;



public final class Even extends NumericFunction.OneArg {

    private static final long PARITY_MASK = 0xFFFFFFFFFFFFFFFEL;

    private static long calcEven(double d) {
        long x = ((long) d) & PARITY_MASK;
        if (x == d) {
            return x;
        }
        return x + 2;
    }

    protected double evaluate(double d) {
        if (d == 0) {
            return 0;
        }
        long result;
        if (d > 0) {
            result = calcEven(d);
        } else {
            result = -calcEven(-d);
        }
        return result;
    }
}



package com.document.render.office.fc.hssf.formula.function;


public final class Sumxmy2 extends XYNumericFunction {

    private static final Accumulator XMinusYSquaredAccumulator = new Accumulator() {
        public double accumulate(double x, double y) {
            double xmy = x - y;
            return xmy * xmy;
        }
    };

    protected Accumulator createAccumulator() {
        return XMinusYSquaredAccumulator;
    }
}

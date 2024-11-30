

package com.document.render.office.fc.hssf.formula.function;



public final class Sumx2my2 extends XYNumericFunction {

    private static final Accumulator XSquaredMinusYSquaredAccumulator = new Accumulator() {
        public double accumulate(double x, double y) {
            return x * x - y * y;
        }
    };

    protected Accumulator createAccumulator() {
        return XSquaredMinusYSquaredAccumulator;
    }
}

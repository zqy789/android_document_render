

package com.document.render.office.fc.hssf.formula.function;



public final class Sumx2py2 extends XYNumericFunction {

    private static final Accumulator XSquaredPlusYSquaredAccumulator = new Accumulator() {
        public double accumulate(double x, double y) {
            return x * x + y * y;
        }
    };

    protected Accumulator createAccumulator() {
        return XSquaredPlusYSquaredAccumulator;
    }
}

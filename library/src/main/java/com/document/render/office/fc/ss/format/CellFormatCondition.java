
package com.document.render.office.fc.ss.format;

import java.util.HashMap;
import java.util.Map;


public abstract class CellFormatCondition {
    private static final int LT = 0;
    private static final int LE = 1;
    private static final int GT = 2;
    private static final int GE = 3;
    private static final int EQ = 4;
    private static final int NE = 5;

    private static final Map<String, Integer> TESTS;

    static {
        TESTS = new HashMap<String, Integer>();
        TESTS.put("<", LT);
        TESTS.put("<=", LE);
        TESTS.put(">", GT);
        TESTS.put(">=", GE);
        TESTS.put("=", EQ);
        TESTS.put("==", EQ);
        TESTS.put("!=", NE);
        TESTS.put("<>", NE);
    }


    public static CellFormatCondition getInstance(String opString,
                                                  String constStr) {

        if (!TESTS.containsKey(opString))
            throw new IllegalArgumentException("Unknown test: " + opString);
        int test = TESTS.get(opString);

        final double c = Double.parseDouble(constStr);

        switch (test) {
            case LT:
                return new CellFormatCondition() {
                    public boolean pass(double value) {
                        return value < c;
                    }
                };
            case LE:
                return new CellFormatCondition() {
                    public boolean pass(double value) {
                        return value <= c;
                    }
                };
            case GT:
                return new CellFormatCondition() {
                    public boolean pass(double value) {
                        return value > c;
                    }
                };
            case GE:
                return new CellFormatCondition() {
                    public boolean pass(double value) {
                        return value >= c;
                    }
                };
            case EQ:
                return new CellFormatCondition() {
                    public boolean pass(double value) {
                        return value == c;
                    }
                };
            case NE:
                return new CellFormatCondition() {
                    public boolean pass(double value) {
                        return value != c;
                    }
                };
            default:
                throw new IllegalArgumentException(
                        "Cannot create for test number " + test + "(\"" + opString +
                                "\")");
        }
    }


    public abstract boolean pass(double value);
}

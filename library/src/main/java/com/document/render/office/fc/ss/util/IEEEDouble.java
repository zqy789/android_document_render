

package com.document.render.office.fc.ss.util;



final class IEEEDouble {
    public static final long FRAC_MASK = 0x000FFFFFFFFFFFFFL;
    public static final int EXPONENT_BIAS = 1023;

    public static final int BIASED_EXPONENT_SPECIAL_VALUE = 0x07FF;
    private static final long EXPONENT_MASK = 0x7FF0000000000000L;
    private static final int EXPONENT_SHIFT = 52;
    public static final long FRAC_ASSUMED_HIGH_BIT = (1L << EXPONENT_SHIFT);


    public static int getBiasedExponent(long rawBits) {
        return (int) ((rawBits & EXPONENT_MASK) >> EXPONENT_SHIFT);
    }
}

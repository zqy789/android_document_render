

package com.document.render.office.fc.ss.util;

import static com.document.render.office.fc.ss.util.IEEEDouble.BIASED_EXPONENT_SPECIAL_VALUE;
import static com.document.render.office.fc.ss.util.IEEEDouble.EXPONENT_BIAS;
import static com.document.render.office.fc.ss.util.IEEEDouble.FRAC_MASK;
import static com.document.render.office.fc.ss.util.IEEEDouble.getBiasedExponent;


public final class NumberComparer {


    public static int compare(double a, double b) {
        long rawBitsA = Double.doubleToLongBits(a);
        long rawBitsB = Double.doubleToLongBits(b);

        int biasedExponentA = getBiasedExponent(rawBitsA);
        int biasedExponentB = getBiasedExponent(rawBitsB);

        if (biasedExponentA == BIASED_EXPONENT_SPECIAL_VALUE) {
            throw new IllegalArgumentException("Special double values are not allowed: " + toHex(a));
        }
        if (biasedExponentB == BIASED_EXPONENT_SPECIAL_VALUE) {
            throw new IllegalArgumentException("Special double values are not allowed: " + toHex(a));
        }

        int cmp;


        boolean aIsNegative = rawBitsA < 0;
        boolean bIsNegative = rawBitsB < 0;


        if (aIsNegative != bIsNegative) {


            return aIsNegative ? -1 : +1;
        }


        cmp = biasedExponentA - biasedExponentB;
        int absExpDiff = Math.abs(cmp);
        if (absExpDiff > 1) {
            return aIsNegative ? -cmp : cmp;
        }

        if (absExpDiff == 1) {


        } else {

            if (rawBitsA == rawBitsB) {

                return 0;
            }
        }
        if (biasedExponentA == 0) {
            if (biasedExponentB == 0) {
                return compareSubnormalNumbers(rawBitsA & FRAC_MASK, rawBitsB & FRAC_MASK, aIsNegative);
            }

            return -compareAcrossSubnormalThreshold(rawBitsB, rawBitsA, aIsNegative);
        }
        if (biasedExponentB == 0) {

            return +compareAcrossSubnormalThreshold(rawBitsA, rawBitsB, aIsNegative);
        }



        ExpandedDouble edA = ExpandedDouble.fromRawBitsAndExponent(rawBitsA, biasedExponentA - EXPONENT_BIAS);
        ExpandedDouble edB = ExpandedDouble.fromRawBitsAndExponent(rawBitsB, biasedExponentB - EXPONENT_BIAS);
        NormalisedDecimal ndA = edA.normaliseBaseTen().roundUnits();
        NormalisedDecimal ndB = edB.normaliseBaseTen().roundUnits();
        cmp = ndA.compareNormalised(ndB);
        if (aIsNegative) {
            return -cmp;
        }
        return cmp;
    }


    private static int compareSubnormalNumbers(long fracA, long fracB, boolean isNegative) {
        int cmp = fracA > fracB ? +1 : fracA < fracB ? -1 : 0;

        return isNegative ? -cmp : cmp;
    }



    private static int compareAcrossSubnormalThreshold(long normalRawBitsA, long subnormalRawBitsB, boolean isNegative) {
        long fracB = subnormalRawBitsB & FRAC_MASK;
        if (fracB == 0) {

            return isNegative ? -1 : +1;
        }
        long fracA = normalRawBitsA & FRAC_MASK;
        if (fracA <= 0x0000000000000007L && fracB >= 0x000FFFFFFFFFFFFAL) {

            if (fracA == 0x0000000000000007L && fracB == 0x000FFFFFFFFFFFFAL) {

                return 0;
            }

            return isNegative ? +1 : -1;
        }

        return isNegative ? -1 : +1;
    }



    private static String toHex(double a) {
        return "0x" + Long.toHexString(Double.doubleToLongBits(a)).toUpperCase();
    }
}

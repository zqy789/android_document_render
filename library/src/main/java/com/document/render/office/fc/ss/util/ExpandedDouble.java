

package com.document.render.office.fc.ss.util;

import static com.document.render.office.fc.ss.util.IEEEDouble.FRAC_ASSUMED_HIGH_BIT;
import static com.document.render.office.fc.ss.util.IEEEDouble.FRAC_MASK;

import java.math.BigInteger;


final class ExpandedDouble {
    private static final BigInteger BI_FRAC_MASK = BigInteger.valueOf(FRAC_MASK);
    private static final BigInteger BI_IMPLIED_FRAC_MSB = BigInteger.valueOf(FRAC_ASSUMED_HIGH_BIT);

    private final BigInteger _significand;
    private final int _binaryExponent;

    public ExpandedDouble(long rawBits) {
        int biasedExp = (int) (rawBits >> 52);
        if (biasedExp == 0) {

            BigInteger frac = BigInteger.valueOf(rawBits).and(BI_FRAC_MASK);
            int expAdj = 64 - frac.bitLength();
            _significand = frac.shiftLeft(expAdj);
            _binaryExponent = (biasedExp & 0x07FF) - 1023 - expAdj;
        } else {
            BigInteger frac = getFrac(rawBits);
            _significand = frac;
            _binaryExponent = (biasedExp & 0x07FF) - 1023;
        }
    }
    ExpandedDouble(BigInteger frac, int binaryExp) {
        if (frac.bitLength() != 64) {
            throw new IllegalArgumentException("bad bit length");
        }
        _significand = frac;
        _binaryExponent = binaryExp;
    }

    private static BigInteger getFrac(long rawBits) {
        return BigInteger.valueOf(rawBits).and(BI_FRAC_MASK).or(BI_IMPLIED_FRAC_MSB).shiftLeft(11);
    }

    public static ExpandedDouble fromRawBitsAndExponent(long rawBits, int exp) {
        return new ExpandedDouble(getFrac(rawBits), exp);
    }


    public NormalisedDecimal normaliseBaseTen() {
        return NormalisedDecimal.create(_significand, _binaryExponent);
    }


    public int getBinaryExponent() {
        return _binaryExponent;
    }

    public BigInteger getSignificand() {
        return _significand;
    }
}

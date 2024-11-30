

package com.document.render.office.thirdpart.mozilla.intl.chardet;

public abstract class nsVerifier {

    static final byte eStart = (byte) 0;
    static final byte eError = (byte) 1;
    static final byte eItsMe = (byte) 2;
    static final int eidxSft4bits = 3;
    static final int eSftMsk4bits = 7;
    static final int eBitSft4bits = 2;
    static final int eUnitMsk4bits = 0x0000000F;

    nsVerifier() {
    }

    public static byte getNextState(nsVerifier v, byte b, byte s) {

        return (byte) (0xFF &
                (((v.states()[((
                        (s * v.stFactor() + (((v.cclass()[((b & 0xFF) >> v.eidxSft4bits)])
                                >> ((b & v.eSftMsk4bits) << v.eBitSft4bits))
                                & v.eUnitMsk4bits)) & 0xFF)
                        >> v.eidxSft4bits)]) >> (((
                        (s * v.stFactor() + (((v.cclass()[((b & 0xFF) >> v.eidxSft4bits)])
                                >> ((b & v.eSftMsk4bits) << v.eBitSft4bits))
                                & v.eUnitMsk4bits)) & 0xFF)
                        & v.eSftMsk4bits) << v.eBitSft4bits)) & v.eUnitMsk4bits)
        );

    }

    public abstract String charset();

    public abstract int stFactor();

    public abstract int[] cclass();

    public abstract int[] states();

    public abstract boolean isUCS2();


}

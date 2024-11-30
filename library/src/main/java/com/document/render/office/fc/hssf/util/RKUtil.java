

package com.document.render.office.fc.hssf.util;


public final class RKUtil {
    private RKUtil() {

    }


    public static double decodeNumber(int number) {
        long raw_number = number;



        raw_number = raw_number >> 2;
        double rvalue = 0;

        if ((number & 0x02) == 0x02) {


            rvalue = raw_number;
        } else {




            rvalue = Double.longBitsToDouble(raw_number << 34);
        }
        if ((number & 0x01) == 0x01) {




            rvalue /= 100;
        }

        return rvalue;
    }
}

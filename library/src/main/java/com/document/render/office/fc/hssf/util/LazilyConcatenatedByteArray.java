

package com.document.render.office.fc.hssf.util;

import java.util.ArrayList;
import java.util.List;


public class LazilyConcatenatedByteArray {
    private final List<byte[]> arrays = new ArrayList<byte[]>(1);


    public void clear() {
        arrays.clear();
    }


    public void concatenate(byte[] array) {
        if (array == null) {
            throw new IllegalArgumentException("array cannot be null");
        }
        arrays.add(array);
    }


    public byte[] toArray() {
        if (arrays.isEmpty()) {
            return null;
        } else if (arrays.size() > 1) {
            int totalLength = 0;
            for (byte[] array : arrays) {
                totalLength += array.length;
            }

            byte[] concatenated = new byte[totalLength];
            int destPos = 0;
            for (byte[] array : arrays) {
                System.arraycopy(array, 0, concatenated, destPos, array.length);
                destPos += array.length;
            }

            arrays.clear();
            arrays.add(concatenated);
        }

        return arrays.get(0);
    }
}


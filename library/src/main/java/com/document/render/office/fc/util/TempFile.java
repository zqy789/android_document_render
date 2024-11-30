

package com.document.render.office.fc.util;

import java.io.File;
import java.util.Random;


public final class TempFile {
    private static final Random rnd = new Random();
    private static File dir;


    public static File createTempFile(String prefix, String suffix) {
        if (dir == null) {
            dir = new File(System.getProperty("java.io.tmpdir"), "poifiles");
            dir.mkdir();
            if (System.getProperty("poi.keep.tmp.files") == null)
                dir.deleteOnExit();
        }

        File newFile = new File(dir, prefix + rnd.nextInt() + suffix);
        if (System.getProperty("poi.keep.tmp.files") == null)
            newFile.deleteOnExit();
        return newFile;
    }
}



package com.document.render.office.fc.openxml4j.opc.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;


public final class FileHelper {


    public static File getDirectory(File f) {
        if (f != null) {
            String path = f.getPath();
            int len = path.length();
            int num2 = len;
            while (--num2 >= 0) {
                char ch1 = path.charAt(num2);
                if (ch1 == File.separatorChar) {
                    return new File(path.substring(0, num2));
                }
            }
        }
        return null;
    }


    public static void copyFile(File in, File out) throws IOException {
        FileChannel sourceChannel = new FileInputStream(in).getChannel();
        FileChannel destinationChannel = new FileOutputStream(out).getChannel();
        sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
        sourceChannel.close();
        destinationChannel.close();
    }


    public static String getFilename(File file) {
        if (file != null) {
            String path = file.getPath();
            int len = path.length();
            int num2 = len;
            while (--num2 >= 0) {
                char ch1 = path.charAt(num2);
                if (ch1 == File.separatorChar)
                    return path.substring(num2 + 1, len);
            }
        }
        return "";
    }

}

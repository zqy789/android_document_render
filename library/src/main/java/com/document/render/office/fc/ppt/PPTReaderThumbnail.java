
package com.document.render.office.fc.ppt;

import android.graphics.Bitmap;

import com.document.render.office.constant.MainConstant;


public class PPTReaderThumbnail {

    private static PPTReaderThumbnail kit = new PPTReaderThumbnail();

    public static PPTReaderThumbnail instance() {
        return kit;
    }


    public Bitmap getThumbnail(String filePath) {
        try {
            String fileName = filePath.toLowerCase();

            if (fileName.endsWith(MainConstant.FILE_TYPE_PPT)
                    || fileName.endsWith(MainConstant.FILE_TYPE_POT)) {
                return getThumbnailForPPT(filePath);
            }

            else if (fileName.endsWith(MainConstant.FILE_TYPE_PPTX)
                    || fileName.endsWith(MainConstant.FILE_TYPE_PPTM)
                    || fileName.endsWith(MainConstant.FILE_TYPE_POTX)
                    || fileName.endsWith(MainConstant.FILE_TYPE_POTM)) {
                return getThumbnailForPPT(filePath);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }


    private Bitmap getThumbnailForPPT(String filePath) throws Exception {

        return null;
    }


    private Bitmap getThumbnailForPPTX(String filePath) throws Exception {
        return null;
    }
}

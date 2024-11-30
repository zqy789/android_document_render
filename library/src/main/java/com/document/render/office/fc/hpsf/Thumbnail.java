

package com.document.render.office.fc.hpsf;

import com.document.render.office.fc.util.LittleEndian;


public final class Thumbnail {


    public static int OFFSET_CFTAG = 4;


    public static int OFFSET_CF = 8;


    public static int OFFSET_WMFDATA = 20;


    public static int CFTAG_WINDOWS = -1;


    public static int CFTAG_MACINTOSH = -2;


    public static int CFTAG_FMTID = -3;


    public static int CFTAG_NODATA = 0;


    public static int CF_METAFILEPICT = 3;


    public static int CF_DIB = 8;


    public static int CF_ENHMETAFILE = 14;


    public static int CF_BITMAP = 2;


    private byte[] _thumbnailData = null;



    public Thumbnail() {
        super();
    }



    public Thumbnail(final byte[] thumbnailData) {
        this._thumbnailData = thumbnailData;
    }



    public byte[] getThumbnail() {
        return _thumbnailData;
    }



    public void setThumbnail(final byte[] thumbnail) {
        this._thumbnailData = thumbnail;
    }



    public long getClipboardFormatTag() {
        long clipboardFormatTag = LittleEndian.getUInt(getThumbnail(),
                OFFSET_CFTAG);
        return clipboardFormatTag;
    }



    public long getClipboardFormat() throws HPSFException {
        if (!(getClipboardFormatTag() == CFTAG_WINDOWS))
            throw new HPSFException("Clipboard Format Tag of Thumbnail must " +
                    "be CFTAG_WINDOWS.");

        return LittleEndian.getUInt(getThumbnail(), OFFSET_CF);
    }



    public byte[] getThumbnailAsWMF() throws HPSFException {
        if (!(getClipboardFormatTag() == CFTAG_WINDOWS))
            throw new HPSFException("Clipboard Format Tag of Thumbnail must " +
                    "be CFTAG_WINDOWS.");
        if (!(getClipboardFormat() == CF_METAFILEPICT)) {
            throw new HPSFException("Clipboard Format of Thumbnail must " +
                    "be CF_METAFILEPICT.");
        }
        byte[] thumbnail = getThumbnail();
        int wmfImageLength = thumbnail.length - OFFSET_WMFDATA;
        byte[] wmfImage = new byte[wmfImageLength];
        System.arraycopy(thumbnail,
                OFFSET_WMFDATA,
                wmfImage,
                0,
                wmfImageLength);
        return wmfImage;
    }
}

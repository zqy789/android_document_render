


package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.ddf.EscherBitmapBlip;
import com.document.render.office.fc.ddf.EscherBlipRecord;
import com.document.render.office.fc.ddf.EscherMetafileBlip;
import com.document.render.office.fc.ss.usermodel.PictureData;



public class HSSFPictureData implements PictureData {

    public static final short MSOBI_WMF = 0x2160;
    public static final short MSOBI_EMF = 0x3D40;
    public static final short MSOBI_PICT = 0x5420;
    public static final short MSOBI_PNG = 0x6E00;
    public static final short MSOBI_JPEG = 0x46A0;
    public static final short MSOBI_DIB = 0x7A80;

    public static final short FORMAT_MASK = (short) 0xFFF0;


    private EscherBlipRecord blip;


    public HSSFPictureData(EscherBlipRecord blip) {
        this.blip = blip;
    }


    public byte[] getData() {
        return blip.getPicturedata();
    }


    public int getFormat() {
        return blip.getRecordId() - (short) 0xF018;
    }


    public String suggestFileExtension() {
        switch (blip.getRecordId()) {
            case EscherMetafileBlip.RECORD_ID_WMF:
                return "wmf";
            case EscherMetafileBlip.RECORD_ID_EMF:
                return "emf";
            case EscherMetafileBlip.RECORD_ID_PICT:
                return "pict";
            case EscherBitmapBlip.RECORD_ID_PNG:
                return "png";
            case EscherBitmapBlip.RECORD_ID_JPEG:
                return "jpeg";
            case EscherBitmapBlip.RECORD_ID_DIB:
                return "dib";
            default:
                return "";
        }
    }


    public String getMimeType() {
        switch (blip.getRecordId()) {
            case EscherMetafileBlip.RECORD_ID_WMF:
                return "image/x-wmf";
            case EscherMetafileBlip.RECORD_ID_EMF:
                return "image/x-emf";
            case EscherMetafileBlip.RECORD_ID_PICT:
                return "image/x-pict";
            case EscherBitmapBlip.RECORD_ID_PNG:
                return "image/png";
            case EscherBitmapBlip.RECORD_ID_JPEG:
                return "image/jpeg";
            case EscherBitmapBlip.RECORD_ID_DIB:
                return "image/bmp";
            default:
                return "image/unknown";
        }
    }
}

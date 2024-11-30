
package com.document.render.office.fc;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.document.render.bean.DocSourceType;
import com.document.render.office.fc.fs.filesystem.CFBFileSystem;
import com.document.render.office.fc.fs.filesystem.Property;
import com.document.render.office.fc.fs.storage.LittleEndian;
import com.document.render.office.fc.openxml4j.opc.PackagePart;
import com.document.render.office.fc.openxml4j.opc.PackageRelationship;
import com.document.render.office.fc.openxml4j.opc.PackageRelationshipTypes;
import com.document.render.office.fc.openxml4j.opc.ZipPackage;
import com.document.render.office.fc.pdf.PDFLib;
import com.document.render.office.fc.ppt.PPTReader;
import com.document.render.office.java.awt.Dimension;
import com.document.render.office.pg.control.PGEditor;
import com.document.render.office.pg.model.PGModel;
import com.document.render.office.pg.view.SlideDrawKit;

import java.io.File;
import java.io.FileInputStream;



public class ReaderThumbnail {

    private static ReaderThumbnail kit = new ReaderThumbnail();

    public static ReaderThumbnail instance() {
        return kit;
    }


    public Bitmap getThumbnailForPPT(String filePath, int width, int height)
            throws Exception {

        FileInputStream fis = new FileInputStream(new File(filePath));
        CFBFileSystem poifs = new CFBFileSystem(fis, true);
        Property property = poifs.getProperty("\u0005SummaryInformation");
        if (property != null) {
            byte[] data = property.getDocumentRawData();

            int offset = 0;
            int byteOrder = LittleEndian.getUShort(data, offset);
            offset += LittleEndian.SHORT_SIZE;

            int format = LittleEndian.getUShort(data, offset);
            offset += LittleEndian.SHORT_SIZE;

            int osVersion = (int) LittleEndian.getUInt(data, offset);
            offset += LittleEndian.INT_SIZE;



            offset += 16;

            int sectionCount = LittleEndian.getInt(data, offset);
            offset += LittleEndian.INT_SIZE;
            if (sectionCount < 0) {


                return null;
            }
            for (int i = 0; i < sectionCount; i++) {

                Bitmap thumbnail = readSection(data, offset, width, height);
                if (thumbnail != null) {
                    return thumbnail;
                }

                offset += 16 + LittleEndian.INT_SIZE;
            }
        }
        return null;
    }


    private Bitmap readSection(byte[] data, int offset, int width, int height) {
        int read_off = offset;


        read_off += 16;

        int section_offset = (int) LittleEndian.getUInt(data, read_off);
        read_off = section_offset;

        int size = (int) LittleEndian.getUInt(data, read_off);
        read_off += LittleEndian.INT_SIZE;

        final int propertyCount = (int) LittleEndian.getUInt(data, read_off);
        read_off += LittleEndian.INT_SIZE;

        int pro_off = read_off;
        int propertyID = -1;
        int propertyOffset = 0;
        for (int i = 0; i < propertyCount; i++) {


            propertyID = (int) LittleEndian.getUInt(data, pro_off);
            pro_off += LittleEndian.INT_SIZE;


            propertyOffset = (int) LittleEndian.getUInt(data, pro_off);
            pro_off += LittleEndian.INT_SIZE;


            if (propertyID == 17) {
                break;
            }
        }

        if (propertyID == 17) {
            int tOffset = propertyOffset + section_offset;

            int type = (int) LittleEndian.getUInt(data, tOffset);
            tOffset += LittleEndian.INT_SIZE;

            int tSize = (int) LittleEndian.getUInt(data, tOffset);
            tOffset += LittleEndian.INT_SIZE;

            int osType = (int) LittleEndian.getUInt(data, tOffset);
            tOffset += LittleEndian.INT_SIZE;

            int picType = (int) LittleEndian.getUInt(data, tOffset);
            tOffset += LittleEndian.INT_SIZE;


            if (osType == -1) {
                int pic_data_offset = propertyOffset + section_offset;

                if (picType == 0x0003) {
                    pic_data_offset += 24;
                }

                else if (picType == 0x000E) {

                }

                else if (picType == 0x0333) {

                }
                if (pic_data_offset > propertyOffset + section_offset) {
                    try {
                        if (picType == 0x0003) {









                        } else if (picType == 0x0333) {

                            return BitmapFactory.decodeByteArray(data, pic_data_offset, data.length - pic_data_offset);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }


    private Bitmap getThumbnailForPPT_Small(String filePath, int width, int height) {
        try {

            PPTReader reader = new PPTReader(null, filePath, DocSourceType.PATH, true);
            PGModel model = (PGModel) reader.getModel();


            if (model != null) {
                Dimension d = model.getPageSize();
                float zoom = (float) (Math.min(width / d.getWidth(), height / d.getHeight()));
                PGEditor editor = new PGEditor(null);
                Bitmap bitmap = SlideDrawKit.instance().getThumbnail(model, editor, model.getSlide(0), zoom);

                return bitmap;

            }
        } catch (Exception e) {
        }
        return null;

    }


    public Bitmap getThumbnailForPPTX(String filePath) throws Exception {
        ZipPackage zipPackage = new ZipPackage(filePath);
        

        


        PackageRelationship thumbnail = zipPackage.getRelationshipsByType(
                PackageRelationshipTypes.THUMBNAIL).getRelationship(0);
        if (thumbnail == null) {
            return null;
        }
        PackagePart part = zipPackage.getPart(thumbnail.getTargetURI());
        if (part == null) {
            return null;
        }
        return BitmapFactory.decodeStream(part.getInputStream());
    }


    public Bitmap getThumbnailForPDF(String filePath, float zoom) throws Exception {
        try {
            PDFLib lib = PDFLib.getPDFLib();
            lib.openFileSync(filePath);
            if (lib.hasPasswordSync()) {
                return null;
            }
            Rect rect = lib.getAllPagesSize()[0];
            int w = (int) (rect.width() * zoom);
            int h = (int) (rect.height() * zoom);
            Bitmap bitmap = null;
            try {
                bitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
                lib.drawPageSync(bitmap, 0, w, h, 0, 0, w, h, 1);
            } catch (OutOfMemoryError e) {

            }
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }
}

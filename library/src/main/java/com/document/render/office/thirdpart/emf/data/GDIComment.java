

package com.document.render.office.thirdpart.emf.data;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.document.render.office.thirdpart.emf.EMFConstants;
import com.document.render.office.thirdpart.emf.EMFInputStream;
import com.document.render.office.thirdpart.emf.EMFRenderer;
import com.document.render.office.thirdpart.emf.EMFTag;

import java.io.ByteArrayInputStream;
import java.io.IOException;


public class GDIComment extends EMFTag {

    private int type;

    private String comment = "";


    private Bitmap image;

    public GDIComment() {
        super(70, 1);
    }

    public GDIComment(String comment) {
        this();
        this.comment = comment;
    }

    public EMFTag read(int tagID, EMFInputStream emf, int len) throws IOException {

        GDIComment result = new GDIComment();

        int l = emf.readDWORD();

        result.type = emf.readDWORD();



        if (result.type == 726027589) {

            emf.readByte(l - 4);
            if (l % 4 != 0) {
                emf.readBYTE(4 - l % 4);
            }
        } else if (result.type == EMFConstants.GDICOMMENT_BEGINGROUP) {



            emf.readRECTL();





            int nDescription = emf.readDWORD();


            if (nDescription > 0) {
                result.comment = new String(emf.readByte(nDescription));
            }
        } else if (result.type == EMFConstants.GDICOMMENT_ENDGROUP) {

        } else if (result.type == EMFConstants.GDICOMMENT_MULTIFORMATS) {



            emf.readRECTL();




            emf.readDWORD();










            l = l - 4 - 8;

            result.comment = new String(emf.readBYTE(l));

            if (l % 4 != 0)
                emf.readBYTE(4 - l % 4);

        } else if (result.type == EMFConstants.GDICOMMENT_WINDOWS_METAFILE) {



            emf.readDWORD();









            emf.readDWORD();



            emf.readDWORD();



            int size = emf.readDWORD();


            byte[] bytes = emf.readByte(size);

            result.image = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes));
            return this;

        } else {
            l = l - 4;

            if (l > 0) {
                result.comment = new String(emf.readBYTE(l));

                if (l % 4 != 0) {
                    emf.readBYTE(4 - l % 4);
                }
            } else {
                comment = "";
            }
        }
        return result;
    }

    public String toString() {
        return super.toString() + "\n  length: " + comment.length();
    }


    public void render(EMFRenderer renderer) {

    }
}

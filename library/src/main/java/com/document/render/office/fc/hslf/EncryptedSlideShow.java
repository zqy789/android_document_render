

package com.document.render.office.fc.hslf;

import com.document.render.office.fc.hslf.exceptions.CorruptPowerPointFileException;
import com.document.render.office.fc.hslf.record.CurrentUserAtom;
import com.document.render.office.fc.hslf.record.DocumentEncryptionAtom;
import com.document.render.office.fc.hslf.record.PersistPtrHolder;
import com.document.render.office.fc.hslf.record.Record;
import com.document.render.office.fc.hslf.record.UserEditAtom;




public final class EncryptedSlideShow {

    public static boolean checkIfEncrypted(HSLFSlideShow hss) {



        return false;
    }


    public static DocumentEncryptionAtom fetchDocumentEncryptionAtom(HSLFSlideShow hss) {



        CurrentUserAtom cua = hss.getCurrentUserAtom();
        if (cua.getCurrentEditOffset() != 0) {

            if (cua.getCurrentEditOffset() > hss.getUnderlyingBytes().length) {
                throw new CorruptPowerPointFileException(
                        "The CurrentUserAtom claims that the offset of last edit details are past the end of the file");
            }



            Record r = null;
            try {
                r = Record.buildRecordAtOffset(hss.getUnderlyingBytes(),
                        (int) cua.getCurrentEditOffset());
            } catch (ArrayIndexOutOfBoundsException e) {
                return null;
            }
            if (r == null) {
                return null;
            }
            if (!(r instanceof UserEditAtom)) {
                return null;
            }
            UserEditAtom uea = (UserEditAtom) r;


            Record r2 = Record.buildRecordAtOffset(hss.getUnderlyingBytes(),
                    uea.getPersistPointersOffset());
            if (!(r2 instanceof PersistPtrHolder)) {
                return null;
            }
            PersistPtrHolder pph = (PersistPtrHolder) r2;


            int[] slideIds = pph.getKnownSlideIDs();
            int maxSlideId = -1;
            for (int i = 0; i < slideIds.length; i++) {
                if (slideIds[i] > maxSlideId) {
                    maxSlideId = slideIds[i];
                }
            }
            if (maxSlideId == -1) {
                return null;
            }

            int offset = ((Integer) pph.getSlideLocationsLookup().get(Integer.valueOf(maxSlideId)))
                    .intValue();
            Record r3 = Record.buildRecordAtOffset(hss.getUnderlyingBytes(), offset);


            if (r3 instanceof DocumentEncryptionAtom) {
                return (DocumentEncryptionAtom) r3;
            }
        }

        return null;
    }
}

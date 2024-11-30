

package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.util.Internal;
import com.document.render.office.fc.util.LittleEndian;

import java.util.Collections;



@Internal
public final class OldPAPBinTable extends PAPBinTable {

    public OldPAPBinTable(byte[] documentStream, int offset, int size, int fcMin, TextPieceTable tpt) {
        PlexOfCps binTable = new PlexOfCps(documentStream, offset, size, 2);

        int length = binTable.length();
        for (int x = 0; x < length; x++) {
            GenericPropertyNode node = binTable.getProperty(x);

            int pageNum = LittleEndian.getShort(node.getBytes());
            int pageOffset = POIFSConstants.SMALLER_BIG_BLOCK_SIZE * pageNum;

            PAPFormattedDiskPage pfkp = new PAPFormattedDiskPage(documentStream, documentStream,
                    pageOffset, tpt);

            int fkpSize = pfkp.size();

            for (int y = 0; y < fkpSize; y++) {
                PAPX papx = pfkp.getPAPX(y);
                if (papx != null) {
                    _paragraphs.add(papx);
                }
            }
        }
        Collections.sort(_paragraphs, PropertyNode.StartComparator.instance);
    }
}

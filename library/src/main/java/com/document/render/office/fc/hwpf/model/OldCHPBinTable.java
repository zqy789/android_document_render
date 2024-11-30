

package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.util.Internal;
import com.document.render.office.fc.util.LittleEndian;

import java.util.Collections;



@Internal
public final class OldCHPBinTable extends CHPBinTable {

    public OldCHPBinTable(byte[] documentStream, int offset, int size, int fcMin, TextPieceTable tpt) {
        PlexOfCps binTable = new PlexOfCps(documentStream, offset, size, 2);

        int length = binTable.length();
        for (int x = 0; x < length; x++) {
            GenericPropertyNode node = binTable.getProperty(x);

            int pageNum = LittleEndian.getShort(node.getBytes());
            int pageOffset = POIFSConstants.SMALLER_BIG_BLOCK_SIZE * pageNum;

            CHPFormattedDiskPage cfkp = new CHPFormattedDiskPage(documentStream, pageOffset, tpt);

            int fkpSize = cfkp.size();

            for (int y = 0; y < fkpSize; y++) {
                CHPX chpx = cfkp.getCHPX(y);
                if (chpx != null)
                    _textRuns.add(chpx);
            }
        }
        Collections.sort(_textRuns, PropertyNode.StartComparator.instance);
    }
}

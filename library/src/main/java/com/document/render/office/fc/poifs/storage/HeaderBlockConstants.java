

package com.document.render.office.fc.poifs.storage;


import com.document.render.office.fc.poifs.common.POIFSConstants;
import com.document.render.office.fc.util.LittleEndianConsts;



public interface HeaderBlockConstants {
    public static final long _signature = 0xE11AB1A1E011CFD0L;
    public static final int _bat_array_offset = 0x4c;
    public static final int _max_bats_in_header =
            (POIFSConstants.SMALLER_BIG_BLOCK_SIZE - _bat_array_offset)
                    / LittleEndianConsts.INT_SIZE;







    public static final int _signature_offset = 0;
    public static final int _bat_count_offset = 0x2C;
    public static final int _property_start_offset = 0x30;
    public static final int _sbat_start_offset = 0x3C;
    public static final int _sbat_block_count_offset = 0x40;
    public static final int _xbat_start_offset = 0x44;
    public static final int _xbat_count_offset = 0x48;
}


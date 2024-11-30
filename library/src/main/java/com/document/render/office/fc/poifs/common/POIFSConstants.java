


package com.document.render.office.fc.poifs.common;


public interface POIFSConstants {
    
    public static final int SMALLER_BIG_BLOCK_SIZE = 0x0200;
    public static final POIFSBigBlockSize SMALLER_BIG_BLOCK_SIZE_DETAILS =
            new POIFSBigBlockSize(SMALLER_BIG_BLOCK_SIZE, (short) 9);
    
    public static final int LARGER_BIG_BLOCK_SIZE = 0x1000;
    public static final POIFSBigBlockSize LARGER_BIG_BLOCK_SIZE_DETAILS =
            new POIFSBigBlockSize(LARGER_BIG_BLOCK_SIZE, (short) 12);

    
    public static final int SMALL_BLOCK_SIZE = 0x0040;

    
    public static final int PROPERTY_SIZE = 0x0080;

    
    public static final int BIG_BLOCK_MINIMUM_DOCUMENT_SIZE = 0x1000;

    
    public static final int LARGEST_REGULAR_SECTOR_NUMBER = -5;

    
    public static final int DIFAT_SECTOR_BLOCK = -4;
    
    public static final int FAT_SECTOR_BLOCK = -3;
    
    public static final int END_OF_CHAIN = -2;
    
    public static final int UNUSED_BLOCK = -1;

    
    public static final byte[] OOXML_FILE_HEADER =
            new byte[]{0x50, 0x4b, 0x03, 0x04};
}   

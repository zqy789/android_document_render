package com.document.render.office.fc.fs.filesystem;



public interface CFBConstants {

    public static final int SMALLER_BIG_BLOCK_SIZE = 0x0200;
    public static final BlockSize SMALLER_BIG_BLOCK_SIZE_DETAILS = new BlockSize(SMALLER_BIG_BLOCK_SIZE, (short) 9);

    public static final int LARGER_BIG_BLOCK_SIZE = 0x1000;
    public static final BlockSize LARGER_BIG_BLOCK_SIZE_DETAILS = new BlockSize(LARGER_BIG_BLOCK_SIZE, (short) 12);


    public static final int SMALL_BLOCK_SIZE = 0x0040;


    public static final int PROPERTY_SIZE = 0x0080;

    public static final int BIG_BLOCK_MINIMUM_DOCUMENT_SIZE = 0x1000;


    public static final int LARGEST_REGULAR_SECTOR_NUMBER = -5;


    public static final int DIFAT_SECTOR_BLOCK = -4;

    public static final int FAT_SECTOR_BLOCK = -3;

    public static final int END_OF_CHAIN = -2;

    public static final int UNUSED_BLOCK = -1;
} 

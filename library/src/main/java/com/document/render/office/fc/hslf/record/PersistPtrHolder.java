

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.POILogger;

import java.util.Enumeration;
import java.util.Hashtable;




public final class PersistPtrHolder extends PositionDependentRecordAtom {
    private byte[] _header;
    private byte[] _ptrData;
    private long _type;


    private Hashtable<Integer, Integer> _slideLocations;

    private Hashtable<Integer, Integer> _slideOffsetDataLocation;


    protected PersistPtrHolder(byte[] source, int start, int len) {


        if (len < 8) {
            len = 8;
        }


        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);
        _type = LittleEndian.getUShort(_header, 2);








        _slideLocations = new Hashtable<Integer, Integer>();
        _slideOffsetDataLocation = new Hashtable<Integer, Integer>();
        _ptrData = new byte[len - 8];
        System.arraycopy(source, start + 8, _ptrData, 0, _ptrData.length);

        int pos = 0;
        while (pos < _ptrData.length) {

            long info = LittleEndian.getUInt(_ptrData, pos);



            int offset_count = (int) (info >> 20);
            int offset_no = (int) (info - (offset_count << 20));



            pos += 4;


            for (int i = 0; i < offset_count; i++) {
                int sheet_no = offset_no + i;
                long sheet_offset = LittleEndian.getUInt(_ptrData, pos);
                _slideLocations.put(Integer.valueOf(sheet_no), Integer.valueOf((int) sheet_offset));
                _slideOffsetDataLocation.put(Integer.valueOf(sheet_no), Integer.valueOf(pos));


                pos += 4;
            }
        }
    }


    public int[] getKnownSlideIDs() {
        int[] ids = new int[_slideLocations.size()];
        Enumeration<Integer> e = _slideLocations.keys();
        for (int i = 0; i < ids.length; i++) {
            Integer id = e.nextElement();
            ids[i] = id.intValue();
        }
        return ids;
    }


    public Hashtable<Integer, Integer> getSlideLocationsLookup() {
        return _slideLocations;
    }


    public Hashtable<Integer, Integer> getSlideOffsetDataLocationsLookup() {
        return _slideOffsetDataLocation;
    }


    public void addSlideLookup(int slideID, int posOnDisk) {



        byte[] newPtrData = new byte[_ptrData.length + 8];
        System.arraycopy(_ptrData, 0, newPtrData, 0, _ptrData.length);


        _slideLocations.put(Integer.valueOf(slideID), Integer.valueOf(posOnDisk));

        _slideOffsetDataLocation.put(Integer.valueOf(slideID),
                Integer.valueOf(_ptrData.length + 4));




        int infoBlock = slideID;
        infoBlock += (1 << 20);


        LittleEndian.putInt(newPtrData, newPtrData.length - 8, infoBlock);
        LittleEndian.putInt(newPtrData, newPtrData.length - 4, posOnDisk);


        _ptrData = newPtrData;


        LittleEndian.putInt(_header, 4, newPtrData.length);
    }


    public long getRecordType() {
        return _type;
    }


    public void updateOtherRecordReferences(Hashtable<Integer, Integer> oldToNewReferencesLookup) {
        int[] slideIDs = getKnownSlideIDs();




        for (int i = 0; i < slideIDs.length; i++) {
            Integer id = Integer.valueOf(slideIDs[i]);
            Integer oldPos = (Integer) _slideLocations.get(id);
            Integer newPos = (Integer) oldToNewReferencesLookup.get(oldPos);

            if (newPos == null) {
                logger.log(POILogger.WARN, "Couldn't find the new location of the \"slide\" with id " + id + " that used to be at " + oldPos);
                logger.log(POILogger.WARN, "Not updating the position of it, you probably won't be able to find it any more (if you ever could!)");
                newPos = oldPos;
            }


            Integer dataOffset = (Integer) _slideOffsetDataLocation.get(id);
            LittleEndian.putInt(_ptrData, dataOffset.intValue(), newPos.intValue());


            _slideLocations.remove(id);
            _slideLocations.put(id, newPos);
        }
    }


    public void dispose() {
        _header = null;
        _ptrData = null;
    }
}



package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.ddf.DefaultEscherRecordFactory;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.fc.ddf.EscherRecord;
import com.document.render.office.fc.ddf.EscherRecordFactory;
import com.document.render.office.fc.ddf.NullEscherSerializationListener;
import com.document.render.office.fc.hssf.util.LazilyConcatenatedByteArray;
import com.document.render.office.fc.util.LittleEndian;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public abstract class AbstractEscherHolderRecord extends Record {
    private static boolean DESERIALISE;

    static {
        try {
            DESERIALISE = (System.getProperty("poi.deserialize.escher") != null);
        } catch (SecurityException e) {
            DESERIALISE = false;
        }
    }

    private List<EscherRecord> escherRecords;
    private LazilyConcatenatedByteArray rawDataContainer = new LazilyConcatenatedByteArray();

    public AbstractEscherHolderRecord() {
        escherRecords = new ArrayList<EscherRecord>();
    }

    public AbstractEscherHolderRecord(RecordInputStream in) {
        escherRecords = new ArrayList<EscherRecord>();
        if (!DESERIALISE) {
            rawDataContainer.concatenate(in.readRemainder());
        } else {
            byte[] data = in.readAllContinuedRemainder();
            convertToEscherRecords(0, data.length, data);
        }
    }

    protected void convertRawBytesToEscherRecords() {
        byte[] rawData = getRawData();
        convertToEscherRecords(0, rawData.length, rawData);
    }

    private void convertToEscherRecords(int offset, int size, byte[] data) {
        escherRecords.clear();
        EscherRecordFactory recordFactory = new DefaultEscherRecordFactory();
        int pos = offset;
        while (pos < offset + size) {
            EscherRecord r = recordFactory.createRecord(data, pos);
            int bytesRead = r.fillFields(data, pos, recordFactory);
            escherRecords.add(r);
            pos += bytesRead;
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        final String nl = System.getProperty("line.separator");
        buffer.append('[' + getRecordName() + ']' + nl);
        if (escherRecords.size() == 0)
            buffer.append("No Escher Records Decoded" + nl);
        for (Iterator<EscherRecord> iterator = escherRecords.iterator(); iterator.hasNext(); ) {
            EscherRecord r = iterator.next();
            buffer.append(r.toString());
        }
        buffer.append("[/" + getRecordName() + ']' + nl);

        return buffer.toString();
    }

    protected abstract String getRecordName();

    public int serialize(int offset, byte[] data) {
        LittleEndian.putShort(data, 0 + offset, getSid());
        LittleEndian.putShort(data, 2 + offset, (short) (getRecordSize() - 4));
        byte[] rawData = getRawData();
        if (escherRecords.size() == 0 && rawData != null) {
            LittleEndian.putShort(data, 0 + offset, getSid());
            LittleEndian.putShort(data, 2 + offset, (short) (getRecordSize() - 4));
            System.arraycopy(rawData, 0, data, 4 + offset, rawData.length);
            return rawData.length + 4;
        }
        LittleEndian.putShort(data, 0 + offset, getSid());
        LittleEndian.putShort(data, 2 + offset, (short) (getRecordSize() - 4));

        int pos = offset + 4;
        for (Iterator<EscherRecord> iterator = escherRecords.iterator(); iterator.hasNext(); ) {
            EscherRecord r = iterator.next();
            pos += r.serialize(pos, data, new NullEscherSerializationListener());
        }
        return getRecordSize();
    }

    public int getRecordSize() {
        byte[] rawData = getRawData();
        if (escherRecords.size() == 0 && rawData != null) {

            return rawData.length;
        }
        int size = 0;
        for (Iterator<EscherRecord> iterator = escherRecords.iterator(); iterator.hasNext(); ) {
            EscherRecord r = iterator.next();
            size += r.getRecordSize();
        }
        return size;
    }


    public abstract short getSid();

    public Object clone() {
        return cloneViaReserialise();
    }

    public void addEscherRecord(int index, EscherRecord element) {
        escherRecords.add(index, element);
    }

    public boolean addEscherRecord(EscherRecord element) {
        return escherRecords.add(element);
    }

    public List<EscherRecord> getEscherRecords() {
        return escherRecords;
    }

    public void clearEscherRecords() {
        escherRecords.clear();
    }


    public EscherContainerRecord getEscherContainer() {
        for (Iterator<EscherRecord> it = escherRecords.iterator(); it.hasNext(); ) {
            EscherRecord er = it.next();
            if (er instanceof EscherContainerRecord) {
                return (EscherContainerRecord) er;
            }
        }
        return null;
    }


    public List<EscherContainerRecord> getgetEscherContainers() {
        List<EscherContainerRecord> containers = new ArrayList<EscherContainerRecord>();
        for (Iterator<EscherRecord> it = escherRecords.iterator(); it.hasNext(); ) {
            EscherRecord er = it.next();
            if (er instanceof EscherContainerRecord) {
                containers.add((EscherContainerRecord) er);
            }
        }

        return containers;
    }


    public EscherRecord findFirstWithId(short id) {
        return findFirstWithId(id, getEscherRecords());
    }

    private EscherRecord findFirstWithId(short id, List<EscherRecord> records) {

        for (Iterator<EscherRecord> it = records.iterator(); it.hasNext(); ) {
            EscherRecord r = it.next();
            if (r.getRecordId() == id) {
                return r;
            }
        }


        for (Iterator<EscherRecord> it = records.iterator(); it.hasNext(); ) {
            EscherRecord r = it.next();
            if (r.isContainerRecord()) {
                EscherRecord found = findFirstWithId(id, r.getChildRecords());
                if (found != null) {
                    return found;
                }
            }
        }


        return null;
    }


    public EscherRecord getEscherRecord(int index) {
        return (EscherRecord) escherRecords.get(index);
    }


    public void join(AbstractEscherHolderRecord record) {
        rawDataContainer.concatenate(record.getRawData());
    }

    public void processContinueRecord(byte[] record) {
        rawDataContainer.concatenate(record);
    }

    public byte[] getRawData() {
        return rawDataContainer.toArray();
    }

    public void setRawData(byte[] rawData) {
        rawDataContainer.clear();
        rawDataContainer.concatenate(rawData);
    }


    public void decode() {
        byte[] rawData = getRawData();
        convertToEscherRecords(0, rawData.length, rawData);
    }
}

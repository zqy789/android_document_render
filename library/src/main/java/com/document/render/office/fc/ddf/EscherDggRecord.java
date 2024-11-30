

package com.document.render.office.fc.ddf;


import androidx.annotation.Keep;

import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.RecordFormatException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public final class EscherDggRecord extends EscherRecord {
    @Keep
    public static final short RECORD_ID = (short) 0xF006;
    public static final String RECORD_DESCRIPTION = "MsofbtDgg";
    private static final Comparator<FileIdCluster> MY_COMP = new Comparator<FileIdCluster>() {
        public int compare(FileIdCluster f1, FileIdCluster f2) {
            if (f1.getDrawingGroupId() == f2.getDrawingGroupId()) {
                return 0;
            }
            if (f1.getDrawingGroupId() < f2.getDrawingGroupId()) {
                return -1;
            }
            return +1;
        }
    };
    private int field_1_shapeIdMax;

    private int field_3_numShapesSaved;
    private int field_4_drawingsSaved;
    private FileIdCluster[] field_5_fileIdClusters;
    private int maxDgId;

    public int fillFields(byte[] data, int offset, EscherRecordFactory recordFactory) {
        int bytesRemaining = readHeader(data, offset);
        int pos = offset + 8;
        int size = 0;
        field_1_shapeIdMax = LittleEndian.getInt(data, pos + size);
        size += 4;
        LittleEndian.getInt(data, pos + size);
        size += 4;
        field_3_numShapesSaved = LittleEndian.getInt(data, pos + size);
        size += 4;
        field_4_drawingsSaved = LittleEndian.getInt(data, pos + size);
        size += 4;
        field_5_fileIdClusters = new FileIdCluster[(bytesRemaining - size) / 8];
        for (int i = 0; i < field_5_fileIdClusters.length; i++) {
            field_5_fileIdClusters[i] = new FileIdCluster(LittleEndian.getInt(data, pos + size), LittleEndian.getInt(data, pos + size + 4));
            maxDgId = Math.max(maxDgId, field_5_fileIdClusters[i].getDrawingGroupId());
            size += 8;
        }
        bytesRemaining -= size;
        if (bytesRemaining != 0)
            throw new RecordFormatException("Expecting no remaining data but got " + bytesRemaining + " byte(s).");
        return 8 + size + bytesRemaining;
    }

    public int serialize(int offset, byte[] data, EscherSerializationListener listener) {
        listener.beforeRecordSerialize(offset, getRecordId(), this);

        int pos = offset;
        LittleEndian.putShort(data, pos, getOptions());
        pos += 2;
        LittleEndian.putShort(data, pos, getRecordId());
        pos += 2;
        int remainingBytes = getRecordSize() - 8;
        LittleEndian.putInt(data, pos, remainingBytes);
        pos += 4;

        LittleEndian.putInt(data, pos, field_1_shapeIdMax);
        pos += 4;
        LittleEndian.putInt(data, pos, getNumIdClusters());
        pos += 4;
        LittleEndian.putInt(data, pos, field_3_numShapesSaved);
        pos += 4;
        LittleEndian.putInt(data, pos, field_4_drawingsSaved);
        pos += 4;
        for (int i = 0; i < field_5_fileIdClusters.length; i++) {
            LittleEndian.putInt(data, pos, field_5_fileIdClusters[i].field_1_drawingGroupId);
            pos += 4;
            LittleEndian.putInt(data, pos, field_5_fileIdClusters[i].field_2_numShapeIdsUsed);
            pos += 4;
        }

        listener.afterRecordSerialize(pos, getRecordId(), getRecordSize(), this);
        return getRecordSize();
    }

    public int getRecordSize() {
        return 8 + 16 + (8 * field_5_fileIdClusters.length);
    }

    public short getRecordId() {
        return RECORD_ID;
    }

    public String getRecordName() {
        return "Dgg";
    }

    public String toString() {

        StringBuffer field_5_string = new StringBuffer();
        if (field_5_fileIdClusters != null)
            for (int i = 0; i < field_5_fileIdClusters.length; i++) {
                field_5_string.append("  DrawingGroupId").append(i + 1).append(": ");
                field_5_string.append(field_5_fileIdClusters[i].field_1_drawingGroupId);
                field_5_string.append('\n');
                field_5_string.append("  NumShapeIdsUsed").append(i + 1).append(": ");
                field_5_string.append(field_5_fileIdClusters[i].field_2_numShapeIdsUsed);
                field_5_string.append('\n');
            }
        return getClass().getName() + ":" + '\n' +
                "  RecordId: 0x" + HexDump.toHex(RECORD_ID) + '\n' +
                "  Options: 0x" + HexDump.toHex(getOptions()) + '\n' +
                "  ShapeIdMax: " + field_1_shapeIdMax + '\n' +
                "  NumIdClusters: " + getNumIdClusters() + '\n' +
                "  NumShapesSaved: " + field_3_numShapesSaved + '\n' +
                "  DrawingsSaved: " + field_4_drawingsSaved + '\n' +
                "" + field_5_string.toString();

    }

    public int getShapeIdMax() {
        return field_1_shapeIdMax;
    }


    public void setShapeIdMax(int shapeIdMax) {
        this.field_1_shapeIdMax = shapeIdMax;
    }


    public int getNumIdClusters() {
        return (field_5_fileIdClusters == null ? 0 : (field_5_fileIdClusters.length + 1));
    }

    public int getNumShapesSaved() {
        return field_3_numShapesSaved;
    }

    public void setNumShapesSaved(int numShapesSaved) {
        this.field_3_numShapesSaved = numShapesSaved;
    }

    public int getDrawingsSaved() {
        return field_4_drawingsSaved;
    }

    public void setDrawingsSaved(int drawingsSaved) {
        this.field_4_drawingsSaved = drawingsSaved;
    }


    public int getMaxDrawingGroupId() {
        return maxDgId;
    }

    public void setMaxDrawingGroupId(int id) {
        maxDgId = id;
    }

    public FileIdCluster[] getFileIdClusters() {
        return field_5_fileIdClusters;
    }

    public void setFileIdClusters(FileIdCluster[] fileIdClusters) {
        this.field_5_fileIdClusters = fileIdClusters;
    }

    public void addCluster(int dgId, int numShapedUsed) {
        addCluster(dgId, numShapedUsed, true);
    }


    public void addCluster(int dgId, int numShapedUsed, boolean sort) {
        List<FileIdCluster> clusters = new ArrayList<FileIdCluster>(Arrays.asList(field_5_fileIdClusters));
        clusters.add(new FileIdCluster(dgId, numShapedUsed));
        if (sort) Collections.sort(clusters, MY_COMP);
        maxDgId = Math.min(maxDgId, dgId);
        field_5_fileIdClusters = clusters.toArray(new FileIdCluster[clusters.size()]);
    }

    public void dispose() {
    }

    public static class FileIdCluster {
        private int field_1_drawingGroupId;
        private int field_2_numShapeIdsUsed;
        public FileIdCluster(int drawingGroupId, int numShapeIdsUsed) {
            this.field_1_drawingGroupId = drawingGroupId;
            this.field_2_numShapeIdsUsed = numShapeIdsUsed;
        }

        public int getDrawingGroupId() {
            return field_1_drawingGroupId;
        }

        public int getNumShapeIdsUsed() {
            return field_2_numShapeIdsUsed;
        }

        public void incrementShapeId() {
            this.field_2_numShapeIdsUsed++;
        }
    }
}

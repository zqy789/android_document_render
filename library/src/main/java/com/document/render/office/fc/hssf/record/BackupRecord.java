


package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;



public final class BackupRecord
        extends StandardRecord {
    @Keep
    public final static short sid = 0x40;
    private short field_1_backup;

    public BackupRecord() {
    }

    public BackupRecord(RecordInputStream in) {
        field_1_backup = in.readShort();
    }



    public short getBackup() {
        return field_1_backup;
    }



    public void setBackup(short backup) {
        field_1_backup = backup;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[BACKUP]\n");
        buffer.append("    .backup          = ")
                .append(Integer.toHexString(getBackup())).append("\n");
        buffer.append("[/BACKUP]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getBackup());
    }

    protected int getDataSize() {
        return 2;
    }

    public short getSid() {
        return sid;
    }
}

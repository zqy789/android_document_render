


package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.util.LittleEndianOutput;



public final class CountryRecord
        extends StandardRecord {
    @Keep
    public final static short sid = 0x8c;


    private short field_1_default_country;
    private short field_2_current_country;

    public CountryRecord() {
    }

    public CountryRecord(RecordInputStream in) {
        field_1_default_country = in.readShort();
        field_2_current_country = in.readShort();
    }



    public short getDefaultCountry() {
        return field_1_default_country;
    }



    public void setDefaultCountry(short country) {
        field_1_default_country = country;
    }



    public short getCurrentCountry() {
        return field_2_current_country;
    }



    public void setCurrentCountry(short country) {
        field_2_current_country = country;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[COUNTRY]\n");
        buffer.append("    .defaultcountry  = ")
                .append(Integer.toHexString(getDefaultCountry())).append("\n");
        buffer.append("    .currentcountry  = ")
                .append(Integer.toHexString(getCurrentCountry())).append("\n");
        buffer.append("[/COUNTRY]\n");
        return buffer.toString();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(getDefaultCountry());
        out.writeShort(getCurrentCountry());
    }

    protected int getDataSize() {
        return 4;
    }

    public short getSid() {
        return sid;
    }
}

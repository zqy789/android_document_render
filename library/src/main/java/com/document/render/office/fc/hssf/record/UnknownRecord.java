

package com.document.render.office.fc.hssf.record;


import com.document.render.office.fc.hssf.record.aggregates.PageSettingsBlock;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianOutput;



public final class UnknownRecord extends StandardRecord {



    public static final int PRINTSIZE_0033 = 0x0033;

    public static final int PLS_004D = 0x004D;
    public static final int SHEETPR_0081 = 0x0081;
    public static final int SORT_0090 = 0x0090;
    public static final int STANDARDWIDTH_0099 = 0x0099;
    public static final int SCL_00A0 = 0x00A0;
    public static final int BITMAP_00E9 = 0x00E9;
    public static final int PHONETICPR_00EF = 0x00EF;
    public static final int LABELRANGES_015F = 0x015F;
    public static final int QUICKTIP_0800 = 0x0800;
    public static final int SHEETEXT_0862 = 0x0862;
    public static final int SHEETPROTECTION_0867 = 0x0867;
    public static final int HEADER_FOOTER_089C = 0x089C;
    public static final int CODENAME_1BA = 0x01BA;

    private int _sid;
    private byte[] _rawData;


    public UnknownRecord(int id, byte[] data) {
        _sid = id & 0xFFFF;
        _rawData = data;
    }



    public UnknownRecord(RecordInputStream in) {
        _sid = in.getSid();
        _rawData = in.readRemainder();
        if (false && getBiffName(_sid) == null) {



            System.out.println("Unknown record 0x" + Integer.toHexString(_sid).toUpperCase());
        }
    }


    public static String getBiffName(int sid) {



        switch (sid) {
            case PRINTSIZE_0033:
                return "PRINTSIZE";
            case PLS_004D:
                return "PLS";
            case 0x0050:
                return "DCON";
            case 0x007F:
                return "IMDATA";
            case SHEETPR_0081:
                return "SHEETPR";
            case SORT_0090:
                return "SORT";
            case 0x0094:
                return "LHRECORD";
            case STANDARDWIDTH_0099:
                return "STANDARDWIDTH";
            case SCL_00A0:
                return "SCL";
            case 0x00AE:
                return "SCENMAN";

            case 0x00B2:
                return "SXVI";
            case 0x00B4:
                return "SXIVD";
            case 0x00B5:
                return "SXLI";

            case 0x00D3:
                return "OBPROJ";
            case 0x00DC:
                return "PARAMQRY";
            case 0x00DE:
                return "OLESIZE";
            case BITMAP_00E9:
                return "BITMAP";
            case PHONETICPR_00EF:
                return "PHONETICPR";
            case 0x00F1:
                return "SXEX";

            case LABELRANGES_015F:
                return "LABELRANGES";
            case 0x01BA:
                return "CODENAME";
            case 0x01A9:
                return "USERBVIEW";
            case 0x01AD:
                return "QSI";

            case 0x01C0:
                return "EXCEL9FILE";

            case 0x0802:
                return "QSISXTAG";
            case 0x0803:
                return "DBQUERYEXT";
            case 0x0805:
                return "TXTQUERY";
            case 0x0810:
                return "SXVIEWEX9";

            case 0x0812:
                return "CONTINUEFRT";
            case QUICKTIP_0800:
                return "QUICKTIP";
            case SHEETEXT_0862:
                return "SHEETEXT";
            case 0x0863:
                return "BOOKEXT";
            case 0x0864:
                return "SXADDL";
            case SHEETPROTECTION_0867:
                return "SHEETPROTECTION";
            case 0x086B:
                return "DATALABEXTCONTENTS";
            case 0x086C:
                return "CELLWATCH";
            case 0x0874:
                return "DROPDOWNOBJIDS";
            case 0x0876:
                return "DCONN";
            case 0x087B:
                return "CFEX";
            case 0x087C:
                return "XFCRC";
            case 0x087D:
                return "XFEXT";
            case 0x087F:
                return "CONTINUEFRT12";
            case 0x088B:
                return "PLV";
            case 0x088C:
                return "COMPAT12";
            case 0x088D:
                return "DXF";
            case 0x0892:
                return "STYLEEXT";
            case 0x0896:
                return "THEME";
            case 0x0897:
                return "GUIDTYPELIB";
            case 0x089A:
                return "MTRSETTINGS";
            case 0x089B:
                return "COMPRESSPICTURES";
            case HEADER_FOOTER_089C:
                return "HEADERFOOTER";
            case 0x08A1:
                return "SHAPEPROPSSTREAM";
            case 0x08A3:
                return "FORCEFULLCALCULATION";
            case 0x08A4:
                return "SHAPEPROPSSTREAM";
            case 0x08A5:
                return "TEXTPROPSSTREAM";
            case 0x08A6:
                return "RICHTEXTSTREAM";

            case 0x08C8:
                return "PLV{Mac Excel}";


        }
        if (isObservedButUnknown(sid)) {
            return "UNKNOWN-" + Integer.toHexString(sid).toUpperCase();
        }

        return null;
    }


    private static boolean isObservedButUnknown(int sid) {
        switch (sid) {
            case 0x0033:

            case 0x0034:



            case 0x01BD:
            case 0x01C2:



            case 0x089D:
            case 0x089E:
            case 0x08A7:

            case 0x1001:
            case 0x1006:
            case 0x1007:
            case 0x1009:
            case 0x100A:
            case 0x100B:
            case 0x100C:
            case 0x1014:
            case 0x1017:
            case 0x1018:
            case 0x1019:
            case 0x101A:
            case 0x101B:
            case 0x101D:
            case 0x101E:
            case 0x101F:
            case 0x1020:
            case 0x1021:
            case 0x1022:
            case 0x1024:
            case 0x1025:
            case 0x1026:
            case 0x1027:
            case 0x1032:
            case 0x1033:
            case 0x1034:
            case 0x1035:
            case 0x103A:
            case 0x1041:
            case 0x1043:
            case 0x1044:
            case 0x1045:
            case 0x1046:
            case 0x104A:
            case 0x104B:
            case 0x104E:
            case 0x104F:
            case 0x1051:
            case 0x105C:
            case 0x105D:
            case 0x105F:
            case 0x1060:
            case 0x1062:
            case 0x1063:
            case 0x1064:
            case 0x1065:
            case 0x1066:
                return true;
        }
        return false;
    }


    public void serialize(LittleEndianOutput out) {
        out.write(_rawData);
    }

    protected int getDataSize() {
        return _rawData.length;
    }

    public byte[] getData() {
        return _rawData;
    }


    public String toString() {
        String biffName = getBiffName(_sid);
        if (biffName == null) {
            biffName = "UNKNOWNRECORD";
        }
        StringBuffer sb = new StringBuffer();

        sb.append("[").append(biffName).append("] (0x");
        sb.append(Integer.toHexString(_sid).toUpperCase() + ")\n");
        if (_rawData.length > 0) {
            sb.append("  rawData=").append(HexDump.toHex(_rawData)).append("\n");
        }
        sb.append("[/").append(biffName).append("]\n");
        return sb.toString();
    }

    public short getSid() {
        return (short) _sid;
    }

    public Object clone() {

        return this;
    }
}

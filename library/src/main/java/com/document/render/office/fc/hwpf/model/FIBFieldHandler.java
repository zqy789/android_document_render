

package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.hwpf.model.io.HWPFOutputStream;
import com.document.render.office.fc.util.Internal;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


@Internal
public final class FIBFieldHandler {

    public static final int STSHFORIG = 0;
    public static final int STSHF = 1;
    public static final int PLCFFNDREF = 2;
    public static final int PLCFFNDTXT = 3;
    public static final int PLCFANDREF = 4;
    public static final int PLCFANDTXT = 5;
    public static final int PLCFSED = 6;
    public static final int PLCFPAD = 7;
    public static final int PLCFPHE = 8;
    public static final int STTBGLSY = 9;

    public static final int PLCFGLSY = 10;

    public static final int PLCFHDD = 11;
    public static final int PLCFBTECHPX = 12;
    public static final int PLCFBTEPAPX = 13;
    public static final int PLCFSEA = 14;
    public static final int STTBFFFN = 15;
    public static final int PLCFFLDMOM = 16;
    public static final int PLCFFLDHDR = 17;

    public static final int PLCFFLDFTN = 18;

    public static final int PLCFFLDATN = 19;

    public static final int PLCFFLDMCR = 20;

    public static final int STTBFBKMK = 21;

    public static final int PLCFBKF = 22;
    public static final int PLCFBKL = 23;
    public static final int CMDS = 24;
    public static final int PLCMCR = 25;
    public static final int STTBFMCR = 26;
    public static final int PRDRVR = 27;
    public static final int PRENVPORT = 28;
    public static final int PRENVLAND = 29;
    public static final int WSS = 30;

    public static final int DOP = 31;
    public static final int STTBFASSOC = 32;
    public static final int CLX = 33;
    public static final int PLCFPGDFTN = 34;
    public static final int AUTOSAVESOURCE = 35;
    public static final int GRPXSTATNOWNERS = 36;
    public static final int STTBFATNBKMK = 37;
    public static final int PLCFDOAMOM = 38;
    public static final int PLCDOAHDR = 39;

    public static final int PLCSPAMOM = 40;

    public static final int PLCSPAHDR = 41;
    public static final int PLCFATNBKF = 42;

    public static final int PLCFATNBKL = 43;

    public static final int PMS = 44;
    public static final int FORMFLDSTTBS = 45;
    public static final int PLCFENDREF = 46;
    public static final int PLCFENDTXT = 47;
    public static final int PLCFFLDEDN = 48;
    public static final int PLCFPGDEDN = 49;

    public static final int DGGINFO = 50;
    public static final int STTBFRMARK = 51;
    public static final int STTBCAPTION = 52;
    public static final int STTBAUTOCAPTION = 53;
    public static final int PLCFWKB = 54;
    public static final int PLCFSPL = 55;
    public static final int PLCFTXBXTXT = 56;

    public static final int PLCFFLDTXBX = 57;
    public static final int PLCFHDRTXBXTXT = 58;
    public static final int PLCFFLDHDRTXBX = 59;
    public static final int STWUSER = 60;
    public static final int STTBTTMBD = 61;
    public static final int UNUSED = 62;
    public static final int PGDMOTHER = 63;
    public static final int BKDMOTHER = 64;
    public static final int PGDFTN = 65;
    public static final int BKDFTN = 66;
    public static final int PGDEDN = 67;
    public static final int BKDEDN = 68;
    public static final int STTBFINTFLD = 69;
    public static final int ROUTESLIP = 70;
    public static final int STTBSAVEDBY = 71;
    public static final int STTBFNM = 72;
    public static final int PLCFLST = 73;
    public static final int PLFLFO = 74;
    public static final int PLCFTXBXBKD = 75;
    public static final int PLCFTXBXHDRBKD = 76;
    public static final int DOCUNDO = 77;
    public static final int RGBUSE = 78;
    public static final int USP = 79;
    public static final int USKF = 80;
    public static final int PLCUPCRGBUSE = 81;
    public static final int PLCUPCUSP = 82;
    public static final int STTBGLSYSTYLE = 83;
    public static final int PLGOSL = 84;
    public static final int PLCOCX = 85;
    public static final int PLCFBTELVC = 86;
    public static final int MODIFIED = 87;
    public static final int PLCFLVC = 88;
    public static final int PLCASUMY = 89;
    public static final int PLCFGRAM = 90;
    public static final int STTBLISTNAMES = 91;
    public static final int STTBFUSSR = 92;
    private static final int FIELD_SIZE = LittleEndian.INT_SIZE * 2;
    private static POILogger log = POILogFactory.getLogger(FIBFieldHandler.class);
    private Map<Integer, UnhandledDataStructure> _unknownMap = new HashMap<Integer, UnhandledDataStructure>();
    private int[] _fields;


    public FIBFieldHandler(byte[] mainStream, int startOffset, byte[] tableStream,
                           HashSet<Integer> offsetList, boolean areKnown) {
        int numFields = LittleEndian.getShort(mainStream, startOffset);
        int offset = startOffset + LittleEndian.SHORT_SIZE;
        _fields = new int[numFields * 2];

        for (int x = 0; x < numFields; x++) {
            int fieldOffset = (x * FIELD_SIZE) + offset;
            int dsOffset = LittleEndian.getInt(mainStream, fieldOffset);
            fieldOffset += LittleEndian.INT_SIZE;
            int dsSize = LittleEndian.getInt(mainStream, fieldOffset);

            if (offsetList.contains(Integer.valueOf(x)) ^ areKnown) {
                if (dsSize > 0) {
                    if (dsOffset + dsSize > tableStream.length) {
                        log.log(POILogger.WARN, "Unhandled data structure points to outside the buffer. " +
                                "offset = " + dsOffset + ", length = " + dsSize +
                                ", buffer length = " + tableStream.length);
                    } else {
                        UnhandledDataStructure unhandled = new UnhandledDataStructure(
                                tableStream, dsOffset, dsSize);
                        _unknownMap.put(Integer.valueOf(x), unhandled);
                    }
                }
            }
            _fields[x * 2] = dsOffset;
            _fields[(x * 2) + 1] = dsSize;
        }
    }

    public void clearFields() {
        Arrays.fill(_fields, 0);
    }

    public int getFieldOffset(int field) {
        return _fields[field * 2];
    }

    public int getFieldSize(int field) {
        return _fields[(field * 2) + 1];
    }

    public void setFieldOffset(int field, int offset) {
        _fields[field * 2] = offset;
    }

    public void setFieldSize(int field, int size) {
        _fields[(field * 2) + 1] = size;
    }

    public int sizeInBytes() {
        return (_fields.length * LittleEndian.INT_SIZE) + LittleEndian.SHORT_SIZE;
    }

    void writeTo(byte[] mainStream, int offset, HWPFOutputStream tableStream)
            throws IOException {
        int length = _fields.length / 2;
        LittleEndian.putShort(mainStream, offset, (short) length);
        offset += LittleEndian.SHORT_SIZE;

        for (int x = 0; x < length; x++) {
            UnhandledDataStructure ds = _unknownMap.get(Integer.valueOf(x));
            if (ds != null) {
                _fields[x * 2] = tableStream.getOffset();
                LittleEndian.putInt(mainStream, offset, tableStream.getOffset());
                offset += LittleEndian.INT_SIZE;

                byte[] buf = ds.getBuf();
                tableStream.write(buf);

                _fields[(x * 2) + 1] = buf.length;
                LittleEndian.putInt(mainStream, offset, buf.length);
                offset += LittleEndian.INT_SIZE;
            } else {
                LittleEndian.putInt(mainStream, offset, _fields[x * 2]);
                offset += LittleEndian.INT_SIZE;
                LittleEndian.putInt(mainStream, offset, _fields[(x * 2) + 1]);
                offset += LittleEndian.INT_SIZE;
            }
        }
    }
}
